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
package freeguide.gui.dialogs;

import freeguide.*;
import freeguide.gui.viewer.*;
import freeguide.lib.fgspecific.*;
import freeguide.lib.general.*;
import java.util.*;
import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 *  Provides a list of the user's channelsets and allows them to add or edit
 *  them by launching a FreeGuideChannelSetEditor.
 *
 * @author     Brendan Corrigan (based on FreeGuideChannelSetList by Andy
 *             Balaam)
 * @created    22nd August 2003
 * @version    2
 */


public class ChannelSetListDialog extends FGDialog {


    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton butEdit;
    private javax.swing.JButton butRemove;
    private javax.swing.JButton butAdd;
    private javax.swing.JList list;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butCancel;
    private javax.swing.JLabel jLabel1;

    private Vector channelsets;
    	// The channelsets
	
    private ViewerFrameXMLTVLoader loader;

    private DefaultListModel channelsetModel;
    private int latestIndex;

     
    /**
     * Constructor which sets the channel set list up as a JDialog...
     *
     * Note: This class takes the ViewerFrameXMLTVLoader as a parameter to keep
     * the structure of the application intact. This would probably be better refactored
     * as a static reference object such as the singleton pattern - on the "todo" list!
     *               
     *@param owner - the <code>JFrame</code> from which the dialog is displayed 
     *@param title - the <code>String</code> to display in the dialog's title bar
     *@param loader - the <code>ViewerFrameXMLTVLoader</code> for compatibility
     */
                    
    public ChannelSetListDialog(JFrame owner, ViewerFrameXMLTVLoader loader) {
        super(owner, "Channel Sets");


        this.loader = loader;
        channelsetModel = new DefaultListModel();

        initComponents();
        loadChannelSet();
        fillList();

        latestIndex = 0;
        selectLatest();

    }


    /**
     *  todo - Description of the Method
     */
    private void selectLatest() {

        list.setSelectedIndex(latestIndex);

    }


    /**
     *  todo - Description of the Method
     */
    private void loadChannelSet() {

        channelsets = new Vector(
			Arrays.asList( FreeGuide.prefs.getChannelSets() ) );
	
    }


    /**
     *  todo - Description of the Method
     */
    private void fillList() {

        channelsetModel.removeAllElements();
        //DMT: iterate over keys()
        for (int i = 0; i < channelsets.size(); i++) {
            channelsetModel.addElement(((ChannelSetInterface) channelsets.get(i)).getChannelSetName());
        }

        //list = new javax.swing.JList(titles);

    }


    /**
     * todo -  Description of the Method
     */
    private void saveChannelSet() {

        // Write out our channelsets to the config file
        // DMT TODO
		FreeGuide.prefs.replaceChannelSets(
			Utils.arrayFromVector_ChannelSet( channelsets ) );

        updatedFlag = true;
        FreeGuide.prefs.flushAll();

    }


    private void initComponents() {

        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        butAdd = new javax.swing.JButton();
        butEdit = new javax.swing.JButton();
        butRemove = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        list = new javax.swing.JList(channelsetModel);
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        butOK = new javax.swing.JButton();
        butCancel = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setLayout(new java.awt.GridBagLayout());

        butAdd.setText("Add");
        butAdd.setPreferredSize(new java.awt.Dimension(83, 26));
        butAdd.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butAddActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanel1.add(butAdd, gridBagConstraints);

        butEdit.setText("Edit");
        butEdit.setPreferredSize(new java.awt.Dimension(83, 26));
        butEdit.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butEditActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanel1.add(butEdit, gridBagConstraints);

        butRemove.setText("Remove");
        butRemove.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butRemoveActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 10);
        jPanel1.add(butRemove, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(jPanel1, gridBagConstraints);

        jScrollPane1.setViewportView(list);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 5);
        getContentPane().add(jScrollPane1, gridBagConstraints);

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Channel sets:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jLabel1, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        butOK.setText("OK");
        butOK.setPreferredSize(new java.awt.Dimension(83, 26));
        butOK.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butOKActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 5);
        jPanel2.add(butOK, gridBagConstraints);

        butCancel.setText("Cancel");
        butCancel.setPreferredSize(new java.awt.Dimension(83, 26));
        butCancel.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    butCancelActionPerformed(evt);
                }
            });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 10, 10);
        jPanel2.add(butCancel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane().add(jPanel2, gridBagConstraints);

		getRootPane().setDefaultButton( butOK );
		
        pack();
        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setSize(new java.awt.Dimension(400, 300));
        setLocation((screenSize.width - 400) / 2, (screenSize.height - 300) / 2);
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butRemoveActionPerformed(java.awt.event.ActionEvent evt) {

        latestIndex = list.getSelectedIndex();
        if (latestIndex == channelsetModel.size() - 1) {
            latestIndex--;
        }

        int[] sel = list.getSelectedIndices();

        for (int i = 0; i < sel.length; i++) {
            channelsets.remove(sel[i]);
        }

        fillList();
        selectLatest();
        
        updatedFlag = true;

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
    private void butOKActionPerformed(java.awt.event.ActionEvent evt) {

        saveChannelSet();
        quit();

    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butEditActionPerformed(java.awt.event.ActionEvent evt) {

        latestIndex = list.getSelectedIndex();
        int i = list.getSelectedIndex();
        if (i != -1) {
            ChannelSet fav = (ChannelSet) channelsets.get(i);
            //update the channel set names
            fav.updateChannelNames(loader);

            ChannelSetEditorDialog channels = 
                new ChannelSetEditorDialog( this, "Edit Channel Set", loader,
					fav );

            boolean changed = channels.showDialog();
            
            if (changed) {
                updatedFlag = true;
            }


        }
    }


    /**
     *  Description of the Method
     *
     *@param  evt  Description of the Parameter
     */
    private void butAddActionPerformed(java.awt.event.ActionEvent evt) {
        ChannelSet newCset = new ChannelSet();

        channelsets.add(newCset);
                
        ChannelSetEditorDialog channels = 
            new ChannelSetEditorDialog( this, "Add a new Channel Set", loader,
				newCset );
  
        boolean changed = channels.showDialog();      
        
		updatedFlag = true;
		
        latestIndex = channelsetModel.size();
        reShow();

    }

    /**
     *  Description of the Method
     */
    public void reShow() {

        fillList();
        selectLatest();
    }


}
