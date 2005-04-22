package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.lib.general.LanguageHelper;

import freeguide.plugins.IModule;
import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IModuleViewer;

import freeguide.plugins.grabber.www_cosmostv_com.GrabberCosmostv;
import freeguide.plugins.grabber.www_vsetv_com.GrabberVsetv;
import freeguide.plugins.grabber.xmltv.GrabberXMLTV;

import freeguide.plugins.ui.horizontal.HorizontalViewer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.prefs.Preferences;

/**
 * Plugins manager.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PluginsManager
{

    protected static List modules;

    static
    {
        modules = new ArrayList(  );

        // This hack should be changed for call loadModules() in future.
        modules.add( new GrabberXMLTV(  ) );

        //modules.add( new GrabberCosmostv(  ) );
        //modules.add( new GrabberVsetv(  ) );
        modules.add( new HorizontalViewer(  ) );

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );
            String nodeName = null;

            if( module instanceof IModuleGrabber )
            {
                nodeName =
                    "/org/freeguide-tv/modules/grabber/" + module.getID(  );
            }
            else if( module instanceof IModuleViewer )
            {
                nodeName =
                    "/org/freeguide-tv/modules/viewer/" + module.getID(  );
            }

            module.setConfigStorage( 
                Preferences.userRoot(  ).node( nodeName ) );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void loadModules(  ) throws IOException
    {

        List jarUrls = new ArrayList(  );

        File[] libs = new File( "modules" ).listFiles(  );

        if( libs != null )
        {

            for( int i = 0; i < libs.length; i++ )
            {
                FreeGuide.log.finest( 
                    "Load module jar: " + libs[i].getPath(  ) );

                jarUrls.add( libs[i].toURL(  ) );

            }
        }

        ClassLoader classLoader =
            new URLClassLoader( 
                (URL[])jarUrls.toArray( new URL[jarUrls.size(  )] ),
                PluginsManager.class.getClassLoader(  ) );

        List listGrabbers = new ArrayList(  );

        Enumeration urls = classLoader.getResources( "module.properties" );

        while( urls.hasMoreElements(  ) )
        {

            URL url = (URL)urls.nextElement(  );

            Properties props = new Properties(  );

            InputStream stream = url.openStream(  );

            props.load( stream );

            stream.close(  );

            String className = props.getProperty( "classname" );

            if( className != null )
            {

                try
                {

                    Class moduleClass = classLoader.loadClass( className );

                    if( IModuleGrabber.class.isAssignableFrom( moduleClass ) )
                    {
                        listGrabbers.add( moduleClass.newInstance(  ) );

                    }
                }

                catch( ClassNotFoundException ex )
                {
                    ex.printStackTrace(  );

                }

                catch( InstantiationException ex )
                {
                    ex.printStackTrace(  );

                }

                catch( IllegalAccessException ex )
                {
                    ex.printStackTrace(  );

                }
            }
        }
    }

    /**
     * Get supported IModuleGrabber.
     *
     * @return
     */
    public static IModuleGrabber[] getGrabbers(  )
    {

        final List result = new ArrayList(  );

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );

            if( module instanceof IModuleGrabber )
            {
                result.add( module );
            }
        }

        return (IModuleGrabber[])result.toArray( 
            new IModuleGrabber[result.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleViewer[] getViewers(  )
    {

        final List result = new ArrayList(  );

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );

            if( module instanceof IModuleViewer )
            {
                result.add( module );
            }
        }

        return (IModuleViewer[])result.toArray( 
            new IModuleViewer[result.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param id DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleGrabber getGrabberByID( final String id )
    {

        IModuleGrabber[] grabbers = getGrabbers(  );

        for( int i = 0; i < grabbers.length; i++ )
        {

            if( id.equals( grabbers[i].getID(  ) ) )
            {

                return grabbers[i];

            }
        }

        return null;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param id DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleViewer getViewerByID( final String id )
    {

        IModuleViewer[] viewers = getViewers(  );

        for( int i = 0; i < viewers.length; i++ )
        {

            if( id.equals( viewers[i].getID(  ) ) )
            {

                return viewers[i];

            }
        }

        return null;

    }

    /**
     * Get supported IModuleExport.
     *
     * @return
     */
    public static IModuleExport[] getExporters(  )
    {

        return new IModuleExport[0];

    }

    /**
     * DOCUMENT_ME!
     *
     * @param locales DOCUMENT_ME!
     */
    public static void setLocale( Locale[] locales )
    {

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );
            Locale locale = null;

            try
            {

                Locale[] modLocales = module.getSuppotedLocales(  );
                module.setLocale( 
                    LanguageHelper.getPreferredLocale( locales, modLocales ) );
            }
            catch( Exception ex )
            {
                ex.printStackTrace(  );
            }
        }
    }
}
