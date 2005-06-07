package freeguide.plugins.storage.serfiles;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.StorageHelper;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleStorage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Storage for store data in ser files splitted by channels and days.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class StorageSerFiles extends BaseModule implements IModuleStorage
{

    protected static final String ID = "serfiles";
    protected static Pattern DIR_MASK =
        Pattern.compile( "day-\\d{4}-\\d{2}-\\d{2}" );
    protected static long MSEC_PER_DAY = 24L * 60L * 60L * 1000L;
    protected SimpleDateFormat dateFormat;
    protected Info cachedInfo;

    /**
     * Creates a new StorageSerFiles object.
     */
    public StorageSerFiles(  )
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

                File[] dateDirs =
                    new File( 
                        Application.getInstance(  ).getWorkingDirectory(  ) )
                    .listFiles( new FilterDirectories(  ) );

                if( dateDirs != null )
                {
                    cachedInfo = new Info(  );

                    for( int i = 0; i < dateDirs.length; i++ )
                    {

                        File[] files =
                            dateDirs[i].listFiles( new FilterFiles(  ) );

                        if( files != null )
                        {

                            for( int j = 0; j < files.length; j++ )
                            {

                                File file = files[j];
                                TVData data = new TVData(  );
                                load( file, data );
                                StorageHelper.performInInfo( cachedInfo, data );
                            }
                        }
                    }
                }
            }

            return cachedInfo;
        }
    }

    protected void load( final File f, final TVData data )
    {

        if( !f.exists(  ) )
        {

            return;
        }

        try
        {

            ObjectInputStream in =
                new ObjectInputStream( 
                    new BufferedInputStream( new FileInputStream( f ) ) );

            TVChannel ch = (TVChannel)in.readObject(  );

            TVChannel stCh = data.get( ch.getID(  ) );
            stCh.loadHeadersFrom( ch );
            stCh.mergeFrom( ch );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error read file " + f.getAbsolutePath(  ), ex );
        }
    }

    protected File getDir( long date )
    {

        return new File( 
            Application.getInstance(  ).getWorkingDirectory(  ) + "/" + "day-"
            + dateFormat.format( new Date( date ) ) );
    }

    protected File getFile( final String channelID, long date )
    {

        return new File( 
            getDir( date ).getAbsolutePath(  ) + "/"
            + channelID.replace( '/', '_' ).replace( '&', '_' ).replace( 
                '"', '_' ) + ".ser" );
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
    public TVData get( TVChannelsSet channels, long minDate, long maxDate )
        throws Exception
    {

        synchronized( this )
        {

            final TVData result = new TVData(  );

            for( 
                long dt = ( minDate / MSEC_PER_DAY ) * MSEC_PER_DAY;
                    dt < maxDate; dt += MSEC_PER_DAY )
            {

                if( channels != null )
                {

                    Iterator it = channels.getChannels(  ).iterator(  );

                    while( it.hasNext(  ) )
                    {

                        TVChannelsSet.Channel channel =
                            (TVChannelsSet.Channel)it.next(  );
                        load( getFile( channel.getChannelID(  ), dt ), result );
                    }
                }
                else
                {

                    File[] files =
                        getDir( dt ).listFiles( new FilterFiles(  ) );

                    if( files != null )
                    {

                        for( int i = 0; i < files.length; i++ )
                        {
                            load( files[i], result );
                        }
                    }
                }
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
                long dt = ( minDate / MSEC_PER_DAY ) * MSEC_PER_DAY;
                    dt < getInfo(  ).maxDate; dt += MSEC_PER_DAY )
            {

                File[] files = getDir( dt ).listFiles( new FilterFiles(  ) );

                if( files != null )
                {

                    final TVData data = new TVData(  );

                    for( int i = 0; i < files.length; i++ )
                    {
                        load( files[i], data );
                    }

                    final TVProgramme prog =
                        StorageHelper.findEarliest( data, minDate, check );

                    if( prog != null )
                    {

                        return prog;
                    }
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

    protected static class FilterDirectories implements FileFilter
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

            if( pathname.isDirectory(  ) )
            {

                Matcher m = DIR_MASK.matcher( pathname.getName(  ) );

                return m.matches(  );
            }
            else
            {

                return false;
            }
        }
    }

    protected static class FilterFiles implements FilenameFilter
    {

        /**
         * DOCUMENT_ME!
         *
         * @param dir DOCUMENT_ME!
         * @param name DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean accept( File dir, String name )
        {

            return name.endsWith( ".ser" );
        }
    }

    protected class WriteIterator extends TVIteratorProgrammes
    {

        File file;
        TVData fileData;

        /**
         * DOCUMENT_ME!
         *
         * @param channel DOCUMENT_ME!
         */
        public void onChannel( TVChannel channel )
        {
            fileData = null;
            file = null;

            if( cachedInfo == null )
            {
                cachedInfo = new Info(  );
            }

            if( !cachedInfo.allChannels.contains( channel.getID(  ) ) )
            {
                cachedInfo.allChannels.add( 
                    new TVChannelsSet.Channel( channel ) );
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param programme DOCUMENT_ME!
         */
        public void onProgramme( TVProgramme programme )
        {

            File currFile =
                getFile( 
                    programme.getChannel(  ).getID(  ), programme.getStart(  ) );

            if( !currFile.equals( file ) )
            {
                sync(  );
                file = currFile;
                fileData = new TVData(  );
                load( file, fileData );

                fileData.get( programme.getChannel(  ).getID(  ) )
                        .loadHeadersFrom( programme.getChannel(  ) );
            }

            fileData.get( programme.getChannel(  ).getID(  ) ).put( 
                (TVProgramme)programme.clone(  ) );

            if( cachedInfo.minDate > programme.getStart(  ) )
            {
                cachedInfo.minDate = programme.getStart(  );
            }

            if( cachedInfo.maxDate < programme.getEnd(  ) )
            {
                cachedInfo.maxDate = programme.getEnd(  );
            }
        }

        /**
         * DOCUMENT_ME!
         */
        public void sync(  )
        {

            if( file == null )
            {

                return;
            }

            try
            {
                file.getParentFile(  ).mkdirs(  );

                ObjectOutputStream out =
                    new ObjectOutputStream( 
                        new BufferedOutputStream( 
                            new FileOutputStream( file ) ) );

                out.writeObject( fileData.getChannelsIterator(  ).next(  ) );

                out.flush(  );

                out.close(  );
            }
            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING,
                    "Error write file " + file.getAbsolutePath(  ), ex );
            }
        }
    }
}
