package freeguide.plugins.program.freeguide.lib.fgspecific;

import freeguide.common.gui.ExecutorDialog;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.IModuleGrabber;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.viewer.MainController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.nio.channels.ClosedByInterruptException;

import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.JButton;
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
    protected Thread grabberThread;

    /**
     * Show grabber dialog when grabbing running, or start grabbing in
     * new thread.
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
                new Thread(  )
                    {
                        public void run(  )
                        {
                            progressDialog.setVisible( true );
                        }
                    }.start(  );
            }
            else
            {
                // Start new grabbing
                grabberThread =
                    new Thread(  )
                        {
                            public void run(  )
                            {
                                FreeGuide.log
                                .finest( "start grabbing" );

                                try
                                {
                                    grab( 
                                        controller.getApplicationFrame(  ),
                                        controller.mainFrame.getProgressBar(  ),
                                        controller.mainFrame
                                        .getForegroundButton(  ) );
                                    controller.viewer.onDataChanged(  );
                                    MainController.remindersReschedule(  );
                                }
                                catch( Exception ex )
                                {
                                    ex.printStackTrace(  );
                                }

                                FreeGuide.log.finest( "stop grabbing" );
                            }
                        };
                grabberThread.start(  );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param owner DOCUMENT_ME!
     * @param secondProgressBar DOCUMENT ME!
     * @param foregroundButton DOCUMENT ME!
     */
    public void grab( 
        final JFrame owner, final JProgressBar secondProgressBar,
        final JButton foregroundButton )
    {
        this.secondProgressBar = secondProgressBar;

        synchronized( this )
        {
            wasError = false;
            progressDialog = new ExecutorDialog( 
                    owner, secondProgressBar, foregroundButton );
            progressDialog.setStepCount( 1 );
            progressDialog.setStepNumber( 0 );

            progressDialog.getCancelButton(  ).addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent evt )
                    {
                        synchronized( GrabberController.this )
                        {
                            grabberThread.interrupt(  );

                            // leave dialog when details open or was error
                            if( progressDialog != null )
                            {
                                progressDialog.dispose(  );
                                progressDialog = null;
                            }
                        }
                    }
                } );

            progressDialog.addWindowListener( 
                new WindowAdapter(  )
                {
                    public void windowClosing( WindowEvent e )
                    {
                        if( !grabberThread.isAlive(  ) )
                        {
                            if( progressDialog != null )
                            {
                                progressDialog.dispose(  );
                                progressDialog = null;
                            }
                        }
                    }
                } );
        }

        new Thread(  )
            {
                public void run(  )
                {
                    progressDialog.setVisible( true );
                }
            }.start(  );

        if( MainController.config.activeGrabberIDs.size(  ) == 0 )
        {
            wasError = true;
            progressDialog.showNoGrabberMessage(  );

        }
        else
        {
            Iterator it = MainController.config.activeGrabberIDs
                .iterator(  );

            while( it.hasNext(  ) )
            {
                String grabberID = (String)it.next(  );

                try
                {
                    IModuleGrabber grabber =
                        (IModuleGrabber)PluginsManager.getModuleByID( 
                            grabberID );

                    if( grabber == null )
                    {
                        FreeGuide.log.warning( 
                            "There is no grabber " + grabberID );

                        continue;

                    }

                    if( Thread.interrupted(  ) )
                    {
                        break;
                    }

                    final StoragePipe pipe = new StoragePipe(  );

                    wasError = !grabber.grabData( 
                            progressDialog, progressDialog, pipe );

                    pipe.finish(  );

                    if( Thread.interrupted(  ) )
                    {
                        break;
                    }
                }
                catch( ClosedByInterruptException ex )
                {
                    break;
                }
                catch( InterruptedException ex )
                {
                    break;
                }
                catch( Throwable ex )
                {
                    wasError = true;

                    if( progressDialog != null )
                    {
                        if( ex instanceof Exception )
                        {
                            progressDialog.error( 
                                "Error grab data by grabber '" + grabberID
                                + "'", (Exception)ex );
                        }
                        else
                        {
                            progressDialog.error( 
                                "Error grab data by grabber '" + grabberID
                                + "': " + ex.getClass(  ).getName(  ) );
                        }
                    }

                    FreeGuide.log.log( 
                        Level.WARNING,
                        "Error grab data by grabber '" + grabberID, ex );
                }
            }
        }

        synchronized( this )
        {
            if( progressDialog != null )
            {
                if( !wasError )
                {
                    if( !progressDialog.isLogVisible(  ) )
                    {
                        progressDialog.dispose(  );
                        progressDialog = null;
                    }
                    else
                    {
                        progressDialog.setDefaultCloseOperation( 
                            JDialog.DISPOSE_ON_CLOSE );
                        progressDialog.setCloseLabel(  );
                        progressDialog.setProgressMessage( 
                            null,
                            Application.getInstance(  )
                                       .getLocalizedMessage( 
                                "ExecutionDialog.Finish.OK" ) );
                        progressDialog.bringToForeground(  );
                    }
                }
                else
                {
                    progressDialog.setDefaultCloseOperation( 
                        JDialog.DISPOSE_ON_CLOSE );
                    progressDialog.setCloseLabel(  );
                    progressDialog.setProgressMessage( 
                        null,
                        Application.getInstance(  )
                                   .getLocalizedMessage( 
                            "ExecutionDialog.Finish.Error" ) );
                    progressDialog.bringToForeground(  );
                    progressDialog.showDetails(  );
                }
            }
        }

        foregroundButton.setVisible( false );
        secondProgressBar.setVisible( false );

        if( progressDialog != null )
        {
            progressDialog.disableBackgroundButton(  );
        }
    }
}
