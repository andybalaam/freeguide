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
import java.io.FileFilter;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * FreeGuideStartupChecker
 *
 * Provides a single static method that checks everything is ok, and exits if
 * not.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideStartupChecker {

	public static void runChecks(String[] args) {
		
		if(!checkJavaVersion()){
			FreeGuide.die("Halted due to wrong Java version.");
		}
		
		if(!processArgs(args)) {
			FreeGuide.die("Argument processing failed.");
		}
		
		if(!setupLog()) {
			FreeGuide.die("Failed to create log file.");
		}
		
		if(!setupPrefs()) {
			FreeGuide.die("Failed to set up configuration.");
		}
		
		if(!checkXMLTV()) {
			FreeGuide.die("Halted since XMLTV tools not found.");
		}
		
		if(!checkWorkingDir()) {
			FreeGuide.die("Failed to set up a working directory.");
		}
		
		FreeGuide.log.info("Checks ok, starting FreeGuide " + FreeGuide.version + " ...");
		
	}

	/**
	* checkJavaVersion
	*
	* Checks we have at least Java 1.4.
	*/
	private static boolean checkJavaVersion() {
		
		// Check for Java 1.4 +
		String version = System.getProperty("java.version");
		System.out.println(version);
		
		return true;
		
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
		FreeGuide.prefs = new FreeGuidePreferences();
		
		return FreeGuide.prefs.noErrors();
		
	}
	
	/**
	* checkXMLTV
	*
	* Creates that the XMLTV tools are installed
	*/
	private static boolean checkXMLTV() {
	
		// First check whether it's already in the preferences
		
		// Come up with a guess as to where XMLTV would be
		String guess;
		boolean win = System.getProperty("os.name").startsWith("Windows");
		
		if(win) {
			// Some kind of windows
			guess = "C:/Program Files/XMLTV/";
		} else {
			// Some kind of Unix, hopefully
			guess = "/usr/bin/";
		}
		
		// Get the preference using the guess as a default
		File xmltvDir = new File(FreeGuide.prefs.getMisc("xmltv_directory", guess));
		
		// Check the chosen directory
		while(!testXMLTV(xmltvDir)) {
			
			// Ask the user for a dir to try
			String msg = "Could not find XMLTV in the chosen or default locations.\n"+
				"FreeGuide needs XMLTV to work.  Please choose a location\n"+
				"where XMLTV is installed.\n"+
				"If you have not installed XMLTV please download it from\n"+
				"http://www.doc.ic.ac.uk/~epa98/work/apps/xmltv/";
			FreeGuideDirectoryDialog dirDialog = new FreeGuideDirectoryDialog(null, msg);
			xmltvDir = dirDialog.getDirectory(xmltvDir);

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
			
		}
		
		// The chosen directory passed the test
		return true;
		
	}
		
	/** Find whether a proposed XMLTV directory is correct */
	private static boolean testXMLTV(File xmltvDir) {
		
		// Check the dir exists
		if(xmltvDir.exists()) {
			
			// Check a file starting with tv_split exists in this dir
			// (tv_split is used because that's the one I wrote to make
			// FreeGuide work.)
			if( xmltvDir.listFiles(new FreeGuideFilenameFilter("^tv_split")).length > 0 ) {
				// All is ok
				return true;
			}
			
		}
		
		// Something went wrong
		return false;
	}
	
	/**
	* checkWorkingDir
	*
	* Checks that we've got a working directory of some kind.
	*/
	private static boolean checkWorkingDir() {
	
		// Find out what the config file says the working dir is
		String possWorkDir = FreeGuide.prefs.getMisc("working_directory");
		
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
			String msg = "Working directory does not exist or is not writeable.\n"+
				"Please choose a location to be your working directory for FreeGuide.";
			FreeGuideDirectoryDialog dirDialog = new FreeGuideDirectoryDialog(null, msg);
			possWorkDir = dirDialog.getDirectory(possWorkDir);

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
			
		}//while
		
		// Working directory is ok
		return true;
	}
	
	private static boolean testWorkDir(String possWorkDir) {
		
		if(possWorkDir==null) {
			return false;
		}
		
		try {
		
			// Check it exists and you can write a file to it
			File testFile = new File(possWorkDir + "test.tmp");
			testFile.createNewFile();
			if(new File(possWorkDir).exists() && testFile.canWrite()) {
				
				testFile.delete();
				return true;
				
			}
			testFile.delete();
			return false;
			
		} catch(java.io.IOException e) {
			
			return false;
			
		}
	}
	
	private static boolean warnBadWorkDir(String possWorkDir) {
		
		String msg = "The chosen working directory cannot be created\n" + 
			"or is not writeable.  Do you want to choose another one\n" +
			"or give up and quit?\n";
				
		String[] options = { "Choose another", "Quit" };
		int r = JOptionPane.showOptionDialog(null, msg, "Bad working directory", 0, JOptionPane.WARNING_MESSAGE, null, options, "Choose another" );

		return (r==0);
		
	}
	
	/**
	 * Look for a possible working directory location.
	 */
	private static String findPossWorkDir() {
		
		String home = System.getProperty("user.home");
		String fs = System.getProperty("file.separator");
		
		return home + fs + ".xmltv" + fs +"freeguide-tv";
		
	}
	
}
