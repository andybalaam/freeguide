
import java.awt.Component;
import java.io.File;
import java.lang.Class;
import java.lang.reflect.Method;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 *  A class to produce a wizard interface.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    1
 */
public class WizardFrame extends javax.swing.JFrame {

    // The user passes in a title and an array of FreeGuideWizardPanels
    /**
     *  Constructor for the FreeGuideWizard object
     *
     *@param  title     Description of the Parameter
     *@param  panels    Description of the Parameter
     */
    public WizardFrame(String title, WizardPanel[] panels) {

        this(title, panels, null, null);

    }


    // The user passes in a title and an array of FreeGuideWizardPanels
    /**
     *  Constructor for the FreeGuideWizard object
     *
     *@param  title         Description of the Parameter
     *@param  panels        Description of the Parameter
     *@param  finishObject  Description of the Parameter
     *@param  finishMethod  Description of the Parameter
     */
    public WizardFrame(String title, WizardPanel[] panels, Object finishObject,
			Method finishMethod) {

        this.panels = panels;
        this.finishObject = finishObject;
        this.finishMethod = finishMethod;

        panelCounter = 0;

        initComponents(title);

    }


    /**
     *  Description of the Method
     *
     *@param  title  Description of the Parameter
     */
    private void initComponents(String title) {
        java.awt.GridBagConstraints gridBagConstraints;

        // Set up the panels ready to be used
        for (int i = 0; i < panels.length; i++) {
            panels[i].construct();
        }

        panButtons = new javax.swing.JPanel();
        butCancel = new javax.swing.JButton();
        butBack = new javax.swing.JButton();
        butNext = new javax.swing.JButton();
        butFinish = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle(title);
        addWindowListener(
            new java.awt.event.WindowAdapter() {
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    exitForm(evt);
                }
            });

        butCancel.setFont(new java.awt.Font("Dialog", 0, 12));
        butCancel.setText("Exit");
        butCancel.setMaximumSize(new java.awt.Dimension(85, 26));
        butCancel.setMinimumSize(new java.awt.Dimension(85, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(85, 26));
        butCancel.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butCancelActionPerformed(evt);
                }
            });

        panButtons.add(butCancel);

        butBack.setFont(new java.awt.Font("Dialog", 0, 12));
        butBack.setText("<< Back");
        butBack.setEnabled(false);
        butBack.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butBackActionPerformed(evt);
                }
            });

        panButtons.add(butBack);

        butNext.setFont(new java.awt.Font("Dialog", 0, 12));
        butNext.setText("Next >>");
        butNext.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butNextActionPerformed(evt);
                }
            });

        panButtons.add(butNext);

        butFinish.setFont(new java.awt.Font("Dialog", 0, 12));
        butFinish.setText("Finish");
        butFinish.setMaximumSize(new java.awt.Dimension(85, 26));
        butFinish.setMinimumSize(new java.awt.Dimension(85, 26));
        butFinish.setPreferredSize(new java.awt.Dimension(85, 26));
        butFinish.setEnabled(false);
        butFinish.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butFinishActionPerformed(evt);
                }
            });

        panButtons.add(butFinish);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(panButtons, gridBagConstraints);

        displayPanel(panels[panelCounter]);

    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {
        quit();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butFinishActionPerformed(java.awt.event.ActionEvent evt) {

		panels[panelCounter].onExit();
		
        if (finishMethod != null) {

            try {

                finishMethod.invoke(finishObject, new Class[0]);

            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            } catch (java.lang.reflect.InvocationTargetException e) {
                e.printStackTrace();
            }

        }

        quit();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void exitForm(java.awt.event.WindowEvent evt) {
        quit();
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butBackActionPerformed(java.awt.event.ActionEvent evt) {

        // Save the info on this panel and check we're allowed to leave it
        if (panelCounter > 0 && panels[panelCounter].onExit()) {

            // Go to the previous panel
            panelCounter--;
            displayPanel(panels[panelCounter]);

        }
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butNextActionPerformed(java.awt.event.ActionEvent evt) {

        // Save the info on this panel and check we're allowed to leave it
        if (panelCounter < panels.length && panels[panelCounter].onExit()) {

            // Go to the next panel
            panelCounter++;
            displayPanel(panels[panelCounter]);

        }
    }


    /**construct
     *  Description of the Method
     *
     *@param  newPanel  Description of the Parameter
     */
    private void displayPanel(WizardPanel newPanel) {

        java.awt.GridBagConstraints gridBagConstraints;

        java.awt.Container contentPane = getContentPane();

        // Perform any operations required when entering it
        newPanel.onEnter();

        // Remove the old panel
        if (contentPane.getComponentCount() > 1) {
            contentPane.remove(1);
        }

        // Set up gridBagConstraints
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;

        // Display the new panel
        getContentPane().add(newPanel, gridBagConstraints);

        refreshButtons();

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(500, 350));
        setLocation((screenSize.width - 500) / 2, (screenSize.height - 350) / 2);

        newPanel.revalidate();
        newPanel.repaint();

    }


    /**
     *  Enable the buttons according to where we are in the wizard: beginning,
	 *  middle, or end.
     */
    private void refreshButtons() {

        if (panelCounter == 0) {	// Beginning
            butBack.setEnabled(false);
            butNext.setEnabled(true);
            butFinish.setEnabled(false);
			getRootPane().setDefaultButton( butNext );
			butNext.requestFocus();
        } else if (panelCounter == panels.length - 1) {	// End
            butBack.setEnabled(true);
            butNext.setEnabled(false);
            butFinish.setEnabled(true);
			getRootPane().setDefaultButton( butFinish );
			butBack.requestFocus();
        } else {	// Middle
            butBack.setEnabled(true);
            butNext.setEnabled(true);
            butFinish.setEnabled(false);
			getRootPane().setDefaultButton( butNext );
			butBack.requestFocus();
        }

    }


    /**
     *  Description of the Method
     */
    private void quit() {
        setVisible(false);
        dispose();
    }


    private WizardPanel[] panels;

    private javax.swing.JPanel panButtons;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butBack;
    private javax.swing.JButton butFinish;

    private int panelCounter;

    private Object finishObject;
    private Method finishMethod;

}
