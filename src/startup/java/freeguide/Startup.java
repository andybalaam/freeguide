package freeguide;

import java.io.File;

import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class load all jars to classloader and runs main application
 * class(freeguide.FreeGuide) again and again. It uses for restart
 * application after update jars and change locale, etc. If we want to close
 * application, we need to run System.exit().
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class Startup
{

    protected static final String STARTUP_CLASS = "freeguide.FreeGuide";
    protected static final String STARTUP_METHOD = "main";
    protected static final Logger log =
        Logger.getLogger( "org.freeguide-tv.startup" );

    /**
     * Main method.
     *
     * @param args
     */
    public static void main( final String[] args )
    {

        try
        {

            while( true )
            {
                run( args );
                System.gc(  );
            }
        }
        catch( MalformedURLException ex )
        {
            log.log( Level.SEVERE, "Error in ClassLoader URL", ex );
        }
        catch( ClassNotFoundException ex )
        {
            log.log( 
                Level.SEVERE, "Main class('" + STARTUP_CLASS + "') not found",
                ex );
        }
        catch( NoSuchMethodException ex )
        {
            log.log( 
                Level.SEVERE, "Main method not found in startup class", ex );
        }
        catch( Exception ex )
        {
            log.log( Level.SEVERE, "Main method exception throwed", ex );
        }
    }

    protected static ClassLoader getAllClasses(  )
        throws MalformedURLException
    {

        List jarUrls = new ArrayList(  );

        File[] libs = new File( "lib" ).listFiles(  );

        if( libs != null )
        {

            for( int i = 0; i < libs.length; i++ )
            {
                log.fine( "Load module jar: " + libs[i].getPath(  ) );

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
            classLoader = getAllClasses(  );
        }

        Class startupClass = classLoader.loadClass( STARTUP_CLASS );
        Method startupMethod =
            startupClass.getMethod( 
                STARTUP_METHOD, new Class[] { String[].class } );

        startupMethod.invoke( startupClass, new Object[] { args } );
    }
}
