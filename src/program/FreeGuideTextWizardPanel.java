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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
/**
 * A JPanel to go on a FreeGuideWizard that allows entry of a text value.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideTextWizardPanel extends FreeGuideWizardPanel {
	
	/**
	 * Create a new FreeGuideTextWizardPanel.
	 */
	public FreeGuideTextWizardPanel() {
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
		
		textfield = new JTextField();

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
			butGuess.setText("Default");
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
		
		textfield.setMinimumSize(new java.awt.Dimension(4, 26));
        textfield.setPreferredSize(new java.awt.Dimension(69, 26));
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		
        midPanel.add(textfield, gridBagConstraints);

        add(midPanel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }
	
	// -------------------------------------------
	
	protected void saveToPrefs(FreeGuidePreferences pref) {
		pref.put( configEntry, (String)getBoxValue() );
	}
	
	protected void loadFromPrefs(FreeGuidePreferences pref) {
		setBoxValue( pref.get(configEntry) );
	}
	
	// -----------------------------------
	
	protected Object getBoxValue() {
		return textfield.getText();
	}
	
	protected void setBoxValue(Object val) {
		textfield.setText((String)val);
	}
	
	// -------------------------------------------
	
	private JTextField textfield;
		// The textfield that is the box on this panel

}
