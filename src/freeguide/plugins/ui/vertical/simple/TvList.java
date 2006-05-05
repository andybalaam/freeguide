package freeguide.plugins.ui.vertical.simple;

import freeguide.plugins.ui.vertical.simple.filter.*;
import freeguide.plugins.ui.vertical.simple.filter.gui.*;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

/**
 * DOCUMENT ME!
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class TvList extends JTable implements MouseListener
{
    TvTableModel model;
    ProgrammeFilterModel filterModel;
    ProgrammeCellRenderer cellRenderer = null;
    protected ChannelFilter channelFilter;
    protected ChannelMenu mnuChannels;
    protected GenericFilterMenu mnuTitle;
    protected GenericFilterMenu mnuTime;
    protected TitleFilter titleFilter;
    protected TimeFilter timeFilter;

/**
     * Creates a new TvList object.
     */
    public TvList(  )
    {
        super(  );
        this.model = new TvTableModel(  );
        this.filterModel = new ProgrammeFilterModel( this.model, this );
        this.setModel( this.filterModel );
        this.cellRenderer = new ProgrammeCellRenderer(  );
        this.setDefaultRenderer( Object.class, this.cellRenderer );

        //Channel Filter
        this.channelFilter = new ChannelFilter(  );
        this.filterModel.addFilter( this.channelFilter );
        this.mnuChannels = new ChannelMenu( this.channelFilter );

        //Title string search filter
        titleFilter = new TitleFilter(  );
        this.filterModel.addFilter( titleFilter );

        //Time filter
        timeFilter = new TimeFilter(  );
        this.filterModel.addFilter( timeFilter );

        this.getTableHeader(  ).addMouseListener( this );

        this.filterModel.applyFilter(  );
        this.setColumnWidths(  );
    }

    //public TvList()
    protected void setColumnWidths(  )
    {
        //TODO: save settings
        this.getColumnModel(  ).getColumn( 0 ).setPreferredWidth( 80 );
        this.getColumnModel(  ).getColumn( 1 ).setPreferredWidth( 100 );
        this.getColumnModel(  ).getColumn( 2 ).setPreferredWidth( 400 );
        this.getColumnModel(  ).getColumn( 3 ).setPreferredWidth( 80 );
    }

    //protected void setColumnWidths()
    /**
     * This method is called when the viewer has finished getting new
     * data. Then we can re-initialize channels and so.
     */
    public void postpare(  )
    {
        this.cellRenderer.init(  );
        this.mnuChannels.init(  );
    }

    //public void postpare()
    /**
     * The mouse has been clicked on a header
     *
     * @param mouseEvent DOCUMENT ME!
     */
    public void mouseClicked( MouseEvent mouseEvent )
    {
        int nColumn =
            this.getTableHeader(  ).getColumnModel(  )
                .getColumnIndexAtX( mouseEvent.getX(  ) );

        switch( nColumn )
        {
        case TvTableModel.COL_CHANNEL:
            this.mnuChannels.show( 
                mouseEvent.getComponent(  ), mouseEvent.getX(  ),
                mouseEvent.getY(  ) );

            break;

        case TvTableModel.COL_TITLE:

            if( this.mnuTitle == null )
            {
                this.mnuTitle = new GenericFilterMenu( 
                        this.titleFilter, TitleDialog.class );
            }

            this.mnuTitle.show( 
                mouseEvent.getComponent(  ), mouseEvent.getX(  ),
                mouseEvent.getY(  ) );

            break;

        case TvTableModel.COL_TIME:

            if( this.mnuTime == null )
            {
                this.mnuTime = new GenericFilterMenu( 
                        timeFilter, TimeDialog.class );
            }

            this.mnuTime.show( 
                mouseEvent.getComponent(  ), mouseEvent.getX(  ),
                mouseEvent.getY(  ) );

            break;
        }
    }

    //public void mouseClicked(MouseEvent mouseEvent)
    /**
     * DOCUMENT_ME!
     *
     * @param mouseEvent DOCUMENT_ME!
     */
    public void mouseEntered( MouseEvent mouseEvent )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param mouseEvent DOCUMENT_ME!
     */
    public void mouseExited( MouseEvent mouseEvent )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param mouseEvent DOCUMENT_ME!
     */
    public void mousePressed( MouseEvent mouseEvent )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param mouseEvent DOCUMENT_ME!
     */
    public void mouseReleased( MouseEvent mouseEvent )
    {
    }
}
//public class TvList extends JTable
