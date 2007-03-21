package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleStorage;

import freeguide.plugins.program.freeguide.options.OptionsDialog;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * Controller for edit configuration.
 *
 * @author Alex Buloichik
 */
public class AdvancedReminderUIController implements IModuleConfigurationUI
{
    protected final AdvancedReminder.Config config;
    protected Map<String, Component> panels;
    protected JTree tree;
    protected final Map<String, DefaultMutableTreeNode> treeNodes =
        new TreeMap<String, DefaultMutableTreeNode>(  );
    protected List<ChannelInfo> channelsHardwareData;
    protected OneReminderPanel currentAddNewPanel;

/**
     * Creates a new AdvancedReminderUIController object.
     * 
     * @param config
     *            config
     */
    public AdvancedReminderUIController( final AdvancedReminder.Config config )
    {
        this.config = config;
    }

    /**
     * Setup one reminder panel.
     *
     * @param remPanel panel
     * @param config reminder config
     * @param isNew DOCUMENT ME!
     */
    protected void setupReminderPanel( 
        final OneReminderPanel remPanel, final OneReminderConfig config,
        final boolean isNew )
    {
        remPanel.btnAction.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    String name = remPanel.config.name;

                    if( name == null )
                    {
                        name = remPanel.txtName.getText(  );
                    }

                    if( "".equals( name ) )
                    {
                        name = "Add new...";
                    }

                    if( 
                        !( (JButton)e.getSource(  ) ).getText(  )
                               .equals( "Delete" ) )
                    {
                        // new reminder panel
                        name = "Add new...";

                        if( panels.containsKey( remPanel.txtName.getText(  ) ) )
                        {
                            JOptionPane.showMessageDialog( 
                                remPanel,
                                "Reminder with the same name already exists",
                                "Add new reminder error",
                                JOptionPane.ERROR_MESSAGE );
                        }
                        else
                        {
                            DefaultMutableTreeNode treeNode =
                                treeNodes.get( name );
                            treeNode.setUserObject( 
                                new OptionsDialog.ModuleNode( 
                                    AdvancedReminderUIController.this,
                                    remPanel.txtName.getText(  ) ) );

                            panels.put( 
                                remPanel.txtName.getText(  ), remPanel );
                            remPanel.btnAction.setText( "Delete" );
                            treeNodes.put( 
                                remPanel.txtName.getText(  ), treeNode );

                            final DefaultMutableTreeNode modBranch =
                                new DefaultMutableTreeNode( 
                                    new OptionsDialog.ModuleNode( 
                                        AdvancedReminderUIController.this,
                                        "Add new..." ) );
                            ( (DefaultTreeModel)tree.getModel(  ) )
                            .insertNodeInto( 
                                modBranch,
                                (MutableTreeNode)treeNode.getParent(  ),
                                treeNode.getParent(  ).getChildCount(  ) );
                            ( (DefaultMutableTreeNode)treeNode.getParent(  ) )
                            .add( modBranch );
                            treeNodes.put( "Add new...", modBranch );

                            AdvancedReminder.OneReminderConfig c =
                                new AdvancedReminder.OneReminderConfig(  );
                            currentAddNewPanel = new OneReminderPanel( c );
                            setupReminderPanel( currentAddNewPanel, c, true );
                            panels.put( "Add new...", currentAddNewPanel );
                        }
                    }
                    else
                    {
                        // exists reminder panel                               
                        panels.remove( name );

                        MutableTreeNode treeNode = treeNodes.remove( name );

                        // remove node from tree and set default node to 'reminders'
                        TreeNode[] parentPath =
                            ( (DefaultMutableTreeNode)treeNode.getParent(  ) )
                            .getPath(  );
                        ( (DefaultTreeModel)tree.getModel(  ) )
                        .removeNodeFromParent( treeNode );
                        tree.setSelectionPath( new TreePath( parentPath ) );
                    }
                }
            } );

        remPanel.txtName.setText( config.name );
        remPanel.cbPopup.setSelected( config.isPopup );
        remPanel.tmPopupShow.setTimeValue( -config.popupOpenTime );
        remPanel.tmPopupHide.setTimeValue( config.popupCloseTime );
        remPanel.cbSound.setSelected( config.isSound );
        remPanel.tmSound.setTimeValue( -config.soundPlayTime );
        remPanel.txtSoundFile.setText( config.soundFile );
        remPanel.cbExecute.setSelected( config.isExecute );
        remPanel.tmExecuteStart.setTimeValue( -config.executeStartTime );
        remPanel.txtExecuteStart.setText( config.executeStartCommand );
        remPanel.tmExecuteStop.setTimeValue( 
            config.executeStopTimeOnFinishProgramme );
        remPanel.txtExecuteStop.setText( config.executeStopCommand );
        remPanel.config = config;
        remPanel.setIcon( config.iconName );

        if( config.name == null )
        {
            remPanel.btnAction.setText( "Create new" );
        }
    }

    /**
     * Return UI.
     *
     * @param leafName DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param tree DOCUMENT ME!
     *
     * @return UI panel
     */
    public Component getPanel( 
        String leafName, MutableTreeNode node, JTree tree )
    {
        if( panels == null )
        {
            treeNodes.clear(  );

            panels = new TreeMap<String, Component>(  );

            AdvancedReminderUIPanel chPanel = new AdvancedReminderUIPanel(  );
            chPanel.tblChannels.setModel( new ChannelsTableModel( config ) );
            panels.put( "Channel names", chPanel );

            synchronized( config )
            {
                for( AdvancedReminder.OneReminderConfig c : config.reminders )
                {
                    OneReminderPanel remPanel = new OneReminderPanel( c );
                    setupReminderPanel( remPanel, c, false );
                    panels.put( c.name, remPanel );
                }
            }

            AdvancedReminder.OneReminderConfig c =
                new AdvancedReminder.OneReminderConfig(  );
            currentAddNewPanel = new OneReminderPanel( c );
            setupReminderPanel( currentAddNewPanel, c, true );
            panels.put( "Add new...", currentAddNewPanel );
        }

        Component result = panels.get( leafName );
        treeNodes.put( leafName, (DefaultMutableTreeNode)node );
        this.tree = tree;

        return result;
    }

    /**
     * Save data from UI into config object.
     */
    public void save(  )
    {
        if( panels == null )
        {
            return;
        }

        List<OneReminderConfig> reminders =
            new ArrayList<OneReminderConfig>(  );

        // map from old to new reminder names
        final Map<String, String> oldToNewNames =
            new TreeMap<String, String>(  );

        for( final Component panel : panels.values(  ) )
        {
            if( panel instanceof OneReminderPanel )
            {
                OneReminderPanel remPanel = (OneReminderPanel)panel;

                if( remPanel == currentAddNewPanel )
                {
                    continue;
                }

                final OneReminderConfig pc = remPanel.config;

                if( pc.name != null )
                {
                    oldToNewNames.put( pc.name, remPanel.txtName.getText(  ) );
                }

                pc.name = remPanel.txtName.getText(  );
                pc.isPopup = remPanel.cbPopup.isSelected(  );
                pc.popupOpenTime = -remPanel.tmPopupShow.getTimeValue(  );
                pc.popupCloseTime = remPanel.tmPopupHide.getTimeValue(  );
                pc.isSound = remPanel.cbSound.isSelected(  );
                pc.soundPlayTime = -remPanel.tmSound.getTimeValue(  );
                pc.soundFile = remPanel.txtSoundFile.getText(  );
                pc.isExecute = remPanel.cbExecute.isSelected(  );
                pc.executeStartTime = -remPanel.tmExecuteStart.getTimeValue(  );
                pc.executeStartCommand = remPanel.txtExecuteStart.getText(  );
                pc.executeStopTimeOnFinishProgramme = remPanel.tmExecuteStop
                    .getTimeValue(  );
                pc.executeStopCommand = remPanel.txtExecuteStop.getText(  );

                final OneReminderPanel.CBItem item =
                    (OneReminderPanel.CBItem)remPanel.cbIcons.getSelectedItem(  );
                pc.iconName = ( item != null ) ? item.key : null;

                reminders.add( remPanel.config );
            }
        }

        synchronized( config )
        {
            afterReminderRenamed( oldToNewNames );

            config.reminders.clear(  );
            config.reminders.addAll( reminders );

            for( final ChannelInfo ch : channelsHardwareData )
            {
                if( ch.hardwareId != null )
                {
                    config.channelsHardwareId.put( 
                        ch.channelID, ch.hardwareId );
                }
                else
                {
                    config.channelsHardwareId.remove( ch.channelID );
                }
            }
        }
    }

    protected void afterReminderRenamed( 
        final Map<String, String> oldToNewNames )
    {
        for( final ManualSelection sel : config.manualSelectionList )
        {
            final Map<String, Boolean> newReminders =
                new TreeMap<String, Boolean>(  );

            for( final Map.Entry<String, Boolean> rem : sel.reminders.entrySet(  ) )
            {
                final String newName = oldToNewNames.get( rem.getKey(  ) );

                if( newName != null )
                {
                    newReminders.put( newName, rem.getValue(  ) );
                }
            }

            sel.reminders.clear(  );
            sel.reminders.putAll( newReminders );
        }

        for( final Favourite fav : config.favouritesList )
        {
            final Set<String> newReminders = new TreeSet<String>(  );

            for( final String rem : fav.reminders )
            {
                final String newName = oldToNewNames.get( rem );

                if( newName != null )
                {
                    newReminders.add( newName );
                }
            }

            fav.reminders.clear(  );
            fav.reminders.addAll( newReminders );
        }
    }

    /**
     * Reset data to defaults.
     */
    public void resetToDefaults(  )
    {
    }

    /**
     * Cancel editing.
     */
    public void cancel(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getTreeNodes(  )
    {
        synchronized( config )
        {
            List<String> result = new ArrayList<String>(  );
            result.add( "Channel names" );

            for( AdvancedReminder.OneReminderConfig c : config.reminders )
            {
                result.add( c.name );
            }

            result.add( "Add new..." );

            return result.toArray( new String[result.size(  )] );
        }
    }

    protected static class ChannelInfo
    {
        protected final String channelID;
        protected final String name;
        protected String hardwareId;

/**
         * Creates a new ChannelInfo object.
         * 
         * @param channelID
         *            DOCUMENT ME!
         * @param name
         *            DOCUMENT ME!
         * @param hardwareId
         *            DOCUMENT ME!
         */
        public ChannelInfo( 
            final String channelID, final String name, final String hardwareId )
        {
            this.channelID = channelID;
            this.name = name;
            this.hardwareId = hardwareId;
        }
    }

    protected class ChannelsTableModel extends AbstractTableModel
    {
/**
         * Creates a new ChannelsTableModel object.
         * 
         * @param config
         *            DOCUMENT ME!
         */
        public ChannelsTableModel( final AdvancedReminder.Config config )
        {
            final IModuleStorage.Info info =
                Application.getInstance(  ).getDataStorage(  ).getInfo(  );

            if( info.channelsList != null )
            {
                channelsHardwareData = new ArrayList<ChannelInfo>( 
                        info.channelsList.channels.size(  ) );

                synchronized( config.channelsHardwareId )
                {
                    for( final TVChannelsSet.Channel ch : info.channelsList.channels )
                    {
                        channelsHardwareData.add( 
                            new ChannelInfo( 
                                ch.getChannelID(  ), ch.getDisplayName(  ),
                                config.channelsHardwareId.get( 
                                    ch.getChannelID(  ) ) ) );
                    }
                }
            }
            else
            {
                channelsHardwareData = new ArrayList<ChannelInfo>(  );
            }

            Collections.sort( 
                channelsHardwareData,
                new Comparator<ChannelInfo>(  )
                {
                    public int compare( 
                        final ChannelInfo o1, final ChannelInfo o2 )
                    {
                        return o1.channelID.compareTo( o2.channelID );
                    }
                } );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param rowIndex DOCUMENT_ME!
         * @param columnIndex DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean isCellEditable( int rowIndex, int columnIndex )
        {
            return columnIndex == 2;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public int getColumnCount(  )
        {
            return 3;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public int getRowCount(  )
        {
            return channelsHardwareData.size(  );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param column DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String getColumnName( int column )
        {
            switch( column )
            {
            case 0:
                return "id";

            case 1:
                return "name";

            case 2:
                return "hardwareName";
            }

            return null;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param rowIndex DOCUMENT_ME!
         * @param columnIndex DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Object getValueAt( int rowIndex, int columnIndex )
        {
            final ChannelInfo line = channelsHardwareData.get( rowIndex );

            switch( columnIndex )
            {
            case 0:
                return line.channelID;

            case 1:
                return line.name;

            case 2:
                return line.hardwareId;
            }

            return null;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param aValue DOCUMENT_ME!
         * @param rowIndex DOCUMENT_ME!
         * @param columnIndex DOCUMENT_ME!
         */
        public void setValueAt( Object aValue, int rowIndex, int columnIndex )
        {
            if( columnIndex == 2 )
            {
                final ChannelInfo line = channelsHardwareData.get( rowIndex );
                line.hardwareId = (String)aValue;
            }
        }
    }
}
