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
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Startup
{

    protected static final String STARTUP_CLASS = "freeguide.FreeGuide";
    protected static final Logger log =
        Logger.getLogger( "org.freeguide-tv.startup" );

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     */
    public static void main( final String[] args )
    {

        try
        {

            while( true )
            {

                ClassLoader classLoader = getAllClasses(  );
                Class startupClass = classLoader.loadClass( STARTUP_CLASS );
                Method startupMethod =
                    startupClass.getMethod( 
                        "main", new Class[] { String[].class } );
                Object result =
                    startupMethod.invoke( startupClass, new Object[] { args } );

                if( 
                    ( result == null ) || ( result.getClass(  ) != int.class )
                        || ( ( (Integer)result ).intValue(  ) != -1 ) )
                {

                    break;
                }
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
            (URL[])jarUrls.toArray( new URL[jarUrls.size(  )] ),
            Startup.class.getClassLoader(  ) );
    }
}
