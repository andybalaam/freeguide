/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */

package freeguide.gui.wizard;

import javax.swing.*;

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
        JLabel middlePane = new JLabel();
        JLabel bottomLabel = new JLabel();

        setLayout(new java.awt.GridLayout(3, 0));

        topLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        topLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        topLabel.setText(topMessage);
        add(topLabel);

        middlePane.setFont(new java.awt.Font("Dialog", 0, 12));
        middlePane.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        middlePane.setText(middleMessage);
        add(middlePane);

        bottomLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        bottomLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        bottomLabel.setText(bottomMessage);
        add(bottomLabel);

    }


    // -------------------------------------------

    private String middleMessage;
    // The message to display

}
