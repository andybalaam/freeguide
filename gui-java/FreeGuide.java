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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

/**
 * The main class called to start FreeGuide.  Performs some
 * housekeeping before launching the viewer or downloader.
 *
 * @author  Andy Balaam
 * @version 3
 */
public class FreeGuide implements FreeGuideLauncher {

	/** 
	 * The constructor for a particular FreeGuide instance.
	 */
	public FreeGuide() {
	
		construct();
		
	}
	
	/**
	 * Start the viewer unless we are not properly set up, in which case, 
	 * go to the options or downloader screen.
	 */
	private void construct() {
		
		// If we've not got a proper config file set up
		if(config.getValue("workingDirectory")==null) {
			
			// Send the user straight to the options screen
			new FreeGuideOptions(this).show();
			
		} else {	// Start the program proper
			
			// Quick check - if the first channel file exists for today, go
			// to the viewer.  Otherwise, go to the downloader.

			// Get the channels
			//Vector channels = config.getListValue("channels");
			
			// If we've got no channels listed
			//if(channels==null || channels.size()==0) {
				
				// Send the user straight to the options screen again - 
				// no channels selected
			//	new FreeGuideOptions(this).show();
				
			//} else { // All is still ok - we've got some channels
				
			//SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
			//String datestr = fmt.format(new Date());
				
			// Get the first channel name
			//String tmp = (String)channels.get(0);
				
			// and get it in the right format
			//tmp = tmp.toLowerCase().replace(' ', '_');
			//tmp = makeRightFilenameLength(tmp);
				
			// Find the file it ought to be in
			//File xmlFile = new File(config.getValue("workingDirectory")+"data/"+tmp+"-"+datestr+".fgd");
				
			//if(xmlFile.exists()) {	// Check if it exists
				
				// If so, display the TV guide for today!
				
					
			//} else {	// If it doesn't exist
				
				// Create (working and) data directory(ies) if needed
				File dataDir = new File(config.getValue("workingDirectory")+"/data/");
				
				if(!dataDir.exists()){
					dataDir.mkdirs();
				}
				
				// Go to the downloader
				//new FreeGuideDownloader(this).show();
				
				new FreeGuideViewer(this).show();
				
			//}
				
			//}
		
		}
		
		
	}
	
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
		String[] vitals = {
			"logFile=$$workingDirectory$$/log.txt",
//			"channelsFile=channels.txt",
//			"downloadAmount=Day",
			"browserCommandLine=netscape",
			"cssFile=$$freeguideDirectory$$/tvguide.css",
			"maxFilenameLength=16",
			"channelHeight=28",
			"verticalGap=1",
			"horizontalGap=1",
			"panelWidth=6000"
		};
		
		String cfgFilename = arguments.getValue("config-file");
		
		if(cfgFilename==null) {
			cfgFilename=System.getProperty("user.home")+"/.freeguide-tv/freeguiderc.txt";
		}
		
		config = new JoveConfigFile(vitals, cfgFilename);
		
		// Open the log file
		log = new JoveLogFile(config.getValue("logFile"), true);
	
		new FreeGuide();
		
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
	 * Execute an external application via the command line interface and
	 * wait for it to end.
	 *
	 * @param cmdstr  the command to execute
	 */
	public static void execExternal(String cmdstr, JTextArea feedback) {
		execExternal(cmdstr, true, feedback);
	}
	
	/** execExternal
	 *
	 * Execute an external application via the command line interface.
	 *
	 * @param cmdstr  the command to execute
	 * @param waitFor true wait for this command to end before continuing?
	 */
	public static void execExternal(String cmdstr, boolean waitFor, JTextArea feedback) {
	
		String lb = System.getProperty("line.separator");
		
		try {//IOException etc
			
			// Log what we're about to do
			if(waitFor) {
				log.writeLine("FreeGuide - Executing system command: "+cmdstr+" ...");
			} else {
				log.writeLine("FreeGuide - Executing system command in background: "+cmdstr);
			}//if
			
			feedback.append(cmdstr+lb);
			
			// Execute the command
			Process pr = Runtime.getRuntime().exec(cmdstr);
			
			if(waitFor) {
						
				BufferedReader prErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				BufferedReader prOut = new BufferedReader(new InputStreamReader(pr.getOutputStream()));
			
				// Wait for it to finish
				//pr.waitFor();
				// Above is no good in Windows!
				
				// Read from error stream
				String line = prErr.readLine();
			
				// initialise a timer
				int secs=0;
				
				boolean running = true;
			
				// wait til thing ends or we run out of time
				while(running) {
				//while(line==null || !line.startsWith("Ended normally.") || secs>100) {
				
					// Wait a sec (literally)
					Thread.sleep(1000);
				
					// Read from error stream
					line = prErr.readLine();
				
					// increment timer
					secs++;
				}//while
			
				pr.destroy();
				
				if(!line.startsWith("Ended normally.")) {
					log.writeLine("FreeGuide - execution error - "+line);
					
				}
			
				if(secs>100) {
					log.writeLine("FreeGuide - execution timed out.");
				}
				
				// Log it finishing
				log.writeLine("FreeGuide - Finished execution.");
				
			}//if
	    
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}//try
	
    }//execExternal
	
	public void reShow() {
		
		Object[] options = { "View", "Quit" };
		int retval = JOptionPane.showOptionDialog(null, "Do you want to view programme listings or quit?", "Quit?", 0, JOptionPane.QUESTION_MESSAGE, null, options, "View" );
		
		if(retval==0) {
		
			// We had to go to a config screen: start again now that's done
			construct();
			
		} else {
			
			System.exit(0);
			
		}
		
	}
	
	//------------------------------------------------------------------------
	
	public static JoveCmdArgs arguments;	// The command line args
	public static JoveConfigFile config;	// The config file
	public static JoveLogFile log;			// The log file
	
}
