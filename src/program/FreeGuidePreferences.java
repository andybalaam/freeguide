/*
 * FreeGuide J2
 *
 * Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.awt.Color;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.prefs.*;
import java.util.regex.Pattern;
import java.util.Vector;

/**
 * FreeGuidePreferences
 *
 * A wrapper around the java.util.prefs.Preferences class to extend its
 * capabilities in ways useful for FreeGuide.
 *
 * This includes accessor functions for objects such as Dates, FreeGuideTimes
 * and even complex ones like FreeGuideFavourites.  The way this is
 * implemented is with keys structured as dot-separated lists 
 * (e.g. 1.name, 1.after_time, 2.name, 2.after_time etc. for favourites and
 * tv_grab.1, tv_grab.2 etc. for commandline options.)
 *
 * @author  Andy Balaam
 * @version 3
 */

public class FreeGuidePreferences {
	
	/** 
	 * Creates a new instance of FreeGuidePreferences, putting the information
	 * in the user node "/org/freeguide-tv/" + subNode
	 */
	public FreeGuidePreferences(String subNode) {
		
		prefs = Preferences.userRoot().node("/org/freeguide-tv/"+subNode);
		
	}
	
	// ------------------------------------------------------------------------
	// Convenience methods
	
	/**
	 * Return all keys that are not commented out with a # as the name.
	 */
	public String[] noncommentedKeys() throws BackingStoreException {
		
		String[] allKeys = keys();
		Vector ans = new Vector();
		
		for(int i=0;i<allKeys.length;i++) {
			// If not commented, remember it
			if(!get(allKeys[i]).equals("#")) {
				ans.add(allKeys[i]);
			}
		}
		
		return FreeGuideUtils.arrayFromVector_String(ans);
		
	}
	
	/**
	 * Remove all key-value pairs in this node and replace them with the keys
	 * in keys and the values in values.
	 */
	public void replaceAll(String[] keys, String[] values) throws BackingStoreException {
		clear();
		for(int i=0;i<keys.length;i++) {
			put( keys[i], values[i] );
		}
	}
	
	/**
	 * Remove all key-value pairs in this node and replace them with the keys
	 * in keys and the FreeGuideFavourites in values.
	 */
	public void replaceAllFreeGuideFavourite(String[] keys, FreeGuideFavourite[] values) throws BackingStoreException {
		clear();
		for(int i=0;i<keys.length;i++) {
			putFreeGuideFavourite( keys[i], values[i] );
		}
	}
        
	public void replaceAllFreeGuideChannelSets(String[] keys, FreeGuideChannelSet[] values) throws BackingStoreException
        {
            clear();
            for(int i=0;i<keys.length;i++)
            {
                putFreeGuideChannelSet( keys[i], values[i] );
            }
	}
	/**
	 * Adds the given FreeGuideProgramme to the end of the list stored in this
	 * node.  See the notes for getFreeGuideProgramme about the amount of
	 * information stored.
	 */
	public void appendFreeGuideProgramme(FreeGuideProgramme prog) {
		
		// Find the first unoccupied spot
		int i = 1;
		String title = get( i + ".title" );
		while( title != null ) {
			i++;
			title = get( i + ".title" );
		}
		
		// And put our programme in it
		put( i + ".title", prog.getTitle() );
		putDate( i + ".start", prog.getStart().getTime() );
		put( i + ".channel_id", prog.getChannelID() );
		
	}
	
	/**
	 * Adds the given FreeGuideFavourite to the end of the list stored in this
	 * node.
	 */
	public void appendFreeGuideFavourite(FreeGuideFavourite fav) {
		
		// Find the first unoccupied spot
		int i = 1;
		String title = get( i + ".name" );
		while( title != null ) {
			i++;
			title = get( i + ".name" );
		}
		
		putFreeGuideFavourite(String.valueOf(i), fav);
		
	}
	
	public int findFreeGuideProgramme(FreeGuideProgramme prog) {
		
		int i = 1;
		
		String title = get( i + ".title" );
		
		while( title != null ) {
			
			String startKey = i + ".start";
			String channelIDKey = i + ".channel_id";
			
			if(title.equals(prog.getTitle()) && 
				getDate(startKey, null).equals(prog.getStart().getTime()) &&
				get(channelIDKey, "").equals(prog.getChannelID())) {
			
				// If this is the programme, return
				return i;
			}
			
			// Otherwise look at the next one
			i++;
			title = get( i + ".title" );
			
		}
		
		// Nothing found: return -1
		return -1;
		
	}
	
	public void removeChoice(int index) {
		
		// Remove these even though they will be over-written in a minute
		// (just for neatness)
		remove(index + ".title");
		remove(index + ".start");
		remove(index + ".channel_id");
		
		// Shift everything down
		index++;
		String title = get( index + ".title" );
		while( title != null ) {
			
			put( (index-1) + ".title", title );
			putDate( (index-1) + ".start", getDate(index + ".start", null) );
			put( (index-1) + ".channel_id", get(index + ".channel_id", "") );
			
			index++;
			title = get( index + ".title" );
			
		}
		
		// Remove the last one in the list
		// (so that it's 1 shorter than what we started with)
		remove((index-1) + ".title");
		remove((index-1) + ".start");
		remove((index-1) + ".channel_id");
		
	}
	
	public void removeFreeGuideFavourite(int index) {
		
		// Remove these even though they will be over-written in a minute
		// (just for neatness)
		remove(index + ".name");
		remove(index + ".title_string");
		remove(index + ".title_regex");
		remove(index + ".channel_id");
		remove(index + ".after_time");
		remove(index + ".before_time");
		remove(index + ".day_of_week");
		
		// Shift everything down
		index++;
		String name = get( index + ".name" );
		while( name != null ) {
			
			put((index-1) + ".name", get(index+".name"));
			put((index-1) + ".title_string", get(index+".title_string"));
			put((index-1) + ".title_regex", get(index+".title_regex"));
			put((index-1) + ".channel_id", get(index+".channel_id"));
			put((index-1) + ".after_time", get(index+".after_time"));
			put((index-1) + ".before_time", get(index+".before_time"));
			put((index-1) + ".day_of_week", get(index+".day_of_week"));
			
			index++;
			name = get( index + ".name" );
			
		}
		
		// Remove the last one in the list
		// (so that it's 1 shorter than what we started with)
		remove((index-1) + ".name");
		remove((index-1) + ".title_string");
		remove((index-1) + ".title_regex");
		remove((index-1) + ".channel_id");
		remove((index-1) + ".after_time");
		remove((index-1) + ".before_time");
		remove((index-1) + ".day_of_week");
		
	}
	
	/**
	 * Return an array of strings with keys key+".1", key+".2" etc.
	 */
	public String[] getStrings(String key) {
		
		Vector ans = new Vector();
		
		int i=1;
		String line = get(key+"."+i);
		
		while(line != null) {
			ans.add(line);
			i++;
			line = get(key+"."+i);
		}
		
		return FreeGuideUtils.arrayFromVector_String(ans);
		
	}
	
	public void putStrings(String key, String[] values) {
		
		int i;
		for(i=0;i<values.length;i++) {
			
			put(key+"."+(i+1), values[i]);
			
		}
		
		while(get(key+"."+(i+1), null) != null) {
			remove(key+"."+(i+1));
			i++;
		}
		
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Performs a get using a default value of null
	 */
	public String get(String key) {
		return get(key, null);
	}
	
	// ------------------------------------------------------------------------
	// Special pseudo-wrappers
	
	public Integer getInteger(String key, Integer def) {
		String ans = get(key);	
		if(ans==null) {
			return def;
		} else {
			return Integer.valueOf(ans);
		}
	}
	public void putInteger(String key, Integer value) {
		if(value!=null) {
			putInt(key, value.intValue());
		} else {
			remove(key);
		}
	}
	
	public Date getDate(String key, Date def) {
		String ans = get(key);
		if(ans==null) {
			return def;
		} else {
			return new Date( getLong( key, 0 ) );
		}
	}
	public void putDate(String key, Date value) {
		if(value!=null) {
			putLong( key, value.getTime() );
		} else {
			remove(key);
		}
	}
	
	public Color getColor(String key, Color def) {
		int r = getInt( key + ".r", def.getRed() );
		int g = getInt( key + ".g", def.getGreen() );
		int b = getInt( key + ".b", def.getBlue() );
		return new Color( r, g, b );
	}
	public void putColor(String key, Color value) {
		if(value!=null) {
			putInt( key + ".r", value.getRed() );
			putInt( key + ".g", value.getGreen() );
			putInt( key + ".b", value.getBlue() );
		} else {
			remove(key + ".r");
			remove(key + ".g");
			remove(key + ".b");
		}
	}
	
	public Pattern getPattern(String key, Pattern def) {
		String ans = get(key);
		if(ans==null) {
			return def;
		} else {
			return Pattern.compile( ans );
		}
	}
	public void putPattern(String key, Pattern value) {
		if(value!=null) {
			put( key, value.pattern() );
		} else {
			remove(key);
		}
	}
	
	public FreeGuideTime getFreeGuideTime(String key) {
		return getFreeGuideTime(key, null);
	}
	public FreeGuideTime getFreeGuideTime(String key, FreeGuideTime def) {
		String ans = get(key);
		if(ans==null) {
			return def;
		} else {
			return new FreeGuideTime( ans );
		}
	}
	public void putFreeGuideTime(String key, FreeGuideTime value) {
		if(value!=null) {
			put(key, value.getHHMMString());
		} else {
			remove(key);
		}
	}
	
	/**
	 * Gets enough info from the preferences to identify a programme reliably.
	 * Note: does not store all the info about a programme, just enough to
	 * identify it i.e. title, start time, channel id.
	 */
	public FreeGuideProgramme getFreeGuideProgramme(String key, FreeGuideProgramme def) {
		
		String title = get(key+".title");
		
		// If the title isn't there, the programme isn't there (by convention)
		if(title==null) {
			return def;
		} else {	// If it is there, fill in the details
			FreeGuideProgramme prog = new FreeGuideProgramme();
			prog.addToTitle(title);
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(getDate( key+".start", null ));
			prog.setStart( cal );
			
			prog.setChannelID( get(key+".channel_id", "" ) );
			
			return prog;
		}
		
	}
	
	/**
	 * Gets a FreeGuideFavourite object from this preferences store.
	 */
	public FreeGuideFavourite getFreeGuideFavourite(String key, FreeGuideFavourite def) {
		
		String name = get(key+".name");
		
		// If the title isn't there, the programme isn't there (by convention)
		if(name==null) {
			return def;
		} else {	// If it is there, fill in the details
			FreeGuideFavourite fav = new FreeGuideFavourite();
			
			fav.setName( name );
			fav.setTitleString( get( key+".title_string" ) );
			fav.setTitleContains( get( key+".title_contains" ) );
			fav.setTitleRegex( getPattern( key+".title_regex", null ) );
			fav.setChannelID( get( key+".channel_id" ) );
			fav.setAfterTime( getFreeGuideTime( key+".after_time", null ) );
			fav.setBeforeTime( getFreeGuideTime( key+".before_time", null ) );
			fav.setDayOfWeek( getInteger( key+".day_of_week", null ) );
			
			return fav;
		}
	}
	/**
	 * Puts a FreeGuideFavourite object into this preferences store.
	 */
	public void putFreeGuideFavourite(String key, FreeGuideFavourite value) {
		
		put( key+".name", value.getName() );
		put( key+".title_string", value.getTitleString() );
		put( key+".title_contains", value.getTitleContains() );
		putPattern( key+".title_regex", value.getTitleRegex() );
		put( key+".channel_id", value.getChannelID() );
		putFreeGuideTime( key+".after_time", value.getAfterTime() );
		putFreeGuideTime( key+".before_time", value.getBeforeTime() );
		putInteger( key+".day_of_week", value.getDayOfWeek() );
		
	}
        public FreeGuideChannelSet getFreeGuideChannelSet(String key, FreeGuideChannelSet def)
        {
		
            String name = get(key+".name");

            
            if(name==null) {
                    return def;
            } else {	// If it is there, fill in the details
                    FreeGuideChannelSetImpl  cset = new FreeGuideChannelSetImpl();

                    cset.setChannelSetName(name);
                    
                    String ids = get(key+".channelids");
                    if (ids != null)
                        cset.addChannelsFromString(ids,null);
                    else return def;
                    return cset;
            }
	}
        
        public void putFreeGuideChannelSet(String key, FreeGuideChannelSet value)
        {
            put(key+".name", value.getChannelSetName());
            put(key+".channelids",FreeGuideChannelSetImpl.toString(value.getChannelIDs()));
        }

	// ------------------------------------------------------------------------
	// Wrapper methods
	
	public String get(String key, String def) {return prefs.get(key, def);}
	public boolean getBoolean(String key, boolean def) {return prefs.getBoolean(key, def);}
	public byte[] getByteArray(String key, byte[] def) {return prefs.getByteArray(key, def);}
	public double getDouble(String key, double def) {return prefs.getDouble(key, def);}
	public float getFloat(String key, float def) {return prefs.getFloat(key, def);}
	public int getInt(String key, int def) {return prefs.getInt(key, def);}
	public long getLong(String key, long def) {return prefs.getLong(key, def);}
	
	public void put(String key, String value) {
		if(value!=null) {
			prefs.put(key, value);
		} else {
			remove(key);
		}
	}
	public void putBoolean(String key, boolean value) {prefs.putBoolean(key, value);}
	public void putByteArray(String key, byte[] value) {prefs.putByteArray(key, value);}
	public void putDouble(String key, double value) {prefs.putDouble(key, value);}
	public void putFloat(String key, float value) {prefs.putFloat(key, value);}
	public void putInt(String key, int value) {prefs.putInt(key, value);}
	public void putLong(String key, long value)	 {prefs.putLong(key, value);}
	
	public void clear() throws BackingStoreException {prefs.clear();}
	public void flush() throws BackingStoreException {prefs.flush();}
	public void remove(String key) {prefs.remove(key);}
	public String[] keys() throws BackingStoreException {return prefs.keys();}
	public void sync() throws BackingStoreException {prefs.sync();}
	
	public void exportNode(OutputStream os) throws java.io.IOException, BackingStoreException {prefs.exportNode(os);}
	public void importPreferences(InputStream is) throws java.io.IOException, BackingStoreException, InvalidPreferencesFormatException {prefs.importPreferences(is);}
	
	// ------------------------------------------------------------------------
	
	private Preferences prefs;
	
}
