package freeguide.plugins.ui.vertical.simple.filter.gui.helper;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * DOCUMENT ME!
 *
 * @version 1.0 08/12/99
 */
public class CompTitledPane extends JPanel
{
    private static final long serialVersionUID = 1L;
    protected CompTitledBorder border;
    protected JComponent component;
    protected JPanel panel;
    protected boolean transmittingAllowed;
    protected StateTransmitter transmitter;

    /**
     * Creates a new CompTitledPane object.
     */
    public CompTitledPane(  )
    {
        this( new JLabel( "Title" ) );

        // debug
        // JLabel label = (JLabel)getTitleComponent();
        // label.setOpaque(true);
        // label.setBackground(Color.yellow);
    }

    /**
     * Creates a new CompTitledPane object.
     *
     * @param component DOCUMENT ME!
     */
    public CompTitledPane( JComponent component )
    {
        this.component = component;
        border = new CompTitledBorder( component );
        setBorder( border );
        panel = new JPanel( new BorderLayout(  ) );
        setLayout( null );
        add( component, false );
        add( panel, false );
        transmittingAllowed = false;
        transmitter = null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JComponent getTitleComponent(  )
    {
        return component;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param newComponent DOCUMENT_ME!
     */
    public void setTitleComponent( JComponent newComponent )
    {
        remove( component );
        add( newComponent );
        border.setTitleComponent( newComponent );
        component = newComponent;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param c DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component add( Component c )
    {
        return this.add( c, true );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param c DOCUMENT_ME!
     * @param bToContentPane DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component add( Component c, boolean bToContentPane )
    {
        if( bToContentPane )
        {
            this.getContentPane(  ).add( c );
        }
        else
        {
            super.add( c );
        }

        return c;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param c DOCUMENT_ME!
     */
    public void remove( Component c )
    {
        getContentPane(  ).remove( c );
        getContentPane(  ).updateUI(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JPanel getContentPane(  )
    {
        return panel;
    }

    /**
     * DOCUMENT_ME!
     */
    public void doLayout(  )
    {
        Insets insets = getInsets(  );
        Rectangle rect = getBounds(  );
        rect.x = 0;
        rect.y = 0;

        Rectangle compR = border.getComponentRect( rect, insets );
        component.setBounds( compR );
        rect.x += insets.left;
        rect.y += insets.top;
        rect.width -= ( insets.left + insets.right );
        rect.height -= ( insets.top + insets.bottom );
        panel.setBounds( rect );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param enable DOCUMENT_ME!
     */
    public void setTransmittingAllowed( boolean enable )
    {
        transmittingAllowed = enable;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean getTransmittingAllowed(  )
    {
        return transmittingAllowed;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param transmitter DOCUMENT_ME!
     */
    public void setTransmitter( StateTransmitter transmitter )
    {
        this.transmitter = transmitter;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public StateTransmitter getTransmitter(  )
    {
        return transmitter;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param enable DOCUMENT_ME!
     */
    public void setEnabled( boolean enable )
    {
        super.setEnabled( enable );

        if( transmittingAllowed && ( transmitter != null ) )
        {
            transmitter.setChildrenEnabled( enable );
        }
    }
}
