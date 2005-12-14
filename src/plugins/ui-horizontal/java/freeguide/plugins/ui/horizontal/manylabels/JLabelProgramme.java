package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModuleReminder;
import freeguide.plugins.IModuleReminder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

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

    /** Programme for current label. */
    final protected TVProgramme programme;

    /** Parent controller. */
    final protected HorizontalViewer controller;

    /** Need to draw heart. */
    protected boolean isDrawHeart;

    /** Cached tooltip text. */
    private String tooltip;
    protected ProgrammeFormat htmlFormat;
    protected final boolean moveNames;

    /**
     * Creates a new JLabelProgramme object.
     *
     * @param programme DOCUMENT ME!
     * @param main DOCUMENT ME!
     * @param textFormat DOCUMENT ME!
     * @param htmlFormat DOCUMENT ME!
     * @param moveNames DOCUMENT ME!
     */
    public JLabelProgramme( 
        final TVProgramme programme, final HorizontalViewer main,
        final ProgrammeFormat textFormat, final ProgrammeFormat htmlFormat,
        final boolean moveNames )
    {
        super( textFormat.formatForMainGuide( programme ) );
        this.htmlFormat = htmlFormat;
        this.programme = programme;
        this.controller = main;
        this.moveNames = moveNames;
        setupColors(  );
        setupHeart(  );
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
    {

        if( ( REMINDER != null ) && REMINDER.isSelected( programme ) )
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

        DEFAULT_BORDER =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorNonTicked, 2 ) );
        MOVIE_BORDER =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorMovie, 2 ) );
        INGUIDE_BORDER =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK, 1 ),
                BorderFactory.createLineBorder( main.config.colorTicked, 2 ) );
        FOCUSED_BORDER =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLUE, 2 ),
                BorderFactory.createLineBorder( main.config.colorNonTicked, 1 ) );
    }

    /**
     * Setup need to draw heart for current label.
     */
    protected void setupHeart(  )
    {

        if( REMINDER == null )
        {
            isDrawHeart = false;
        }
        else
        {
            isDrawHeart =
                ( (BaseModuleReminder)REMINDER ).getFavourite( programme ) != null;
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

        if( !isDrawHeart )
        {

            return;
        }

        Graphics2D graphics = (Graphics2D)g;
        AffineTransform originalTransform = graphics.getTransform(  );

        graphics.setColor( Color.RED );

        // switch on anti-aliasing
        graphics.setRenderingHint( 
            RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );

        // Scale and position appropriately--taking into account the borders
        Rectangle bounds = HEART_SHAPE.getBounds(  );

        double scale = 0.45 * ( getHeight(  ) / bounds.getHeight(  ) );

        double right = getWidth(  ) - 2 - ( scale * bounds.getWidth(  ) );

        graphics.translate( right, 2 );

        graphics.scale( scale, scale );

        graphics.fill( HEART_SHAPE );

        graphics.setTransform( originalTransform );
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
}
