/*
 *FreeGuide J2
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

import java.awt.Color;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * FreeGuidePreferences
 *
 * Provides a place to hold the various Preferences objects needed for FreeGuide, and some
 * convenience methods for dealing with them.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuidePreferences {

	/** The constructor */
    public FreeGuidePreferences() {
		
		screenPrefs = Preferences.userRoot().node("/org/freeguide-tv/screen");
		grabberPrefs = Preferences.userRoot().node("/org/freeguide-tv/grabber");
		miscPrefs = Preferences.userRoot().node("/org/freeguide-tv/misc");
		favouritesPrefs = Preferences.userRoot().node("/org/freeguide-tv/favourites");
		chosenPrefs = Preferences.userRoot().node("/org/freeguide-tv/chosen");
		channelsPrefs = Preferences.userRoot().node("/org/freeguide-tv/channels");
		
    }
	
	/**
	 * noErrors
	 *
	 * Reports on whether setting up preferences was successful.
	 *
	 * @returns true unless errors were encountered in the course of
	 * configuration processing
	 */
	public boolean noErrors() {
		
		// FIXME - always returns true as assumes nothing can go wrong.
		return true;
		
	}
	
	// ------------------------------------------------------------------------
	// Special convenience accessor methods
	
	/**
	 * getChannels
	 *
	 * @returns an array of size 2 with each element being arrays of Strings,
	 * the first being the channel ids, and the second the names.
	 */
	public String[] getChannelIDs() {
		
		// If there's a config file for the grabber, get its info
		String grabberConfig = channelsPrefs.get("grabber_config_file", null);
		if( grabberConfig != null) {
			return getChannelIDsFromConfigFile( grabberConfig );
		}
		
		// Otherwise we'll get it from channelsPrefs
		
		String[] ans = new String[0];
		
		try {
		
			return channelsPrefs.keys();
			
		} catch(java.util.prefs.BackingStoreException e) {
			
			FreeGuide.log.warning(e.getMessage());
			
		}
		
		return new String[0];
		
	}		
	
	public void putChannelIDs(String[] channelIDs) {
		
		// If there's a config file for the grabber, write to it
		String grabberConfig = channelsPrefs.get("grabber_config_file", null);
		if( grabberConfig != null) {
			putChannelIDsToConfigFile( channelIDs );
		} else {
		
			try {
			
				// Otherwise we'll put it to channelsPrefs;
				channelsPrefs.clear();
				for(int i=0;i<channelIDs.length;i++) {
			
					channelsPrefs.put( channelIDs[i], "" );
			
				}
				
			} catch(java.util.prefs.BackingStoreException e) {
			
				FreeGuide.log.warning(e.getMessage());
			
			}//try			
		}//if
	}//putChannelIDs
	
	public FreeGuideProgramme[] getChoices() {
		
		Vector ans = new Vector();
		
		int i = 1;
		
		String title = chosenPrefs.get( i + ".title", null );
		
		while( title != null ) {
			
			FreeGuideProgramme prog = new FreeGuideProgramme();
			prog.setTitle( title );
			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(getChosenDate( i + ".start", null ));
			prog.setStart( cal );
			prog.setChannelID( chosenPrefs.get( i + ".channel_id", "" ) );
			ans.add( prog );
			
			i++;
			title = chosenPrefs.get( i + ".title", null );
		}
		
		return (FreeGuideProgramme[])ans.toArray();
		
	}
	
	public void addChoice(FreeGuideProgramme prog) {
		
		// Find the first unoccupied spot
		int i = 1;
		String title = chosenPrefs.get( i + ".title", null );
		while( title != null ) {
			i++;
			title = chosenPrefs.get( i + ".title", null );
		}
		
		// And put our programme in it
		chosenPrefs.put( i + ".title", prog.getTitle() );
		putChosenDate( i + ".start", prog.getStart().getTime() );
		chosenPrefs.put( i + ".channel_id", prog.getChannelID() );
		
	}
	
	public void removeChoice(FreeGuideProgramme prog) {
		
		// Search for our programme, remove it and shift the others down
		int i = 1;
		boolean shiftDown = false;
		String title = chosenPrefs.get( i + ".title", null );
		while( title != null ) {
			
			String titleKey = i + ".title";
			String startKey = i + ".start";
			String channelIDKey = i + ".channel_id";
			
			if(shiftDown) {
				
				// Copy down
				chosenPrefs.put( (i-1) + ".title", chosenPrefs.get( titleKey, "") );
				putChosenDate( (i-1) + ".start", getChosenDate( startKey, null) );
				chosenPrefs.put( (i-1) + ".channel_id", chosenPrefs.get( channelIDKey, "") );
				chosenPrefs.remove(titleKey);
				chosenPrefs.remove(startKey);
				chosenPrefs.remove(channelIDKey);
				
			} else if(title.equals(prog.getTitle()) && 
				getChosenDate(i + ".start", null).equals(prog.getStart().getTime()) &&
				chosenPrefs.get(i + ".channel_id", "").equals(prog.getChannelID())) {
				// If this is the programme to be removed
				
				// Remove it
				chosenPrefs.remove( titleKey );
				chosenPrefs.remove( startKey );
				chosenPrefs.remove( channelIDKey );
				
				// And start shifting the others down
				shiftDown = true;
				
			}
			
			i++;
			title = chosenPrefs.get( titleKey, null );
		}
	}
		
	public FreeGuideFavourite[] getFavourites() {
		
		Vector ans = new Vector();
		
		int i = 1;
		String name = favouritesPrefs.get( i + ".name", null );
		
		while( name != null ) {
			
			FreeGuideFavourite fav = new FreeGuideFavourite();
			
			fav.setName( name );
			fav.setTitleString( favouritesPrefs.get( i + ".title_string", null ) );
			fav.setTitleRegex( getFavouritePattern( i + ".title_regex", null ) );
			fav.setChannelID( favouritesPrefs.get( i + ".channel_id", null ) );
			fav.setAfterTime( getFavouriteTime( i + ".after_time", null ) );
			fav.setBeforeTime( getFavouriteTime( i + ".before_time", null ) );
			fav.setDayOfWeek( getFavouriteInteger( i + "day_of_week", null ) );
			
			ans.add( fav );
			
			i++;
			name = favouritesPrefs.get( i + ".name", null );
		}
		
		return (FreeGuideFavourite[])ans.toArray();
		
	}
	
	public void putFavourites(FreeGuideFavourite[] favs) {
		
		try {
		
			favouritesPrefs.clear();
		
			for(int i=1;i<=favs.length;i++) {
			
				FreeGuideFavourite f = favs[i-1];
			
				putFavourite( i+".name", f.getName() );
				putFavourite( i+".title_string", f.getTitleString() );
				putFavouritePattern( i+".title_regex", f.getTitleRegex() );
				putFavourite( i+".channel_id", f.getChannelID() );
				putFavouriteTime( i+".after_time", f.getAfterTime() );
				putFavouriteTime( i+".before_time", f.getBeforeTime() );
				putFavouriteInteger( i+".day_of_week", f.getDayOfWeek() );
				
			}
			
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
		
	}
	
	// ------------------------------------------------------------------------
	// Standard accessor methods
	
	// No default
	public String getScreen		(String key) {return screenPrefs.get(key, null);}
	public String getGrabber	(String key) {return grabberPrefs.get(key, null);}
	public String getMisc		(String key) {return miscPrefs.get(key, null);}
	public String getFavourite	(String key) {return favouritesPrefs.get(key, null);}
	public String getChosen		(String key) {return chosenPrefs.get(key, null);}
	public String getChannel	(String key) {return channelsPrefs.get(key, null);}
	
	// With default
	public String getScreen		(String key, String def) {return screenPrefs.get(key, def);}
	public String getGrabber	(String key, String def) {return grabberPrefs.get(key, def);}
	public String getMisc		(String key, String def) {return miscPrefs.get(key, def);}
	public String getFavourite	(String key, String def) {return favouritesPrefs.get(key, def);}
	public String getChosen		(String key, String def) {return chosenPrefs.get(key, def);}
	public String getChannel	(String key, String def) {return channelsPrefs.get(key, def);}
	
	// ints
	public int getScreenInt		(String key, int def){return screenPrefs.getInt(key, def);}
	public int getGrabberInt	(String key, int def){return grabberPrefs.getInt(key, def);}
	public int getMiscInt		(String key, int def){return miscPrefs.getInt(key, def);}
	
	// Integers
	public Integer getFavouriteInteger(String key, Integer def) {
				
		String ans = favouritesPrefs.get(key, null);
		
		if(ans==null) {
			return def;
		} else {
			return Integer.valueOf(ans);
		}
	}
	
	// Dates
	public Date getMiscDate		(String key, Date def) {
		String ans = miscPrefs.get(key, null);
		if(ans!=null) {
			// Doesn't matter what the default is as this exists
			return new Date( miscPrefs.getLong( key, 0 ) );
		} else {
			return def;
		}
	}
	public Date getChosenDate		(String key, Date def) {
		String ans = chosenPrefs.get(key, null);
		if(ans!=null) {
			// Doesn't matter what the default is as this exists
			return new Date( chosenPrefs.getLong( key, 0 ) );
		} else {
			return def;
		}
	}
	
	
	// Colours
	public Color getScreenColor	(String key, Color def) {
		int r = screenPrefs.getInt( key + ".r", def.getRed() );
		int g = screenPrefs.getInt( key + ".g", def.getGreen() );
		int b = screenPrefs.getInt( key + ".b", def.getBlue() );
		return new Color( r, g, b );
	}
	
	// Patterns
	public Pattern getFavouritePattern(String key, Pattern def) {
		
		String ans = favouritesPrefs.get(key, null);
		
		if(ans!=null) {
			
			return Pattern.compile( ans );
			
		} else {
			return def;
		}
	}
	
	// FreeGuideTimes
	public FreeGuideTime getFavouriteTime(String key, FreeGuideTime def) {
		
		String ans = favouritesPrefs.get(key, null);
		
		if(ans!=null) {
			return new FreeGuideTime( ans );
		} else {
			return def;
		}
		
	}
	
	// ----
	
	public void putScreen		(String key, String value) {screenPrefs.put(key, value);}
	public void putGrabber		(String key, String value) {grabberPrefs.put(key, value);}
	public void putMisc			(String key, String value) {miscPrefs.put(key, value);}
	public void putFavourite	(String key, String value) {favouritesPrefs.put(key, value);}
	public void putChosen		(String key, String value) {chosenPrefs.put(key, value);}
	public void putChannel		(String key, String value) {channelsPrefs.put(key, value);}
	
	// ints
	public void putScreenInt	(String key, int value){screenPrefs.putInt(key, value);}
	public void putGrabberInt	(String key, int value){grabberPrefs.putInt(key, value);}
	public void putMiscInt		(String key, int value){miscPrefs.putInt(key, value);}
	
	// Integers
	public void putFavouriteInteger(String key, Integer value) {
		favouritesPrefs.putInt(key, value.intValue());
	}
	
	// Dates
	public void putMiscDate		(String key, Date value) {
		miscPrefs.putLong( key, value.getTime() );
	}
	public void putChosenDate	(String key, Date value) {
		chosenPrefs.putLong( key, value.getTime() );
	}
	
	// Colours
	public void putScreenColor	(String key, Color value) {
		screenPrefs.putInt( key + ".r", value.getRed() );
		screenPrefs.putInt( key + ".g", value.getGreen() );
		screenPrefs.putInt( key + ".b", value.getBlue() );
	}
	
	// Patterns
	public void putFavouritePattern(String key, Pattern value) {
		favouritesPrefs.put( key, value.pattern() );
	}
	
	// FreeGuideTimes
	public void putFavouriteTime(String key, FreeGuideTime value) {
		favouritesPrefs.put(key, value.getHHMMString());
	}
	
	// ------------------------------------------------------------------------
	// Private functions
	
	private String[] getChannelIDsFromConfigFile( String confFilename ) {
		
		// FIXME - write this
		
		return new String[0];
		
	}
	
	private void putChannelIDsToConfigFile( String[] channelIDs ) {
		
		// FIXME - write this
		
	}
	
	// ------------------------------------------------------------------------
	
	private Preferences screenPrefs;		// The screen dimensions etc.
	private Preferences grabberPrefs;		// Grabber commands and options
	private Preferences miscPrefs;			// Other prefs
	private Preferences favouritesPrefs;	// The user's favourite progs
	private Preferences chosenPrefs;		// The selected progs
	private Preferences channelsPrefs;		// The selected channels

}
