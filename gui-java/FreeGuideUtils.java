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

import java.awt.Color;
import java.lang.reflect.Array;
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
 * Some static global methods used in various parts of FreeGuide.
 *
 * @author  Andy Balaam
 * @version 2
 */

public class FreeGuideUtils {
	
	/** execExternal
	 *
	 * Execute several external applications via the command line interface,
	 * and wait until they have finished executing before returning.
	 *
	 * @param cmdstr  the commands to execute, one per entry in the array
	 */
	public static void execExternal(String[] cmds, JTextArea feedback) {
		execExternal(cmds, true, feedback);
	}
	public static void execExternal(String[] cmds, boolean waitFor, JTextArea feedback) {
		
		for(int i=0;i<cmds.length;i++) {
			
			execExternal(FreeGuide.prefs.performSubstitutions(cmds[i]), waitFor, feedback);
			
		}
		
	}
	
	
	/** execExternal
	 *
	 * Execute an external application via the command line interface and
	 * wait for it to end.
	 *
	 * @param cmdstr  the command to execute
	 */
	private static void execExternal(String cmdstr, JTextArea feedback) {
		execExternal(cmdstr, true, feedback);
	}
	
	/** execExternal
	 *
	 * Execute an external application via the command line interface.
	 *
	 * @param cmdstr  the command to execute
	 * @param waitFor true wait for this command to end before continuing?
	 */
	private static void execExternal(String cmdstr, boolean waitFor, JTextArea feedback) {
	
		if(cmdstr==null || cmdstr.equals("")) {
			return;
		}
		
		String lb = System.getProperty("line.separator");
		
		// Tokenize this string properly, not the default way.
		String[] cmdarr = parse(cmdstr);
		
		try {//IOException etc
			
			// Log what we're about to do
			if(waitFor) {
				FreeGuide.log.info("FreeGuide - Executing system command: "+cmdstr+" ...");
			} else {
				FreeGuide.log.info("FreeGuide - Executing system command in background: "+cmdstr);
			}//if
			
			feedback.append("$ "+cmdstr+lb);
			
			int exitCode=0;
			
			// Execute the command
			Process pr = Runtime.getRuntime().exec(cmdarr);
			
			if(waitFor) {
						
				BufferedReader prErr = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
				BufferedReader prOut = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				
				String stdLine;
				String errLine;
				boolean x = false;
				while(!x) {
					stdLine = prOut.readLine();
					errLine = prErr.readLine();
					x = true;
					if(stdLine!=null) {
						x=false;
						feedback.append(stdLine + lb);
					}
					if(errLine!=null) {
						x=false;
						feedback.append("! " + errLine + lb);
					}
					
				}
			
				// We have automatically waited for the command to end by
				// capturing its output.  This is fortunate as Windows waits
				// forever if you call Process.waitFor()
				
				// Kill it if windows hasn't
				//pr.destroy();
				
				exitCode = pr.exitValue();
				
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
		//} catch(InterruptedException e) {
		//	e.printStackTrace();
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
	
	/**
	 * In each of the string in str, replace any occurences of oldStr with
	 * newStr.
	 */
	public static String[] substitute(String[] str, String oldStr, String newStr) {
		
		String[] ans = new String[str.length];
		
		// Go through each string we're processing
		for(int i=0;i<str.length;i++) {
			
			// Copy it into the output array
			ans[i] = str[i];
			
			// Find the first occurence of the string to be replaced
			int j = ans[i].indexOf(oldStr);
			int k;
			
			// Keep replacing until there are no more.
			while(j!=-1) {
				
				k = j + oldStr.length();
				if(k<ans[i].length()) {
					ans[i] = ans[i].substring(0, j) + newStr + ans[i].substring(k);
				} else {
					ans[i] = ans[i].substring(0, j) + newStr;
				}
				j = ans[i].indexOf(oldStr);
				
			}
			
		}
		
		return ans;
		
	}
	
	/**
	 * Convert a Vector of FreeGuideProgrammes to an array.
	 */
	public static FreeGuideProgramme[] arrayFromVector_FreeGuideProgramme(Vector vector) {
		FreeGuideProgramme[] ans = new FreeGuideProgramme[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Convert a Vector of FreeGuideFavourites to an array.
	 */
	public static FreeGuideFavourite[] arrayFromVector_FreeGuideFavourite(Vector vector) {
		FreeGuideFavourite[] ans = new FreeGuideFavourite[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Convert a Vector of Strings to an array.
	 */
	public static String[] arrayFromVector_String(Vector vector) {
		String[] ans = new String[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Fill a combo box with possible types of OS.
	 */
	public static void addOSsToComboBox(javax.swing.JComboBox box) {
		
		box.addItem(os_Windows);
		box.addItem(os_Other);
		
	}
	
	/**
	 * Fill a combo box with possible countries.
	 */
	public static void addCountriesToComboBox(javax.swing.JComboBox box) {
		
		box.addItem(country_UK);
		box.addItem(country_NA);
		box.addItem(country_Germany);
		
	}
	
	/**
	 * Fill a combo box with possible Browsers.
	 */
	public static void addBrowsersToComboBox(javax.swing.JComboBox box) {
		
		box.addItem(browser_IE);
		box.addItem(browser_NS);
		box.addItem(browser_Opera);
		box.addItem(browser_Mozilla);
		box.addItem(browser_Galeon);
		box.addItem(browser_Konqueror);
		
	}
	
	/**
	 * Set up the default options according to the OS, country and browser
	 * passed in.  Fill in the boxes on the given options screen, rather than
	 * setting them directly.
	 */
	public static void setDefaultOptions(String os, String country, String browser) {
		
		String lb = System.getProperty("line.separator");
		
		FreeGuidePreferences misc = FreeGuide.prefs.misc;
		FreeGuidePreferences screen = FreeGuide.prefs.screen;
		FreeGuidePreferences commandline = FreeGuide.prefs.commandline;

		misc.put("working_directory", "%home%/.xmltv/freeguide-tv");
		misc.put("css_file", "%misc.working_directory%/guide.css");

		FreeGuide.prefs.screen.putColor("programme_normal_colour", Color.white);
		FreeGuide.prefs.screen.putColor("programme_chosen_colour", new Color(220,220,220));
		FreeGuide.prefs.screen.putColor("channel_colour", new Color(245,245,255));
		FreeGuide.prefs.screen.putInt("channel_height", 28);
		FreeGuide.prefs.screen.putInt("vertical_gap", 1);
		FreeGuide.prefs.screen.putInt("horizontal_gap", 1);
		FreeGuide.prefs.screen.putInt("panel_width", 6000);
		FreeGuide.prefs.screen.putInt("channel_panel_width", 600);
		
		FreeGuide.prefs.misc.putFreeGuideTime("day_start_time", new FreeGuideTime(06,00));
			
		if(country.equals(country_UK)) {
			
			misc.put("grabber_config", "%home%/.xmltv/tv_grab_uk");
			
		} else if(country.equals(country_NA)) {
			
			misc.put("grabber_config", "%home%/.xmltv/tv_grab_na");
			
		} else if(country.equals(country_Germany)) {
			
			misc.put("grabber_config", null);
			
		} else {
			FreeGuide.log.warning("Invalid country chosen!");
		}
		
		if(os.equals(os_Windows)) {
			
			String[] tmp = new String[2];
			if(country.equals(country_UK)) {
				tmp[0] = "\"%misc.xmltv_directory%\\tv_grab_uk\" --output \"%misc.working_directory%/listings_unprocessed.xml\"";
			} else if(country.equals(country_NA)) {
				tmp[0] = "\"%misc.xmltv_directory%\\tv_grab_na\" --output \"%misc.working_directory%/listings_unprocessed.xml\"";
			} else if(country.equals(country_Germany)) {
				tmp[0] = "\"%misc.xmltv_directory%\\tv_grab_de\" --output \"%misc.working_directory%/listings_unprocessed.xml\"";
			} else {
				tmp[0] = "";
			}
			tmp[1] = "\"%misc.xmltv_directory%\\tv_split\" --output \"%misc.working_directory%\\%%channel-%%Y%%m%%d.fgd\" --day_start_time %misc.day_start_time% \"%misc.working_directory%/listings_unprocessed.xml\"";
			commandline.putStrings("tv_grab", tmp);
			
			misc.put("xmltv_directory", "C:/Program Files/xmltv");
			
			tmp = new String[1];
			if(browser.equals(browser_IE)) {
				tmp[0] = "\"C:\\Program Files\\Internet Explorer\\iexplore.exe\" \"%filename%\"";
			} else if(browser.equals(browser_NS)) {
				tmp[0] = "\"C:\\Program Files\\Netscape\\netscape.exe\" \"%filename%\"";
			} else if(browser.equals(browser_Mozilla)) {
				tmp[0] = "\"C:\\Program Files\\mozilla.org\\Mozilla\\mozilla.exe\" %filename%";
			} else if(browser.equals(browser_Opera)) {
				tmp[0] = "\"C:\\Program Files\\Opera\\opera.exe\" %filename%";
			} else {
				FreeGuide.log.warning("Invalid browser chosen!");
				tmp[0] = "";
			}
			commandline.putStrings("browser_command", tmp);
			
		} else if(os.equals(os_Other)) {
			
			String[] tmp = new String[2];
			if(country.equals(country_UK)) {
				tmp[0] = "tv_grab_uk --output %misc.working_directory%/listings_unprocessed.xml";
			} else if(country.equals(country_NA)) {
				tmp[0] = "tv_grab_na --output %misc.working_directory%/listings_unprocessed.xml";
			} else if(country.equals(country_Germany)) {
				tmp[0] = "tv_grab_de --output %misc.working_directory%/listings_unprocessed.xml";
			} else {
				tmp[0] = "";
			}
			tmp[1] = "tv_split --output %misc.working_directory%/%%channel-%%Y%%m%%d.fgd --day_start_time %misc.day_start_time% %misc.working_directory%/listings_unprocessed.xml";
			commandline.putStrings("tv_grab", tmp);
			
			misc.put("xmltv_directory", "/usr/bin");
			
			tmp = new String[1];
			if(browser.equals(browser_Galeon)) {
				tmp[0] = "galeon %filename%";
			} else if(browser.equals(browser_NS)) {
				tmp[0] = "netscape %filename%";
			} else if(browser.equals(browser_Mozilla)) {
				tmp[0] = "mozilla %filename%";
			} else if(browser.equals(browser_Konqueror)) {
				tmp[0] = "konqueror %filename%";
			} else if(browser.equals(browser_Opera)) {
				tmp[0] = "opera %filename%";
			} else {
				FreeGuide.log.warning("Invalid browser chosen!");
				tmp[0] = "";
			}
			commandline.putStrings("browser_command", tmp);
			
		} else {
			FreeGuide.log.warning("Invalid OS chosen!");
		}
		
	}
	
	private static final String os_Windows = "Windows";
	private static final String os_Other = "Linux/Unix/Other";
	
	private static final String country_UK = "UK";
	private static final String country_NA = "North America";
	private static final String country_Germany = "Germany";
	
	private static final String browser_IE = "MS Internet Explorer";
	private static final String browser_NS = "Netscape";
	private static final String browser_Opera = "Opera";
	private static final String browser_Mozilla = "Mozilla";
	private static final String browser_Galeon = "Galeon";
	private static final String browser_Konqueror = "Konqueror";
	
	private static final char dosSlash = 127;
	
}
