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
package freeguide.gui.viewer;

import freeguide.*;

import freeguide.lib.fgspecific.*;

import freeguide.lib.general.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.*;

import java.text.*;

import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.border.*;
import javax.swing.event.PopupMenuEvent;

/**
 * A JLabel that displays a TV programme
 *
 * @author Andy Balaam
 * @version 5
 */
public class ProgrammeJLabel extends JLabel
{

    private final static Shape heartShape;

    static
    {

        GeneralPath path = new GeneralPath(  );
        path.moveTo( 300, 200 );
        path.curveTo( 100, 0, 0, 400, 300, 580 );
        path.moveTo( 300, 580 );
        path.curveTo( 600, 400, 500, 0, 300, 200 );
        heartShape = path;
    }

    private static boolean alignTextToLeftOfScreen;
    private static Color nonTickedColour;
    private static Color tickedColour;
    private static Color movieColour;
    private static Color heartColour;
    private static Border nonTickedBorder;
    private static Border tickedBorder;
    private static Border movieBorder;
    static private ProgrammePopupMenu popMenuProgramme;
    static private ToggleAction selectAction;
    static private ToggleAction favouriteAction;

    /** The menu item to view a link */
    static private javax.swing.JMenuItem mbtGoToWebSite;
    private Model model;
    final private ProgrammeFormat textFormat;
    final private ProgrammeFormat htmlFormat;

    // Cached model data
    private String tooltip;
    private Programme programme;
    private boolean isInGuide;
    private boolean isFavourite;

    ProgrammeJLabel( 
        java.text.SimpleDateFormat timeFormat, java.awt.Font font )
    {
        this( null, timeFormat, font );
    }

    /**
     * Construct a ProgrammeJLabel make it selected, favourite, etc as
     * appropriate.
     *
     * @param model the model giving the programme shown in this label
     * @param timeFormat the format in which to display the time, or null to
     *        not display the time
     * @param font The font to use
     */
    ProgrammeJLabel( Model model, SimpleDateFormat timeFormat, Font font )
    {
        super(  );

        boolean printDelta =
            FreeGuide.prefs.screen.getBoolean( "display_time_delta", true );

        textFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat, printDelta );
        htmlFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat, printDelta );

        ToolTipManager tipManager = ToolTipManager.sharedInstance(  );

        // Register this component for tooltip management.
        // This is normally done by setting the tooltip text,
        // but we want to "lazily" evaluate tooltip text--we defer
        // creation of the tip text until the tip is actually needed.
        // (see the getToolTipText() method below)
        tipManager.registerComponent( this );

        setFont( font );

        setBorder( nonTickedBorder );

        setOpaque( true );

        getActionMap(  ).put( 
            "select",
            new AbstractAction(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    toggleSelection(  );
                }
            } );

        getActionMap(  ).put( 
            "favourite",
            new AbstractAction(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    setFavourite( !getModel(  ).isFavourite(  ) );
                }
            } );

        getActionMap(  ).put( 
            "menu",
            new AbstractAction(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    ProgrammePopupMenu menu = getPopupMenu(  );
                    menu.label = ProgrammeJLabel.this;
                    menu.show( ProgrammeJLabel.this, 0, getHeight(  ) );
                }
            } );

        InputMap map = getInputMap( JComponent.WHEN_FOCUSED );
        map.put( KeyStroke.getKeyStroke( "SPACE" ), "select" );
        map.put( KeyStroke.getKeyStroke( "typed f" ), "favourite" );
        map.put( KeyStroke.getKeyStroke( "shift F10" ), "menu" );

        addMouseListener( 
            new java.awt.event.MouseListener(  )
            {
                public void mouseClicked( java.awt.event.MouseEvent evt )
                {

                    if( evt.getClickCount(  ) == 2 )
                    {
                        toggleSelection(  );
                    }
                }

                public void mousePressed( java.awt.event.MouseEvent evt )
                {
                    maybeShowPopup( evt );
                }

                public void mouseReleased( java.awt.event.MouseEvent evt )
                {
                    maybeShowPopup( evt );
                }

                public void mouseEntered( java.awt.event.MouseEvent evt )
                {
                }

                public void mouseExited( java.awt.event.MouseEvent evt )
                {
                }

                private void maybeShowPopup( java.awt.event.MouseEvent evt )
                {

                    if( evt.isPopupTrigger(  ) )
                    {

                        ProgrammePopupMenu menu = getPopupMenu(  );
                        menu.label = ProgrammeJLabel.this;
                        menu.show( 
                            evt.getComponent(  ), evt.getX(  ), evt.getY(  ) );
                    }
                }
            } );

        setModel( model );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param popup DOCUMENT_ME!
     */
    public void setComponentPopupMenu( JPopupMenu popup )
    {

        // TODO: 1.5 has this method, we could emulate it for previous versions
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

        // Reset tooltip to trigger new one on demand
        this.tooltip = null;

        if( model == null )
        {
            programme = null;
            isInGuide = false;
            isFavourite = false;
            setText( "(null)" );

        }
        else
        {

            // Cache model data
            programme = model.getValue(  );
            updateIsInGuide( model.isInGuide(  ) );
            updateIsFavourite( model.isFavourite(  ) );
            setText( textFormat.formatForMainGuide( programme ) );
        }
    }

    protected void paintComponent( Graphics g )
    {

        if( alignTextToLeftOfScreen )
        {

            // Paint our own text, aligning to the left of the screen
            g.setClip( this.getVisibleRect(  ) );

            // First paint background
            g.setColor( this.getBackground(  ) );
            g.fillRect( 0, 0, this.getWidth(  ), this.getHeight(  ) );

            // Then set everything to draw the text
            g.setFont( this.getFont(  ) );
            g.setColor( this.getForeground(  ) );

            // Compute the right place from the left border
            int fontX = 0;
            Insets ins = this.getInsets(  );

            if( this.getBorder(  ) != null )
            {
                fontX = ins.left;
            }

            if( 
                ( g.getClipBounds(  ) != null )
                    && ( fontX < g.getClipBounds(  ).x ) )
            {
                fontX = g.getClipBounds(  ).x + ins.left;
            }

            // now we now where, draw the text
            g.drawString( 
                getText(  ), fontX,
                ( ins.top
                + ( getHeight(  ) - ins.top - ins.bottom
                + g.getFontMetrics(  ).getAscent(  ) ) ) >> 1 );

        }
        else
        {

            // Just ask the parent to paint the text
            super.paintComponent( g );

        }

        if( isFavourite )
        {
            drawFavouriteIcon( g );
        }

        URL link = programme.getLink(  );

        if( link != null )
        {
            g.setColor( Color.BLUE );

            int width = getWidth(  );
            int height = getHeight(  );

            g.fillRect( width - 4, height - 4, width - 1, height - 1 );

        }
    }

    /**
     * Draws the favourite icon on this panel.
     *
     * @param g The Graphics context to draw on.
     */
    protected void drawFavouriteIcon( final Graphics g )
    {

        Graphics2D g2 = (Graphics2D)g;
        AffineTransform originalTransform = g2.getTransform(  );

        g2.setColor( heartColour );

        // switch on anti-aliasing
        g2.setRenderingHint( 
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        // Scale and position appropriately--taking into account the borders
        Rectangle bounds = heartShape.getBounds(  );
        double scale = 0.45 * ( getHeight(  ) / bounds.getHeight(  ) );
        double right = getWidth(  ) - 2 - ( scale * bounds.getWidth(  ) );
        g2.translate( right, 2 );
        g2.scale( scale, scale );
        g2.fill( heartShape );
        g2.setTransform( originalTransform );

    }

    private void toggleSelection(  )
    {
        setSelected( !isInGuide );
    }

    private void setSelected( boolean isInGuide )
    {

        if( model != null )
        {
            model.setInGuide( isInGuide );
        }

        updateIsInGuide( isInGuide );

        // FIXME - repaint only this strip?
        model.getStripView(  ).repaint(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param isFavourite DOCUMENT_ME!
     */
    public void setFavourite( boolean isFavourite )
    {

        if( model != null )
        {
            model.setFavourite( isFavourite );
        }
    }

    private void updateIsInGuide( boolean isInGuide )
    {
        this.isInGuide = isInGuide;

        if( isInGuide )
        {
            setBorder( tickedBorder );
            setBackground( tickedColour );
        }
        else if( programme.getIsMovie(  ) )
        {
            setBorder( movieBorder );
            setBackground( movieColour );
        }
        else
        {
            setBorder( nonTickedBorder );
            setBackground( nonTickedColour );
        }
    }

    protected void updateIsFavourite( boolean isFavourite )
    {
        this.isFavourite = isFavourite;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param align DOCUMENT_ME!
     */
    public static void setAlignTextToLeftOfScreen( boolean align )
    {
        ProgrammeJLabel.alignTextToLeftOfScreen = align;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param nonTickedColour DOCUMENT_ME!
     */
    public static void setNonTickedColour( Color nonTickedColour )
    {
        ProgrammeJLabel.nonTickedColour = nonTickedColour;
        nonTickedBorder =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK ),
                BorderFactory.createLineBorder( nonTickedColour, 2 ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param tickedColour DOCUMENT_ME!
     */
    public static void setTickedColour( Color tickedColour )
    {
        ProgrammeJLabel.tickedColour = tickedColour;
        tickedBorder =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK ),
                BorderFactory.createLineBorder( tickedColour, 2 ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param movieColour DOCUMENT_ME!
     */
    public static void setMovieColour( Color movieColour )
    {
        ProgrammeJLabel.movieColour = movieColour;
        movieBorder =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK ),
                BorderFactory.createLineBorder( movieColour, 2 ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param heartColour DOCUMENT_ME!
     */
    public static void setHeartColour( Color heartColour )
    {
        ProgrammeJLabel.heartColour = heartColour;
    }

    /**
     * DOCUMENT_ME!
     */
    public void onFocus(  )
    {
        model.onFocus(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getToolTipText(  )
    {

        if( model != null )
        {
            model.isHovering(  );
        }

        // If the prefs say no tooltips, just return null
        if( !FreeGuide.prefs.screen.getBoolean( "display_tooltips", false ) )
        {

            return null;
        }

        String tooltip = super.getToolTipText(  );

        if( tooltip != null )
        {

            return tooltip;
        }

        boolean printDelta =
            FreeGuide.prefs.screen.getBoolean( "display_time_delta", true );

        if( ( this.tooltip != null ) && !printDelta )
        {

            return this.tooltip;
        }

        htmlFormat.setWrap( true );
        htmlFormat.setOnScreen( false );
        this.tooltip = htmlFormat.formatLong( programme ).toString(  );

        return this.tooltip;
    }

    static protected ProgrammePopupMenu getPopupMenu(  )
    {

        if( popMenuProgramme != null )
        {

            return popMenuProgramme;
        }

        popMenuProgramme = new ProgrammePopupMenu(  );

        mbtGoToWebSite = new javax.swing.JMenuItem(  );

        popMenuProgramme.addPopupMenuListener( 
            new javax.swing.event.PopupMenuListener(  )
            {
                public void popupMenuWillBecomeVisible( PopupMenuEvent evt )
                {
                    popMenuProgrammePopupMenuWillBecomeVisible( 
                        evt, favouriteAction );
                }

                public void popupMenuWillBecomeInvisible( PopupMenuEvent evt )
                {
                }

                public void popupMenuCanceled( PopupMenuEvent evt )
                {
                }
            } );

        selectAction =
            new ToggleAction( 
                FreeGuide.msg.getString( "add_to_guide" ),
                FreeGuide.msg.getString( "remove_from_guide" ) )
                {
                    public void actionPerformed( ActionEvent e )
                    {

                        ProgrammeJLabel label =
                            ( (ProgrammePopupMenu)( (java.awt.Component)e
                            .getSource(  ) ).getParent(  ) ).label;
                        label.toggleSelection(  );
                        setToggle( !state );
                    }
                };
        popMenuProgramme.add( selectAction );

        favouriteAction =
            new ToggleAction( 
                FreeGuide.msg.getString( "add_to_favourites" ),
                FreeGuide.msg.getString( "remove_from_favourites" ) )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        setToggle( !state );
                        mbtAddFavouriteActionPerformed( e );
                    }
                };
        popMenuProgramme.add( favouriteAction );

        mbtGoToWebSite.setText( FreeGuide.msg.getString( "go_to_web_site" ) );
        mbtGoToWebSite.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( ActionEvent evt )
                {
                    mbtGoToWebSiteActionPerformed( evt );
                }
            } );

        return popMenuProgramme;
    }

    /**
     * Event handler when the popup menu is going to be displayed
     *
     * @param evt The event object
     * @param favouriteAction DOCUMENT ME!
     */
    static protected void popMenuProgrammePopupMenuWillBecomeVisible( 
        PopupMenuEvent evt, ToggleAction favouriteAction )
    {

        ProgrammeJLabel label =
            ( (ProgrammePopupMenu)evt.getSource(  ) ).label;

        selectAction.setToggle( label.isInGuide );
        favouriteAction.setToggle( label.getModel(  ).isFavourite(  ) );

        int popMenuProgrammeSize = popMenuProgramme.getSubElements(  ).length;
        URL link = label.getModel(  ).getValue(  ).getLink(  );

        if( ( link != null ) && ( popMenuProgrammeSize < 3 ) )
        {
            popMenuProgramme.add( mbtGoToWebSite );
        }

        if( ( link == null ) && ( popMenuProgrammeSize > 2 ) )
        {
            popMenuProgramme.remove( popMenuProgrammeSize - 1 );
        }
    }

    /**
     * Event handler for when the Add to Favourites popup menu item is clicked
     *
     * @param evt The event object
     */
    static protected void mbtAddFavouriteActionPerformed( 
        java.awt.event.ActionEvent evt )
    {

        // Find out which ProgrammeJLabel was right-clicked, and call its
        // setFavourite method.
        ProgrammeJLabel label =
            ( (ProgrammePopupMenu)( (java.awt.Component)evt.getSource(  ) )
            .getParent(  ) ).label;

        label.setFavourite( !label.getModel(  ).isFavourite(  ) );

    }

    /**
     * Event handler for when the Go to web site popup menu item is clicked
     *
     * @param evt The event object
     */
    static protected void mbtGoToWebSiteActionPerformed( 
        java.awt.event.ActionEvent evt )
    {

        Programme programme =
            ( (ProgrammePopupMenu)( (java.awt.Component)evt.getSource(  ) ).getParent(  ) ).label.getModel(  )
                                                                                                 .getValue(  );

        String[] cmds =
            Utils.substitute( 
                FreeGuide.prefs.commandline.getStrings( "browser_command" ),
                "%filename%",
                programme.getLink(  ).toString(  ).replaceAll( "%", "%%" ) );

        Utils.execNoWait( cmds );
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    static public interface Model
    {
        Programme getValue(  );

        boolean isInGuide(  );

        boolean isFavourite(  );

        void setInGuide( boolean state );

        void setFavourite( boolean state );

        StripView getStripView(  );

        void isHovering(  );

        void onFocus(  );
    }

    // --- Static popup menu stuff ---
    static private abstract class ToggleAction extends AbstractAction
    {

        String off;
        String on;
        boolean state = false;

        ToggleAction( String off, String on )
        {
            super( off );
            this.off = off;
            this.on = on;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param state DOCUMENT_ME!
         */
        public void setToggle( boolean state )
        {

            if( this.state == state )
            {

                return;
            }

            this.state = state;

            firePropertyChange( 
                AbstractAction.NAME, state ? off : on, state ? on : off );
        }

        /* (non-Javadoc)
         * @see javax.swing.Action#getValue(java.lang.String)
         */
        public Object getValue( String key )
        {

            if( key.equals( AbstractAction.NAME ) )
            {

                if( state )
                {

                    return on;
                }
                else
                {

                    return off;
                }
            }

            return super.getValue( key );
        }

        /* (non-Javadoc)
         * @see javax.swing.Action#putValue(java.lang.String, java.lang.Object)
         */
        public void putValue( String key, Object newValue )
        {

            if( key.equals( AbstractAction.NAME ) )
            {

                if( state )
                {
                    on = (String)newValue;
                }
                else
                {
                    off = (String)newValue;
                }
            }

            super.putValue( key, newValue );
        }
    }

    /**
     * The popup menu when you right-click a programme
     */
    static protected class ProgrammePopupMenu extends JPopupMenu
    {

        /** DOCUMENT ME! */
        public ProgrammeJLabel label;
    }
}
