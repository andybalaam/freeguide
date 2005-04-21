/*

 *  FreeGuide J2

 *

 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors

 *

 *  freeguide-tv.sourceforge.net

 *

 *  Released under the GNU General Public License

 *  with ABSOLUTELY NO WARRANTY.

 *

 *  See the file COPYING for more information.

 */
package freeguide.lib.fgspecific.selection;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.Time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

/**
 * FreeGuideFavourite A description of a favourite TV program, vague or
 * specific.
 *
 * @author Andy Balaam
 * @version 3
 */
public class Favourite
{

    // ------------------------------------------------------------------------

    /** DOCUMENT ME! */
    public String name;

    // The user-specified name of this favourite

    /** DOCUMENT ME! */
    public String titleString;

    // Exact match for the title

    /** DOCUMENT ME! */
    public String titleContains;

    // Exact match for the title

    /** DOCUMENT ME! */
    public String titleRegex;
    transient protected Pattern titleRegexPattern;

    // Regular expression to match the title

    /** DOCUMENT ME! */
    public String channelID;

    // The channel it must be on

    /** DOCUMENT ME! */
    public Time afterTime = new Time(  );

    // The time it must be on after

    /** DOCUMENT ME! */
    public Time beforeTime = new Time(  );

    // Day of week, or -1 if any.

    /** DOCUMENT ME! */
    public int dayOfWeek = -1;

    /**
     * Constructor for the Favourite object
     */
    public Favourite(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object clone(  )
    {

        final Favourite result = new Favourite(  );

        result.name = name;

        result.titleString = titleString;

        result.titleContains = titleContains;

        result.titleRegex = titleRegex;

        result.channelID = channelID;

        result.afterTime = afterTime;

        result.beforeTime = beforeTime;

        return result;

    }

    protected Pattern getTitleRegexPattern(  )
    {

        if( ( titleRegexPattern == null ) && ( titleRegex != null ) )
        {
            titleRegexPattern = Pattern.compile( titleRegex );

        }

        return titleRegexPattern;

    }

    // ------------------------------------------------------------------------

    /**
     * matches Decides whether or not a programme matches this favourite.
     *
     * @param prog the programme to check
     *
     * @return true if this programme matches this favourite
     */
    public boolean matches( TVProgramme prog )
    {

        String progTitle = prog.getTitle(  );

        // Match the title exactly
        if( ( titleString != null ) && !titleString.equals( progTitle ) )
        {

            return false;

        }

        // Match the title to containing a string
        if( 
            ( titleContains != null )
                && ( progTitle.indexOf( titleContains ) == -1 ) )
        {

            return false;

        }

        // Match the title to a regular expression
        if( 
            ( titleRegex != null )
                && !getTitleRegexPattern(  ).matcher( progTitle ).matches(  ) )
        {

            return false;

        }

        // Match the channel ID
        if( 
            ( channelID != null )
                && !channelID.equals( prog.getChannel(  ).getID(  ) ) )
        {

            return false;

        }

        Time progStartTime = new Time( new Date( prog.getStart(  ) ) );

        // Match the time it must be after
        if( !afterTime.isEmpty(  ) && afterTime.after( progStartTime ) )
        {

            return false;

        }

        // Match the time it must be before
        if( !beforeTime.isEmpty(  ) && beforeTime.before( progStartTime ) )
        {

            return false;

        }

        Calendar cal = GregorianCalendar.getInstance(  );

        cal.setTimeZone( FreeGuide.getTimeZone(  ) );

        cal.setTimeInMillis( prog.getStart(  ) );

        // Match the day of the week
        if( 
            ( dayOfWeek != -1 )
                && ( dayOfWeek != cal.get( Calendar.DAY_OF_WEEK ) ) )
        {

            return false;

        }

        return true;

    }

    // ------------------------------------------------------------------------
    // Accessors

    /**
     * Gets the name attribute of the Favourite object
     *
     * @return The name value
     */
    public String getName(  )
    {

        return name;

    }

    /**
     * Gets the titleString attribute of the Favourite object
     *
     * @return The titleString value
     */
    public String getTitleString(  )
    {

        return titleString;

    }

    /**
     * Gets the titleContains attribute of the Favourite object
     *
     * @return The titleContains value
     */
    public String getTitleContains(  )
    {

        return titleContains;

    }

    /**
     * Gets the titleRegex attribute of the Favourite object
     *
     * @return The titleRegex value
     */
    public String getTitleRegex(  )
    {

        return titleRegex;

    }

    /**
     * Gets the channel attribute of the Favourite object
     *
     * @return The channel value
     */
    public String getChannelID(  )
    {

        return channelID;

    }

    /**
     * Gets the afterTime attribute of the Favourite object
     *
     * @return The afterTime value
     */
    public Time getAfterTime(  )
    {

        return afterTime;

    }

    /**
     * Gets the beforeTime attribute of the Favourite object
     *
     * @return The beforeTime value
     */
    public Time getBeforeTime(  )
    {

        return beforeTime;

    }

    /**
     * Gets the dayOfWeek attribute of the Favourite object
     *
     * @return The dayOfWeek value
     */
    public int getDayOfWeek(  )
    {

        return dayOfWeek;

    }

    /**
     * Sets the name attribute of the Favourite object
     *
     * @param name The new name value
     */
    public void setName( String name )
    {
        this.name = name;

    }

    /**
     * Sets the titleString attribute of the Favourite object
     *
     * @param titleString The new titleString value
     */
    public void setTitleString( String titleString )
    {
        this.titleString = titleString;

    }

    /**
     * Sets the titleContains attribute of the Favourite object
     *
     * @param titleContains The new titleContains value
     */
    public void setTitleContains( String titleContains )
    {
        this.titleContains = titleContains;

    }

    /**
     * Sets the titleRegex attribute of the Favourite object
     *
     * @param titleRegex The new titleRegex value
     */
    public void setTitleRegex( String titleRegex )
    {
        this.titleRegex = titleRegex;

        this.titleRegexPattern = null;

    }

    /**
     * Sets the channelID attribute of the Favourite object
     *
     * @param channelID The new channelID value
     */
    public void setChannelID( String channelID )
    {
        this.channelID = channelID;

    }

    /**
     * Sets the afterTime attribute of the Favourite object
     *
     * @param afterTime The new afterTime value
     */
    public void setAfterTime( Time afterTime )
    {
        this.afterTime = afterTime;

    }

    /**
     * Sets the beforeTime attribute of the Favourite object
     *
     * @param beforeTime The new beforeTime value
     */
    public void setBeforeTime( Time beforeTime )
    {
        this.beforeTime = beforeTime;

    }

    /**
     * Sets the dayOfWeek attribute of the Favourite object
     *
     * @param dayOfWeek The new dayOfWeek value
     */
    public void setDayOfWeek( int dayOfWeek )
    {
        this.dayOfWeek = dayOfWeek;

    }

    // The day of the week it's on
}
