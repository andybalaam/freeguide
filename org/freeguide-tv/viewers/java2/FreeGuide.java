/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * The main class called to start FreeGuide.  Performs some
 * housekeeping before launching the viewer or downloader.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuide {

    /**
	 * The method called when FreeGuide is run.  Processes
	 * command line arguments, loads a config file, creates
	 * a log file, and starts the viewer or downloader.
	 *
     * @param args the command line arguments
     */
    public static void main (String args[]) {
		
		// Process the command line arguments
		arguments = new JoveCmdArgs(args);
		
		// Load the config file
		String[] vitals = {"logFile=.freeguidelog"};
		String cfgFilename = arguments.getValue("config-file");
		
		if(cfgFilename==null) {
			cfgFilename=".freeguide";
		}
		
		config = new JoveConfigFile(vitals, cfgFilename);
		
		// Open the log file
		log = new JoveLogFile(config.getValue("logFile"));
		
		// If we've not got a proper config file set up
		if(config.getValue("freeguideDir")==null) {
			
			// Send the user straight to the options screen
			new FreeGuideOptions().show();
			
		} else {	// Start the program proper
			
			// Quick check - if the first channel file exists for today, go
			// to the viewer.  Otherwise, go to the downloader.

			// Get the channels
			Vector channels = config.getListValue("channels");
			
			// If we've got no channels listed
			if(channels==null || channels.size()==0) {
				
				// Send the user straight to the options screen again - 
				// no channels selected
				new FreeGuideOptions().show();
				
			} else { // All is still ok - we've got some channels
				
				SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
				String datestr = fmt.format(new Date());
				
				// Get the first channel name
				String tmp = (String)channels.get(0);
				
				// and get it in the right format
				tmp = tmp.toLowerCase().replace(' ', '_');
				tmp = makeRightFilenameLength(tmp);
				
				// Find the file it ought to be in
				File xmlFile = new File(config.getValue("freeguideDir")+"data/"+tmp+"-"+datestr+".fgd");
				
				if(xmlFile.exists()) {	// Check if it exists
				
					// If so, display the TV guide for today!
					new FreeGuideViewer().show();
					
				} else {	// If it doesn't exist
					
					// Go to the downloader
					new FreeGuideDownloader().show();
					
				}
				
			}
		
		}
		
    }

	/**
	 * Shortens a filename if necessary to make it shorter than
	 * maxFilenameLength
	 *
	 * @param name the string to be shortened
	 */
	public static String makeRightFilenameLength(String name) {
		
		int maxFilenameLength = Integer.parseInt(config.getValue("maxFilenameLength"));
		
		int hMFNL = (int)(maxFilenameLength/2);
		
		if(name.length()>maxFilenameLength) {
			name = name.substring(0, hMFNL-1) + "-" + name.substring(name.length()-(hMFNL-1), name.length());
		}
		
		return name;
		
	}
	
	/** execExternal
	 *
	 * Execute an external application via the command line interface.
	 *
	 * @param cmdstr the command to execute
	 */
	public static void execExternal(String cmdstr) {
	
		try {//IOException etc
			
			// Log what we're about to do
			log.writeLine("FreeGuide - Executing system command: "+cmdstr+" ...");
			
			// Execute the command
			Process pr = Runtime.getRuntime().exec(cmdstr);
			
			// Wait for it to finish
			pr.waitFor();
			
			// Log it finishing
			log.writeLine("Finished execution.");
	    
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}//try
	
    }//execExternal
	
	//------------------------------------------------------------------------
	
	public static JoveCmdArgs arguments;	// The command line args
	public static JoveConfigFile config;	// The config file
	public static JoveLogFile log;			// The log file
	
}
