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
package freeguide.plugins.program.freeguide;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.PreferencesHelper;
import freeguide.common.lib.general.Version;

import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.plugininterfaces.IModuleImport;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IModuleViewer;

import freeguide.plugins.program.freeguide.dialogs.PleaseWaitFrame;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;
import freeguide.plugins.program.freeguide.lib.fgspecific.StoragePipe;
import freeguide.plugins.program.freeguide.lib.general.CmdArgs;
import freeguide.plugins.program.freeguide.migration.Migrate;
import freeguide.plugins.program.freeguide.viewer.MainController;
import freeguide.plugins.program.freeguide.wizard.FirstTimeWizard;

import java.io.File;
import java.io.FileFilter;

import java.text.MessageFormat;

import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.JFrame;
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
    /** Predefined UI module. */
    public static final String VIEWER_ID = "ui-horizontal";

    /** Predefined storage module. */
    public static final String STORAGE_ID = "storage-serfiles";

    /** DOCUMENT ME! */
    public final static String DEFAULT_pref_root_name = "/org/freeguide-tv";

    /** DOCUMENT ME! */
    public static String pref_root_name;

    /** Storage of TV data. */
    public static IModuleStorage storage;

    /** Runtime info. */
    public static RuntimeInfo runtimeInfo = new RuntimeInfo(  );

    /** Application config info. */
    public static Config config;
    protected static final ResourceBundle startupMessages =
        ResourceBundle.getBundle( "resources.i18n.Startup" );

    // ------------------------------------------------------------------------
    /** Holds all commandline arguments */
    public static Properties arguments;

    /** The log file */
    public static Logger log = Logger.getLogger( "org.freeguide-tv" );
    protected static final Version MINIMUM_JAVA_VERSION = new Version( 1, 4 );

    /** DOCUMENT ME! */
    protected static PleaseWaitFrame pleaseWaitFrame;

/**
     * Run FreeGuide. Command line arguments are: --language lang Set the
     * language FreeGuide uses, e.g. "en" for English, "de" for German --country
     * ctry Set the country variant for localisation, e.g. "GB" for Great
     * Britain --log-prefs Write every Java Preferences change to the log.
     *
     * @param args
     *            Command line arguments
     *
     * @throws Exception
     *             DOCUMENT ME!
     */
    public FreeGuide( String[] args ) throws Exception
    {
        // Check Java version. If wrong, exit with error
        checkJavaVersion(  );

        arguments = CmdArgs.parse( args );

        if( arguments.containsKey( "log_level" ) )
        {
            Level lev;
            String strlev =
                arguments.getProperty( "log_level" ).toUpperCase(  );

            if( strlev.equals( "SEVERE" ) )
            {
                lev = Level.SEVERE;
            }
            else if( strlev.equals( "WARNING" ) )
            {
                lev = Level.WARNING;
            }
            else if( strlev.equals( "INFO" ) )
            {
                lev = Level.INFO;
            }
            else if( strlev.equals( "CONFIG" ) )
            {
                lev = Level.CONFIG;
            }
            else if( strlev.equals( "FINE" ) )
            {
                lev = Level.FINE;
            }
            else if( strlev.equals( "FINER" ) )
            {
                lev = Level.FINER;
            }
            else if( strlev.equals( "FINEST" ) )
            {
                lev = Level.FINEST;
            }
            else
            {
                lev = Level.INFO;
                log.warning(
                    "Unrecognised log level \"" + strlev
                    + "\", defaulting to info" );
            }

            // I know this looks wrong and totally stupid, but this is what you
            // have to do:
            log.setLevel( lev );
            log.getParent(  ).setLevel( lev );
            log.getParent(  ).getHandlers(  )[0].setLevel( lev );
        }

        if( arguments.containsKey( "pref_root" ) )
        {
            pref_root_name = arguments.getProperty( "pref_root" );
        }
        else
        {
            pref_root_name = DEFAULT_pref_root_name;
        }

        // Find out what the documents directory is from the command line
        if( arguments.containsKey( "doc_directory" ) )
        {
            runtimeInfo.docDirectory = arguments.getProperty( "doc_directory" );
        }
        else
        {
            // we will unpack doc as need
            runtimeInfo.docDirectory = null;
        }

        if( arguments.containsKey( "install_directory" ) )
        {
            runtimeInfo.installDirectory = arguments.getProperty(
                    "install_directory" );
        }
        else
        {
            runtimeInfo.installDirectory = null;

        }

        config = new Config(  );

        if( arguments.containsKey( "dump_prefs" ) )
        {
            Migrate.setDumpPrefs( true );
        }

        try
        {
            Migrate.migrateBeforeWizard(  );
        }
        catch( Exception ex )
        {
            log.log( Level.WARNING, "Error on migration", ex );
        }

        if( Migrate.isDumpPrefs(  ) )
        {
            Migrate.dumpPrefs( pref_root_name );
            log.info(
                "The preferences were written to files in the current"
                + " directory." );
            System.exit( 0 );
        }
        else
        {
            // load config
            try
            {
                PreferencesHelper.load(
                    Preferences.userRoot(  ).node( pref_root_name ), config );
                config.version = Application.VERSION.getDotFormat(  );
            }
            catch( Exception ex )
            {
                log.log( Level.SEVERE, "Error load config", ex );
            }

            PluginsManager.loadModules(  );

            if( PluginsManager.getApplicationModuleInfo(  ) == null )
            {
                die(
                    startupMessages.getString( "startup.NoApplicationModule" ) );
            }

            Application.setInstance(
                (IApplication)PluginsManager.getApplicationModuleInfo(  )
                                            .getInstance(  ) );

            setLocale( config.lang );

            String modID = null;

            if( Migrate.isNeedToRunWizard(  ) )
            {
                hidePleaseWait(  );

                final FirstTimeWizard wizard =
                    new FirstTimeWizard( !Migrate.isFirstTime(  ) );
                wizard.getFrame(  ).waitForClose(  );
                modID = wizard.getSelectedModuleID(  );
            }

            normalStartup( modID );
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public static void saveConfig(  )
    {
        try
        {
            PreferencesHelper.save(
                Preferences.userRoot(  ).node( pref_root_name ), config );

        }

        catch( Exception ex )
        {
            log.log( Level.SEVERE, "Error save config", ex );
        }
    }

    /**
     * Perform a normal startup. Loads the selected/default user
     * interface (horizontal/vertical), loads the storage module, tries to
     * import XMLTV data and starts the application
     *
     * @param grabberFromWizard DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void normalStartup( String grabberFromWizard )
        throws Exception
    {
        IModuleViewer viewer =
            (IModuleViewer)PluginsManager.getModuleByID(
                ( (MainController.Config)( (MainController)Application
                .getInstance(  ) ).getConfig(  ) ).viewerId );
        storage = (IModuleStorage)PluginsManager.getModuleByID( STORAGE_ID );

        if( viewer == null )
        {
            if( PluginsManager.getViewers(  ).length == 0 )
            {
                die( startupMessages.getString( "startup.NoUI" ) );
            }

            viewer = (IModuleViewer)PluginsManager.getModuleByID(
                    PluginsManager.getViewers(  )[0].getID(  ) );
        }

        if( storage == null )
        {
            die( startupMessages.getString( "startup.NoStorage" ) );
        }

        try
        {
            importXMLTV(  );
        }
        catch( Exception ex )
        {
            log.log( Level.WARNING, "Error loading XMLTV file", ex );
        }

        ( (MainController)Application.getInstance(  ) ).start(
            viewer, grabberFromWizard );
    }

    /**
     * Import all XMLTV files from data directory on startup.
     *
     * @throws Exception
     */
    protected void importXMLTV(  ) throws Exception
    {
        IModuleImport xmltvHandler =
            (IModuleImport)PluginsManager.getModuleByID( "importexport-xmltv" );

        if( xmltvHandler == null )
        {
            return;
        }

        File[] xmltvFiles =
            new File( config.workingDirectory ).listFiles(
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {
                        return !pathname.isDirectory(  )
                        && pathname.getName(  ).endsWith( ".xmltv" );
                    }
                } );

        if( xmltvFiles != null )
        {
            for( int i = 0; i < xmltvFiles.length; i++ )
            {
                final StoragePipe pipe = new StoragePipe(  );
                xmltvHandler.importData( xmltvFiles[i], pipe );
                pipe.finish(  );
                xmltvFiles[i].delete(  );
            }
        }
    }

    /**
     * The method called when FreeGuide is run by startup.
     *
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        try
        {
            showPleaseWait(  );
            new FreeGuide( args );
        }
        catch( Exception ex )
        {
            log.log( Level.SEVERE, "Error in main class", ex );
            System.exit( 2 );
        }
    }

    /**
     * Stop the program and display the supplied error message
     *
     * @param msg The error message string to display
     */
    public static void die( String msg )
    {
        log.severe( msg );
        JOptionPane.showMessageDialog(
            null, msg, null, JOptionPane.ERROR_MESSAGE );
        System.exit( 1 );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param msg DOCUMENT_ME!
     */
    public static void warning( String msg )
    {
        log.warning( msg );
        JOptionPane.showMessageDialog(
            null, msg, null, JOptionPane.WARNING_MESSAGE );
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
        if( pleaseWaitFrame != null )
        {
            pleaseWaitFrame.dispose(  );
            pleaseWaitFrame = null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static JFrame getPleaseWaitFrame(  )
    {
        return pleaseWaitFrame;
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
     * Sets the locale language used for translation
     *
     * @param newLocale The new locale
     */
    public static void setLocale( final Locale newLocale )
    {
        final Locale locale;

        if( newLocale == null )
        {
            locale = Locale.getDefault(  );
        }
        else
        {
            locale = newLocale;
        }

        Locale.setDefault( locale );
        PluginsManager.setLocale( locale );
        FreeGuide.log.fine( "Set locale to " + locale.getDisplayName(  ) );
    }

    /**
     * Checks if the version of java with which freeguide is currently
     * run is at least the minimum required version. The method die()s the
     * whole app if it lower.
     */
    public static void checkJavaVersion(  )
    {
        if( Version.getJavaVersion(  ).lessThan( MINIMUM_JAVA_VERSION ) )
        {
            die(
                MessageFormat.format(
                    startupMessages.getString( "startup.WrongJavaVersion" ),
                    new Object[] { System.getProperty( "java.version" ) } ) );
        }
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
            workingDirectory = System.getProperty( "user.home" )
                + File.separatorChar + ".freeguide";

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
