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

import java.util.Date;
import java.util.Timer;

/**
 *  Handles timers to remind the user of programmes she wants to watch.
 *
 *@author     Andy Balaam
 *@created    05 Novemver 2003
 *@version    1
 */
public class MessageDialogTimer {

    /**
     *  Sets up a timer handler.
     */
    public MessageDialogTimer() {

    }
	
	public void schedule( String message, Date startTime, Date giveUpTime ) {
		
		timer = new Timer();
		timer_closer = new Timer();
		
		MessageDialogTimerTask task = new MessageDialogTimerTask( message );
		MessageDialogCloseTimerTask task_closer
			= new MessageDialogCloseTimerTask( task );
		
		timer.schedule( task, startTime );
		timer_closer.schedule( task_closer, giveUpTime );
		
	}

	public void cancel() {
		
		timer_closer.cancel();
		timer.cancel();
		
	}
    
	Timer timer;
	Timer timer_closer;
	
}

