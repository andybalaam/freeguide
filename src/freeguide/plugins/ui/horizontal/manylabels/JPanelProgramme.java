package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.Time;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import java.text.DateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JViewport;

/**
 * Panel implementation for JLabelProgrammes with support of focus
 * movement.
 */
public class JPanelProgramme extends JPanel
{
    protected final HorizontalViewer controller;
    protected long startDate;
    protected List[] rows = new List[0];
    protected Map labelsForProgrammes = new HashMap(  );
    protected DateFormat timeFormat;
    private long cursorHorizontalPos = -1;

    /**
     * Creates a new JPanelProgramme object.
     *
     * @param controller DOCUMENT ME!
     */
    public JPanelProgramme( final HorizontalViewer controller )
    {
        super( null );
        this.controller = controller;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param theDate DOCUMENT_ME!
     * @param font DOCUMENT_ME!
     * @param rowCount DOCUMENT_ME!
     * @param timeFormat DOCUMENT_ME!
     */
    public void init(
        final long theDate, final Font font, final int rowCount,
        final DateFormat timeFormat )
    {
        removeAll(  );
        labelsForProgrammes.clear(  );
        this.startDate = theDate;
        this.timeFormat = timeFormat;
        setFont( font );
        rows = new List[rowCount];

        for( int i = 0; i < rows.length; i++ )
        {
            rows[i] = new ArrayList(  );
        }
    }

    protected JLabelProgramme getLabelForProgramme(
        final TVProgramme programme )
    {
        return (JLabelProgramme)labelsForProgrammes.get( programme );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     * @param row DOCUMENT_ME!
     */
    public void addProgramme( final TVProgramme programme, final int row )
    {
        JLabelProgramme label =
            new JLabelProgramme(
                programme, controller, controller.config.displayAlignToLeft,
                timeFormat );
        setupBounds( label, programme, row );
        add( label );
        label.setFont( getFont(  ) );

        rows[row].add( label );
        labelsForProgrammes.put( programme, label );
    }

    /**
     * DOCUMENT_ME!
     */
    public void sort(  )
    {
        for( int i = 0; i < rows.length; i++ )
        {
            Collections.sort(
                rows[i],
                new Comparator(  )
                {
                    public int compare( Object arg0, Object arg1 )
                    {
                        JLabelProgramme lab0 = (JLabelProgramme)arg0;
                        JLabelProgramme lab1 = (JLabelProgramme)arg1;

                        return (int)( lab0.getProgramme(  ).getStart(  )
                        - lab1.getProgramme(  ).getStart(  ) );
                    }
                } );
        }
    }

    /**
     * Set size and position.
     *
     * @param label width
     * @param programme DOCUMENT ME!
     * @param row label row
     */
    protected void setupBounds(
        final JLabelProgramme label, final TVProgramme programme, final int row )
    {
        int    sizePanel = (int) (controller.config.sizeProgrammeHour * controller.todayMillis / Time.HOUR);
        double sizeMilli = (double) (controller.config.sizeProgrammeHour) / Time.HOUR;

        int x =
            ( ( controller.config.sizeHalfHorGap * 2 )
            + (int) ( ( programme.getStart(  ) - startDate ) * sizeMilli ) )
            - 1;
        int y =
            ( ( controller.config.sizeHalfVerGap * 2 )
            + ( row * controller.config.sizeChannelHeight ) ) - 1;
        int height =
            controller.config.sizeChannelHeight
            - ( controller.config.sizeHalfVerGap * 4 );
        int width =
            (int) ( ( programme.getEnd(  ) - programme.getStart(  ) ) * sizeMilli )
            - ( controller.config.sizeHalfHorGap * 4 );

        if( x < 0 )
        {
            // trunc for window if programme starts before current day
            int dt = -x;
            x += dt;
            width -= dt;
        }

        if( x > sizePanel )
        {
            // trunc for window if programme ends after current day
            int dt = x - sizePanel;
            width -= dt;
        }

        label.setBounds( x, y, width, height );
    }

    /**
     * Up button event.
     *
     * @param label current label
     */
    public void focusMoveUp( final JLabelProgramme label )
    {
        int row = getRowOfLabel( label );

        if( row < 0 )
        {
            return;
        }

        if( cursorHorizontalPos == -1 )
        {
            cursorHorizontalPos = label.getMiddle(
                    startDate, startDate + controller.todayMillis );
        }

        for( int i = row - 1; i >= 0; i-- )
        {
            JLabelProgramme newLabel = getNearestFor( i, cursorHorizontalPos );

            if( newLabel != null )
            {
                focusAndShowPartly( newLabel );

                break;
            }
        }
    }

    /**
     * Down button event.
     *
     * @param label current label
     */
    public void focusMoveDown( final JLabelProgramme label )
    {
        int row = getRowOfLabel( label );

        if( row < 0 )
        {
            return;
        }

        if( cursorHorizontalPos == -1 )
        {
            cursorHorizontalPos = label.getMiddle(
                    startDate, startDate + controller.todayMillis );
        }

        for( int i = row + 1; i < rows.length; i++ )
        {
            JLabelProgramme newLabel = getNearestFor( i, cursorHorizontalPos );

            if( newLabel != null )
            {
                focusAndShowPartly( newLabel );

                break;
            }
        }
    }

    /**
     * Left button event.
     *
     * @param label current label
     */
    public void focusMoveLeft( final JLabelProgramme label )
    {
        int row = getRowOfLabel( label );

        if( row < 0 )
        {
            return;
        }

        int pos = rows[row].indexOf( label );

        if( pos > 0 )
        {
            JLabelProgramme newLabel =
                (JLabelProgramme)rows[row].get( pos - 1 );
            focusAndShow( newLabel );

            cursorHorizontalPos = newLabel.getMiddle(
                    startDate, startDate + controller.todayMillis );
        }
    }

    /**
     * Right button event.
     *
     * @param label current label
     */
    public void focusMoveRight( final JLabelProgramme label )
    {
        int row = getRowOfLabel( label );

        if( row < 0 )
        {
            return;
        }

        int pos = rows[row].indexOf( label );

        if( pos < ( rows[row].size(  ) - 1 ) )
        {
            JLabelProgramme newLabel =
                (JLabelProgramme)rows[row].get( pos + 1 );
            focusAndShow( newLabel );

            cursorHorizontalPos = newLabel.getMiddle(
                    startDate, startDate + controller.todayMillis );
        }
    }

    /**
     * Find label and return its row.
     *
     * @param label label
     *
     * @return row or -1 if label not found
     */
    protected int getRowOfLabel( final JLabelProgramme label )
    {
        for( int i = 0; i < rows.length; i++ )
        {
            if( rows[i].contains( label ) )
            {
                return i;
            }
        }

        return -1;
    }

    /**
     * Find nesrest programme for specified row and time.
     *
     * @param row row, i.e. channel
     * @param middleTime time to find
     *
     * @return found label, or null if there is no label in specified row
     */
    protected JLabelProgramme getNearestFor(
        final int row, final long middleTime )
    {
        for( int i = 0; i < rows[row].size(  ); i++ )
        {
            JLabelProgramme current = (JLabelProgramme)rows[row].get( i );

            if(
                current.isOverlap(
                        middleTime, startDate,
                        startDate + controller.todayMillis ) )
            {
                return current;
            }
        }

        return null;
    }

    /**
     * Focus specified label and scroll to it if need.
     *
     * @param label label
     */
    protected void focusAndShow( final JLabelProgramme label )
    {
        label.requestFocus(  );

        JViewport vp = (JViewport)getParent(  );
        Rectangle r2 = label.getBounds(  );
        Point origin = vp.getViewPosition(  );
        r2.translate( -origin.x, -origin.y );
        vp.scrollRectToVisible( r2 );
    }

    /**
     * Focus specified label and scroll to it to show only 50 pixels
     * horizontally.
     *
     * @param label label
     */
    protected void focusAndShowPartly( final JLabelProgramme label )
    {
        label.requestFocus(  );

        JViewport vp = (JViewport)getParent(  );
        Rectangle r2 = label.getBounds(  );
        Point origin = vp.getViewPosition(  );
        int dx = r2.width - 50;
        r2.width = 50;

        if( r2.x > origin.x )
        {
            // label on the right of visible window begin
        }
        else if( r2.x < origin.x )
        {
            // label on the left of visible window begin
            r2.x += dx;
        }

        r2.translate( -origin.x, -origin.y );
        vp.scrollRectToVisible( r2 );
    }
}
