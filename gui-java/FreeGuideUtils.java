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
 * parseCommand() method Copyright (c) by Slava Pestov
 *
 * from the Jedit project: www.jedit.org
 *
 */

import java.awt.Color;
import java.lang.reflect.Array;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;
import java.util.Calendar;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

/**
 * Some static global methods used in various parts of FreeGuide.
 *
 * @author  Andy Balaam
 * @version 2
 */

public class FreeGuideUtils {
	
	/**
	 * Execute an external command, without waiting for it to finish.
	 */
	public static void execNoWait(String[] cmds) {
		
		// Step through each command in the list
		for(int i=0;i<cmds.length;i++) {
			
			// Substitute in any system variables for this command
			String cmdstr = FreeGuide.prefs.performSubstitutions(cmds[i]);
			
			// Log what we're doing
			FreeGuide.log.info("Executing system command in background: "+cmdstr);
			
			try {
			
				// Parse the command into arguments and execute
				Runtime.getRuntime().exec(parseCommand(cmdstr));
				
			} catch(java.io.IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/** 
	 * Execute an external command and wait for it to finish, providing visual
	 * feedback to the user.
	 */
	public static void execAndWait(String[] cmds, String commandType, FreeGuideLauncher launcher) {
		// Show the command execution window
		launcher.setVisible(false);
		FreeGuideExecutor executor = new FreeGuideExecutor(launcher, cmds, commandType);
		executor.setVisible(true);
	}
	
	// parse
	/**
	 * Convert a command into an array of arguments.
	 *
	 * Adapted from a method written by Slava Pestov
	 * for the JEdit project www.jedit.org
	 *
	 * Thanks Slava!
	 */
	public static String[] parseCommand(String command)
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
loop:			while(true)
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
		catch(java.io.IOException io)
		{
			// won't happen
		}

		ans = new String[args.size()];
		args.copyInto(ans);
			
		return ans;
	}
	
	// --------------------------------------------------------------------
	
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
	 * Convert a Vector of Calendars to an array.
	 */
	public static Calendar[] arrayFromVector_Calendar(Vector vector) {
		Calendar[] ans = new Calendar[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Convert a Vector of Integers to an array of ints.
	 */
	public static int[] arrayFromVector_int(Vector vector) {
		int[] ans = new int[vector.size()];
		for(int i=0;i<ans.length;i++) {
			ans[i] = ((Integer)vector.get(i)).intValue();
		}
		return ans;
	}
	
	public static void fillComboBox(JComboBox box, String name) {
		
		if(name.equals("os")) {
			fillComboBoxOS(box);
		} else if(name.equals("country")) {
			fillComboBoxCountry(box);
		} else if(name.equals("browser_name")) {
			fillComboBoxBrowserName(box);
		}
		
	}
	
	/**
	 * Fill a combo box with possible types of OS.
	 */
	public static void fillComboBoxOS(javax.swing.JComboBox box) {
		
		box.addItem(os_Windows);
		box.addItem(os_Other);
		
	}
	
	/**
	 * Fill a combo box with possible countries.
	 */
	public static void fillComboBoxCountry(javax.swing.JComboBox box) {
		
		box.addItem(country_UK);
		box.addItem(country_NA);
		box.addItem(country_Germany);
		
	}
	
	/**
	 * Fill a combo box with possible Browsers.
	 */
	public static void fillComboBoxBrowserName(javax.swing.JComboBox box) {
		
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
	/*public static void setDefaultOptions(String os, String country, String browser) {
		
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
		
	}*/
	
	public static String getDefault(String name) {
		if(name.equals("os")) {
			return getDefaultOS();
		} else if(name.equals("country")) {
			return getDefaultCountry();
		} else if(name.equals("browser_name")) {
			return getDefaultBrowserName();
		} else if(name.equals("xmltv_directory")) {
			return getDefaultXMLTVCmdDir();
		} else if(name.equals("working_directory")) {
			return getDefaultWorkingDir();
		} else if(name.equals("tv_grab")) {
			return getDefaultGrabber();
		} else if(name.equals("browser")) {
			return getDefaultBrowser();
		} else if(name.equals("grabber_config")) {
			return getDefaultXMLTVCfg();
		} else {
			FreeGuide.log.warning("Unknown default requested.");
			return null;
		}
	}
	
	/**
	 * Returns a guess at the xmltv config file
	 */
	private static String getDefaultXMLTVCfg() {
		String os = FreeGuide.prefs.misc.get("os");
		String country = FreeGuide.prefs.misc.get("country");
		String lb = System.getProperty("line.separator");
		
		String ans = new String();
		
		if(os.equals(os_Windows)) {
			
			if(country.equals(country_UK)) {
				ans = "C:\\My Documents\\.xmltv\\tv_grab_uk";
			} else if(country.equals(country_NA)) {
				ans = "C:\\My Documents\\.xmltv\\tv_grab_na";
			} else if(country.equals(country_Germany)) {
				ans = "C:\\My Documents\\.xmltv\\tv_grab_de";
			} else {
				FreeGuide.log.warning("Invalid country chosen!");
				ans = "";
			}
			
		} else if(os.equals(os_Other)) {
			
			if(country.equals(country_UK)) {
				ans = "%home%/.xmltv/tv_grab_uk";
			} else if(country.equals(country_NA)) {
				ans = "%home%/.xmltv/tv_grab_na";
			} else if(country.equals(country_Germany)) {
				ans = "%home%/.xmltv/tv_grab_de";
			} else {
				FreeGuide.log.warning("Invalid country chosen!");
				ans = "";
			}

		} else {
			FreeGuide.log.warning("Invalid OS chosen!");
		}
		
		return ans;
	}
	
	/**
	 * Returns a guess at the browser command
	 */
	private static String getDefaultBrowser() {
		String os = FreeGuide.prefs.misc.get("os");
		String browser = FreeGuide.prefs.misc.get("browser");
		String xmltvDir = FreeGuide.prefs.misc.get("xmltv_directory");
		String workingDir = FreeGuide.prefs.misc.get("working_directory");
		String lb = System.getProperty("line.separator");
		
		String ans = new String();
		
		if(os.equals(os_Windows)) {
			
			if(browser.equals(browser_IE)) {
				ans = "\"C:\\Program Files\\Internet Explorer\\iexplore.exe\" \"%filename%\"";
			} else if(browser.equals(browser_NS)) {
				ans = "\"C:\\Program Files\\Netscape\\netscape.exe\" \"%filename%\"";
			} else if(browser.equals(browser_Mozilla)) {
				ans = "\"C:\\Program Files\\mozilla.org\\Mozilla\\mozilla.exe\" %filename%";
			} else if(browser.equals(browser_Opera)) {
				ans = "\"C:\\Program Files\\Opera\\opera.exe\" %filename%";
			} else {
				FreeGuide.log.warning("Invalid browser chosen!");
				ans = "";
			}
			
		} else if(os.equals(os_Other)) {
			
			if(browser.equals(browser_Galeon)) {
				ans = "galeon %filename%";
			} else if(browser.equals(browser_NS)) {
				ans = "netscape %filename%";
			} else if(browser.equals(browser_Mozilla)) {
				ans = "mozilla %filename%";
			} else if(browser.equals(browser_Konqueror)) {
				ans = "konqueror %filename%";
			} else if(browser.equals(browser_Opera)) {
				ans = "opera %filename%";
			} else {
				FreeGuide.log.warning("Invalid browser chosen!");
				ans = "";
			}

		} else {
			FreeGuide.log.warning("Invalid OS chosen!");
		}
		
		return ans;
		
	}
	
	/**
	 * Returns a guess at the xmltv grabber command
	 */
	private static String getDefaultGrabber() {
		String os = FreeGuide.prefs.misc.get("os");
		String country = FreeGuide.prefs.misc.get("country");
		String xmltvDir = FreeGuide.prefs.misc.get("xmltv_directory");
		String workingDir = FreeGuide.prefs.misc.get("working_directory");
		String lb = System.getProperty("line.separator");
		
		String ans = new String();
		
		if(os.equals(os_Windows)) {
			
			
			if(country.equals(country_UK)) {
				ans = "\"%misc.xmltv_directory%\\tv_grab_uk\" --output \"%misc.working_directory%\\listings_unprocessed.xml\"";
			} else if(country.equals(country_NA)) {
				ans = "\"%misc.xmltv_directory%\\tv_grab_na\" --output \"%misc.working_directory%\\listings_unprocessed.xml\"";
			} else if(country.equals(country_Germany)) {
				ans = "\"%misc.xmltv_directory%\\tv_grab_de\" --output \"%misc.working_directory%\\listings_unprocessed.xml\"";
			} else {
				FreeGuide.log.warning("Invalid country chosen!");
				ans = "";
			}
			ans += lb + "\"" + xmltvDir + "\\tv_split\" --output \"%misc.working_directory%\\%%channel-%%Y%%m%%d.fgd\" --day_start_time %misc.day_start_time% \"%misc.working_directory%\\listings_unprocessed.xml\"";
			
		} else if(os.equals(os_Other)) {
			
			if(country.equals(country_UK)) {
				ans = "tv_grab_uk --output %misc.working_directory%/listings_unprocessed.xml";
			} else if(country.equals(country_NA)) {
				ans = "tv_grab_na --output %misc.working_directory%/listings_unprocessed.xml";
			} else if(country.equals(country_Germany)) {
				ans = "tv_grab_de --output %misc.working_directory%/listings_unprocessed.xml";
			} else {
				FreeGuide.log.warning("Invalid country chosen!");
				ans = "";
			}
			ans += lb + "tv_split --output %misc.working_directory%/%%channel-%%Y%%m%%d.fgd --day_start_time %misc.day_start_time% %misc.working_directory%/listings_unprocessed.xml";

		} else {
			FreeGuide.log.warning("Invalid OS chosen!");
		}
		
		return ans;
	}
	
	/**
	 * Returns a guess at the working directory
	 */
	private static String getDefaultWorkingDir() {
		String os = FreeGuide.prefs.misc.get("os");
		if(os.equals(os_Windows)) {
			return "C:\\My Documents\\.xmltv\\freeguide-tv\\";
		} else {
			return "%home%/.xmltv/freeguide-tv/";
		}
	}
	
	/**
	 * Returns a guess at the xmltv directory
	 */
	private static String getDefaultXMLTVCmdDir() {
		String os = FreeGuide.prefs.misc.get("os");
		if(os.equals(os_Windows)) {
			return "C:\\Program Files\\xmltv\\";
		} else {
			return "/usr/bin/";
		}
	}
	
	/**
	 * Returns IE on Windows and Netscape for real men
	 */
	private static String getDefaultBrowserName() {
		String os = FreeGuide.prefs.misc.get("os");
		if(os.equals(os_Windows)) {
			return browser_IE;
		} else {
			return browser_NS;
		}
	}
	
	/**
	 * Returns UK
	 */
	private static String getDefaultCountry() {
		return country_UK;
	}
	
	/**
	 * Returns either "Windows" if we're on windows or "Linux" otherwise.
	 */
	private static String getDefaultOS() {
		String os = System.getProperty("os.name");
		if(os.startsWith("Windows")) {
			return os_Windows;
		} else {
			return os_Other;
		}
	}
	
	// -----------------------------------------------------------------------
	
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
