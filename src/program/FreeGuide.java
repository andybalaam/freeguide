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
import java.awt.Color;
import java.util.logging.Logger;

/**
 *  The main class called to start FreeGuide. Calls the FreeGuideStartupChecker
 *  to do the real work. Also contains some global objects.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    6
 */
public class FreeGuide implements Launcher {

    /**
     *  The constructor for the class that starts it all.
     *
     *@param  args  The commandline arguments
     */
    public FreeGuide(String[] args) {
        PleaseWaitFrame pleaseWait = new PleaseWaitFrame(this, args);
    }


    /**
     *  The method called when FreeGuide is run.
     *
     *@param  args  the command line arguments
     */
    public static void main(String args[]) {

        new FreeGuide(args);

    }


    /**
     *  Description of the Method
     *
     *@param  msg  Description of the Parameter
     */
    public static void die(String msg) {

        System.err.println(msg);
		log.severe(msg);
        System.exit(1);

    }


    // -----------------------------------------------------------------------

    /**
     *  Gets the launcher attribute of the FreeGuide object
     *
     *@return    The launcher value
     */
    public Launcher getLauncher() {
        return null;
    }


    /**
     *  Sets the visible attribute of the FreeGuide object
     *
     *@param  show  The new visible value
     */
    public void setVisible(boolean show) {
        // Nothing - not used
    }


    /**
     *  Description of the Method
     */
    public void reShow() {
        if (FreeGuide.prefs.screen.getBoolean("use_metal_landf", false)) {
            ViewerFrame.setDefaultLookAndFeelDecorated(true);
        }
        new ViewerFrame(this, null);
    }


    /**
     *  Gets the version attribute of the FreeGuide class
     *
     *@return    The version value
     */
    public static String getVersion() {
        if (version_revision == 0) {
            return version_major + "." + version_minor;
        } else {
            return version_major + "." + version_minor + "." + version_revision;
        }
    }


    //------------------------------------------------------------------------

    /**
     *  The command line args
     */
    public static CmdArgs arguments;

    /**
     *  Holds all preferences info
     */
    public static PreferencesGroup prefs;

    /**
     *  The log file
     */
    public static Logger log;

    /**
     *  The major version of FreeGuide
     */
    public final static int version_major = 0;
    /**
     *  The minor version of FreeGuide
     */
    public final static int version_minor = 6;
    /**
     *  What revision of the version this is
     */
    public final static int version_revision = 1;

    /**
     *  Default colour of a normal programme
     */
    public final static Color PROGRAMME_NORMAL_COLOUR = Color.white;
    /**
     *  Default colour of a clicked programme
     */
    public final static Color PROGRAMME_CHOSEN_COLOUR
		= new Color(204, 255, 204);
	/**
     *  Default colour of a heart that indicates a favourite
     */
    public final static Color PROGRAMME_HEART_COLOUR = Color.red;
    /**
     *  Default colour of a movie
     */
    public final static Color PROGRAMME_MOVIE_COLOUR = new Color(255, 230, 230);
    /**
     *  Default colour of the channel labels
     */
    public final static Color CHANNEL_COLOUR = new Color(245, 245, 255);
    /**
     *  Default height of each channel row
     */
    public final static int CHANNEL_HEIGHT = 28;
    /**
     *  Default gap between channel rows
     */
    public final static int VERTICAL_GAP = 1;
    /**
     *  Default horizontal gap between programmes
     */
    public final static int HORIZONTAL_GAP = 1;
    /**
     *  Default width of the scrolling panel containign programmes
     */
    public final static int PANEL_WIDTH = 8000;
    /**
     *  Default width of the channels scrolling panel
     */
    public final static int CHANNEL_PANEL_WIDTH = 400;

}
