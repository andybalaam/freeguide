/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.dialogs;

import freeguide.*;

import freeguide.gui.*;

import freeguide.gui.viewer.*;

import freeguide.lib.fgspecific.*;

import freeguide.lib.general.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.text.*;

import java.util.*;

import javax.swing.*;

/**
 * Provides facilities for executing external commands with a GUI for user
 * feedback.
 *
 * @author Andy Balaam
 * @version 9 (Used to be ExecutorFrame)
 */
public class ExecutorDialog extends JDialog implements Runnable, Progressor
{

    private final static String lb = System.getProperty( "line.separator" );

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_ALWAYS = 0;

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_NEVER = 1;

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_ASK = 2;
    private Process pr;
    private StreamReaderThread readOutput;
    private StreamReaderThread readError;
    private Thread runner;
    private String commandType;
    private String[] cmds;
    private StringViewer viewer;
    private Calendar date;
    private PreferencesGroup prefs;

    //------------------------------------------------------------------------
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butDetails;
    private javax.swing.JLabel labPleaseWait;
    private javax.swing.JProgressBar progressBar;

    /**
     * Constructs the UI elements.
     *
     * @param owner DOCUMENT ME!
     * @param cmds Description of the Parameter
     * @param commandType Description of the Parameter
     * @param date Description of the Parameter
     */
    public ExecutorDialog( 
        JFrame owner, String[] cmds, String commandType, Calendar date )
    {
        super( 
            owner, FreeGuide.prefs.screen.getBoolean( "executor_modal", true ) );

        this.prefs = FreeGuide.prefs;
        this.date = date;
        this.cmds = cmds;

        SimpleDateFormat showdate = new SimpleDateFormat( "yyyyMMdd" );

        initComponents(  );

        // Set the please wait message
        Object[] messageArguments = { commandType };
        labPleaseWait.setText( 
            FreeGuide.getCompoundMessage( 
                "comma_please_wait_template", messageArguments ) );
        setTitle( commandType );

        viewer =
            new StringViewer( 
                this, FreeGuide.msg.getString( "commands" ) + ":" + lb,
                FreeGuide.msg.getString( "output" ) + ":" + lb );

        start(  );
    }

    /**
     * Constructor for the Executor object
     *
     * @param owner DOCUMENT ME!
     * @param cmds Description of the Parameter
     * @param commandType Description of the Parameter
     */
    public ExecutorDialog( JFrame owner, String[] cmds, String commandType )
    {
        this( owner, cmds, commandType, FreeGuide.prefs );

    }

    /**
     * Constructor for the Executor object
     *
     * @param owner DOCUMENT ME!
     * @param cmds Description of the Parameter
     * @param commandType Description of the Parameter
     * @param prefs DOCUMENT ME!
     */
    public ExecutorDialog( 
        JFrame owner, String[] cmds, String commandType, PreferencesGroup prefs )
    {
        super( owner, prefs.screen.getBoolean( "executor_modal", true ) );

        this.prefs = prefs;
        this.cmds = cmds;

        initComponents(  );

        // Set the please wait message
        Object[] messageArguments = { commandType };
        labPleaseWait.setText( 
            FreeGuide.getCompoundMessage( 
                "comma_please_wait_template", messageArguments ) );
        setTitle( commandType );

        viewer =
            new StringViewer( 
                this, FreeGuide.msg.getString( "commands" ) + ":" + lb,
                FreeGuide.msg.getString( "output" ) + ":" + lb );

        start(  );

    }

    private void initComponents(  )
    {

        java.awt.GridBagConstraints gridBagConstraints;

        getContentPane(  ).setLayout( new java.awt.GridBagLayout(  ) );

        setTitle( FreeGuide.msg.getString( "executing_command" ) );
        addWindowListener( 
            new java.awt.event.WindowAdapter(  )
            {
                public void windowClosing( java.awt.event.WindowEvent evt )
                {
                    exitForm( evt );
                }
            } );

        butCancel =
            new javax.swing.JButton( FreeGuide.msg.getString( "cancel" ) );
        butCancel.setMaximumSize( new java.awt.Dimension( 115, 23 ) );
        butCancel.setMinimumSize( new java.awt.Dimension( 115, 23 ) );
        butCancel.setPreferredSize( new java.awt.Dimension( 115, 23 ) );
        butCancel.setMnemonic( KeyEvent.VK_C );
        butCancel.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butCancelActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( butCancel, gridBagConstraints );

        butDetails =
            new javax.swing.JButton( FreeGuide.msg.getString( "show_output" ) );
        butDetails.setFont( new java.awt.Font( "Dialog", 0, 12 ) );
        butDetails.setMaximumSize( new java.awt.Dimension( 115, 23 ) );
        butDetails.setMinimumSize( new java.awt.Dimension( 115, 23 ) );
        butDetails.setPreferredSize( new java.awt.Dimension( 115, 23 ) );
        butDetails.setMnemonic( KeyEvent.VK_S );
        butDetails.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butDetailsActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( butDetails, gridBagConstraints );

        labPleaseWait =
            new javax.swing.JLabel( 
                FreeGuide.msg.getString( "please_wait" ),
                javax.swing.SwingConstants.CENTER );
        labPleaseWait.setBorder( 
            javax.swing.BorderFactory.createBevelBorder( 
                javax.swing.border.BevelBorder.LOWERED ) );
        labPleaseWait.setMaximumSize( new java.awt.Dimension( 400, 22 ) );
        labPleaseWait.setMinimumSize( new java.awt.Dimension( 400, 22 ) );
        labPleaseWait.setPreferredSize( new java.awt.Dimension( 400, 22 ) );
        labPleaseWait.setHorizontalTextPosition( 
            javax.swing.SwingConstants.CENTER );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labPleaseWait, gridBagConstraints );

        progressBar = new javax.swing.JProgressBar( 0, 100 );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );
        getContentPane(  ).add( progressBar, gridBagConstraints );

        pack(  );

        // Centre the screen
        java.awt.Dimension screenSize =
            java.awt.Toolkit.getDefaultToolkit(  ).getScreenSize(  );

        setLocation( 
            ( screenSize.width - getWidth(  ) ) / 2,
            ( screenSize.height - getHeight(  ) ) / 2 );

        // To Be Added Shortly (Rob)
        //        GuiUtils.centerDialog( this );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butDetailsActionPerformed( java.awt.event.ActionEvent evt )
    {
        showDetails(  );

    }

    // -----------------------------------------------------------------------
    private void showDetails(  )
    {
        viewer.setVisible( true );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butCancelActionPerformed( java.awt.event.ActionEvent evt )
    {

        //GEN-FIRST:event_butCancelActionPerformed
        stop(  );
        clearUp(  );

    }

    //------------------------------------------------------------------------

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void exitForm( java.awt.event.WindowEvent evt )
    {
        stop(  );
        clearUp(  );

    }

    /**
     * Description of the Method
     */
    private void clearUp(  )
    {

        if( pr != null )
        {
            pr.destroy(  );
        }

        if( readOutput != null )
        {
            readOutput.stop(  );
        }

        if( readError != null )
        {
            readError.stop(  );
        }

        setVisible( false );
        viewer.dispose(  );
        dispose(  );

    }

    /**
     * Description of the Method
     */
    public void start(  )
    {
        runner = new Thread( this );
        runner.start(  );
    }

    /**
     * Description of the Method
     */
    public void stop(  )
    {
        runner = null;

    }

    /**
     * run Execute several external applications via the command line
     * interface. (called from the thread launched in the constructor)
     */
    public void run(  )
    {

        Thread thisThread = Thread.currentThread(  );
        readOutput = new StreamReaderThread(  );
        readError = new StreamReaderThread(  );

        // Step through each command
        for( int i = 0; i < cmds.length; i++ )
        {

            // Exit if we've been stopped externally
            if( runner != thisThread )
            {

                break;
            }

            // Run the command and exit if there's an error
            if( !exec( cmds[i] ) )
            {
                dumpOutputAndError(  );

                return;
            }
        }

        clearUp(  );

    }

    /**
     * exec Execute an external application via the command line interface.
     *
     * @param cmdstr the command to execute
     *
     * @return true if the command finished successfully
     */
    public boolean exec( String cmdstr )
    {

        if( ( cmdstr == null ) || cmdstr.equals( "" ) )
        {

            // No command to execute: say it was successful
            return true;
        }

        Calendar thisDate = GregorianCalendar.getInstance(  );

        if( !prefs.misc.getBoolean( "grabber_start_today", true ) )
        {
            thisDate.setTime( date.getTime(  ) );

        }

        // Check for any elements that mean this command must be called multiple
        // times, once for each day.
        if( 
            ( cmdstr.indexOf( "%date%" ) != -1 )
                || ( cmdstr.indexOf( "%offset%" ) != -1 ) )
        {

            ViewerFrameXMLTVLoader loader = new ViewerFrameXMLTVLoader(  );

            int alwaysDownload =
                FreeGuide.prefs.misc.getInt( "re_download", REDOWNLOAD_ASK );

            boolean didOK = true;

            //Calendar date = GregorianCalendar.getInstance();
            int days_to_grab = prefs.misc.getInt( "days_to_grab", 7 );

            for( int i = 0; i < days_to_grab; i++ )
            {
                setProgress( 5 + ( ( i * 95 ) / days_to_grab ) );

                String subbedCmd =
                    prefs.performSubstitutions( cmdstr, thisDate, true );

                if( alwaysDownload == REDOWNLOAD_ALWAYS )
                {

                    // Recursive call to this function
                    if( !exec( subbedCmd ) )
                    {
                        didOK = false;
                    }
                }
                else
                {

                    // Parse the data we have for this date
                    loader.loadProgrammeData( thisDate );

                    // Only load a new lot if we haven't got any for this date
                    if( loader.hasData(  ) )
                    {

                        // If we not have chsen already, ask the user whether
                        // to re-download
                        if( alwaysDownload == REDOWNLOAD_ASK )
                        {

                            Object[] options =
                            {
                                FreeGuide.msg.getString( "redownload" ),
                                FreeGuide.msg.getString( "skip" )
                            };
                            int ans =
                                JOptionPane.showOptionDialog( 
                                    this,
                                    FreeGuide.msg.getString( 
                                        "some_listings_already_downloaded" ),
                                    FreeGuide.msg.getString( "redownload_q" ),
                                    JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, options,
                                    options[0] );

                            if( ans == 0 )
                            {
                                alwaysDownload = REDOWNLOAD_ALWAYS;

                                // Recursive call to this function
                                if( !exec( subbedCmd ) )
                                {
                                    didOK = false;
                                }
                            }
                            else
                            {
                                alwaysDownload = REDOWNLOAD_NEVER;

                            }

                            //if( ans == 0 )
                        }

                        //if( alwaysDownload == REDOWNLOAD_ASK ) {
                    }
                    else
                    {

                        // Recursive call to this function
                        if( !exec( subbedCmd ) )
                        {
                            didOK = false;
                        }
                    }

                    //if( !loader.hasData() ) {
                }

                //if( alwaysDownload == REDOWNLOAD_ALWAYS ) {
                thisDate.add( Calendar.DATE, 1 );

            }

            //for
            return didOK;
        }

        // this is only necessary if the above condition doesn't match.
        // It won't hurt to call it an extra time at this point if the
        // above condition does match.  The date is needed though.
        cmdstr = prefs.performSubstitutions( cmdstr, thisDate );

        // Log what we're about to do
        String message =
            FreeGuide.msg.getString( "executing_system_command" ) + ": "
            + cmdstr + " ...";

        if( FreeGuide.log != null )
        {
            FreeGuide.log.info( message );
        }
        else
        {
            System.err.println( message );
        }

        try
        {

            // Execute the command (after parsing it into tokens)
            pr = Runtime.getRuntime(  ).exec( Utils.parseCommand( cmdstr ) );

            // Get the input and output streams of this process
            BufferedReader prOut =
                new BufferedReader( 
                    new InputStreamReader( pr.getInputStream(  ) ) );
            BufferedReader prErr =
                new BufferedReader( 
                    new InputStreamReader( pr.getErrorStream(  ) ) );

            // Suck the output from the command
            readOutput.begin( prOut, cmdstr, viewer, viewer.getOutput(  ) );
            readError.begin( prErr, viewer, viewer.getError(  ) );

            // Actually wait for it to finish
            int exitCode = pr.waitFor(  );
            boolean retVal = ( exitCode == 0 );

            // Log it finishing
            Object[] messageArguments = { String.valueOf( exitCode ) };
            message =
                FreeGuide.getCompoundMessage( 
                    "finished_execution_template", messageArguments );

            if( FreeGuide.log != null )
            {
                FreeGuide.log.info( message );
            }
            else
            {
                System.err.println( message );
            }

            return retVal;
        }
        catch( java.io.IOException e )
        {
            e.printStackTrace(  );

            return false;
        }
        catch( java.lang.InterruptedException e )
        {
            e.printStackTrace(  );

            return false;
        }
    }

    //execExternal
    // ----------------------------------------------------------------------

    /**
     * Description of the Method
     */
    private void dumpOutputAndError(  )
    {

        try
        {
            Thread.sleep( 1000 );
        }
        catch( java.lang.InterruptedException e )
        {
            e.printStackTrace(  );
        }

        labPleaseWait.setText( FreeGuide.msg.getString( "execution_error" ) );
        butCancel.setText( FreeGuide.msg.getString( "continue" ) );

        String output_message =
            FreeGuide.msg.getString( "command_output_stdout" ) + ":" + lb
            + readOutput.getStoredOutput(  );
        String error_message =
            FreeGuide.msg.getString( "command_output_stderr" ) + ":" + lb
            + readError.getStoredOutput(  );

        if( FreeGuide.log != null )
        {
            FreeGuide.log.warning( output_message );
            FreeGuide.log.warning( error_message );
        }
        else
        {
            System.err.println( output_message );
            System.err.println( error_message );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param percent DOCUMENT_ME!
     */
    public void setProgress( int percent )
    {
        progressBar.setValue( percent );

    }
}
