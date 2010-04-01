package freeguide.startup;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * Class for display message box on any version jre.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class MessageBox extends Dialog
{
    protected MessageBox( final String title )
    {
        super( new Frame(  ), title, true );
    }

    /**
     * Display message.
     *
     * @param title
     * @param text
     */
    public static void display( final String title, final String text )
    {
        final MessageBox box = new MessageBox( title );

        box.setLayout( new BorderLayout( 5, 5 ) );
        box.add( new Label( text ), BorderLayout.CENTER );

        final Button okBtn = new Button( "OK" );
        box.add( okBtn, BorderLayout.SOUTH );
        box.setResizable( false );
        box.pack(  );

        final Dimension screenSize =
            Toolkit.getDefaultToolkit(  ).getScreenSize(  );

        final Dimension dialogSize = box.getSize(  );

        box.setLocation(
            ( screenSize.width - dialogSize.width ) / 2,
            ( screenSize.height - dialogSize.height ) / 2 );

        okBtn.addActionListener(
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    box.dispose(  );
                }
            } );
        box.addWindowListener(
            new WindowListener(  )
            {
                public void windowClosing( WindowEvent e )
                {
                    box.dispose(  );
                }

                public void windowDeactivated( WindowEvent e )
                {
                }

                public void windowClosed( WindowEvent e )
                {
                }

                public void windowActivated( WindowEvent e )
                {
                }

                public void windowDeiconified( WindowEvent e )
                {
                }

                public void windowIconified( WindowEvent e )
                {
                }

                public void windowOpened( WindowEvent e )
                {
                }
            } );
        box.setVisible( true );
    }
}
