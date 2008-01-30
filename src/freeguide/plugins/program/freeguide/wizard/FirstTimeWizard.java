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

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.FileHelper;

import freeguide.common.plugininterfaces.IModuleConfigureFromWizard;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;
import freeguide.plugins.program.freeguide.migration.Migrate;

import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JOptionPane;

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
     * @param upgrade
     *            DOCUMENT ME!
     */
    public FirstTimeWizard( boolean upgrade )
    {
        config = (FreeGuide.Config)FreeGuide.config.clone(  );

        // setStandardProps( );
        // If we haven't got a region, assume it's UK.
        /*
         * if( FreeGuide.prefs.misc.get( "region" ) == null )
         *
         *  {
         *
         *
         * FreeGuide.prefs.misc.put( "region", "UK" );
         *
         *  }
         */
        getAllRegions(  );

        allBrowsers = getAllBrowsers(  );

        List<WizardPanel> panels = new ArrayList<WizardPanel>(  );

        panels.add( createFirstPanel( upgrade ) );
        panels.add( createRegionPanel(  ) );
        panels.add( createWorkingDirectoryPanel(  ) );
        panels.add( createBrowserPanel(  ) );
        panels.add( createPrivacyPanel(  ) );
        panels.add( createInstallPanel(  ) );

        wizardFrame =
            new WizardFrame(
                Application.getInstance(  )
                           .getLocalizedMessage(
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
     *
    DOCUMENT ME!
     *
     * @return
     */
    private InstallWizardPanel createInstallPanel(  )
    {
        InstallWizardPanel installPanel = new InstallWizardPanel(  );

        installPanel.setMessages(
            Application.getInstance(  ).getLocalizedMessage(
                "about_to_start.1" ),
            Application.getInstance(  ).getLocalizedMessage(
                "about_to_start.2" ) );

        installPanel.setOnExit(
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    showREADME = ( (InstallWizardPanel)panel ).readmeCheckBox
                        .isSelected(  );

                    configGrabber = ( (InstallWizardPanel)panel ).configgrabberCheckBox
                        .isSelected(  );

                    FreeGuide.config.browserName = config.browserName;

                    FreeGuide.config.browserCommand = config.browserCommand;

                    FreeGuide.config.countryID = config.countryID;

                    FreeGuide.config.workingDirectory = config.workingDirectory;

                }
            } );

        return installPanel;
    }

    /**
     *
    DOCUMENT ME!
     *
     * @return
     */
    private PrivacyWizardPanel createPrivacyPanel(  )
    {
        PrivacyWizardPanel privacyPanel = new PrivacyWizardPanel(  );

        privacyPanel.setOnEnter(
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    panel.setBoxValue( config.privacyInfo );

                }
            } );

        privacyPanel.setOnExit(
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.privacyInfo = (String)panel.getBoxValue(  );

                }
            } );

        return privacyPanel;
    }

    /**
     *
    DOCUMENT ME!
     *
     * @return
     */
    private ChoiceWizardPanel createBrowserPanel(  )
    {
        ChoiceWizardPanel browserPanel =
            new ChoiceWizardPanel( allBrowsers.keySet(  ) );

        browserPanel.setMessages(
            Application.getInstance(  )
                       .getLocalizedMessage(
                "what_is_the_name_of_your_web_browser.1" ),
            Application.getInstance(  )
                       .getLocalizedMessage(
                "what_is_the_name_of_your_web_browser.2" ), KeyEvent.VK_W );

        if( config.browserName == null )
        {
            config.browserName = defaultBrowser;
        }

        browserPanel.setOnEnter(
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    panel.setBoxValue( config.browserName );

                }
            } );

        browserPanel.setOnExit(
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.browserName = (String)panel.getBoxValue(  );

                }
            } );

        return browserPanel;
    }

    /**
     *
    DOCUMENT ME!
     *
     * @return
     */
    private DirectoryWizardPanel createWorkingDirectoryPanel(  )
    {
        DirectoryWizardPanel thirdPanel = new DirectoryWizardPanel(  );

        thirdPanel.setMessages(
            Application.getInstance(  )
                       .getLocalizedMessage(
                "choose_your_working_directory.1" ),
            Application.getInstance(  )
                       .getLocalizedMessage(
                "choose_your_working_directory.2" ), KeyEvent.VK_C );

        thirdPanel.setOnEnter(
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    panel.setBoxValue( config.workingDirectory );

                }
            } );

        thirdPanel.setOnExit(
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.workingDirectory = ( (File)panel.getBoxValue(  ) )
                        .getPath(  );

                }
            } );

        return thirdPanel;
    }

    /**
     *
    DOCUMENT ME!
     *
     * @param upgrade
     *
     * @return
     */
    private LabelWizardPanel createFirstPanel( boolean upgrade )
    {
        LabelWizardPanel firstPanel;

        if( upgrade )
        {
            firstPanel = new LabelWizardPanel(
                    "<html>"
                    + Application.getInstance(  )
                                 .getLocalizedMessage(
                        "advanced_settings_will_be_overwritten" ) + "<html>" );

            firstPanel.setMessages(
                Application.getInstance(  )
                           .getLocalizedMessage( "about_to_upgrade.1" ),
                Application.getInstance(  )
                           .getLocalizedMessage( "about_to_upgrade.2" ) );

        }

        else
        {
            firstPanel = new LabelWizardPanel(
                    Application.getInstance(  )
                               .getLocalizedMessage( "need_to_ask_questions" ) );

            firstPanel.setMessages(
                Application.getInstance(  )
                           .getLocalizedMessage( "welcome_to_freeguide.1" ),
                Application.getInstance(  )
                           .getLocalizedMessage( "welcome_to_freeguide.2" ) );

        }

        return firstPanel;
    }

    /**
     *
    DOCUMENT ME!
     *
     * @return
     */
    private ChoiceWizardPanel createRegionPanel(  )
    {
        ChoiceWizardPanel secondPanel =
            new ChoiceWizardPanel( isoByRegion.keySet(  ) );

        secondPanel.setOnExit(
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                }
            } );

        secondPanel.setMessages(
            Application.getInstance(  )
                       .getLocalizedMessage( "choose_your_region.1" ),
            Application.getInstance(  )
                       .getLocalizedMessage( "choose_your_region.2" ),
            KeyEvent.VK_C );

        secondPanel.setOnEnter(
            new WizardPanel.OnEnter(  )
            {
                public void onEnter( WizardPanel panel )
                {
                    ( (ChoiceWizardPanel)panel ).setBoxValue(
                        regionByISO.get( config.countryID ) );

                }
            } );

        secondPanel.setOnExit(
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.countryID = (String)isoByRegion.get(
                            panel.getBoxValue(  ) );
                }
            } );

        return secondPanel;
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
        final Properties result = new Properties(  );
        final InputStream in =
            FirstTimeWizard.class.getClassLoader(  )
                                 .getResourceAsStream( resourceName );

        if( in == null )
        {
            throw new FileNotFoundException( resourceName );
        }

        try
        {
            result.load( in );
        }
        finally
        {
            in.close(  );
        }

        return result;
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
                    "resources/main/browsers-"
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
                        needToSet = grabberInfo[j].getPriority(  ) > prevValue
                            .getPriority(  );
                    }
                    else
                    {
                        needToSet = true;
                    }

                    if( needToSet )
                    {
                        String countryName =
                            grabberInfo[j].getDisplayCountry(  );
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
            Application.getInstance(  )
                       .getLocalizedMessage(
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
            FreeGuide.log.log( Level.WARNING, "Error finishing migration", ex );
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
            try
            {
                FileHelper.showDocs(  );
            }
            catch( IOException ex )
            {
                JOptionPane.showMessageDialog(
                    wizardFrame, ex.getMessage(  ), "Error display help",
                    JOptionPane.ERROR_MESSAGE );
            }
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
