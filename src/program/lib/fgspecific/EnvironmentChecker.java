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
import java.util.regex.Pattern;

/**
 *  A checker that is run on startup (and in the installer) - just checks that
 *  we have the right Java Virtual Machine version.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    2
 */

public class EnvironmentChecker {

    /**
     *  At the moment just checks for the right version of Java
     */
    public static void runChecks() {

        // Check the user has the right version of Java
        if (!checkJavaVersion()) {
            die("Halted due to wrong Java version - should be 1.4.0 or " +
                    "greater but you are using " + System.getProperty("java.version")
                    + ".");
        }
    }


    /**
     *  Exits with an error.
     *
     *@param  message  Description of the Parameter
     */
    private static void die(String message) {
        System.err.println(message);
        System.exit(1);
    }


    /**
     *  checkJavaVersion Checks we have at least Java 1.4.
     *
     *@return    Description of the Return Value
     */
    private static boolean checkJavaVersion() {

        // Check for Java 1.4 +
        return isJavaVersionAtLeast(1, 4, 0);
    }


    /**
     *  Gets the javaVersionAtLeast attribute of the EnvironmentChecker class
     *
     *@param  wantedMajor     Description of the Parameter
     *@param  wantedMinor     Description of the Parameter
     *@param  wantedRevision  Description of the Parameter
     *@return                 The javaVersionAtLeast value
     */
    private static boolean isJavaVersionAtLeast(int wantedMajor, int wantedMinor, int wantedRevision) {

        // Find out the version from the system
        String versionString = System.getProperty("java.version");

        String[] splitVersion = new String[3];
        int pos = 0;
        int oldpos = 0;

        for (int i = 0; i < 3; i++) {

            pos = versionString.indexOf('.', oldpos);

            if (pos == -1) {
                pos = versionString.indexOf('_', oldpos);
            }

            if (pos == -1) {
                pos = versionString.indexOf('-', oldpos);
            }

            if (pos == -1) {
                pos = versionString.length();
            }

            splitVersion[i] = versionString.substring(oldpos, pos);
            oldpos = pos + 1;

        }

        int actualMajor;
        int actualMinor;
        int actualRevision;

        // Parse the bits
        try {

            actualMajor = Integer.parseInt(splitVersion[0]);
            actualMinor = Integer.parseInt(splitVersion[1]);
            actualRevision = Integer.parseInt(splitVersion[2]);

        } catch (NumberFormatException e) {

            e.printStackTrace();
            return false;
        }

        // Check we have the required version
        if (actualMajor > wantedMajor ||
                (actualMajor == wantedMajor && actualMinor > wantedMinor) ||
                (actualMajor == wantedMajor && actualMinor == wantedMinor && actualRevision >= wantedRevision)) {
            return true;
        }

        // If not, we fail
        return false;
    }

}
