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

import freeguide.lib.fgspecific.PluginsManager;
import freeguide.lib.fgspecific.StartupChecker;
import freeguide.lib.fgspecific.data.TVChannel;

import freeguide.lib.general.CmdArgs;
import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.PreferencesHelper;
import freeguide.lib.general.Version;

import freeguide.migration.Migrate;

import freeguide.plugins.IModuleViewer;
import freeguide.plugins.IStorage;

import freeguide.plugins.storage.serfiles.StorageSerFilesByDay;

import java.io.File;
import java.io.IOException;

import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JOptionPane;

/**
 * The main class called to start FreeGuide. Calls other objects to do the
 * real work. Also contains some global objects.
 *
 * @author Andy Balaam
 * @version 10
 */
public class FreeGuide
{

    /** The current version of the programme */
    public final static Version version = new Version( 0, 10, 1 );

    /** DOCUMENT ME! */
    public final static Preferences PREF_ROOT =
        Preferences.userRoot(  ).node( "/org/freeguide-tv" );

    /** Main application frame controller. */
    public static MainController mainController;

    /** Storage of TV data. */
    public static IStorage storage;

    /** Runtime info. */
    public static RuntimeInfo runtimeInfo = new RuntimeInfo(  );

    /** Application config info. */
    public static Config config;

    //------------------------------------------------------------------------

    /** Holds all commandline arguments */
    public static CmdArgs arguments;

    /** Holds all preferences info */

    //    public static PreferencesGroup prefs;

    /** The log file */
    public static Logger log;

    /** The locale set from the command line. */
    public static Locale[] locales = new Locale[1];

    /** The bundle of internationalized messages. */
    public static LanguageHelper msg;

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
     */
    public FreeGuide( String[] args )
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

            locales[0] =
                new Locale( arguments.getValue( "language" ), country );

        }

        else
        {
            locales[0] = Locale.getDefault(  );

        }

        try
        {

            Locale[] supportedLocales =
                LanguageHelper.getLocaleList( 
                    FreeGuide.class.getClassLoader(  ), "i18n/MessagesBundle" );
            msg = new LanguageHelper( 
                    FreeGuide.class.getClassLoader(  ), "i18n/MessagesBundle",
                    LanguageHelper.getPreferredLocale( 
                        locales, supportedLocales ) );

        }

        catch( IOException ex )
        {
            log.log( Level.SEVERE, "Error loading i18n data", ex );
            System.exit( 1 );
        }

        PluginsManager.setLocale( locales );

        // Find out what the documents directory is from the command line
        if( arguments.isSet( "doc_directory" ) )
        {
            runtimeInfo.docDirectory = arguments.getValue( "doc_directory" );

        }

        else
        {
            log.warning( FreeGuide.msg.getString( "no_docs_dir_supplied" ) );

        }

        if( arguments.isSet( "install_directory" ) )
        {
            runtimeInfo.installDirectory =
                arguments.getValue( "install_directory" );

        }

        else if( System.getProperty( "os.name" ).startsWith( "Windows" ) )
        {
            log.warning( FreeGuide.msg.getString( "no_install_dir_supplied" ) );

        }

        config = new Config(  );

        Migrate m = new Migrate(  );

        try
        {
            m.migrateBeforeWizard(  );

        }

        catch( Exception ex )
        {
            log.log( Level.WARNING, "Error on migration", ex );
        }

        // load config
        try
        {
            PreferencesHelper.loadObject( PREF_ROOT, "config.", config );

        }
        catch( Exception ex )
        {
            log.log( Level.SEVERE, "Error load config", ex );
        }

        if( config.version == null )
        {
            launchFirstTime(  );

        }

        else
        {

            // If the installed version number is lower than the version we are
            // running, we need to upgrade
            if( new Version( config.version ).lessThan( version ) )
            {
                launchUpgrade( m );

            }

            else
            {
                normalStartup( null );

            }
        }

        // [Note: upgrade question just notifies you that your custom settings
        // will be over-written and lets you cancel.]
    }

    private void launchFirstTime(  )
    {
        new FirstTimeWizard( this, false, null );

    }

    private void launchUpgrade( Migrate m )
    {
        new FirstTimeWizard( this, true, m );

    }

    /**
     * DOCUMENT_ME!
     */
    public static void saveConfig(  )
    {

        try
        {
            PreferencesHelper.saveObject( PREF_ROOT, "config.", config );

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
     */
    public void normalStartup( String grabberFromWizard )
    {
        showPleaseWait(  );

        Vector failedWhat = StartupChecker.runChecks(  );

        if( failedWhat.size(  ) > 0 )
        {

            // Something's wrong, so begin with configuration
            String message;

            message = FreeGuide.msg.getString( "config_messed_up" );

            for( int i = 0; i < failedWhat.size(  ); i++ )
            {
                message += ( failedWhat.get( i ) + "\n" );

            }

            message += message += FreeGuide.msg.getString( 
                "go_to_options_screen" );

            JOptionPane.showMessageDialog( 
                null, message,
                FreeGuide.msg.getString( "configuration_problems" ),
                JOptionPane.WARNING_MESSAGE );

        }

        mainController =
            new MainController( PREF_ROOT.node( "mainController" ) );

        storage = new StorageSerFilesByDay(  );

        IModuleViewer viewer = PluginsManager.getViewerByID( "Horizontal" );

        mainController.start( viewer, grabberFromWizard );

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
     */
    public static void main( String[] args )
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
     * @return DOCUMENT_ME!
     */
    public static StringBuffer getIconCacheDir(  )
    {

        StringBuffer ans = new StringBuffer(  );

        ans.append( FreeGuide.config.workingDirectory );

        ans.append( '/' );

        ans.append( TVChannel.ICONCACHE_SUBDIR );

        ans.append( '/' );

        return ans;

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
        public String version;

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

        /**
         * Creates a new RuntimeInfo object.
         */
        public RuntimeInfo(  )
        {
            isUnix = !System.getProperty( "os.name" ).startsWith( "Windows" );

        }
    }
}
