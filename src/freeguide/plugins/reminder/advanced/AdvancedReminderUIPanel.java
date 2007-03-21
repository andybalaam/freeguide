package freeguide.plugins.reminder.advanced;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;

/**
 * Panel for edit reminders.
 *
 * @author Alex Buloichik
 */
public class AdvancedReminderUIPanel extends JPanel
{
    protected final JTable tblChannels;

/**
     * Creates a new AdvancedReminderUIPanel object.
     */
    public AdvancedReminderUIPanel(  )
    {
        setLayout( new BorderLayout(  ) );

        tblChannels = new JTable(  );

        final JScrollPane scTable = new JScrollPane( tblChannels );
        scTable.setHorizontalScrollBarPolicy( 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        scTable.setPreferredSize( new Dimension( 50, 100 ) );

        add( new JScrollPane( scTable ), BorderLayout.CENTER );
    }
}
