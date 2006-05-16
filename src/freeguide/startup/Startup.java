package freeguide.startup;

import java.io.File;
import java.io.FileFilter;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import java.io.IOException;

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
    protected static final String STARTUP_CLASS =
        "freeguide.plugins.program.freeguide.FreeGuide";
    protected static final String STARTUP_METHOD = "main";
    protected static final String INSTALL_PREFIX = "--install_directory";

    /**
     * Main method.
     *
     * @param args
     */
    public static void main( final String[] args )
    {
        //try
        //{
            try
            {
                new StartupUpdates(  ).update( getInstallDirectory( args ) );
            }
            catch( IOException ex )
            {
                ex.printStackTrace(  );
                MessageBox.display( 
                    "Warning W01", "Error unpacking updates: "
                    + ex.getMessage(  ) );
            }

            run( args );
        //}
    }

    protected static ClassLoader getAllClasses( final String[] args )
    {
        List jarUrls = new ArrayList(  );

        File installDirectory = getInstallDirectory( args );
        File libDirectory = new File( installDirectory, "lib" );

        if( !libDirectory.isDirectory(  ) )
        {
            libDirectory = new File( installDirectory, "../lib" );
        }

        File[] libs =
            libDirectory.listFiles( 
                new FileFilter(  )
                {
                    public boolean accept( File fl )
                    {
                        return fl.toString(  ).endsWith( ".jar" );
                    }
                } );

        if( libs != null )
        {
            for( int i = 0; i < libs.length; i++ )
            {
                System.err.println( "Load module jar: " + libs[i].getPath(  ) );
                
                try
                {
                    jarUrls.add( libs[i].toURL(  ) );
                }
                catch( MalformedURLException e )
                {
                    e.printStackTrace(  );
                }
            }
        }

        return new URLClassLoader( 
            (URL[])jarUrls.toArray( new URL[jarUrls.size(  )] ) );
    }

    protected static void run( final String[] args )
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
            ex.printStackTrace(  );
            die( 
                "Error E05",
                "Wrong java version: " + System.getProperty( "java.version" )
                + ". You need at least version 1.4", null );
        }
        catch( UnsupportedClassVersionError ex )
        {
            ex.printStackTrace(  );
            die( 
                "Error E06",
                "Wrong java version: " + System.getProperty( "java.version" )
                + ". You need at least version 1.4", null );
        }
        catch( ClassNotFoundException ex )
        {
            ex.printStackTrace(  );
            die(
                "Error E07",
                "Main class not found", ex );
        }
        catch( NoSuchMethodException ex )
        {
            ex.printStackTrace(  );
            die(
                "Error E08",
                "Main method not found", ex );
        }
        catch( IllegalAccessException ex )
        {
            ex.printStackTrace(  );
            die(
                "Error E09",
                "Main method not accessible", ex );
        }
        catch( InvocationTargetException ex )
        {
            ex.printStackTrace(  );
            Throwable t = ex.getCause(  );
            if( t != null )
            {
                t.printStackTrace(  );
            }
            die(
                "Error E10",
                "Exception in main method", ex );
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
            if( 
                args[i].equals( INSTALL_PREFIX ) && ( ( i + 1 ) < args.length ) )
            {
                return new File( args[i + 1] );
            }
            else if( args[i].startsWith( INSTALL_PREFIX + "=" ) )
            {
                return new File( 
                    args[i].substring( INSTALL_PREFIX.length(  ) + 1 ) );
            }
        }

        return new File( "." );
    }
}
