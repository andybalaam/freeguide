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

package freeguidetv.gui.options;

import freeguidetv.gui.dialogs.*;
import freeguidetv.lib.general.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/*
 *  A panel full of options about the downloading listings
 *
 * @author     Andy Balaam
 * @created    10 Dec 2003
 * @version    2
 */

public class DownloadingOptionPanel extends OptionPanel {

	public DownloadingOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		// Make the objects
		
		JLabel commandLabel = newLeftJLabel( "Grabber Command:" );
		commandTextArea = newRightJTextArea();
		JScrollPane commandScrollPane = new JScrollPane(commandTextArea);
		commandLabel.setLabelFor(commandTextArea);
		commandLabel.setDisplayedMnemonic( KeyEvent.VK_G );
		
		JLabel daysLabel = newLeftJLabel( "Download how much:" );
		Object[] options = new Object[8];
		options[0] = "1 day";
		options[1] = "2 days";
		options[2] = "3 days";
		options[3] = "4 days";
		options[4] = "5 days";
		options[5] = "6 days";
		options[6] = "1 week";
		options[7] = "2 weeks";
		daysComboBox = newRightJComboBox( options );
		daysLabel.setLabelFor(daysComboBox);
		daysLabel.setDisplayedMnemonic( KeyEvent.VK_D );
		
		JLabel startTodayLabel = newLeftJLabel( "Start grabbing:" );
		options = new Object[2];
		options[0] = "Today";
		options[1] = "Day viewed";
		startTodayComboBox = newRightJComboBox( options );
		startTodayLabel.setLabelFor(startTodayComboBox);
		startTodayLabel.setDisplayedMnemonic( KeyEvent.VK_S );
		
		JLabel dayStartLabel = newLeftJLabel( "Day start time (hh:mm):" );
		dayStartTextField = newRightJTextField();
		dayStartLabel.setLabelFor(dayStartTextField);
		dayStartLabel.setDisplayedMnemonic( KeyEvent.VK_A );
		
		JLabel todayOffsetLabel = newLeftJLabel( "Today offset:" );
		todayOffsetTextField = newRightJTextField();
		todayOffsetLabel.setLabelFor(todayOffsetTextField);
		todayOffsetLabel.setDisplayedMnemonic( KeyEvent.VK_T );
		
		options = new Object[2];
		options[0] = "Yes";
		options[1] = "No";
		JLabel modalLabel = newLeftJLabel( "Download in background?" );
		modalComboBox = newRightJComboBox(options);
		modalLabel.setLabelFor(modalComboBox);
		modalLabel.setDisplayedMnemonic( KeyEvent.VK_B );
		
		JLabel configLabel = newLeftJLabel( "Config Command:" );
		configTextArea = newRightJTextArea();
		JScrollPane configScrollPane = new JScrollPane(configTextArea);
		configLabel.setLabelFor(configTextArea);
		configLabel.setDisplayedMnemonic( KeyEvent.VK_G );
		
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addAFWX ( commandLabel      , 0, 0, gbe.ANCH_NORTH, gbe.FILL_HOR,
			0.2 );
		gbe.addFWXWY( commandScrollPane , 1, 0, gbe.FILL_BOTH  , 0.8, 0.5 );
		
		gbe.addFWX  ( daysLabel         , 0, 1, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( daysComboBox      , 1, 1, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( startTodayLabel   , 0, 2, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( startTodayComboBox, 1, 2, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( dayStartLabel     , 0, 3, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( dayStartTextField , 1, 3, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( todayOffsetLabel    , 0, 4, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( todayOffsetTextField, 1, 4, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( modalLabel          , 0, 5, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( modalComboBox       , 1, 5, gbe.FILL_HOR   , 0.8 );
		
		gbe.addAFWX ( configLabel         , 0, 6, gbe.ANCH_NORTH, gbe.FILL_HOR,
			0.2 );
		gbe.addFWXWY( configScrollPane    , 1, 6, gbe.FILL_BOTH  , 0.8, 0.5 );
		
		// Load in the values from config
		load();
		
	}
	
	protected void doLoad( String prefix ) {

		String[] commands = commandline.getStrings( prefix + "tv_grab" );
		commandTextArea.setText( lineBreakise( commands ) );
		
		int daysToDownload = misc.getInt( prefix + "days_to_grab", 7 );
		if( daysToDownload < 7 ) {
			daysComboBox.setSelectedIndex( daysToDownload - 1 );
		} else if( daysToDownload < 14 ) {
			daysComboBox.setSelectedIndex( 6 );
		} else {
			daysComboBox.setSelectedIndex( 7 );
		}
	
		boolean startToday = misc.getBoolean( prefix + "grabber_start_today",
			true );
		if( startToday ) {
			startTodayComboBox.setSelectedIndex( 0 );
		} else {
			startTodayComboBox.setSelectedIndex( 1 );
		}
	
		dayStartTextField.setText( misc.get( prefix + "grabber_start_time",
			"06:00" ) );
	
		todayOffsetTextField.setText( misc.get( prefix
			+ "grabber_today_offset" ) );
		
		boolean modalExecutor = screen.getBoolean( prefix + "executor_modal",
			true );
		if( modalExecutor ) {
			modalComboBox.setSelectedIndex( 1 );
		} else {
			modalComboBox.setSelectedIndex( 0 );
		}
		
		String[] configs = commandline.getStrings( prefix + "tv_config" );
		configTextArea.setText( lineBreakise( configs ) );
		
	}
	
	
	
	/**
	 * Saves the values in this option pane.
	 *
	 * @return false always since these options don't affect the screen display.
	 */
	public boolean doSave() {

		commandline.putStrings( "tv_grab", unlineBreakise(
			commandTextArea.getText() ) );
		
		int daysToDownload;
		int selectedIndex = daysComboBox.getSelectedIndex();
		switch( selectedIndex ) {
			case 7:
				daysToDownload = 14;
				break;
			case 6:
				daysToDownload = 7;
				break;
			default:
				daysToDownload = selectedIndex + 1;
				break;
		}
		misc.putInt( "days_to_grab", daysToDownload );
		
		if( startTodayComboBox.getSelectedIndex() == 0 ) {
			misc.putBoolean( "grabber_start_today", true );
		} else {
			misc.putBoolean( "grabber_start_today", false );
		}
		
		misc.putTime( "grabber_start_time", new Time(
			dayStartTextField.getText() ) );
		
		misc.putInt( "grabber_today_offset", Integer.parseInt(
			todayOffsetTextField.getText() ) );
		
		if( modalComboBox.getSelectedIndex() == 0 ) {
			screen.putBoolean( "executor_modal", false );
		} else {
			screen.putBoolean( "executor_modal", true );
		}
		
		commandline.putStrings( "tv_config", unlineBreakise(
			configTextArea.getText() ) );
		
		// Return value is false since none of these options alter the screen
		// appearance.
		return false;
		
	}
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Downloading";
		
	}

	// ----------------------------------
	
	private JTextArea commandTextArea;
	private JTextArea configTextArea;
	private JComboBox daysComboBox;
	private JComboBox startTodayComboBox;
	private JTextField dayStartTextField;
	private JTextField todayOffsetTextField;
	private JComboBox modalComboBox;	
	
}
