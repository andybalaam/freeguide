package freeguide.commandline;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.PluginsManager;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorChannels;

import freeguide.lib.impexp.XMLTVExport;

import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import freeguide.plugins.importexport.palmatv.ExportPalmAtv;

import java.io.File;
import java.io.IOException;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabForWeb
{

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        FreeGuide.log = Logger.getLogger( "org.freeguide-tv" );
        FreeGuide.setLocale( Locale.ENGLISH );

        final ExportPalmAtv palmExp = new ExportPalmAtv(  );

        for( int i = 0; i < args.length; i++ )
        {

            final String siteName = args[i];

            IModuleGrabber grabber = PluginsManager.getGrabberByID( siteName );

            if( grabber == null )
            {
                System.out.println( "Grabber '" + siteName + "' not found" );
            }
            else
            {
                System.out.println( "Start grabbing '" + siteName + "'" );

                TVData result =
                    grabber.grabData( new CmdProgress(  ), new CmdLogger(  ) );

                if( result != null )
                {
                    result.iterate( 
                        new TVIteratorChannels(  )
                        {
                            protected void onChannel( TVChannel channel )
                            {
                                channel.normalizeTime(  );
                            }
                        } );
                    new XMLTVExport(  ).export( 
                        new File( siteName + ".xml" ), result );
                    palmExp.exportBatch( result, siteName );
                    new File( siteName ).mkdirs(  );
                    result.iterate( 
                        new TVIteratorChannels(  )
                        {
                            protected void onChannel( TVChannel channel )
                            {

                                try
                                {

                                    TVData oneData = new TVData(  );
                                    TVChannel tc =
                                        oneData.get( channel.getID(  ) );
                                    tc.mergeFrom( channel );
                                    System.out.println( 
                                        "out " + channel.getID(  ) );
                                    palmExp.exportBatch( 
                                        oneData,
                                        siteName + '-'
                                        + channel.getID(  ).hashCode(  ) );
                                }
                                catch( IOException ex )
                                {
                                    ex.printStackTrace(  );
                                }
                            }
                        } );
                }
            }
        }
    }

    protected static class CmdProgress implements IProgress
    {

        /**
         * DOCUMENT_ME!
         *
         * @param label DOCUMENT_ME!
         */
        public void setButtonLabel( String label )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param message DOCUMENT_ME!
         */
        public void setProgressMessage( String message )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param percent DOCUMENT_ME!
         */
        public void setProgressValue( int percent )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param stepCount DOCUMENT_ME!
         */
        public void setStepCount( int stepCount )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param stepNumber DOCUMENT_ME!
         */
        public void setStepNumber( int stepNumber )
        {
        }
    }

    protected static class CmdLogger implements ILogger
    {

        /**
         * DOCUMENT_ME!
         *
         * @param message DOCUMENT_ME!
         */
        public void error( String message )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param message DOCUMENT_ME!
         * @param ex DOCUMENT_ME!
         */
        public void error( String message, Exception ex )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param message DOCUMENT_ME!
         */
        public void info( String message )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param message DOCUMENT_ME!
         */
        public void warning( String message )
        {
        }
    }
}
