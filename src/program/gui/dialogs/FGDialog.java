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

package freeguidetv.gui.dialogs;

import java.awt.*;
import javax.swing.*;

/**
 *  A dialog that returns whether anything was changed while it was being used.
 *
 *@author     Andy Balaam
 *@created    9 Dec 2003
 *@version    1
 */
public class FGDialog extends JDialog {

    /**
     * Constructor - just creates a modal dialog with the given title and owner
     *
     *@param owner the <code>JFrame</code> from which the dialog is displayed 
     *@param title the <code>String</code> to display in the dialog's title
	 * 			   bar
	 *
     */
    public FGDialog(JFrame owner, String title) {
        super(owner, title, true);

		init();
	}
	
	/**
     * Constructor - just creates a modal dialog with the given title and owner
     *
     *@param owner the <code>JDialog</code> from which the dialog is displayed 
     *@param title the <code>String</code> to display in the dialog's title
	 * 			   bar
	 *
     */
    public FGDialog(JDialog owner, String title) {
        super(owner, title, true);

		init();
		
    }

	private void init() {
		
		addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });
    }
	
    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
        quit();
    }


    /**
     * Method showDialog calls the default show method but additionally
     * returns a simple flag to indicate whether anything has been changed.
     *
     * @returns Returns <code>true</code> if any of the settings in the
     *                 dialog have been changed, and <code>false</code>
	 *                 otherwise.
     */
    
    public boolean showDialog() {
        show();
        return updatedFlag;
    }


    // ------------------------------------------------------------------------

    /**
     *  Closes the form and goes back to the viewer.
     */
    protected void quit() {

        hide();
        dispose();

    }

    /**
     * This flag indicated whether any of the parameters on the
     * CustomiserDialog have been updated during this session.
     *
     */
    protected boolean updatedFlag = false;
    

}
