package freeguide.plugins;

import freeguide.FreeGuide;

import freeguide.gui.viewer.MainController;

import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.selection.Favourite;
import freeguide.lib.fgspecific.selection.ManualSelection;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Base class for support reminders.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
abstract public class BaseModuleReminder extends BaseModule
    implements IModuleReminder
{

    final protected static String MENU_ITEM_LABEL = "menu.label";
    final protected Config config = new Config(  );
    protected Scheduler thread;

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {
        thread = new Scheduler(  );
        thread.start(  );
    }

    /**
     * DOCUMENT_ME!
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
     * DOCUMENT_ME!
     *
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToMenu( final JMenu menu )
    {

        JMenuItem item = new JMenuItem(  );
        item.setText( i18n.getString( MENU_ITEM_LABEL ) );
        menu.add( item );
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
     * DOCUMENT_ME!
     */
    public void reschedule(  )
    {

        if( thread != null )
        {
            thread.reschedule(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prog DOCUMENT_ME!
     * @param label DOCUMENT_ME!
     */
    public void onPaintProgrammeLabel( 
        final TVProgramme prog, final JLabel label )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prog DOCUMENT_ME!
     * @param graphics DOCUMENT_ME!
     */
    public void onPaintProgrammeLabel( 
        final TVProgramme prog, final Graphics graphics )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Favourite getFavourite( TVProgramme programme )
    {

        synchronized( config )
        {

            for( int i = 0; i < config.favouritesList.size(  ); i++ )
            {

                Favourite fav = (Favourite)config.favouritesList.get( i );

                if( fav.matches( programme ) )
                {

                    return fav;

                }
            }
        }

        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public ManualSelection getManualSelection( TVProgramme programme )
    {

        synchronized( config )
        {

            for( int i = 0; i < config.manualSelectionList.size(  ); i++ )
            {

                ManualSelection sel =
                    (ManualSelection)config.manualSelectionList.get( i );

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
     * @param programme DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isSelected( TVProgramme programme )
    {

        synchronized( config )
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
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public void selectProgramme( TVProgramme programme )
    {

        synchronized( config )
        {

            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                sel.setSelected( true );

            }

            else
            {
                config.manualSelectionList.add( 
                    new ManualSelection( programme, true ) );

            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public void deselectProgramme( TVProgramme programme )
    {

        synchronized( config )
        {

            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                sel.setSelected( false );

            }

            else
            {
                config.manualSelectionList.add( 
                    new ManualSelection( programme, false ) );

            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourite DOCUMENT_ME!
     */
    public void addFavourite( final Favourite favourite )
    {

        synchronized( config )
        {
            config.favouritesList.add( favourite );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourite DOCUMENT_ME!
     */
    public void removeFavourite( final Favourite favourite )
    {

        synchronized( config )
        {
            config.favouritesList.remove( favourite );

        }
    }

    abstract protected void onTime(  );

    abstract protected long getNextTime(  );

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Config
    {

        /** DOCUMENT ME! */
        public static final Class favouritesList_TYPE = Favourite.class;

        /** DOCUMENT ME! */
        public static final Class manualSelectionList_TYPE =
            ManualSelection.class;

        /** DOCUMENT ME! */
        public List favouritesList = new ArrayList(  );

        /** DOCUMENT ME! */
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
                    FreeGuide.log.log( 
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
                notify(  );
            }
        }

        /*try
        {

            long schedTime;

            if(
                ( closeDialogTime != 0 )
                    && ( schedTime > closeDialogTime ) )
            {
                schedTime = closeDialogTime;
            }

            long waitTime = schedTime - System.currentTimeMillis(  );

            if( waitTime < 10 )
            {
                waitTime = 10;

            }

            wait( waitTime );

            if( stop )
            {

                break;

            }

            if( dialog != null )
            {
                dialog.dispose(  );

                dialog = null;

            }

            closeDialogTime = 0;

            if( scheduledProgramme != null )
            {

                if(
                    ( scheduledProgramme.getStart(  )
                        - MainController.config.reminderWarning ) < System
                        .currentTimeMillis(  ) )
                {
                    displayDialog(  );

                    closeDialogTime =
                        System.currentTimeMillis(  )
                        + MainController.config.reminderGiveUp;

                }
            }

            scheduledProgramme = findNextProgramme(  );
        }
        catch( InterruptedException ex )
        {
            FreeGuide.log.log(
                Level.WARNING, "Reminder thread interrupted ", ex );
        }*/
    }
}
