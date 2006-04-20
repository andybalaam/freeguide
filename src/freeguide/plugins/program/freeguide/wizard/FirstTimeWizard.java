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
package freeguide.plugins.program.freeguide.wizard;

import freeguide.FreeGuide;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.PluginInfo;
import freeguide.common.lib.fgspecific.PluginsManager;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.LanguageHelper;

import freeguide.plugins.program.freeguide.migration.Migrate;

import freeguide.common.plugininterfaces.IModuleConfigureFromWizard;

import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
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

    // map of properties files by region name
    private Map isoByRegion;

    // map of properties files by region name
    private Map regionByISO;
    private Map allRegionsGrabbers;
    private Map allBrowsers;
    private boolean showREADME;
    private boolean configGrabber;
    private WizardFrame wizardFrame;
    protected String selectedModuleID;

    /**
     * Constructor for the FirstTimeWizard object
     *
     * @param upgrade DOCUMENT ME!
     */
    public FirstTimeWizard( boolean upgrade )
    {
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
                    + Application.getInstance(  ).getLocalizedMessage( 
                        "advanced_settings_will_be_overwritten" ) + "<html>" );

            panels[0].setMessages( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "about_to_upgrade.1" ),
                Application.getInstance(  ).getLocalizedMessage( 
                    "about_to_upgrade.2" ) );

        }

        else
        {
            panels[0] =
                new LabelWizardPanel( 
                    Application.getInstance(  ).getLocalizedMessage( 
                        "need_to_ask_questions" ) );

            panels[0].setMessages( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "welcome_to_freeguide.1" ),
                Application.getInstance(  ).getLocalizedMessage( 
                    "welcome_to_freeguide.2" ) );

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
            Application.getInstance(  ).getLocalizedMessage( 
                "choose_your_region.1" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "choose_your_region.2" ), KeyEvent.VK_C );

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
            Application.getInstance(  ).getLocalizedMessage( 
                "choose_your_working_directory.1" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "choose_your_working_directory.2" ), KeyEvent.VK_C );

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

        panels[3] = new ChoiceWizardPanel( allBrowsers.keySet(  ) );

        panels[3].setMessages( 
            Application.getInstance(  ).getLocalizedMessage( 
                "what_is_the_name_of_your_web_browser.1" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "what_is_the_name_of_your_web_browser.2" ), KeyEvent.VK_W );

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
            Application.getInstance(  ).getLocalizedMessage( 
                "about_to_start.1" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "about_to_start.2" ) );

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
                Application.getInstance(  ).getLocalizedMessage( 
                    "freeguide_first_time_wizard" ), panels,
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
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public WizardFrame getFrame(  )
    {

        return wizardFrame;
    }

    protected static Map readMap( final String resourceName )
        throws IOException
    {

        Map props = new TreeMap(  );

        LanguageHelper.loadProperties( resourceName, props );

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

        PluginInfo[] grabbers = PluginsManager.getGrabbers(  );

        for( int i = 0; i < grabbers.length; i++ )
        {

            if( 
                grabbers[i].getInstance(  ) instanceof IModuleConfigureFromWizard )
            {

                IModuleConfigureFromWizard configurator =
                    (IModuleConfigureFromWizard)grabbers[i].getInstance(  );
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
                        String countryName = country.getDisplayCountry(  );
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
            Application.getInstance(  ).getLocalizedMessage( 
                "the_user_quit_the_install_before_it_completed" ) );
        onFinish(  );
    }

    /**
     * Description of the Method
     */
    public void onFinish(  )
    {
        config.browserCommand = (String)allBrowsers.get( config.browserName );

        new File( config.workingDirectory ).mkdirs(  );

        FreeGuide.config = config;

        FreeGuide.saveConfig(  );

        try
        {
            Migrate.migrateAfterWizard(  );
        }
        catch( Exception ex )
        {
            FreeGuide.log.log( Level.WARNING, "Error finish migration", ex );
        }

        PluginInfo mod =
            (PluginInfo)allRegionsGrabbers.get( config.countryID );

        if( mod != null )
        {
            ( (IModuleConfigureFromWizard)mod.getInstance(  ) )
            .configureFromWizard( config.countryID, configGrabber );
            selectedModuleID = mod.getID(  );
        }

        if( showREADME )
        {
            FileHelper.openFile( 
                FreeGuide.runtimeInfo.docDirectory + "/userguide.html" );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getSelectedModuleID(  )
    {

        return selectedModuleID;
    }
}
