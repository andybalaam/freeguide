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
		checkTextFailure(prefs.misc, "days_to_grab", failedWhat);
		// Not needed since e.g. german grabber doesn't use it:
		//checkFileFailure(prefs.misc, "grabber_config", failedWhat);
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
	
	private static void checkTextFailure(FreeGuidePreferences pref,
			String entry, Vector failedWhat) {
	
		if(FreeGuideConfigGuesser.checkValue("misc", entry, 
				FreeGuide.prefs.performSubstitutions( 
				pref.get(entry)) ) != null ) {
			
			failedWhat.add("misc." + entry);
		}
		
	}
	
	private static void checkFileFailure(FreeGuidePreferences pref, String entry, Vector failedWhat) {
	
		String pr = FreeGuide.prefs.performSubstitutions( pref.get(entry) );
		
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
}
