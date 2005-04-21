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

import freeguide.*;

import freeguide.gui.viewer.MainController;

import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.*;

import freeguide.migration.Migrate;

import freeguide.plugins.IModuleConfigureFromWizard;
import freeguide.plugins.IModuleGrabber;

import freeguide.plugins.grabber.xmltv.GrabberXMLTV;

import java.awt.event.*;

import java.io.*;

import java.util.*;

/**
 * A first time wizard for FreeGuide
 *
 * @author Andy Balaam
 * @version 10 (used to be called Install)
 */
public class FirstTimeWizard
{

    private FreeGuide.Config config;
    private FreeGuide launcher;

    // map of properties files by region name
    private Map allRegions;
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

        panels[1] = new ChoiceWizardPanel( allRegions.keySet(  ) );

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
                        config.regionName );

                }
            } );

        panels[1].setOnExit( 
            new WizardPanel.OnExit(  )
            {
                public void onExit( WizardPanel panel )
                {
                    config.regionName = (String)panel.getBoxValue(  );

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

                    FreeGuide.config.regionName = config.regionName;

                    FreeGuide.config.regionTree = config.regionTree;

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

            return readMap( 
                "main/browsers-"
                + ( FreeGuide.runtimeInfo.isUnix ? "lin" : "win" )
                + ".properties" );

        }

        catch( IOException ex )
        {
            ex.printStackTrace(  );

            return null;

        }
    }

    /**
     * Gets the allRegions attribute of the FirstTimeWizard object
     */
    private void getAllRegions(  )
    {

        try
        {
            allRegions = readMap( "main/regions.properties" );

        }

        catch( IOException ex )
        {
            ex.printStackTrace(  );

        }

        /*


        * try {


        *


        * String[] resources = LanguageHelper.listResources( getClass(


        * ).getClassLoader( ), "freeguide/plugins/grabber/xmltv/resources/xmltv" +


        * osSuffix, ".properties" );


        *


        * for( int i = 0; i < resources.length; i++ ) {


        *


        * Properties pr = new Properties( ); pr.load( new BufferedInputStream(


        * getClass( ).getClassLoader( ).getResourceAsStream( resources[i] ) ) );


        * allRegions.put( pr.getProperty( "region" ), pr ); } } catch( IOException ex ) {


        * ex.printStackTrace( ); }


        */
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

        config.regionTree = (String)allRegions.get( config.regionName );

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
                ex.printStackTrace(  );

            }
        }

        IModuleGrabber mod = PluginsManager.getGrabberByID( "xmltv" );

        if( mod instanceof IModuleConfigureFromWizard )
        {
            ( (IModuleConfigureFromWizard)mod ).configureFromWizard( 
                config.regionTree, configGrabber );
        }

        /*if( configGrabber )


        {




               //new GrabberXMLTV().


                String preconfig_message =


                    FreeGuide.prefs.misc.get( "preconfig_message" );




                if( preconfig_message != null )


                {


                    JOptionPane.showMessageDialog( wizardFrame, preconfig_message );




                }




                new GrabberController(  ).grabXMLTV(


                    null, FreeGuide.prefs.getCommands( "tv_config" ),


                    FreeGuide.msg.getString( "configuring" ), null );


        }*/
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
                ex.printStackTrace(  );
            }

            /*            String[] cmds =


                Utils.substitute(


                    FreeGuide.prefs.commandline.getStrings( "browser_command" ),


                    "%filename%",


                    FreeGuide.prefs.performSubstitutions(


                        "%misc.doc_directory%"


                        + System.getProperty( "file.separator" )


                        + "README.html" ) );






            Utils.execNoWait( cmds, FreeGuide.prefs );*/
        }

        wizardFrame.dispose(  );

        if( launcher != null )
        {
            launcher.normalStartup( mod.getID(  ) );

        }
    }
}
