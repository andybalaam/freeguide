/**
 * FreeGuide J2
 *
 * Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
 
/**
 * A JPanel to go on a FreeGuideWizard to choose a boolean option with a
 * checkbox.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideBooleanWizardPanel extends FreeGuideWizardPanel {
	
	FreeGuideBooleanWizardPanel() {
		super();
	}
	
	/**
	 * Construct the GUI of this Wizard Panel.
	 */
	public void construct() {

        java.awt.GridBagConstraints	gridBagConstraints;
		
		JPanel midPanel = new JPanel();
		JLabel topLabel = new JLabel();
        JLabel bottomLabel = new JLabel();
		JButton butBrowse = new JButton();
		
		checkbox = new JCheckBox();

        setLayout(new java.awt.GridLayout(3, 0));

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        //topLabel.setText(topMessage);
        add(topLabel);

		midPanel.setLayout(new java.awt.GridBagLayout());
		
		// Make the Guess button if required
		if(configEntry!=null) {
			JButton butGuess = new JButton();
			butGuess.setFont(new java.awt.Font("Dialog", 0, 12));
			butGuess.setText("Guess");
			butGuess.setToolTipText("Ask FreeGuide to guess this value for you.");
			butGuess.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					guess();
				}
			});

			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = 0;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
			midPanel.add(butGuess, gridBagConstraints);
		}
		
		//checkbox.setMinimumSize(new java.awt.Dimension(4, 26));
        //checkbox.setPreferredSize(new java.awt.Dimension(69, 26));
		
		//for(int i=0;i<choices.length;i++) {
		//	combobox.addItem(choices[i]);
		//}
		
		checkbox.setText( topMessage );
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		
        midPanel.add(checkbox, gridBagConstraints);

        add(midPanel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }

	// --------------------------------------------
	
	protected void saveToPrefs(FreeGuidePreferences pref) {
		pref.putBoolean( configEntry, ((Boolean)getBoxValue()).booleanValue() );
	}
	
	protected void loadFromPrefs(FreeGuidePreferences pref) {
		setBoxValue( new Boolean( pref.getBoolean(configEntry, true ) ) );
	}
	
	// --------------------------------------------
	
	protected Object getBoxValue() {
		return new Boolean( checkbox.isSelected() );
	}
	
	protected void setBoxValue(Object val) {
		checkbox.setSelected( ((Boolean)val).booleanValue() );
	}
	
	// -------------------------------------------

	private JCheckBox checkbox;		// The checkbox for the choice

}

