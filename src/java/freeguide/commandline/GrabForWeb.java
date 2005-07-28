package freeguide.commandline;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PluginsManager;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorChannels;

import freeguide.lib.general.LanguageHelper;

import freeguide.lib.impexp.XMLTVExport;

import freeguide.plugins.IApplication;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import freeguide.plugins.impexp.palmatv.ExportPalmAtv;

import java.io.File;
import java.io.IOException;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabForWeb
{

    protected static String path = "";
    protected static final Map CHARCONV = new TreeMap(  );

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        LanguageHelper.loadProperties( 
            GrabForWeb.class.getPackage(  ).getName(  ).replace( '.', '/' )
            + "/filenames.replaces.properties", CHARCONV );

        FreeGuide.log = Logger.getLogger( "org.freeguide-tv" );

        PluginsManager.loadModules(  );

        Application.setInstance( 
            (IApplication)PluginsManager.getApplicationModuleInfo(  )
                                        .getInstance(  ) );

        FreeGuide.setLocale( Locale.ENGLISH );

        final ExportPalmAtv palmExp = new ExportPalmAtv(  );

        for( int i = 0; i < args.length; i++ )
        {

            if( args[i].startsWith( "--path=" ) )
            {
                path = args[i].substring( "--path=".length(  ) ) + "/";

                continue;
            }

            final String siteName = args[i];

            IModuleGrabber grabber =
                (IModuleGrabber)PluginsManager.getModuleByID( 
                    "grabber-" + siteName );

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
                        new File( path + siteName + ".xml" ), result );
                    palmExp.exportBatch( 
                        result, siteName, new File( path + siteName + ".pdb" ) );
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

                                    File outFile =
                                        new File( 
                                            path + siteName + "/"
                                            + getFNbyCN( 
                                                channel.getDisplayName(  ) )
                                            + ".pdb" );
                                    outFile.getParentFile(  ).mkdirs(  );
                                    System.out.println( 
                                        "out " + outFile.getPath(  ) );
                                    palmExp.exportBatch( 
                                        oneData,
                                        siteName + '-'
                                        + channel.getID(  ).hashCode(  ),
                                        outFile );
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

    protected static String getFNbyCN( String ChannelName )
    {

        char c;
        String cz;
        String result = "";

        for( int i = 0; i < ChannelName.length(  ); i++ )
        {
            c = ChannelName.charAt( i );
            cz = (String)CHARCONV.get( "" + c );

            if( cz == null )
            {
                System.err.println( 
                    "Warning: Error convert char to filename : '" + c
                    + "' in string \"" + ChannelName + "\"" );
            }
            else
            {
                result += cz;
            }
        }

        return result;
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
