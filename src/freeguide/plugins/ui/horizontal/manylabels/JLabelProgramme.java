package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.TemplateParser;

import freeguide.common.plugininterfaces.IModuleReminder;

import freeguide.plugins.ui.horizontal.manylabels.templates.HandlerProgrammeInfo;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;

import java.io.StringWriter;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
    /** Cache for unfocused borders by color. */
    protected static final Map<Color, Border> unfocusedBordersCache =
        new HashMap<Color, Border>(  );

    /** Cache for focused borders by color. */
    protected static final Map<Color, Border> focusedBordersCache =
        new HashMap<Color, Border>(  );

    /** Programme for current label. */
    final protected TVProgramme programme;

    /** Parent controller. */
    final protected HorizontalViewer controller;

    /** Cached tooltip text. */
    private String tooltip;
    protected final boolean moveNames;
    protected final DateFormat timeFormat;
    protected final List<ImageIcon> icons = new ArrayList<ImageIcon>(  );

/**
     * Creates a new JLabelProgramme object.
     * 
     * @param programme
     *            DOCUMENT ME!
     * @param main
     *            DOCUMENT ME!
     * @param moveNames
     *            DOCUMENT ME!
     * @param timeFormat
     *            DOCUMENT ME!
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
        setupReminder(  );
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
    public void setupReminder(  )
    {
        final IModuleReminder reminder =
            Application.getInstance(  ).getReminder(  );

        if( reminder != null )
        {
            reminder.showProgramme( programme, this, icons );
            Collections.reverse( icons );
        }

        setBorder( 
            isFocusOwner(  ) ? getFocusedBorder( getBackground(  ) )
                             : getUnfocusedBorder( getBackground(  ) ) );
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

        int y = 3;
        int x = getWidth(  ) - 3;

        for( final ImageIcon img : icons )
        {
            x -= img.getImage(  ).getWidth( null );
            x -= 2;
            g.drawImage( img.getImage(  ), x, y, null );
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

    /**
     * Get unfocused border from cache and create it if need.
     *
     * @param color color
     *
     * @return border
     */
    protected static Border getUnfocusedBorder( final Color color )
    {
        Border result = unfocusedBordersCache.get( color );

        if( result == null )
        {
            result = BorderFactory.createCompoundBorder( 
                    BorderFactory.createLineBorder( Color.BLACK, 1 ),
                    BorderFactory.createLineBorder( color, 2 ) );
            unfocusedBordersCache.put( color, result );
        }

        return result;
    }

    /**
     * Get focused border from cache and create it if need.
     *
     * @param color color
     *
     * @return border
     */
    protected static Border getFocusedBorder( final Color color )
    {
        Border result = focusedBordersCache.get( color );

        if( result == null )
        {
            result = BorderFactory.createCompoundBorder( 
                    BorderFactory.createLineBorder( Color.BLUE, 2 ),
                    BorderFactory.createLineBorder( color, 1 ) );
            focusedBordersCache.put( color, result );
        }

        return result;
    }
}
