/**
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
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
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
 
/**
 * A JPanel containing JTextArea for system commands to go on a FreeGuideWizard.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideCommandsWizardPanel extends FreeGuideWizardPanel {
	
	/**
	 * Create a new FreeGuideCommandsWizardPanel.
	 */
	public FreeGuideCommandsWizardPanel() {
		super();
	}
	
	public void construct() {

		java.awt.GridBagConstraints gridBagConstraints;
		
        JLabel topLabel = new JLabel();
        JLabel bottomLabel = new JLabel();
		JPanel midPanel = new JPanel();
		JScrollPane midScrollPane = new JScrollPane();
		textarea = new JTextArea();

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
			gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
			gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
			midPanel.add(butGuess, gridBagConstraints);
		}
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		
		midScrollPane.setViewportView(textarea);
		midScrollPane.setPreferredSize(new java.awt.Dimension(300, 50));
        midPanel.add(midScrollPane, gridBagConstraints);
		
		add(midPanel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }
	
	// -------------------------------------------
	
	protected void saveToPrefs(FreeGuidePreferences pref) {
		pref.putStrings( configEntry, (String[])getBoxValue() );
	}
	
	protected void loadFromPrefs(FreeGuidePreferences pref) {
		setBoxValue(pref.getStrings(configEntry));
	}
	
	// ------------------------------------------
	
	protected Object getBoxValue() {
		return textarea.getText().split(System.getProperty("line.separator"));
	}
	
	protected void setBoxValue(Object val) {
		String[] vals = (String[])val;
		String lb = System.getProperty("line.separator");
		String ans = new String();
		for(int i=0;i<vals.length;i++) {
			ans += vals[i] + lb;
		}
		textarea.setText(ans);
	}
	
	// -------------------------------------------
	
	private JTextArea textarea;		// The textarea for commandline panels
	
}
