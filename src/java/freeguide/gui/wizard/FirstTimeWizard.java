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
package freeguide.gui.wizard;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.StringHelper;
import freeguide.lib.general.Utils;

import freeguide.migration.Migrate;

import freeguide.plugins.IModuleConfigureFromWizard;
import freeguide.plugins.IModuleGrabber;

import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;

/**
 * A first time wizard for FreeGuide
 *
 * @author Andy Balaam
 * @version 10 (used to be called Install)
 */
public class FirstTimeWizard
{

    static private String defaultBrowser;
    private FreeGuide.Config config;
    private FreeGuide launcher;

    // map of properties files by region name
    private Map isoByRegion;

    // map of properties files by region name
    private Map regionByISO;
    private Map allRegionsGrabbers;
    private Map allBrowsers;
    private boolean showREADME;
    private boolean configGrabber;
    private WizardFrame wizardFrame;
    protected Migrate migrate;

    /**
     * Constructor for the FirstTimeWizard object
     *
     * @param launcher DOCUMENT ME!
     * @param upgrade DOCUMENT ME!
     * @param m DOCUMENT ME!
     */
    public FirstTimeWizard( FreeGuide launcher, boolean upgrade, Migrate m )
    {
        this.launcher = launcher;

        this.migrate = m;

        config = (FreeGuide.Config)FreeGuide.config.clone(  );

        // setStandardProps(  );
        // If we haven't got a region, assume it's UK.
        /*if( FreeGuide.prefs.misc.get( "region" ) == null )


        {


        FreeGuide.prefs.misc.put( "region", "UK" );


        }*/
        getAllRegions(  );

        allBrowsers = getAllBrowsers(  );

        WizardPanel[] panels = new WizardPanel[6];

        if( upgrade )
        {
            panels[0] =
                new LabelWizardPanel( 
                    "<html>"
                    + FreeGuide.msg.getString( 
                        "advanced_settings_will_be_overwritten" ) + "<html>" );

            panels[0].setMessages( 
                FreeGuide.msg.getString( "about_to_upgrade.1" ),
                FreeGuide.msg.getString( "about_to_upgrade.2" ) );

        }

        else
        {
            panels[0] =
                new LabelWizardPanel( 
                    FreeGuide.msg.getString( "need_to_ask_questions" ) );

            panels[0].setMessages( 
                FreeGuide.msg.getString( "welcome_to_freeguide.1" ),
                FreeGuide.msg.getString( "welcome_to_freeguide.2" ) );

        }

        panels[1] = new ChoiceWizardPanel( isoByRegion.keySet(  ) );

        panels[1].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                }
            } );

        panels[1].setMessages( 
            FreeGuide.msg.getString( "choose_your_region.1" ),
            FreeGuide.msg.getString( "choose_your_region.2" ), KeyEvent.VK_C );

        panels[1].setOnEnter( 
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    ( (ChoiceWizardPanel)panel ).setBoxValue( 
                        regionByISO.get( config.countryID ) );

                }
            } );

        panels[1].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.countryID =
                        (String)isoByRegion.get( panel.getBoxValue(  ) );
                }
            } );

        panels[2] = new DirectoryWizardPanel(  );

        panels[2].setMessages( 
            FreeGuide.msg.getString( "choose_your_working_directory.1" ),
            FreeGuide.msg.getString( "choose_your_working_directory.2" ),
            KeyEvent.VK_C );

        panels[2].setOnEnter( 
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    panel.setBoxValue( config.workingDirectory );

                }
            } );

        panels[2].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.workingDirectory =
                        ( (File)panel.getBoxValue(  ) ).getPath(  );

                }
            } );

        Set dummyChoices = new TreeSet(  );

        panels[3] = new ChoiceWizardPanel( allBrowsers.keySet(  ) );

        panels[3].setMessages( 
            FreeGuide.msg.getString( "what_is_the_name_of_your_web_browser.1" ),
            FreeGuide.msg.getString( "what_is_the_name_of_your_web_browser.2" ),
            KeyEvent.VK_W );

        if( config.browserName == null )
        {
            config.browserName = defaultBrowser;
        }

        panels[3].setOnEnter( 
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    panel.setBoxValue( config.browserName );

                }
            } );

        panels[3].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.browserName = (String)panel.getBoxValue(  );

                }
            } );

        panels[4] = new PrivacyWizardPanel(  );

        panels[4].setOnEnter( 
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    panel.setBoxValue( config.privacyInfo );

                }
            } );

        panels[4].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.privacyInfo = (String)panel.getBoxValue(  );

                }
            } );

        panels[5] = new InstallWizardPanel(  );

        panels[5].setMessages( 
            FreeGuide.msg.getString( "about_to_start.1" ),
            FreeGuide.msg.getString( "about_to_start.2" ) );

        panels[5].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    showREADME =
                        ( (InstallWizardPanel)panel ).readmeCheckBox
                        .isSelected(  );

                    configGrabber =
                        ( (InstallWizardPanel)panel ).configgrabberCheckBox
                        .isSelected(  );

                    FreeGuide.config.browserName = config.browserName;

                    FreeGuide.config.browserCommand = config.browserCommand;

                    FreeGuide.config.countryID = config.countryID;

                    FreeGuide.config.workingDirectory =
                        config.workingDirectory;

                }
            } );

        wizardFrame =
            new WizardFrame( 
                FreeGuide.msg.getString( "freeguide_first_time_wizard" ),
                panels,
                new Runnable(  )
                {
                    public void run(  )
                    {
                        onFinish(  );

                    }
                },
                new Runnable(  )
                {
                    public void run(  )
                    {
                        onExit(  );

                    }
                } );

        wizardFrame.setVisible( true );

    }

    /**
     * Load in the standard properties file. Note this method just stores the
     * preferences listed in this file and then forgets anything else.
     *
     * @param resourceName DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT ME!
     */

    /*    private void setStandardProps(  )


    {




    if( FreeGuide.isUnix )


    {


        osSuffix = "-lin-";


    }


    else


    {


        osSuffix = "-win-";


    }




    // Then load up the properties in the file install-all.props


    standardProps = new Properties(  );




    try


    {


        standardProps.load(


            new BufferedInputStream(


                getClass(  ).getClassLoader(  ).getResourceAsStream(


                    "main/main" + osSuffix + "all.properties" ) ) );




    }


    catch( java.io.IOException e )


    {


        e.printStackTrace(  );


    }




    readPrefsFromProps( standardProps );




    }*/

    /**
     * Given a properties file real in all the preferences listed and store
     * them.
     *
     * @param resourceName DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT ME!
     */

    /*    private void readPrefsFromProps( Properties iProps )


    {




    String prefString = "";




    for(


        int j = 1;


            ( prefString = iProps.getProperty( "prefs." + j ) ) != null;


            j++ )


    {


        FreeGuide.prefs.put( prefString );




    }


    }*/
    protected static Map readMap( final String resourceName )
        throws IOException
    {

        Map props = new TreeMap(  );

        LanguageHelper.loadProperties( 
            FirstTimeWizard.class.getClassLoader(  ).getResourceAsStream( 
                resourceName ), props );

        return props;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Map getAllBrowsers(  )
    {

        try
        {

            Map result =
                readMap( 
                    "main/browsers-"
                    + ( FreeGuide.runtimeInfo.isUnix ? "lin" : "win" )
                    + ".properties" );

            defaultBrowser = (String)result.remove( "DEFAULT" );

            return result;

        }

        catch( IOException ex )
        {
            FreeGuide.log.log( Level.SEVERE, "Error loading browser list", ex );

            return null;
        }
    }

    /**
     * Get list of all countries from all grabbers.
     */
    private void getAllRegions(  )
    {

        final TreeMap allRegions = new TreeMap(  );
        allRegionsGrabbers = new TreeMap(  );
        isoByRegion = new TreeMap(  );
        regionByISO = new TreeMap(  );

        IModuleGrabber[] grabbers = PluginsManager.getGrabbers(  );

        for( int i = 0; i < grabbers.length; i++ )
        {

            if( grabbers[i] instanceof IModuleConfigureFromWizard )
            {

                IModuleConfigureFromWizard configurator =
                    (IModuleConfigureFromWizard)grabbers[i];
                IModuleConfigureFromWizard.CountryInfo[] grabberInfo =
                    configurator.getSupportedCountries(  );

                for( int j = 0; j < grabberInfo.length; j++ )
                {

                    IModuleConfigureFromWizard.CountryInfo prevValue =
                        (IModuleConfigureFromWizard.CountryInfo)allRegions.get( 
                            grabberInfo[j].getCountry(  ) );
                    final boolean needToSet;

                    if( prevValue != null )
                    {
                        needToSet =
                            grabberInfo[j].getPriority(  ) > prevValue
                            .getPriority(  );
                    }
                    else
                    {
                        needToSet = true;
                    }

                    if( needToSet )
                    {

                        Locale country =
                            new Locale( "", grabberInfo[j].getCountry(  ) );
                        String countryName =
                            country.getDisplayCountry( 
                                FreeGuide.msg.getLocale(  ) );
                        isoByRegion.put( 
                            countryName, grabberInfo[j].getCountry(  ) );
                        regionByISO.put( 
                            grabberInfo[j].getCountry(  ), countryName );
                        allRegions.put( 
                            grabberInfo[j].getCountry(  ), grabberInfo[j] );
                        allRegionsGrabbers.put( 
                            grabberInfo[j].getCountry(  ), grabbers[i] );
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void onExit(  )
    {
        FreeGuide.log.info( 
            FreeGuide.msg.getString( 
                "the_user_quit_the_install_before_it_completed" ) );

        System.exit( 0 );

    }

    /**
     * Description of the Method
     */
    public void onFinish(  )
    {
        config.browserCommand = (String)allBrowsers.get( config.browserName );

        new File( config.workingDirectory ).mkdirs(  );

        config.version = FreeGuide.version.getDotFormat(  );

        FreeGuide.config = config;

        FreeGuide.saveConfig(  );

        if( migrate != null )
        {

            try
            {
                migrate.migrateAfterWizard(  );

            }

            catch( Exception ex )
            {
                FreeGuide.log.log( 
                    Level.WARNING, "Error finish migration", ex );
            }
        }

        IModuleGrabber mod =
            (IModuleGrabber)allRegionsGrabbers.get( config.countryID );

        if( mod instanceof IModuleConfigureFromWizard )
        {
            ( (IModuleConfigureFromWizard)mod ).configureFromWizard( 
                config.countryID, configGrabber );
        }

        if( showREADME )
        {

            String cmd =
                StringHelper.replaceAll( 
                    config.browserCommand, "%filename%",
                    FreeGuide.runtimeInfo.docDirectory + "/README.html" );

            try
            {
                Utils.execNoWait( cmd );
            }
            catch( Exception ex )
            {
                FreeGuide.log.log( Level.WARNING, "Error display README", ex );
            }
        }

        wizardFrame.dispose(  );

        if( launcher != null )
        {

            try
            {
                launcher.normalStartup( mod.getID(  ) );
            }
            catch( IOException ex )
            {
                Application.getInstance(  ).getLogger(  ).severe( 
                    "Error startup application: " + ex.getMessage(  ) );
            }
        }
    }
}
