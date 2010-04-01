package freeguide.plugins.storage.serfiles;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.StorageHelper;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorChannels;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Storage for store data in ser files splitted by channels and days.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class StorageSerFilesByDay extends BaseModule implements IModuleStorage
{
    protected static Pattern FILE_MASK =
        Pattern.compile( "day-(\\d{4}-\\d{2}-\\d{2})-[A-D].ser" );
    protected static long MSEC_PER_DAY = 24L * 60L * 60L * 1000L;
    protected static long MSEC_PARTS = 6L * 60L * 60L * 1000L;
    protected static long OLD_DATA = 10L * 24L * 60L * 60L * 1000L; // 10 days

    /**
     * The maximal age of a file. If it's older, it gets deleted in
     * cleanup() 86400 (seconds in 1 day)  7 (days/week) 4 (weeks)  1000 (to
     * make milliseconds)
     */
    protected static final long MAX_FILE_AGE = (long)86400 * 7 * 4 * 1000;
    protected SimpleDateFormat dateFormat;
    protected Info cachedInfo;

    /**
     * Directory where data will be stored, or null if need to use
     * application working directory.
     */
    protected final File storageDir;

    /**
     * Set to true for testing purposes only.
     */
    public boolean debugIncludeOldProgrammes = false;
    /**
     * Creates a new StorageSerFiles object on application storage dir.
     */
    public StorageSerFilesByDay(  )
    {
        this( null );
    }

    /**
     * Create storage on custom directory. Used not for FreeGuide GUI application.
     *
     * @param dir directory where files will be stored
     */
    public StorageSerFilesByDay( final File dir )
    {
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        this.storageDir = dir;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return null;
    }

    /**
     * Get storage directory. It is program working directory if
     * doesn't defined other.
     *
     * @return
     */
    public File getStorageDirectory(  )
    {
        return ( storageDir != null ) ? storageDir
                                      : new File(
            Application.getInstance(  ).getWorkingDirectory(  ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public synchronized Info getInfo(  )
    {
        if( cachedInfo == null )
        {
            File[] files =
                getStorageDirectory(  ).listFiles( new FilterFiles(  ) );

            cachedInfo = new Info(  );

            if( files != null )
            {
                for( int i = 0; i < files.length; i++ )
                {
                    TVData data = load( files[i] );

                    if( data != null )
                    {
                        StorageHelper.performInInfo( cachedInfo, data );
                    }
                }
            }
        }

        return cachedInfo;
    }

    /**
     * Load data from the specified file.
     *
     * @param f file for load
     *
     * @return data
     */
    protected TVData load( final File f )
    {
        if( !f.exists(  ) )
        {
            return null;
        }

        if( isOldFile( f ) )
        {
            // remove old data
            f.delete(  );

            return null;
        }

        try
        {
            ObjectInputStream in =
                new ObjectInputStream(
                    new BufferedInputStream( new FileInputStream( f ) ) );

            try
            {
                return (TVData)in.readObject(  );
            }
            finally
            {
                in.close(  );
            }
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log(
                Level.WARNING, "Error read file " + f.getAbsolutePath(  ), ex );

            return null;
        }
    }

    /**
     * Check if file contains old data.
     *
     * @param f file for checking
     *
     * @return true if data is old
     */
    protected boolean isOldFile( final File f )
    {
        final Matcher m = FILE_MASK.matcher( f.getName(  ) );

        if( m.matches(  ) )
        {
            if( debugIncludeOldProgrammes )
            {
                return false;
            }
            else
            {

                try
                {
                    Date fileDate = dateFormat.parse( m.group( 1 ) );

                    return fileDate.getTime(  ) <
                        ( System.currentTimeMillis(  ) - OLD_DATA );
                }
                catch( ParseException ex )
                {
                }
            }
        }

        return false;
    }

    /**
     * Calculate file name by date.
     *
     * @param date date for loading
     *
     * @return file which include data for this date
     */
    protected File getFile( long date )
    {
        long letterNum = ( date % MSEC_PER_DAY ) / MSEC_PARTS;

        return new File(
            getStorageDirectory(  ),
            "/" + "day-" + dateFormat.format( new Date( date ) ) + "-"
            + (char)( 'A' + letterNum ) + ".ser" );
    }

    /**
     * Create storage directory if it didn't exists yet.
     */
    protected void createDir(  )
    {
        final File dir = getStorageDirectory(  );

        if( !dir.exists(  ) )
        {
            dir.mkdirs(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param loadInfo DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public synchronized TVData get( final Info loadInfo )
        throws Exception
    {
        final TVData result = new TVData(  );

        /*
         * AJB: bug 270348: We go backwards through the files for each date.
         * This means that if a new programme arrived that spans a file
         * boundary and should have wiped out a programme in a later file
         * (but didn't because it was in a later file), it is encountered
         * later (because we are going backwards) and therefore wipes out
         * the programmes it should have wiped out earlier, now.
         *
         * See ImportTwiceSlowTest::test_bug270348 for details.
         */
        long earliest = ( ( loadInfo.minDate / MSEC_PARTS ) - 1 ) * MSEC_PARTS;
        long latest = loadInfo.maxDate;
        for( long dt = latest; dt >= earliest ; dt -= MSEC_PARTS )
        {
            TVData data = load( getFile( dt ) );

            if( data == null )
            {
                continue;
            }

            filterData( loadInfo, result, data );
        }

        return result;
    }

    /**
     * @param loadInfo
     * @param resultData
     * @param loadedData
     */
    protected void filterData( final Info loadInfo, TVData resultData, final TVData loadedData )
    {
        if( loadInfo.channelsList != null )
        {
            loadedData.iterateChannels(
                new TVIteratorChannels(  )
                {
                    protected void onChannel( TVChannel channel )
                    {
                        if(
                            !loadInfo.channelsList.contains(
                                    channel.getID(  ) ) )
                        {
                            it.remove(  );
                        }
                    }
                } );
        }

        loadedData.iterateProgrammes(
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                protected void onProgramme( TVProgramme programme )
                {
                    if(
                        ( programme.getStart(  ) >= loadInfo.maxDate )
                            || ( programme.getEnd(  ) <= loadInfo.minDate ) )
                    {
                        itProgrammes.remove(  );
                    }
                }
            } );

        resultData.moveFrom( loadedData );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param minDate DOCUMENT_ME!
     * @param check DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public synchronized TVProgramme findEarliest(
        long minDate, EarliestCheckAllow check ) throws Exception
    {
        for(
            long dt = ( minDate / MSEC_PARTS ) * MSEC_PARTS;
                dt < getInfo(  ).maxDate; dt += MSEC_PARTS )
        {
            TVData data = load( getFile( dt ) );

            if( data == null )
            {
                continue;
            }

            final TVProgramme prog =
                StorageHelper.findEarliest( data, minDate, check );

            if( prog != null )
            {
                return prog;
            }
        }

        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     */
    public synchronized void store( TVData data )
    {
        WriteIterator it = new WriteIterator(  );
        data.iterate( it );
        it.sync(  );
    }

    /**
     * Deletes all the files older than four (4) weeks. Have a look at
     * the MAX_FILE_AGE class constant to get the real maximum age.
     */
    public void cleanup(  )
    {
        File[] files =
            getStorageDirectory(  ).listFiles( new FilterFiles(  ) );

        if( files != null )
        {
            for( int i = 0; i < files.length; i++ )
            {
                if(
                    ( System.currentTimeMillis(  ) - files[i].lastModified(  ) ) > MAX_FILE_AGE )
                {
                    files[i].delete(  );
                }
            }
        }
    }

    //public void cleanup()
    protected static class FilterFiles implements FileFilter
    {
        /**
         * DOCUMENT_ME!
         *
         * @param pathname DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean accept( File pathname )
        {
            if( !pathname.isDirectory(  ) )
            {
                Matcher m = FILE_MASK.matcher( pathname.getName(  ) );

                return m.matches(  );
            }
            else
            {
                return false;
            }
        }
    }

    protected class WriteIterator extends TVIteratorProgrammes
    {
        /**
         * Map for store TVData by File.  Each programme collected
         * in own TVData, and all TVData's stored in sync.
         */
        Map filesData = new TreeMap(  );

        protected void onChannel( TVChannel channel )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param programme DOCUMENT_ME!
         */
        public void onProgramme( TVProgramme programme )
        {
            if( !debugIncludeOldProgrammes && (
                programme.getStart(  ) < ( System.currentTimeMillis(  )
                    - OLD_DATA ) )
                   )
            {
                return; // This is old programme
            }

            File file = getFile( programme.getStart(  ) );
            TVData data = (TVData)filesData.get( file );

            if( data == null )
            {
                data = new TVData(  );
                filesData.put( file, data );
            }

            TVChannel ch = data.get( programme.getChannel(  ).getID(  ) );

            ch.mergeHeaderFrom( programme.getChannel(  ) );

            ch.put( programme );
            StorageHelper.performInInfo( getInfo(  ), programme );
        }

        /**
         * DOCUMENT_ME!
         */
        public void sync(  )
        {
            createDir(  );

            Iterator it = filesData.keySet(  ).iterator(  );

            while( it.hasNext(  ) )
            {
                File file = (File)it.next(  );
                TVData storedData = null;

                if( file.exists(  ) )
                {
                    storedData = load( file );
                }

                if( storedData == null )
                { // may be it is old file or file doesn't exists
                    storedData = new TVData(  );
                }

                TVData data = (TVData)filesData.get( file );

                storedData.moveFrom( data );

                try
                {
                    file.getParentFile(  ).mkdirs(  );

                    ObjectOutputStream out =
                        new ObjectOutputStream(
                            new BufferedOutputStream(
                                new FileOutputStream( file ) ) );

                    out.writeObject( storedData );

                    out.flush(  );

                    out.close(  );
                }
                catch( Exception ex )
                {
                    Application.getInstance(  ).getLogger(  )
                               .log(
                        Level.WARNING,
                        "Error write file " + file.getAbsolutePath(  ), ex );
                }
            }
        }
    }
}
