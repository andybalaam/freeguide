/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;

/*
 * An installer for FreeGuide
 *
 * @author  Andy Balaam
 * @version 4
 */
public class FreeGuideInstall implements FreeGuideLauncher {
	
	public FreeGuideInstall() {
		
		// Make sure we have the right Java version etc.
		FreeGuideEnvironmentChecker.runChecks();
		
		prefs = new FreeGuidePreferencesGroup(); 
		
		// Branch into Reinstall or first time
		String install_directory = prefs.performSubstitutions(
			prefs.misc.get("install_directory") );
		
		//System.out.println(install_directory);
		
		if( install_directory==null || 
			!(new File(install_directory + File.separator + 
			"FreeGuide.jar").exists()) ) {
			
			// First time install
		
			install(install_directory, false);
		
		} else {
			
			String msg = "There is a version of FreeGuide installed.  Would";
			msg += " you like to uninstall it, or install the new version?";
			
			String[] options = { 
				"Cancel",
				"Install but keep old preferences",
				"Complete re-install (recommended)", 
				"Uninstall" };
			
			int response = JOptionPane.showOptionDialog(null, msg,
				"Install question", 0, JOptionPane.QUESTION_MESSAGE, null,
				options, "Complete re-install (recommended)" );
			
			switch(response) {
				case 1:
					install(install_directory, true);
					break;
				case 2:
					install(install_directory, false);
					break;
				case 3:
					uninstall(install_directory);
					break;
				default:
					System.err.println("Exiting installer without doing anything.");
					System.exit(0);
			}
			
		}
		
	}
	
	public void reShow() {
		
	}
	
	public FreeGuideLauncher getLauncher() {
		return null;
	}
	
	public void setVisible(boolean show) {
		// Do nothing
	}
	
	private void install(String install_directory, boolean keepOldPrefs) {
		
		try {
		
			// Load the install.props file
			props = new Properties();
			props.load(new BufferedInputStream(getClass().getResourceAsStream(
				"/install.props")));
		
			// Do all the preferences in the properties file - set the defaults
			// to them, and set the real values too if we're not keeping old
			// ones.
			int i=1;
			String prefString="";
			while( (prefString=props.getProperty("prefs."+i)) != null ) {
				
				doPref(prefString, keepOldPrefs);
				i++;
				
			}
		
			/*if(install_directory==null) {
		
				install_directory = props.getProperty("default_dir");
				if(install_directory.equals("")) {
					install_directory = System.getProperty("user.home") + fs + "freeguide-tv" + fs;
				}
				
			}*/
		
			//prefs.misc.put("install_directory", install_directory);
		
			FreeGuideWizardPanel[] panels = new FreeGuideWizardPanel[3];
			
			panels[0] = new FreeGuideLabelWizardPanel("");
			panels[0].setMessages("You are about to install FreeGuide.",
				"Click \"Next\" to continue.");
		
			panels[1] = new FreeGuideDirectoryWizardPanel();
			panels[1].setMessages("Choose your installation directory.",
				"This will be created if it doesn't exist.");
			panels[1].setConfig("misc", "install_directory");
		
			panels[2] = new FreeGuideLabelWizardPanel("Now you need to configure your grabber before you can start using it.");
			panels[2].setMessages("FreeGuide will be installed when you click \"Finish\".", "Read the README in the directory you chose to find out how.");
	
			new FreeGuideWizard("FreeGuide Setup Wizard", panels ,this, this, getClass().getMethod("doInstall", new Class[0])).setVisible(true);
		
		} catch(java.io.IOException e) {
			e.printStackTrace();
		} catch(java.lang.NoSuchMethodException e) {
			e.printStackTrace();
		} catch(java.lang.SecurityException e) {
			e.printStackTrace();
		}
		
	}
	
	private void uninstall(String install_directory) {
		
		File inst = new File(install_directory);
		deleteDir(inst);
		
		String w = prefs.misc.get("working_directory");
		if(w!=null) {
			File work = new File(w);
			deleteDir(work);
		}
		
		Preferences node = Preferences.userRoot().node("/org/freeguide-tv");
		
		try {
		
			node.removeNode();
			
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
		
		JOptionPane.showMessageDialog(null,
			"FreeGuide has been successfully uninstalled.");
		
		System.err.println("Finished uninstall.");
		System.exit(0);
		
	}
	
	/**
	 * Deletes a whole directory recursively (also deletes a single file).
	 */
	private void deleteDir(File dir) {
		
        if(!dir.exists()) return;
		
        if(dir.isDirectory()) {
			String[] list = dir.list();
			for(int i = 0; i < list.length; i++) {
            	deleteDir(new File(dir.getPath() + File.separator + list[i]));
        	}
		}

        dir.delete();
		
	}
	
	public static void main(String[] args) {
		
		new FreeGuideInstall();
		
	}
	
	public void doInstall() {
		
		try {
		
			String install_directory = prefs.performSubstitutions( 
					prefs.misc.get("install_directory") );
		
			//FreeGuidePreferencesGroup prefs = new FreeGuidePreferencesGroup();
		
			/*if(!install_directory.endsWith(fs)) {
				install_directory += fs;
			}
			String working_directory = install_directory + props.getProperty("working_dir") + fs;
		
			boolean full_paths = props.getProperty("full_paths").equals("true");
		
			String xmltv_directory;
			if(full_paths) {
				xmltv_directory = install_directory + props.getProperty("xmltv_dir") + fs;
			} else {
				xmltv_directory = props.getProperty("xmltv_dir") + fs;
			}*/
		
			// Make the required directories
			new File( install_directory ).mkdirs();
			new File( prefs.performSubstitutions( 
				prefs.misc.get("xmltv_directory") ) ).mkdirs();
			new File( prefs.performSubstitutions( 
				prefs.misc.get("working_directory") ) ).mkdirs();
		
			// Copy in the files
			int i=1;
			String filename="";
			while( (filename=props.getProperty("file."+i)) != null ) {
				installFile(filename);
				i++;
			}
		
			// Do the shared files (Win only)
			/*i=1;
			filename="";
			while( (filename=props.getProperty("share."+i)) != null ) {
				// FIXME this is a hack!
				installFile(filename, "C:\\Perl\\");
				i++;
			}*/
		
			// Set up registry
			/*refs.misc.put("os", props.getProperty("os"));
			prefs.misc.put("country", props.getProperty("country"));
			prefs.misc.put("browser_name", props.getProperty("browser_name"));
			prefs.misc.put("working_directory", working_directory);
			prefs.misc.putInt("days_to_grab", 7);
			prefs.misc.put("xmltv_directory", xmltv_directory);
		
			String grabber_exe = props.getProperty("grabber_exe");
			String grabber_config = props.getProperty("grabber_config");
			String splitter_exe = props.getProperty("splitter_exe");
			String browser_exe = props.getProperty("browser_exe");
		
			String[] grabber = new String[2];
			String[] browser = new String[1];
		
			if(full_paths) {
		
				grabber[0] = "\"" + xmltv_directory + grabber_exe + "\" --config-file \"" + xmltv_directory + grabber_config + "\"" + " --output \"" + working_directory + "tv-unprocessed.xmltv\"";
				grabber[1] = "\"" + xmltv_directory + splitter_exe + "\" --output \"" + working_directory + "tv-%%Y%%m%%d.xmltv\" --day_start_time 00:00 \"" + working_directory + "tv-unprocessed.xmltv\"";
			
				browser[0] = "\"" + browser_exe +"\" \"%filename%\"";
			
				prefs.misc.put("grabber_config", xmltv_directory + grabber_config );
			
			} else {
			
				grabber[0] = grabber_exe + " --config-file " + System.getProperty("user.home") + fs + ".xmltv" + fs + grabber_config + " --output " + working_directory + "tv-unprocessed.xmltv";
				grabber[1] = splitter_exe + " --output " + working_directory + "tv-%%Y%%m%%d.fgd --day_start_time 00:00 " + working_directory + "tv-unprocessed.xmltv";
			
				browser[0] = browser_exe + " %filename%";
			
				prefs.misc.put("grabber_config", System.getProperty("user.home") + fs + ".xmltv" + fs + grabber_config );
			
			}
		
			prefs.commandline.putStrings("tv_grab", grabber);
			prefs.commandline.putStrings("browser_command", browser);
			prefs.misc.putFreeGuideTime("day_start_time", new FreeGuideTime(6,0));
			*/
		
			System.err.println("Finished install.");
			System.exit(0);
			
		} catch(java.io.IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/*private void installFile(String name) throws java.io.IOException {
		installFile(name, install_directory);
	}*/
		
	private void installFile(String command) throws java.io.IOException {

		//System.out.println("Installing file: " + name);
		
		String[] srcdest = command.split(">");
		String src = srcdest[0];
		String dest = prefs.performSubstitutions( srcdest[1] );
		
		byte[] buf = new byte[32768];
		
		// make the directory if it doesn't exist
		
		int i = dest.lastIndexOf('/');
		if(i>-1) { 
			new File(dest.substring(0, i)).mkdirs();
		}
		
		BufferedInputStream in = new BufferedInputStream(getClass().getResourceAsStream("/" + src));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));

		int count;
		while((count = in.read(buf, 0, buf.length)) > -1) {
			out.write(buf,0,count);
		}
			
		in.close();
		out.close();
		
	}
	
	private void doPref(String prefString, boolean keepOldPrefs) {
		
		// Split this string into its constituent parts
		
		int i = prefString.indexOf('=');
		String key = prefString.substring(0, i);
		String value = prefString.substring(i+1);
		
		i = key.indexOf('.');
		String keyCategory = key.substring(0, i);
		key = key.substring(i+1);
				
		// Find out what preferences category we're dealing with
		FreeGuidePreferences pr;
		if(keyCategory.equals("misc")) {
			pr = prefs.misc;
		} else if(keyCategory.equals("commandline")) {
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
		if(!keepOldPrefs) {
			pr.put(key, value);
		}
		
	}
	
	private Properties props;
	private FreeGuidePreferencesGroup prefs;
	private String fs = System.getProperty("file.separator");
	private String lb = System.getProperty("line.separator");
	
}
