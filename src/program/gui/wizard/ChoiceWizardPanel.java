
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *  A JPanel to go on a FreeGuideWizard to choose from a list of choices in a
 *  combobox.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    2
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

        setLayout(new java.awt.GridLayout(3, 0));

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText(topMessage);
        add(topLabel);

        midPanel.setLayout(new java.awt.GridBagLayout());

        // Make the Guess button if required
        if (configEntry != null) {
            JButton butGuess = new JButton();
            butGuess.setFont(new java.awt.Font("Dialog", 0, 12));
            butGuess.setText("Guess");
            butGuess.setToolTipText("Ask FreeGuide to guess this value for you.");
            butGuess.addActionListener(
                new java.awt.event.ActionListener() {
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
		combobox.setMaximumRowCount( 20 );

        for (int i = 0; i < choices.length; i++) {
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

	public void setChoices( String[] choices ) {
		
		int length = choices.length;
		
		this.choices = new String[ length ];
		combobox.removeAllItems();
		
		for( int i=0; i<length; i++ ) {
			
			this.choices[i] = choices[i];
			combobox.addItem(choices[i]);
			
		}
		
	}
	
    // --------------------------------------------

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
        return (String) combobox.getSelectedItem();
    }


    /**
     *  Sets the boxValue attribute of the ChoiceWizardPanel object
     *
     *@param  val  The new boxValue value
     */
    protected void setBoxValue(Object val) {
        combobox.setSelectedItem(val);
    }


    // -------------------------------------------

    private JComboBox combobox;
    // The combobox for the choice
    private String[] choices;
    // The possible choices

}

