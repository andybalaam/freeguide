package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.lib.general.LanguageHelper;

import freeguide.plugins.IModule;
import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IModuleImport;
import freeguide.plugins.IModuleReminder;
import freeguide.plugins.IModuleStorage;
import freeguide.plugins.IModuleViewer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.prefs.Preferences;

/**
 * Plugins manager.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PluginsManager
{

    // protected static List allPlugins = new ArrayList(  );
    protected static Map pluginsByID = new TreeMap(  );
    protected static IModuleGrabber[] grabbers;
    protected static IModuleStorage[] storages;
    protected static IModuleViewer[] viewers;
    protected static IModuleReminder[] reminders;
    protected static IModule[] impexps;
    protected static IModuleImport[] importers;
    protected static IModuleExport[] exporters;
    protected static List grabbersList;
    protected static List storagesList;
    protected static List viewersList;
    protected static List remindersList;
    protected static List impexpsList;
    protected static List importersList;
    protected static List exportersList;

    /**
     * Load all modules.
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void loadModules(  ) throws IOException
    {
        grabbersList = new ArrayList(  );
        storagesList = new ArrayList(  );
        viewersList = new ArrayList(  );
        remindersList = new ArrayList(  );
        impexpsList = new ArrayList(  );
        importersList = new ArrayList(  );
        exportersList = new ArrayList(  );

        pluginsByID.put( 
            Application.getApplicationModule(  ).getID(  ),
            Application.getApplicationModule(  ) );

        try
        {

            final String[] classNamesList =
                LanguageHelper.loadStrings( 
                    new FileInputStream( "plugins.classes" ) );

            for( int i = 0; i < classNamesList.length; i++ )
            {
                loadClass( classNamesList[i] );
            }
        }
        catch( IOException ex )
        {

            Enumeration urls =
                PluginsManager.class.getClassLoader(  ).getResources( 
                    "plugin.properties" );

            while( urls.hasMoreElements(  ) )
            {

                URL url = (URL)urls.nextElement(  );

                Properties props = new Properties(  );

                InputStream stream = url.openStream(  );

                props.load( stream );

                stream.close(  );

                String className = props.getProperty( "class" );

                if( className != null )
                {
                    loadClass( className );
                }
            }
        }

        grabbers =
            (IModuleGrabber[])grabbersList.toArray( 
                new IModuleGrabber[grabbersList.size(  )] );
        grabbersList = null;
        storages =
            (IModuleStorage[])storagesList.toArray( 
                new IModuleStorage[storagesList.size(  )] );
        storagesList = null;
        viewers =
            (IModuleViewer[])viewersList.toArray( 
                new IModuleViewer[viewersList.size(  )] );
        viewersList = null;
        reminders =
            (IModuleReminder[])remindersList.toArray( 
                new IModuleReminder[remindersList.size(  )] );
        remindersList = null;
        impexps =
            (IModule[])impexpsList.toArray( new IModule[impexpsList.size(  )] );
        impexpsList = null;
        importers =
            (IModuleImport[])importersList.toArray( 
                new IModuleImport[importersList.size(  )] );
        importersList = null;
        exporters =
            (IModuleExport[])exportersList.toArray( 
                new IModuleExport[exportersList.size(  )] );
        exportersList = null;
    }

    protected static void loadClass( final String className )
    {

        try
        {

            Class moduleClass =
                PluginsManager.class.getClassLoader(  ).loadClass( className );

            FreeGuide.log.fine( "Loading class '" + className + "'" );

            if( IModule.class.isAssignableFrom( moduleClass ) )
            {

                IModule module = (IModule)moduleClass.newInstance(  );
                setConfig( module );
                pluginsByID.put( module.getID(  ), module );

                if( module instanceof IModuleGrabber )
                {
                    grabbersList.add( module );
                }
                else if( module instanceof IModuleStorage )
                {
                    storagesList.add( module );
                }
                else if( module instanceof IModuleViewer )
                {
                    viewersList.add( module );
                }
                else if( module instanceof IModuleReminder )
                {
                    remindersList.add( module );
                }
                else if( 
                    module instanceof IModuleImport
                        || module instanceof IModuleExport )
                {
                    impexpsList.add( module );

                    if( module instanceof IModuleImport )
                    {
                        importersList.add( module );
                    }

                    if( module instanceof IModuleExport )
                    {
                        exportersList.add( module );
                    }
                }
            }
        }
        catch( Exception ex )
        {
            FreeGuide.log.log( 
                Level.SEVERE, "Error loading plugin from " + className, ex );
        }
    }

    protected static void setConfig( final IModule module )
    {
        module.setConfigStorage( 
            Preferences.userRoot(  ).node( 
                "/org/freeguide-tv/modules/" + module.getID(  ) ) );
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

        return (IModule)pluginsByID.get( id );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param locales DOCUMENT_ME!
     */
    public static void setLocale( Locale[] locales )
    {

        Iterator it = pluginsByID.values(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            IModule module = (IModule)it.next(  );
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
     * @return DOCUMENT_ME!
     */
    public static IModuleGrabber[] getGrabbers(  )
    {

        return grabbers;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModule[] getImportersAndExporters(  )
    {

        return impexps;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleReminder[] getReminders(  )
    {

        return reminders;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleStorage[] getStorages(  )
    {

        return storages;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleViewer[] getViewers(  )
    {

        return viewers;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleImport[] getImporters(  )
    {

        return importers;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static IModuleExport[] getExporters(  )
    {

        return exporters;
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

        return pluginsByID.containsKey( id );
    }
}
