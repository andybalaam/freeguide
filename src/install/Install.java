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

import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.prefs.*;
import javax.swing.*;

/**
 *  An installer for FreeGuide
 *
 *  @author  Andy Balaam
 *  @created 02 July 2003
 *  @version 9
 */
public class Install extends PrefsHolder {

    /**
     *  Constructor for the Install object
     */
    public Install() {

        // Make sure we have the right Java version etc.
        StartupChecker.doJavaVersionCheck();

        prefs = new PreferencesGroup();

        // Branch into Reinstall or first time
        String install_directory = prefs.performSubstitutions(
                prefs.misc.get("install_directory"));
				
        if (install_directory == null ||
                !(new File(install_directory + File.separator +
                "FreeGuide.jar").exists())) {

            // First time install
            install(install_directory, false);

        } else {

            String txt = "There is a version of FreeGuide installed.";
            txt += System.getProperty("line.separator");
            txt += "Would  you like to uninstall it, or install the new version?";

            String[] options = {
                    "Complete Install (overwrite prefs - recommended)",
                    "Lite Install (keep old preferences)",
                    "Uninstall"};

            Object response = JOptionPane.showInputDialog(null,
                    txt,
                    "Install question",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    "Complete Install (overwrite prefs - recommended)");

            if (response == null) {
                System.err.println("Exiting installer without doing anything.");
                System.exit(0);
            } else if (response.equals("Complete Install (overwrite prefs - recommended)")) {
                install(install_directory, false);
            } else if (response.equals("Lite Install (keep old preferences)")) {
                install(install_directory, true);
            } else {
                uninstall(install_directory);
            }

        }

    }


    /**
     *  Description of the Method
     *
     *@param  install_directory   Description of the Parameter
     *@param  keepOldPreferences  Description of the Parameter
     */
    private void install(String install_directory, boolean keepOldPreferences) {

        keepOldPrefs = keepOldPreferences;

		setStandardProps();
		
		// If we haven't got a region, assume it's UK.
		if( prefs.misc.get( "region" ) == null ) {
			prefs.misc.get( "region", "UK" );
		}
		
        try {
			
            getAllRegions();

            WizardPanel[] panels = new WizardPanel[6];

            panels[0] = new LabelWizardPanel("");
            panels[0].setMessages("You are about to install FreeGuide.",
                    "Click \"Next\" to continue.");

			panels[1] = new ChoiceWizardPanel(allRegions);
			Class[] clses = new Class[1];
            clses[0] = Object.class;
			panels[1].setOnExit(this,
                    getClass().getMethod("setProps", clses));
            panels[1].setMessages("Choose your region.",
                    "This affects which listings grabber will be used.",
					KeyEvent.VK_C);
			panels[1].setConfig("misc", "region");
					
			panels[2] = new DirectoryWizardPanel();
            panels[2].setMessages("Choose your installation directory.",
                    "This will be created if it doesn't exist.",
					KeyEvent.VK_C);
            panels[2].setConfig("misc", "install_directory");

			String[] dummyChoices = new String[0];
			panels[3] = new ChoiceWizardPanel(dummyChoices);
            panels[3].setMessages("What is the name of your web browser?",
                    "Choose the default if you don't know.",
					KeyEvent.VK_W);
            panels[3].setConfig("misc", "browser");
			clses = new Class[1];
			clses[0] = ChoiceWizardPanel.class;
			panels[3].setOnEnter( this, getClass().getMethod( "enterBrowser",
				clses ) );
			clses[0] = String.class;
			panels[3].setOnExit( this, getClass().getMethod( "exitBrowser",
				clses ) );
			
			panels[4] = new PrivacyWizardPanel();
			panels[4].setConfig( "misc", "privacy" );
            clses = new Class[1];
            clses[0] = String.class;
			panels[4].setOnExit( this, getClass().getMethod( "exitPrivacy",
				clses ) );
            
            panels[5] = new InstallWizardPanel( this );
            panels[5].setMessages(
				"FreeGuide will be installed when you click \"Finish\".",
			   "If you chooose to configure your grabber, please connect to "
			   + "the Internet now.");
			clses = new Class[1];
			clses[0] = InstallWizardPanel.class;
			panels[5].setOnExit( this, getClass().getMethod( "exitFinal",
				clses ) );

			clses = new Class[0];
            new WizardFrame("FreeGuide Setup Wizard", panels,
				this, getClass().getMethod("doInstall", clses),
				this, getClass().getMethod("quitInstall", clses)
					).setVisible(true);

        } catch (java.lang.NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.SecurityException e) {
            e.printStackTrace();
        }

    }

	public String enterBrowser( ChoiceWizardPanel panel ) {
		
		String[] choices = prefs.getBrowsers();
		
		panel.setChoices( choices );
		
		return prefs.misc.get( "browser", choices[0] );
		
	}
	
	public void exitBrowser( String choice ) {
		
		String[] choices = prefs.getBrowsers();
		
		for( int i=0; i<choices.length; i++ ) {
			
			if( choices[i].equals( choice ) ) {
				
				prefs.commandline.putStrings( "browser_command",
					prefs.getCommands( "browser_command." + (i+1) ) );
				return;
				
			}
			
		}
		
		System.err.println( "exitBrowser: Chosen browser command not found!" );
		
	}
	
    public void exitPrivacy( String unused ) {
		
        String preconfig_message = prefs.misc.get( "preconfig_message" );
		if( preconfig_message != null ) {
            JOptionPane.showMessageDialog( null, preconfig_message );
        }
		
	}
    
	public void exitFinal( InstallWizardPanel panel ) {
		
		showREADME = panel.readmeCheckBox.isSelected();
		configGrabber = panel.configgrabberCheckBox.isSelected();
		
	}
    
	/**
     * Load in the standard properties file.  Note this method just stores the
	 * preferences listed in this file and then forgets anything else.
     *
     */
    private void setStandardProps() {  
		
		standardProps = new Properties();
		
		try {
			
			standardProps.load( new BufferedInputStream(
				getClass().getResourceAsStream( "/install-all.props") ) );
							
        } catch (java.io.IOException e) {
			e.printStackTrace();
        }
        
		readPrefsFromProps( standardProps );

    }
	
    /**
     *  Load in the properties file for the chosen region
     *
     *@param  boxValue  The name of the region chosen by the user.
     */
    public void setProps(Object boxValue) {
		
        String region = (String) boxValue;
		
        for (int i = 0; i < allRegions.length; i++) {
			
            if (allRegions[i].equals(region)) {

                // Load the install.props file
                specificProps = new Properties();

                try {

                    specificProps.load(
                            new BufferedInputStream(
                            getClass().getResourceAsStream(
                            "/install-" + i + ".props")));

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }

                readPrefsFromProps( specificProps );

                return;
            }
        }

        System.err.println(
                "Install.setProps - Invalid region chosen.");

    }

	/**
	 * Given a properties file real in all the preferences listed and store
	 * them.  Store them as defaults always, and possibly overwrite actual
	 * values if keepOldPrefs is false.
	 */
	private void readPrefsFromProps( Properties iProps ) {
		
        String prefString = "";
		
        for ( 	int j = 1;
				( prefString = iProps.getProperty( "prefs." + j ) ) != null;
				j++ ) {

			doPref(prefString);

        }
		
	}

    /**
     *  Gets the allRegions attribute of the Install object
     */
    private void getAllRegions() {

        int i = 0;

        Vector regs = new Vector();

        Properties pr = new Properties();
        InputStream is;

        is = getClass().getResourceAsStream("/install-" + i + ".props");

        while (is != null) {

            try {
                pr.load(new BufferedInputStream(is));

            } catch (java.io.IOException e) {
                e.printStackTrace();
                return;
            }

            regs.add(pr.getProperty("region"));

            i++;

            is = getClass().getResourceAsStream("/install-" + i + ".props");

        }

        allRegions = Utils.arrayFromVector_String(regs);

    }


    /**
     *  Remove FreeGuide and all the stuff it uses
     *
     *@param  install_directory  The directory from which to uninstall it
     */
    private void uninstall(String install_directory) {

		String w = prefs.performSubstitutions(
			prefs.misc.get( "working_directory" ) );
		
        if (w != null) {
            File work = new File(w);
            deleteDir(work);
        }
		
        File inst = new File(install_directory);
        deleteDir(inst);

        Preferences node = Preferences.userRoot().node("/org/freeguide-tv");

        try {

            node.removeNode();

        } catch (java.util.prefs.BackingStoreException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null,
                "FreeGuide has been successfully uninstalled.");

        System.err.println("Finished uninstall.");
        System.exit(0);

    }


    /**
     *  Deletes a whole directory recursively (also deletes a single file).
     *
     *@param  dir  The directory to delete
     */
    private void deleteDir(File dir) {

        if (!dir.exists()) {
            return;
        }

        if (dir.isDirectory()) {
            String[] list = dir.list();
            for (int i = 0; i < list.length; i++) {
                deleteDir(new File(dir.getPath() + File.separator + list[i]));
            }
        }

        dir.delete();

    }


    /**
     *  The main program for the Install class
     *
     *@param  args  The command line arguments
     */
    public static void main(String[] args) {

        new Install();

    }


	public void quitInstall() {
		
		System.err.println("The user quit the install before it completed.");
		System.exit(0);
		
	}
	
    /**
     *  Description of the Method
     */
    public void doInstall() {

        try {

            String install_directory = prefs.performSubstitutions(
                    prefs.misc.get("install_directory"));

            // Make the required directories
            new File(install_directory).mkdirs();
            new File(prefs.performSubstitutions(
                    prefs.misc.get("xmltv_directory"))).mkdirs();
            new File(prefs.performSubstitutions(
                    prefs.misc.get("working_directory"))).mkdirs();

			installFilesFromProps( standardProps );
			installFilesFromProps( specificProps );

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
		
		if(configGrabber) {
			
			Utils.execAndWait( null, prefs.getCommands(
				"tv_config" ), "Configuring", prefs );
                
        }
		
        if(configGrabber) {
        
            String pw_file = prefs.misc.get( "password_file" );
            
            if( pw_file != null ) {
            
                String pw = JOptionPane.showInputDialog(
                    "Enter your password:" );
                    
                try {
            
                    BufferedWriter out = new BufferedWriter( new 
                        FileWriter( prefs.performSubstitutions( pw_file ) ) );

                    out.write( pw );
                
                    out.close();
                    
                } catch( java.io.IOException e ) {
                    
                    e.printStackTrace();
                    
                }
                
            }
            
		}
		
		if( showREADME ) {
			
			String[] cmds = Utils.substitute(
				prefs.commandline.getStrings( "browser_command" ),
				"%filename%", prefs.performSubstitutions( 
					"%misc.install_directory%"
					+ System.getProperty( "file.separator" )
					+ "README.html" ) );
			
			Utils.execNoWait( cmds, prefs );
			
		}
		
		System.err.println("Finished install.");
		System.exit(0);
		
    }
	
	private void installFilesFromProps( Properties iProps )
			throws java.io.IOException {
	
		String filename = "";
		String exec = "";
		
		for( int i=1;
			 	( filename = iProps.getProperty("file." + i) ) != null;
			 	i++ ) {
			
			exec = iProps.getProperty("file." + i + ".exec");
			
			installFile(filename, exec);
            
        }
		
	}
	
    /**
     * Given a String "src>dest", copies a file in this jar (path=src) to the
	 * location given in dest.
     *
     */
    private void installFile(String command, String exec) throws java.io.IOException {

        String[] srcdest = command.split(">");

        doInstallFile(srcdest[0],
                prefs.performSubstitutions(srcdest[1]));

		if( exec != null ) {
			
			Utils.execNoWait( exec, prefs );
			
			// Give it a second to actually change.
			try {
				Thread.currentThread().sleep( 1000 );
			} catch( InterruptedException e ) {
				e.printStackTrace();
			}
						
		}
				
    }


    /**
     *  Description of the Method
     *
     *@param  src                      Description of the Parameter
     *@param  dest                     Description of the Parameter
     *@exception  java.io.IOException  Description of the Exception
     */
    private void doInstallFile(String src, String dest)
             throws java.io.IOException {

        byte[] buf = new byte[32768];

        // make the directory if it doesn't exist
        int i = dest.lastIndexOf('/');
        if (i > -1) {
            new File(dest.substring(0, i)).mkdirs();
        }

        BufferedInputStream in = new BufferedInputStream(getClass().getResourceAsStream("/" + src));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));

        int count;
        while ((count = in.read(buf, 0, buf.length)) > -1) {
            out.write(buf, 0, count);
        }

        in.close();
        out.close();

    }


    /**
     *  Description of the Method
     *
     *@param  prefString  Description of the Parameter
     */
    private void doPref(String prefString) {

        // Split this string into its constituent parts

        int i = prefString.indexOf('=');
        String key = prefString.substring(0, i);
        String value = prefString.substring(i + 1);

        i = key.indexOf('.');
        String keyCategory = key.substring(0, i);
        key = key.substring(i + 1);

        // Find out what preferences category we're dealing with
        FGPreferences pr;
        if (keyCategory.equals("misc")) {
            pr = prefs.misc;
        } else if (keyCategory.equals("commandline")) {
            pr = prefs.commandline;
        } else {
            // Following is to make it compile ok.
            pr = prefs.misc;
            System.err.println("Unknown preferences group: " + keyCategory
                    + " - Aborting");
            System.exit(1);
        }

        // Set the default value always
        pr.put("default-" + key, value);

		// Only set the real value if we're not keeping old prefs
		// and this isn't the install dir or region (unless these are null)
		boolean overwritePref = !keepOldPrefs
			&& ( !(key.equals( "install_directory" ) )
				|| prefs.misc.get( "install_directory" ) == null );
		
        if( overwritePref ) {
            pr.put(key, value);
        }
    }


    private boolean keepOldPrefs;
    private String[] allRegions;
    private Properties standardProps;
	private Properties specificProps;
    private String fs = System.getProperty("file.separator");
    private String lb = System.getProperty("line.separator");
	
	private boolean showREADME;
	private boolean configGrabber;

}
