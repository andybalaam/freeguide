package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleStorage;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Controller for edit configuration.
 *
 * @author Alex Buloichik
 */
public class AdvancedReminderUIController implements IModuleConfigurationUI
{
    protected final AdvancedReminderUIPanel panel;
    protected final AdvancedReminder.Config config;
    protected List<ChannelInfo> channelsHardwareData;
    protected ActionListener actionAddReminder =
        new ActionListener(  )
        {
            public void actionPerformed( ActionEvent e )
            {
                final OneReminderConfig config = new OneReminderConfig(  );
                final OneReminderPanel remPanel =
                    new OneReminderPanel( config );
                panel.addReminderPanel( remPanel );
                setupReminderPanel( remPanel, config );
                panel.revalidate(  );
            }
        };

/**
     * Creates a new AdvancedReminderUIController object.
     * 
     * @param config
     *            config
     */
    public AdvancedReminderUIController( final AdvancedReminder.Config config )
    {
        this.config = config;
        panel = new AdvancedReminderUIPanel(  );

        for( final OneReminderConfig rem : config.reminders )
        {
            final OneReminderPanel remPanel = new OneReminderPanel( rem );
            panel.addReminderPanel( remPanel );
            setupReminderPanel( remPanel, rem );
        }

        panel.btnAddReminder.addActionListener( actionAddReminder );

        panel.tblChannels.setModel( new ChannelsTableModel( config ) );
    }

    /**
     * Setup one reminder panel.
     *
     * @param remPanel panel
     * @param config reminder config
     */
    protected void setupReminderPanel( 
        final OneReminderPanel remPanel, final OneReminderConfig config )
    {
        remPanel.btnDelete.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    panel.removeReminderPanel( remPanel );
                    panel.revalidate(  );

                    final Container c = panel.getParent(  );
                    c.repaint(  );
                }
            } );
        remPanel.txtName.setText( config.name );
        remPanel.cbPopup.setSelected( config.isPopup );
        remPanel.tmPopupShow.setText( 
            Long.toString( config.popupOpenTime / 1000 ) );
        remPanel.tmPopupHide.setText( 
            Long.toString( config.popupCloseTime / 1000 ) );
        remPanel.cbSound.setSelected( config.isSound );
        remPanel.tmSound.setText( 
            Long.toString( config.soundPlayTime / 1000 ) );
        remPanel.txtSoundFile.setText( config.soundFile );
        remPanel.cbExecute.setSelected( config.isExecute );
        remPanel.tmExecuteStart.setText( 
            Long.toString( config.executeStartTime / 1000 ) );
        remPanel.txtExecuteStart.setText( config.executeStartCommand );
        remPanel.tmExecuteStop.setText( 
            Long.toString( config.executeStopTimeOnFinishProgramme / 1000 ) );
        remPanel.txtExecuteStop.setText( config.executeStopCommand );
        remPanel.config = config;
        remPanel.setIcon( config.iconName );
    }

    /**
     * Return UI.
     *
     * @return UI panel
     */
    public Component getPanel(  )
    {
        return panel;
    }

    /**
     * Save data from UI into config object.
     */
    public void save(  )
    {
        List<OneReminderConfig> reminders =
            new ArrayList<OneReminderConfig>(  );

        for( final OneReminderPanel remPanel : panel.reminderPanels )
        {
            final OneReminderConfig pc = remPanel.config;

            pc.name = remPanel.txtName.getText(  );
            pc.isPopup = remPanel.cbPopup.isSelected(  );
            pc.popupOpenTime = Long.parseLong( 
                    remPanel.tmPopupShow.getText(  ) ) * 1000;
            pc.popupCloseTime = Long.parseLong( 
                    remPanel.tmPopupHide.getText(  ) ) * 1000;
            pc.isSound = remPanel.cbSound.isSelected(  );
            pc.soundPlayTime = Long.parseLong( remPanel.tmSound.getText(  ) ) * 1000;
            pc.soundFile = remPanel.txtSoundFile.getText(  );
            pc.isExecute = remPanel.cbExecute.isSelected(  );
            pc.executeStartTime = Long.parseLong( 
                    remPanel.tmExecuteStart.getText(  ) ) * 1000;
            pc.executeStartCommand = remPanel.txtExecuteStart.getText(  );
            pc.executeStopTimeOnFinishProgramme = Long.parseLong( 
                    remPanel.tmExecuteStop.getText(  ) ) * 1000;
            pc.executeStopCommand = remPanel.txtExecuteStop.getText(  );

            final OneReminderPanel.CBItem item =
                (OneReminderPanel.CBItem)remPanel.cbIcons.getSelectedItem(  );
            pc.iconName = ( item != null ) ? item.key : null;

            reminders.add( remPanel.config );
        }

        config.reminders.clear(  );
        config.reminders.addAll( reminders );

        synchronized( config.channelsHardwareId )
        {
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

    protected static class ChannelInfo
    {
        protected final String channelID;
        protected final String name;
        protected String hardwareId;

        /**
         * Creates a new ChannelInfo object.
         *
         * @param channelID DOCUMENT ME!
         * @param name DOCUMENT ME!
         * @param hardwareId DOCUMENT ME!
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
         * @param config DOCUMENT ME!
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
