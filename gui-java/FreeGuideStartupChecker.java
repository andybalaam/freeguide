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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * FreeGuideStartupChecker
 *
 * Provides a single static method that checks everything is ok, and exits if
 * not.
 *
 * @author  Andy Balaam
 * @version 2
 */
public class FreeGuideStartupChecker {

	public static void runChecks(FreeGuideLauncher launcher, String[] args) {
		
		// --------------------------------------------------------------------
		// Potentially fatal checks
		
		// Check we can make a log file
		if(!setupLog()) {
			System.err.println("FreeGuide - Failed to create log file.");
			System.exit(1);
		}
		
		// Check the user has the right version of Java
		if(!checkJavaVersion()){FreeGuide.die("Halted due to wrong Java version.");}
		
		// Check any arguments that were passed in are ok
		if(!processArgs(args)) {FreeGuide.die("Argument processing failed.");}
		
		// Set up the Preferences clases
		if(!setupPrefs()) {	FreeGuide.die("Failed to set up configuration.");}
		
		// --------------------------------------------------------------------
		// Checks that need correction
		
		// Variables that will be true if something needs correcting
		boolean failRunBefore;
		boolean failOS, failCountry, failBrowserName;
		boolean failXMLTVCmdDir;
		boolean failGrabber, failBrowser, failDayStartTime;
		//, failStyleSheet;
		boolean failXMLTVCfg, failWorkingDir;
		
		failRunBefore = !checkRunBefore();
		
		failOS = (FreeGuide.prefs.misc.get("os") == null);
		failCountry = (FreeGuide.prefs.misc.get("country") == null);
		failBrowserName = (FreeGuide.prefs.misc.get("browser_name") == null);
		
		failXMLTVCmdDir = !checkXMLTVCmdDir();
		
		failGrabber= (FreeGuide.prefs.commandline.getStrings("tv_grab").length == 0);
		failBrowser = (FreeGuide.prefs.commandline.getStrings("browser_command").length == 0);
		failDayStartTime = (FreeGuide.prefs.misc.get("day_start_time") == null);
		//failStyleSheet = (FreeGuide.prefs.misc.get("css_file") == null);
		
		failXMLTVCfg = !checkXMLTVCfg();
		failWorkingDir = !checkWorkingDir();
		
		boolean failSomething = failOS || failCountry || failBrowserName ||
			failXMLTVCmdDir || failWorkingDir || failGrabber || failBrowser ||
			failDayStartTime || failXMLTVCfg;
		// failStyleSheet ||
		
		int runType = failRunBefore ? FreeGuideOptionsWizard.SCREEN_FIRST_TIME : FreeGuideOptionsWizard.SCREEN_PROBLEM;
		
		if(failSomething) {
			// Something's wrong, so begin with configuration
			new FreeGuideOptionsWizard(launcher, 
				runType, failOS, failCountry, 
				failBrowserName, failXMLTVCmdDir, failWorkingDir, failGrabber, 
				failBrowser, failDayStartTime, failXMLTVCfg).setVisible(true);
			//failStyleSheet, 
		} else {
			// All is ok, so begin with viewer
			new FreeGuideViewer(launcher);
		}
		
		/*if(!checkXMLTV()) {
			FreeGuide.die("Halted since XMLTV tools not found.");
		}
		
		if(!checkWorkingDir()) {
			FreeGuide.die("Failed to set up a working directory.");
		}*/
		
		//checkDayStartTime();
		
		FreeGuide.log.info("Checks ok, starting FreeGuide " + FreeGuide.getVersion() + " ...");
		
	}

	private static boolean checkRunBefore() {
		
		int version_major = FreeGuide.prefs.misc.getInt("version_major", -1);
		int version_minor = FreeGuide.prefs.misc.getInt("version_minor", -1);
		int version_revision = FreeGuide.prefs.misc.getInt("version_revision", -1);
		
		// If we'return using an old version, update it here and let return false
		// so that the first time wizard runs.
		if(	version_major != FreeGuide.version_major ||
			version_minor != FreeGuide.version_minor ||
			version_revision != FreeGuide.version_revision) {
			FreeGuide.prefs.misc.putInt("version_major", FreeGuide.version_major);
			FreeGuide.prefs.misc.putInt("version_minor", FreeGuide.version_minor);
			FreeGuide.prefs.misc.putInt("version_revision", FreeGuide.version_revision);
			return false;
		
			// FIXME if the preferences setup changes we'll
			// need to have clever stuff in an "else" here.
		
		}
	
		return true;
	}
	
	/**
	 * checkDayStartTime
	 *
	 * Checks we have a start time for our days, and adds one if not.
	 */
	 /*private static void checkDayStartTime() {
		 
		 String ds = FreeGuide.prefs.misc.get("day_start_time");
		 if(ds == null) {
			 
			 FreeGuide.prefs.misc.putFreeGuideTime("day_start_time", new FreeGuideTime(06,00));
			 
		 }
		 
	 }*/
	
	/**
	* checkJavaVersion
	*
	* Checks we have at least Java 1.4.
	*/
	private static boolean checkJavaVersion() {
		
		// Check for Java 1.4 +
		return isJavaVersionAtLeast(1, 4, 0);
		
	}
	
	private static boolean isJavaVersionAtLeast(int wantedMajor, int wantedMinor, int wantedRevision) {
		
		// Find out the version from the system
		String versionString = System.getProperty("java.version");
		
		// Split it
		Pattern dot = Pattern.compile("\\.|_");
		String[] splitVersion = dot.split(versionString);
		
		// If we've got something unexpected, say it's wrong
		if(splitVersion.length<3) {
			return false;
		}
		
		// Parse the bits
		int actualMajor = Integer.parseInt(splitVersion[0]);
		int actualMinor = Integer.parseInt(splitVersion[1]);
		int actualRevision = Integer.parseInt(splitVersion[2]);
		
		// Check we have the required version
		if(actualMajor > wantedMajor ||
			(actualMajor == wantedMajor && actualMinor > wantedMinor) ||
			(actualMajor == wantedMajor && actualMinor == wantedMinor && actualRevision >= wantedRevision)) {
			return true;
		}
		
		// If not, we fail
		return false;
		
	}
	
	/**
	* processArgs
	*
	* Processes the commandline arguments and stores them.
	*/
	private static boolean processArgs(String[] args) {
		// Process the command line arguments and store them
		FreeGuide.arguments = new FreeGuideCmdArgs(args);
	
		return FreeGuide.arguments.noErrors();
	
	}
	
	/**
	* setupLog
	*
	* Creates a log file.
	*/
	private static boolean setupLog() {
	
		// Set up the logger
		FreeGuide.log = Logger.getLogger("org.freeguide-tv");

		return true;
		
	}
	
	/**
	* setupPrefs
	*
	* Creates the preferences object that holds the configuration info
	*/
	private static boolean setupPrefs() {
	
		// Make the object that holds all the Preferences objects
		FreeGuide.prefs = new FreeGuidePreferencesGroup();
		
		return FreeGuide.prefs.noErrors();
		
	}
	
	/**
	* checkXMLTV
	*
	* Creates that the XMLTV tools are installed
	*/
	public static boolean checkXMLTVCmdDir() {
	
		String fname =  FreeGuide.prefs.misc.get("xmltv_directory");
		
		// If there is no config file entry, we fail
		if(fname==null) {return false;}
		
		// Otherwise check the file exists
		File xmltvDir = new File(FreeGuide.prefs.performSubstitutions(fname));
		
		return testXMLTVCmdDir(xmltvDir);
		
		/*
		
		// First check whether it's already in the preferences
		
		// Come up with a guess as to where XMLTV would be
		String guess;
		boolean win = System.getProperty("os.name").startsWith("Windows");
		
		if(win) {
			// Some kind of windows
			guess = "C:/Program Files/xmltv/";
		} else {
			// Some kind of Unix, hopefully
			guess = "/usr/bin/";
		}
		
		// Get the preference using the guess as a default
		String fname = FreeGuide.prefs.performSubstitutions( FreeGuide.prefs.misc.get("xmltv_directory", guess) );
		File xmltvDir = new File(fname);
		
		// Check the chosen directory
		while(!testXMLTV(xmltvDir)) {
			
			// Ask the user for a dir to try
			String msg = "Could not find XMLTV in the chosen or default locations.\n"+
				"FreeGuide needs XMLTV to work.  Please choose a location\n"+
				"where XMLTV is installed.\n"+
				"If you have not installed XMLTV please download it from\n"+
				"http://www.doc.ic.ac.uk/~epa98/work/apps/xmltv/";
			FreeGuideDirectoryDialog dirDialog = new FreeGuideDirectoryDialog(null, msg, xmltvDir);
			xmltvDir = dirDialog.getDirectory();

			// This has buttons OK (choose dir), Cancel (forget dir)
			// and Quit (exit program - done immediately)
						
			// If the user chose to skip creation, exit
			if(dirDialog.cancelClicked()) {
				FreeGuide.log.warning("Chosen to skip choice of XMLTV directory - problems may arise.");
				return true;
			}
			
			// If the user chose to quit, do it
			if(dirDialog.quitClicked()) {
				return false;
			}
			
			// Otherwise we have chosen a new possibility to test
			FreeGuide.prefs.misc.put("xmltv_directory", xmltvDir.getPath());
			
		}
		
		// The chosen directory passed the test
		return true;
		*/
	}
		
	/** Find whether a proposed XMLTV directory is correct.  Checks this
	 * by looking for the file tv_split in it.
	 */
	public static boolean testXMLTVCmdDir(File xmltvDir) {
		
		// Check the dir exists
		if(xmltvDir.exists()) {

			File[] tmp = xmltvDir.listFiles(new FreeGuideFilenameFilter("^tv_split.*", false));
			
			// Check a file starting with tv_split exists in this dir
			// (tv_split is used because that's the one I wrote to make
			// FreeGuide work.)
			if( tmp.length > 0 ) {
				// All is ok
				return true;
			}
			
		}
		
		// Something went wrong
		return false;
	}
	
	/**
	 * checkXMLTVCfg
	 *
	 * Check whether the file we've got for the xmtlv config file exists and is
	 * appropriate.
	 */
	public static boolean checkXMLTVCfg() {
		
		String fname =  FreeGuide.prefs.misc.get("grabber_config");
		
		// If there is no config file entry, we fail
		if(fname==null) {return false;}
		
		// Otherwise check the file exists and contains a channel entry
		File grabber_config = new File(FreeGuide.prefs.performSubstitutions(fname));
		
		return testXMLTVCfg(grabber_config);
		
	}
	
	private static boolean testXMLTVCfg(File grabber_config) {
		
		// Check the dir exists
		if(grabber_config.exists()) {

			try {
			
				BufferedReader buffy = new BufferedReader(new FileReader(grabber_config));
			
				String line;
				while( (line = buffy.readLine()) != null ) {
					line = line.trim();
					// If we found a channel entry, the file is good
					if(line.startsWith("channel") || line.startsWith("#channel")) {
						buffy.close();
						return true;
					}
				}
				
				buffy.close();
				
			} catch (java.io.IOException e) {
				e.printStackTrace();
			}

		}
		
		// Something went wrong: either file doesn't exist or it has no
		// channel entries
		return false;
		
	}
	
	/**
	* checkWorkingDir
	*
	* Checks that we've got a working directory of some kind.
	*/
	public static boolean checkWorkingDir() {
		
		// Is there a config entry?
		String workDir = FreeGuide.prefs.misc.get("working_directory");
		if(workDir==null) {return false;}
		
		File working_directory = new File(FreeGuide.prefs.performSubstitutions(workDir));
		
		return testWorkingDir(working_directory);
		
		/*
		// Find out what the config file says the working dir is
		String possWorkDir = FreeGuide.prefs.performSubstitutions(FreeGuide.prefs.misc.get("working_directory"));
		
		// Check the chosen directory
		while(!testWorkDir(possWorkDir)) {
			
			// If the config setting existed, or the user has chosen a dir,
			// we need to warn them that something went wrong.
			// (If possWorkDir is null that means they've started the program
			// for the very first time and we should not scare them.)
			if(possWorkDir!=null) {
				// Warn the user and ask whether they want to quit
				if(!warnBadWorkDir(possWorkDir)) {
					return false;
				}
			}
			
			// Find a candidate working directory
			possWorkDir = findPossWorkDir();
			
			// And ask the user
			String msg = "Working directory does not exist or is not writeable.\n" + 
				"Please choose a location to be your working directory for FreeGuide.";
			FreeGuideDirectoryDialog dirDialog = new FreeGuideDirectoryDialog(null, msg, new File(possWorkDir));
			possWorkDir = dirDialog.getDirectory().getPath();

			// This has buttons OK (choose dir), Cancel (forget dir)
			// and Quit (exit program - done immediately)
						
			// If the user chose to skip creation, exit
			if(dirDialog.cancelClicked()) {
				FreeGuide.log.warning("Chosen to skip creation of working directory - problems may arise.");
				return true;
			}
			
			// If the user chose to quit, do it
			if(dirDialog.quitClicked()) {
				return false;
			}
			
			// Otherwise we have chosen a new possibility to test
			
			// Make the directory if it doesn't exist
			File fileWorkDir = new File(possWorkDir);
			if(!fileWorkDir.exists()) {
				fileWorkDir.mkdirs();
			}
			
			FreeGuide.prefs.misc.put("working_directory", possWorkDir);
			
		}//while
		
		// Working directory is ok
		return true;
		*/
	}
	
	private static boolean testWorkingDir(File working_directory) {
		
		try {
		
			// Check it exists and you can write a file to it
			File testFile = new File(working_directory.getPath() + "test.tmp");
			testFile.createNewFile();
			if(working_directory.exists() && testFile.canWrite()) {
				
				testFile.delete();
				return true;
				
			}
			testFile.delete();
			return false;
			
		} catch(java.io.IOException e) {
			
			return false;
			
		}
	}
	
	/*private static boolean warnBadWorkDir(String possWorkDir) {
		
		String msg = "The chosen working directory cannot be created\n" + 
			"or is not writeable.  Do you want to choose another one\n" +
			"or give up and quit?\n";
				
		String[] options = { "Choose another", "Quit" };
		int r = JOptionPane.showOptionDialog(null, msg, "Bad working directory", 0, JOptionPane.WARNING_MESSAGE, null, options, "Choose another" );

		return (r==0);
		
	}*/
	
	/**
	 * Look for a possible working directory location.
	 */
	/*private static String findPossWorkDir() {
		
		String home = System.getProperty("user.home");
		String fs = System.getProperty("file.separator");
		
		return home + fs + ".xmltv" + fs +"freeguide-tv";
		
	}*/
	
}
