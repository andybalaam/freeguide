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


import java.io.File;
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

/**
 * The main class called to start FreeGuide.  Performs some
 * housekeeping before launching the viewer or downloader.
 *
 * @author  Andy Balaam
 * @version 4
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
			"channelsFile=$$workingDirectory$$/data/channels.xml",
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
		
		// Tokenize this string properly, not the default way.
		String[] cmdarr = parse(cmdstr);
		
		/*for(int i=0;i<cmdarr.length;i++) {
			log.writeLine(cmdarr[i]);
		}*/
		
		try {//IOException etc
			
			// Log what we're about to do
			if(waitFor) {
				log.writeLine("FreeGuide - Executing system command: "+cmdstr+" ...");
			} else {
				log.writeLine("FreeGuide - Executing system command in background: "+cmdstr);
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
				String errLine;
				String stdLine;
				
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
						// flow but it's the oly way around Windows' not
						// halting when the process ends.
					}
					
					// Read from error stream
					errLine = prErr.readLine();
					stdLine = prOut.readLine();

					while(errLine!=null) {
						log.writeLine("Freeguide - command execution error: "+errLine);
						feedback.append("! "+errLine + lb);
						errLine = prErr.readLine();
					}
					while(stdLine!=null) {
						log.writeLine("Freeguide - command execution output: "+stdLine);
						feedback.append(stdLine + lb);
						stdLine = prOut.readLine();
					}
					
				}//while
			
				// Kill it if windows hasn't
				pr.destroy();
				
				// Log it finishing
				log.writeLine("FreeGuide - Finished execution with exit code "+exitCode+".");
				
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
		
		/*
		for(int i=0;i<args.size();i++) {
			ans[i] = (String)args.get(i);
		}*/
			
		return ans;
	}


	
	/**
	 * tokenize
	 * (sic)
	 *
	 * Split the string into an array of strings separated by
	 * spaces, except where preceded by \'s or quoted out.
	 *
	 * e.g. this is\ a "nice example" returns
	 *
	 * this
	 * is a
	 * nice example
	 *
	 * @param input the String to be split up
	 * @returns an array of Strings, one for each word (token) in input
	 *
	 */
	/*public static String[] tokenize(String input) {
		
		Vector tmpans = new Vector();
		
		StreamTokenizer tk = new StreamTokenizer(new StringReader(input));
		
		tk.wordChars('-', '-');
		
		
		try {//IOException
			int t = tk.nextToken();
			while(t!=StreamTokenizer.TT_EOF) {
			
				if(tk.sval!=null) {
					tmpans.add(tk.sval);
					log.writeLine((String)tmpans.lastElement());
				}
				
				t = tk.nextToken();
			
			}//while
		} catch(IOException e) {
			e.printStackTrace();
		}//try
		
		String[] ans = new String[tmpans.size()];
		for(int i=0;i<ans.length;i++) {
			ans[i] = (String)tmpans.get(i);
		}
		
		return ans;
		
	}*/
	
	/**
	 * noSpaces
	 *
	 * Slash out spaces and dashes for filenames etc.
	 *
	 * e.g. "Channel 4 - Wales (Digital)" becomes
	 * "Channel\ 4\ \-\ Wales\ (Digital)"
	 *
	 * @param input the String to be processed
	 * @returns input with all difficult characters replaced by \char
	 */
	/*public static String noSpaces(String input) {
		
		return slashChar('-', slashChar(' ', input));
		
	}//noSpaces*/
	
	/**
	 * slashChar
	 *
	 * Replaces every instance of ch with \ch
	 *
	 * @param ch    the character to replace
	 * @param input the String to be processed
	 * @returns     input with all chs replaced by \ch
	 */
	/*private static String slashChar(char ch, String input) {
		
		String ans = new String(input);
		
		int i = ans.indexOf(ch);
		while(i>-1) {
			ans = ans.substring(0, i) + "\\" + ch + ans.substring(i+1);
			i = ans.indexOf(ch, i+2);
		}
		
		return ans;
		
	}//slashChar*/
	
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
	
	private static final char dosSlash = 127;
	
}
