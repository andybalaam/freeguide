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
package freeguide.lib.fgspecific;

import java.util.Date;
import java.util.TimerTask;

import javax.swing.JOptionPane;

/**
 * Closes a dialog box that the user hasn't closed.
 *
 * @author Andy Balaam
 * @version 1
 */
public class MessageDialogCloseTimerTask extends TimerTask
{

    /** The MessageDialogTimerTask we want to cancel when we are triggered */
    private MessageDialogTimerTask taskToClose;

    /**
     * Creates a new MessageDialogCloseTimerTask object.
     *
     * @param taskToClose DOCUMENT ME!
     */
    public MessageDialogCloseTimerTask( MessageDialogTimerTask taskToClose )
    {
        this.taskToClose = taskToClose;

    }

    /**
     * Displays a message dialog.
     */
    public void run(  )
    {
        taskToClose.stop(  );

    }
}
