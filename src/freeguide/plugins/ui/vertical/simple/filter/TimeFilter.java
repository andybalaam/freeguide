package freeguide.plugins.ui.vertical.simple.filter;

import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ui.vertical.simple.VerticalViewer;

import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * Shows only programs which start and end in a given timeframe
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class TimeFilter extends ProgrammeFilter
{

    /**
     * The time at which the program has to start (or later) -1 means that
     * this field is not used for filtering. If it's not -1, the meaning is
     * "HHmm" as integer (2-digit hour with 24 hour format)
     */
    protected int nStartTime = -1;

    /**
     * The time at which the program may start at last -1 deactivates that
     * field, normal format is "HHmm"
     */
    protected int nEndTime = -1;

    /** The date formatter */
    protected SimpleDateFormat time = new SimpleDateFormat( "HHmm" );

    /**
     * Decides if the given program shall be shown or gets filtered out.
     *
     * @param programme DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean showProgramme( TVProgramme programme )
    {

        //I think that here is too expensive, but tell me how to make it faster..
        int nTime =
            Integer.parseInt( 
                time.format( new Date( programme.getStart(  ) ) ) );

        return ( ( this.nStartTime == -1 ) || ( nTime >= this.nStartTime ) )
        && ( ( this.nEndTime == -1 ) || ( nTime <= this.nEndTime ) );
    }

    //public boolean showProgramme(TVProgramme programme)

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getStartTime(  )
    {

        return nStartTime;
    }

    //public int getStartTime()

    /**
     * DOCUMENT_ME!
     *
     * @param nStartTime DOCUMENT_ME!
     */
    public void setStartTime( int nStartTime )
    {
        setStartTime( nStartTime, true );
    }

    //public void setStartTime(int nStartTime)

    /**
     * DOCUMENT_ME!
     *
     * @param nStartTime DOCUMENT_ME!
     * @param bNotify DOCUMENT_ME!
     */
    public void setStartTime( int nStartTime, boolean bNotify )
    {
        this.nStartTime = nStartTime;

        if( bNotify )
        {
            this.notifyFilterChange(  );
        }
    }

    //public void setStartTime(int nStartTime, boolean bNotify)

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getEndTime(  )
    {

        return nEndTime;
    }

    //public int getEndTime()

    /**
     * DOCUMENT_ME!
     *
     * @param nEndTime DOCUMENT_ME!
     */
    public void setEndTime( int nEndTime )
    {
        setEndTime( nEndTime, true );
    }

    //public void setEndTime(int nEndTime)

    /**
     * DOCUMENT_ME!
     *
     * @param nEndTime DOCUMENT_ME!
     * @param bNotify DOCUMENT_ME!
     */
    public void setEndTime( int nEndTime, boolean bNotify )
    {
        this.nEndTime = nEndTime;

        if( bNotify )
        {
            this.notifyFilterChange(  );
        }
    }

    //public void setEndTime(int nEndTime, boolean bNotify)

    /**
     * sets the title dependent on the start and ending time
     */
    protected void setTitle(  )
    {

        if( ( this.nStartTime == -1 ) && ( this.nEndTime == -1 ) )
        {
            this.setTitle( 
                VerticalViewer.getInstance(  ).getLocalizer(  )
                              .getLocalizedMessage( "timefilter.anytime" ) );
        }
        else
        {

            StringBuffer strTitle = new StringBuffer( 20 );
            strTitle.append( 
                VerticalViewer.getInstance(  ).getLocalizedMessage( 
                    "timefilter.begins" ) ).append( " " );

            if( this.nStartTime != -1 )
            {
                strTitle.append( getTimeFromInt( this.nStartTime ) );

                if( this.nEndTime != -1 )
                {
                    strTitle.append( " - " ).append( 
                        getTimeFromInt( this.nEndTime ) );
                }
            }
            else
            {
                strTitle.append( 
                    VerticalViewer.getInstance(  ).getLocalizedMessage( 
                        "timefilter.until" ) ).append( " " ).append( 
                    getTimeFromInt( this.nEndTime ) );
            }

            this.setTitle( strTitle.toString(  ) );
        }
    }

    //protected void setTitle()

    /**
     * Make a time string "HH:mm" out of the TimeFilter time integer
     *
     * @param nTime DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public static String getTimeFromInt( int nTime )
    {

        StringBuffer strTime = new StringBuffer( 5 );
        strTime.append( nTime );

        int length = strTime.length(  );

        for( int nA = 0; nA < ( 4 - length ); nA++ )
        {
            strTime.insert( 0, '0' );
        }

        strTime.insert( 2, ':' );

        return strTime.toString(  );
    }

    //public static String getTimeFromInt(int nTime)

    /**
     * Our own notification method because we have to set the title.
     */
    public void notifyFilterChange(  )
    {
        this.setTitle(  );
        super.notifyFilterChange(  );
    }

    //public void notifyFilterChange()

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String exportSettings(  )
    {

        return "" + ( ( this.nStartTime * 10000 ) + this.nEndTime );
    }

    //public String exportSettings()

    /**
     * DOCUMENT_ME!
     *
     * @param strValue DOCUMENT_ME!
     */
    public void importSettings( String strValue )
    {

        int nValue = Integer.parseInt( strValue );
        this.nEndTime = nValue % 10000;
        this.nStartTime = ( nValue - this.nEndTime ) / 10000;

        this.notifyFilterChange(  );
    }

    //public void importSettings(String strValue)

    /**
     * DOCUMENT_ME!
     */
    public void deactivate(  )
    {
        this.nStartTime = -1;
        this.nEndTime = -1;
        this.notifyFilterChange(  );
    }

    //public void deactivate()

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isDeactivated(  )
    {

        return ( this.nStartTime == -1 ) && ( this.nEndTime == -1 );
    }

    //public boolean isDeactivated()
}


//public class TimeFilter extends ProgrammeFilter
