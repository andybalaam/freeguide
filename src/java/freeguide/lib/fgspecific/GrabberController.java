package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.ExecutorDialog;

import freeguide.gui.viewer.MainController;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorChannels;

import freeguide.plugins.IModuleGrabber;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * Class for run specified grabber and display progress dialog.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class GrabberController
{

    protected ExecutorDialog progressDialog;
    protected JProgressBar secondProgressBar;
    protected boolean wasError;
    protected IModuleGrabber currentGrabber;
    protected boolean isFinished;

    /**
     * Show grabber dialog when grabbing rinning, or start grabbing in new
     * thread.
     *
     * @param controller DOCUMENT ME!
     */
    public void activate( final MainController controller )
    {

        synchronized( this )
        {

            if( progressDialog != null )
            {

                // Show dialog
                progressDialog.setVisible( true );
            }
            else
            {

                // Start new grabbing
                new Thread(  )
                    {
                        public void run(  )
                        {
                            FreeGuide.log.finest( "start grabbing" );
                            grab( 
                                controller.getApplicationFrame(  ),
                                controller.mainFrame.getProgressBar(  ) );
                            controller.viewer.onDataChanged(  );
                            controller.remindersReschedule(  );
                            FreeGuide.log.finest( "stop grabbing" );
                        }
                    }.start(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param owner DOCUMENT_ME!
     * @param secondProgressBar DOCUMENT ME!
     */
    public void grab( JFrame owner, JProgressBar secondProgressBar )
    {
        this.secondProgressBar = secondProgressBar;

        synchronized( this )
        {
            wasError = false;
            currentGrabber = null;
            isFinished = false;
            progressDialog = new ExecutorDialog( owner, secondProgressBar );

            progressDialog.getCancelButton(  ).addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent evt )
                    {
                        closeDialog(  );
                    }
                } );
        }

        secondProgressBar.setVisible( true );
        new Thread(  )
            {
                public void run(  )
                {
                    progressDialog.setVisible( true );
                }
            }.start(  );

        Iterator it = MainController.config.activeGrabberIDs.iterator(  );

        while( it.hasNext(  ) )
        {

            String grabberID = (String)it.next(  );

            try
            {

                IModuleGrabber grabber =
                    (IModuleGrabber)PluginsManager.getModuleByID( grabberID );

                if( grabber == null )
                {
                    FreeGuide.log.warning( "There is no grabber " + grabberID );

                    continue;

                }

                synchronized( this )
                {
                    currentGrabber = grabber;
                }

                if( isFinished )
                {

                    break;
                }

                TVData result =
                    grabber.grabData( progressDialog, progressDialog );

                if( isFinished )
                {

                    break;
                }

                if( result != null )
                {
                    result.iterate( 
                        new TVIteratorChannels(  )
                        {
                            protected void onChannel( TVChannel channel )
                            {
                                channel.normalizeTime(  );
                            }
                        } );
                    FreeGuide.storage.add( result );
                }
            }
            catch( Exception ex )
            {
                wasError = true;
                progressDialog.error( 
                    "Error grab data by grabber '" + grabberID + "'", ex );

                FreeGuide.log.log( 
                    Level.WARNING, "Error grab data by grabber '" + grabberID,
                    ex );
            }
        }

        if( !wasError && !progressDialog.isLogVisible(  ) )
        {
            closeDialog(  );
        }
        else
        {

            synchronized( this )
            {
                secondProgressBar.setVisible( false );
                progressDialog.setDefaultCloseOperation( 
                    JDialog.DISPOSE_ON_CLOSE );
                progressDialog.setCloseLabel(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void closeDialog(  )
    {

        synchronized( this )
        {
            isFinished = true;

            if( currentGrabber != null )
            {
                currentGrabber.stop(  );
            }

            secondProgressBar.setVisible( false );

            // leave dialog when details open or was error
            if( progressDialog != null )
            {
                progressDialog.dispose(  );
                progressDialog = null;
            }
        }
    }
}
