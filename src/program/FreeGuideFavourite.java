/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * FreeGuideFavourite
 *
 * A description of a favourite TV program, vague or specific.
 *
 * @author  Andy Balaam
 * @version 3
 */
public class FreeGuideFavourite {
	
	
	public FreeGuideFavourite() {
		
	}
	
	// ------------------------------------------------------------------------

	/**
	 * matches
	 *
	 * Decides whether or not a programme matches this favourite.
	 *
	 * @param	prog the programme to check
	 * @returns true if this programme matches this favourite
	 */
	public boolean matches(FreeGuideProgramme prog) {
		
		String progTitle = prog.getTitle();
		
		// Match the title exactly
		if( (titleString != null) && !titleString.equals(progTitle) ) {
			//FreeGuide.log.info(progTitle + " Title match failed");
			return false;
		}
		
		// Match the title to containing a string
		if( (titleContains!=null) && (progTitle.indexOf(titleContains)==-1) ) {
			//FreeGuide.log.info(progTitle + " Title contains failed");
			return false;
		}
		
		// Match the title to a regular expression
		if( (titleRegex!=null) && !titleRegex.matcher(progTitle).matches() ) {
			//FreeGuide.log.info(progTitle + " Title regex failed");
			return false;
		}
		
		// Match the channel ID
		if( (channelID != null) && !channelID.equals(prog.getChannelID()) ) {
			//FreeGuide.log.info(progTitle + " '" + channelID + "' '" + prog.getChannelID()+"'");
			return false;
		}
		
		// Get the start time of the programme
		FreeGuideTime progStartTime = new FreeGuideTime(prog.getStart());
		
		//FreeGuide.log.info(progTitle + " " + afterTime + " must be after " + progStartTime);
		
		// Match the time it must be after
		if(afterTime!=null && afterTime.after(progStartTime)) {
			//FreeGuide.log.info(progTitle + " afterTime failed");
			return false;
		}
		
		// Match the time it must be before
		if(beforeTime!=null && beforeTime.before(progStartTime)) {
			//FreeGuide.log.info(progTitle + " beforeTime failed");
			return false;
		}
		
		// Match the day of the week
		if( dayOfWeek!=null && ( dayOfWeek.intValue() != prog.getStart().get( Calendar.DAY_OF_WEEK ) ) ) {
			//FreeGuide.log.info(progTitle + " Day of week failed");
			return false;
		}

		//FreeGuide.log.info(progTitle + " Passed");
		// Passed all the tests!
		return true;

	}
	
	// ------------------------------------------------------------------------
	// Accessors
	
	public String getName(){return name;}
	public String getTitleString(){return titleString;}
	public String getTitleContains(){return titleContains;}
	public Pattern getTitleRegex(){return titleRegex;}
	public String getChannelID(){return channelID;}
	public FreeGuideTime getAfterTime(){return afterTime;}
	public FreeGuideTime getBeforeTime(){return beforeTime;}
	public Integer getDayOfWeek(){return dayOfWeek;}
	
	public void setName(String name){this.name = name;}
	public void setTitleString(String titleString){this.titleString = titleString;}
	public void setTitleContains(String titleContains){this.titleContains = titleContains;}
	public void setTitleRegex(Pattern titleRegex){this.titleRegex = titleRegex;}
	public void setChannelID(String channelID){this.channelID = channelID;}
	public void setAfterTime(FreeGuideTime afterTime){this.afterTime = afterTime;}
	public void setBeforeTime(FreeGuideTime beforeTime){this.beforeTime = beforeTime;}
	public void setDayOfWeek(Integer dayOfWeek){this.dayOfWeek = dayOfWeek;}
	
	// ------------------------------------------------------------------------
	
	private String name;			// The user-specified name of this favourite
	private String titleString;		// Exact match for the title
	private String titleContains;		// Exact match for the title
	private Pattern titleRegex;		// Regular expression to match the title
	private String channelID;		// The channel it must be on
	private FreeGuideTime afterTime;// The time it must be on after
	private FreeGuideTime beforeTime;// The time it must be on before
	private Integer dayOfWeek;		// The day of the week it's on
	
}