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
import java.util.logging.Logger;
import java.util.Vector;

/**
 * FreeGuideStartupChecker
 *
 * Provides a single static method that checks everything is ok, and exits if
 * not.
 *
 * @author  Andy Balaam
 * @version 3
 */
public class FreeGuideStartupChecker {

	public static void runChecks(FreeGuideLauncher launcher, String[] args, FreeGuidePleaseWait pleaseWait) {
		
		// Check the environment is right
		FreeGuideEnvironmentChecker.runChecks();
		
		// ------------------------------------------------------
		// Check we can make a log file
		if(!setupLog()) {die("FreeGuide - Failed to create log file.");}

		// ------------------------------------------------------
		// Unused checks
		
		// Check any arguments that were passed in are ok
		if(!processArgs(args)) {FreeGuide.die("Argument processing failed.");}
		
		// Set up the Preferences clases
		if(!setupPrefs()) {FreeGuide.die("Failed to set up configuration.");}
		
		// --------------------------------------------------------------------
		// Checks that need correction
		
		// Variables that will be true if something needs correcting
		Vector failedWhat = new Vector();
		
		FreeGuidePreferencesGroup prefs = FreeGuide.prefs;
		
		checkFileFailure(prefs.misc, "xmltv_directory", failedWhat);
		checkCommandLineFailure(prefs.commandline, "tv_grab", failedWhat);
		checkCommandLineFailure(prefs.commandline, "browser_command", failedWhat);
		checkTextFailure(prefs.misc, "day_start_time", failedWhat);
		checkFileFailure(prefs.misc, "grabber_config", failedWhat);
		checkFileFailure(prefs.misc, "working_directory", failedWhat);		
		
		if(failedWhat.size()>0) {
			pleaseWait.dispose();
			// Something's wrong, so begin with configuration
			FreeGuide.log.info("Checks failed, going into configuration ...");
			new FreeGuideOptions(launcher, failedWhat).setVisible(true);
		} else {
			// All is ok, so begin with viewer
			//FreeGuide.log.info("Checks ok, starting FreeGuide " + FreeGuide.getVersion() + " ...");
			new FreeGuideViewer(launcher, pleaseWait);
		}
		
	}
	
	private static void die(String message) {
		System.err.println(message);
		System.exit(1);
	}
	
	private static void checkTextFailure(FreeGuidePreferences pref, String entry, Vector failedWhat) {
	
		if(FreeGuideConfigGuesser.checkValue("misc", entry, pref.get(entry))!=null) {
			failedWhat.add("misc." + entry);
		}
		
	}
	
	private static void checkFileFailure(FreeGuidePreferences pref, String entry, Vector failedWhat) {
	
		String pr = pref.get(entry);
		
		if( (pr==null) || 
				(FreeGuideConfigGuesser.checkValue("misc", entry, new File(pr))!=null) ) {
					
			failedWhat.add("misc." + entry);
			
		}
		
	}
	
	private static void checkCommandLineFailure(FreeGuidePreferences pref, String entry, Vector failedWhat) {
	
		if(FreeGuideConfigGuesser.checkValue("commandline", entry, pref.getStrings(entry))!=null) {
			failedWhat.add("commandline." + entry);
		}
		
	}
	
	/*private static boolean checkRunBefore() {
		
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
	}*/
	
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
	/*public static boolean checkXMLTVCmdDir() {
	
		String fname =  FreeGuide.prefs.misc.get("xmltv_directory");
		
		// If there is no config file entry, we fail
		if(fname==null) {return false;}
		
		// Otherwise check the file exists
		File xmltvDir = new File(FreeGuide.prefs.performSubstitutions(fname));
		
		return testXMLTVCmdDir(xmltvDir);
		
		
	}*/
		
	/** 
	 * Find whether a proposed XMLTV directory is correct.  Checks this
	 * by looking for the file tv_split in it.
	 */
	/*public static boolean testXMLTVCmdDir(File xmltvDir) {
		
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
	}*/
	
	/**
	 * checkXMLTVCfg
	 *
	 * Check whether the file we've got for the xmtlv config file exists and is
	 * appropriate.
	 */
	/*public static boolean checkXMLTVCfg() {
		
		String fname =  FreeGuide.prefs.misc.get("grabber_config");
		
		// If there is no config file entry, we fail
		if(fname==null) {return false;}
		
		// Otherwise check the file exists and contains a channel entry
		File grabber_config = new File(FreeGuide.prefs.performSubstitutions(fname));
		
		return testXMLTVCfg(grabber_config);
		
	}*/
	
	/*private static boolean testXMLTVCfg(File grabber_config) {
		
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
		
	}*/
	
	/**
	* checkWorkingDir
	*
	* Checks that we've got a working directory of some kind.
	*/
	/*public static boolean checkWorkingDir() {
		
		// Is there a config entry?
		String workDir = FreeGuide.prefs.misc.get("working_directory");
		if(workDir==null) {return false;}
		
		File working_directory = new File(FreeGuide.prefs.performSubstitutions(workDir));
		
		return testWorkingDir(working_directory);
		
	}*/
	
	/*private static boolean testWorkingDir(File working_directory) {
		
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
	}*/
	
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
