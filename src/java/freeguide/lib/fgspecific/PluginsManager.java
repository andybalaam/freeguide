package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.lib.general.LanguageHelper;

import freeguide.plugins.IModule;
import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IModuleImport;
import freeguide.plugins.IModuleViewer;

import freeguide.plugins.grabber.newsvm_com.GrabberNewsvm;
import freeguide.plugins.grabber.tv_kulichki_net.GrabberKulichki;
import freeguide.plugins.grabber.www_cosmostv_com.GrabberCosmostv;
import freeguide.plugins.grabber.www_ntvplus_ru.GrabberNtvplus;
import freeguide.plugins.grabber.www_vsetv_com.GrabberVsetv;
import freeguide.plugins.grabber.xmltv.GrabberXMLTV;

import freeguide.plugins.importexport.jtv.JTV;
import freeguide.plugins.importexport.palmatv.ExportPalmAtv;
import freeguide.plugins.importexport.xmltv.ImpExpXmltv;

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
import java.util.logging.Level;
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
        modules.add( new HorizontalViewer(  ) );
        modules.add( new GrabberXMLTV(  ) );

        modules.add( new GrabberVsetv(  ) );
        modules.add( new GrabberNtvplus(  ) );
        modules.add( new GrabberCosmostv(  ) );
        modules.add( new GrabberNewsvm(  ) );
        modules.add( new GrabberKulichki(  ) );

        modules.add( new ExportPalmAtv(  ) );
        modules.add( new ImpExpXmltv(  ) );
        modules.add( new JTV(  ) );

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
            else if( 
                module instanceof IModuleImport
                    || module instanceof IModuleExport )
            {
                nodeName =
                    "/org/freeguide-tv/modules/importexport/"
                    + module.getID(  );
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
                catch( Exception ex )
                {
                    FreeGuide.log.log( 
                        Level.WARNING, "Error loading plugin from "
                        + className, ex );
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
     * Get module by ID.
     *
     * @param id DOCUMENT ME!
     *
     * @return module
     */
    public static IModule getModuleByID( final String id )
    {

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );

            if( id.equals( module.getID(  ) ) )
            {

                return module;
            }
        }

        return null;
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

        final List result = new ArrayList(  );

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );

            if( module instanceof IModuleExport )
            {
                result.add( module );
            }
        }

        return (IModuleExport[])result.toArray( 
            new IModuleExport[result.size(  )] );
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
                FreeGuide.log.log( 
                    Level.SEVERE,
                    "Error set locale for module " + module.getID(  ), ex );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param id DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isInstalled( final String id )
    {

        for( int i = 0; i < modules.size(  ); i++ )
        {

            IModule module = (IModule)modules.get( i );

            if( id.equals( module.getID(  ) ) )
            {

                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param id DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws InstantiationException DOCUMENT_ME!
     * @throws IllegalAccessException DOCUMENT_ME!
     */
    public static IModule cloneModule( final String id )
        throws InstantiationException, IllegalAccessException
    {

        IModule mod = getModuleByID( id );

        return ( mod != null ) ? (IModule)mod.getClass(  ).newInstance(  ) : null;
    }
}
