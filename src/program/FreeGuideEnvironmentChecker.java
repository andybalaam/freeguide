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

import java.util.regex.Pattern;
 
/**
 * An checker that is run on startup (and in the installer)
 *
 * @author  Andy Balaam
 * @version 1
 */

public class FreeGuideEnvironmentChecker {

	/**
	 * At the moment just checks for the right version of Java
	 */
	public static void runChecks() {
		
		// Check the user has the right version of Java
		if(!checkJavaVersion()){die("Halted due to wrong Java version.");}
		
	}

	/**
	 * Exits with an error.
	 */
	private static void die(String message) {
		System.err.println(message);
		System.exit(1);
	}
	
	/**
	* checkJavaVersion
	*
	* Checks we have at least Java 1.4.
	*/
	private static boolean checkJavaVersion() {
		
		// Check for Java 1.4 +
		return isJavaVersionAtLeast(1, 4, 0);
		
	}
	
	private static boolean isJavaVersionAtLeast(int wantedMajor, int wantedMinor, int wantedRevision) {
		
		// Find out the version from the system
		String versionString = System.getProperty("java.version");
		
		// Split it
		Pattern dot = Pattern.compile("\\.|_");
		String[] splitVersion = dot.split(versionString);
		
		// If we've got something unexpected, say it's wrong
		if(splitVersion.length<3) {
			return false;
		}
		
		// Parse the bits
		int actualMajor = Integer.parseInt(splitVersion[0]);
		int actualMinor = Integer.parseInt(splitVersion[1]);
		int actualRevision = Integer.parseInt(splitVersion[2]);
		
		// Check we have the required version
		if(actualMajor > wantedMajor ||
			(actualMajor == wantedMajor && actualMinor > wantedMinor) ||
			(actualMajor == wantedMajor && actualMinor == wantedMinor && actualRevision >= wantedRevision)) {
			return true;
		}
		
		// If not, we fail
		return false;
		
	}
	
}
