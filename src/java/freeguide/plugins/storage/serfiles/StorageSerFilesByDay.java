package freeguide.plugins.storage.serfiles;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.*;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IStorage;

import freeguide.plugins.storage.StorageHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
public class StorageSerFilesByDay extends BaseModule implements IStorage
{

    protected static final String ID = "serfiles";
    protected static Pattern FILE_MASK =
        Pattern.compile( "day-\\d{4}-\\d{2}-\\d{2}-[A-D].ser" );
    protected static long MSEC_PER_DAY = 24L * 60L * 60L * 1000L;
    protected static long MSEC_PARTS = 6L * 60L * 60L * 1000L;
    protected SimpleDateFormat dateFormat;
    protected Info cachedInfo;

    /**
     * Creates a new StorageSerFiles object.
     */
    public StorageSerFilesByDay(  )
    {
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        dateFormat.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return ID;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Info getInfo(  )
    {

        synchronized( this )
        {

            if( cachedInfo == null )
            {

                File[] files =
                    new File( FreeGuide.config.workingDirectory ).listFiles( 
                        new FilterFiles(  ) );

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
    }

    protected TVData load( final File f )
    {

        if( !f.exists(  ) )
        {

            return null;
        }

        try
        {

            ObjectInputStream in =
                new ObjectInputStream( 
                    new BufferedInputStream( new FileInputStream( f ) ) );

            return (TVData)in.readObject(  );
        }
        catch( Exception ex )
        {
            FreeGuide.log.log( 
                Level.WARNING, "Error read file " + f.getAbsolutePath(  ), ex );

            return null;
        }
    }

    protected File getFile( long date )
    {

        long letterNum = ( date % MSEC_PER_DAY ) / MSEC_PARTS;

        return new File( 
            FreeGuide.config.workingDirectory + "/" + "day-"
            + dateFormat.format( new Date( date ) ) + "-"
            + (char)( 'A' + letterNum ) + ".ser" );
    }

    protected void createDir( )
    {
        new File(FreeGuide.config.workingDirectory).mkdirs();
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channels DOCUMENT_ME!
     * @param minDate DOCUMENT_ME!
     * @param maxDate DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData get( 
        final TVChannelsSet channels, final long minDate, final long maxDate )
        throws Exception
    {

        synchronized( this )
        {

            final TVData result = new TVData(  );

            for( 
                long dt = ( minDate / MSEC_PARTS ) * MSEC_PARTS; dt < maxDate;
                    dt += MSEC_PARTS )
            {

                TVData data = load( getFile( dt ) );

                if( data == null )
                {

                    continue;
                }

                if( channels != null )
                {
                    data.iterateChannels( 
                        new TVIteratorChannels(  )
                        {
                            protected void onChannel( TVChannel channel )
                            {

                                if( !channels.contains( channel.getID(  ) ) )
                                {
                                    it.remove(  );
                                }
                            }
                        } );
                }

                data.iterateProgrammes( 
                    new TVIteratorProgrammes(  )
                    {
                        protected void onChannel( TVChannel channel )
                        {
                        }

                        protected void onProgramme( TVProgramme programme )
                        {

                            if( 
                                ( programme.getStart(  ) >= maxDate )
                                    || ( programme.getEnd(  ) <= minDate ) )
                            {
                                itProgrammes.remove(  );
                            }
                        }
                    } );

                result.mergeFrom( data );
            }

            return result;
        }
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
    public TVProgramme findEarliest( long minDate, EarliestCheckAllow check )
        throws Exception
    {

        synchronized( this )
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
        }

        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void add( TVData data ) throws Exception
    {

        synchronized( this )
        {

            WriteIterator it = new WriteIterator(  );
            data.iterate( it );
            it.sync(  );
        }
    }

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

        // map for store TVData by File. Each programme collected in own TVData, and all TVData's stored in sync.
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

            File file = getFile( programme.getStart(  ) );
            TVData data = (TVData)filesData.get( file );

            if( data == null )
            {

                if( file.exists(  ) )
                {
                    data = load( file );
                }
                else
                {
                    data = new TVData(  );
                }

                filesData.put( file, data );
            }

            boolean exist =
                data.containsChannel( programme.getChannel(  ).getID(  ) );
            TVChannel ch = data.get( programme.getChannel(  ).getID(  ) );

            if( !exist )
            {
                ch.loadHeadersFrom( programme.getChannel(  ) );
            }

            ch.put( programme );
            StorageHelper.performInInfo( cachedInfo, programme );
        }

        /**
         * DOCUMENT_ME!
         */
        public void sync(  )
        {
        	createDir();

            Iterator it = filesData.keySet(  ).iterator(  );

            while( it.hasNext(  ) )
            {

                File file = (File)it.next(  );
                TVData data = (TVData)filesData.get( file );

                try
                {
                    file.getParentFile(  ).mkdirs(  );

                    ObjectOutputStream out =
                        new ObjectOutputStream( 
                            new BufferedOutputStream( 
                                new FileOutputStream( file ) ) );

                    out.writeObject( data );

                    out.flush(  );

                    out.close(  );
                }
                catch( Exception ex )
                {
                    FreeGuide.log.log( 
                        Level.WARNING,
                        "Error write file " + file.getAbsolutePath(  ), ex );
                }
            }
        }
    }
}
