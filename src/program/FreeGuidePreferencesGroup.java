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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.text.SimpleDateFormat;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.prefs.Preferences;
import java.util.Vector;

/**
 * FreeGuidePreferencesGroup
 *
 * Provides a place to hold the various FreeGuidePreferences objects needed for
 * FreeGuide, and some convenience methods for dealing with them.
 *
 * @author  Andy Balaam
 * @version 2
 */
public class FreeGuidePreferencesGroup {

	/** The constructor */
    public FreeGuidePreferencesGroup() {
		
		screen =		new FreeGuidePreferences("screen");
		commandline =	new FreeGuidePreferences("commandline");
		misc =			new FreeGuidePreferences("misc");
		favourites =	new FreeGuidePreferences("favourites");
		chosen_progs =	new FreeGuidePreferences("chosenprogs");
		channels =		new FreeGuidePreferences("channels");
	
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
		
		// Always returns true as assumes nothing can go wrong.  Errors are
		// dealt with gracefully by the Java Preferences implementation.
		return true;
		
	}
	
	// ------------------------------------------------------------------------
	
	public void flushAll() {
		try {
			screen.flush();
			commandline.flush();
			misc.flush();
			favourites.flush();
			chosen_progs.flush();
			channels.flush();
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Substitute any preference values within the string in and return the
	 * result.  Assumes any dates should be today, and any "offset" is 1.
	 */
	public String performSubstitutions(String in) {
		return performSubstitutions(in, GregorianCalendar.getInstance(), 1);
	}
	
	/**
	 * Substitute any preference values within the string in and return the
	 * result.  Replaces "date" with the date given (formatted as
	 * FreeGuideViewer.fileDateFormat) and "offset" with the number given.
	 */
	public String performSubstitutions(String in, Calendar date, int offset) {
		
		if(in==null) {
			return null;
		}
		
		String ans = new String(in);
		
		int i = ans.indexOf('%');
		while(i!=-1) {
			
			int j = ans.indexOf('%', i+1);
			
			// If this was a %% to escape a %, deal with it
			if(j==i+1) {
				ans = ans.substring(0, i) + "---FREEGUIDE_PERCENT---" + ans.substring(j+1);
			} else {
				// Otherwise this is a keyword
				String ref = ans.substring(i+1, j);
				int k = ref.indexOf('.');
				FreeGuidePreferences thePref;
				String theKey;
				if(k==-1) {
					thePref = null;
					theKey = ref;
				} else {
					String node = ref.substring(0, k);
					if(node.equals("screen")) {
						thePref = screen;
					} else if(node.equals("commandline")) {
						thePref = commandline;
					} else if(node.equals("misc")) {
						thePref = misc;
					} else if(node.equals("favourites")) {
						thePref = favourites;
					} else if(node.equals("chosenprogs")) {
						thePref = chosen_progs;
					} else if(node.equals("channels")) {
						thePref = channels;	
					} else {
						thePref = null;
					}
					theKey = ref.substring(k+1);
				}//if
				
				String sub = "";
				
				if(thePref==null) {
					
					if(theKey.toLowerCase().equals("home")) {
						sub = System.getProperty("user.home");
					} else if(theKey.toLowerCase().equals("date")) {
						sub = FreeGuideViewer.fileDateFormat.format(
							date.getTime() );
					} else if(theKey.toLowerCase().equals("offset")) {
						sub = String.valueOf(offset);
					} else {
						sub = "!!!Unknown preference!!!";
					}
					
				} else {
					
					sub = thePref.get(theKey);
				}
				ans = ans.substring(0, i) + sub + ans.substring(j+1);
			}//if
			
			i = ans.indexOf('%');
			
		}//while
		
		// Put the %'s back in
		i = ans.indexOf("---FREEGUIDE_PERCENT---");
		while(i!=-1) {
			
			ans = ans.substring(0, i) + "%" + ans.substring(i+23);
			
			i = ans.indexOf("---FREEGUIDE_PERCENT---");
			
		}
		
		return ans;
		
	}//performSubstitutions
	
	// ------------------------------------------------------------------------
	// Convenience methods
	
	/* Channel stuff has been removed because we no longer need it, but is
	 * only commented in case we want it for channel ordering in the future.
	 */
	
	/**
	 * Get the selected channel IDs.
	 *
	 * @returns an array containing the channel ids of the selected channels.
	 */
	/*public String[] getChannelIDs() {
		
		try {
		
			// If there's a config file for the grabber, get its info
			String grabberConfig = misc.get("grabber_config");
			if( grabberConfig != null) {
				grabberConfig = performSubstitutions(grabberConfig);
				return getChannelIDsFromConfigFile( grabberConfig );
			}
			// Otherwise we'll get it from channelsPrefs
			return channels.noncommentedKeys();
			
		} catch(java.io.IOException e) {
			e.printStackTrace();
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
		
		return new String[0];
		
	}*/
	
	/**
	 * Set the channel list, with commented array containing '#' for an
	 * inactive channel and '' for an active one.
	 */
	/*public void putAllChannelIDs(String[] channelIDs, String[] commented) {
		
		try {
		
			// If there's a config file for the grabber, write to it
			String grabberConfig = misc.get("grabber_config");
			if( grabberConfig != null) {
				grabberConfig = performSubstitutions(grabberConfig);
				putAllChannelIDsToConfigFile( grabberConfig, channelIDs, commented );
				
			} else {	// Otherwise write to the preferences
		
				channels.replaceAll(channelIDs, commented);
				
			}
				
		} catch(java.io.IOException e) {
			e.printStackTrace();
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}//try
		
	}//putChannelIDs*/
	
	/**
	 * Gets a list of all channel IDs, selected or not.  The unselected ones may
	 * be identified in one of two ways: either their returned ID will begin
	 * with "#" or their value on a call of channels.get(channelID, "") will
	 * equal "#".  This reflects the differences between storing our own
	 * channel list or using a grabber config file.
	 *
	 * @returns an array containing the channel ids of all the known channels.
	 */
	/*public String[] getAllChannelIDs() {
		
		try {
		
			// If there's a config file for the grabber, get its info
			String grabberConfig = misc.get("grabber_config");
			if( grabberConfig != null) {
				grabberConfig = performSubstitutions(grabberConfig);
				return getAllChannelIDsFromConfigFile( grabberConfig );
			}
			// Otherwise we'll get it from channelsPrefs
			return channels.keys();
			
		} catch(java.io.IOException e) {
			e.printStackTrace();
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
		
		return new String[0];
		
	}*/

	/**
	 * Has the user chosen any programmes for today?
	 */
	public boolean chosenAnything(Calendar date) {
		String dateStr = "day-" + chosenDateFormat.format(date.getTime());
		return chosen_progs.getBoolean(dateStr, false);
		//date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" + date.get(Calendar.DAY_OF_MONTH), false );
	}
	
	/**
	 * Get a list of all days that have been chosen.
	 */
	public Calendar[] getAllChosenDays() {
		Vector ans = new Vector();
		
		try {
		
			String[] keys = chosen_progs.keys();
		
			for(int i=0;i<keys.length;i++) {
				if(keys[i].startsWith("day-")) {
					Calendar cal = GregorianCalendar.getInstance();
					try {
						cal.setTime(chosenDateFormat.parse(keys[i].substring(4)));
						ans.add(cal);
					} catch(java.text.ParseException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
		
		return FreeGuideUtils.arrayFromVector_Calendar(ans);
		
	}
	
	/**
	 * Remember that we have made a choice for today.
	 */
	public void chosenSomething(Calendar date) {chosenSomething(date, true);}
	public void chosenSomething(Calendar date, boolean yes) {
		String dateStr = "day-" + chosenDateFormat.format(date.getTime());
		if(yes) {
			chosen_progs.putBoolean(dateStr, true);
		} else {
			// Remove the choices for this day
			int[] chosenProgKeys = getChosenProgKeys(date);
			for(int i=0;i<chosenProgKeys.length;i++) {
				chosen_progs.removeChoice(chosenProgKeys[i]);
			}
			// Remove the fact that this day is chosen
			chosen_progs.remove(dateStr);
		}
		//+ date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" + date.get(Calendar.DAY_OF_MONTH), true );
	}
	
	/**
	 * Returns all the choices the user has made for one day.
	 *
	 * NOTE: actually returns all choice _if_ there are any choices for today.
	 *
	 * @return	null if there are no choices for today, or all chosen programmes
	 */
	public Vector getChosenProgs(Calendar date) {
		
		if(!chosenAnything(date)) {
			return null;
		}
		
		Vector ans = new Vector();
		
		int i = 1;
		
		FreeGuideProgramme prog = chosen_progs.getFreeGuideProgramme(String.valueOf(i), null);
		
		while( prog != null ) {
			
			ans.add( prog );
			i++;
			prog = chosen_progs.getFreeGuideProgramme(String.valueOf(i), null);
			
		}
		
		if(ans.size()>0) {
			return ans;
		} else {
			return null;
		}
		
	}
	
	/**
	 * Returns the keys of all the choices the user has made for one day.
	 *
	 * NOTE: this returns programmes actually on that date, not adjusted for
	 * the day_start_time parameter.
	 *
	 * @return	null if there are no choices for today, or all chosen programmes
	 */
	public int[] getChosenProgKeys(Calendar date) {
		
		Vector ans = new Vector();
		
		if(chosenAnything(date)) {
			
			int i = 1;
			FreeGuideProgramme prog = chosen_progs.getFreeGuideProgramme(String.valueOf(i), null);
		
			while( prog != null ) {
	
				Calendar progStart = prog.getStart();
			
				if( (progStart.YEAR == date.YEAR) && (progStart.DAY_OF_YEAR == date.DAY_OF_YEAR) ) {
					ans.add( new Integer(i) );
					i++;
					prog = chosen_progs.getFreeGuideProgramme(String.valueOf(i), null);	
				}
			
			}
		
		}
			
		return FreeGuideUtils.arrayFromVector_int(ans);
		
	}
	
	public void addChoice(FreeGuideProgramme prog) {
		if(!chosenAnything(prog.getStart())) {
			chosenSomething(prog.getStart());
		}
		chosen_progs.appendFreeGuideProgramme(prog);
	}
	
	public void removeChoice(FreeGuideProgramme prog) {
		
		int i = chosen_progs.findFreeGuideProgramme(prog);
		
		if(i!=-1) {
			chosen_progs.removeChoice(i);	
		}
		
	}
		
	public FreeGuideFavourite[] getFavourites() {
		
		Vector ans = new Vector();
		
		int i = 1;
		FreeGuideFavourite fav = favourites.getFreeGuideFavourite(String.valueOf(i), null);
		
		while( fav != null ) {

			ans.add( fav );			
			i++;
			fav = favourites.getFreeGuideFavourite(String.valueOf(i), null);
		}
		
		return FreeGuideUtils.arrayFromVector_FreeGuideFavourite(ans);
		
	}
	
	public void replaceFavourites(FreeGuideFavourite[] favs) {
		
		try {
		
			int size = favs.length;
			String[] keys = new String[size];
			for(int i=0;i<size;i++) {
				keys[i] = String.valueOf(i+1);
			}
			favourites.replaceAllFreeGuideFavourite(keys, favs);
			
		} catch(java.util.prefs.BackingStoreException e) {
			e.printStackTrace();
		}
		
	}
	
	public String[] getCommands(String key) {
		
		return commandline.getStrings(key);
		
	}
	
	// ------------------------------------------------------------------------
	// Private functions
	
	/**
	 * Gets all the lines from the config file that start with "channel", and
	 * strips off the "channel " or "channel: " and returns the rest.
	 */
	/*private String[] getChannelIDsFromConfigFile( String confFilename ) throws java.io.IOException {
		
		Vector ans = new Vector();
	
		BufferedReader buffy = new BufferedReader(new FileReader(confFilename));
		
		String line = buffy.readLine();
		while(line != null) {
			
			line = commentTrim(line);
			
			if(line.startsWith("channel")) {
				ans.add(getChannelIDFromConfigLine(line));
			}
		
			line = buffy.readLine();
		}
		
		buffy.close();
		
		return FreeGuideUtils.arrayFromVector_String(ans);
		
	}*/
	
	/**
	 * Gets all the lines from the config file that start with "channel"  or
	 * "#channel", strips off the "channel" bit and returns the rest.
	 */
	/*private String[] getAllChannelIDsFromConfigFile( String confFilename ) throws java.io.IOException {
		
		Vector ans = new Vector();
	
		BufferedReader buffy = new BufferedReader(new FileReader(confFilename));
		
		String line = buffy.readLine();
		while(line != null) {
			
			if(line.startsWith("channel")) {
				line = commentTrim(line);
				ans.add(getChannelIDFromConfigLine(line));
			} else if(line.startsWith("#channel")) {
				line = commentTrim(line.substring(1));
				ans.add("#"+getChannelIDFromConfigLine(line));
			}
		
			line = buffy.readLine();
		}
		
		buffy.close();
		
		return FreeGuideUtils.arrayFromVector_String(ans);
		
	}*/
	
	/*private String commentTrim(String ans) {
		
		//String ans = new String(in);
		
		// Get rid of comments
		int i = ans.indexOf('#');
		if(i!=-1) {
			ans = ans.substring(0, i);
		}

		// Trim and return
		return ans.trim();
		
	}*/
	
	/*private String getChannelIDFromConfigLine(String line) {
		int i = line.indexOf(' ');		
		if(i!=-1) {
			return(line.substring(i+1));		
		}
		return "";
		
	}*/
	
	/**
	 * Parse the given config file to comment out channels we don't want, and
	 * add ones we do.
	 */
	/*private void putAllChannelIDsToConfigFile( String confFilename, String[] channelIDs, String[] commented ) throws java.io.IOException {
		
		Vector oldConfigFile = new Vector();	// Holds the old config file
		String channelPrefix = null;
		
		BufferedReader buffy = new BufferedReader(new FileReader(confFilename));
		String line = buffy.readLine();
		while(line != null) {
			
			String trimmed = line.trim();
			if(trimmed.startsWith("channel") || trimmed.startsWith("#channel")) {
				if(channelPrefix == null) {
					int i = trimmed.indexOf(' ');
					if(i==-1) { i=trimmed.length(); }
					if(trimmed.charAt(0)=='#') {
						channelPrefix = trimmed.substring( 1, i );
					} else {
						channelPrefix = trimmed.substring( 0, i );
					}
				}
			} else {
				oldConfigFile.add(line);
			}
			
			line = buffy.readLine();
		}
		
		buffy.close();
		
		// If we didn't find any channels, guess the prefix
		if(channelPrefix == null) {
			channelPrefix = "channel";
		}
		
		String lb = System.getProperty("line.separator");
		BufferedWriter buffyw = new BufferedWriter(new FileWriter(confFilename));
		
		// Write out the rest of the config file
		for(int i=0;i<oldConfigFile.size();i++) {
			buffyw.write((String)(oldConfigFile.get(i)) + lb);
		}
		
		// Then write out the channels found in the file
		for(int i=0;i<channelIDs.length;i++) {
			buffyw.write( commented[i] + channelPrefix + " " + channelIDs[i] + lb);	
		}
		
		buffyw.close();
		
	}*/
	
	// ------------------------------------------------------------------------
	
	public FreeGuidePreferences screen;		// The screen dimensions etc.
	public FreeGuidePreferences commandline;// Grabber commands and options
	public FreeGuidePreferences misc;		// Other prefs
	public FreeGuidePreferences favourites;	// The user's favourite progs
	public FreeGuidePreferences chosen_progs;// The selected progs
	public FreeGuidePreferences channels;	// The selected channels
	
	private static final SimpleDateFormat chosenDateFormat = new SimpleDateFormat("yyyyMMdd");
	
}
