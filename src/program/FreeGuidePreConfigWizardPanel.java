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
import javax.swing.JPanel;
import javax.swing.JButton;
 
/**
 * A JPanel to go on a FreeGuideWizard that allows the user to press a button to
 * launch the pre-config wizard about OS, Country and Browser.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuidePreConfigWizardPanel extends FreeGuideWizardPanel {
	
	/**
	 * Create a new FreeGuideTextWizardPanel.
	 */
	public FreeGuidePreConfigWizardPanel() {
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
		
		button = new JButton();

        setLayout(new java.awt.GridLayout(3, 0));

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText(topMessage);
        add(topLabel);

		midPanel.setLayout(new java.awt.GridBagLayout());
		
		button.setMinimumSize(new java.awt.Dimension(4, 26));
        button.setPreferredSize(new java.awt.Dimension(300, 26));
		button.setFont(new java.awt.Font("Dialog", 0, 12));
        button.setText("Change OS, country or browser");
        button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchPreConfig();
            }
        });
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        //gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		
        midPanel.add(button, gridBagConstraints);

        add(midPanel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }
	
	private void launchPreConfig() {
		
		FreeGuideWizardPanel[] panels = new FreeGuideWizardPanel[5];
		
		panels[0] = new FreeGuideLabelWizardPanel("Here you'll set up some basics so FreeGuide can try");
		panels[0].setMessages("Welcome to the setup wizard.", "to guess the rest of your config options.");
		
		panels[1] = new FreeGuideChoiceWizardPanel(FreeGuideConfigGuesser.guessChoices("misc", "os"));
		panels[1].setMessages("Choose your operating system.", "If you don't know, choose Windows.");
		panels[1].setConfig("misc", "os");
		
		panels[2] = new FreeGuideChoiceWizardPanel(FreeGuideConfigGuesser.guessChoices("misc", "country"));
		panels[2].setMessages("Choose your region.", "");
		panels[2].setConfig("misc", "country");
		
		panels[3] = new FreeGuideChoiceWizardPanel(FreeGuideConfigGuesser.guessChoices("misc", "browser_name"));
		panels[3].setMessages("Choose your browser.", "If you don't know, choose Internet Explorer.");
		panels[3].setConfig("misc", "browser_name");
		
		panels[4] = new FreeGuideLabelWizardPanel("then click \"Guess\" next to any options you think are wrong,");
		panels[4].setMessages("OK, that's done - click \"Finish\"", "and FreeGuide will try and fix them.");
		
		new FreeGuideWizard("FreeGuide Setup Wizard", panels).setVisible(true);
		
	}
	
	// -------------------------------------------
	
	private JButton button;
		// The textfield that is the box on this panel
		
}
