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

import java.awt.Component;
import java.io.File;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 * A class to produce a wizard interface.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideWizard extends javax.swing.JFrame {
	
	
	// The user passes in a title and an array of FreeGuideWizardPanels
	public FreeGuideWizard(String title, FreeGuideWizardPanel[] panels, FreeGuideLauncher launcher) {
		
		this.launcher = launcher;
		this.panels = panels;
		
		panelCounter=0;
		
		initComponents(title);
		
	}
	
    private void initComponents(String title) {
        java.awt.GridBagConstraints gridBagConstraints;

		// Set up the panels ready to be used
		for(int i=0;i<panels.length;i++) {
			panels[i].construct();
		}
		
        panButtons = new javax.swing.JPanel();
        butCancel = new javax.swing.JButton();
        butBack = new javax.swing.JButton();
        butNext = new javax.swing.JButton();
        butFinish = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle(title);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        butCancel.setFont(new java.awt.Font("Dialog", 0, 12));
        butCancel.setText("Exit");
        butCancel.setMaximumSize(new java.awt.Dimension(85, 26));
        butCancel.setMinimumSize(new java.awt.Dimension(85, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(85, 26));
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        panButtons.add(butCancel);

        butBack.setFont(new java.awt.Font("Dialog", 0, 12));
        butBack.setText("<< Back");
        butBack.setEnabled(false);
        butBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBackActionPerformed(evt);
            }
        });

        panButtons.add(butBack);

        butNext.setFont(new java.awt.Font("Dialog", 0, 12));
        butNext.setText("Next >>");
        butNext.addActionListener(new java.awt.event.ActionListener() {
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
        butFinish.addActionListener(new java.awt.event.ActionListener() {
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

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {
		quit();
	}

	private void butFinishActionPerformed(java.awt.event.ActionEvent evt) {
		quit();
	}
	
	private void exitForm(java.awt.event.WindowEvent evt) {
		quit();	
	}

	private void butBackActionPerformed(java.awt.event.ActionEvent evt) {
		
		// Save the info on this panel and check we're allowed to leave it
		if(panelCounter>0 && panels[panelCounter].onExit()) {
			
			// Go to the previous panel
			panelCounter--;
			displayPanel(panels[panelCounter]);
			
		}
		
	}
	
	private void butNextActionPerformed(java.awt.event.ActionEvent evt) {
		
		// Save the info on this panel and check we're allowed to leave it
		if(panelCounter<panels.length && panels[panelCounter].onExit()) {

			// Go to the next panel
			panelCounter++;			
			displayPanel(panels[panelCounter]);

		}
		
	}

	private void displayPanel(FreeGuideWizardPanel newPanel) {
		
		java.awt.GridBagConstraints gridBagConstraints;
		
		java.awt.Container contentPane = getContentPane();
		
		// Perform any operations required when entering it
		newPanel.onEnter();
		
		// Remove the old panel
		if(contentPane.getComponentCount()>1) {
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
        setLocation((screenSize.width-500)/2,(screenSize.height-350)/2);
		
		newPanel.revalidate();
		newPanel.repaint();
		
	}
	
	private void refreshButtons() {
		
		if(panelCounter==0) {
			butBack.setEnabled(false);
			butNext.setEnabled(true);
			butFinish.setEnabled(false);
		} else if( panelCounter==panels.length-1 ) {
			butBack.setEnabled(true);
			butNext.setEnabled(false);
			butFinish.setEnabled(true);
		} else {
			butBack.setEnabled(true);
			butNext.setEnabled(true);
			butFinish.setEnabled(false);
		}
		
	}

	
	private void quit() {
		setVisible(false);
		if(launcher!=null) {
			launcher.reShow();
		}
		dispose();
	}
	
	
	private FreeGuideWizardPanel[] panels;
	
	private javax.swing.JPanel panButtons;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butNext;
	private javax.swing.JButton butBack;
	private javax.swing.JButton butFinish;
    
	private FreeGuideLauncher launcher;
	private int panelCounter;
	
}
