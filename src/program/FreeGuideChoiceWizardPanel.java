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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
 
/**
 * A JPanel to go on a FreeGuideWizard to choose from a list
 * of choices in a combobox.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideChoiceWizardPanel extends FreeGuideWizardPanel {
	
	/**
	 * Create a new FreeGuideChoiceWizardPanel.
	 */
	FreeGuideChoiceWizardPanel(String[] choices) {
		super();
		this.choices = choices;
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
		
		combobox = new JComboBox();

        setLayout(new java.awt.GridLayout(3, 0));

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText(topMessage);
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
		
		combobox.setMinimumSize(new java.awt.Dimension(4, 26));
        combobox.setPreferredSize(new java.awt.Dimension(69, 26));
		
		for(int i=0;i<choices.length;i++) {
			combobox.addItem(choices[i]);
		}
		
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

    }

	// --------------------------------------------
	
	protected void saveToPrefs(FreeGuidePreferences pref) {
		pref.put( configEntry, (String)getBoxValue() );
	}
	
	protected void loadFromPrefs(FreeGuidePreferences pref) {
		setBoxValue( pref.get(configEntry) );
	}
	
	// --------------------------------------------
	
	protected Object getBoxValue() {
		return (String)combobox.getSelectedItem();
	}
	
	protected void setBoxValue(Object val) {
		combobox.setSelectedItem(val);
	}
	
	// -------------------------------------------

	private JComboBox combobox;		// The combobox for the choice
	private String[] choices;		// The possible choices

}

