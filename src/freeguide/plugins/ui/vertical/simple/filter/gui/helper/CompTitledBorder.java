package freeguide.plugins.ui.vertical.simple.filter.gui.helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Found at
 * http://www.objects.com.au/java/examples/swing/CheckBoxBorder.do
 *
 * @version 1.0 08/12/99
 */
public class CompTitledBorder extends TitledBorder
{
    private static final long serialVersionUID = 1L;
    protected JComponent component;

    /**
     * Creates a new CompTitledBorder object.
     *
     * @param component DOCUMENT ME!
     */
    public CompTitledBorder( JComponent component )
    {
        this( null, component, LEFT, TOP );
    }

    /**
     * Creates a new CompTitledBorder object.
     *
     * @param border DOCUMENT ME!
     */
    public CompTitledBorder( Border border )
    {
        this( border, null, LEFT, TOP );
    }

    /**
     * Creates a new CompTitledBorder object.
     *
     * @param border DOCUMENT ME!
     * @param component DOCUMENT ME!
     */
    public CompTitledBorder( Border border, JComponent component )
    {
        this( border, component, LEFT, TOP );
    }

    /**
     * Creates a new CompTitledBorder object.
     *
     * @param border DOCUMENT ME!
     * @param component DOCUMENT ME!
     * @param titleJustification DOCUMENT ME!
     * @param titlePosition DOCUMENT ME!
     */
    public CompTitledBorder(
        Border border, JComponent component, int titleJustification,
        int titlePosition )
    {
        super( border, null, titleJustification, titlePosition, null, null );
        this.component = component;

        if( border == null )
        {
            this.border = super.getBorder(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param c DOCUMENT_ME!
     * @param g DOCUMENT_ME!
     * @param x DOCUMENT_ME!
     * @param y DOCUMENT_ME!
     * @param width DOCUMENT_ME!
     * @param height DOCUMENT_ME!
     */
    public void paintBorder(
        Component c, Graphics g, int x, int y, int width, int height )
    {
        Rectangle borderR =
            new Rectangle(
                x + EDGE_SPACING, y + EDGE_SPACING,
                width - ( EDGE_SPACING * 2 ), height - ( EDGE_SPACING * 2 ) );
        Insets borderInsets;

        if( border != null )
        {
            borderInsets = border.getBorderInsets( c );
        }
        else
        {
            borderInsets = new Insets( 0, 0, 0, 0 );
        }

        Rectangle rect = new Rectangle( x, y, width, height );
        Insets insets = getBorderInsets( c );
        Rectangle compR = getComponentRect( rect, insets );
        int diff;

        switch( titlePosition )
        {
        case ABOVE_TOP:
            diff = compR.height + TEXT_SPACING;
            borderR.y += diff;
            borderR.height -= diff;

            break;

        case TOP:
        case DEFAULT_POSITION:
            diff = ( insets.top / 2 ) - borderInsets.top - EDGE_SPACING;
            borderR.y += diff;
            borderR.height -= diff;

            break;

        case BELOW_TOP:
        case ABOVE_BOTTOM:
            break;

        case BOTTOM:
            diff = ( insets.bottom / 2 ) - borderInsets.bottom - EDGE_SPACING;
            borderR.height -= diff;

            break;

        case BELOW_BOTTOM:
            diff = compR.height + TEXT_SPACING;
            borderR.height -= diff;

            break;
        }

        border.paintBorder(
            c, g, borderR.x, borderR.y, borderR.width, borderR.height );

        Color col = g.getColor(  );
        g.setColor( c.getBackground(  ) );
        g.fillRect( compR.x, compR.y, compR.width, compR.height );
        g.setColor( col );
        component.repaint(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param c DOCUMENT_ME!
     * @param insets DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Insets getBorderInsets( Component c, Insets insets )
    {
        Insets borderInsets;

        if( border != null )
        {
            borderInsets = border.getBorderInsets( c );
        }
        else
        {
            borderInsets = new Insets( 0, 0, 0, 0 );
        }

        insets.top = EDGE_SPACING + TEXT_SPACING + borderInsets.top;
        insets.right = EDGE_SPACING + TEXT_SPACING + borderInsets.right;
        insets.bottom = EDGE_SPACING + TEXT_SPACING + borderInsets.bottom;
        insets.left = EDGE_SPACING + TEXT_SPACING + borderInsets.left;

        if( ( c == null ) || ( component == null ) )
        {
            return insets;
        }

        int compHeight = 0;

        if( component != null )
        {
            compHeight = component.getPreferredSize(  ).height;
        }

        switch( titlePosition )
        {
        case ABOVE_TOP:
            insets.top += ( compHeight + TEXT_SPACING );

            break;

        case TOP:
        case DEFAULT_POSITION:
            insets.top += ( Math.max( compHeight, borderInsets.top )
            - borderInsets.top );

            break;

        case BELOW_TOP:
            insets.top += ( compHeight + TEXT_SPACING );

            break;

        case ABOVE_BOTTOM:
            insets.bottom += ( compHeight + TEXT_SPACING );

            break;

        case BOTTOM:
            insets.bottom += ( Math.max( compHeight, borderInsets.bottom )
            - borderInsets.bottom );

            break;

        case BELOW_BOTTOM:
            insets.bottom += ( compHeight + TEXT_SPACING );

            break;
        }

        return insets;
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
     * @param component DOCUMENT_ME!
     */
    public void setTitleComponent( JComponent component )
    {
        this.component = component;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param rect DOCUMENT_ME!
     * @param borderInsets DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Rectangle getComponentRect( Rectangle rect, Insets borderInsets )
    {
        Dimension compD = component.getPreferredSize(  );
        Rectangle compR = new Rectangle( 0, 0, compD.width, compD.height );

        switch( titlePosition )
        {
        case ABOVE_TOP:
            compR.y = EDGE_SPACING;

            break;

        case TOP:
        case DEFAULT_POSITION:
            compR.y = EDGE_SPACING
                + ( ( borderInsets.top - EDGE_SPACING - TEXT_SPACING
                - compD.height ) / 2 );

            break;

        case BELOW_TOP:
            compR.y = borderInsets.top - compD.height - TEXT_SPACING;

            break;

        case ABOVE_BOTTOM:
            compR.y = rect.height - borderInsets.bottom + TEXT_SPACING;

            break;

        case BOTTOM:
            compR.y = rect.height - borderInsets.bottom + TEXT_SPACING
                + ( ( borderInsets.bottom - EDGE_SPACING - TEXT_SPACING
                - compD.height ) / 2 );

            break;

        case BELOW_BOTTOM:
            compR.y = rect.height - compD.height - EDGE_SPACING;

            break;
        }

        switch( titleJustification )
        {
        case LEFT:
        case DEFAULT_JUSTIFICATION:
            compR.x = TEXT_INSET_H + borderInsets.left;

            break;

        case RIGHT:
            compR.x = rect.width - borderInsets.right - TEXT_INSET_H
                - compR.width;

            break;

        case CENTER:
            compR.x = ( rect.width - compR.width ) / 2;

            break;
        }

        return compR;
    }
}
