package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.lib.fgspecific.ProgrammeFormat;
import freeguide.lib.fgspecific.data.TVProgramme;

import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JViewport;

/**
 * Panel implementation for JLabelProgrammes with support of focus movement.
 */
public class JPanelProgramme extends JPanel
{

    protected final HorizontalViewer controller;
    protected long startDate;
    protected ProgrammeFormat textFormat;
    protected List[] rows = new List[0];

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
     * @param textFormat DOCUMENT_ME!
     * @param font DOCUMENT_ME!
     * @param rowCount DOCUMENT_ME!
     */
    public void init( 
        final long theDate, final ProgrammeFormat textFormat, final Font font,
        final int rowCount )
    {
        removeAll(  );
        this.startDate = theDate;
        this.textFormat = textFormat;
        setFont( font );
        rows = new List[rowCount];

        for( int i = 0; i < rows.length; i++ )
        {
            rows[i] = new ArrayList(  );
        }
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
            new JLabelProgramme( programme, controller, textFormat );
        setupBounds( label, programme, row );
        add( label );

        rows[row].add( label );
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

        label.setBounds( x, y, width, height );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param label DOCUMENT_ME!
     */
    public void focusMoveUp( final JLabelProgramme label )
    {

        int row = getRowOfLabel( label );

        if( row < 0 )
        {

            return;
        }

        for( int i = row - 1; i >= 0; i-- )
        {

            JLabelProgramme newLabel =
                getNearestFor( i, label.getProgramme(  ).getStart(  ) );

            if( newLabel != null )
            {
                focusAndShow( newLabel );

                break;
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param label DOCUMENT_ME!
     */
    public void focusMoveDown( final JLabelProgramme label )
    {

        int row = getRowOfLabel( label );

        if( row < 0 )
        {

            return;
        }

        for( int i = row + 1; i < rows.length; i++ )
        {

            JLabelProgramme newLabel =
                getNearestFor( i, label.getProgramme(  ).getStart(  ) );

            if( newLabel != null )
            {
                focusAndShow( newLabel );

                break;
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param label DOCUMENT_ME!
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
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param label DOCUMENT_ME!
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
        }
    }

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

    protected JLabelProgramme getNearestFor( 
        final int row, final long startTime )
    {

        JLabelProgramme nearest = null;

        for( int i = 0; i < rows[row].size(  ); i++ )
        {

            JLabelProgramme current = (JLabelProgramme)rows[row].get( i );

            if( nearest == null )
            {
                nearest = current;
            }
            else
            {

                if( 
                    Math.abs( 
                            startTime - current.getProgramme(  ).getStart(  ) ) < ( startTime
                        - nearest.getProgramme(  ).getStart(  ) ) )
                {
                    nearest = current;
                }
            }
        }

        return nearest;
    }

    protected void focusAndShow( final JLabelProgramme label )
    {
        label.requestFocus(  );

        JViewport vp = (JViewport)getParent(  );
        Rectangle r2 = label.getBounds(  );
        Point origin = vp.getViewPosition(  );
        r2.translate( -origin.x, -origin.y );
        vp.scrollRectToVisible( r2 );
    }
}
