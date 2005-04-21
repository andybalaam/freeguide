package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.ExecutorDialog;

import freeguide.gui.viewer.MainController;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorChannels;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.impexp.XMLTVExport;

import freeguide.plugins.IModuleGrabber;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;

import java.util.Iterator;

import javax.swing.JFrame;

/**
 * Class for run specified grabber and display progress dialog.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class GrabberController
{

    ExecutorDialog progressDialog;

    /**
     * DOCUMENT_ME!
     *
     * @param owner DOCUMENT_ME!
     */
    public void grab( JFrame owner )
    {
        progressDialog = new ExecutorDialog( owner );

        progressDialog.getActionButton(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    finish(  );

                }
            } );

        progressDialog.addWindowListener( 
            new WindowAdapter(  )
            {
                public void windowClosing( WindowEvent evt )
                {
                    finish(  );

                }
            } );

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
                System.out.println( "Run grabber " + grabberID );

                IModuleGrabber grabber =
                    PluginsManager.getGrabberByID( grabberID );

                if( grabber == null )
                {
                    System.err.println( "There is no grabber " + grabberID );

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

                System.err.println( 
                    "Error grab data by grabber '" + grabberID
                    + "'. This is only debug stack trace:" );

                ex.printStackTrace(  );

            }
        }

        progressDialog.dispose(  );

        MainController.reminderReschedule(  );

    }

    /**
     * DOCUMENT_ME!
     */

    /*  public void grabXMLTV(


      JFrame owner, String[] cmds, String commandType, Calendar date )


    {




      GrabberXMLTV module = new GrabberXMLTV(  );


      module.set( cmds, commandType, date );




      try


      {


          module.setLocale( Locale.ENGLISH );


      }


      catch( Exception ex )


      {


          ex.printStackTrace(  );


      }




      progressDialog = new ExecutorDialog( owner );


      progressDialog.setProgressMessage( commandType );


      progressDialog.getActionButton(  ).addActionListener(


          new ActionListener(  )


          {


              public void actionPerformed( ActionEvent evt )


              {


                  finish(  );


              }


          } );


      progressDialog.addWindowListener(


          new WindowAdapter(  )


          {


              public void windowClosing( WindowEvent evt )


              {


                  finish(  );


              }


          } );




      new Thread(  )


          {


              public void run(  )


              {


                  progressDialog.setVisible( true );


              }


          }.start(  );




      grabModule = (IModuleGrabber)module;


      start(  );


    }*/

    /**
     * DOCUMENT_ME!
     */

    /*    public void run(  )


    {




        try


        {


            System.out.println( "before grab" );


          //  grabModule.grabData( progressDialog, null );


            System.out.println( "after grab" );


           // grabModule = null;


        }


        catch( Exception ex )


        {


            ex.printStackTrace(  );


        }




        finish(  );


    }*/

    /**
     * DOCUMENT_ME!
     */
    public void finish(  )
    {
        progressDialog.setVisible( false );

        //viewer.dispose();
        progressDialog.dispose(  );

    }
}
