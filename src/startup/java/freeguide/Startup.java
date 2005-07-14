package freeguide;

import java.io.File;

import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * This class load all jars to classloader and runs main application
 * class(freeguide.FreeGuide). It uses for unpack updates BEFORE starting
 * main application.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class Startup
{

    protected static final String STARTUP_CLASS = "freeguide.FreeGuide";
    protected static final String STARTUP_METHOD = "main";
    protected static final String INSTALL_PREFIX = "--install_directory=";

    /**
     * Main method.
     *
     * @param args
     */
    public static void main( final String[] args )
    {

        try
        {

            try
            {
                new StartupUpdates(  ).update( getInstallDirectory( args ) );
            }
            catch( Exception ex )
            {
                ex.printStackTrace(  );
                MessageBox.display( 
                    "Warning W01", "Error unpack updates: "
                    + ex.getMessage(  ) );
            }

            run( args );
        }
        catch( MalformedURLException ex )
        {
            die( "Error E01", "Error in ClassLoader URL", ex );
        }
        catch( ClassNotFoundException ex )
        {
            die( 
                "Error E02", "Main class('" + STARTUP_CLASS + "') not found",
                null );
        }
        catch( NoSuchMethodException ex )
        {
            die( "Error E03", "Main method not found in startup class", ex );
        }
        catch( Exception ex )
        {
            die( "Error E04", "Main method exception throwed", ex );
        }
    }

    protected static ClassLoader getAllClasses( final String[] args )
        throws MalformedURLException
    {

        List jarUrls = new ArrayList(  );

        File[] libs =
            new File( getInstallDirectory( args ), "lib" ).listFiles(  );

        if( libs != null )
        {

            for( int i = 0; i < libs.length; i++ )
            {
                System.err.println( "Load module jar: " + libs[i].getPath(  ) );

                jarUrls.add( libs[i].toURL(  ) );

            }
        }

        return new URLClassLoader( 
            (URL[])jarUrls.toArray( new URL[jarUrls.size(  )] ) );
    }

    protected static void run( final String[] args ) throws Exception
    {

        final ClassLoader classLoader;

        if( System.getProperty( "debugPlugins" ) != null )
        {
            classLoader = Startup.class.getClassLoader(  );
        }
        else
        {
            classLoader = getAllClasses( args );
        }

        try
        {

            Class startupClass = classLoader.loadClass( STARTUP_CLASS );
            Method startupMethod =
                startupClass.getMethod( 
                    STARTUP_METHOD, new Class[] { String[].class } );

            startupMethod.invoke( startupClass, new Object[] { args } );
        }
        catch( NoClassDefFoundError ex )
        {
            die( 
                "Error E05",
                "Wrong java version: " + System.getProperty( "java.version" )
                + ". You need at least version 1.4", null );
        }
        catch( UnsupportedClassVersionError ex )
        {
            die( 
                "Error E06",
                "Wrong java version: " + System.getProperty( "java.version" )
                + ". You need at least version 1.4", null );
        }
    }

    protected static void die( 
        final String title, final String message, final Exception ex )
    {
        System.err.println( message );

        if( ex != null )
        {
            ex.printStackTrace(  );
        }

        String text;

        if( ex != null )
        {
            text = message + ": " + ex.getMessage(  );
        }
        else
        {
            text = message;
        }

        MessageBox.display( title, text );

        System.exit( 1 );
    }

    protected static File getInstallDirectory( final String[] args )
    {

        for( int i = 0; i < args.length; i++ )
        {

            if( args[i].startsWith( INSTALL_PREFIX ) )
            {

                return new File( 
                    args[i].substring( INSTALL_PREFIX.length(  ) ) );
            }
        }

        return new File( "." );
    }
}
