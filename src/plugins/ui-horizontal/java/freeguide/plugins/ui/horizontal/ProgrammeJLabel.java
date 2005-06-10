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
package freeguide.plugins.ui.horizontal;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.IModuleReminder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;

import java.net.URL;

import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;

/**
 * A JLabel that displays a TV programme
 *
 * @author Andy Balaam
 * @version 5
 */
public class ProgrammeJLabel extends JLabel
{

    private static boolean alignTextToLeftOfScreen;
    private static Color nonTickedColour;
    private static Color movieColour;
    private static Color heartColour;
    private static Border nonTickedBorder;
    private static Border tickedBorder;
    private static Border movieBorder;
    static private ProgrammePopupMenu popMenuProgramme;
    protected final IModuleReminder[] reminders =
        Application.getInstance(  ).getReminders(  );

    //static private ToggleAction selectAction;
    //static private ToggleAction favouriteAction;

    /** The menu item to view a link */
    private Model model;
    final private ProgrammeFormat textFormat;
    final private ProgrammeFormat htmlFormat;

    // Cached model data
    private String tooltip;
    protected TVProgramme programme;

    //private boolean isInGuide;
    //private boolean isFavourite;
    private HorizontalViewer controller;

    ProgrammeJLabel( 
        HorizontalViewer controller, java.text.SimpleDateFormat timeFormat,
        java.awt.Font font )
    {
        this( controller, null, timeFormat, font );

    }

    /**
     * Construct a ProgrammeJLabel make it selected, favourite, etc as
     * appropriate.
     *
     * @param controller DOCUMENT ME!
     * @param model the model giving the programme shown in this label
     * @param timeFormat the format in which to display the time, or null to
     *        not display the time
     * @param font The font to use
     */
    ProgrammeJLabel( 
        HorizontalViewer controller, Model model, SimpleDateFormat timeFormat,
        Font font )
    {
        super(  );

        this.controller = controller;

        textFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat,
                controller.config.displayDelta );

        htmlFormat =
            new ProgrammeFormat( 
                ProgrammeFormat.HTML_FORMAT, timeFormat,
                controller.config.displayDelta );

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

                        //toggleSelection(  );
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

            setText( "(null)" );

        }

        else
        {

            // Cache model data
            programme = model.getValue(  );

            setText( textFormat.formatForMainGuide( programme ) );

            setBorder( 
                BorderFactory.createCompoundBorder( 
                    BorderFactory.createLineBorder( Color.BLACK ),
                    BorderFactory.createLineBorder( Color.WHITE, 2 ) ) );
            setBackground( Color.WHITE );

            for( int i = 0; i < reminders.length; i++ )
            {
                reminders[i].onPaintProgrammeLabel( programme, this );
            }
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

        for( int i = 0; i < reminders.length; i++ )
        {
            reminders[i].onPaintProgrammeLabel( 
                programme, this, (Graphics2D)g );
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

    /*private void toggleSelection(  )
    {
    
    if( SelectionManager.isInGuide( programme ) )
    {
        SelectionManager.deselectProgramme( programme );
    
    }
    
    else
    {
        SelectionManager.selectProgramme( programme );
    }
    
    MainController.remindersReschedule(  );
    
    controller.panel.printedGuideArea.update(  );
    }*/
    /* private void updateIsInGuide( boolean isInGuide )
    {
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
    
     repaint(  );
    
    }*/

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

        // If the prefs say no tooltips, just return null
        if( !controller.config.displayTooltips )
        {

            return null;

        }

        String tooltip = super.getToolTipText(  );

        if( tooltip != null )
        {

            return tooltip;

        }

        boolean printDelta = controller.config.displayDelta;

        if( ( this.tooltip != null ) && !printDelta )
        {

            return this.tooltip;

        }

        htmlFormat.setWrap( true );

        htmlFormat.setOnScreen( false );

        this.tooltip = htmlFormat.formatLong( programme ).toString(  );

        return this.tooltip;

    }

    protected ProgrammePopupMenu getPopupMenu(  )
    {

        if( popMenuProgramme != null )
        {

            return popMenuProgramme;

        }

        popMenuProgramme = new ProgrammePopupMenu(  );

        popMenuProgramme.addPopupMenuListener( 
            new javax.swing.event.PopupMenuListener(  )
            {
                public void popupMenuWillBecomeVisible( PopupMenuEvent evt )
                {

                    // popMenuProgrammePopupMenuWillBecomeVisible( 
                    //   evt, favouriteAction );
                    popMenuProgramme.display( ProgrammeJLabel.this );

                }

                public void popupMenuWillBecomeInvisible( PopupMenuEvent evt )
                {
                }

                public void popupMenuCanceled( PopupMenuEvent evt )
                {
                }
            } );

        /*selectAction =
        new ToggleAction(
        controller.getLocalizer(  ).getLocalizedMessage(
            "add_to_guide" ),
        controller.getLocalizer(  ).getLocalizedMessage(
            "remove_from_guide" ) )
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
        
        popMenuProgramme.add( selectAction );*/
        /*favouriteAction =
        new ToggleAction(
        controller.getLocalizer(  ).getLocalizedMessage(
            "add_to_favourites" ),
        controller.getLocalizer(  ).getLocalizedMessage(
            "remove_from_favourites" ) )
        {
            public void actionPerformed( ActionEvent e )
            {
                setToggle( !state );
        
                mbtAddFavouriteActionPerformed( e );
        
            }
        };
        
        popMenuProgramme.add( favouriteAction );*/
        return popMenuProgramme;

    }

    /**
     * Event handler when the popup menu is going to be displayed
     */

    /*static protected void popMenuProgrammePopupMenuWillBecomeVisible(
    PopupMenuEvent evt, ToggleAction favouriteAction )
    {
    
    ProgrammeJLabel label =
        ( (ProgrammePopupMenu)evt.getSource(  ) ).label;
    
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
    }*/

    /**
     * Event handler for when the Add to Favourites popup menu item is clicked
     */

    /*protected void mbtAddFavouriteActionPerformed(
    java.awt.event.ActionEvent evt )
    {
    
    // Find out which ProgrammeJLabel was right-clicked, and call its
    // setFavourite method.
    ProgrammeJLabel label =
        ( (ProgrammePopupMenu)( (java.awt.Component)evt.getSource(  ) )
        .getParent(  ) ).label;
    
    label.setFavourite( !label.getModel(  ).isFavourite(  ) );
    
    controller.panel.printedGuideArea.update(  );
    
    }*/

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    static public interface Model
    {
        TVProgramme getValue(  );

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
}
