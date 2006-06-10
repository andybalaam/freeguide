package freeguide.plugins.reminder.alarm;

import freeguide.common.gui.FavouritesController;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.BaseModuleReminder;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleReminder;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IModuleViewer;

import freeguide.plugins.reminder.alarm.AlarmUIController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

/**
 * Alarm reminder module. It works like previous reminder and just display
 * prompt.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class AlarmReminder extends BaseModuleReminder
    implements IModuleReminder
{
    /** Config object. */
    protected final ConfigAlarm config = new ConfigAlarm(  );
    protected TVProgramme scheduledProgramme;
    protected JDialog scheduledDialog;
    protected long timeForClose;
    protected long timeForDisplay;
    protected long scheduledDialogDisplayTime;
    protected MListsner mouseListener = new MListsner(  );

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return config;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return new AlarmUIController( this, parentDialog );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param menu DOCUMENT_ME!
     */
    public void addItemsToPopupMenu( 
        final TVProgramme programme, final JPopupMenu menu )
    {
        final JMenuItem sel = new JMenuItem(  );

        if( !isSelected( programme ) )
        {
            sel.setText( i18n.getLocalizedMessage( "popup.selection.add" ) );
            sel.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setProgrammeSelection( programme, true );
                        favSelectionChanged( true );
                    }
                } );
        }
        else
        {
            sel.setText( i18n.getLocalizedMessage( "popup.selection.del" ) );
            sel.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setProgrammeSelection( programme, false );
                        favSelectionChanged( true );
                    }
                } );
        }

        menu.add( sel );

        final JMenuItem fav = new JMenuItem(  );

        if( getFavourite( programme ) == null )
        {
            fav.setText( i18n.getLocalizedMessage( "popup.favourite.add" ) );
            fav.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        Favourite f = new Favourite(  );

                        f.setTitleString( programme.getTitle(  ) );

                        f.setName( programme.getTitle(  ) );
                        addFavourite( f );
                        favSelectionChanged( false );
                    }
                } );
        }
        else
        {
            fav.setText( i18n.getLocalizedMessage( "popup.favourite.del" ) );
            fav.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        Favourite fav = getFavourite( programme );

                        if( fav != null )
                        {
                            removeFavourite( fav );
                        }
                    }
                } );
        }

        menu.add( fav );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourite DOCUMENT_ME!
     */
    public void removeFavourite( final Favourite favourite )
    {
        Object[] messageArguments = { favourite.getName(  ) };

        int r =
            JOptionPane.showConfirmDialog( 
                null, //controller.getPanel(  ),
                i18n.getLocalizedMessage( 
                    "popup.favourite.del.prompt", messageArguments ),
                i18n.getLocalizedMessage( "popup.favourite.del.title" ),
                JOptionPane.YES_NO_OPTION );

        if( r == 0 )
        {
            super.removeFavourite( favourite );
            favSelectionChanged( false );
        }
    }

    protected void onMenuItem(  )
    {
        FavouritesController favController =
            new FavouritesController( 
                Application.getInstance(  ).getApplicationFrame(  ),
                config.favouritesList,
                Application.getInstance(  ).getDataStorage(  ).getInfo(  ).channelsList );

        Utils.centreDialog( 
            Application.getInstance(  ).getApplicationFrame(  ),
            favController.getListDialog(  ) );
        favController.getListDialog(  ).setVisible( true );

        if( favController.isChanged(  ) )
        {
            config.favouritesList = favController.getFavourites(  );

            saveConfigNow(  );

            Application.getInstance(  ).getViewer(  ).redraw(  );

            reschedule(  );
        }
    }

    protected void favSelectionChanged( final boolean onlyOneProgramme )
    {
        IModuleViewer viewer = Application.getInstance(  ).getViewer(  );

        if( viewer != null )
        {
            if( onlyOneProgramme )
            {
                viewer.redrawCurrentProgramme(  );
            }
            else
            {
                viewer.redraw(  );
            }
        }

        saveConfigNow(  );
    }

    protected long getNextTime(  )
    {
        timeForClose = Long.MAX_VALUE;
        timeForDisplay = Long.MAX_VALUE;

        synchronized( this )
        {
            if( scheduledDialog != null )
            {
                timeForClose = scheduledDialogDisplayTime
                    + config.reminderGiveUp;
            }

            scheduledProgramme = null;

            if( config.reminderOn )
            {
                try
                {
                    scheduledProgramme =
                        Application.getInstance(  ).getDataStorage(  ).findEarliest( 
                            System.currentTimeMillis(  )
                            + config.reminderWarning,
                            new IModuleStorage.EarliestCheckAllow(  )
                            {
                                public boolean isAllow( TVProgramme programme )
                                {
                                    return isSelected( programme );

                                }
                            } );

                    if( scheduledProgramme != null )
                    {
                        timeForDisplay = scheduledProgramme.getStart(  )
                            - config.reminderWarning;
                    }
                }

                catch( Exception ex )
                {
                    Application.getInstance(  ).getLogger(  )
                               .log( 
                        Level.WARNING, "Error find next programme", ex );
                }
            }

            return Math.min( 
                Math.min( timeForClose, timeForDisplay ),
                System.currentTimeMillis(  ) + 300000 );
        }
    }

    protected void onTime(  )
    {
        synchronized( this )
        {
            if( scheduledDialog != null )
            {
                if( 
                    ( timeForClose <= System.currentTimeMillis(  ) )
                        || ( timeForDisplay <= System.currentTimeMillis(  ) ) )
                {
                    scheduledDialog.dispose(  );
                    scheduledDialog = null;
                }
            }

            if( 
                ( scheduledProgramme != null )
                    && ( timeForDisplay <= System.currentTimeMillis(  ) ) )
            {
                String message =
                    i18n.getLocalizedMessage( 
                        "alarm.text",
                        new Object[] { scheduledProgramme.getTitle(  ) } );

                JOptionPane optionPane =
                    new JOptionPane( message, JOptionPane.INFORMATION_MESSAGE );

                scheduledDialog = optionPane.createDialog( 
                        Application.getInstance(  ).getApplicationFrame(  ),
                        i18n.getString( "alarm.title" ) );

                scheduledDialog.setModal( false );

                scheduledDialog.setVisible( true );
                scheduledDialogDisplayTime = System.currentTimeMillis(  );
            }
        }
    }

    protected class MListsner implements MouseListener
    {
        TVProgramme programme;

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseClicked( MouseEvent e )
        {
            if( e.getClickCount(  ) <= 1 )
            {
                return;
            }

            if( !isSelected( programme ) )
            {
                setProgrammeSelection( programme, true );
            }
            else
            {
                setProgrammeSelection( programme, false );
            }

            favSelectionChanged( true );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseEntered( MouseEvent e )
        {
            // TODO Auto-generated method stub
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseExited( MouseEvent e )
        {
            // TODO Auto-generated method stub
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mousePressed( MouseEvent e )
        {
            // TODO Auto-generated method stub
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseReleased( MouseEvent e )
        {
            // TODO Auto-generated method stub
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class ConfigAlarm extends Config
    {
        /** Is reminder on. */
        public boolean reminderOn = true;

        /** Time in milliseconds. */
        public long reminderGiveUp = 600000L;

        /** Time in milliseconds. */
        public long reminderWarning = 300000L;
    }
}
