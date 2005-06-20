package freeguide.gui.jcommon;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class JWaitFrame extends JFrame
{

    /**
     * Creates a new JWaitFrame object.
     */
    public JWaitFrame(  )
    {
        setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        addWindowListener( 
            new WindowAdapter(  )
            {
                public void windowClosed( WindowEvent e )
                {

                    synchronized( JWaitFrame.this )
                    {
                        JWaitFrame.this.notifyAll(  );
                    }
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     */
    public void waitForClose(  )
    {

        try
        {

            synchronized( this )
            {
                wait(  );
            }
        }
        catch( InterruptedException ex )
        {
        }
    }
}
