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
import java.util.TimerTask;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *  Handles timers to remind the user of programmes she wants to watch.
 *
 *@author     Andy Balaam
 *@created    05 Novemver 2003
 *@version    1
 */
public class MessageDialogTimerTask extends TimerTask {

	public MessageDialogTimerTask( String message ) {
		
		this.message = message;
		
	}
	
	/**
	 * Displays a message dialog.
	 */
	public void run() {
		
		 //JOptionPane.showMessageDialog( null, message, "Reminder",
		 //	JOptionPane.INFORMATION_MESSAGE ); 
		
		JOptionPane optionPane = new JOptionPane( message,
			JOptionPane.INFORMATION_MESSAGE );
			
		dialog = optionPane.createDialog( null, "FreeGuide Reminder" );
		
		dialog.show();
		
	}
	
	public void stop() {
		
		dialog.dispose();
		
	}
	
	/**
	 * The message to display on screen at the scheduled time.
	 */
	private String message;
	
	/**
	 * The message box which displays the message
	 */
	private JDialog dialog;
	
}


