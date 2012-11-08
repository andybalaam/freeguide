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
package freeguide.common.lib.fgspecific.selection;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.Time;

import java.text.Collator;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

import java.util.Map;
import java.util.Collection;
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
    /** The user-specified name of this favourite */
    public String name;

    /** Exact match for the title */
    public String titleString;

    /** String contained in the title */
    public String titleContains;

    /** Regular expression to match the title */
    public String titleRegex;
    transient protected Pattern titleRegexPattern;

    /** String contained in the description or in the extraTags */
    public String descriptionContains;

    /** The channel it must be on */
    public String channelID;

    /** The time it must be on after */
    public Time afterTime = new Time(  );

    /** The time it must be on before */
    public Time beforeTime = new Time(  );

    /** Day of week, or -1 if any. */
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
        result.descriptionContains = descriptionContains;
        result.channelID = channelID;
        result.afterTime = afterTime;
        result.beforeTime = beforeTime;
        result.dayOfWeek = dayOfWeek;

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
     * matches Decides whether or not a programme matches this
     * favourite.
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

        // Matches if the description or anything else in extraTags (like the
        // actor, producer, writer, or episode number) contains the string.
        if( !descriptionOrTagsMatch( prog ) )
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
        cal.setTimeZone( Application.getInstance(  ).getTimeZone(  ) );
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

    public String getDescriptionContains(  )
    {
        return descriptionContains;
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

    public void setDescriptionContains( String descriptionContains )
    {
        this.descriptionContains = descriptionContains;
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

    public static class FavouriteComparator implements Comparator
    {
        public int compare( Object o1, Object o2 )
        {
           Favourite f1 = (Favourite)o1;
           Favourite f2 = (Favourite)o2;

           return Collator.getInstance( Locale.getDefault() ).compare(
               f1.getName(), f2.getName() );
        }
    }

    public static Comparator GetNameComparator()
    {
        return new FavouriteComparator();
    }
    // The day of the week it's on


    /**
     * descriptionOrTagsMatch decides if a description (ultimately entered by
     * the user) is contained in a program's Description or ExtraTags.
     *
     * @param prog the programme whose description we will check
     *
     * @return true if this programme matches this favourite's description
     *         field, or the descriptionContains field is empty.
     */
    public boolean descriptionOrTagsMatch( TVProgramme prog )
    {
        if( descriptionContains == null )
        {
            // If there is no requirement for the description to match
            // anything, we "match".
            return true;
        }

        String progDescription = prog.getDescription(  );
        Map progExtraTags = prog.getExtraTags(  );

        if(
            progDescription != null &&
            progDescription.contains( descriptionContains )
        )
        {
            return true;
        }

        if(
            progExtraTags != null &&
            matchInExtraTags( progExtraTags.values(  ), descriptionContains )
        )
        {
            return true;
        }

        return false;
    }

    /**
     * Searches for the supplied description in the extra tags map of a
     * programme.
     *
     * @param tags the programme to check
     * @param description the description to match
     *
     * @return true if the supplied description appears in one of the tags
     *         in the map supplied.  False otherwise.
     */
    public boolean matchInExtraTags( Collection tags, String description )
    {
        for( Object tag : tags )
        {
            Map hashOfAttrs = (Map)tag;
            for( Object attrValue : hashOfAttrs.values( ) )
            {
                String s = (String)attrValue;
                if( s.indexOf( description ) != -1 )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
