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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*
 *  A panel full of options about time
 *
 * @author     Andy Balaam
 * @created    12 Dec 2003
 * @version    1
 */

public class RemindersOptionPanel extends OptionPanel {

	public RemindersOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		// Make the objects
		
		JLabel remindLabel = newLeftJLabel( "Remind me of progs:" );
		Object[] options = new Object[2];
		options[0] = "Yes";
		options[1] = "No";
		remindComboBox = newRightJComboBox( options );
		remindLabel.setLabelFor(remindComboBox);
		remindLabel.setDisplayedMnemonic(KeyEvent.VK_R);
		
		JLabel warningLabel = newLeftJLabel( "Seconds warning:" );
		warningTextField = newRightJTextField();
		warningLabel.setLabelFor(warningTextField);
		warningLabel.setDisplayedMnemonic(KeyEvent.VK_W);
		
		JLabel giveupLabel = newLeftJLabel( "Give up after (secs):" );
		giveupTextField = newRightJTextField();
		giveupLabel.setLabelFor(giveupTextField);
		giveupLabel.setDisplayedMnemonic(KeyEvent.VK_G);
		
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addFWX  ( remindLabel     , 0, 0, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( remindComboBox  , 1, 0, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( warningLabel    , 0, 1, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( warningTextField, 1, 1, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( giveupLabel     , 0, 2, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( giveupTextField , 1, 2, gbe.FILL_HOR   , 0.8 );
		
		// Load in the values from config
		load();
		
	}
	
	protected void doLoad( String prefix ) {

		if( misc.getBoolean( prefix + "reminders_on", true ) ) {
			remindComboBox.setSelectedIndex(0);
		} else {
			remindComboBox.setSelectedIndex(1);
		}

		warningTextField.setText( String.valueOf( misc.getInt( prefix
			+ "reminders_warning_secs", 300 ) ) );
		
		giveupTextField.setText( String.valueOf(misc.getInt( prefix
			+ "reminders_give_up_secs",	600 ) ) );
		
	}
	
	
	
	/**
	 * Saves the values in this option pane.
	 *
	 * @return false since nothing changes the view
	 */
	public boolean doSave() {
	
		if( remindComboBox.getSelectedIndex() == 0 ) {
			misc.putBoolean( "reminders_on", true );
		} else {
			misc.putBoolean( "reminders_on", false );
		}
		
		misc.putInt( "reminders_warning_secs", Integer.parseInt(
			warningTextField.getText() ) );
		
		misc.putInt( "reminders_give_up_secs", Integer.parseInt(
			giveupTextField.getText() ) );
		
		return false;
		
	}
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Reminders";
		
	}

	// ----------------------------------
	
	private JComboBox remindComboBox;
	private JTextField warningTextField;
	private JTextField giveupTextField;
	
}
