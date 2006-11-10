package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Popup menu filler and handler.
 *
 * @author Alex Buloichik
 */
public class PopupMenuHandler
{
    protected final AdvancedReminder parent;

/**
     * Creates a new PopupMenuHandler object.
     *
     * @param parent DOCUMENT ME!
     */
    public PopupMenuHandler( final AdvancedReminder parent )
    {
        this.parent = parent;
    }

    /**
     * Fill popup menu items.
     *
     * @param menu menu
     * @param programme programme for this menu
     */
    public void fillMenu( final JPopupMenu menu, final TVProgramme programme )
    {
        /** menu items for each reminder */
        final Map<String, Boolean> selMap = new TreeMap<String, Boolean>(  );

        // find this programme in favourites
        for( final Favourite fav : parent.config.favouritesList )
        {
            if( fav.matches( programme ) )
            {
                for( final String rem : fav.reminders )
                {
                    selMap.put( rem, Boolean.TRUE );
                }
            }
        }

        // find in selections
        for( final ManualSelection sel : parent.config.manualSelectionList )
        {
            if( sel.matches( programme ) )
            {
                selMap.putAll( sel.reminders );

                break;
            }
        }

        for( final String name : parent.getReminderNames(  ) )
        {
            final JMenuItem menuItem = new JMenuItem(  );

            final Boolean currentState = selMap.get( name );

            if( currentState == null )
            {
                menuItem.setText( "add to " + name );
                menuItem.addActionListener( 
                    new AddActionListener( programme, name ) );
            }
            else if( currentState.booleanValue(  ) )
            {
                menuItem.setText( "remove from " + name );
                menuItem.addActionListener( 
                    new RemoveActionListener( programme, name ) );
            }
            else if( !currentState.booleanValue(  ) )
            {
                menuItem.setText( "set default for " + name );
                menuItem.addActionListener( 
                    new DefaultActionListener( programme, name ) );
            }

            menu.add( menuItem );
        }
    }

    /**
     * Allow remind programme.
     */
    public class AddActionListener implements ActionListener
    {
        protected final String reminderName;
        protected final TVProgramme programme;

/**
         * Creates a new AddActionListener object.
         * 
         * @param config
         *            DOCUMENT ME!
         * @param programme
         *            DOCUMENT ME!
         * @param reminderName
         *            DOCUMENT ME!
         */
        public AddActionListener( 
            final TVProgramme programme, final String reminderName )
        {
            this.programme = programme;
            this.reminderName = reminderName;
        }

        /**
         * Event.
         *
         * @param e event
         */
        public void actionPerformed( ActionEvent e )
        {
            // find in selections
            for( final ManualSelection sel : parent.config.manualSelectionList )
            {
                if( sel.matches( programme ) )
                {
                    sel.reminders.put( reminderName, Boolean.TRUE );

                    parent.reschedule(  );

                    return;
                }
            }

            final ManualSelection sel = new ManualSelection( programme );
            sel.reminders.put( reminderName, Boolean.TRUE );
            parent.config.manualSelectionList.add( sel );

            parent.reschedule(  );
        }
    }

    /**
     * Disable remind programme.
     */
    public class RemoveActionListener implements ActionListener
    {
        protected final String reminderName;
        protected final TVProgramme programme;

/**
         * Creates a new RemoveActionListener object.
         * 
         * @param config
         *            DOCUMENT ME!
         * @param programme
         *            DOCUMENT ME!
         * @param reminderName
         *            DOCUMENT ME!
         */
        public RemoveActionListener( 
            final TVProgramme programme, final String reminderName )
        {
            this.programme = programme;
            this.reminderName = reminderName;
        }

        /**
         * Event.
         *
         * @param e event
         */
        public void actionPerformed( ActionEvent e )
        {
            // find in selections
            for( final ManualSelection sel : parent.config.manualSelectionList )
            {
                if( sel.matches( programme ) )
                {
                    sel.reminders.put( reminderName, Boolean.FALSE );

                    parent.reschedule(  );

                    return;
                }
            }

            final ManualSelection sel = new ManualSelection( programme );
            sel.reminders.put( reminderName, Boolean.FALSE );
            parent.config.manualSelectionList.add( sel );

            parent.reschedule(  );
        }
    }

    /**
     * Set reminder to default for programme(as favourites settings).
     */
    public class DefaultActionListener implements ActionListener
    {
        protected final String reminderName;
        protected final TVProgramme programme;

/**
         * Creates a new DefaultActionListener object.
         * 
         * @param config
         *            DOCUMENT ME!
         * @param programme
         *            DOCUMENT ME!
         * @param reminderName
         *            DOCUMENT ME!
         */
        public DefaultActionListener( 
            final TVProgramme programme, final String reminderName )
        {
            this.programme = programme;
            this.reminderName = reminderName;
        }

        /**
         * Event.
         *
         * @param e event
         */
        public void actionPerformed( ActionEvent e )
        {
            // find in selections
            for( 
                final Iterator<ManualSelection> it =
                    parent.config.manualSelectionList.iterator(  );
                    it.hasNext(  ); )
            {
                final ManualSelection sel = it.next(  );

                if( sel.matches( programme ) )
                {
                    sel.reminders.remove( reminderName );

                    if( sel.reminders.size(  ) == 0 )
                    {
                        it.remove(  );
                    }

                    parent.reschedule(  );

                    return;
                }
            }
        }
    }
}
