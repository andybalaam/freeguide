/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */

package freeguide.lib.fgspecific;

import freeguide.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *  A static class to check configuration options.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    4
 */

public class ConfigGuesser {

    /**
     *  Description of the Method
     *
     *@param  group  Description of the Parameter
     *@param  entry  Description of the Parameter
     *@param  val    Description of the Parameter
     *@return        Description of the Return Value
     */
    public static String checkValue(String group, String entry, Object val) {

		if( group.equals( "misc" ) ) {
			
			if( entry.equals("day_start_time") ) {
				return checkValidTime( (String)val, "the FreeGuide day start" );
			} else if( entry.equals( "grabber_start_time" ) ) {
				return checkValidTime( (String)val, "the grabber start time" );
			} else if( entry.equals( "days_to_grab" ) ) {
				return checkNumeric( (String)val, "the number of days to grab");
			} else if( entry.equals( "working_directory" ) ) {
				return checkDirWriteable( (File)val, "a working directory");
			} else if( entry.equals( "install_directory" ) ) {
				return checkDirWriteable( (File)val, "the install directory");
			} else if( entry.equals( "region" ) ) {
				return checkNonEmpty( (String)val, "region" );
			} else if( entry.equals( "browser" ) ) {
				return null;
			} else if( entry.equals( "privacy" ) ) {
				return null;
			}
			
		}
		
		String msg = "Check asked for on an unknown option \"" + group + "."
			+ entry + "\".";
		
		if( FreeGuide.log != null ) {
			FreeGuide.log.info( msg );
		} else {
			System.err.println( msg );
		}
		
		// If it's not one of these, don't check , just say it's good.
		return null;
		
    }


    /**
     *  Description of the Method
     *
     *@param  time    Description of the Parameter
     *@param  option  Description of the Parameter
     *@return         Description of the Return Value
     */
    private static String checkValidTime(String time, String whatFor) {
		
        if (time != null && time.length() == 5 && time.charAt(2) == ':') {
            return null;
        }
        return "The time you gave for " + whatFor
			+ " is not in valid hh:mm format.";
    }


	/**
     *  Description of the Method
     *
     *@param  number  Description of the Parameter
     *@param  option  Description of the Parameter
     *@return         Description of the Return Value
     */
    private static String checkNumeric(String number, String whatFor) {
		
		if( number == null ) {
			return "The option for " + whatFor + " is blank.";
		}
		
		try {
			Integer.parseInt( number );
		} catch( NumberFormatException e ) {
			
			return "The option for " + whatFor + " is not a valid number.";
			
		}
		
        return null;
    }
	
	/**
     *  Description of the Method
     *
     *@param  number  Description of the Parameter
     *@param  option  Description of the Parameter
     *@return         Description of the Return Value
     */
    private static String checkNonEmpty(String number, String whatFor) {
		
		if( number == null || number.equals("") ) {
			return "The option for " + whatFor + " is blank.";
		}
		
        return null;
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
            return "The directory you chose for " + whatFor
				+ " can't be created or isn't writeable.";
			
        } catch (java.io.IOException e) {

            return "There was an error trying to create the directory you "
				+ "chose for " + whatFor + ".";
        }
    }

    private final static String fs = File.separator;

}
