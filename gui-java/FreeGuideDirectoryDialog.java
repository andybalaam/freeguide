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

import java.io.File;
import javax.swing.JFileChooser;

/*
 * FreeGuideDirectoryDialog
 *
 * Provides a standard dialog for choosing a directory, strongly biasing
 * the user towards choosing a suggested one.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideDirectoryDialog extends javax.swing.JDialog {
	
	/** Creates new form FreeGuideDirectoryDialog */
	public FreeGuideDirectoryDialog(java.awt.Frame parent, String msg, File suggested) {
		super(parent, true);
		initComponents();
		
		// Show the given message
		labMsg.setText(msg);
		
		// Set the filename to the right thing
		txtFilename.setText(suggested.getAbsolutePath());
		
		setVisible(true);
		
	}
	
	// ------------------------------------------------------------------------
	
	public File getDirectory() {
		return new File(txtFilename.getText());
	}
	
	public boolean okClicked() {
		return okclicked;
	}
	public boolean cancelClicked() {
		return cancelclicked;
	}
	public boolean quitClicked() {
		return quitclicked;
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        txtFilename = new javax.swing.JTextField();
        panOKCancel = new javax.swing.JPanel();
        butOK = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();
        butBrowse = new javax.swing.JButton();
        butQuit = new javax.swing.JButton();
        labMsg = new javax.swing.JTextArea();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Choose Directory");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        txtFilename.setMinimumSize(new java.awt.Dimension(4, 24));
        txtFilename.setPreferredSize(new java.awt.Dimension(69, 24));
        txtFilename.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                txtFilenameInputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        getContentPane().add(txtFilename, gridBagConstraints);

        butOK.setFont(new java.awt.Font("Dialog", 0, 12));
        butOK.setText("OK");
        butOK.setMaximumSize(new java.awt.Dimension(82, 24));
        butOK.setMinimumSize(new java.awt.Dimension(82, 24));
        butOK.setPreferredSize(new java.awt.Dimension(82, 24));
        butOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butOKActionPerformed(evt);
            }
        });

        panOKCancel.add(butOK);

        butCancel.setFont(new java.awt.Font("Dialog", 0, 12));
        butCancel.setText("Cancel");
        butCancel.setMaximumSize(new java.awt.Dimension(82, 24));
        butCancel.setMinimumSize(new java.awt.Dimension(82, 24));
        butCancel.setPreferredSize(new java.awt.Dimension(82, 24));
        butCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butCancelActionPerformed(evt);
            }
        });

        panOKCancel.add(butCancel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(panOKCancel, gridBagConstraints);

        butBrowse.setFont(new java.awt.Font("Dialog", 0, 12));
        butBrowse.setText("Browse...");
        butBrowse.setMaximumSize(new java.awt.Dimension(100, 24));
        butBrowse.setMinimumSize(new java.awt.Dimension(100, 24));
        butBrowse.setPreferredSize(new java.awt.Dimension(100, 24));
        butBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butBrowseActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        getContentPane().add(butBrowse, gridBagConstraints);

        butQuit.setFont(new java.awt.Font("Dialog", 0, 12));
        butQuit.setText("Quit");
        butQuit.setMaximumSize(new java.awt.Dimension(82, 24));
        butQuit.setMinimumSize(new java.awt.Dimension(82, 24));
        butQuit.setPreferredSize(new java.awt.Dimension(82, 24));
        butQuit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                butQuitActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(butQuit, gridBagConstraints);

        labMsg.setBackground(new java.awt.Color(204, 204, 204));
        labMsg.setEditable(false);
        labMsg.setText("Please choose a directory:");
        labMsg.setWrapStyleWord(true);
        labMsg.setPreferredSize(new java.awt.Dimension(1000, 1000));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weighty = 0.9;
        getContentPane().add(labMsg, gridBagConstraints);

        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(500, 250));
        setLocation((screenSize.width-500)/2,(screenSize.height-250)/2);
    }//GEN-END:initComponents

	private void butCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butCancelActionPerformed
		cancel();
	}//GEN-LAST:event_butCancelActionPerformed

	private void butOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butOKActionPerformed
		ok();
	}//GEN-LAST:event_butOKActionPerformed

	private void butQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butQuitActionPerformed
		quit();
	}//GEN-LAST:event_butQuitActionPerformed

	private void butBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_butBrowseActionPerformed
		browse();
	}//GEN-LAST:event_butBrowseActionPerformed

	private void txtFilenameInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_txtFilenameInputMethodTextChanged
		
		if(txtFilename.getText().equals("")) {
			
			butOK.setEnabled(false);
			
		} else {
			
			butOK.setEnabled(true);
			
		}
		
	}//GEN-LAST:event_txtFilenameInputMethodTextChanged
	
	/** Closing has the same effect as clicking Cancel */
	private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
		cancel();
	}//GEN-LAST:event_closeDialog
	
	private void cancel() {
		cancelclicked = true;
		setVisible(false);
		dispose();
	}
	private void ok() {
		okclicked = true;
		setVisible(false);
		dispose();
	}
	private void quit() {
		quitclicked = true;
		setVisible(false);
		dispose();
	}
	
	private void browse() {
		
		JFileChooser chooser = new JFileChooser();
    
		chooser.setCurrentDirectory( new File(txtFilename.getText()) );
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		
		int returnVal = chooser.showDialog(this, "Choose Directory");
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			
			txtFilename.setText(chooser.getSelectedFile().getAbsolutePath());
			
		}

	}
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea labMsg;
    private javax.swing.JButton butQuit;
    private javax.swing.JButton butBrowse;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butCancel;
    private javax.swing.JTextField txtFilename;
    private javax.swing.JPanel panOKCancel;
    // End of variables declaration//GEN-END:variables
	
	private boolean okclicked;
	private boolean cancelclicked;
	private boolean quitclicked;
	
}