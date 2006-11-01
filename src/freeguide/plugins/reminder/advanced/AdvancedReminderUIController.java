package freeguide.plugins.reminder.advanced;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import freeguide.plugins.reminder.advanced.AdvancedReminder.OneReminderConfig;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
                final OneReminderPanel remPanel = new OneReminderPanel(  );
                panel.addReminderPanel( remPanel );
                setupReminderPanel( remPanel );
                panel.revalidate(  );
            }
        };

/**
     * Creates a new AdvancedReminderUIController object.
     *
     * @param config DOCUMENT ME!
     */
    public AdvancedReminderUIController( final AdvancedReminder.Config config )
    {
        this.config = config;
        panel = new AdvancedReminderUIPanel(  );

        for( final OneReminderConfig rem : config.reminders )
        {
            final OneReminderPanel remPanel = new OneReminderPanel(  );
            panel.addReminderPanel( remPanel );
            setupReminderPanel( remPanel );
        }

        panel.btnAddReminder.addActionListener( actionAddReminder );
    }

    protected void setupReminderPanel( final OneReminderPanel remPanel )
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
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {
        return panel;
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
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
