/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
import java.io.File;
import java.util.logging.Logger;
import java.util.Vector;

/**
 *  FreeGuideStartupChecker Provides a single static method that checks
 *  everything is ok, and exits if not.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    4
 */
public class StartupChecker {

    /**
     *  Description of the Method
     *
     *@param  launcher    Description of the Parameter
     *@param  args        Description of the Parameter
     *@return             Description of the Return Value
     */
    public static Vector runChecks(Launcher launcher, String[] args) {

        EnvironmentChecker.runChecks();

        // ------------------------------------------------------
        // Check we can make a log file
        if (!setupLog()) {
            die("FreeGuide - Failed to create log file.");
        }

        // Check any arguments that were passed in are ok
        if (!processArgs(args)) {
            FreeGuide.die("Argument processing failed.");
        }

        // Set up the Preferences clases
        if (!setupPrefs()) {
            FreeGuide.die("Failed to set up configuration.");
        }

        // --------------------------------------------------------------------
        // Checks that need correction

        // Variables that will be true if something needs correcting
        Vector failedWhat = new Vector();
        PreferencesGroup prefs = FreeGuide.prefs;

        checkFileFailure(prefs.misc, "xmltv_directory", failedWhat);
        checkTextFailure(prefs.misc, "day_start_time", failedWhat);
        checkTextFailure(prefs.misc, "days_to_grab", failedWhat);
        checkFileFailure(prefs.misc, "working_directory", failedWhat);

        return failedWhat;
    }


    /**
     *  Stop the program with an error
     *
     *@param  message  The error message to display
     */
    private static void die(String message) {
        System.err.println(message);
        System.exit(1);
    }


    /**
     *  Check that there is a config entry of text type
     *
     *@param  pref        Description of the Parameter
     *@param  entry       Description of the Parameter
     *@param  failedWhat  Description of the Parameter
     */
    private static void checkTextFailure(FGPreferences pref,
            String entry, Vector failedWhat) {

        if (ConfigGuesser.checkValue("misc", entry,
                FreeGuide.prefs.performSubstitutions(
                pref.get(entry))) != null) {

            failedWhat.add("misc." + entry);
        }
    }


    /**
     *  Check that there is a config entry of file type
     *
     *@param  pref        Description of the Parameter
     *@param  entry       Description of the Parameter
     *@param  failedWhat  Description of the Parameter
     */
    private static void checkFileFailure(FGPreferences pref, String entry, Vector failedWhat) {

        String pr = FreeGuide.prefs.performSubstitutions(pref.get(entry));

        if ((pr == null) ||
                (ConfigGuesser.checkValue("misc", entry, new File(pr)) != null)) {

            failedWhat.add("misc." + entry);

        }
    }


    /**
     *  processArgs Processes the commandline arguments and stores them.
     *
     *@param  args  The commandline arguments
     *@return       true if all is well, false otherwise.
     */
    private static boolean processArgs(String[] args) {
        // Process the command line arguments and store them
        FreeGuide.arguments = new CmdArgs(args);

        return FreeGuide.arguments.noErrors();
    }


    /**
     *  setupLog Creates a log file.
     *
     *@return    true if all is well, false otherwise
     */
    private static boolean setupLog() {

        // Set up the logger
        FreeGuide.log = Logger.getLogger("org.freeguide-tv");

        return true;
    }


    /**
     *  setupPrefs Creates the preferences object that holds the configuration
     *  info
     *
     *@return    true if all is well, false otherwise
     */
    private static boolean setupPrefs() {

        // Make the object that holds all the Preferences objects
        FreeGuide.prefs = new PreferencesGroup();

        return FreeGuide.prefs.noErrors();
    }
}
