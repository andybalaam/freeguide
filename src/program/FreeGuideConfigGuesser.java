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
 * A static class to guess and check configuration options.
 *
 * @author  Andy Balaam
 * @version 2
 */

public class FreeGuideConfigGuesser {
	
	public static int guessType(String group, String entry) {
		
		if(group.equals("commandline")) {
			
			if(entry.equals("browser_command")) {
				return COMMANDS_TYPE;
			} else if(entry.equals("tv_grab")) {
				return COMMANDS_TYPE;
			}
			
		} else if(group.equals("misc")) {

			if(entry.equals("day_start_time")) {
				return TEXT_TYPE;
			} else if(entry.equals("days_to_grab")) {
				return TEXT_TYPE;
			} else if(entry.equals("grabber_config")) {
				return FILE_TYPE;
			} else if(entry.equals("install_directory")) {
				return DIRECTORY_TYPE;
			} else if(entry.equals("working_directory")) {
				return DIRECTORY_TYPE;
			} else if(entry.equals("xmltv_directory")) {
				return DIRECTORY_TYPE;
			}
			
		}
		
		System.out.println("Unknown option asked for - " + entry);
		return -1;

	}
	
	/**
	 * Return the default value of the given preference
	 */
	public static Object guess(String group, String entry) {
		
		FreeGuidePreferences gp = new FreeGuidePreferences( group );
		
		switch( guessType(group, entry) ) {
					
			case TEXT_TYPE:
				return gp.get( "default-" + entry, "-ERROR GETTING DEFAULT-" );
				
			case FILE_TYPE:
				return new File( gp.get( "default-" + entry,
					"-ERROR GETTING DEFAULT-" ) );
				
			case DIRECTORY_TYPE:
				return new File( gp.get( "default-" + entry,
					"-ERROR GETTING DEFAULT-" ) );
				
			case COMMANDS_TYPE:
				return gp.getStrings( "default-" + entry );
				
			
		}
		
		System.err.println("Unknown config type - " + entry);
		
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
			
			if(entry.equals("day_start_time")) {
				return DAY_START_TIME;
			} else if(entry.equals("days_to_grab")) {
				return DAYS_TO_GRAB;
			} else if(entry.equals("grabber_config")) {
				return GRABBER_CONFIG;
			} else if(entry.equals("install_directory")) {
				return INSTALL_DIRECTORY;
			} else if(entry.equals("working_directory")) {
				return WORKING_DIRECTORY;
			} else if(entry.equals("xmltv_directory")) {
				return XMLTV_DIRECTORY;
			}
			
		}
		
		System.out.println("Unknown option asked for - " + entry);
		return -1;
		
	}
	
	// -------------------------------------------
	
	public static String checkValue(String group, String entry, Object val) {
		
		switch(prefToInt(group, entry)) {
			
			case DAY_START_TIME:
				return checkValidTime((String)val);
			
			//case GRABBER_CONFIG:
			//	return checkXMLTVConfigFile((File)val);

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
		if(time!=null && time.length()==5 && time.charAt(2)==':') {
			return null;
		}
		return "The time you gave for the start of days is not in valid hh:mm format.";
	}
	
	private static String checkDirWriteable(File dir, String whatFor) {
		
		FreeGuidePreferencesGroup prefs = new FreeGuidePreferencesGroup();
		
		dir = new File( prefs.performSubstitutions( dir.toString() ) );
		
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
	
	/** 
	 * Find whether a proposed XMLTV directory is correct.  Checks this
	 * by looking for the files xmltv.exe or tv_grab* in it.
	 */
	private static String checkXMLTVDir(File dir) {
		
		FreeGuidePreferencesGroup prefs = new FreeGuidePreferencesGroup();
		
		dir = new File( prefs.performSubstitutions( dir.toString() ) );
		
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
	public static final int DAY_START_TIME = 2;
	public static final int GRABBER_CONFIG = 3;
	public static final int INSTALL_DIRECTORY = 4;
	public static final int WORKING_DIRECTORY = 5;
	public static final int XMLTV_DIRECTORY = 6;
	public static final int DAYS_TO_GRAB = 7;
	
	// -------------------------------------------
	
	public static final int COMMANDS_TYPE = 0;
	public static final int TEXT_TYPE = 1;
	public static final int FILE_TYPE = 2;
	public static final int DIRECTORY_TYPE = 3;
	
	// -------------------------------------------
	
	private static final String fs = File.separator;
	
}
