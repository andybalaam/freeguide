package freeguide.plugins.program.freeguide.lib.fgspecific;

import freeguide.common.gui.ExecutorDialog;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.plugininterfaces.IExecutionController;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.general.ICommandRunner;
import freeguide.plugins.program.freeguide.viewer.MainController;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

/**
 * Class for run specified grabber and display progress dialog.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ExecutionController implements IExecutionController
{
    protected ExecutorDialog progressDialog;
    protected JProgressBar secondProgressBar;
    protected boolean wasError;
    protected Thread runnerThread;

    /**
     * Show grabber dialog when grabbing running, or start grabbing in
     * new thread.
     *
     * @param controller DOCUMENT ME!
     */
    public void activate( final IApplication controller, final ICommandRunner runner,
        final boolean dialogVisible )
    {
        synchronized( this )
        {
            if( progressDialog != null )
            {
                // Show dialog
                progressDialog.bringToForeground(  );
            }
            else
            {
                // Start new grabbing
                runnerThread =
                    new Thread(  )
                        {
                            public void run(  )
                            {
                                FreeGuide.log
                                .finest( "start running" );

                                try
                                {
                                    doRunCommand(
                                        controller.getApplicationFrame(  ),
                                        controller.getApplicationProgressBar(  ),
                                        controller.getApplicationForegroundButton(  ), runner,
                                        dialogVisible );
                                    controller.getViewer().onDataChanged(  );
                                    MainController.remindersReschedule(  );
                                }
                                catch( Exception ex )
                                {
                                    ex.printStackTrace(  );
                                }

                                FreeGuide.log.finest( "stop running" );
                            }
                        };
                runnerThread.start(  );
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
    public void doRunCommand(
        final JFrame owner, final JProgressBar secondProgressBar,
        final JButton foregroundButton, ICommandRunner runner,
        boolean dialogVisible )
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
                        synchronized( ExecutionController.this )
                        {
                            runnerThread.interrupt(  );

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
                        if( !runnerThread.isAlive(  ) )
                        {
                            if( progressDialog != null )
                            {
                                progressDialog.dispose(  );
                                progressDialog = null;
                            }
                        }
                    }
                } );

            if( !dialogVisible )
            {
                progressDialog.sendToBackground();
            }
        }

        if( dialogVisible )
        {
            new Thread(  )
                {
                    public void run(  )
                    {
                        progressDialog.setVisible( true );
                    }
                }.start(  );
        }

        if( MainController.config.getActiveGrabberIDs().size(  ) == 0 )
        {
            wasError = true;
            progressDialog.showNoGrabberMessage(  );

        }
        else
        {
            wasError = !runner.run( progressDialog, progressDialog );
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

        if( progressDialog != null )
        {
            progressDialog.disableBackgroundButton(  );
        }
    }
}
