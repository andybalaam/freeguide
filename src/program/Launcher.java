/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
/**
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    1
 */
public interface Launcher {

    /**
     *  Description of the Method
     */
    public void reShow();


    /**
     *  Gets the launcher attribute of the Launcher object
     *
     *@return    The launcher value
     */
    public Launcher getLauncher();


    /**
     *  Sets the visible attribute of the Launcher object
     *
     *@param  show  The new visible value
     */
    public void setVisible(boolean show);

}

