package freeguide.lib.general;

import freeguide.*;

import freeguide.gui.viewer.*;

import freeguide.lib.fgspecific.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import java.util.*;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.CellRendererPane;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.event.MouseInputListener;

/**
 * The StripView component displays a number of horizontal rows containing
 * sets of non-overlapping rectangles. The position of the rectangles is
 * determined by the data given by {@link StripView.Model}and the appearance
 * of the rectangles is determined by a {@link StripRenderer}.  StripView
 * maps the screen coordinates to the coordinate space of the
 * StripView.Model.  StripView supports focusing and selecting particular
 * strips by using keyboard or mouse.
 *
 * @author Risto Kankkunen risto.kankkunen at iki.fi
 */
public class StripView extends JPanel implements Scrollable
{

    private static final long NONE = -1000;
    protected Model model;
    protected StripRenderer renderer;
    protected StripRenderer editor;
    private int horGap;
    private int verGap;
    private int channelHeight;
    private double widthMultiplier;
    private long min;
    private long max;

    /**
     * The current horizontal position of the "cursor".  To the user it looks
     * like the cursor is just selecting a programme, but in actual fact we
     * keep this value to make the cursor movement feel right.  When the user
     * moves left and right this value is set to the central point of the
     * selected programme.  When the user moves up and down, this value
     * remains the same and helps us decide which programme to select. A
     * value of NONE means no value, so the next time we move up or down we
     * take our cue from the midpoint of the current programme.
     */
    private long x_pos = NONE;
    protected CellRendererPane rendererPane;
    protected Strip currentStrip;
    protected int currentRow;
    private Component dispatchComponent;

    /**
     * Creates a new StripView object.
     */
    public StripView(  )
    {
        super(  );
        setLayout( null );
        rendererPane =
            new CellRendererPane(  )
                {
                    public void repaint( int x, int y, int width, int height )
                    {
                    }
                };
        add( rendererPane );

        ToolTipManager toolTipManager = ToolTipManager.sharedInstance(  );
        toolTipManager.registerComponent( this );

        setFocusable( true );

        addMouseListener( new MouseInputHandler(  ) );
        addKeyListener( new KeyInputListener(  ) );

        getActionMap(  ).put( "up", new UpAction(  ) );
        getActionMap(  ).put( "down", new DownAction(  ) );
        getActionMap(  ).put( "left", new LeftAction(  ) );
        getActionMap(  ).put( "right", new RightAction(  ) );

        InputMap map =
            getInputMap( JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        map.put( KeyStroke.getKeyStroke( "UP" ), "up" );
        map.put( KeyStroke.getKeyStroke( "DOWN" ), "down" );
        map.put( KeyStroke.getKeyStroke( "LEFT" ), "left" );
        map.put( KeyStroke.getKeyStroke( "RIGHT" ), "right" );

        addFocusListener( new MyFocusListener(  ) );
    }

    /**
     * Creates a new StripView object.
     *
     * @param model DOCUMENT ME!
     * @param renderer DOCUMENT ME!
     */
    public StripView( Model model, StripRenderer renderer )
    {
        this(  );
        setModel( model );
        setRenderer( renderer );
    }

    private void setDispatchComponent( Point p )
    {

        if( 
            ( dispatchComponent != null )
                && dispatchComponent.getBounds(  ).contains( p ) )
        {

            return;
        }

        Component editorComponent = getEditorAt( p );

        if( editorComponent == null )
        {

            return;
        }

        Point p2 =
            SwingUtilities.convertPoint( StripView.this, p, editorComponent );
        dispatchComponent =
            SwingUtilities.getDeepestComponentAt( editorComponent, p2.x, p2.y );
    }

    /*
     * Overrided to allow us to stretch the rendererPane to cover the whole
     * StripView.
     *
     * @see java.awt.Component#doLayout()
     */
    public void doLayout(  )
    {
        rendererPane.setBounds( 0, 0, getWidth(  ), getHeight(  ) );

        if( model == null )
        {
            channelHeight = getHeight(  );
        }
        else if( model.getRowCount(  ) > 0 )
        {
            channelHeight = getHeight(  ) / model.getRowCount(  );
        }
        else
        {
            channelHeight = 30;
        }

        super.doLayout(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param vis DOCUMENT_ME!
     */
    public void setVisible( boolean vis )
    {
        super.setVisible( vis );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Model getModel(  )
    {

        return model;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param model DOCUMENT_ME!
     */
    public void setModel( Model model )
    {
        this.model = model;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param renderer DOCUMENT_ME!
     */
    public void setRenderer( StripRenderer renderer )
    {
        this.renderer = renderer;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param editor DOCUMENT_ME!
     */
    public void setEditor( StripRenderer editor )
    {
        this.editor = editor;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param min DOCUMENT_ME!
     * @param max DOCUMENT_ME!
     */
    public void setHorizontalRange( long min, long max )
    {
        this.min = min;
        this.max = max;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param width DOCUMENT_ME!
     * @param height DOCUMENT_ME!
     */
    public void setIntercellSpacing( int width, int height )
    {
        this.horGap = width;
        this.verGap = height;
    }

    /**
     * DOCUMENT_ME!
     */
    public void focusSomething(  )
    {

        if( model == null )
        {

            return;
        }

        if( currentStrip != null )
        {

            return;
        }

        Strip s = null;
        int row;

        for( row = 0; row < model.getRowCount(  ); row++ )
        {
            s = model.getNextStrip( row, min );

            if( s != null )
            {

                break;
            }
        }

        if( s != null )
        {
            focusStrip( row, s );
        }

        return;
    }

    /**
     * focus a strip at time millis on the current or some other row
     *
     * @param millis time in milliseconds
     */
    public void focus( long millis )
    {

        Point p = new Point( xToXPixel( millis ), 0 );
        final int startRow = ( currentStrip != null ) ? currentRow : 0;

        int row = startRow;
        final int rowCount = model.getRowCount(  );

        do
        {
            p.y = rowToYPixel( row );

            Strip s = getStripAt( p );

            if( s != null )
            {
                focusStrip( row, s );

                return;
            }

            if( ++row >= rowCount )
            {
                row = 0;
            }
        }
        while( row != startRow );
    }

    private void focusStrip( int row, Strip s )
    {

        if( s == null )
        {

            return;
        }

        // repositions the editor component at the given strip
        Component c = getEditorAt( s, row );
        Point middle =
            new Point( 
                c.getX(  ) + ( c.getWidth(  ) / 2 ),
                c.getY(  ) + ( c.getHeight(  ) / 2 ) );

        if( c instanceof ProgrammeJLabel )
        {
            ( (ProgrammeJLabel)c ).onFocus(  );
        }

        setDispatchComponent( middle );

        Rectangle r1 = getStripRect( currentRow, currentStrip );
        currentRow = row;
        currentStrip = s;

        Rectangle r2 = getStripRect( currentRow, currentStrip );

        if( r1 != null )
        {
            repaint( r1 );
        }

        if( r2 != null )
        {
            repaint( r2 );

            // HACK ALERT!
            Component p = getParent(  );

            if( p instanceof javax.swing.JViewport )
            {

                javax.swing.JViewport vp = (javax.swing.JViewport)p;
                Point origin = vp.getViewPosition(  );
                r2.translate( -origin.x, -origin.y );
                vp.scrollRectToVisible( r2 );
            }
        }
    }

    private int yPixelToRow( int y )
    {

        return y / channelHeight;
    }

    private long xPixelToX( int x )
    {

        return (long)( x / widthMultiplier ) + min;
    }

    private int rowToYPixel( int row )
    {

        return row * channelHeight;
    }

    private int xToXPixel( long x )
    {

        return (int)( ( x - min ) * widthMultiplier );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param p DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Strip getStripAt( Point p )
    {

        int row = yPixelToRow( p.y );
        long x = xPixelToX( p.x );
        Strip s = model.getValueAt( row, x );

        if( s == null )
        {

            return null;
        }

        if( ( s.start > x ) || ( s.end < x ) )
        {

            return null;
        }

        return s;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param g DOCUMENT_ME!
     */
    public void paintChildren( Graphics g )
    {

        if( ( model == null ) || ( renderer == null ) )
        {

            return;
        }

        int width = getSize(  ).width;

        // Find the multiplier to help us position strips
        widthMultiplier = (double)width / ( max - min );

        Rectangle r = g.getClipBounds(  );
        int rowMin = yPixelToRow( r.y );
        int rowMax = yPixelToRow( r.y + r.height );
        long xMin = xPixelToX( r.x );
        long xMax = xPixelToX( r.x + r.width );

        for( int y = rowMin; y <= rowMax; y++ )
        {
            paintRow( g, y, xMin, xMax );
        }

        /*if (currentStrip != null) {
            Component c = getEditorAt(currentStrip, currentRow);
            c.paint(g);
        }*/
    }

    private void paintRow( Graphics g, int y, long x1, long x2 )
    {

        Strip s = model.getValueAt( y, x1 );

        if( s == null )
        {
            s = model.getNextStrip( y, x1 );
        }

        for( 
            ; ( s != null ) && ( s.start <= x2 );
                s = model.getNextStrip( y, s.end ) )
        {

            if( s.end < x1 )
            {

                continue;
            }

            paintStrip( g, y, s );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param row DOCUMENT_ME!
     * @param s DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Rectangle getStripRect( int row, Strip s )
    {

        if( s == null )
        {

            return null;
        }

        int left = xToXPixel( s.start );
        int right = xToXPixel( s.end );

        int top = rowToYPixel( row );
        int bottom = rowToYPixel( row + 1 );

        return new Rectangle( 
            left + ( horGap / 2 ), top + ( verGap / 2 ), right - left - horGap,
            bottom - top - verGap );
    }

    private void paintStrip( Graphics g, int y, Strip s )
    {

        Component c = prepareRenderer( renderer, y, s );
        Rectangle r = getStripRect( y, s );

        rendererPane.paintComponent( g, c, this, r );

    }

    private Component getRendererAt( Point p )
    {

        Strip s = getStripAt( p );
        int y = yPixelToRow( p.y );
        Rectangle r = getStripRect( y, s );
        Component c = prepareRenderer( renderer, y, s );

        if( c == null )
        {

            return null;
        }

        c.setBounds( r );

        return c;
    }

    private Component getEditorAt( Point p )
    {

        Strip s = getStripAt( p );
        int y = yPixelToRow( p.y );

        return getEditorAt( s, y );
    }

    private Component getEditorAt( Strip s, int y )
    {

        Rectangle r = getStripRect( y, s );
        Component c = prepareRenderer( editor, y, s );

        if( c == null )
        {

            return null;
        }

        Component parent = c.getParent(  );

        if( parent == rendererPane )
        {
            rendererPane.remove( c );
        }

        if( parent != StripView.this )
        {
            StripView.this.add( c );
        }

        c.setBounds( r );

        return c;
    }

    protected Component prepareRenderer( 
        StripRenderer renderer, int y, Strip s )
    {

        if( ( renderer == null ) || ( s == null ) || ( s.value == null ) )
        {

            return null;
        }

        boolean hasFocus = false;

        if( 
            ( currentStrip != null ) && ( currentRow == y )
                && ( currentStrip.start == s.start )
                && ( currentStrip.end == s.end ) )
        {
            hasFocus = true;
        }

        Component c =
            renderer.getStripRendererComponent( 
                this, s.value, false, hasFocus, y, s.start, s.end );

        return c;
    }

    /*static Vector empty = new Vector();
    public Vector getSelectedLabels() {
        // FIXME
        return empty;
    }*/

    /**
     * Overrides <code>JComponent</code>'s<code>getToolTipText</code> method
     * in order to allow the renderer's tips to be used if it has text set.
     *
     * @see JComponent#getToolTipText
     */
    public String getToolTipText( MouseEvent event )
    {

        Point p = event.getPoint(  );

        // Locate the renderer under the event location
        Strip s = getStripAt( p );
        int row = yPixelToRow( p.y );

        if( ( s == null ) || ( row < 0 ) )
        {

            return getToolTipText(  );
        }

        Component component = prepareRenderer( renderer, row, s );

        // Only JComponents can have tooltips
        if( !( component instanceof JComponent ) )
        {

            return getToolTipText(  );
        }

        // Convert the event to the renderer's coordinate system
        Rectangle r = getStripRect( row, s );
        p.translate( -r.x, -r.y );

        MouseEvent newEvent =
            new MouseEvent( 
                component, event.getID(  ), event.getWhen(  ),
                event.getModifiers(  ), p.x, p.y, event.getClickCount(  ),
                event.isPopupTrigger(  ) );

        String tip = ( (JComponent)component ).getToolTipText( newEvent );

        // No tip from the renderer get our own tip
        if( tip == null )
        {
            tip = getToolTipText(  );
        }

        return tip;
    }

    // --- Scrollable implementation ---

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
     */
    public boolean getScrollableTracksViewportHeight(  )
    {

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
     */
    public boolean getScrollableTracksViewportWidth(  )
    {

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
     */
    public Dimension getPreferredScrollableViewportSize(  )
    {

        return getPreferredSize(  );
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
     *      int, int)
     */
    public int getScrollableBlockIncrement( 
        Rectangle visibleRect, int orientation, int direction )
    {

        return visibleRect.width;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
     *      int, int)
     */
    public int getScrollableUnitIncrement( 
        Rectangle visibleRect, int orientation, int direction )
    {

        return visibleRect.width / 10;
    }

    /**
     * The StripView.Model interface specifies the methods {@link Strip}will
     * use to interrogate a strip data model.
     *
     * @see StripView
     */
    public interface Model
    {

        /*
         * TODO
         *
         * /** Adds a listener to the list that is notified each time a change
         * to the data model occurs. void
         * addStripModelListener(StripViewModelListener l);
         *
         * /** Removes a listener from the list that is notified each time a
         * change to the data model occurs. void
         * removeStripModelListener(StripViewModelListener l);
         *
         */

        /**
         * Returns the number of rows in the model
         *
         * @return DOCUMENT_ME!
         */
        int getRowCount(  );

        /**
         * Returns the strip on row <code>rowIndex</code> covering point
         * <code>x</code>, or the nearest one with a smaller
         * <code>start</code>.
         *
         * @param rowIndex DOCUMENT ME!
         * @param x DOCUMENT ME!
         *
         * @return DOCUMENT_ME!
         */
        Strip getValueAt( int rowIndex, long x );

        /**
         * Returns the strip on row <code>rowIndex</code> covering point
         * <code>x</code>, or the nearest one with a bigger
         * <code>start</code>.
         *
         * @param rowIndex DOCUMENT ME!
         * @param x DOCUMENT ME!
         *
         * @return DOCUMENT_ME!
         */
        Strip getNextStrip( int rowIndex, long x );

        /*
         * TODO /** Sets the value for the strip on row <code> rowIndex </code>
         * covering point <code> x </code> .
         *
         * void setValueAt(Object aValue, int rowIndex, long x1, long x2); TBD
         */
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Strip
    {

        long start;
        long end;
        Object value;

        /**
         * Creates a new Strip object.
         *
         * @param start DOCUMENT ME!
         * @param end DOCUMENT ME!
         * @param value DOCUMENT ME!
         */
        public Strip( long start, long end, Object value )
        {
            this.start = start;
            this.end = end;
            this.value = value;
        }
    }

    class LeftAction extends AbstractAction
    {

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {

            if( ( model == null ) || ( currentStrip == null ) )
            {

                return;
            }

            Strip s = model.getValueAt( currentRow, currentStrip.start - 1 );

            if( s != null )
            {
                focusStrip( currentRow, s );
            }

            x_pos = NONE;
        }
    }

    class RightAction extends AbstractAction
    {

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {

            if( ( model == null ) || ( currentStrip == null ) )
            {

                return;
            }

            Strip s = model.getNextStrip( currentRow, currentStrip.start + 1 );

            if( s != null )
            {
                focusStrip( currentRow, s );
            }

            x_pos = NONE;
        }
    }

    class UpAction extends AbstractAction
    {

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {

            // If we don't have a model or a strip, exit
            if( ( model == null ) || ( currentStrip == null ) )
            {

                return;
            }

            // If we're in the top row, exit
            if( currentRow < 1 )
            {

                return;
            }

            // If we don't have an x_pos value stored, store the midpoint of
            // the current programme.
            if( x_pos == NONE )
            {
                x_pos = ( currentStrip.start + currentStrip.end ) / 2;
            }

            // Step through the rows looking for a programme that overlaps
            // x_pos
            Strip s = null;
            int row;

            for( row = currentRow - 1; row >= 0; row-- )
            {
                s = model.getValueAt( row, x_pos );

                if( ( s != null ) && ( s.end >= x_pos ) )
                {

                    break;
                }
                else
                {
                    s = null;
                }
            }

            // Go to the strip we found
            if( s != null )
            {
                focusStrip( row, s );
            }
            else
            {

                // If we found nothing (and we know we're not in the top row)
                // go to the closest strip in the rows above.
                for( row = currentRow - 1; row >= 0; row-- )
                {

                    // Try to the left
                    s = model.getValueAt( row, x_pos );

                    if( s != null )
                    {

                        break;
                    }

                    // And the right
                    s = model.getNextStrip( row, x_pos );

                    if( s != null )
                    {

                        break;
                    }
                }

                // If we found something, go there.
                if( s != null )
                {
                    focusStrip( row, s );
                }

                // Otherwise, give up - there's nothing in the rows below
            }
        }
    }

    class DownAction extends AbstractAction
    {

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( ActionEvent e )
        {

            // If we don't have a model or a strip, exit
            if( ( model == null ) || ( currentStrip == null ) )
            {

                return;
            }

            // If we're on the bottom row, exit
            if( ( currentRow + 1 ) >= model.getRowCount(  ) )
            {

                return;
            }

            // If we don't have an x_pos value stored, store the midpoint of
            // the current programme.
            if( x_pos == NONE )
            {
                x_pos = ( currentStrip.start + currentStrip.end ) / 2;
            }

            // Step through the rows looking for a programme that overlaps
            // x_pos
            Strip s = null;
            int row;

            for( row = currentRow + 1; row < model.getRowCount(  ); row++ )
            {
                s = model.getValueAt( row, x_pos );

                if( ( s != null ) && ( s.end >= x_pos ) )
                {

                    break;
                }
                else
                {
                    s = null;
                }
            }

            // Go to the strip we found
            if( s != null )
            {
                focusStrip( row, s );
            }
            else
            {

                // If we found nothing (and we know we're not in the bottom row)
                // go to the closest strip in the rows below.
                for( row = currentRow + 1; row < model.getRowCount(  );
                        row++ )
                {

                    // Try to the left
                    s = model.getValueAt( row, x_pos );

                    if( s != null )
                    {

                        break;
                    }

                    // And the right
                    s = model.getNextStrip( row, x_pos );

                    if( s != null )
                    {

                        break;
                    }
                }

                // If we found something, go there.
                if( s != null )
                {
                    focusStrip( row, s );
                }

                // Otherwise, give up - there's nothing in the rows above
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public class MyFocusListener implements FocusListener
    {

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void focusGained( FocusEvent e )
        {
            repaint(  );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void focusLost( FocusEvent e )
        {
            repaint(  );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public class KeyInputListener implements KeyListener
    {

        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
         */
        public void keyPressed( KeyEvent e )
        {

            if( e.isConsumed(  ) )
            {

                return;
            }

            if( dispatchComponent != null )
            {
                dispatchComponent.dispatchEvent( e );
            }
            else
            {
                System.out.println( "nowhere to dispatch" ); // TODO DEBUG
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
         */
        public void keyReleased( KeyEvent e )
        {

            if( dispatchComponent != null )
            {
                dispatchComponent.dispatchEvent( e );
            }
            else
            {
                System.out.println( "nowhere to dispatch" ); // TODO DEBUG
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
         */
        public void keyTyped( KeyEvent e )
        {

            if( dispatchComponent != null )
            {
                dispatchComponent.dispatchEvent( e );
            }
            else
            {
                System.out.println( "nowhere to dispatch" ); // TODO DEBUG
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public class MouseInputHandler implements MouseInputListener
    {

        private boolean selectedOnPress;

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseClicked( MouseEvent e )
        {

            if( !isFocusOwner(  ) )
            {
                requestFocusInWindow(  );
            }

            if( shouldIgnore( e ) )
            {

                return;
            }

            repostEvent( e );
        }

        private boolean repostEvent( MouseEvent e )
        {
            setDispatchComponent( e.getPoint(  ) );

            if( dispatchComponent == null )
            {

                return false;
            }

            MouseEvent e2 =
                SwingUtilities.convertMouseEvent( 
                    StripView.this, e, dispatchComponent );
            dispatchComponent.dispatchEvent( e2 );

            return true;
        }

        private boolean shouldIgnore( MouseEvent e )
        {

            return e.isConsumed(  );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mousePressed( MouseEvent e )
        {

            if( e.isConsumed(  ) )
            {
                selectedOnPress = false;

                return;
            }

            selectedOnPress = false /* true */;
            adjustFocusAndSelection( e );
        }

        void adjustFocusAndSelection( MouseEvent e )
        {

            if( shouldIgnore( e ) )
            {

                return;
            }

            Point p = e.getPoint(  );
            int row = yPixelToRow( p.y );
            Strip s = getStripAt( p );

            if( s == null )
            {

                return;
            }

            focusStrip( row, s );

            repostEvent( e );
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseReleased( MouseEvent e )
        {

            if( selectedOnPress )
            {

                if( shouldIgnore( e ) )
                {

                    return;
                }

                repostEvent( e );

                /* dispatchComponent = null; */
            }
            else
            {
                adjustFocusAndSelection( e );
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseEntered( MouseEvent e )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseExited( MouseEvent e )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseMoved( MouseEvent e )
        {
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void mouseDragged( MouseEvent e )
        {

            if( shouldIgnore( e ) )
            {

                return;
            }

            repostEvent( e );
        }
    }
}
