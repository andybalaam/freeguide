package freeguide.common.plugininterfaces;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.fgspecific.selection.ManualSelection;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Base class for support reminders. It supports scheduler thread for call
 * event on scheduled time.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class BaseModuleReminder extends BaseModule
    implements IModuleReminder
{
    protected Scheduler thread;

    /**
     * The maximal age of a selection. If it's older, it gets deleted in
     * cleanup() 86400 (seconds in 1 day)  7 (days/week) 4 (weeks)  1000 (to
     * make milliseconds)
     */
    private static final long MAX_SELECTION_AGE = (long)86400 * 7 * 4 * 1000;

    /**
     * Start scheduler thread.
     */
    public void start(  )
    {
        thread = new Scheduler(  );
        thread.start(  );
    }

    /**
     * Stop scheduler thread.
     */
    public void stop(  )
    {
        if( thread != null )
        {
            thread.finish(  );
            thread = null;
        }
    }

    /**
     * Public for test only.
     *
     * @return our config object cast to the base
     * reminder config type.
     */
    public Config getReminderConfig(  )
    {
        return (Config)getConfig(  );
    }

    /**
     * Add items to main frame menu.
     *
     * @param menu main frame menu
     */
    public void addItemsToMenu( final JMenu menu )
    {
        JMenuItem item = new JMenuItem(  );
        item.setText( i18n.getString( "menu.label" ) );
        menu.insert( item, 0 );
        item.addActionListener(
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    onMenuItem(  );
                }
            } );
    }

    abstract protected void onMenuItem(  );

    /**
     * Read programmes and schedule again.
     */
    public void reschedule(  )
    {
        if( thread != null )
        {
            thread.reschedule(  );
        }
    }

    /**
     * Get favourite description for programme.
     *
     * @param programme programme
     *
     * @return favourite description or null if there is no favourite found
     */
    public Favourite getFavourite( TVProgramme programme )
    {
        synchronized( getReminderConfig(  ) )
        {
            for( int i = 0; i < getReminderConfig(  ).favouritesList.size(  );
                    i++ )
            {
                Favourite fav =
                    (Favourite)getReminderConfig(  ).favouritesList
                    .get( i );

                if( fav.matches( programme ) )
                {
                    return fav;

                }
            }
        }

        return null;
    }

    /**
     * Get manual selection description for programme.
     *
     * @param programme programme
     *
     * @return selection description or null if there is no selection found
     */
    public ManualSelection getManualSelection( TVProgramme programme )
    {
        synchronized( getReminderConfig(  ) )
        {
            for(
                int i = 0;
                    i < getReminderConfig(  ).manualSelectionList.size(  );
                    i++ )
            {
                ManualSelection sel =
                    (ManualSelection)getReminderConfig(  ).manualSelectionList
                    .get( i );

                if( sel.matches( programme ) )
                {
                    return sel;

                }
            }
        }

        return null;
    }

    /**
     * Check if programme in the favourites or manual selection list.
     *
     * @param programme programme
     *
     * @return true if programme selected
     */
    public boolean isSelected( TVProgramme programme )
    {
        synchronized( getReminderConfig(  ) )
        {
            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                return sel.isSelected(  );

            }

            else
            {
                Favourite fav = getFavourite( programme );

                return fav != null;

            }
        }
    }

    /**
     * Add/remove programme to selection list.
     *
     * @param programme programme
     * @param newSelection DOCUMENT ME!
     */
    public void setProgrammeSelection(
        final TVProgramme programme, final boolean newSelection )
    {
        synchronized( getReminderConfig(  ) )
        {
            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                sel.setSelected( newSelection );

            }

            else
            {
                getReminderConfig(  ).manualSelectionList.add(
                    new ManualSelection( programme, newSelection ) );

            }
        }
    }

    /**
     * Add new favourite.
     *
     * @param favourite favourite description
     */
    public void addFavourite( final Favourite favourite )
    {
        synchronized( getReminderConfig(  ) )
        {
            getReminderConfig(  ).favouritesList.add( favourite );

        }
    }

    /**
     * Remove favourite.
     *
     * @param favourite favourite description
     */
    public void removeFavourite( final Favourite favourite )
    {
        synchronized( getReminderConfig(  ) )
        {
            getReminderConfig(  ).favouritesList.remove( favourite );

        }
    }

    abstract protected void onTime(  );

    abstract protected long getNextTime(  );

    /**
     * Favourite and selection lists storage.
     */
    public static class Config
    {
        /** Favourite element type. */
        public static final Class favouritesList_TYPE = Favourite.class;

        /** Selection element type. */
        public static final Class manualSelectionList_TYPE =
            ManualSelection.class;

        /** Favourites list. */
        public List favouritesList = new ArrayList(  );

        /** Selections list. */
        public List manualSelectionList = new ArrayList(  );
    }

    protected class Scheduler extends Thread
    {
        protected static final int STATE_WORK = 0;
        protected static final int STATE_RESCHEDULE = 1;
        protected static final int STATE_STOP = 99;
        protected int state = STATE_RESCHEDULE;
        protected long scheduledTime;

        /**
         * DOCUMENT_ME!
         */
        public void run(  )
        {
            synchronized( this )
            {
                try
                {
                    while( true )
                    {
                        switch( state )
                        {
                        case STATE_WORK:
                            onTime(  );
                            scheduledTime = getNextTime(  );

                            break;

                        case STATE_RESCHEDULE:
                            scheduledTime = getNextTime(  );
                            state = STATE_WORK;

                            break;

                        case STATE_STOP:
                            return;
                        }

                        long waitTime =
                            Math.max(
                                scheduledTime - System.currentTimeMillis(  ),
                                10 );

                        wait( waitTime );
                    }
                }
                catch( InterruptedException ex )
                {
                    Application.getInstance(  ).getLogger(  )
                               .log(
                        Level.WARNING, "Reminder thread interrupted ", ex );
                }
            }
        }

        protected void reschedule(  )
        {
            synchronized( this )
            {
                state = STATE_RESCHEDULE;
                notify(  );
            }
        }

        protected void finish(  )
        {
            synchronized( this )
            {
                state = STATE_STOP;
                notifyAll(  );
            }

            try
            {
                join(  );
            }
            catch( InterruptedException ex )
            {
            }
        }
    }

    /**
     *  Delete old selections so they don't slow us down
     *  in future.
     */
    public void cleanup( long timeNow )
    {
        ArrayList<ManualSelection> to_remove = new ArrayList<ManualSelection>();

        List selections = getReminderConfig(  ).manualSelectionList;
        for( Iterator sit = selections.iterator(); sit.hasNext(); )
        {
            ManualSelection sel = (ManualSelection)sit.next();
            if( timeNow - sel.programmeTime > MAX_SELECTION_AGE )
            {
                to_remove.add( sel );
            }
        }

        selections.removeAll( to_remove );
    }
}
