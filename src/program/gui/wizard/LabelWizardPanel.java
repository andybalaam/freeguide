
import javax.swing.JLabel;

/**
 *  A JPanel to go on a FreeGuideWizard that just informs the user of something
 *  using 3 labels.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    1
 */
public class LabelWizardPanel extends WizardPanel {

    /**
     *  Create a new FreeGuideLabelWizardPanel.
     *
     *@param  middleMessage  Description of the Parameter
     */
    public LabelWizardPanel(String middleMessage) {
        super();
        this.middleMessage = middleMessage;
    }


    /**
     *  Construct the GUI of this Wizard Panel.
     */
    public void construct() {

        java.awt.GridBagConstraints gridBagConstraints;

        JLabel topLabel = new JLabel();
        JLabel middleLabel = new JLabel();
        JLabel bottomLabel = new JLabel();

        setLayout(new java.awt.GridLayout(3, 0));

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText(topMessage);
        add(topLabel);

        middleLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        middleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        middleLabel.setText(middleMessage);
        add(middleLabel);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }


    // -------------------------------------------

    private String middleMessage;
    // The message to display

}
