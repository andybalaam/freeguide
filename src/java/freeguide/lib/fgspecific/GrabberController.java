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

    /**
     * DOCUMENT_ME!
     *
     * @param owner DOCUMENT_ME!
     * @param secondProgressBar DOCUMENT ME!
     */
    public void grab( JFrame owner, JProgressBar secondProgressBar )
    {
        this.secondProgressBar = secondProgressBar;

        try
        {

            synchronized( this )
            {
                progressDialog =
                    new ExecutorDialog( owner, secondProgressBar );

                progressDialog.getCancelButton(  ).addActionListener( 
                    new ActionListener(  )
                    {
                        public void actionPerformed( ActionEvent evt )
                        {
                            finish(  );

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
                        (IModuleGrabber)PluginsManager.getModuleByID( 
                            grabberID );

                    if( grabber == null )
                    {
                        FreeGuide.log.warning( 
                            "There is no grabber " + grabberID );

                        continue;

                    }

                    TVData result =
                        grabber.grabData( progressDialog, progressDialog );

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
                    progressDialog.error( 
                        "Error grab data by grabber '" + grabberID + "'", ex );

                    FreeGuide.log.log( 
                        Level.WARNING,
                        "Error grab data by grabber '" + grabberID, ex );
                }
            }
        }
        finally
        {
            finish(  );
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void finish(  )
    {

        synchronized( this )
        {
            secondProgressBar.setVisible( false );
            progressDialog.dispose(  );
            progressDialog = null;
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void showDialog(  )
    {

        synchronized( this )
        {
            progressDialog.setVisible( true );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isStarted(  )
    {

        synchronized( this )
        {

            return progressDialog != null;
        }
    }
}
