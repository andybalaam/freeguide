/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.wizard;

import freeguide.FreeGuide;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.net.URL;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A class to produce a wizard interface.
 *
 * @author Andy Balaam
 * @version 1
 */
public class WizardFrame extends javax.swing.JFrame
{

    private WizardPanel[] panels;
    private javax.swing.JPanel panButtons;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butNext;
    private javax.swing.JButton butBack;
    private javax.swing.JButton butFinish;
    private int panelCounter;
    private Runnable finishMethod;
    private Runnable exitMethod;

    /**
     * Construct this wizard
     *
     * @param title the String title of the wizard
     * @param panels an array of WizardPanels to be displayed
     * @param finishMethod a method to call when the wizard finishes
     *        successfully
     * @param exitMethod a method to call when the user prematurely exited
     */
    public WizardFrame( 
        String title, WizardPanel[] panels, Runnable finishMethod,
        Runnable exitMethod )
    {

        URL imgURL = getClass(  ).getResource( "/images/logo-16x16.png" );
        Image icon =
            ( new javax.swing.ImageIcon( imgURL, "icon" ) ).getImage(  );
        setIconImage( icon );

        this.panels = panels;

        this.finishMethod = finishMethod;

        this.exitMethod = exitMethod;

        panelCounter = 0;

        initComponents( title );

    }

    /**
     * Description of the Method
     *
     * @param title Description of the Parameter
     */
    private void initComponents( String title )
    {

        GridBagConstraints gridBagConstraints;

        // Set up the panels ready to be used
        for( int i = 0; i < panels.length; i++ )
        {
            panels[i].construct(  );

        }

        panButtons = new JPanel(  );

        butCancel = new JButton(  );

        butBack = new JButton(  );

        butNext = new JButton(  );

        butFinish = new JButton(  );

        getContentPane(  ).setLayout( new GridBagLayout(  ) );

        setTitle( title );

        addWindowListener( 
            new WindowAdapter(  )
            {
                public void windowClosing( WindowEvent evt )
                {
                    exitForm( evt );

                }
            } );

        butCancel.setText( FreeGuide.msg.getString( "exit" ) );

        butCancel.setMnemonic( KeyEvent.VK_X );

        butCancel.setMaximumSize( new java.awt.Dimension( 85, 26 ) );

        butCancel.setMinimumSize( new java.awt.Dimension( 85, 26 ) );

        butCancel.setPreferredSize( new java.awt.Dimension( 85, 26 ) );

        butCancel.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butCancelActionPerformed( evt );

                }
            } );

        panButtons.add( butCancel );

        butBack.setText( "<< " + FreeGuide.msg.getString( "back" ) );

        butBack.setMnemonic( KeyEvent.VK_B );

        butBack.setEnabled( false );

        butBack.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butBackActionPerformed( evt );

                }
            } );

        panButtons.add( butBack );

        butNext.setText( FreeGuide.msg.getString( "next" ) + " >>" );

        butNext.setMnemonic( KeyEvent.VK_N );

        butNext.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butNextActionPerformed( evt );

                }
            } );

        panButtons.add( butNext );

        butFinish.setText( FreeGuide.msg.getString( "finish" ) );

        butFinish.setMnemonic( KeyEvent.VK_F );

        butFinish.setMaximumSize( new java.awt.Dimension( 85, 26 ) );

        butFinish.setMinimumSize( new java.awt.Dimension( 85, 26 ) );

        butFinish.setPreferredSize( new java.awt.Dimension( 85, 26 ) );

        butFinish.setEnabled( false );

        butFinish.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butFinishActionPerformed( evt );

                }
            } );

        panButtons.add( butFinish );

        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 1;

        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;

        getContentPane(  ).add( panButtons, gridBagConstraints );

        displayPanel( panels[panelCounter] );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butCancelActionPerformed( java.awt.event.ActionEvent evt )
    {
        quit(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butFinishActionPerformed( java.awt.event.ActionEvent evt )
    {
        butFinish.setEnabled( false );

        panels[panelCounter].onExit(  );

        if( finishMethod != null )
        {
            finishMethod.run(  );

        }

        setVisible( false );

        dispose(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void exitForm( java.awt.event.WindowEvent evt )
    {
        quit(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butBackActionPerformed( java.awt.event.ActionEvent evt )
    {

        // Save the info on this panel and check we're allowed to leave it
        if( ( panelCounter > 0 ) && panels[panelCounter].onExit(  ) )
        {

            // Go to the previous panel
            panelCounter--;

            displayPanel( panels[panelCounter] );

        }
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butNextActionPerformed( java.awt.event.ActionEvent evt )
    {

        // Save the info on this panel and check we're allowed to leave it
        if( 
            ( panelCounter < panels.length )
                && panels[panelCounter].onExit(  ) )
        {

            // Go to the next panel
            panelCounter++;

            displayPanel( panels[panelCounter] );

        }
    }

    /**
     * construct Description of the Method
     *
     * @param newPanel Description of the Parameter
     */
    private void displayPanel( WizardPanel newPanel )
    {

        java.awt.GridBagConstraints gridBagConstraints;

        java.awt.Container contentPane = getContentPane(  );

        // Perform any operations required when entering it
        newPanel.onEnter(  );

        // Remove the old panel
        if( contentPane.getComponentCount(  ) > 1 )
        {
            contentPane.remove( 1 );

        }

        // Set up gridBagConstraints
        gridBagConstraints = new java.awt.GridBagConstraints(  );

        gridBagConstraints.gridx = 0;

        gridBagConstraints.gridy = 0;

        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;

        gridBagConstraints.weightx = 0.9;

        gridBagConstraints.weighty = 0.9;

        // Display the new panel
        getContentPane(  ).add( newPanel, gridBagConstraints );

        refreshButtons(  );

        Dimension screenSize = Toolkit.getDefaultToolkit(  ).getScreenSize(  );

        setSize( new java.awt.Dimension( 500, 350 ) );

        setLocation( 
            ( screenSize.width - 500 ) / 2, ( screenSize.height - 350 ) / 2 );

        newPanel.revalidate(  );

        newPanel.repaint(  );

    }

    /**
     * Enable the buttons according to where we are in the wizard: beginning,
     * middle, or end.
     */
    private void refreshButtons(  )
    {

        if( panelCounter == 0 )
        { // Beginning
            butBack.setEnabled( false );

            butNext.setEnabled( true );

            butFinish.setEnabled( false );

            getRootPane(  ).setDefaultButton( butNext );

            butNext.requestFocus(  );

        }

        else if( panelCounter == ( panels.length - 1 ) )
        { // End
            butBack.setEnabled( true );

            butNext.setEnabled( false );

            butFinish.setEnabled( true );

            getRootPane(  ).setDefaultButton( butFinish );

            butBack.requestFocus(  );

        }

        else
        { // Middle
            butBack.setEnabled( true );

            butNext.setEnabled( true );

            butFinish.setEnabled( false );

            getRootPane(  ).setDefaultButton( butNext );

            butBack.requestFocus(  );

        }
    }

    /**
     * Close this wizard and invoke the method given to be invoked when the
     * user prematurely stops the wizard.
     */
    private void quit(  )
    {
        setVisible( false );

        dispose(  );

        if( exitMethod != null )
        {
            exitMethod.run(  );

        }
    }
}
