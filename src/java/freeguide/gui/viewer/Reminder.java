package freeguide.gui.viewer;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.selection.SelectionManager;

import freeguide.plugins.IStorage;

import java.util.Timer;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 * Reminder for selected programmes.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class Reminder extends Thread
{

    protected boolean isStopped = false;
    protected JDialog dialog;
    protected Timer timerOpen;
    protected Timer timerClose;
    protected boolean stop = false;
    protected TVProgramme scheduledProgramme;

    /**
     * DOCUMENT_ME!
     */
    public void reSchedule(  )
    {

        synchronized( this )
        {
            scheduledProgramme = null;

            notify(  );

        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void run(  )
    {

        long closeDialogTime = 0;

        synchronized( this )
        {

            while( !stop )
            {

                try
                {

                    long schedTime;

                    if( scheduledProgramme != null )
                    {
                        schedTime =
                            scheduledProgramme.getStart(  )
                            - MainController.config.reminderWarning;
                    }

                    else
                    {
                        schedTime = Long.MAX_VALUE;

                    }

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
                }
            }
        }
    }

    protected void displayDialog(  )
    {

        String message =
            FreeGuide.msg.getLocalizedMessage( 
                "is_starting_soon_template",
                new Object[] { scheduledProgramme.getTitle(  ) } );

        JOptionPane optionPane =
            new JOptionPane( message, JOptionPane.INFORMATION_MESSAGE );

        dialog =
            optionPane.createDialog( 
                null, FreeGuide.msg.getString( "freeguide_reminder" ) );

        dialog.setModal( false );

        dialog.setVisible( true );

    }

    protected TVProgramme findNextProgramme(  )
    {

        if( MainController.config.reminderOn )
        {

            try
            {

                return FreeGuide.storage.findEarliest( 
                    System.currentTimeMillis(  )
                    + MainController.config.reminderWarning,
                    new IStorage.EarliestCheckAllow(  )
                    {
                        public boolean isAllow( TVProgramme programme )
                        {

                            return SelectionManager.isInGuide( programme );

                        }
                    } );
            }

            catch( Exception ex )
            {
                FreeGuide.log.log( 
                    Level.WARNING, "Error find next programme", ex );
            }
        }

        return null;

    }
}
