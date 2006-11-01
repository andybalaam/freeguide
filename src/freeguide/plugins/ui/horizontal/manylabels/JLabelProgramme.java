package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.TemplateParser;

import freeguide.common.plugininterfaces.IModuleReminder;

import freeguide.plugins.ui.horizontal.manylabels.templates.HandlerProgrammeInfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import java.io.StringWriter;

import java.text.DateFormat;

import java.util.Date;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

/**
 * Label for display programme info.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class JLabelProgramme extends JLabel
{
    protected static Border DEFAULT_BORDER;
    protected static Border MOVIE_BORDER;
    protected static Border INGUIDE_BORDER;
    protected static Border FOCUSED_BORDER;

    /** Setup border colors */
    protected static Border FAVOURITE_BORDER;
    protected static Border GUIDE_BORDER;

    /** Standard reminder. */
    protected static IModuleReminder REMINDER;

    /** Heart shape. */
    protected final static Shape HEART_SHAPE;

    static
    {
        GeneralPath path = new GeneralPath(  );

        path.moveTo( 300, 200 );

        path.curveTo( 100, 0, 0, 400, 300, 580 );

        path.moveTo( 300, 580 );

        path.curveTo( 600, 400, 500, 0, 300, 200 );

        HEART_SHAPE = path;

    }

    /** Square shape. */
    protected final static Rectangle SQUARE_SHAPE =
        new Rectangle( 125, 650, 350, 350 );

    /** Programme for current label. */
    final protected TVProgramme programme;

    /** Parent controller. */
    final protected HorizontalViewer controller;

    /** Need to draw heart symbol. */
    protected boolean isDrawHeart;

    /** Need to draw square symbol (=record). */
    protected boolean isDrawSquare;

    /** Cached tooltip text. */
    private String tooltip;
    protected final boolean moveNames;
    protected final DateFormat timeFormat;

/**
     * Creates a new JLabelProgramme object.
     *
     * @param programme DOCUMENT ME!
     * @param main DOCUMENT ME!
     * @param moveNames DOCUMENT ME!
     * @param timeFormat DOCUMENT ME!
     */
    public JLabelProgramme( 
        final TVProgramme programme, final HorizontalViewer main,
        final boolean moveNames, final DateFormat timeFormat )
    {
        this.timeFormat = timeFormat;
        this.programme = programme;
        this.controller = main;
        this.moveNames = moveNames;
        setText( getTitle( programme ) );
        setupColors(  );
        setupSymbols(  );
        setOpaque( true );
        setFocusable( true );
        addMouseListener( main.handlers.labelProgrammeMouseListener );
        addFocusListener( main.handlers.labelProgrammeFocusListener );

        setActionMap( main.handlers.labelProgrammeActionMap );
        setInputMap( 
            JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
            main.handlers.labelProgrammeInputMap );

        ToolTipManager tipManager = ToolTipManager.sharedInstance(  );

        // Register this component for tooltip management.
        // This is normally done by setting the tooltip text,
        // but we want to "lazily" evaluate tooltip text--we defer
        // creation of the tip text until the tip is actually needed.
        // (see the getToolTipText() method below)
        tipManager.registerComponent( this );
    }

    /**
     * Generate title for label.
     *
     * @param programme
     *
     * @return title text
     */
    protected String getTitle( final TVProgramme programme )
    {
        final StringBuffer toAppendTo = new StringBuffer(  );

        long programmeStart = programme.getStart(  );

        String programmeTitle = programme.getTitle(  );

        String programmeSubTitle = programme.getSubTitle(  );

        String programmeStarString = programme.getStarString(  );

        if( 
            ( (HorizontalViewerConfig)controller.getConfig(  ) ).displayTime
                && ( timeFormat != null ) )
        {
            toAppendTo.append( 
                timeFormat.format( new Date( programmeStart ) ) ).append( " " );
        }

        toAppendTo.append( programmeTitle );

        if( programmeSubTitle != null )
        {
            toAppendTo.append( ": " ).append( programmeSubTitle );

        }

        if( programme.getIsMovie(  ) && ( programmeStarString != null ) )
        {
            toAppendTo.append( " " ).append( programmeStarString );

        }

        if( programme.getPreviouslyShown(  ) )
        {
            toAppendTo.append( " " );

            toAppendTo.append( 
                Application.getInstance(  ).getLocalizedMessage( "r" ) );

        }

        return toAppendTo.toString(  );
    }

    /**
     * Get programme for label.
     *
     * @return programme
     */
    public TVProgramme getProgramme(  )
    {
        return programme;
    }

    /**
     * Setup colors for current label.
     */
    public void setupColors(  )
    { // is favourite?

        if( 
            ( REMINDER != null )
                && ( REMINDER.getFavourite( programme ) != null )
                && REMINDER.isHighlighted( programme ) )
        {
            setBackground( controller.config.colorFavourite );
            setBorder( FAVOURITE_BORDER );

        }
        else if( 
            ( REMINDER != null ) && REMINDER.isSelected( programme )
                && REMINDER.isHighlighted( programme ) ) // is selected??
        {
            setBackground( controller.config.colorGuide );
            setBorder( GUIDE_BORDER );
        }
        else if( ( REMINDER != null ) && REMINDER.isHighlighted( programme ) ) // is highlighted?
        {
            setBackground( controller.config.colorTicked );
            setBorder( INGUIDE_BORDER );

        }
        else if( !programme.getIsMovie(  ) )
        {
            setBackground( controller.config.colorNonTicked );
            setBorder( DEFAULT_BORDER );
        }
        else
        {
            setBackground( controller.config.colorMovie );
            setBorder( MOVIE_BORDER );
        }

        if( isFocusOwner(  ) )
        {
            setBorder( FOCUSED_BORDER );
        }
    }

    /**
     * Setup colors and borders using parent controller's config.
     *
     * @param main
     */
    public static void setupLabel( final HorizontalViewer main )
    {
        // TODO change
        IModuleReminder[] rems = Application.getInstance(  ).getReminders(  );

        if( rems.length > 0 )
        {
            REMINDER = rems[0];
        }
        else
        {
            REMINDER = null;
        }

        DEFAULT_BORDER = BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorNonTicked, 2 ) );
        MOVIE_BORDER = BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorMovie, 2 ) );
        INGUIDE_BORDER = BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorTicked, 2 ) );
        FOCUSED_BORDER = BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLUE, 2 ),
                BorderFactory.createLineBorder( main.config.colorNonTicked, 1 ) );
/**
         * Setup border colors
         * 
         *  @author Patrick Huber, Annetta Schaad (aschaad at hotmail.com)
         *  
         *  inserted: borders for favourite and selected 
         */
        GUIDE_BORDER = BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorGuide, 2 ) );
        FAVOURITE_BORDER = BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorFavourite, 2 ) );
    }

    /**
     * Setup need to draw heart for current label.
     */
    protected void setupSymbols(  )
    {
        if( REMINDER == null )
        {
            isDrawHeart = false;
            isDrawSquare = false; // for recording.
        }
        else
        {
            freeguide.common.lib.fgspecific.selection.Favourite f =
                REMINDER.getFavourite( programme );
            isDrawHeart = ( f != null );
            isDrawSquare = ( ( f != null ) && f.getRecord(  ) );
        }
    }

    /**
     * Paint component override.
     *
     * @param g DOCUMENT ME!
     */
    protected void paintComponent( Graphics g )
    {
        if( moveNames )
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
            super.paintComponent( g );
        }

        if( isDrawHeart )
        {
            Graphics2D graphics = (Graphics2D)g;
            AffineTransform originalTransform = graphics.getTransform(  );

            graphics.setColor( Color.RED );

            // switch on anti-aliasing
            graphics.setRenderingHint( 
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );

            // Scale and position appropriately--taking into account the borders
            Rectangle bounds = HEART_SHAPE.getBounds(  );

            double scale = 0.45 * ( getHeight(  ) / bounds.getHeight(  ) );

            double right = getWidth(  ) - 2 - ( scale * bounds.getWidth(  ) );

            graphics.translate( right, 2 );

            graphics.scale( scale, scale );

            graphics.fill( HEART_SHAPE );

            if( isDrawSquare )
            {
                graphics.setColor( new Color( 175, 0, 0 ) );
                graphics.fill( SQUARE_SHAPE );
            }

            graphics.setTransform( originalTransform );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getToolTipText(  )
    {
        if( !controller.config.displayTooltips )
        {
            return null;
        }

        String tooltip = super.getToolTipText(  );

        if( tooltip != null )
        {
            return tooltip;
        }

        if( ( this.tooltip != null ) )
        {
            return this.tooltip;
        }

        try
        {
            final StringWriter out = new StringWriter(  );
            TemplateParser parser =
                new TemplateParser( 
                    "resources/plugins/ui/horizontal/manylabels/templates/TemplateProgrammeTooltip.html" );
            parser.process( 
                new HandlerProgrammeInfo( 
                    controller.getLocalizer(  ), programme, timeFormat ), out );
            this.tooltip = out.toString(  );
        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( Level.WARNING, "Error generate tooltip text", ex );
        }

        return this.tooltip;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param startMin DOCUMENT_ME!
     * @param endMax DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public long getMiddle( final long startMin, final long endMax )
    {
        long start = Math.max( getProgramme(  ).getStart(  ), startMin );
        long end = Math.min( getProgramme(  ).getEnd(  ), endMax );

        return ( start + end ) / 2;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param middleTime DOCUMENT_ME!
     * @param startMin DOCUMENT_ME!
     * @param endMax DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isOverlap( 
        final long middleTime, final long startMin, final long endMax )
    {
        long start = Math.max( getProgramme(  ).getStart(  ), startMin );
        long end = Math.min( getProgramme(  ).getEnd(  ), endMax );

        return ( middleTime >= start ) && ( middleTime < end );
    }
}
