/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
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
 *  FreeGuidePreferences A sort of wrapper around the
 *  java.util.prefs.Preferences class
 *  to extend its capabilities in ways useful for FreeGuide. This includes
 *  accessor functions for objects such as Dates, FreeGuideTimes and even
 *  complex ones like FreeGuideFavourites. The way this is implemented is with
 *  keys structured as dot-separated lists (e.g. 1.name, 1.after_time, 2.name,
 *  2.after_time etc. for favourites and tv_grab.1, tv_grab.2 etc. for
 *  commandline options.)
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    5
 */

public class FGPreferences {

    /**
     *  Creates a new instance of FreeGuidePreferences, putting the information
     *  in the user node "/org/freeguide-tv/" + subNode
     *
     *@param  subNode  Description of the Parameter
     */
    public FGPreferences(String subNode) {

        prefs = Preferences.userRoot().node("/org/freeguide-tv/" + subNode);

    }


    // ------------------------------------------------------------------------
    // Convenience methods

    /**
     *  Return all keys that are not commented out with a # as the name.
     *
     *@return                            Description of the Return Value
     *@exception  BackingStoreException  Description of the Exception
     */
    public String[] noncommentedKeys() throws BackingStoreException {

        String[] allKeys = keys();
        Vector ans = new Vector();

        for (int i = 0; i < allKeys.length; i++) {
            // If not commented, remember it
            if (!get(allKeys[i]).equals("#")) {
                ans.add(allKeys[i]);
            }
        }

        return Utils.arrayFromVector_String(ans);
    }


    /**
     *  Remove all key-value pairs in this node and replace them with the keys
     *  in keys and the values in values.
     *
     *@param  keys                       Description of the Parameter
     *@param  values                     Description of the Parameter
     *@exception  BackingStoreException  Description of the Exception
     */
    public void replaceAll(String[] keys, String[] values) throws BackingStoreException {
        clear();
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], values[i]);
        }
    }


    /**
     *  Remove all key-value pairs in this node and replace them with the keys
     *  in keys and the FreeGuideFavourites in values.
     *
     *@param  keys                       Description of the Parameter
     *@param  values                     Description of the Parameter
     *@exception  BackingStoreException  Description of the Exception
     */
    public void replaceAllFavourites(String[] keys, Favourite[] values) throws BackingStoreException {
        clear();
        for (int i = 0; i < keys.length; i++) {
            putFavourite(keys[i], values[i]);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  keys                       Description of the Parameter
     *@param  values                     Description of the Parameter
     *@exception  BackingStoreException  Description of the Exception
     */
    public void replaceAllChannelSets(String[] keys, ChannelSetInterface[] values) throws BackingStoreException {
        clear();
        for (int i = 0; i < keys.length; i++) {
            putChannelSet(keys[i], values[i]);
        }
    }


    /**
     *  Adds the given FreeGuideProgramme to the end of the list stored in this
     *  node. See the notes for getProgramme about the amount of
     *  information stored.
     *
     *@param  prog  Description of the Parameter
     */
    public void appendProgramme(Programme prog) {

        // Find the first unoccupied spot
        int i = 1;
        String title = get(i + ".title");
        while (title != null) {
            i++;
            title = get(i + ".title");
        }

        // And put our programme in it
        put(i + ".title", prog.getTitle());
        putDate(i + ".start", prog.getStart().getTime());
        put(i + ".channel_id", prog.getChannelID());

    }


    /**
     *  Adds the given FreeGuideFavourite to the end of the list stored in this
     *  node.
     *
     *@param  fav  Description of the Parameter
     */
    public void appendFavourite(Favourite fav) {

        // Find the first unoccupied spot
        int i = 1;
        String title = get(i + ".name");
        while (title != null) {
            i++;
            title = get(i + ".name");
        }

        putFavourite(String.valueOf(i), fav);

    }


    /**
     *  Description of the Method
     *
     *@param  prog  Description of the Parameter
     *@return       Description of the Return Value
     */
    public int findProgramme(Programme prog) {

        int i = 1;

        String title = get(i + ".title");

        while (title != null) {

            String startKey = i + ".start";
            String channelIDKey = i + ".channel_id";

            if (title.equals(prog.getTitle()) &&
                    getDate(startKey, null).equals(prog.getStart().getTime()) &&
                    get(channelIDKey, "").equals(prog.getChannelID())) {

                // If this is the programme, return
                return i;
            }

            // Otherwise look at the next one
            i++;
            title = get(i + ".title");

        }

        // Nothing found: return -1
        return -1;
    }


    /**
     *  Remove a choice of programme from the list of choices
     *
     *@param  index  The index number in the list of the programme to remove
     */
    public void removeChoice( int index ) {

		//FreeGuide.log.info( "begin" );
		
        // Remove these even though they will be over-written in a minute
        // (just for neatness)
        remove(index + ".title");
        remove(index + ".start");
        remove(index + ".channel_id");

        // Shift everything down
        index++;
        String title = get(index + ".title");
        while (title != null) {

            put((index - 1) + ".title", title);
            putDate((index - 1) + ".start", getDate(index + ".start", null));
            put((index - 1) + ".channel_id", get(index + ".channel_id", ""));

            index++;
            title = get(index + ".title");

        }

        // Remove the last one in the list
        // (so that it's 1 shorter than what we started with)
        remove((index - 1) + ".title");
        remove((index - 1) + ".start");
        remove((index - 1) + ".channel_id");

		//FreeGuide.log.info( "end" );
		
    }

	
	/**
     *  Remove some chosen programmes from the list of choices
     *
     *@param  indices  The indices of the programmes to remove
     */
    public void removeChoices( int[] indices ) {

		//FreeGuide.log.info( "indices.length=" + indices.length );
		
		Vector oldIndices = new Vector();
		Vector newTitles = new Vector();
		Vector newStarts = new Vector();
		Vector newChannelIDs = new Vector();
		
		// Get the indices into a vector
		for( int i = 0; i<indices.length; i++ ) {
			
			oldIndices.add( new Integer( indices[i] ) );
			
		}
		
		String title;
		int index;
		
		index = 1;
		title = get( index + ".title" );
		while( title != null ) {
			
			// Unless this index is to be deleted, add this choice to new vector
			if( oldIndices.indexOf( new Integer(index) ) == -1 ) {
			
				newTitles.add( title );
				newStarts.add( getDate( index + ".start", null ) );
				newChannelIDs.add( get( index + ".channel_id", "" ) );
				
			}
			
			// And delete it from the preferences
			remove( index + ".title" );
			remove( index + ".start" );
			remove( index + ".channel_id" );
			
			index++;
			title = get( index + ".title" );
			
		}
		
		for( int i=0; i<newTitles.size(); i++ ) {
			
			put( (i+1) + ".title", (String)newTitles.get(i) );
			putDate( (i+1) + ".start", (Date)newStarts.get(i) );
			put( (i+1) + ".channel_id", (String)newChannelIDs.get(i) );
			
		}
		
    }

    /**
     *  Description of the Method
     *
     *@param  index  Description of the Parameter
     */
    public void removeFavourite(int index) {

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
        String name = get(index + ".name");
        while (name != null) {

            put((index - 1) + ".name", get(index + ".name"));
            put((index - 1) + ".title_string", get(index + ".title_string"));
            put((index - 1) + ".title_regex", get(index + ".title_regex"));
            put((index - 1) + ".channel_id", get(index + ".channel_id"));
            put((index - 1) + ".after_time", get(index + ".after_time"));
            put((index - 1) + ".before_time", get(index + ".before_time"));
            put((index - 1) + ".day_of_week", get(index + ".day_of_week"));

            index++;
            name = get(index + ".name");

        }

        // Remove the last one in the list
        // (so that it's 1 shorter than what we started with)
        remove((index - 1) + ".name");
        remove((index - 1) + ".title_string");
        remove((index - 1) + ".title_regex");
        remove((index - 1) + ".channel_id");
        remove((index - 1) + ".after_time");
        remove((index - 1) + ".before_time");
        remove((index - 1) + ".day_of_week");

    }


    /**
     *  Return an array of strings with keys key+".1", key+".2" etc.
     *
     *@param  key  Description of the Parameter
     *@return      The strings value
     */
    public String[] getStrings(String key) {

        Vector ans = new Vector();

        int i = 1;
        String line = get(key + "." + i);

        while (line != null) {
            ans.add(line);
            i++;
            line = get(key + "." + i);
        }

        return Utils.arrayFromVector_String(ans);
    }


    /**
     *  Description of the Method
     *
     *@param  key     Description of the Parameter
     *@param  values  Description of the Parameter
     */
    public void putStrings(String key, String[] values) {

        int i;
        for (i = 0; i < values.length; i++) {

            put(key + "." + (i + 1), values[i]);

        }

        while (get(key + "." + (i + 1), null) != null) {
            remove(key + "." + (i + 1));
            i++;
        }

    }


    // ------------------------------------------------------------------------

    /**
     *  Performs a get using a default value of null
     *
     *@param  key  Description of the Parameter
     *@return      Description of the Return Value
     */
    public String get(String key) {
        return get(key, null);
    }


    // ------------------------------------------------------------------------
    // Special pseudo-wrappers

    /**
     *  Gets the integer attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The integer value
     */
    public Integer getInteger(String key, Integer def) {
        String ans = get(key);
        if (ans == null) {
            return def;
        } else {
            return Integer.valueOf(ans);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putInteger(String key, Integer value) {
        if (value != null) {
            putInt(key, value.intValue());
        } else {
            remove(key);
        }
    }


    /**
     *  Gets the date attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The date value
     */
    public Date getDate(String key, Date def) {
        String ans = get(key);
        if (ans == null) {
            return def;
        } else {
            return new Date(getLong(key, 0));
        }
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putDate(String key, Date value) {
        if (value != null) {
            putLong(key, value.getTime());
        } else {
            remove(key);
        }
    }


    /**
     *  Gets the color attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The color value
     */
    public Color getColor(String key, Color def) {
        int r = getInt(key + ".r", def.getRed());
        int g = getInt(key + ".g", def.getGreen());
        int b = getInt(key + ".b", def.getBlue());
        return new Color(r, g, b);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putColor(String key, Color value) {

        if (value != null) {
            putInt(key + ".r", value.getRed());
            putInt(key + ".g", value.getGreen());
            putInt(key + ".b", value.getBlue());
        } else {
            remove(key + ".r");
            remove(key + ".g");
            remove(key + ".b");
        }
    }

    /**
     *  Gets the pattern attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The pattern value
     */
    public Pattern getPattern(String key, Pattern def) {
        String ans = get(key);
        if (ans == null) {
            return def;
        } else {
            return Pattern.compile(ans);
        }
    }

    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putPattern(String key, Pattern value) {
        if (value != null) {
            put(key, value.pattern());
        } else {
            remove(key);
        }
    }


    /**
     *  Gets the freeGuideTime attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@return      The freeGuideTime value
     */
    public Time getTime(String key) {
        return getTime(key, null);
    }


    /**
     *  Gets the freeGuideTime attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The freeGuideTime value
     */
    public Time getTime(String key, Time def) {
        String ans = get(key);
        if (ans == null) {
            return def;
        } else {
            return new Time(ans);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putTime(String key, Time value) {
        if (value != null) {
            put(key, value.getHHMMString());
        } else {
            remove(key);
        }
    }


    /**
     *  Gets enough info from the preferences to identify a programme reliably.
     *  Note: does not store all the info about a programme, just enough to
     *  identify it i.e. title, start time, channel id.
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The freeGuideProgramme value
     */
    public Programme getProgramme(String key, Programme def) {

        String title = get(key + ".title");

        // If the title isn't there, the programme isn't there (by convention)
        if (title == null) {
            return def;
        } else {
            Programme prog = new Programme();
            prog.setTitle(title);

            Calendar cal = GregorianCalendar.getInstance();
            cal.setTime(getDate(key + ".start", null));
            prog.setStart(cal);

            prog.setChannelID(get(key + ".channel_id", ""));

            return prog;
        }

    }


    /**
     *  Gets a FreeGuideFavourite object from this preferences store.
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The freeGuideFavourite value
     */
    public Favourite getFavourite(String key, Favourite def) {

        String name = get(key + ".name");

        // If the title isn't there, the programme isn't there (by convention)
        if (name == null) {
            return def;
        } else {
            Favourite fav = new Favourite();

            fav.setName(name);
            fav.setTitleString(get(key + ".title_string"));
            fav.setTitleContains(get(key + ".title_contains"));
            fav.setTitleRegex(getPattern(key + ".title_regex", null));
            fav.setChannelID(get(key + ".channel_id"));
            fav.setAfterTime(getTime(key + ".after_time", null));
            fav.setBeforeTime(getTime(key + ".before_time", null));
            fav.setDayOfWeek(getInteger(key + ".day_of_week", null));

            return fav;
        }
    }


    /**
     *  Puts a FreeGuideFavourite object into this preferences store.
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putFavourite(String key, Favourite value) {

        put(key + ".name", value.getName());
        put(key + ".title_string", value.getTitleString());
        put(key + ".title_contains", value.getTitleContains());
        putPattern(key + ".title_regex", value.getTitleRegex());
        put(key + ".channel_id", value.getChannelID());
        putTime(key + ".after_time", value.getAfterTime());
        putTime(key + ".before_time", value.getBeforeTime());
        putInteger(key + ".day_of_week", value.getDayOfWeek());

    }


    /**
     *  
     *
     *@param  key  
     *@param  def  
     *@return      
     */
    public ChannelSet getChannelSet(String key, ChannelSet def) {

        String name = get(key + ".name");

        if (name == null) {
            return def;
        } else {
            ChannelSet cset = new ChannelSet();

            cset.setChannelSetName(name);

            String ids = get(key + ".channelids");
            if (ids != null) {
                cset.addChannelsFromString(ids, null);
            } else {
                return def;
            }
            return cset;
        }
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putChannelSet(String key, ChannelSetInterface value) {
        put(key + ".name", value.getChannelSetName());
        put(key + ".channelids", ChannelSet.toString(value.getChannelIDs()));
    }


    // ------------------------------------------------------------------------
    // Wrapper methods

    /**
     *  Description of the Method
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      Description of the Return Value
     */
    public String get(String key, String def) {
        return prefs.get(key, def);
    }


    /**
     *  Gets the boolean attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The boolean value
     */
    public boolean getBoolean(String key, boolean def) {
        return prefs.getBoolean(key, def);
    }


    /**
     *  Gets the byteArray attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The byteArray value
     */
    public byte[] getByteArray(String key, byte[] def) {
        return prefs.getByteArray(key, def);
    }


    /**
     *  Gets the double attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The double value
     */
    public double getDouble(String key, double def) {
        return prefs.getDouble(key, def);
    }


    /**
     *  Gets the float attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The float value
     */
    public float getFloat(String key, float def) {
        return prefs.getFloat(key, def);
    }


    /**
     *  Gets the int attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The int value
     */
    public int getInt(String key, int def) {
        return prefs.getInt(key, def);
    }


    /**
     *  Gets the long attribute of the FreeGuidePreferences object
     *
     *@param  key  Description of the Parameter
     *@param  def  Description of the Parameter
     *@return      The long value
     */
    public long getLong(String key, long def) {
        return prefs.getLong(key, def);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void put(String key, String value) {
        if (value != null) {
            prefs.put(key, value);
        } else {
            remove(key);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putBoolean(String key, boolean value) {
        prefs.putBoolean(key, value);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putByteArray(String key, byte[] value) {
        prefs.putByteArray(key, value);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putDouble(String key, double value) {
        prefs.putDouble(key, value);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putFloat(String key, float value) {
        prefs.putFloat(key, value);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putInt(String key, int value) {
        prefs.putInt(key, value);
    }


    /**
     *  Description of the Method
     *
     *@param  key    Description of the Parameter
     *@param  value  Description of the Parameter
     */
    public void putLong(String key, long value) {
        prefs.putLong(key, value);
    }


    /**
     *  Description of the Method
     *
     *@exception  BackingStoreException  Description of the Exception
     */
    public void clear() throws BackingStoreException {
        prefs.clear();
    }


    /**
     *  Description of the Method
     *
     *@exception  BackingStoreException  Description of the Exception
     */
    public void flush() throws BackingStoreException {
        prefs.flush();
    }


    /**
     *  Description of the Method
     *
     *@param  key  Description of the Parameter
     */
    public void remove(String key) {
        prefs.remove(key);
    }


    /**
     *  Description of the Method
     *
     *@return                            Description of the Return Value
     *@exception  BackingStoreException  Description of the Exception
     */
    public String[] keys() throws BackingStoreException {
        return prefs.keys();
    }


    /**
     *  Description of the Method
     *
     *@exception  BackingStoreException  Description of the Exception
     */
    public void sync() throws BackingStoreException {
        prefs.sync();
    }


    /**
     *  Description of the Method
     *
     *@param  os                         Description of the Parameter
     *@exception  java.io.IOException    Description of the Exception
     *@exception  BackingStoreException  Description of the Exception
     */
    public void exportNode(OutputStream os) throws java.io.IOException, BackingStoreException {
        prefs.exportNode(os);
    }


    /**
     *  Description of the Method
     *
     *@param  is                                     Description of the
     *      Parameter
     *@exception  java.io.IOException                Description of the
     *      Exception
     *@exception  BackingStoreException              Description of the
     *      Exception
     *@exception  InvalidPreferencesFormatException  Description of the
     *      Exception
     */
    public void importPreferences(InputStream is) throws java.io.IOException, BackingStoreException, InvalidPreferencesFormatException {
        prefs.importPreferences(is);
    }


    // BEANO - 22/08/03

    /**
     * Updates a boolean preference if the value of the preference has changed from
     * that already stored. Returns a boolean value to indicate if the value has been 
     * updated.
     *
     * @param  _key    The name of the preference to update
     * @param  value  The <code>boolean</code> value to set
     * @return Returns <code>true</code> if the values has been 
     *      updated, otherwise returns <code>false</code>.
     */
    public boolean updateBoolean(String _key, boolean value) {
        /*boolean changed = false;
        boolean defaultValue = false;
        
        boolean temp = getBoolean(_key, defaultValue);
        if (temp != value) {
            changed = true;
            putBoolean(_key, value);                
        }
        
        return changed;*/
		
		return update( _key, value ? "true" : "false" );
		
    }

	/**
     * Updates a time preference if the value of the preference has changed from
     * that already stored. Returns a boolean value to indicate if the value
	 * has been updated.
     *
     * @param  _key    The name of the preference to update
     * @param  value  The <code>Time</code> value to set
     * @return Returns <code>true</code> if the value has been 
     *      updated, otherwise returns <code>false</code>.
     */
    public boolean updateTime(String _key, Time value) {
        /*boolean changed = false;        
        Time defaultValue = new Time();
        
        Time temp = getTime(_key, defaultValue);
        if (!temp.equals(value)) {
            changed = true;
            putTime(_key, value);                
        }
        
        return changed;*/
		
		return update( _key, value.getHHMMString() );
		
    }
	
    /**
     * Updates a colour preference if the value of the preference has changed
	 * from that already stored. Returns a boolean value to indicate if the
	 * value has been updated.
     *
     * @param  _key    The name of the preference to update
     * @param  value  The <code>Color</code> value to set
     * @return Returns <code>true</code> if the value has been 
     *      updated, otherwise returns <code>false</code>.
     */
    public boolean updateColor(String _key, Color value) {
		boolean changed = false;        
        
        String temp = get(_key);
        
        if (temp == null ) {
            changed = true;
            putColor(_key, value);               

        }
		
		if( !temp.equals(value) ) {
            changed = true;
            putColor(_key, value);               
        }
        
        return changed;
		
    }

    /**
     * Updates an int preference if the value of the preference has changed from
     * that already stored. Returns a boolean value to indicate if the value
	 * has been updated.
     *
     * @param  _key    The name of the preference to update
     * @param  value  The <code>int</code> value to set
     * @return Returns <code>true</code> if the value has been 
     *      updated, otherwise returns <code>false</code>.
     */
    public boolean updateInt(String _key, int value) {
        /*boolean changed = false;        
        int defaultValue = -1;
        
        int temp = getInt(_key, defaultValue);
        if (temp != value) {
            changed = true;
            putInt(_key, value);               
        }
        
        return changed;*/
		
		return update( _key, String.valueOf( value ) );
    }

    /**
     * Updates a string preference if the value of the preference has changed from
     * that already stored. Returns a boolean value to indicate if the value has been 
     * updated.
     *
     * @param  _key    The name of the preference to update
     * @param  value  The <code>String</code> value to set
     * @return Returns <code>true</code> if the value has been 
     *      updated, otherwise returns <code>false</code>.
     */
    public boolean update(String _key, String value) {
        boolean changed = false;        
        
        String temp = get(_key);
        
        if (temp == null ) {
            if (value != null) {
                changed = true;
                put(_key, value);               
            }

        } else if (value == null || !temp.equals(value)) {
            changed = true;
            put(_key, value);               
        }
        
        return changed;
    }



    // ------------------------------------------------------------------------

    private Preferences prefs;

}



