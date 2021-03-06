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

import freeguide.common.base.PluginInfo;
import freeguide.common.gui.LaunchBrowserOrError;
import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.IModuleConfigureFromWizard;

import freeguide.plugins.program.freeguide.FreeGuide;
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

/**
 * A first time wizard for FreeGuide
 *
 * @author Andy Balaam
 * @version 10 (used to be called Install)
 */
public class FirstTimeWizard
{
    private FreeGuide.Config config;

    // map of properties files by region name
    private Map isoByRegion;

    // map of properties files by region name
    private Map regionByISO;
    private Map allRegionsGrabbers;
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

        List<WizardPanel> panels = new ArrayList<WizardPanel>(  );

        panels.add( createFirstPanel( upgrade ) );
        panels.add( createRegionPanel(  ) );
        panels.add( createWorkingDirectoryPanel(  ) );
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
     * Creates the final page for the first-time wizard.
     *
     * @return The final {@link InstallWizardPanel}.
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

                    FreeGuide.config.countryID = config.countryID;

                    FreeGuide.config.workingDirectory = config.workingDirectory;

                }
            } );

        return installPanel;
    }

    /**
     * Creates the privacy panel for the first-time wizard.
     *
     * @return The {@link PrivacyWizardPanel}
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
     * Creates the working directory choice panel for the first-time
     * wizard.
     *
     * @return The {@link DirectoryWizardPanel}
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
     * Creates the introductory panel for the first-time wizard.
     *
     * @param upgrade A {@link Boolean} indicating whether or not this is an
     *        upgrade or fresh install.
     *
     * @return The introductory {@link LabelWizardPanel}.
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
     * Creates the region choice panel for the first-time wizard.
     *
     * @return The region {@link ChoiceWizardPanel}
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
            LaunchBrowserOrError.displayDocsOrError();
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
