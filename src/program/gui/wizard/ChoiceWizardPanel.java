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
import javax.swing.*;

/**
 *  A JPanel to go on a WizardFrame to choose from a list of choices in a
 *  combobox.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    3
 */
public class ChoiceWizardPanel extends WizardPanel {

    /**
     *  Create a new FreeGuideChoiceWizardPanel.
     *
     *@param  choices  Description of the Parameter
     */
    ChoiceWizardPanel(String[] choices) {
        super();
        this.choices = choices;
    }


    /**
     *  Construct the GUI of this Wizard Panel.
     */
    public void construct() {

        java.awt.GridBagConstraints gridBagConstraints;

        JPanel midPanel = new JPanel();
        JLabel topLabel = new JLabel();
        JLabel bottomLabel = new JLabel();

        combobox = new JComboBox();

        setLayout( new GridLayout(3, 0) );

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText(topMessage);
		topLabel.setDisplayedMnemonic(topMnemonic);
        add(topLabel);

        midPanel.setLayout(new java.awt.GridBagLayout());

        combobox.setMinimumSize(new java.awt.Dimension(4, 26));
        combobox.setPreferredSize(new java.awt.Dimension(69, 26));
		combobox.setMaximumRowCount( 20 );

        updateChoices();
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);

        midPanel.add(combobox, gridBagConstraints);

        add(midPanel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

		topLabel.setLabelFor(combobox);
		
    }
	
    // --------------------------------------------

	/**
	 * Change the choices available in the combobox
	 *
	 * @param choices the new choices in an array of String.
	 */
	 public void setChoices( String[] choices ) {
		 
		 this.choices = choices;
		 updateChoices();
		 
	 }
	 
	 /**
	  * Make the combobox reflect the choices available.
	  */
	 private void updateChoices() {
		 
		 combobox.removeAllItems();
		 
		 for (int i = 0; i < choices.length; i++) {
            combobox.addItem(choices[i]);
        }
		
		/*if( choices.length > 0 ) {
			combobox.setSelectedItem( choices[0] );
			System.err.println( choices[0] );
		}*/
		 
	 }
	
    /**
     *  Description of the Method
     *
     *@param  pref  Description of the Parameter
     */
    protected void saveToPrefs(FGPreferences pref) {
        pref.put(configEntry, (String) getBoxValue());
    }


    /**
     *  Description of the Method
     *
     *@param  pref  Description of the Parameter
     */
    protected void loadFromPrefs(FGPreferences pref) {
        setBoxValue(pref.get(configEntry));
    }


    // --------------------------------------------

    /**
     *  Gets the boxValue attribute of the ChoiceWizardPanel object
     *
     *@return    The boxValue value
     */
    protected Object getBoxValue() {
        return (String)combobox.getSelectedItem();
    }


    /**
     *  Sets the boxValue attribute of the ChoiceWizardPanel object
     *
     *@param  val  The new boxValue value
     */
    protected void setBoxValue(Object val) {
		if( val != null ) {
			combobox.setSelectedItem(val);
		}
    }


    // -------------------------------------------

    private JComboBox combobox;
    // The combobox for the choice
    private String[] choices;
    // The possible choices

}

