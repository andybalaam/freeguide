package freeguide.plugins.reminder.advanced;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class AdvancedReminderUIController implements IModuleConfigurationUI
{
    protected final AdvancedReminderUIPanel panel;
    protected final AdvancedReminder.Config config;
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
     *            DOCUMENT ME!
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

            reminders.add( remPanel.config );
        }

        config.reminders.clear(  );
        config.reminders.addAll( reminders );
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }
}
