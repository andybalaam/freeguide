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

import java.awt.Color;
import java.util.logging.Logger;

/**
 * The main class called to start FreeGuide.  Calls the FreeGuideStartupChecker
 * to do the real work.
 *
 * Also contains some global objects.
 *
 * @author  Andy Balaam
 * @version 6
 */
public class FreeGuide implements FreeGuideLauncher {
	
	/**
	 * The constructor for the class that starts it all.
	 */
	public FreeGuide(String[] args) {
		
		// Show the Please Wait screen
		FreeGuidePleaseWait pleaseWait = new FreeGuidePleaseWait();
		pleaseWait.setVisible(true);
		
		// Check various things and then begin
		FreeGuideStartupChecker.runChecks(this, args, pleaseWait);
	}

    /**
	 * The method called when FreeGuide is run.
	 *
     * @param args the command line arguments
     */
    public static void main (String args[]) {
		
		new FreeGuide(args);
		
	}

	public static void die(String msg) {
		
		log.severe(msg);
		System.err.println(msg);
		System.exit(1);
		
	}

	// -----------------------------------------------------------------------
	
	public FreeGuideLauncher getLauncher() {
		return null;
	}
	
	public void setVisible(boolean show) {
		// Nothing - not used
	}
	
	public void reShow() {	
		new FreeGuideViewer(this, null);
	}
	
	public static String getVersion() {
		if(version_revision==0) {
			return version_major + "." + version_minor;
		} else {
			return version_major + "." + version_minor + "." + version_revision;
		}
	}
	
	//------------------------------------------------------------------------

	public static FreeGuideCmdArgs arguments;	// The command line args
	public static FreeGuidePreferencesGroup prefs;	// Holds all preferences info
	public static Logger log;					// The log file
	
	public static final int version_major = 0;
	public static final int version_minor = 4;
	public static final int version_revision = 3;
	
	// Defaults - can be overridden by user preferences
	public static final Color PROGRAMME_NORMAL_COLOUR = Color.white;
	public static final Color PROGRAMME_CHOSEN_COLOUR = new Color(220,220,220);
	public static final Color CHANNEL_COLOUR = new Color(245,245,255);
	public static final int CHANNEL_HEIGHT = 28;
	public static final int VERTICAL_GAP = 1;
	public static final int HORIZONTAL_GAP = 1;
	public static final int PANEL_WIDTH = 6000;
	public static final int CHANNEL_PANEL_WIDTH = 400;
	
}
