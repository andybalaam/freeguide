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
import java.util.Timer;

/**
 * Handles timers to remind the user of programmes she wants to watch.
 *
 * @author Andy Balaam
 * @version 1
 */
public class MessageDialogTimer
{

    Timer timer;
    Timer timer_closer;

    /**
     * Sets up a timer handler.
     */
    public MessageDialogTimer(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     * @param startTime DOCUMENT_ME!
     * @param giveUpTime DOCUMENT_ME!
     */
    public void schedule( String message, Date startTime, Date giveUpTime )
    {
        timer = new Timer(  );
        timer_closer = new Timer(  );

        MessageDialogTimerTask task = new MessageDialogTimerTask( message );
        MessageDialogCloseTimerTask task_closer =
            new MessageDialogCloseTimerTask( task );

        timer.schedule( task, startTime );
        timer_closer.schedule( task_closer, giveUpTime );

    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
        timer_closer.cancel(  );
        timer.cancel(  );

    }
}
