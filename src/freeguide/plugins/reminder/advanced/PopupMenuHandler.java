package freeguide.plugins.reminder.advanced;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class PopupMenuHandler
{
    /**
     * DOCUMENT_ME!
     *
     * @param menu DOCUMENT_ME!
     * @param programme DOCUMENT_ME!
     * @param parent DOCUMENT_ME!
     */
    public static void fillMenu( 
        final JPopupMenu menu, final TVProgramme programme,
        final AdvancedReminder parent )
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
                    new AddActionListener( parent.config, programme, name ) );
            }
            else if( currentState.booleanValue(  ) )
            {
                menuItem.setText( "remove from " + name );
                menuItem.addActionListener( 
                    new RemoveActionListener( parent.config, programme, name ) );
            }
            else if( !currentState.booleanValue(  ) )
            {
                menuItem.setText( "set default for " + name );
                menuItem.addActionListener( 
                    new DefaultActionListener( parent.config, programme, name ) );
            }

            menu.add( menuItem );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class AddActionListener implements ActionListener
    {
        protected final AdvancedReminder.Config config;
        protected final String reminderName;
        protected final TVProgramme programme;

        /**
         * Creates a new AddActionListener object.
         *
         * @param config DOCUMENT ME!
         * @param programme DOCUMENT ME!
         * @param reminderName DOCUMENT ME!
         */
        public AddActionListener( 
            final AdvancedReminder.Config config, final TVProgramme programme,
            final String reminderName )
        {
            this.config = config;
            this.programme = programme;
            this.reminderName = reminderName;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {
            // find in selections
            for( final ManualSelection sel : config.manualSelectionList )
            {
                if( sel.matches( programme ) )
                {
                    sel.reminders.put( reminderName, Boolean.TRUE );

                    return;
                }
            }

            final ManualSelection sel = new ManualSelection( programme );
            sel.reminders.put( reminderName, Boolean.TRUE );
            config.manualSelectionList.add( sel );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class RemoveActionListener implements ActionListener
    {
        protected final AdvancedReminder.Config config;
        protected final String reminderName;
        protected final TVProgramme programme;

        /**
         * Creates a new RemoveActionListener object.
         *
         * @param config DOCUMENT ME!
         * @param programme DOCUMENT ME!
         * @param reminderName DOCUMENT ME!
         */
        public RemoveActionListener( 
            final AdvancedReminder.Config config, final TVProgramme programme,
            final String reminderName )
        {
            this.config = config;
            this.programme = programme;
            this.reminderName = reminderName;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {
            // find in selections
            for( final ManualSelection sel : config.manualSelectionList )
            {
                if( sel.matches( programme ) )
                {
                    sel.reminders.put( reminderName, Boolean.FALSE );

                    return;
                }
            }

            final ManualSelection sel = new ManualSelection( programme );
            sel.reminders.put( reminderName, Boolean.FALSE );
            config.manualSelectionList.add( sel );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
      */
    public static class DefaultActionListener implements ActionListener
    {
        protected final AdvancedReminder.Config config;
        protected final String reminderName;
        protected final TVProgramme programme;

        /**
         * Creates a new DefaultActionListener object.
         *
         * @param config DOCUMENT ME!
         * @param programme DOCUMENT ME!
         * @param reminderName DOCUMENT ME!
         */
        public DefaultActionListener( 
            final AdvancedReminder.Config config, final TVProgramme programme,
            final String reminderName )
        {
            this.config = config;
            this.programme = programme;
            this.reminderName = reminderName;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {
            // find in selections
            for( final ManualSelection sel : config.manualSelectionList )
            {
                if( sel.matches( programme ) )
                {
                    sel.reminders.remove( reminderName );

                    return;
                }
            }
        }
    }
}
