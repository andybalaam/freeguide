package freeguide.plugins.storage.xmltv;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.impexp.XMLTVImport;

import freeguide.plugins.BaseModule;
import freeguide.plugins.IModuleStorage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.logging.Level;

/**
 * XMLTV xml storage loader.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class XMLTVProcessor extends BaseModule //implements IStorage
{

    protected IModuleStorage.Info cachedInfo;

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
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public synchronized IModuleStorage.Info getInfo(  )
    {

        if( cachedInfo == null )
        {

            GetInfoFilter filter = new GetInfoFilter(  );
        }

        return cachedInfo;

    }

    /**
     * Load data from external storage in xmltv XML format to in-memory
     * storage.
     *
     * @param channels filter
     * @param minDate DOCUMENT ME!
     * @param maxDate DOCUMENT ME!
     *
     * @return data
     *
     * @throws Exception
     */
    public TVData load( 
        final TVChannelsSet channels, long minDate, long maxDate )
        throws Exception
    {

        final TVData result = new TVData(  );

        //processAllFiles( result, filter );
        //patch invalid length values

        /* new TVData.ByProgrammesIterator(  )
        {
        public void onProgramme( TVProgramme programme )
        {

        if(
        ( programme.getStart(  ) >= programme.getEnd(  ) )
        || ( programme.getEnd(  ) == 0 )
        || ( ( programme.getEnd(  )
        - programme.getStart(  ) ) > ( 120L * 60 * 1000 ) ) )
        {
        programme.setEnd(
        programme.getStart(  ) + ( 30L * 60 * 1000 ) );

        }
        }
        }.iterate( result );   */
        return result;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     */
    public void save( final TVData data )
    {
        cachedInfo = null;

        /*                    new XMLTVExport(  ).export(


        new File(


        FreeGuide.config.workingDirectory + "/grab-"


        + grabber.getID(  ) + ".xmltv" ), result );*/
    }

    protected void processAllFiles( TVData data, XMLTVImport.Filter filter )
        throws Exception
    {

        /*
                String working_directory = FreeGuide.config.workingDirectory;

                if( working_directory == null )
                {
                    throw new IOException( "Working directory not defined" );

                }

                File[] dataFiles =
                    new File( working_directory ).listFiles(
                        new FilenameFilter(  )
                        {
                            public boolean accept( File dir, String name )
                            {

                                return name.endsWith( ".xmltv" );

                            }
                        } );

                if( dataFiles != null )
                {

                    XMLTVImport importer = new XMLTVImport(  );

                    for( int i = 0; i < dataFiles.length; i++ )
                    {

                        try
                        {
                            importer.process( dataFiles[i], data, filter );

                        }

                        catch( Exception ex )
                        {
                            FreeGuide.log.log(
                                Level.WARNING,
                                "Error on parse xmltv data file '"
                                + dataFiles[i].getAbsolutePath(  ) + "': "
                                + ex.getMessage(  ), ex );
                        }
                    }
                }*/
    }

    protected static class GetInfoFilter extends XMLTVImport.Filter
    {

        protected IModuleStorage.Info info;

        protected GetInfoFilter(  )
        {
            info = new IModuleStorage.Info(  );

            info.allChannels = new TVChannelsSet(  );

            info.minDate = Long.MAX_VALUE;

            info.maxDate = Long.MIN_VALUE;

        }

        /**
         * DOCUMENT_ME!
         *
         * @param currentChannel DOCUMENT_ME!
         */
        public void performChannelEnd( final TVChannel currentChannel )
        {

            TVChannelsSet.Channel ch =
                new TVChannelsSet.Channel( 
                    currentChannel.getID(  ), currentChannel.getDisplayName(  ) );

            if( !info.allChannels.contains( ch.getChannelID(  ) ) )
            {
                info.allChannels.add( ch );

            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param programme DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean checkProgrammeStart( TVProgramme programme )
        {

            if( programme.getStart(  ) < info.minDate )
            {
                info.minDate = programme.getStart(  );

            }

            // we can't check endtime, because xmltv grabber produce wrong endtime.
            //TODO when we will preprocess grabber result, we will be able check endtime 
            if( programme.getStart(  ) > info.maxDate )
            {
                info.maxDate = programme.getStart(  );

            }

            return false;

        }
    }
}
