/*
 * FreeGuide J2
 *
 * Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
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
		if(!checkJavaVersion()){
			die( "Halted due to wrong Java version - should be 1.4.0 or " +
			"greater but you are using " + System.getProperty("java.version")
			+ "." );
		}
		
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
		
		String[] splitVersion = new String[3];
		int pos=0;
		int oldpos=0;
		
		for( int i=0; i<3; i++ ) {
			
			
			pos = versionString.indexOf( '.', oldpos );
			
			if( pos == -1 ) {
				pos = versionString.indexOf( '_', oldpos );
			}
			
			if( pos == -1 ) {
				pos = versionString.indexOf( '-', oldpos );
			}
			
			if( pos == -1 ) {
				pos = versionString.length();
			}
			
			splitVersion[i] = versionString.substring( oldpos, pos );
			oldpos = pos + 1;
			
		}
		
		/* Uses Java 1.4 regular expressions!
		// Split it
		Pattern dot = Pattern.compile("\\.|_|-");
		String[] splitVersion = dot.split(versionString);
		
		// If we've got something unexpected, say it's wrong
		if(splitVersion.length<3) {
			return false;
		}*/
		
		int actualMajor;
		int actualMinor;
		int actualRevision;
		
		// Parse the bits
		try {
			
			actualMajor = Integer.parseInt(splitVersion[0]);
			actualMinor = Integer.parseInt(splitVersion[1]);
			actualRevision = Integer.parseInt(splitVersion[2]);
			
		} catch( NumberFormatException e ) {
			
			e.printStackTrace();
			return false;
			
		}
		
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
