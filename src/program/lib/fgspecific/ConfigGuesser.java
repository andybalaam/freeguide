
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *  A static class to guess and check configuration options.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    3
 */

public class ConfigGuesser {

    /**
     *  Description of the Method
     *
     *@param  group  Description of the Parameter
     *@param  entry  Description of the Parameter
     *@return        Description of the Return Value
     */
    public static int guessType(String group, String entry) {

        if (group.equals("commandline")) {

            if (entry.equals("browser_command")) {
                return COMMANDS_TYPE;
            } else if (entry.equals("tv_grab")) {
                return COMMANDS_TYPE;
            }
        } else if (group.equals("misc")) {

            if (entry.equals("day_start_time")) {
                return TEXT_TYPE;
            } else if (entry.equals("days_to_grab")) {
                return TEXT_TYPE;
            } else if (entry.equals("grabber_config")) {
                return FILE_TYPE;
            } else if (entry.equals("install_directory")) {
                return DIRECTORY_TYPE;
            } else if (entry.equals("working_directory")) {
                return DIRECTORY_TYPE;
            } else if (entry.equals("xmltv_directory")) {
                return DIRECTORY_TYPE;
            } else if (entry.equals("grabber_today_offset")) {
                return TEXT_TYPE;
            } else if (entry.equals("grabber_start_today")) {
                return TEXT_TYPE;
            } else if (entry.equals("grabber_start_time")) {
                return TEXT_TYPE;
			} else if (entry.equals("region")) {
                return TEXT_TYPE;
            }
        }

        System.out.println("guessType: Unknown option asked for - " + group + ", "
                + entry);
        return -1;
    }


    /**
     *  Return the default value of the given preference
     *
     *@param  group  Description of the Parameter
     *@param  entry  Description of the Parameter
     *@return        Description of the Return Value
     */
    public static Object guess(String group, String entry) {
        FGPreferences gp = new FGPreferences(group);
		
		if( group.equals("misc") && entry.equals("region") ) {
			return "UK";
		}
		
        switch (guessType(group, entry)) {

            case TEXT_TYPE:
                return gp.get("default-" + entry, "-ERROR GETTING DEFAULT-");
            case FILE_TYPE:
                return new File(gp.get("default-" + entry,
                        "-ERROR GETTING DEFAULT-"));
            case DIRECTORY_TYPE:
                return new File(gp.get("default-" + entry,
                        "-ERROR GETTING DEFAULT-"));
            case COMMANDS_TYPE:
                return gp.getStrings("default-" + entry);
            case BOOLEAN_TYPE:
                return new Boolean(gp.getBoolean("default-" + entry, true));
        }

        System.err.println("Unknown config type - " + entry);

        return null;
    }


    // -----------------------------

    /**
     *  Description of the Method
     *
     *@param  group  Description of the Parameter
     *@param  entry  Description of the Parameter
     *@return        Description of the Return Value
     */
    private static int prefToInt(String group, String entry) {

        if (group.equals("commandline")) {

            if (entry.equals("browser_command")) {
                return BROWSER_COMMAND;
            } else if (entry.equals("tv_grab")) {
                return TV_GRAB;
            }
        } else if (group.equals("misc")) {

            if (entry.equals("day_start_time")) {
                return DAY_START_TIME;
            } else if (entry.equals("days_to_grab")) {
                return DAYS_TO_GRAB;
            } else if (entry.equals("grabber_config")) {
                return GRABBER_CONFIG;
            } else if (entry.equals("install_directory")) {
                return INSTALL_DIRECTORY;
            } else if (entry.equals("working_directory")) {
                return WORKING_DIRECTORY;
            } else if (entry.equals("xmltv_directory")) {
                return XMLTV_DIRECTORY;
            } else if (entry.equals("grabber_start_time")) {
                return GRABBER_START_TIME;
			} else if (entry.equals("grabber_today_offset")) {
                return GRABBER_TODAY_OFFSET;
            } else if (entry.equals("grabber_start_today")) {
                return GRABBER_START_TODAY;
			} else if (entry.equals("region")) {
                return REGION;
			} else if (entry.equals("browser")) {
                return BROWSER;
			} else if (entry.equals("privacy")) {
                return PRIVACY;
            }
        }

        System.out.println("prefToInt: Unknown option asked for - " + group + ", "
                + entry);
        return -1;
    }


    // -------------------------------------------

    /**
     *  Description of the Method
     *
     *@param  group  Description of the Parameter
     *@param  entry  Description of the Parameter
     *@param  val    Description of the Parameter
     *@return        Description of the Return Value
     */
    public static String checkValue(String group, String entry, Object val) {

        switch (prefToInt(group, entry)) {

            case DAY_START_TIME:
                return checkValidTime((String) val, "the FreeGuide day start");
            //case GRABBER_CONFIG:
            //	return checkXMLTVConfigFile((File)val);

            case INSTALL_DIRECTORY:
                return checkDirWriteable((File) val, "installation");
            case WORKING_DIRECTORY:
                return checkDirWriteable((File) val, "a working directory");
            //case XMLTV_DIRECTORY:
            //	return checkXMLTVDir((File)val);

            //case GRABBER_TODAY_OFFSET: //int
            //case GRABBER_START_TODAY: //int

            case GRABBER_START_TIME:
                return checkValidTime((String) val, "the grabber start time");
				
			//case REGION:

            default:
                // E.g. a country or a grabber command, just say it's right
                return null;
        }

    }


    /**
     *  Description of the Method
     *
     *@param  time    Description of the Parameter
     *@param  option  Description of the Parameter
     *@return         Description of the Return Value
     */
    private static String checkValidTime(String time, String option) {
        if (time != null && time.length() == 5 && time.charAt(2) == ':') {
            return null;
        }
        return "The time you gave for " + option + " is not in valid hh:mm format.";
    }


    /**
     *  Description of the Method
     *
     *@param  dir      Description of the Parameter
     *@param  whatFor  Description of the Parameter
     *@return          Description of the Return Value
     */
    private static String checkDirWriteable(File dir, String whatFor) {
        PreferencesGroup prefs = new PreferencesGroup();

        dir = new File(prefs.performSubstitutions(dir.toString()));

        try {

            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Check it exists and you can write a file to it
            File testFile = new File(dir.getPath() + "test.tmp");
            testFile.createNewFile();
            if (dir.exists() && testFile.canWrite()) {

                testFile.delete();
                return null;
            }
            testFile.delete();
            return "The directory you chose for " + whatFor + " can't be created or isn't writeable.";
        } catch (java.io.IOException e) {

            return "There was an error trying to create the directory you chose for " + whatFor + ".";
        }
    }

    public final static int BROWSER_COMMAND = 0;
    public final static int TV_GRAB = 1;
    public final static int DAY_START_TIME = 2;
    public final static int GRABBER_CONFIG = 3;
    public final static int INSTALL_DIRECTORY = 4;
    public final static int WORKING_DIRECTORY = 5;
    public final static int XMLTV_DIRECTORY = 6;
    public final static int DAYS_TO_GRAB = 7;
    public final static int GRABBER_TODAY_OFFSET = 8;
    public final static int GRABBER_START_TODAY = 9;
    public final static int GRABBER_START_TIME = 10;
	public final static int REGION = 11;
	public final static int BROWSER = 12;
	public final static int PRIVACY = 13;
	
    // -------------------------------------------

    public final static int COMMANDS_TYPE = 0;
    public final static int TEXT_TYPE = 1;
    public final static int FILE_TYPE = 2;
    public final static int DIRECTORY_TYPE = 3;
    public final static int BOOLEAN_TYPE = 4;

    // -------------------------------------------

    private final static String fs = File.separator;

}
