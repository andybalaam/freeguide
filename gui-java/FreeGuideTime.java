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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/*
 * FreeGuideTime.java
 *
 * A (hopefully) very simple class just representing a time of day accurate to
 * the nearest millisecond.
 *
 * Where a choice is available, it aims to be verbose rather than elegant in
 * an attempt to make things as clear as possible.
 *
 * Everything is totally 24-hour.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideTime {
	
	/** 
	 * Create a FreeGuideTime object set to midnight.
	 */
	public FreeGuideTime() {
		milliseconds = 0;
	}
	
	/** 
	 * Create a FreeGuideTime object with the same time as other.
	 */
	public FreeGuideTime(FreeGuideTime other) {
		setTime(other);
	}
	
	/** 
	 * Create a FreeGuideTime object set to the given hour.
	 */
	public FreeGuideTime(int hour) {setTime(hour);}
	
	/** 
	 * Create a FreeGuideTime object set to the given hour and minute.
	 */
	public FreeGuideTime(int hour, int minute) {setTime(hour, minute);}
	
	/** 
	 * Create a FreeGuideTime object set to the given hour, minute and second.
	 */
	public FreeGuideTime(int hour, int minute, int second) {setTime(hour, minute, second);}
	
	/** 
	 * Create a FreeGuideTime object set to the given hour, minute, second and millisecond.
	 */
	public FreeGuideTime(int hour, int minute, int second, int millisecond) {setTime(hour, minute, second, millisecond);}
	
	/**
	 * Create a FreeGuideTime object by taking the just time-related bits of the given Date object.
	 */
	public FreeGuideTime(Date date){setTime(date);}
	
	/**
	 * Create a FreeGuideTime object by taking the just time-related bits of the given Calendar object.
	 */
	public FreeGuideTime(Calendar date){setTime(date);}
	
	/**
	 * Create a FreeGuideTime object from this string in the form HHMM.
	 */
	public FreeGuideTime(String hhmm) {setTimeHHMMString(hhmm);}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Set the time to the given hour (0-23) exactly.
	 * 
	 * Deals with numbers outside the range by simply wrapping e.g. -2 becomes 22.
	 */
	public void setTime(int hour) {
		setMillisecondsSinceMidnight(hour * oneHour);
	}
	
	/** 
	 * Create a FreeGuideTime object with the same time as other.
	 */
	public void setTime(FreeGuideTime other) {
		milliseconds = other.getMillisecondsSinceMidnight();
	}
	
	/**
	 * Set the time to the given hour and minute exactly.
	 * 
	 * Simply adds the numbers given e.g.
	 * setTime(21, -5) gives us a time of 20:55.
	 */
	public void setTime(int hour, int minute) {
		setMillisecondsSinceMidnight( (hour*oneHour) + (minute*oneMinute) );
	}
	
	/**
	 * Set the time to the given hour, minute and second exactly.
	 */
	public void setTime(int hour, int minute, int second) {
		setMillisecondsSinceMidnight( (hour*oneHour) + (minute*oneMinute) + (second*oneSecond) );
	}
	
	/**
	 * Set the time to the given hour, minute, second and millisecond.
	 */
	public void setTime(int hour, int minute, int second, int millisecond) {
		setMillisecondsSinceMidnight( (hour*oneHour) + (minute*oneMinute) + (second*oneSecond) + millisecond );
	}
	
	/**
	 * Set the time by taking the just time-related bits of the given Date object.
	 */
	public void setTime(Date date) {
		
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		
		setTime( cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND) );
		
	}
	
	/**
	 * Set the time by taking the just time-related bits of the given Calendar object.
	 */
	public void setTime(Calendar date) {
		setTime( date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE), date.get(Calendar.SECOND), date.get(Calendar.MILLISECOND) );
	}
	
	/**
	 * Sets this time to the time specified by the String hhmm in the form of
	 * two digits representing the hour and two digits representing the minute.
	 */
	public void setTimeHHMMString(String hhmm) {
		if(hhmm.length() != 4) {
			setMillisecondsSinceMidnight(0);
			FreeGuide.log.warning("Invalid time string \"" + hhmm + "\"");
		} else {
			
			setTime( Integer.parseInt(hhmm.substring(0, 2)), Integer.parseInt(hhmm.substring(2)) );
			
		}
	}
	
	/**
	 * Set the hour of the day.
	 */
	public void setHours(int hours) {
		addMilliseconds( (hours-getHours())*oneHour );
	}
	
	/**
	 * Set the number of minutes past the hour.
	 */
	public void setMinutes(int minutes) {
		addMilliseconds( (minutes-getMinutes())*oneMinute );
	}
	
	/**
	 * Set the number of seconds past the minute.
	 */
	public void setSeconds(int seconds) {
		addMilliseconds( (seconds-getSeconds())*oneSecond );
	}
	
	/**
	 * Set the number of milliseconds past the second.
	 */
	public void setMilliseconds(int milliseconds) {
		addMilliseconds( milliseconds-getMilliseconds() );
	}
	
	/**
	 * Add incHours hours to the time.
	 */
	public void addHours(int incHours) {
		addMilliseconds(incHours * oneHour);
	}
	
	/**
	 * Add incMinutes minutes to the time.
	 */
	public void addMinutes(int incMinutes) {
		addMilliseconds(incMinutes * oneMinute);
	}
	
	/**
	 * Add incSeconds seconds to the time.
	 */
	public void addSeconds(int incSeconds) {
		addMilliseconds(incSeconds * oneSecond);
	}
	
	/**
	 * Add incMilliseconds milliseconds to the time.
	 */
	public void addMilliseconds( long incMilliseconds ) {
		setMillisecondsSinceMidnight( milliseconds + incMilliseconds );
	}
	
	/**
	 * Set the time to given millisecond after midnight.
	 */
	public void setMillisecondsSinceMidnight(long milliseconds) {
		this.milliseconds = milliseconds;
		
		wrap();
		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Return this time, in the form of the number of milliseconds
	 * since midnight.  Guaranteed to be >= 0 and less than the number of
	 * milliseconds in a day ( i.e. < 24*60*60*1000 ).
	 *
	 * @returns the "number of milliseconds since midnight" representation of
	 * this time
	 */
	public long getMillisecondsSinceMidnight() {
		return milliseconds;
	}
	
	/**
	 * Return the number of hours since midnight 0 to 23.
	 */
	public int getHours() {
		return (int)(milliseconds % oneDay);
	}
	
	/**
	 * Return the number of minutes past the hour 0 to 59.
	 */
	public int getMinutes() {
		return (int)(milliseconds % oneHour);
	}
	
	/**
	 * Return the number of seconds past the minute 0 to 59.
	 */
	public int getSeconds() {
		return (int)(milliseconds % oneMinute);
	}
	
	/**
	 * Return the number of milliseconds past the second 0 to 999.
	 */
	public int getMilliseconds() {
		return (int)(milliseconds % oneSecond);
	}
	
	/**
	 * Return the number of hours since midnight formatted as a String of
	 * length 2 - "00" to "23".
	 */
	public String getHoursString() {
		return padded(String.valueOf(getHours()), 2);
	}
	
	/**
	 * Return the number of minutes past the hour formatted as a String of
	 * length 2 - "00" to "59".
	 */
	public String getMinutesString() {
		return padded(String.valueOf(getMinutes()), 2);
	}
	
	/**
	 * Return the number of seconds past the minute formatted as a String of
	 * length 2 - "00" to "59".
	 */
	public String getSecondsString() {
		return padded(String.valueOf(getSeconds()), 2);
	}
	
	/**
	 * Return the number of milliseconds past the second formatted as a String
	 * of length 3 - "000" to "999".
	 */
	public String getMillisecondsString() {
		return padded(String.valueOf(getMilliseconds()), 3);
	}
	
	public String getHHMMString() {
		return getHoursString() + getMinutesString();
	}
	
	/**
	 * Return a string representation of this time formatted as:
	 * hh:mm:ss:nnnn where hh is the hour, mm is the minute, ss is the second
	 * and nnnn is the milliseconds.
	 */
	public String toString() {
		return getHoursString() + ":" + getMinutesString() + ":" + getSecondsString() + ":" + getMillisecondsString();		
	}
	
	/**
	 * A convenience method which sets a Calendar object to this time.
	 */
	public void adjustCalendar(Calendar cal) {
		// FIXME - does this work by reference?
		cal.set( Calendar.HOUR_OF_DAY, getHours() );
		cal.set( Calendar.MINUTE, getMinutes() );
		cal.set( Calendar.SECOND, getSeconds() );
		cal.set( Calendar.MILLISECOND, getMilliseconds() );
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Returns true if this time is after the other.
	 *
	 * @param	other the time to compare with.
	 * @returns true if and only if this time is strictly later than the
	 * other time; false otherwise.
	 */
	public boolean after(FreeGuideTime other) {
		
		return after(other, new FreeGuideTime());
		
	}
	
	/**
	 * Returns true if this time is after the other, but viewing the day as not
	 * starting and ending at midnight, but at divideTime.
	 *
	 * e.g. 
	 * FreeGuideTime a = new FreeGuideTime(6, 0);	// 6am
	 * FreeGuideTime b = new FreeGuideTime(2, 0);	// 2am
	 * // Is 6am after 2am?  Not if you say the day starts/ends at 3am!
	 * FreeGuideTime divide = new FreeGuideTime(3, 0) // 3am
	 *
	 *	if(a.after(b)) {
	 *		System.out.println("a is after b");					// Never gets here.
	 *	} else {
	 *		System.out.println("a is before or the same as b");	// Prints this line!
	 *	}
	 *
	 * @param	other the time to compare with.
	 * @returns true if and only if this time is strictly later than the
	 * other time; false otherwise.
	 */
	public boolean after(FreeGuideTime other, FreeGuideTime divideTime) {
		
		return (compareTo(other, divideTime) > 0);
		
	}
	
	/**
	 * Returns true if this time is before the other.
	 *
	 * @param	other the time to compare with.
	 * @returns true if and only if this time is strictly earlier than the
	 * other time; false otherwise.
	 */
	public boolean before(FreeGuideTime other) {
		
		return before(other, new FreeGuideTime());
		
	}
	
	/**
	 * Returns true if this time is before the other, but viewing the day as not
	 * starting and ending at midnight, but at divideTime.  See the example
	 * under after().
	 *
	 * @param	other the time to compare with.
	 * @returns true if and only if this time is strictly earlier than the
	 * other time; false otherwise.
	 */
	public boolean before(FreeGuideTime other, FreeGuideTime divideTime) {
		
		return (compareTo(other, divideTime) < 0);
		
	}
	
	/**
	 * Compare two times for ordering.
	 *
	 * @param other the other time to compare to
	 * @returns 0 if the other is equal to this time; -1 this time is before
	 * the other time; +1 0 if this time is after the other time.
	 */
	public int compareTo(FreeGuideTime other) {
		
		return compareTo(other, new FreeGuideTime());
		
	}
	
	/**
	 * Compare two times for ordering.  See the example under after() for more
	 * info in divideTime.
	 *
	 * @param other the other time to compare to
	 * @param divideTime the time at which the day is said to start and end.
	 * @returns 0 if the other is equal to this time; -1 if this time is before
	 * the other time; +1 0 if this time is after the other time.
	 */
	public int compareTo(FreeGuideTime other, FreeGuideTime divideTime) {
		
		// Get ourselves into ms
		long thisMS = getMillisecondsSinceMidnight();
		long otherMS = other.getMillisecondsSinceMidnight();
		long divideMS = divideTime.getMillisecondsSinceMidnight();
		
		// check for equality
		if(thisMS == otherMS) {
			return 0;
		}
		
		// Adjust either of the times if they're before the divide time
		if(thisMS < divideMS) {
			thisMS += oneDay;
		}
		if(otherMS < divideMS) {
			otherMS += oneDay;
		}
		
		// And finally compare them
		if(thisMS < otherMS) {
			
			return -1;
			
		} else {
			
			return 1;
			
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	// Get milliseconds within [0:oneDay)
	private void wrap() {
		
		// Get it within (-oneDay, oneDay)
		milliseconds %= oneDay;
		
		// Finish off
		if(milliseconds < 0) {
			milliseconds += oneDay;
		}
		
	}
	
	// Pad out a string with zeros until it's the given length
	private String padded(String input, int length) {
		
		// When I figure out assertions in NetBeans:
		//assert input.length() <= length;
		
		StringBuffer buffy = new StringBuffer(input);
		
		while(buffy.length() < length) {
			buffy.insert(0, '0');
		}
		
		return buffy.toString();
		
	}
	
	// ------------------------------------------------------------------------
	
	private long milliseconds;
	
	private static final long oneSecond = 1000;
	private static final long oneMinute = oneSecond * 60;
	private static final long oneHour = oneMinute * 60;
	private static final long oneDay = oneHour * 24;
	
	// ------------------------------------------------------------------------
	
	public static final FreeGuideTime MIDNIGHT = new FreeGuideTime();
	
}
