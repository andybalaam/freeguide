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
 
import java.io.File;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
 
/**
 * A JPanel to go on a FreeGuideWizard that alowws you to choose a file.
 *
 * @author  Andy Balaam
 * @version 1
 */
public abstract class FreeGuideAbstractFileWizardPanel extends FreeGuideWizardPanel {
	
	/**
	 * Create a new FreeGuideFileWizardPanel.
	 */
	FreeGuideAbstractFileWizardPanel() {
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
		
		textfield.setMinimumSize(new java.awt.Dimension(4, 26));
        textfield.setPreferredSize(new java.awt.Dimension(69, 26));
		
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		
        midPanel.add(textfield, gridBagConstraints);

		// Make the browse button
        butBrowse.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowse.setText("Browse...");
        butBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browse();
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        midPanel.add(butBrowse, gridBagConstraints);
		
        add(midPanel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }
	
	private void browse() {
		
		JFileChooser chooser = new JFileChooser();
    
		chooser.setCurrentDirectory( new File( textfield.getText() ) );
		
		int returnVal;

		chooser.setFileSelectionMode( this.getFileSelectionMode() );
		returnVal = chooser.showDialog(this, this.getFileChooserMessage());
		
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			textfield.setText(chooser.getSelectedFile().getAbsolutePath());
		}

	}
	
	protected abstract int getFileSelectionMode();
	
	protected abstract String getFileChooserMessage();
	
	// -----------------------------------
	
	protected void saveToPrefs(FreeGuidePreferences pref) {
		pref.put( configEntry, ((File)getBoxValue()).getPath() );
	}
	
	protected void loadFromPrefs(FreeGuidePreferences pref) {
		setBoxValue( new File(pref.get(configEntry)) );
	}
	
	// -----------------------------------
	
	protected Object getBoxValue() {
		return new File( textfield.getText() );
	}
	
	protected void setBoxValue(Object val) {
		textfield.setText(((File)val).getPath());
	}
	
	// ------------------------------
	
	private JTextField textfield;	// The textfield for dir, file and text panels	
	
}
