/**
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
import java.io.FileReader;
 
/**
 * A static class to guess various configuration options.
 * Very brute force.
 *
 * @author  Andy Balaam
 * @version 1
 */

public class FreeGuideConfigGuesser {
	
	public static String[] guessMessages(String group, String entry) {
		
		String[] ans = new String[2];
		
		switch(prefToInt(group, entry)) {
			
		case BROWSER_COMMAND:
			ans[0] = "Enter the command to launch your browser";
			ans[1] = "";
			break;
		case TV_GRAB:
			ans[0] = "Enter the commands to grab and split the listings";
			ans[1] = "";
			break;
		case BROWSER_NAME:
			ans[0] = "Choose your web browser";
			ans[1] = "";
			break;
		case COUNTRY:
			ans[0] = "Choose your region";
			ans[1] = "";
			break;
		case DAY_START_TIME:
			ans[0] = "Choose the time that a new day starts";
			ans[1] = "";
			break;
		case DAYS_TO_GRAB:
			ans[0] = "Choose number of days to download every time";
			ans[1] = "";
			break;
		case GRABBER_CONFIG:
			ans[0] = "Give the location of the grabber's config file";
			ans[1] = "";
			break;
		case INSTALL_DIRECTORY:
			ans[0] = "What directory would you like to install FreeGuide into?";
			ans[1] = "";
			break;
		case OS:
			ans[0] = "Choose your operating system";
			ans[1] = "";
			break;
		case WORKING_DIRECTORY:
			ans[0] = "Choose a working directory to use";
			ans[1] = "";
			break;
		case XMLTV_DIRECTORY:
			ans[0] = "Locate the install directory of the XMLTV tools";
			ans[1] = "This must contain the xmltv executable(s)";
			break;
			
		}
		
		return ans;
		
	}
	
	public static int guessType(String group, String entry) {
		
		switch(prefToInt(group, entry)) {
			
		case BROWSER_COMMAND:
			return COMMANDS_TYPE;
		case TV_GRAB:
			return COMMANDS_TYPE;
		case BROWSER_NAME:
			return CHOICE_TYPE;
		case COUNTRY:
			return CHOICE_TYPE;
		case DAY_START_TIME:
			return TEXT_TYPE;
		case GRABBER_CONFIG:
			return FILE_TYPE;
		case INSTALL_DIRECTORY:
			return DIRECTORY_TYPE;
		case OS:
			return CHOICE_TYPE;
		case WORKING_DIRECTORY:
			return DIRECTORY_TYPE;
		case XMLTV_DIRECTORY:
			return DIRECTORY_TYPE;
		default:
			return -1;
		}

	}
	
	public static String[] guessChoices(String group, String entry) {
		
		String[] ans;
		
		switch(prefToInt(group, entry)) {
			
			case BROWSER_NAME:
				ans = new String[6];
				ans[0] = browser_IE;
				ans[1] = browser_NS;
				ans[2] = browser_Opera;
				ans[3] = browser_Mozilla;
				ans[4] = browser_Galeon;
				ans[5] = browser_Konqueror;
				break;
			
			case COUNTRY:	// Just returns North America even though I'm English!
				ans = new String[3];
				ans[0] = country_UK;
				ans[1] = country_NA;
				ans[2] = country_Germany;
				break;
			
			case OS:	// Returns either "Windows" if we're on windows or "Linux" otherwise.
				ans = new String[2];
				ans[0] = os_Windows;
				ans[1] = os_Other;
				break;
				
			default:
				ans = new String[0];
		}

		return ans;
		
	}
	
	/**
	 * Guesses the value that should go in a config value.
	 * Remember it's only a guess - help me fix it if it could be better.
	 */
	public static Object guess(String group, String entry) {
		
		FreeGuidePreferencesGroup prefs = new FreeGuidePreferencesGroup();
		String os;
		
		switch(prefToInt(group, entry)) {
		case BROWSER_COMMAND:
			return guessBrowserCommand(prefs);
			
			
		case TV_GRAB:
			return guessTvGrab(prefs);
			
			
		case BROWSER_NAME:	// Returns IE on Windows and Netscape for real men
			os = prefs.misc.get("os");
			if(os.equals(os_Windows)) {
				return browser_IE;
			} else {
				return browser_NS;
			}

			
		case COUNTRY:	// Just returns North America even though I'm English!
			return country_NA;
			
			
		case DAY_START_TIME:
			return "06:00";

		case DAYS_TO_GRAB:
			return "7";
			
		case GRABBER_CONFIG:
			return new File(guessGrabberConfig(prefs));

			
			
		case INSTALL_DIRECTORY:
			os = prefs.misc.get("os");
			String home = System.getProperty("user.home");
			
			if(os.equals(os_Windows)) {
				return new File("C:\\Program Files\\freeguide-tv\\");
			} else {
				return new File(home + fs + "freeguide-tv" + fs);
			}

			
			
		case OS:	// Returns either "Windows" if we're on windows or "Linux" otherwise.
			os = System.getProperty("os.name");
			if(os.startsWith("Windows")) {
				return os_Windows;
			} else {
				return os_Other;
			}

			
			
		case WORKING_DIRECTORY:
			return new File(prefs.misc.get("install_directory") + fs + "data" + fs);

			
			
		case XMLTV_DIRECTORY:	// Returns a subdir of the installdir on Windows, or /usr/bin
			os = prefs.misc.get("os");
			if(os.equals(os_Windows)) {
				return new File(prefs.misc.get("install_directory") + fs  + "xmltv" + fs);
			} else {
				return new File("/usr/bin/");
			}

			
		default:
			return null;
		}
		
	}
	
	/**
	 * Guesses what the tv_grab command should be.
	 */
	private static String[] guessTvGrab(FreeGuidePreferencesGroup prefs) {
	
		String os = prefs.misc.get("os");
		String country = prefs.misc.get("country");
		String xmltv_directory = prefs.misc.get("xmltv_directory");
		String working_directory = prefs.misc.get("working_directory");
		String grabber_config = prefs.misc.get("grabber_config");
		String day_start_time = prefs.misc.get("day_start_time");

		String[] ans = new String[2];
		
		if(os.equals(os_Windows)) {
			
			if(country.equals(country_UK)) {
				ans[0] = "\"" + xmltv_directory + fs + "tv_grab_uk.exe\" --config-file \"" + grabber_config + "\" --output \"" + working_directory + fs +"listings_unprocessed.xml\"";
			} else if(country.equals(country_NA)) {
				ans[0] = "\"" + xmltv_directory + fs + "tv_grab_na.exe\" --config-file \"" + grabber_config + "\" --output \"" + working_directory + fs +"listings_unprocessed.xml\"";
			} else if(country.equals(country_Germany)) {
				ans[0] = "\"" + xmltv_directory + fs + "tv_grab_de.exe\" --config-file \"" + grabber_config + "\" --output \"" + working_directory + fs +"listings_unprocessed.xml\"";
			}
			ans[1] = "\"" + xmltv_directory + fs + "xmltv.exe\" --output \"" + working_directory + fs + "%%channel-%%Y%%m%%d.fgd\" --day_start_time 00:00 \"" + working_directory + fs + "listings_unprocessed.xml\"";
			
		} else if(os.equals(os_Other)) {
			
			if(country.equals(country_UK)) {
				ans[0] = "tv_grab_uk --config-file " + grabber_config + " --output " + working_directory + fs + "listings_unprocessed.xml";
			} else if(country.equals(country_NA)) {
				ans[0] = "tv_grab_na --config-file " + grabber_config + " --output " + working_directory + fs + "listings_unprocessed.xml";
			} else if(country.equals(country_Germany)) {
				ans[0] = "tv_grab_de --config-file " + grabber_config + " --output " + working_directory + fs + "listings_unprocessed.xml";
			}
			ans[1] = "tv_split --output " + working_directory + fs + "%%channel-%%Y%%m%%d.fgd --day_start_time 00:00 " + working_directory + fs + "listings_unprocessed.xml";
		
		}
		return ans;
	}
	
	/**
	 * Guesses what the browser command should be.
	 */
	private static String[] guessBrowserCommand(FreeGuidePreferencesGroup prefs) {
		
		String os = prefs.misc.get("os");
		String browser = prefs.misc.get("browser_name");
		
		String[] ans = new String[1];
		
		if(os.equals(os_Windows)) {
			
			if(browser.equals(browser_IE)) {
				ans[0] = "\"C:\\Program Files\\Internet Explorer\\iexplore.exe\" \"%filename%\"";
			} else if(browser.equals(browser_NS)) {
				ans[0] = "\"C:\\Program Files\\Netscape\\netscape.exe\" \"%filename%\"";
			} else if(browser.equals(browser_Mozilla)) {
				ans[0] = "\"C:\\Program Files\\mozilla.org\\Mozilla\\mozilla.exe\" %filename%";
			} else if(browser.equals(browser_Opera)) {
				ans[0] = "\"C:\\Program Files\\Opera\\opera.exe\" %filename%";
			}
			
		} else if(os.equals(os_Other)) {
			
			if(browser.equals(browser_Galeon)) {
				ans[0] = "galeon %filename%";
			} else if(browser.equals(browser_NS)) {
				ans[0] = "netscape %filename%";
			} else if(browser.equals(browser_Mozilla)) {
				ans[0] = "mozilla %filename%";
			} else if(browser.equals(browser_Konqueror)) {
				ans[0] = "konqueror %filename%";
			} else if(browser.equals(browser_Opera)) {
				ans[0] = "opera %filename%";
			}

		}
		
		return ans;
		
	}
	
	/**
	 * Guess what the grabber config file should be
	 */
	private static String guessGrabberConfig(FreeGuidePreferencesGroup prefs) {
		String os = prefs.misc.get("os");
		String country = prefs.misc.get("country");
		String xmltv_directory = prefs.misc.get("xmltv_directory");
		String home = System.getProperty("user.home");
		
		if(os.equals(os_Windows)) {
			if(country.equals(country_NA)) {
				return xmltv_directory + "tv_grab_na";
			} else if(country.equals(country_UK)) {
				return xmltv_directory + "tv_grab_uk";
			} else if(country.equals(country_Germany)) {
				return xmltv_directory + "tv_grab_de";
			}
		} else {
			if(country.equals(country_NA)) {
				return home + fs + ".xmltv" + fs + "tv_grab_na";
			} else if(country.equals(country_UK)) {
				return home + fs + ".xmltv" + fs + "tv_grab_uk";
			} else if(country.equals(country_Germany)) {
				return home + fs + ".xmltv" + fs + "tv_grab_de";
			}
		}
		
		return null;
	}
	
	// -----------------------------
	
	private static int prefToInt(String group, String entry) {
		
		if(group.equals("commandline")) {
			if(entry.equals("browser_command")) {
				return BROWSER_COMMAND;
			} else if(entry.equals("tv_grab")) {
				return TV_GRAB;
			}
		} else if(group.equals("misc")) {
			if(entry.equals("browser_name")) {
				return BROWSER_NAME;
			} else if(entry.equals("country")) {
				return COUNTRY;
			} else if(entry.equals("day_start_time")) {
				return DAY_START_TIME;
			} else if(entry.equals("days_to_grab")) {
				return DAYS_TO_GRAB;
			} else if(entry.equals("grabber_config")) {
				return GRABBER_CONFIG;
			} else if(entry.equals("install_directory")) {
				return INSTALL_DIRECTORY;
			} else if(entry.equals("os")) {
				return OS;
			} else if(entry.equals("working_directory")) {
				return WORKING_DIRECTORY;
			} else if(entry.equals("xmltv_directory")) {
				return XMLTV_DIRECTORY;
			}
		}
		
		System.out.println("Unknown option asked for!");
		return -1;
		
	}
	
	// -------------------------------------------
	
	public static String checkValue(String group, String entry, Object val) {
		
		switch(prefToInt(group, entry)) {
			
			case DAY_START_TIME:
				return checkValidTime((String)val);
			
			case GRABBER_CONFIG:
				return checkXMLTVConfigFile((File)val);

			case INSTALL_DIRECTORY:
				return checkDirWriteable((File)val, "installation");

			case WORKING_DIRECTORY:
				return checkDirWriteable((File)val, "a working directory");

			case XMLTV_DIRECTORY:
				return checkXMLTVDir((File)val);

			default:	// E.g. a country or a grabber command, just say it's right 
				return null;

		}
		
	}
	
	private static String checkValidTime(String time) {
		if(time.length()==5 && time.charAt(2)==':') {
			return null;
		}
		return "The time you gave for the start of days is not in valid hh:mm format.";
	}
	
	private static String checkDirWriteable(File dir, String whatFor) {
		
		try {
		
			if(!dir.exists()) {
				dir.mkdirs();
			}
		
			// Check it exists and you can write a file to it
			File testFile = new File(dir.getPath() + "test.tmp");
			testFile.createNewFile();
			if(dir.exists() && testFile.canWrite()) {
				
				testFile.delete();
				return null;
				
			}
			testFile.delete();
			return "The directory you chose for " + whatFor + " can't be created or isn't writeable.";
			
		} catch(java.io.IOException e) {
			
			return "There was an error trying to create the directory you chose for " + whatFor + ".";
			
		}
	}
	
	private static String checkXMLTVConfigFile(File grabber_config) {
		
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
						return null;
					}
				}
				
				buffy.close();
				
			} catch (java.io.IOException e) {
				e.printStackTrace();
				// FIXME - error dialog!
			}

		}
		
		// Something went wrong: either file doesn't exist or it has no
		// channel entries
		return "The chosen channels file either doesn't exist or contains no channels.";
		
	}
	
	/** 
	 * Find whether a proposed XMLTV directory is correct.  Checks this
	 * by looking for the files xmltv.exe or tv_grab* in it.
	 */
	private static String checkXMLTVDir(File dir) {
		
		// Check the dir exists
		if(dir.exists()) {

			File[] tmp = dir.listFiles(new FreeGuideFilenameFilter("xmltv\\.exe", false));
			File[] tmp2 = dir.listFiles(new FreeGuideFilenameFilter("^tv_grab.*", false));
			
			// Check a file xmltv.exe exists in this dir
			if( tmp.length > 0  || tmp2.length > 0) {
				// All is ok
				return null;
			}
			
		}
		
		// Something went wrong
		return "The chosen XMLTV directory either doesn't exist or doesn't contain the xmltv executable(s) and a grabber.";
	}
	
	// -------------------------------------------
	
	public static final int BROWSER_COMMAND = 0;
	public static final int TV_GRAB = 1;
	public static final int BROWSER_NAME = 2;
	public static final int COUNTRY = 3;
	public static final int DAY_START_TIME = 4;
	public static final int GRABBER_CONFIG = 5;
	public static final int INSTALL_DIRECTORY = 6;
	public static final int OS = 7;
	public static final int WORKING_DIRECTORY = 8;
	public static final int XMLTV_DIRECTORY = 9;
	public static final int DAYS_TO_GRAB = 10;
	
	// -------------------------------------------
	
	public static final int COMMANDS_TYPE = 0;
	public static final int CHOICE_TYPE = 1;
	public static final int TEXT_TYPE = 2;
	public static final int FILE_TYPE = 3;
	public static final int DIRECTORY_TYPE = 4;
	public static final int LABEL_TYPE = 5;
	
	// -------------------------------------------
	
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
	
	private static final String fs = File.separator;
	
}
