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
 *  A panel full of options about the screen layout in FreeGuide
 *
 * @author     Andy Balaam
 * @created    11 Dec 2003
 * @version    1
 */

public class ColoursOptionPanel extends OptionPanel implements MouseListener,
		ActionListener {

	public ColoursOptionPanel( FGDialog parent ) {
		super( parent );
	}
			
	public void doConstruct() {
		
		// Make the objects
		
		normalButton = newLeftJButton( "Normal prog. colour:" );
		normalTextField = newRightJTextField();
		normalTextField.setEditable(false);
		normalTextField.setFocusable(false);
		normalButton.setMnemonic(KeyEvent.VK_N);
		
		chosenButton = newLeftJButton( "Chosen prog. colour:" );
		chosenTextField = newRightJTextField();
		chosenTextField.setEditable(false);
		chosenTextField.setFocusable(false);
		chosenButton.setMnemonic(KeyEvent.VK_O);
		
		movieButton = newLeftJButton( "Movie colour:" );
		movieTextField = newRightJTextField();
		movieTextField.setEditable(false);
		movieTextField.setFocusable(false);
		movieButton.setMnemonic(KeyEvent.VK_M);
		
		channelButton = newLeftJButton( "Channel colour:" );
		channelTextField = newRightJTextField();
		channelTextField.setEditable(false);
		channelTextField.setFocusable(false);
		channelButton.setMnemonic(KeyEvent.VK_A);
		
		heartButton = newLeftJButton( "Heart colour:" );
		heartTextField = newRightJTextField();
		heartTextField.setEditable(false);
		heartTextField.setFocusable(false);
		heartButton.setMnemonic(KeyEvent.VK_H);
		
		// Lay them out in a GridBag layout
		
		GridBagEasy gbe = new GridBagEasy( this );
		
		gbe.default_insets = new Insets( 1, 1, 1, 1 );
		gbe.default_ipadx = 5;
		gbe.default_ipady = 5;
		
		gbe.addFWX  ( normalButton        , 0, 0, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( normalTextField    , 1, 0, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( chosenButton        , 0, 1, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( chosenTextField    , 1, 1, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( movieButton         , 0, 2, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( movieTextField     , 1, 2, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( channelButton       , 0, 3, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( channelTextField   , 1, 3, gbe.FILL_HOR   , 0.8 );
		
		gbe.addFWX  ( heartButton         , 0, 4, gbe.FILL_HOR   , 0.2 );
		gbe.addFWX  ( heartTextField     , 1, 4, gbe.FILL_HOR   , 0.8 );
		
		// Set up events
		normalTextField.addMouseListener(this);
		chosenTextField.addMouseListener(this);
		movieTextField.addMouseListener(this);
		channelTextField.addMouseListener(this);
		heartTextField.addMouseListener(this);
		
		normalButton.addActionListener(this);
		chosenButton.addActionListener(this);
		movieButton.addActionListener(this);
		channelButton.addActionListener(this);
		heartButton.addActionListener(this);
		
		// Load in the values from config
		load();
		
	}
		
	protected void doLoad( String prefix ) {
		
		normalTextField.setBackground( screen.getColor(
			prefix + "programme_normal_colour",
			FreeGuide.PROGRAMME_NORMAL_COLOUR ) );
		
		chosenTextField.setBackground( screen.getColor(
			prefix + "programme_chosen_colour",
			FreeGuide.PROGRAMME_CHOSEN_COLOUR ) );
			
		movieTextField.setBackground( screen.getColor(
			prefix + "programme_movie_colour",
			FreeGuide.PROGRAMME_MOVIE_COLOUR ) );
		
		channelTextField.setBackground( screen.getColor(
			prefix + "channel_colour",
			FreeGuide.CHANNEL_COLOUR ) );
			
		heartTextField.setBackground( screen.getColor(
			prefix + "programme_heart_colour",
			FreeGuide.PROGRAMME_HEART_COLOUR ) );
		
	}
	
	public boolean doSave() {

		boolean updated = false;
		
		updated = screen.updateColor( "programme_normal_colour",
				normalTextField.getBackground() ) || updated;
		
		updated = screen.updateColor( "programme_chosen_colour",
				chosenTextField.getBackground() ) || updated;
		
		updated = screen.updateColor( "programme_movie_colour",
				movieTextField.getBackground() ) || updated;
				
		updated = screen.updateColor( "channel_colour",
				channelTextField.getBackground() ) || updated;
				
		updated = screen.updateColor( "programme_heart_colour",
				heartTextField.getBackground() ) || updated;
		
		return updated;
		
	}
	
	/**
	 * Used to find the name of this panel when displayed in a JTree.
	 */
	public String toString() {
		
		return "Colours";
		
	}
	
	// ------------------------------------
	// Event handlers
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	
	public void mouseClicked(MouseEvent e) {
		
		JTextField source = (JTextField)e.getSource();
		
		Color col = JColorChooser.showDialog( this, "Choose a Colour",
			source.getBackground() );
		
        if (col != null) {
            source.setBackground( col );
        }
	}

	public void actionPerformed(ActionEvent e) {
		
		JButton source = (JButton)e.getSource();
		
		JTextField textField = null;
		
		if( source == normalButton ) {
			textField = normalTextField;
		} else if( source == chosenButton ) {
			textField = chosenTextField;
		} else if( source == movieButton ) {
			textField = movieTextField;
		} else if( source == channelButton ) {
			textField = channelTextField;
		} else if( source == heartButton ) {
			textField = heartTextField;
		}
		
		Color col = JColorChooser.showDialog( this, "Choose a Colour",
			textField.getBackground() );
		
        if (col != null) {
			textField.setBackground( col );
        }
		
	}
	
	// ----------------------------------

	private JTextField normalTextField;
	private JTextField chosenTextField;
	private JTextField movieTextField;
	private JTextField channelTextField;
	private JTextField heartTextField;
	
	private JButton normalButton;
	private JButton chosenButton;
	private JButton movieButton;
	private JButton channelButton;
	private JButton heartButton;
	
}
