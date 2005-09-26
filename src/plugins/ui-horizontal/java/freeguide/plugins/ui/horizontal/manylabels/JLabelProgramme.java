package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.BaseModuleReminder;
import freeguide.plugins.IModuleReminder;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
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

    /**
     * Creates a new JLabelProgramme object.
     *
     * @param programme DOCUMENT ME!
     * @param main DOCUMENT ME!
     * @param startDate DOCUMENT ME!
     * @param row DOCUMENT ME!
     * @param font DOCUMENT ME!
     * @param textFormat DOCUMENT ME!
     */
    public JLabelProgramme( 
        final TVProgramme programme, final HorizontalViewer main,
        final long startDate, final int row, final Font font,
        final ProgrammeFormat textFormat )
    {
        super( textFormat.formatForMainGuide( programme ) );
        this.programme = programme;
        this.controller = main;
        setupBounds( row, startDate );
        setFont( font );
        setupColors(  );
        setupHeart(  );
        setOpaque( true );
        setFocusable( true );
        addMouseListener( main.handlers.labelProgrammeMouseListener );
        addFocusListener( main.handlers.labelProgrammeFocusListener );
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

        if( REMINDER != null )
        {

            if( REMINDER.isSelected( programme ) )
            {
                setBackground( controller.config.colorTicked );
                setBorder( INGUIDE_BORDER );

                return;
            }
        }

        if( !programme.getIsMovie(  ) )
        {
            setBackground( controller.config.colorNonTicked );
            setBorder( DEFAULT_BORDER );
        }
        else
        {
            setBackground( controller.config.colorMovie );
            setBorder( MOVIE_BORDER );
        }
    }

    /**
     * Set size and position.
     *
     * @param row label row
     * @param startDate width
     */
    protected void setupBounds( final int row, final long startDate )
    {

        //int x = (int)( ( ( programme.getStart(  ) - startDate ) * main.config.sizeProgrammePanelWidth ) / main.MILLISECONDS_PER_DAY ) + 1;
        //int y = ( main.config.sizeChannelHeight * row ) + 1;
        //int height = main.config.sizeChannelHeight - 2;
        //int width =(int)( ( ( programme.getEnd(  ) - programme.getStart(  ) ) * main.config.sizeProgrammePanelWidth ) / main.MILLISECONDS_PER_DAY ) - 2;
        int x =
            ( ( controller.config.sizeHalfHorGap * 2 )
            + (int)( ( ( programme.getStart(  ) - startDate ) * controller.config.sizeProgrammePanelWidth ) / controller.MILLISECONDS_PER_DAY ) )
            - 1;
        int y =
            ( ( controller.config.sizeHalfVerGap * 2 )
            + ( row * controller.config.sizeChannelHeight ) ) - 1;
        int height =
            controller.config.sizeChannelHeight
            - ( controller.config.sizeHalfVerGap * 4 );
        int width =
            (int)( ( ( programme.getEnd(  ) - programme.getStart(  ) ) * controller.config.sizeProgrammePanelWidth ) / controller.MILLISECONDS_PER_DAY )
            - ( controller.config.sizeHalfVerGap * 4 );

        setBounds( x, y, width, height );
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
                BorderFactory.createLineBorder( Color.BLACK ),
                BorderFactory.createLineBorder( main.config.colorNonTicked, 2 ) );
        MOVIE_BORDER =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK ),
                BorderFactory.createLineBorder( main.config.colorMovie, 2 ) );
        INGUIDE_BORDER =
            BorderFactory.createCompoundBorder( 
                BorderFactory.createLineBorder( Color.BLACK ),
                BorderFactory.createLineBorder( main.config.colorTicked, 2 ) );
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
        super.paintComponent( g );

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
}
