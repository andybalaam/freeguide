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
 * The main class called to start FreeGuide.  Performs some
 * housekeeping before launching the viewer or downloader.
 *
 * Also contains some global objects.
 *
 * @author  Andy Balaam
 * @version 6
 */
public class FreeGuide implements FreeGuideLauncher {

	/** 
	 * The constructor for a particular FreeGuide instance.
	 */
	public FreeGuide() {
	
		new FreeGuideViewer(this);
		
	}
	
    /**
	 * The method called when FreeGuide is run.  Processes
	 * command line arguments, sets up the preferences,
	 * makes a log file, checks there's a working directory,
	 * and starts the viewer.
	 *
     * @param args the command line arguments
     */
    public static void main (String args[]) {
		
		// Check various things and then begin
		FreeGuideStartupChecker.runChecks(args);
		
		// All is ok, so begin
		new FreeGuide();
		
	}
	
	public void reShow() {
		
		die("I don't think it should ever get here!");
		
	}

	public static void die(String msg) {
		
		log.severe(msg);
		System.err.println(msg);
		System.exit(1);
		
	}
	
	public FreeGuideLauncher getLauncher() {
		return null;
	}
	
	//------------------------------------------------------------------------
	
	public static FreeGuideCmdArgs arguments;	// The command line args
	public static FreeGuidePreferencesGroup prefs;	// Holds all preferences info
	public static Logger log;					// The log file
	
	public static final String version = "0.3";
	
	// Defaults - can be overridden by user preferences
	public static final Color PROGRAMME_NORMAL_COLOUR = Color.white;
	public static final Color PROGRAMME_CHOSEN_COLOUR = new Color(220,220,220);
	public static final Color CHANNEL_COLOUR = new Color(245,245,255);
	public static final int CHANNEL_HEIGHT = 28;
	public static final int VERTICAL_GAP = 1;
	public static final int HORIZONTAL_GAP = 1;
	public static final int PANEL_WIDTH = 6000;
	public static final int CHANNEL_PANEL_WIDTH = 6000;
	
}
