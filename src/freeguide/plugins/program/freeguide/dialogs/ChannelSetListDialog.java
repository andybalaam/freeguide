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

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

/*
 *  Provides a list of the user's channelsets and allows them to add or edit
 *  them by launching a FreeGuideChannelSetEditor.
 *
 * @author     Brendan Corrigan (based on FreeGuideChannelSetList by Andy
 *             Balaam)
 * @created    22nd August 2003
 * @version    2
 */
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ChannelSetListDialog extends FGDialog
{
    private javax.swing.JButton butAdd;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butEdit;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList list;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;

    // The channelsets
    private TVChannelsSet allChannels;
    private List channelsSets;
    private DefaultListModel channelsetModel;
    private int latestIndex;

    /**
     * Constructor which sets the channel set list up as a JDialog... Note:
     * This class takes the ViewerFrameXMLTVLoader as a parameter to keep the
     * structure of the application intact. This would probably be better
     * refactored as a static reference object such as the singleton pattern
     * - on the "todo" list!
     *
     * @param owner - the <code>JFrame</code> from which the dialog is
     *        displayed
     * @param allChannels - the<code>ViewerFrameXMLTVLoader</code> for
     *        compatibility
     * @param channelsSets DOCUMENT ME!
     */
    public ChannelSetListDialog(
        JFrame owner, TVChannelsSet allChannels, List channelsSets )
    {
        super(
            owner,
            Application.getInstance(  ).getLocalizedMessage( "channel_sets" ) );
        this.allChannels = allChannels;
        this.channelsSets = new ArrayList( channelsSets.size(  ) );

        for( int i = 0; i < channelsSets.size(  ); i++ )
        {
            TVChannelsSet cs = (TVChannelsSet)channelsSets.get( i );
            this.channelsSets.add( cs.clone(  ) );
        }

        channelsetModel = new DefaultListModel(  );
        initComponents(  );
        Utils.centreDialog( owner, this );
        loadChannelSet(  );
        fillList(  );
        latestIndex = 0;
        selectLatest(  );
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the channelsSets.
     */
    public List getChannelsSets(  )
    {
        return channelsSets;
    }

    /**
     * todo - Description of the Method
     */
    private void selectLatest(  )
    {
        list.setSelectedIndex( latestIndex );
    }

    /**
     * todo - Description of the Method
     */
    private void loadChannelSet(  )
    {
        //channelsets =            new Vector( Arrays.asList( FreeGuide.prefs.getChannelSets(  ) ) );
    }

    /**
     * todo - Description of the Method
     */
    private void fillList(  )
    {
        channelsetModel.removeAllElements(  );

        //DMT: iterate over keys()
        for( int i = 0; i < channelsSets.size(  ); i++ )
        {
            channelsetModel.addElement(
                ( (TVChannelsSet)channelsSets.get( i ) ).getName(  ) );
        }

        //list = new javax.swing.JList(titles);
    }

    /**
     * todo -  Description of the Method
     */
    private void saveChannelSet(  )
    {
        // Write out our channelsets to the config file
        // DMT TODO
        //FreeGuide.prefs.replaceChannelSets( Utils.arrayFromVector_ChannelSet( channelsets ) );
        //updatedFlag = true;
        //FreeGuide.prefs.flushAll(  );
        setSave(  );
    }

    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;
        getContentPane(  ).setLayout( new java.awt.GridBagLayout(  ) );
        jPanel1 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );
        butAdd = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "add" ) );
        butAdd.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butAddActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butAdd, gridBagConstraints );
        butEdit = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "edit" ) );
        butEdit.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butEditActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butEdit, gridBagConstraints );
        butRemove = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "remove" ) );
        butRemove.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butRemoveActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butRemove, gridBagConstraints );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane(  ).add( jPanel1, gridBagConstraints );

        //make same width for all add/edit/remove buttons
        int nWidthAdd = (int)butAdd.getPreferredSize(  ).getWidth(  );
        int nWidthEdit = (int)butEdit.getPreferredSize(  ).getWidth(  );
        int nWidthRemove = (int)butRemove.getPreferredSize(  ).getWidth(  );

        if( nWidthEdit > nWidthAdd )
        {
            nWidthAdd = nWidthEdit;
        }

        if( nWidthRemove > nWidthAdd )
        {
            nWidthAdd = nWidthRemove;
        }

        butAdd.setPreferredSize(
            new Dimension(
                nWidthAdd, (int)butAdd.getPreferredSize(  ).getHeight(  ) ) );
        butEdit.setPreferredSize(
            new Dimension(
                nWidthAdd, (int)butEdit.getPreferredSize(  ).getHeight(  ) ) );
        butRemove.setPreferredSize(
            new Dimension(
                nWidthAdd, (int)butRemove.getPreferredSize(  ).getHeight(  ) ) );
        list = new javax.swing.JList( channelsetModel );
        jScrollPane1 = new javax.swing.JScrollPane( list );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets( 5, 10, 5, 5 );
        getContentPane(  ).add( jScrollPane1, gridBagConstraints );
        jLabel1 = new javax.swing.JLabel(
                Application.getInstance(  ).getLocalizedMessage(
                    "channel_sets" ) + ':', javax.swing.SwingConstants.CENTER );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( jLabel1, gridBagConstraints );
        jPanel2 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );
        butOK = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );
        butOK.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butOKActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 10, 5 );
        jPanel2.add( butOK, gridBagConstraints );
        butCancel = new javax.swing.JButton(
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );
        butCancel.addActionListener(
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butCancelActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 10, 10 );
        jPanel2.add( butCancel, gridBagConstraints );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane(  ).add( jPanel2, gridBagConstraints );
        getRootPane(  ).setDefaultButton( butOK );
        pack(  );

        setSize( new java.awt.Dimension( 400, 300 ) );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butRemoveActionPerformed( java.awt.event.ActionEvent evt )
    {
        latestIndex = list.getSelectedIndex(  );

        if( latestIndex == ( channelsetModel.size(  ) - 1 ) )
        {
            latestIndex--;
        }

        int[] sel = list.getSelectedIndices(  );

        for( int i = 0; i < sel.length; i++ )
        {
            channelsSets.remove( sel[i] );
        }

        fillList(  );
        selectLatest(  );
        setChanged(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butCancelActionPerformed( java.awt.event.ActionEvent evt )
    {
        quit(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butOKActionPerformed( java.awt.event.ActionEvent evt )
    {
        saveChannelSet(  );
        quit(  );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butEditActionPerformed( java.awt.event.ActionEvent evt )
    {
        latestIndex = list.getSelectedIndex(  );

        int i = list.getSelectedIndex(  );

        if( i != -1 )
        {
            TVChannelsSet fav = (TVChannelsSet)channelsSets.get( i );

            //update the channel set names
            //todo: fav.updateChannelNames( data );
            ChannelSetEditorDialog channels =
                new ChannelSetEditorDialog(
                    this,
                    Application.getInstance(  )
                               .getLocalizedMessage( "edit_channel_set" ),
                    allChannels, fav );
            boolean changed = channels.showDialog(  );

            if( changed )
            {
                setChanged(  );
            }
        }
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butAddActionPerformed( java.awt.event.ActionEvent evt )
    {
        TVChannelsSet newCset = new TVChannelsSet(  );
        newCset.setName(
            Application.getInstance(  ).getLocalizedMessage(
                "new_channel_set" ) );
        channelsSets.add( newCset );

        ChannelSetEditorDialog channels =
            new ChannelSetEditorDialog(
                this,
                Application.getInstance(  )
                           .getLocalizedMessage( "add_a_new_channel_set" ),
                allChannels, newCset );
        channels.showDialog(  );
        setChanged(  );
        latestIndex = channelsetModel.size(  );
        reShow(  );
    }

    /**
     * Description of the Method
     */
    public void reShow(  )
    {
        fillList(  );
        selectLatest(  );
    }
}
