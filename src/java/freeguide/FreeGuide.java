/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide;

import freeguide.gui.dialogs.PleaseWaitFrame;

import freeguide.gui.viewer.MainController;

import freeguide.gui.wizard.FirstTimeWizard;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PluginsManager;
import freeguide.lib.fgspecific.StartupChecker;

import freeguide.lib.general.CmdArgs;
import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.PreferencesHelper;

import freeguide.migration.Migrate;

import freeguide.plugins.IApplication;
import freeguide.plugins.IModuleStorage;
import freeguide.plugins.IModuleViewer;

import java.io.File;
import java.io.IOException;

import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * The main class called to start FreeGuide. Calls other objects to do the
 * real work. Also contains some global objects.
 *
 * @author Andy Balaam
 * @version 10
 */
public class FreeGuide
{

    /** Predefined UI module. */
    public static final String VIEWER_ID = "ui-horizontal";

    /** Predefined storage module. */
    public static final String STORAGE_ID = "storage-serfiles";

    /** DOCUMENT ME! */
    public final static Preferences PREF_ROOT =
        Preferences.userRoot(  ).node( "/org/freeguide-tv" );

    /** Storage of TV data. */
    public static IModuleStorage storage;

    /** Runtime info. */
    public static RuntimeInfo runtimeInfo = new RuntimeInfo(  );

    /** Application config info. */
    public static Config config;

    //------------------------------------------------------------------------

    /** Holds all commandline arguments */
    public static CmdArgs arguments;

    /** The log file */
    public static Logger log;

    /** DOCUMENT ME! */
    protected static PleaseWaitFrame pleaseWaitFrame;

    /**
     * Run FreeGuide.  Command line arguments are: --language lang     Set the
     * language FreeGuide uses, e.g. "en" for English, "de" for German
     * --country  ctry     Set the country variant for localisation, e.g.
     * "GB" for Great Britain --log-prefs         Write every Java
     * Preferences change to the log.
     *
     * @param args DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public FreeGuide( String[] args ) throws Exception
    {

        // Check Java version.  If wrong, exit with error
        // Also set up a log and the preferences classes.
        StartupChecker.basicSetup( args );

        if( arguments.isSet( "language" ) )
        {

            String country = arguments.getValue( "country" );

            if( country == null )
            {
                country = "";

            }

            runtimeInfo.defaultLocale =
                new Locale( arguments.getValue( "language" ), country );
        }

        setLocale( runtimeInfo.defaultLocale );

        // Find out what the documents directory is from the command line
        if( arguments.isSet( "doc_directory" ) )
        {
            runtimeInfo.docDirectory = arguments.getValue( "doc_directory" );

        }

        else
        {
            log.warning( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "no_docs_dir_supplied" ) );

        }

        if( arguments.isSet( "install_directory" ) )
        {
            runtimeInfo.installDirectory =
                arguments.getValue( "install_directory" );

        }

        else if( System.getProperty( "os.name" ).startsWith( "Windows" ) )
        {
            log.warning( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "no_install_dir_supplied" ) );

        }

        config = new Config(  );

        try
        {
            Migrate.migrateBeforeWizard(  );

        }
        catch( Exception ex )
        {
            log.log( Level.WARNING, "Error on migration", ex );
        }

        // load config
        try
        {
            PreferencesHelper.load( PREF_ROOT, config );
            config.version = Application.VERSION.getDotFormat(  );
        }
        catch( Exception ex )
        {
            log.log( Level.SEVERE, "Error load config", ex );
        }

        PluginsManager.loadModules(  );
        setLocale( config.lang );

        if( PluginsManager.getApplicationModuleInfo(  ) == null )
        {
            log.log( Level.SEVERE, "Application module not found" );
            System.exit( 1 );
        }

        Application.setInstance( 
            (IApplication)PluginsManager.getApplicationModuleInfo(  )
                                        .getInstance(  ) );

        if( Migrate.isFirstTime(  ) )
        {
            launchFirstTime(  );
        }
        else
        {

            if( Migrate.isNeedToRunWizard(  ) )
            {
                launchUpgrade(  );
            }
            else
            {
                normalStartup( null );
            }
        }
    }

    private void launchFirstTime(  )
    {
        new FirstTimeWizard( this, false );

    }

    private void launchUpgrade(  )
    {
        new FirstTimeWizard( this, true );

    }

    /**
     * DOCUMENT_ME!
     */
    public static void saveConfig(  )
    {

        try
        {
            PreferencesHelper.save( PREF_ROOT, config );

        }

        catch( Exception ex )
        {
            log.log( Level.SEVERE, "Error save config", ex );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param grabberFromWizard DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void normalStartup( String grabberFromWizard )
        throws Exception
    {
        showPleaseWait(  );

        IModuleViewer viewer =
            (IModuleViewer)PluginsManager.getModuleByID( VIEWER_ID );
        storage = (IModuleStorage)PluginsManager.getModuleByID( STORAGE_ID );

        if( viewer == null )
        {
            log.severe( "Undefined viewer for freeguide" );
        }

        if( storage == null )
        {
            log.severe( "Undefined storage for freeguide" );
        }

        if( ( viewer == null ) || ( storage == null ) )
        {
            System.exit( 2 );
        }

        ( (MainController)Application.getInstance(  ) ).start( 
            viewer, grabberFromWizard );

    }

    /**
     * Deletes a whole directory recursively (also deletes a single file).
     *
     * @param dir The directory to delete
     */
    private void deleteDir( File dir )
    {

        if( !dir.exists(  ) )
        {

            return;

        }

        if( dir.isDirectory(  ) )
        {

            String[] list = dir.list(  );

            for( int i = 0; i < list.length; i++ )
            {
                deleteDir( 
                    new File( dir.getPath(  ) + File.separator + list[i] ) );

            }
        }

        dir.delete(  );

    }

    /**
     * The method called when FreeGuide is run.
     *
     * @param args the command line arguments
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void main( String[] args ) throws Exception
    {
        new FreeGuide( args );

    }

    /**
     * Stop the program and display the supplied error message
     *
     * @param msg The error message string to display
     */
    public static void die( String msg )
    {
        log.severe( msg );
        System.exit( 1 );
    }

    /**
     * Show wait dialog.
     */
    public static void showPleaseWait(  )
    {

        if( pleaseWaitFrame == null )
        {
            pleaseWaitFrame = new PleaseWaitFrame(  );
        }

        pleaseWaitFrame.setVisible( true );
    }

    /**
     * Hide wait dialog.
     */
    public static void hidePleaseWait(  )
    {
        pleaseWaitFrame.dispose(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static TimeZone getTimeZone(  )
    {

        if( config.timeZoneName == null )
        {

            return TimeZone.getDefault(  );

        }

        else
        {

            return TimeZone.getTimeZone( config.timeZoneName );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param newLocale DOCUMENT_ME!
     */
    public static void setLocale( final Locale newLocale )
    {

        final Locale locale =
            ( newLocale == null ) ? runtimeInfo.defaultLocale : newLocale;

        PluginsManager.setLocale( new Locale[] { locale } );
        Locale.setDefault( locale );
        FreeGuide.log.fine( "Set locale to " + locale.getDisplayName(  ) );
    }

    /**
     * Class for store main application config information.
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Config
    {

        /** Freeguide version of stored config. */
        public String version = Application.VERSION.getDotFormat(  );

        /** Browser name. */
        public String browserName;

        /** Browser command. */
        public String browserCommand;

        /** Region name for first time wizard. */
        public String countryID;

        /** Working directory dor store all data. */
        public String workingDirectory;

        /** Privacy info mode. */
        public String privacyInfo;

        /** DOCUMENT ME! */
        public String timeZoneName;

        /** User's locale, or null if it use default locale. */
        public Locale lang;

        /**
         * Creates a new Config object and setup default values.
         */
        public Config(  )
        {
            workingDirectory = ( runtimeInfo.isUnix
                ? ( System.getProperty( "user.home" ) + "/.freeguide" )
                : ( runtimeInfo.installDirectory + "/data" ) );

            countryID = "UK";

            privacyInfo = "no";
        }

        /**
         * Clone object.
         *
         * @return new Config object
         */
        public Object clone(  )
        {

            Config result = new Config(  );

            PreferencesHelper.cloneObject( this, result );

            return result;

        }
    }

    /**
     * Class for store runtime information.
     *
     * @author $author$
     * @version $Revision$
     */
    public static class RuntimeInfo
    {

        /** True is working on Unix, false - on windows. */
        public boolean isUnix;

        /** Directory where all documents stored. */
        public String docDirectory;

        /** Directory, where program installed. Used on windows. */
        public String installDirectory;

        /** Default system locale or from --language, --country flags. */
        public Locale defaultLocale;

        /**
         * Creates a new RuntimeInfo object.
         */
        public RuntimeInfo(  )
        {
            isUnix = !System.getProperty( "os.name" ).startsWith( "Windows" );
            defaultLocale = Locale.getDefault(  );
        }
    }
}
