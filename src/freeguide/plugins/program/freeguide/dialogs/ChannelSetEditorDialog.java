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
package freeguide.plugins.program.freeguide.dialogs;

import freeguide.common.gui.FGDialog;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.general.Utils;

import java.awt.Dimension;

import java.util.Iterator;

import javax.swing.DefaultListModel;

/*
 *  Allows the user to edit a channel set
 *
 * @author     Brendan Corrigan (based on FreeGuideChannelSetEditor by dtorok)
 * @created    22nd August 2003
 * @version    2
 */
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ChannelSetEditorDialog extends FGDialog
{
    private javax.swing.JButton addButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton moveDownButton;
    private javax.swing.JButton moveUpButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JList allchannelsJList;
    private javax.swing.JList channelsetJList;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField nameTextField;
    private TVChannelsSet allChannels;
    private TVChannelsSet editChannelsSet;

    /** internal list models for the two JLists */
    private DefaultListModel allModel;

    /** internal list models for the two JLists */
    private DefaultListModel csetModel;

    /**
     * Creates a new ChannelSetEditorDialog object.
     *
     * @param owner DOCUMENT ME!
     * @param title DOCUMENT ME!
     * @param allChannels DOCUMENT ME!
     * @param editChannelsSet DOCUMENT ME!
     */
    public ChannelSetEditorDialog(
        FGDialog owner, String title, TVChannelsSet allChannels,
        TVChannelsSet editChannelsSet )
    {
        super( owner, title );
        this.allChannels = allChannels;
        this.editChannelsSet = editChannelsSet;
        this.allModel = new DefaultListModel(  );
        this.csetModel = new DefaultListModel(  );
        initComponents(  );
        Utils.centreDialog( owner, this );
        fillData(  );
    }

    /**
     * Description of the Method
     */
    private void fillData(  )
    {
        Iterator it;
        it = editChannelsSet.getChannels(  ).iterator(  );

        while( it.hasNext(  ) )
        {
            csetModel.addElement( it.next(  ) );
        }

        it = allChannels.getSortedChannels(  ).iterator(  );

        while( it.hasNext(  ) )
        {
            TVChannelsSet.Channel ch = (TVChannelsSet.Channel)it
                .next(  );

            if( !editChannelsSet.contains( ch.getChannelID(  ) ) )
            {
                allModel.addElement( ch );
            }
        }

        nameTextField.setText( editChannelsSet.getName(  ) );
    }

    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;
        getContentPane(  ).setLayout( new java.awt.GridBagLayout(  ) );
        allchannelsJList = new javax.swing.JList( allModel );
        allchannelsJList.setMinimumSize( new java.awt.Dimension( 100, 600 ) );
        allchannelsJList.setVisibleRowCount( 20 );
        jScrollPane1 = new javax.swing.JScrollPane( allchannelsJList );
        jScrollPane1.setMinimumSize( new java.awt.Dimension( 100, 600 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 0, 10, 0, 5 );
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        getContentPane(  ).add( jScrollPane1, gridBagConstraints );
        channelsetJList = new javax.swing.JList( csetModel );
        channelsetJList.setMinimumSize( new java.awt.Dimension( 100, 600 ) );
        channelsetJList.setVisibleRowCount( 20 );
        channelsetJList.addListSelectionListener(
            new javax.swing.event.ListSelectionListener(  )
            {
                public void valueChanged(
                    javax.swing.event.ListSelectionEvent evt )
                {
                    channelsetListChanged( evt );
                }
            } );
        jScrollPane2 = new javax.swing.JScrollPane( channelsetJList );
        jScrollPane2.setMinimumSize( new java.awt.Dimension( 100, 600 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 0, 5, 0, 10 );
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        getContentPane(  ).add( jScrollPane2, gridBagConstraints );
        jPanel1 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );
        addButton = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "add_to_set" )
                + " >>" );
        addButton.setMaximumSize( new java.awt.Dimension( 135, 26 ) );
        addButton.setMinimumSize( new java.awt.Dimension( 135, 26 ) );
        addButton.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    addButtonActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets( 0, 5, 0, 5 );
        jPanel1.add( addButton, gridBagConstraints );
        removeButton = new javax.swing.JButton(
                "<< "
                + Application.getInstance(  )
                             .getLocalizedMessage( "remove_from_set" ) );
        removeButton.setMaximumSize( new java.awt.Dimension( 135, 26 ) );
        removeButton.setMinimumSize( new java.awt.Dimension( 135, 26 ) );
        removeButton.setPreferredSize( new java.awt.Dimension( 172, 26 ) );
        removeButton.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    removeButtonActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets( 0, 5, 50, 5 );
        gridBagConstraints.weighty = 0.25;
        jPanel1.add( removeButton, gridBagConstraints );
        moveUpButton = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "move_up" ) );
        moveUpButton.setMaximumSize( new java.awt.Dimension( 115, 26 ) );
        moveUpButton.setMinimumSize( new java.awt.Dimension( 115, 26 ) );
        moveUpButton.setPreferredSize( new java.awt.Dimension( 115, 26 ) );
        moveUpButton.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    upButtonAction( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets( 0, 5, 0, 5 );
        jPanel1.add( moveUpButton, gridBagConstraints );
        moveDownButton = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "move_down" ) );
        moveDownButton.setMaximumSize( new java.awt.Dimension( 115, 26 ) );
        moveDownButton.setMinimumSize( new java.awt.Dimension( 115, 26 ) );
        moveDownButton.setPreferredSize( new java.awt.Dimension( 115, 26 ) );
        moveDownButton.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    downButtonActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets( 0, 5, 0, 5 );
        jPanel1.add( moveDownButton, gridBagConstraints );

        //make same width
        int nWidthAdd = (int)addButton.getPreferredSize(  ).getWidth(  );
        int nWidthRemove = (int)removeButton.getPreferredSize(  ).getWidth(  );
        int nWidthMoveUp = (int)moveUpButton.getPreferredSize(  ).getWidth(  );
        int nWidthMoveDown =
            (int)moveDownButton.getPreferredSize(  ).getWidth(  );

        if( nWidthRemove > nWidthAdd )
        {
            nWidthAdd = nWidthRemove;
        }

        if( nWidthMoveUp > nWidthAdd )
        {
            nWidthAdd = nWidthMoveUp;
        }

        if( nWidthMoveDown > nWidthAdd )
        {
            nWidthAdd = nWidthMoveDown;
        }

        addButton.setMinimumSize(
            new Dimension(
                nWidthAdd, (int)addButton.getPreferredSize(  ).getHeight(  ) ) );
        removeButton.setMinimumSize(
            new Dimension(
                nWidthAdd, (int)removeButton.getPreferredSize(  ).getHeight(  ) ) );
        moveUpButton.setMinimumSize(
            new Dimension(
                nWidthAdd, (int)moveUpButton.getPreferredSize(  ).getHeight(  ) ) );
        moveDownButton.setMinimumSize(
            new Dimension(
                nWidthAdd,
                (int)moveDownButton.getPreferredSize(  ).getHeight(  ) ) );
        addButton.setPreferredSize( addButton.getMinimumSize(  ) );
        removeButton.setPreferredSize( removeButton.getMinimumSize(  ) );
        moveUpButton.setPreferredSize( moveUpButton.getMinimumSize(  ) );
        moveDownButton.setPreferredSize( moveDownButton.getMinimumSize(  ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        getContentPane(  ).add( jPanel1, gridBagConstraints );
        jPanel3 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );
        jPanel3.setMinimumSize( new java.awt.Dimension( 400, 80 ) );
        nameLabel = new javax.swing.JLabel(
                Application.getInstance(  )
                           .getLocalizedMessage( "channel_set_name" ) + ":",
                javax.swing.SwingConstants.LEFT );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets( 0, 0, 0, 5 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add( nameLabel, gridBagConstraints );
        nameTextField = new javax.swing.JTextField(  );
        nameTextField.setMinimumSize( new java.awt.Dimension( 200, 20 ) );
        nameTextField.setPreferredSize( new java.awt.Dimension( 200, 20 ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        jPanel3.add( nameTextField, gridBagConstraints );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        getContentPane(  ).add( jPanel3, gridBagConstraints );
        jPanel4 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );
        saveButton = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );
        saveButton.setMaximumSize( new java.awt.Dimension( 83, 26 ) );
        saveButton.setMinimumSize( new java.awt.Dimension( 83, 26 ) );
        saveButton.setPreferredSize( new java.awt.Dimension( 83, 26 ) );
        saveButton.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    saveButtonActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 0, 0, 0, 10 );
        jPanel4.add( saveButton, gridBagConstraints );
        cancelButton = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );
        cancelButton.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    cancelButtonActionPerformed( evt );
                }
            } );
        jPanel4.add( cancelButton, new java.awt.GridBagConstraints(  ) );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets( 10, 5, 10, 10 );
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane(  ).add( jPanel4, gridBagConstraints );
        jLabel1 = new javax.swing.JLabel(
                Application.getInstance(  )
                           .getLocalizedMessage( "available_channels" ) + ":" );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane(  ).add( jLabel1, gridBagConstraints );
        jLabel2 = new javax.swing.JLabel(
                Application.getInstance(  ).getLocalizedMessage( "this_set" )
                + ":" );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        getContentPane(  ).add( jLabel2, gridBagConstraints );
        getRootPane(  ).setDefaultButton( saveButton );
        pack(  );

        setSize( new java.awt.Dimension( 450, 300 ) );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void channelsetListChanged(
        javax.swing.event.ListSelectionEvent evt )
    {
        checkUpAndDownButtons(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void downButtonActionPerformed( java.awt.event.ActionEvent evt )
    {
        int[] selected = channelsetJList.getSelectedIndices(  );

        for( int i = 0; i < selected.length; i++ )
        {
            int oldIndex = selected[i];

            if( oldIndex < ( csetModel.getSize(  ) - 1 ) )
            {
                int newIndex = oldIndex + 1;
                csetModel.insertElementAt(
                    csetModel.remove( oldIndex ), newIndex );
                channelsetJList.setSelectedIndex( newIndex );
                setChanged(  );
            }
        }

        checkUpAndDownButtons(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void upButtonAction( java.awt.event.ActionEvent evt )
    {
        int[] selected = channelsetJList.getSelectedIndices(  );

        for( int i = 0; i < selected.length; i++ )
        {
            int oldIndex = selected[i];

            if( oldIndex > 0 )
            {
                int newIndex = oldIndex - 1;
                csetModel.insertElementAt(
                    csetModel.remove( oldIndex ), newIndex );
                channelsetJList.setSelectedIndex( newIndex );
                setChanged(  );
            }
        }

        checkUpAndDownButtons(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void removeButtonActionPerformed( java.awt.event.ActionEvent evt )
    {
        int[] selected = channelsetJList.getSelectedIndices(  );

        for( int i = 0; i < selected.length; i++ )
        {
            //Object o=allModel.remove(selected[i]);
            allModel.addElement( csetModel.elementAt( selected[i] ) );
            setChanged(  );
        }

        for( int j = selected.length - 1; j >= 0; j-- )
        {
            csetModel.remove( selected[j] );
        }

        checkUpAndDownButtons(  );
    }

    /**
     * Adds a feature to the ButtonActionPerformed attribute of the
     * ChannelSetEditor object
     *
     * @param evt The feature to be added to the ButtonActionPerformed
     *        attribute
     */
    private void addButtonActionPerformed( java.awt.event.ActionEvent evt )
    {
        //take selected item in source and move it into sink
        int[] selected = allchannelsJList.getSelectedIndices(  );

        for( int i = 0; i < selected.length; i++ )
        {
            //Object o=allModel.remove(selected[i]);
            csetModel.addElement( allModel.elementAt( selected[i] ) );
            setChanged(  );
        }

        for( int j = selected.length - 1; j >= 0; j-- )
        {
            allModel.remove( selected[j] );
        }

        checkUpAndDownButtons(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void cancelButtonActionPerformed( java.awt.event.ActionEvent evt )
    {
        quit(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void saveButtonActionPerformed( java.awt.event.ActionEvent evt )
    {
        updateChannelSet(  );
        quit(  );
    }

    /**
     * Description of the Method
     */
    private void checkUpAndDownButtons(  )
    {
        int[] selected = channelsetJList.getSelectedIndices(  );
        boolean enableUp = false;
        boolean enableDown = false;
        int max = csetModel.getSize(  ) - 1;

        for( int i = 0; i < selected.length; i++ )
        {
            int index = selected[i];

            if( index > 0 )
            {
                enableUp = true;
            }

            if( index < max )
            {
                enableDown = true;
            }

            if( enableUp && enableDown )
            {
                break;
            }
        }

        moveUpButton.setEnabled( enableUp );
        moveDownButton.setEnabled( enableDown );
    }

    /**
     * Description of the Method
     */
    private void updateChannelSet(  )
    {
        editChannelsSet.setName( this.nameTextField.getText(  ) );
        editChannelsSet.getChannels(  ).clear(  );

        for( int i = 0; i < csetModel.size(  ); i++ )
        {
            editChannelsSet.add( (TVChannelsSet.Channel)csetModel.get( i ) );
        }

        setSave(  );
    }
}
