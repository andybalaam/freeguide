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

/*
 * parse() method Copyright (c) by Slava Pestov
 *
 * from the Jedit project: www.jedit.org
 *
 */

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StreamTokenizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.util.logging.Logger;

/**
 * The main class called to start FreeGuide.  Performs some
 * housekeeping before launching the viewer or downloader.
 *
 * Also contains some global methods and objects.
 *
 * @author  Andy Balaam
 * @version 5
 */
public class FreeGuide implements FreeGuideLauncher {

	/** 
	 * The constructor for a particular FreeGuide instance.
	 */
	public FreeGuide() {
	
		new FreeGuideViewer(this).show();
		
	}
	
    /**
	 * The method called when FreeGuide is run.  Processes
	 * command line arguments, sets up the preferences,
	 * makes a log file, checks there's a working directory,
	 * and starts the viewer.
	 *
     * @param args the command line arguments
     */
    public static void main (String args[]) {
		
		// Check various things and then begin
		FreeGuideStartupChecker.runChecks(args);
		
		// All is ok, so begin
		new FreeGuide();
		
	}
	
	

	/**
	 * Shortens a filename if necessary to make it shorter than
	 * maxFilenameLength
	 *
	 * @param name the string to be shortened
	 */
	/*public static String makeRightFilenameLength(String name) {
		
		int maxFilenameLength = Integer.parseInt(config.getValue("maxFilenameLength"));
		
		int hMFNL = (int)(maxFilenameLength/2);
		
		if(name.length()>maxFilenameLength) {
			name = name.substring(0, hMFNL-1) + "-" + name.substring(name.length()-(hMFNL-1), name.length());
		}
		
		return name;
		
	}*/
	
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
		
		// Tokenize this string properly, not the default way.
		String[] cmdarr = parse(cmdstr);
		
		try {//IOException etc
			
			// Log what we're about to do
			if(waitFor) {
				log.info("FreeGuide - Executing system command: "+cmdstr+" ...");
			} else {
				log.info("FreeGuide - Executing system command in background: "+cmdstr);
			}//if
			
			feedback.append("$ "+cmdstr+lb);
			
			int exitCode=0;
			
			// Execute the command
			Process pr = Runtime.getRuntime().exec(cmdarr);
			
			if(waitFor) {
						
				BufferedReader prErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				BufferedReader prOut = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			
				//InputStreamReader prErr = new InputStreamReader(pr.getErrorStream());
				//InputStreamReader prOut = new InputStreamReader(pr.getInputStream());
				
				// Wait for it to finish
				//pr.waitFor();
				// Above is no good in Windows!
				
				// Read from error stream
				char[] errLine = new char[1000];
				char[] stdLine = new char[1000];
				
				boolean running = true;
			
				// wait til thing ends or we run out of time
				while(running) {
				//while(line==null || !line.startsWith("Ended normally.") || secs>100) {
				
					// Wait a sec (literally)
					Thread.sleep(1000);
				
					try {
						exitCode = pr.exitValue();
						running=false;
					} catch(IllegalThreadStateException e) {
						// Do nothing because the thread is still running.
						// I know, I know, an exception in normal program
						// flow but it's the only way around Windows' not
						// halting when the process ends.
					}
					
					// Read from error stream
					prErr.read(errLine);
					prOut.read(stdLine);

					while(errLine!=null) {
						feedback.append("! "+errLine + lb);
						prErr.read(errLine);
					}
					while(stdLine!=null) {
						feedback.append(stdLine + lb);
						prOut.read(stdLine);
					}
					
				}//while
			
				// Kill it if windows hasn't
				pr.destroy();
				
				// Log it finishing
				FreeGuide.log.info("FreeGuide - Finished execution with exit code "+exitCode+".");
				
				if(exitCode==0) {
					feedback.append("Finished successfully." + lb);
				} else {
					feedback.append("Command terminated with error code "+exitCode+"." + lb);
				}
				
			}//if
	    
		} catch(IOException e) {
			e.printStackTrace();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}//try
	
    }//execExternal
	
	// parse
	/**
	 * Convert a command into a vector of arguments.
	 *
	 * Adapted from a method written by Slava Pestov
	 * for the Jedit project www.jedit.org
	 *
	 * Thanks Slava!
	 */
	private static String[] parse(String command)
	{
		Vector args = new Vector();
		String[] ans;

		// We replace \ with a non-printable char because
		// StreamTokenizer handles \ specially, which causes
		// problems on Windows as \ is the file separator
		// there.

		// After parsing is done, the non printable char is
		// changed to \ once again.

		// StreamTokenizer needs a way to disable backslash
		// handling...
		command = command.replace('\\',dosSlash);

		StreamTokenizer st = new StreamTokenizer(new StringReader(command));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		try
			{
loop:			for(;;)
				{
					switch(st.nextToken())
					{
					case StreamTokenizer.TT_EOF:
						break loop;
					case StreamTokenizer.TT_WORD:
					case '"':
					case '\'':
						args.addElement(st.sval.replace(dosSlash,'\\'));
					break;
				}
			}
		}
		catch(IOException io)
		{
			// won't happen
		}

		ans = new String[args.size()];
		args.copyInto(ans);
			
		return ans;
	}
	
	
	public void reShow() {
		
		die("I don't think it should ever get here!");
		
	}

	public static void die(String msg) {
		
		log.severe(msg);
		System.err.println(msg);
		System.exit(1);
		
	}
	
	public FreeGuideLauncher getLauncher() {
		return null;
	}
	
	//------------------------------------------------------------------------
	
	public static FreeGuideCmdArgs arguments;	// The command line args
	public static FreeGuidePreferences prefs;	// Holds all preferences info
	public static Logger log;					// The log file
	
	public static final String version = "0.3";
	
	private static final char dosSlash = 127;
	
}
