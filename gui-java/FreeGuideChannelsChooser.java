/*
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

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JTextField;

/**
 * FreeGuideChannelsChooser
 *
 * The channels chooser screen for FreeGuide
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideChannelsChooser extends javax.swing.JFrame {
	
	public FreeGuideChannelsChooser(FreeGuideLauncher launcher) {
		this.launcher = launcher;
		initComponents();
		initChannels();
	}
	
	private void initChannels() {
		
		panChannels.removeAll();
		
		java.awt.GridBagConstraints gridBagConstraints;
		JCheckBox box;
		
		String[] channelIDs = FreeGuide.prefs.getAllChannelIDs();
		
		int i;
		for(i=0;i<channelIDs.length;i++) {
			
			// If it's commented out, non-ticked box, otherwise ticked
			String thisChan = channelIDs[i];
			if(thisChan.charAt(0)=='#') {
				box = new JCheckBox(thisChan.substring(1), false);
			} else {
				box = new JCheckBox(thisChan, !FreeGuide.prefs.channels.get(thisChan, "").equals("#"));
			}
			
			gridBagConstraints = new java.awt.GridBagConstraints();
			gridBagConstraints.gridx = 0;
			gridBagConstraints.gridy = i;
			gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
			panChannels.add(box, gridBagConstraints);
			
		}
		
		/*butEditChannels = new JButton("Refresh Channels");
		butEditChannels.setEnabled(false);
		gridBagConstraints = new java.awt.GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = i;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
		gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
		panChannels.add(butEditChannels, gridBagConstraints);*/
		
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        scrChannels = new javax.swing.JScrollPane();
        panChannels = new javax.swing.JPanel();
        panButtons = new javax.swing.JPanel();
        butOK = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setTitle("FreeGuide Channels");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        panChannels.setLayout(new java.awt.GridBagLayout());

        scrChannels.setViewportView(panChannels);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        getContentPane().add(scrChannels, gridBagConstraints);

        panButtons.setLayout(new java.awt.GridBagLayout());

        butOK.setText("OK");
        butOK.setMinimumSize(new java.awt.Dimension(88, 26));
        butOK.setPreferredSize(new java.awt.Dimension(88, 26));
        butOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOKActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        panButtons.add(butOK, gridBagConstraints);

        butCancel.setText("Cancel");
        butCancel.setMinimumSize(new java.awt.Dimension(88, 26));
        butCancel.setPreferredSize(new java.awt.Dimension(88, 26));
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        panButtons.add(butCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(panButtons, gridBagConstraints);

        pack();
    }//GEN-END:initComponents

	private void butOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOKActionPerformed
		saveChannels();
		quit();
	}//GEN-LAST:event_butOKActionPerformed

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		quit();
	}//GEN-LAST:event_butCancelActionPerformed
	
	private void exitForm(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_exitForm
		quit();
	}//GEN-LAST:event_exitForm

	private void saveChannels() {
		
		Component[] chks = panChannels.getComponents();
		
		Vector channelIDs = new Vector();
		Vector commented = new Vector();
		
		for(int i=0;i<chks.length;i++) {
			
			if(chks[i] instanceof JCheckBox) {
			
				JCheckBox bx = (JCheckBox)chks[i];
			
				channelIDs.add(bx.getText());
			
				if(bx.isSelected()) {
					commented.add("");
				} else {
					commented.add("#");
				}
			
			}//for
		
		}
			
		FreeGuide.prefs.putAllChannelIDs(FreeGuideUtils.arrayFromVector_String(channelIDs), FreeGuideUtils.arrayFromVector_String(commented));
		
	}
	
	/** 
	 * Closes the form and goes back to the viewer.
	 */
	private void quit() {
		
		hide();
		launcher.reShow();
		dispose();

	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panButtons;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butCancel;
    private javax.swing.JScrollPane scrChannels;
    private javax.swing.JPanel panChannels;
    // End of variables declaration//GEN-END:variables
	
	private FreeGuideLauncher launcher;
	
}
