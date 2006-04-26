package freeguide.plugins.program.freeguide.lib.fgspecific;

import freeguide.plugins.program.freeguide.FreeGuide;

import freeguide.common.lib.general.LanguageHelper;
import freeguide.common.lib.general.PreferencesHelper;
import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.IModule;
import freeguide.common.plugininterfaces.IModuleExport;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IModuleImport;
import freeguide.common.plugininterfaces.IModuleReminder;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IModuleViewer;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Plugins manager.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PluginsManager
{

    // protected static List allPlugins = new ArrayList(  );
    protected static Map pluginsInfoByID = new TreeMap(  );
    protected static List grabbersList;
    protected static List storagesList;
    protected static List viewersList;
    protected static List remindersList;
    protected static List importexportsList;
    protected static List importersList;
    protected static List exportersList;
    protected static PluginInfo applicationInfo;

    /**
     * Load all modules.
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void loadModules(  ) throws IOException
    {
        grabbersList = new ArrayList(  );
        storagesList = new ArrayList(  );
        viewersList = new ArrayList(  );
        remindersList = new ArrayList(  );
        importexportsList = new ArrayList(  );
        importersList = new ArrayList(  );
        exportersList = new ArrayList(  );

        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance(  );
            SAXParser saxParser = factory.newSAXParser(  );
        
            URL[] info = new URL[0];
            try
            {
                info = findInClassLoader(  );
            }
            catch( IOException e )
            {
                
                e.printStackTrace();
            }
            
            if( info.length == 0 )
            {
                info = findInDirectories(  );
            }
            
            for( int i = 0; i < info.length; i++ )
            {
                try
                {
                    FreeGuide.log.finest( 
                        "Loading XML from " + info[i].toString(  ) );
    
                    PluginInfo handler = new PluginInfo(  );
    
                    InputStream stream =
                        LanguageHelper.getUncachedStream( info[i] );
    
                    try
                    {
                        saxParser.parse( stream, handler );
                    }
                    finally
                    {
                        stream.close(  );
                    }
    
                    if( handler.getID(  ) == null )
                    {
    
                        continue;
                    }
    
                    pluginsInfoByID.put( handler.getID(  ), handler );
    
                    if( "program-freeguide".equals( handler.getID(  ) ) )
                    {
                        applicationInfo = handler;
                    }
                    else if( handler.getInstance(  ) instanceof IModuleGrabber )
                    {
                        grabbersList.add( handler );
                    }
                    else if( handler.getInstance(  ) instanceof IModuleStorage )
                    {
                        storagesList.add( handler );
                    }
                    else if( handler.getInstance(  ) instanceof IModuleViewer )
                    {
                        viewersList.add( handler );
                    }
                    else if( handler.getInstance(  ) instanceof IModuleReminder )
                    {
                        remindersList.add( handler );
                    }
                    else if( 
                        handler.getInstance(  ) instanceof IModuleImport
                            || handler.getInstance(  ) instanceof IModuleExport )
                    {
                        importexportsList.add( handler );
    
                        if( handler.getInstance(  ) instanceof IModuleImport )
                        {
                            importersList.add( handler );
                        }
    
                        if( handler.getInstance(  ) instanceof IModuleExport )
                        {
                            exportersList.add( handler );
                        }
                    }
    
                    if( handler.getInstance(  ) != null )
                    {
    
                        Object config = handler.getInstance(  ).getConfig(  );
    
                        if( config != null )
                        {
    
                            if( handler == applicationInfo )
                            {
                                PreferencesHelper.load( 
                                    Preferences.userRoot(  ).node( 
                                        "/org/freeguide-tv/mainController" ),
                                    config );
                            }
                            else
                            {
                                PreferencesHelper.load( 
                                    Preferences.userRoot(  ).node( 
                                        "/org/freeguide-tv/modules/"
                                        + handler.getID(  ) ), config );
                            }
                        }
                    }
                }
                catch( Exception ex )
                {
                    Application.getInstance(  ).getLogger(  ).log( 
                        Level.SEVERE, "Error loading plugin", ex );
                }
            }
        }
        catch( javax.xml.parsers.ParserConfigurationException e )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE, "Error loading plugin", e );
        }
        catch( org.xml.sax.SAXException e )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE, "Error loading plugin", e );
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public static void saveAllConfigs(  )
    {

        final Iterator it = pluginsInfoByID.values(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            PluginInfo handler = (PluginInfo)it.next(  );

            try
            {

                IModule moduleInstance = handler.getInstance(  );

                if( moduleInstance != null )
                {

                    Object config = handler.getInstance(  ).getConfig(  );

                    if( config != null )
                    {

                        if( handler == applicationInfo )
                        {
                            PreferencesHelper.save( 
                                Preferences.userRoot(  ).node( 
                                    "/org/freeguide-tv/mainController" ),
                                config );
                        }
                        else
                        {
                            PreferencesHelper.save( 
                                Preferences.userRoot(  ).node( 
                                    "/org/freeguide-tv/modules/"
                                    + handler.getID(  ) ), config );
                        }
                    }
                }
            }
            catch( Exception ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.SEVERE,
                    "Error save config for module " + handler.getID(  ), ex );
            }
        }
    }

    /**
     * Find plugin info files in classloader.
     *
     * @return list of URLs
     *
     * @throws IOException
     */
    protected static URL[] findInClassLoader(  ) throws IOException
    {

        Enumeration urls =
            PluginsManager.class.getClassLoader(  ).getResources( 
                "plugin.xml" );

        List list = Collections.list( urls );

        return (URL[])list.toArray( new URL[list.size(  )] );
    }
    
    /**
     * Go through all the subdirs of dir and add any
     * plugin.xml files to the files list.
     */
    protected static void findPluginDirs( File dir, List files )
    {
        File[] plugin_files = dir.listFiles(
            new FileFilter(  )
            {
                public boolean accept( File fl )
                {
                    return fl.toString().endsWith( "/plugin.xml" );
                }
            } );
        for( int i = 0; i < plugin_files.length; ++i )
        {
            try
            {
                files.add( plugin_files[i].toURL(  ) );
            }
            catch( java.net.MalformedURLException e )
            {
                e.printStackTrace(  );
            }
        }
         
         File[] dirs = dir.listFiles(
            new FileFilter(  )
            {
                public boolean accept( File fl )
                {
                    return fl.isDirectory(  )
                        && !fl.toString(  ).contains( "/.svn" );
                }
            } );
        
        for( int i = 0; i < dirs.length; ++i )
        {
            findPluginDirs( dirs[i], files );
        }
    }
    
    /**
     * Find plugin info files in child directories. This will be called if no
     * plugins were found in JAR files.
     *
     * @return list of URLs
     *
     * @throws IOException
     */
    protected static URL[] findInDirectories(  ) throws IOException
    {
        List files = new ArrayList(  );
        
        findPluginDirs( new File( "freeguide/plugins" ), files );
        
        return (URL[])files.toArray( new URL[files.size(  )] );
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

        PluginInfo info = (PluginInfo)pluginsInfoByID.get( id );

        if( info != null )
        {

            return info.getInstance(  );
        }
        else
        {

            return null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param id DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo getPluginInfoByID( final String id )
    {

        return (PluginInfo)pluginsInfoByID.get( id );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param locales DOCUMENT_ME!
     */
    public static void setLocale( Locale[] locales )
    {

        Iterator it = pluginsInfoByID.values(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            PluginInfo info = (PluginInfo)it.next(  );

            if( info.getInstance(  ) != null )
            {

                try
                {

                    Locale[] modLocales =
                        info.getInstance(  ).getSuppotedLocales(  );
                    info.getInstance(  ).setLocale( 
                        LanguageHelper.getPreferredLocale( 
                            locales, modLocales ) );
                }
                catch( Exception ex )
                {
                    FreeGuide.log.log( 
                        Level.SEVERE,
                        "Error set locale for module " + info.getID(  ), ex );
                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getGrabbers(  )
    {

        return (PluginInfo[])grabbersList.toArray( 
            new PluginInfo[grabbersList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getImportersAndExporters(  )
    {

        return (PluginInfo[])importexportsList.toArray( 
            new PluginInfo[importexportsList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getReminders(  )
    {

        return (PluginInfo[])remindersList.toArray( 
            new PluginInfo[remindersList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getStorages(  )
    {

        return (PluginInfo[])storagesList.toArray( 
            new PluginInfo[storagesList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getViewers(  )
    {
        return (PluginInfo[])viewersList.toArray( 
            new PluginInfo[viewersList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getImporters(  )
    {
        return (PluginInfo[])importersList.toArray( 
            new PluginInfo[importersList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo[] getExporters(  )
    {
        return (PluginInfo[])exportersList.toArray( 
            new PluginInfo[exportersList.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static PluginInfo getApplicationModuleInfo(  )
    {
        return applicationInfo;
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

        return pluginsInfoByID.containsKey( id );
    }
}
