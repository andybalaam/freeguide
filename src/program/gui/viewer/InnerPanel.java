/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */

package freeguide.gui.viewer;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Rectangle;
import java.awt.Dimension;

/**
 *  Description of the Class
 *
 *@author     andy
 *@created    28 June 2003
 */
public class InnerPanel extends JPanel implements Scrollable {

    /**
     *  Gets the scrollableUnitIncrement attribute of the InnerPanel object
     *
     *@param  r   Description of the Parameter
     *@param  i1  Description of the Parameter
     *@param  i2  Description of the Parameter
     *@return     The scrollableUnitIncrement value
     */
    public int getScrollableUnitIncrement(Rectangle r, int i1, int i2) {
        return r.width / 10;
    }


    /**
     *  Gets the scrollableBlockIncrement attribute of the InnerPanel object
     *
     *@param  r   Description of the Parameter
     *@param  i1  Description of the Parameter
     *@param  i2  Description of the Parameter
     *@return     The scrollableBlockIncrement value
     */
    public int getScrollableBlockIncrement(Rectangle r, int i1, int i2) {
        return r.width;
    }


    /**
     *  Gets the preferredScrollableViewportSize attribute of the InnerPanel
     *  object
     *
     *@return    The preferredScrollableViewportSize value
     */
    public Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
    }


    /**
     *  Gets the scrollableTracksViewportWidth attribute of the InnerPanel
     *  object
     *
     *@return    The scrollableTracksViewportWidth value
     */
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }


    /**
     *  Gets the scrollableTracksViewportHeight attribute of the InnerPanel
     *  object
     *
     *@return    The scrollableTracksViewportHeight value
     */
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

}
