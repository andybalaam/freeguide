package freeguide.plugins.ui.vertical.simple.filter.gui;

import freeguide.plugins.ui.vertical.simple.VerticalViewer;
import freeguide.plugins.ui.vertical.simple.filter.ProgrammeFilter;
import freeguide.plugins.ui.vertical.simple.filter.TimeFilter;
import freeguide.plugins.ui.vertical.simple.filter.gui.helper.CompTitledPane;
import freeguide.plugins.ui.vertical.simple.filter.gui.helper.SettingDialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.*;

/**
 * Dialog in which the user can configure a TimeFilter. The layout has
 * been copied shamelessly from nextVEPG.
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class TimeDialog extends JDialog implements AdjustmentListener,
    ActionListener, SettingDialog
{
    /** The filter to modify */
    TimeFilter timeFilter;

    /** GUI elements */
    protected JLabel lblStartValue;
    protected JLabel lblEndValue;
    protected JScrollBar scrStart;
    protected JScrollBar scrEnd;
    protected JCheckBox chkUseStart;
    protected JCheckBox chkUseEnd;
    protected JButton btnOk;
    protected JButton btnCancel;
    protected boolean bClosedWithOk = false;

/**
     * Creates a new TimeDialog object.
     *
     * @throws HeadlessException DOCUMENT ME!
     */
    public TimeDialog(  ) throws HeadlessException
    {
        super(  );
    }

    //public TimeDialog() throws HeadlessException
    /**
     * DOCUMENT_ME!
     *
     * @param filter DOCUMENT_ME!
     */
    public void setFilter( ProgrammeFilter filter )
    {
        this.timeFilter = (TimeFilter)filter;
    }

    //public void setFilter(ProgrammeFilter filter)
    /**
     * DOCUMENT_ME!
     */
    public void init(  )
    {
        this.setModal( true );
        this.buildLayout(  );
        this.loadFilterSettings(  );
    }

    //public void init()
    /**
     * Create the GUI elements
     */
    protected void buildLayout(  )
    {
        this.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
        this.getContentPane(  ).setLayout( new BorderLayout(  ) );
        this.setTitle( 
            VerticalViewer.getInstance(  )
                          .getLocalizedMessage( "timedialog.title" ) );
        this.setSize( 400, 200 );

        /** Buttons */
        JPanel pnlButtons = new JPanel( new BorderLayout(  ) );
        this.btnOk = new JButton( 
                VerticalViewer.getInstance(  )
                              .getLocalizedMessage( "timedialog.ok" ) );
        this.btnOk.setDefaultCapable( true );
        this.btnOk.addActionListener( this );
        this.btnCancel = new JButton( 
                VerticalViewer.getInstance(  )
                              .getLocalizedMessage( "timedialog.cancel" ) );
        this.btnCancel.addActionListener( this );

        pnlButtons.add( btnCancel, BorderLayout.WEST );
        pnlButtons.add( btnOk, BorderLayout.EAST );
        pnlButtons.setBorder( BorderFactory.createEmptyBorder( 10, 1, 1, 1 ) );

        this.getContentPane(  ).add( pnlButtons, BorderLayout.SOUTH );

        /** Controls */
        JPanel contentPane;

        this.chkUseStart = new JCheckBox( 
                VerticalViewer.getInstance(  )
                              .getLocalizedMessage( "timedialog.check.from" ) );

        CompTitledPane pnlFrom = new CompTitledPane( chkUseStart );
        this.lblStartValue = new JLabel( "00:00" );
        this.scrStart = new JScrollBar( JScrollBar.HORIZONTAL, 0, 0, 0, 287 );
        this.scrStart.addAdjustmentListener( this );
        this.scrStart.setBlockIncrement( 12 );
        contentPane = pnlFrom.getContentPane(  );
        contentPane.setLayout( new BorderLayout(  ) );
        contentPane.add( this.lblStartValue, BorderLayout.WEST );
        contentPane.add( this.scrStart, BorderLayout.CENTER );

        this.chkUseEnd = new JCheckBox( 
                VerticalViewer.getInstance(  )
                              .getLocalizedMessage( "timedialog.check.to" ) );

        CompTitledPane pnlTo = new CompTitledPane( this.chkUseEnd );
        this.lblEndValue = new JLabel( "00:00" );
        this.scrEnd = new JScrollBar( JScrollBar.HORIZONTAL, 0, 0, 0, 287 );
        this.scrEnd.setBlockIncrement( 12 );
        this.scrEnd.addAdjustmentListener( this );
        contentPane = pnlTo.getContentPane(  );
        contentPane.setLayout( new BorderLayout(  ) );
        contentPane.add( this.lblEndValue, BorderLayout.WEST );
        contentPane.add( this.scrEnd, BorderLayout.CENTER );

        Box pnlSettings = Box.createVerticalBox(  );
        pnlSettings.add( pnlFrom );
        pnlSettings.add( pnlTo );

        this.getContentPane(  ).add( pnlSettings, BorderLayout.CENTER );
    }

    //protected void buildLayout()
    /**
     * Loads the settings that the filter already has into the GUI
     */
    protected void loadFilterSettings(  )
    {
        int nStart = this.timeFilter.getStartTime(  );

        if( nStart != -1 )
        {
            this.chkUseStart.setSelected( true );
            this.scrStart.setValue( getScrollIntFromTimeInt( nStart ) );
        }
        else
        {
            this.chkUseStart.setSelected( false );
        }

        int nEnd = this.timeFilter.getEndTime(  );

        if( nEnd != -1 )
        {
            this.chkUseEnd.setSelected( true );
            this.scrEnd.setValue( getScrollIntFromTimeInt( nEnd ) );
        }
        else
        {
            this.chkUseEnd.setSelected( false );
        }
    }

    //protected void loadFilterSettings()
    /**
     * A scrollbar value has been changed
     *
     * @param adjustmentEvent DOCUMENT ME!
     */
    public void adjustmentValueChanged( AdjustmentEvent adjustmentEvent )
    {
        if( adjustmentEvent.getSource(  ) == this.scrStart )
        {
            int nStart = this.scrStart.getValue(  );
            this.lblStartValue.setText( 
                TimeFilter.getTimeFromInt( getTimeIntFromScrollInt( nStart ) ) );
            this.chkUseStart.setSelected( true );

            if( 
                this.chkUseEnd.isSelected(  )
                    && ( nStart > this.scrEnd.getValue(  ) ) )
            {
                this.scrEnd.setValue( nStart );
            }
        }
        else
        {
            int nEnd = this.scrEnd.getValue(  );
            this.lblEndValue.setText( 
                TimeFilter.getTimeFromInt( getTimeIntFromScrollInt( nEnd ) ) );
            this.chkUseEnd.setSelected( true );

            if( 
                this.chkUseStart.isSelected(  )
                    && ( nEnd < this.scrStart.getValue(  ) ) )
            {
                this.scrStart.setValue( nEnd );
            }
        }
    }

    //public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent)
    /**
     * Converts a scroll bar integer to a "time integer" Scrollbars
     * use a step of 1 to increment 5 minutes, and this methods converts from
     * the scrollbar value to a TimeFilter value of e.g. "2015" for "20:15"
     *
     * @param nScroll DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    protected static int getTimeIntFromScrollInt( int nScroll )
    {
        int nMinutes = nScroll % 12;

        return ( ( nScroll - nMinutes ) / 12 * 100 ) + ( nMinutes * 5 );
    }

    //protected static int getTimeIntFromScrollInt(int nScroll)
    /**
     * Reverse function to getTimeIntFromScrollInt(int nScroll)
     *
     * @param nTime DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    protected static int getScrollIntFromTimeInt( int nTime )
    {
        int nMinutes = nTime % 100;

        return ( ( nTime - nMinutes ) / 100 * 12 ) + ( nMinutes / 5 );
    }

    //protected static int getScrollIntFromTimeInt(int nTime)
    /**
     * A button has been clicked
     *
     * @param actionEvent DOCUMENT ME!
     */
    public void actionPerformed( ActionEvent actionEvent )
    {
        if( actionEvent.getSource(  ) == this.btnOk )
        {
            //OK - apply the settings to the filter
            if( this.chkUseStart.isSelected(  ) )
            {
                this.timeFilter.setStartTime( 
                    getTimeIntFromScrollInt( this.scrStart.getValue(  ) ) );
            }
            else
            {
                this.timeFilter.setStartTime( -1, false );
            }

            if( this.chkUseEnd.isSelected(  ) )
            {
                this.timeFilter.setEndTime( 
                    getTimeIntFromScrollInt( this.scrEnd.getValue(  ) ) );
            }
            else
            {
                this.timeFilter.setEndTime( -1, false );
            }

            this.timeFilter.notifyFilterChange(  );
            this.bClosedWithOk = true;
            this.dispose(  );
        }
        else
        {
            //Should be Cancel button
            this.dispose(  );
        }
    }

    //public void actionPerformed(ActionEvent actionEvent)
    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isClosedWithOk(  )
    {
        return bClosedWithOk;
    }

    //public boolean isClosedWithOk()
    /**
     * DOCUMENT_ME!
     *
     * @param show DOCUMENT ME!
     */
    public void setVisible( boolean show )
    {
        if( show )
        {
            //reset that
            this.bClosedWithOk = false;
        }

        super.setVisible( show );
    }

    /**
     * To test the dialog in development
     *
     * @param args DOCUMENT ME!
     */
    public static void main( String[] args )
    {
        TimeFilter filter = new TimeFilter(  );
        filter.setStartTime( 2015 );
        filter.setEndTime( -1 );

        TimeDialog dialog = new TimeDialog(  );
        dialog.setFilter( filter );
        dialog.init(  );
        dialog.setVisible( true );
        System.exit( 0 );
    }

    //public static void main(String[] args)
}
//public class TimeDialog extends JDialog
