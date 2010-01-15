package freeguide.cmdstartup;

import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import freeguide.common.lib.fgspecific.search.ProgrammeSearcher;
import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;
import freeguide.plugins.program.freeguide.lib.general.CmdArgs;

public class CmdStartup
{
    /**
     * Run various FreeGuide functionality through the command line
     */
    public static void main( String[] args )
    {
        int retval = 1;

        Properties argProps = new CmdArgs().parse( args );

        if( argProps.containsKey( "search" ) )
        {
            try
            {
                PluginsManager.loadModules(  );
            }
            catch( IOException e )
            {
                e.printStackTrace();
            }

            if( PluginsManager.getApplicationModuleInfo(  ) == null )
            {
                final ResourceBundle startupMessages =
                    ResourceBundle.getBundle( "resources.i18n.Startup" );

                System.err.println(
                    startupMessages.getString( "startup.NoApplicationModule" ) );
                System.exit( 1 );
            }

            try
            {
                FreeGuide.preStartup( args );
            }
            catch( Exception e )
            {
                e.printStackTrace();
                System.exit( 2 );
            }

            retval = new ProgrammeSearcher()
                .run( (String)argProps.getProperty( "search" ), System.out, System.err );
        }

        if( retval != 0 )
        {
            System.exit( retval );
        }
    }

}
