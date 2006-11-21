package freeguide.plugins.reminder.advanced;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
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
    protected final JPanel mainPanel;
    protected final JButton btnAddReminder;
    protected final List<OneReminderPanel> reminderPanels =
        new ArrayList<OneReminderPanel>(  );
    protected int latestLine;
    protected final JTable tblChannels;

/**
     * Creates a new AdvancedReminderUIPanel object.
     */
    public AdvancedReminderUIPanel(  )
    {
        setLayout( new BorderLayout(  ) );
        mainPanel = new JPanel(  );

        final JScrollPane scrollPane = new JScrollPane( mainPanel );
        scrollPane.getVerticalScrollBar(  ).setUnitIncrement( 10 );
        add( scrollPane, BorderLayout.CENTER );
        btnAddReminder = new JButton( "Add reminder" );

        mainPanel.setLayout( new GridBagLayout(  ) );

        final GridBagConstraints gbcButtonAdd = new GridBagConstraints(  );
        gbcButtonAdd.gridy = 0;
        gbcButtonAdd.anchor = GridBagConstraints.EAST;
        mainPanel.add( btnAddReminder, gbcButtonAdd );
        latestLine = 5;

        tblChannels = new JTable(  );

        addChannelsTable(  );
    }

    protected void addChannelsTable(  )
    {
        final GridBagConstraints gbcTable = new GridBagConstraints(  );
        gbcTable.gridy = 1;
        gbcTable.fill = GridBagConstraints.BOTH;

        final JScrollPane scTable = new JScrollPane( tblChannels );
        scTable.setHorizontalScrollBarPolicy( 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        scTable.setPreferredSize( new Dimension( 50, 100 ) );

        mainPanel.add( scTable, gbcTable );
    }

    /**
     * Add new reminder config panel.
     *
     * @param panel reminder config panel.
     */
    public void addReminderPanel( final OneReminderPanel panel )
    {
        reminderPanels.add( panel );

        final GridBagConstraints gbc = new GridBagConstraints(  );
        gbc.gridy = latestLine;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add( panel, gbc );
        latestLine++;
    }

    /**
     * Remove reminder config panel.
     *
     * @param panel reminder config panel
     */
    public void removeReminderPanel( final OneReminderPanel panel )
    {
        reminderPanels.remove( panel );
        mainPanel.remove( panel );
    }
}
