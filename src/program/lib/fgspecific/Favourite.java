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

package freeguidetv.lib.fgspecific;

import freeguidetv.lib.general.*;
import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 *  FreeGuideFavourite
 *
 *  A description of a favourite TV program, vague or specific.
 *
 *  @author  Andy Balaam
 *  @version 3
 */
/**
 *  Description of the Class
 *
 *@author     andy
 *@created    28 June 2003
 */
public class Favourite {

    /**
     *  Constructor for the Favourite object
     */
    public Favourite() { }


    // ------------------------------------------------------------------------

    /**
     *  matches Decides whether or not a programme matches this favourite.
     *
     *@param  prog  the programme to check
     *@return       Description of the Return Value
     *@returns      true if this programme matches this favourite
     */
    public boolean matches( Programme prog ) {

        String progTitle = prog.getTitle();

        // Match the title exactly
        if ((titleString != null) && !titleString.equals(progTitle)) {
            //FreeGuide.log.info(progTitle + " Title match failed");
            return false;
        }

        // Match the title to containing a string
        if ((titleContains != null) && (progTitle.indexOf(titleContains) == -1)) {
            //FreeGuide.log.info(progTitle + " Title contains failed");
            return false;
        }

        // Match the title to a regular expression
        if ((titleRegex != null) && !titleRegex.matcher(progTitle).matches()) {
            //FreeGuide.log.info(progTitle + " Title regex failed");
            return false;
        }

        // Match the channel ID
        if ((channelID != null) && !channelID.equals(prog.getChannelID())) {
            //FreeGuide.log.info(progTitle + " '" + channelID + "' '" + prog.getChannelID()+"'");
            return false;
        }
        Time progStartTime = new Time(prog.getStart());

        //FreeGuide.log.info(progTitle + " " + afterTime + " must be after " + progStartTime);

        // Match the time it must be after
        if (afterTime != null && afterTime.after(progStartTime)) {
            //FreeGuide.log.info(progTitle + " afterTime failed");
            return false;
        }

        // Match the time it must be before
        if (beforeTime != null && beforeTime.before(progStartTime)) {
            //FreeGuide.log.info(progTitle + " beforeTime failed");
            return false;
        }

        // Match the day of the week
        if (dayOfWeek != null && (dayOfWeek.intValue() != prog.getStart().get(Calendar.DAY_OF_WEEK))) {
            //FreeGuide.log.info(progTitle + " Day of week failed");
            return false;
        }

        //FreeGuide.log.info(progTitle + " Passed");
        // Passed all the tests!
        return true;
    }


    // ------------------------------------------------------------------------
    // Accessors

    /**
     *  Gets the name attribute of the Favourite object
     *
     *@return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the titleString attribute of the Favourite object
     *
     *@return    The titleString value
     */
    public String getTitleString() {
        return titleString;
    }


    /**
     *  Gets the titleContains attribute of the Favourite object
     *
     *@return    The titleContains value
     */
    public String getTitleContains() {
        return titleContains;
    }


    /**
     *  Gets the titleRegex attribute of the Favourite object
     *
     *@return    The titleRegex value
     */
    public Pattern getTitleRegex() {
        return titleRegex;
    }


    /**
     *  Gets the channelID attribute of the Favourite object
     *
     *@return    The channelID value
     */
    public String getChannelID() {
        return channelID;
    }


    /**
     *  Gets the afterTime attribute of the Favourite object
     *
     *@return    The afterTime value
     */
    public Time getAfterTime() {
        return afterTime;
    }


    /**
     *  Gets the beforeTime attribute of the Favourite object
     *
     *@return    The beforeTime value
     */
    public Time getBeforeTime() {
        return beforeTime;
    }


    /**
     *  Gets the dayOfWeek attribute of the Favourite object
     *
     *@return    The dayOfWeek value
     */
    public Integer getDayOfWeek() {
        return dayOfWeek;
    }


    /**
     *  Sets the name attribute of the Favourite object
     *
     *@param  name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Sets the titleString attribute of the Favourite object
     *
     *@param  titleString  The new titleString value
     */
    public void setTitleString(String titleString) {
        this.titleString = titleString;
    }


    /**
     *  Sets the titleContains attribute of the Favourite object
     *
     *@param  titleContains  The new titleContains value
     */
    public void setTitleContains(String titleContains) {
        this.titleContains = titleContains;
    }


    /**
     *  Sets the titleRegex attribute of the Favourite object
     *
     *@param  titleRegex  The new titleRegex value
     */
    public void setTitleRegex(Pattern titleRegex) {
        this.titleRegex = titleRegex;
    }


    /**
     *  Sets the channelID attribute of the Favourite object
     *
     *@param  channelID  The new channelID value
     */
    public void setChannelID(String channelID) {
        this.channelID = channelID;
    }


    /**
     *  Sets the afterTime attribute of the Favourite object
     *
     *@param  afterTime  The new afterTime value
     */
    public void setAfterTime(Time afterTime) {
        this.afterTime = afterTime;
    }


    /**
     *  Sets the beforeTime attribute of the Favourite object
     *
     *@param  beforeTime  The new beforeTime value
     */
    public void setBeforeTime(Time beforeTime) {
        this.beforeTime = beforeTime;
    }


    /**
     *  Sets the dayOfWeek attribute of the Favourite object
     *
     *@param  dayOfWeek  The new dayOfWeek value
     */
    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


    // ------------------------------------------------------------------------

    private String name;
    // The user-specified name of this favourite
    private String titleString;
    // Exact match for the title
    private String titleContains;
    // Exact match for the title
    private Pattern titleRegex;
    // Regular expression to match the title
    private String channelID;
    // The channel it must be on
    private Time afterTime;
    // The time it must be on after
    private Time beforeTime;
    // The time it must be on before
    private Integer dayOfWeek;
    // The day of the week it's on

}
