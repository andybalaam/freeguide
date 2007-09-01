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
package freeguide.common.gui;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IProgress;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Provides facilities for executing external commands with a GUI for user
 * feedback.
 *
 * @author Andy Balaam
 * @version 9 (Used to be ExecutorFrame)
 */
public class ExecutorDialog extends JDialog implements IProgress, ILogger
{
    // ------------------------------------------------------------------------
    final private JProgressBar secondProgressBar;
    private javax.swing.JButton foregroundButton;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butDetails;
    private javax.swing.JButton butBackground;
    private javax.swing.JLabel labPleaseWait;
    private javax.swing.JProgressBar progressBar;
    private JTextArea log;
    private JScrollPane logScroll;
    protected int stepCount = 0;
    protected Dimension minPreferredSize;
    protected Dimension maxPreferredSize;
    protected int minHeight;
    protected int maxHeight;

/**
     * Creates a new ExecutorDialog object.
     *
     * @param owner DOCUMENT ME!
     * @param secondProgressBar DOCUMENT ME!
     */
    public ExecutorDialog( 
        JFrame owner, final JProgressBar secondProgressBar,
        final JButton foregroundButton )
    {
        super( owner, true ); //TODO FreeGuide.prefs.screen.getBoolean( "executor_modal", true ) );
        this.secondProgressBar = secondProgressBar;
        this.foregroundButton = foregroundButton;
        initComponents(  );

        Utils.centreDialog( owner, this );
    }

    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;
        getContentPane(  ).setLayout( new java.awt.GridBagLayout(  ) );
        setTitle( 
            Application.getInstance(  ).getLocalizedMessage( 
                "executing_command" ) );
        butCancel = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "cancel" ) );
        butCancel.setMaximumSize( new java.awt.Dimension( 115, 23 ) );
        butCancel.setMinimumSize( new java.awt.Dimension( 115, 23 ) );
        butCancel.setPreferredSize( new java.awt.Dimension( 115, 23 ) );
        butCancel.setMnemonic( KeyEvent.VK_C );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( butCancel, gridBagConstraints );
        butBackground = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( "background" ) );
        butBackground.setMaximumSize( new java.awt.Dimension( 115, 23 ) );
        butBackground.setMinimumSize( new java.awt.Dimension( 115, 23 ) );
        butBackground.setPreferredSize( new java.awt.Dimension( 115, 23 ) );
        butBackground.setMnemonic( KeyEvent.VK_B );
        butBackground.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butBackgroundActionPerformed( evt );
                }
            } );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( butBackground, gridBagConstraints );

        foregroundButton.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    butForegroundActionPerformed( evt );
                }
            } );

        butDetails = new javax.swing.JButton( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "show_output" ) );
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

        labPleaseWait = new javax.swing.JLabel( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "please_wait" ), javax.swing.SwingConstants.CENTER );
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( labPleaseWait, gridBagConstraints );
        progressBar = new javax.swing.JProgressBar( 0, 100 );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );
        getContentPane(  ).add( progressBar, gridBagConstraints );
        log = new JTextArea(  );

        // log.setRows( 10 );
        logScroll = new JScrollPane( log );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets( 2, 2, 2, 2 );
        logScroll.setVisible( false );
        log.setVisible( false );
        getContentPane(  ).add( logScroll, gridBagConstraints );

        pack(  );
        minHeight = getHeight(  );
        maxHeight = minHeight + 150;

        setDefaultCloseOperation( JDialog.HIDE_ON_CLOSE );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JButton getCancelButton(  )
    {
        return butCancel;
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butDetailsActionPerformed( java.awt.event.ActionEvent evt )
    {
        if( !logScroll.isVisible(  ) )
        {
            showDetails(  );
        }
        else
        {
            hideDetails(  );
        }
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butBackgroundActionPerformed( java.awt.event.ActionEvent evt )
    {
        butBackground.setEnabled( false );
        foregroundButton.setVisible( true );
        secondProgressBar.setVisible( true );
        setVisible( false );
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butForegroundActionPerformed( java.awt.event.ActionEvent evt )
    {
        butBackground.setEnabled( true );
        foregroundButton.setVisible( false );
        secondProgressBar.setVisible( false );
    }

    // -----------------------------------------------------------------------
    /**
     * Show details panel.
     */
    public void showDetails(  )
    {
        logScroll.setVisible( true );
        log.setVisible( true );
        butDetails.setText( 
            Application.getInstance(  ).getLocalizedMessage( "hide_output" ) );

        int width = getWidth(  );
        int height = maxHeight;

        setSize( width, height );
        validate(  );
    }

    /**
     * Hide details panel.
     */
    public void hideDetails(  )
    {
        logScroll.setVisible( false );
        log.setVisible( false );
        butDetails.setText( 
            Application.getInstance(  ).getLocalizedMessage( "show_output" ) );

        int width = getWidth(  );
        maxHeight = getHeight(  );

        int height = minHeight;

        setSize( width, height );
        validate(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param percent DOCUMENT_ME!
     */
    public void setProgressValue( final int percent )
    {
        SwingUtilities.invokeLater( 
            new Runnable(  )
            {
                public void run(  )
                {
                    progressBar.setValue( percent );
                    secondProgressBar.setValue( percent );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param stepCount DOCUMENT_ME!
     */
    public void setStepCount( final int stepCount )
    {
        this.stepCount = stepCount;
        setProgressValue( 0 );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param stepNumber DOCUMENT_ME!
     */
    public void setStepNumber( final int stepNumber )
    {
        if( stepCount < stepNumber )
        {
            stepCount = stepNumber;
        }

        if( stepCount > 0 )
        {
            setProgressValue( ( 100 * stepNumber ) / stepCount );
        }
        else
        {
            setProgressValue( 0 );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     */
    public void setProgressMessage( String message )
    {
        setProgressMessage( message, null );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     * @param label DOCUMENT ME!
     */
    public void setProgressMessage( final String message, final String label )
    {
        SwingUtilities.invokeLater( 
            new Runnable(  )
            {
                public void run(  )
                {
                    // Set the please wait message
                    Object[] messageArguments = { message };

                    if( label == null )
                    {
                        String localizedMessage =
                            Application.getInstance(  )
                                       .getLocalizedMessage( 
                                "comma_please_wait_template", messageArguments );

                        labPleaseWait.setText( localizedMessage );
                        secondProgressBar.setString( localizedMessage );
                        secondProgressBar.revalidate(  );
                    }
                    else
                    {
                        labPleaseWait.setText( label );
                        secondProgressBar.setString( message );
                    }

                    setTitle( message );

                    //labPleaseWait.setText(message);
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param label DOCUMENT_ME!
     */
    public void setButtonLabel( final String label )
    {
        SwingUtilities.invokeLater( 
            new Runnable(  )
            {
                public void run(  )
                {
                    butCancel.setText( label );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isLogVisible(  )
    {
        return log.isVisible(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void setCloseLabel(  )
    {
        setButtonLabel( 
            Application.getInstance(  ).getLocalizedMessage( "close" ) );
    }

    protected void addToLog( final String msg )
    {
        SwingUtilities.invokeLater( 
            new Runnable(  )
            {
                public void run(  )
                {
                    log.append( msg + '\n' );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     */
    public void error( String message )
    {
        addToLog( message );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     * @param ex DOCUMENT_ME!
     */
    public void error( String message, Exception ex )
    {
        addToLog( message + ':' + ex.getMessage(  ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     */
    public void info( String message )
    {
        addToLog( message );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param message DOCUMENT_ME!
     */
    public void warning( String message )
    {
        addToLog( message );
    }

    /**
     * There are no grabbers configured/existing. We show a message to
     * tell the user that
     */
    public void showNoGrabberMessage(  )
    {
        butBackground.setEnabled( false );
        error( Application.getInstance(  ).getLocalizedMessage( "nograbber" ) );
        setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( 
                "nograbber_title" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "nograbber_title" ) );
    }
}
