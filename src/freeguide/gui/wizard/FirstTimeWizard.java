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

import freeguide.lib.fgspecific.*;

import freeguide.lib.general.*;

import java.awt.event.*;

import java.io.*;

import java.util.*;
import java.util.prefs.*;

import javax.swing.*;

/**
 * A first time wizard for FreeGuide
 *
 * @author Andy Balaam
 * @version 10 (used to be called Install)
 */
public class FirstTimeWizard
{

    private FreeGuide launcher;
    private String[] allRegions;
    private Properties standardProps;
    private Properties specificProps;
    private String fs = System.getProperty( "file.separator" );
    private String lb = System.getProperty( "line.separator" );
    private boolean showREADME;
    private boolean configGrabber;
    private WizardFrame wizardFrame;

    /**
     * Constructor for the FirstTimeWizard object
     *
     * @param launcher DOCUMENT ME!
     * @param upgrade DOCUMENT ME!
     */
    public FirstTimeWizard( FreeGuide launcher, boolean upgrade )
    {
        this.launcher = launcher;

        setStandardProps(  );

        // If we haven't got a region, assume it's UK.
        if( FreeGuide.prefs.misc.get( "region" ) == null )
        {
            FreeGuide.prefs.misc.put( "region", "UK" );
        }

        try
        {
            getAllRegions(  );

            WizardPanel[] panels = new WizardPanel[6];

            if( upgrade )
            {
                panels[0] =
                    new LabelWizardPanel( 
                        "<html>"
                        + FreeGuide.msg.getString( 
                            "advanced_settings_will_be_overwritten" )
                        + "<html>" );
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

            panels[1] = new ChoiceWizardPanel( allRegions );

            Class[] clses = new Class[1];
            clses[0] = Object.class;
            panels[1].setOnExit( 
                this, getClass(  ).getMethod( "setProps", clses ) );
            panels[1].setMessages( 
                FreeGuide.msg.getString( "choose_your_region.1" ),
                FreeGuide.msg.getString( "choose_your_region.2" ),
                KeyEvent.VK_C );
            panels[1].setConfig( "misc", "region" );

            panels[2] = new DirectoryWizardPanel(  );
            panels[2].setMessages( 
                FreeGuide.msg.getString( "choose_your_working_directory.1" ),
                FreeGuide.msg.getString( "choose_your_working_directory.2" ),
                KeyEvent.VK_C );
            panels[2].setConfig( "misc", "working_directory" );

            String[] dummyChoices = new String[0];
            panels[3] = new ChoiceWizardPanel( dummyChoices );
            panels[3].setMessages( 
                FreeGuide.msg.getString( 
                    "what_is_the_name_of_your_web_browser.1" ),
                FreeGuide.msg.getString( 
                    "what_is_the_name_of_your_web_browser.2" ), KeyEvent.VK_W );
            panels[3].setConfig( "misc", "browser" );
            clses = new Class[1];
            clses[0] = ChoiceWizardPanel.class;
            panels[3].setOnEnter( 
                this, getClass(  ).getMethod( "enterBrowser", clses ) );
            clses[0] = String.class;
            panels[3].setOnExit( 
                this, getClass(  ).getMethod( "exitBrowser", clses ) );

            panels[4] = new PrivacyWizardPanel(  );
            panels[4].setConfig( "misc", "privacy" );

            panels[5] = new InstallWizardPanel(  );
            panels[5].setMessages( 
                FreeGuide.msg.getString( "about_to_start.1" ),
                FreeGuide.msg.getString( "about_to_start.2" ) );
            clses = new Class[1];
            clses[0] = InstallWizardPanel.class;
            panels[5].setOnExit( 
                this, getClass(  ).getMethod( "exitFinal", clses ) );

            clses = new Class[0];

            wizardFrame =
                new WizardFrame( 
                    FreeGuide.msg.getString( "freeguide_first_time_wizard" ),
                    panels, this,
                    getClass(  ).getMethod( "doFirstTime", clses ), this,
                    getClass(  ).getMethod( "quitFirstTimeWizard", clses ) );

            wizardFrame.setVisible( true );

        }
        catch( java.lang.NoSuchMethodException e )
        {
            e.printStackTrace(  );
        }
        catch( java.lang.SecurityException e )
        {
            e.printStackTrace(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param panel DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String enterBrowser( ChoiceWizardPanel panel )
    {

        String[] choices = FreeGuide.prefs.getBrowsers(  );

        panel.setChoices( choices );

        return FreeGuide.prefs.misc.get( "browser", choices[0] );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param choice DOCUMENT_ME!
     */
    public void exitBrowser( String choice )
    {

        String[] choices = FreeGuide.prefs.getBrowsers(  );

        for( int i = 0; i < choices.length; i++ )
        {

            if( choices[i].equals( choice ) )
            {
                FreeGuide.prefs.commandline.putStrings( 
                    "browser_command",
                    FreeGuide.prefs.getCommands( 
                        "browser_command." + ( i + 1 ) ) );

                return;

            }
        }

        System.err.println( "exitBrowser: Chosen browser command not found!" );

    }

    /*public void exitPrivacy( String unused ) {

    }*/
    public void exitFinal( InstallWizardPanel panel )
    {
        showREADME = panel.readmeCheckBox.isSelected(  );
        configGrabber = panel.configgrabberCheckBox.isSelected(  );

    }

    /**
     * Load in the standard properties file.  Note this method just stores the
     * preferences listed in this file and then forgets anything else.
     */
    private void setStandardProps(  )
    {

        // Then load up the properties in the file install-all.props
        standardProps = new Properties(  );

        try
        {
            standardProps.load( 
                new BufferedInputStream( 
                    getClass(  ).getResourceAsStream( "/install-all.props" ) ) );

        }
        catch( java.io.IOException e )
        {
            e.printStackTrace(  );
        }

        readPrefsFromProps( standardProps );

    }

    /**
     * Load in the properties file for the chosen region
     *
     * @param boxValue The name of the region chosen by the user.
     */
    public void setProps( Object boxValue )
    {

        String region = (String)boxValue;

        for( int i = 0; i < allRegions.length; i++ )
        {

            if( allRegions[i].equals( region ) )
            {

                // Load the install.props file
                specificProps = new Properties(  );

                try
                {
                    specificProps.load( 
                        new BufferedInputStream( 
                            getClass(  ).getResourceAsStream( 
                                "/install-" + i + ".props" ) ) );

                }
                catch( java.io.IOException e )
                {
                    e.printStackTrace(  );
                }

                readPrefsFromProps( specificProps );

                return;
            }
        }

        System.err.println( 
            "FirstTimeWizard.setProps - Invalid region chosen." );

    }

    /**
     * Given a properties file real in all the preferences listed and store
     * them.
     *
     * @param iProps DOCUMENT ME!
     */
    private void readPrefsFromProps( Properties iProps )
    {

        String prefString = "";

        for( 
            int j = 1;
                ( prefString = iProps.getProperty( "prefs." + j ) ) != null;
                j++ )
        {
            FreeGuide.prefs.put( prefString );

        }
    }

    /**
     * Gets the allRegions attribute of the FirstTimeWizard object
     */
    private void getAllRegions(  )
    {

        int i = 0;

        Vector regs = new Vector(  );

        Properties pr = new Properties(  );
        InputStream is;

        is = getClass(  ).getResourceAsStream( "/install-" + i + ".props" );

        while( is != null )
        {

            try
            {
                pr.load( new BufferedInputStream( is ) );

            }
            catch( java.io.IOException e )
            {
                e.printStackTrace(  );

                return;
            }

            regs.add( pr.getProperty( "region" ) );

            i++;

            is = getClass(  ).getResourceAsStream( "/install-" + i + ".props" );

        }

        allRegions = Utils.arrayFromVector_String( regs );

    }

    /**
     * DOCUMENT_ME!
     */
    public void quitFirstTimeWizard(  )
    {
        FreeGuide.log.info( 
            FreeGuide.msg.getString( 
                "the_user_quit_the_install_before_it_completed" ) );
        System.exit( 0 );

    }

    /**
     * Description of the Method
     */
    public void doFirstTime(  )
    {

        try
        {

            String xmltv_directory =
                FreeGuide.prefs.misc.get( "xmltv_directory" );

            if( xmltv_directory != null )
            {
                new File( 
                    FreeGuide.prefs.performSubstitutions( xmltv_directory ) )
                .mkdirs(  );
            }

            new File( 
                FreeGuide.prefs.performSubstitutions( 
                    FreeGuide.prefs.misc.get( "working_directory" ) ) ).mkdirs(  );

            // Remember what version is installed
            FreeGuide.prefs.misc.put( 
                "install_version", FreeGuide.version.getDotFormat(  ) );

            // Put all the required files in the required places
            installFilesFromProps( standardProps );
            installFilesFromProps( specificProps );

        }
        catch( java.io.IOException e )
        {
            e.printStackTrace(  );
        }

        if( configGrabber )
        {

            String preconfig_message =
                FreeGuide.prefs.misc.get( "preconfig_message" );

            if( preconfig_message != null )
            {
                JOptionPane.showMessageDialog( wizardFrame, preconfig_message );

            }

            Utils.execAndWait( 
                null, FreeGuide.prefs.getCommands( "tv_config" ),
                FreeGuide.msg.getString( "configuring" ), FreeGuide.prefs );

        }

        if( showREADME )
        {

            String[] cmds =
                Utils.substitute( 
                    FreeGuide.prefs.commandline.getStrings( "browser_command" ),
                    "%filename%",
                    FreeGuide.prefs.performSubstitutions( 
                        "%misc.doc_directory%"
                        + System.getProperty( "file.separator" )
                        + "README.html" ) );

            Utils.execNoWait( cmds, FreeGuide.prefs );

        }

        wizardFrame.dispose(  );

        if( launcher != null )
        {
            launcher.normalStartup(  );
        }
    }

    private void installFilesFromProps( Properties iProps )
        throws java.io.IOException
    {

        String filename = "";
        String exec = "";

        for( 
            int i = 1;
                ( filename = iProps.getProperty( "file." + i ) ) != null;
                i++ )
        {
            exec = iProps.getProperty( "file." + i + ".exec" );

            installFile( filename, exec );

        }
    }

    /**
     * Given a String "src>dest", copies a file in this jar (path=src) to the
     * location given in dest.
     *
     * @param command DOCUMENT ME!
     * @param exec DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     */
    private void installFile( String command, String exec )
        throws java.io.IOException
    {

        String[] srcdest = command.split( ">" );

        doInstallFile( 
            srcdest[0], FreeGuide.prefs.performSubstitutions( srcdest[1] ) );

        if( exec != null )
        {
            Utils.execNoWait( exec, FreeGuide.prefs );

            // Give it a second to actually change.
            try
            {
                Thread.currentThread(  ).sleep( 1000 );
            }
            catch( InterruptedException e )
            {
                e.printStackTrace(  );
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param src Description of the Parameter
     * @param dest Description of the Parameter
     *
     * @exception java.io.IOException Description of the Exception
     */
    private void doInstallFile( String src, String dest )
        throws java.io.IOException
    {

        byte[] buf = new byte[32768];

        // make the directory if it doesn't exist
        int i = dest.lastIndexOf( '/' );

        if( i > -1 )
        {
            new File( dest.substring( 0, i ) ).mkdirs(  );
        }

        BufferedInputStream in =
            new BufferedInputStream( 
                getClass(  ).getResourceAsStream( "/" + src ) );
        BufferedOutputStream out =
            new BufferedOutputStream( new FileOutputStream( dest ) );

        int count;

        while( ( count = in.read( buf, 0, buf.length ) ) > -1 )
        {
            out.write( buf, 0, count );
        }

        in.close(  );
        out.close(  );

    }
}