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
    protected TVProgramme recordingProgramme;
    protected JDialog scheduledDialog;
    protected long timeForClose;
    protected long timeForDisplay;
    protected long scheduledDialogDisplayTime;
    protected long timeForRecordStart;
    protected long timeForRecordStop;
    protected boolean recording = false;
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
    	
    	/**
    	 * menu item for highlighting
    	 *
    	 * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
    	 */
        final JMenuItem hil = new JMenuItem(  ); 
        
        // is highlighted?
        if( !isHighlighted( programme ) )
        {
            hil.setText( i18n.getLocalizedMessage( "popup.highlight.add" ) );
            hil.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setProgrammeSelection( programme, isSelected(programme) , true);
                        favSelectionChanged( true );
                    }
                } );
        }
        else
        {
            hil.setText( i18n.getLocalizedMessage( "popup.highlight.del" ) );
            hil.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                    	setProgrammeSelection( programme, isSelected(programme), false);
                        favSelectionChanged( true );
                    }
                } );
        }

        menu.add( hil );
    	
        final JMenuItem sel = new JMenuItem(  );

        if( !isSelected( programme ) )
        {
            sel.setText( i18n.getLocalizedMessage( "popup.selection.add" ) );
            sel.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setProgrammeSelection( programme, true, true);
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
                        setProgrammeSelection( programme, false, false);
                        favSelectionChanged( true );
                    }
                } );
        }

        menu.add( sel );

        final JMenuItem rec = new JMenuItem(  );

        Favourite favourite = getFavourite( programme );

        if( favourite == null || !favourite.getRecord() )
        {
            rec.setText( i18n.getLocalizedMessage( "popup.record.add" ) );
            if( favourite == null )
            { // not yet in favourites, hence add.
              rec.addActionListener( 
                  new ActionListener(  )
                  {
                      public void actionPerformed( ActionEvent e )
                      {
                          Favourite fav = new Favourite(  );

                          fav.setTitleString( programme.getTitle(  ) );
                          fav.setName( programme.getTitle(  ) );
                          fav.setRecord( true );

                          addFavourite( fav );
                          favSelectionChanged( false );
                      }
                  } );
           }
           else // if( !f.getRecord() )
           { // is in favourites but not yet as recorded.
              rec.addActionListener( 
                  new ActionListener(  )
                  {
                      public void actionPerformed( ActionEvent e )
                      {
                          Favourite fav = getFavourite( programme );
                          if( fav != null )
                          {
                            fav.setRecord( true );
                            favSelectionChanged( false );
                          }
                      }
                  } );
           }
        }
        else
        {
            rec.setText( i18n.getLocalizedMessage( "popup.record.del" ) );
            rec.addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        Favourite fav = getFavourite( programme );
                        if( fav != null )
                        {
                          fav.setRecord( false );
                          favSelectionChanged( false );
                        }
                    }
                } );
        }

        menu.add( rec );

        final JMenuItem fav = new JMenuItem(  );

        if( favourite == null )
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
        timeForRecordStart = Long.MAX_VALUE;
        timeForRecordStop = Long.MAX_VALUE;

        synchronized( this )
        {
            if( scheduledDialog != null )
            {
                timeForClose = scheduledDialogDisplayTime
                    + config.reminderGiveUp;
            }

            scheduledProgramme = null;

            if( config.reminderOn)
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
            if( config.recordOn)
            {
                try
                {
                   if( !recording )
                   { 
                      recordingProgramme = 
                        Application.getInstance(  ).getDataStorage(  ).findEarliest( 
                            System.currentTimeMillis(  )
                            + 0, // 1min before actual start.
                            new IModuleStorage.EarliestCheckAllow(  )
                            {
                                public boolean isAllow( TVProgramme programme )
                                {
                                    return isRecord( programme );
                                }
                            } );
                   }
                   if( recordingProgramme != null )
                   {
                     if( !recording )
                        timeForRecordStart = recordingProgramme.getStart(  ) - 0;
                      timeForRecordStop = recordingProgramme.getEnd( ) + 0;
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
                Math.min( Math.min( Math.min( timeForClose, timeForDisplay ), timeForRecordStart ), timeForRecordStop ),
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
                        i18n.getLocalizedMessage( "alarm.title" ) );

                scheduledDialog.setModal( false );

                scheduledDialog.setVisible( true );
                scheduledDialogDisplayTime = System.currentTimeMillis(  );
            }
            // check for recording start
            if( recordingProgramme != null )
            {
              if( !recording && timeForRecordStart <= System.currentTimeMillis(  ) )
              {
                startRecording();
                recording = true;
              }
              if( recording && timeForRecordStop <= System.currentTimeMillis(  ) )
              {
                stopRecording();
                recordingProgramme = null;
                recording = false;
              }
        }
    }

    }

    /**
     * Is called when a recording should be started.
     * 
     * The programme to record is stored in recrdingProgramme.
     * 
     * @return true if command execution was successfull
     * @see onTime()
     */
    protected boolean startRecording(  )
    {
      JOptionPane optionPane =
          new JOptionPane( recordingProgramme.getTitle(  ), JOptionPane.INFORMATION_MESSAGE );
      JDialog dlg = optionPane.createDialog( 
              Application.getInstance(  ).getApplicationFrame(  ),
              "Start Recording..." );
                dlg.setModal( false );

                dlg.setVisible( true );

      return true;

    }

    /**
     * Is called when a recording should be sopped.
     * 
     * The programme we are currently recording is stored in recrdingProgramme.
     * 
     * @return true if command execution was successfull
     * @see onTime()
     */
    protected boolean stopRecording(  )
    {
      JOptionPane optionPane =
          new JOptionPane( recordingProgramme.getTitle(  ), JOptionPane.INFORMATION_MESSAGE );
      JDialog dlg = optionPane.createDialog( 
              Application.getInstance(  ).getApplicationFrame(  ),
              "Stop Recording..." );
                dlg.setModal( false );

                dlg.setVisible( true );
      return true;
    }

    
	/**
	 * setProgrammSelection: set highlighting according to the selection
	 *
	 * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
	 */

    protected class MListsner implements MouseListener
    {
        TVProgramme programme;

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         * 
	     * @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
	     * add parameter for highlight
         */
        public void mouseClicked( MouseEvent e )
        {
            if( e.getClickCount(  ) <= 1 )
            {
                return;
            }

            if( !isSelected( programme ) )
            {
                setProgrammeSelection( programme, true, true);
            }
            else
            {
                setProgrammeSelection( programme, false, false);
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

        /** Do we record. */
        public boolean recordOn = false;

        /** Command to issue when a programme starts. */
        public String recordStartCommand = "";

        /** Command to issue when a programme stops. */
        public String recordStopCommand = "";
    }
}
