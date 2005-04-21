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
package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVProgramme;
import freeguide.lib.fgspecific.selection.Favourite;

import freeguide.lib.general.Time;
import freeguide.lib.general.Utils;

import java.awt.Color; // Not * since List conflicts with util.List

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * FreeGuidePreferences A sort of wrapper around the
 * java.util.prefs.Preferences class to extend its capabilities in ways
 * useful for FreeGuide. This includes accessor functions for objects such as
 * Dates, FreeGuideTimes and even complex ones like FreeGuideFavourites. The
 * way this is implemented is with keys structured as dot-separated lists
 * (e.g. 1.name, 1.after_time, 2.name, 2.after_time etc. for favourites and
 * tv_grab.1, tv_grab.2 etc. for commandline options.)
 *
 * @author Andy Balaam
 * @version 6
 */
public class FGPreferences
{

    // ------------------------------------------------------------------------
    private Preferences prefs;
    private Vector listeners;
    private boolean log_prefs;
    private String subNode;

    /**
     * Creates a new instance of FreeGuidePreferences, putting the information
     * in the user node "/org/freeguide-tv/" + subNode
     *
     * @param subNode Description of the Parameter
     * @param log_prefs DOCUMENT ME!
     */
    public FGPreferences( String subNode, boolean log_prefs )
    {
        this.subNode = subNode;

        this.log_prefs = log_prefs;

        prefs =
            Preferences.userRoot(  ).node( "/org/freeguide-tv/" + subNode );

        listeners = new Vector(  );

    }

    // ------------------------------------------------------------------------
    // Convenience methods

    /**
     * Return all keys that are not commented out with a # as the name.
     *
     * @return Description of the Return Value
     *
     * @exception BackingStoreException Description of the Exception
     */
    public String[] noncommentedKeys(  ) throws BackingStoreException
    {

        String[] allKeys = keys(  );

        Vector ans = new Vector(  );

        for( int i = 0; i < allKeys.length; i++ )
        {

            // If not commented, remember it
            if( !get( allKeys[i] ).equals( "#" ) )
            {
                ans.add( allKeys[i] );

            }
        }

        return Utils.arrayFromVector_String( ans );

    }

    /**
     * Remove all key-value pairs in this node and replace them with the keys
     * in keys and the values in values.
     *
     * @param keys Description of the Parameter
     * @param values Description of the Parameter
     *
     * @exception BackingStoreException Description of the Exception
     */
    public void replaceAll( String[] keys, String[] values )
        throws BackingStoreException
    {
        clear( false );

        for( int i = 0; i < keys.length; i++ )
        {
            put( keys[i], values[i], false );

        }

        notifyListeners( "-all-", "replaceAll" );

    }

    /**
     * Remove all key-value pairs in this node and replace them with the keys
     * in keys and the FreeGuideFavourites in values.
     *
     * @param keys Description of the Parameter
     * @param values Description of the Parameter
     *
     * @exception BackingStoreException Description of the Exception
     */
    public void replaceAllFavourites( String[] keys, List values )
        throws BackingStoreException
    {
        clear( false );

        Iterator favouritesIterator = values.iterator(  );

        int i = 0;

        while( favouritesIterator.hasNext(  ) )
        {
            putFavourite( 
                keys[i++], (Favourite)favouritesIterator.next(  ), false );

        }

        notifyListeners( "-all-", "replaceAllFavourites" );

    }

    /**
     * Description of the Method
     *
     * @param keys Description of the Parameter
     * @param values Description of the Parameter
     *
     * @exception BackingStoreException Description of the Exception
     */
    public void replaceAllChannelSets( 
        String[] keys, ChannelSetInterface[] values )
        throws BackingStoreException
    {
        clear( false );

        for( int i = 0; i < keys.length; i++ )
        {
            putChannelSet( keys[i], values[i], false );

        }

        notifyListeners( "-all-", "replaceAllFavourites" );

    }

    /**
     * Adds the given FreeGuideProgramme to the end of the list stored in this
     * node. See the notes for getProgramme about the amount of information
     * stored.
     *
     * @param prog Description of the Parameter
     */
    public void appendProgramme( TVProgramme prog )
    {

        // Find the first unoccupied spot
        int i = 1;

        String title = get( i + ".title" );

        while( title != null )
        {
            i++;

            title = get( i + ".title" );

        }

        // And put our programme in it
        put( i + ".title", prog.getTitle(  ), false );

        putDate( i + ".start", new Date( prog.getStart(  ) ), false );

        put( i + ".channel_id", prog.getChannel(  ).getID(  ), false );

        notifyListeners( String.valueOf( i ), prog );

    }

    /**
     * Adds the given FreeGuideFavourite to the end of the list stored in this
     * node.
     *
     * @param fav Description of the Parameter
     */
    public void appendFavourite( Favourite fav )
    {

        // Find the first unoccupied spot
        int i = 1;

        String title = get( i + ".name" );

        while( title != null )
        {
            i++;

            title = get( i + ".name" );

        }

        putFavourite( String.valueOf( i ), fav );

    }

    /**
     * Return the index number of a programme, if it is stored in the
     * preferences, or -1 if it is not.  Uses the title, start time and
     * channel id to identify a match.
     *
     * @param prog The programme to search for
     *
     * @return The index number of the programme, or -1 if not found
     */
    public int findProgramme( TVProgramme prog )
    {

        int i = 1;

        String title = get( i + ".title" );

        while( title != null )
        {

            String startKey = i + ".start";

            String channelIDKey = i + ".channel_id";

            if( 
                title.equals( prog.getTitle(  ) )
                    && getDate( startKey, null ).equals( 
                        new Date( prog.getStart(  ) ) )
                    && get( channelIDKey, "" ).equals( 
                        prog.getChannel(  ).getID(  ) ) )
            {

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

    /**
     * Remove a choice of programme from the list of choices
     *
     * @param index The index number in the list of the programme to remove
     */
    public void removeFromGuide( int index )
    {

        int orig_index = index;

        // Shift everything down
        index++;

        String title = get( index + ".title" );

        while( title != null )
        {
            put( ( index - 1 ) + ".title", title, false );

            putDate( 
                ( index - 1 ) + ".start", getDate( index + ".start", null ),
                false );

            put( 
                ( index - 1 ) + ".channel_id", get( index + ".channel_id", "" ),
                false );

            index++;

            title = get( index + ".title" );

        }

        // Remove the last one in the list
        // (so that it's 1 shorter than what we started with)
        remove( ( index - 1 ) + ".title", false );

        remove( ( index - 1 ) + ".start", false );

        remove( ( index - 1 ) + ".channel_id", false );

        notifyListeners( String.valueOf( orig_index ), null );

    }

    /**
     * Remove some chosen programmes from the list of choices
     *
     * @param indices The indices of the programmes to remove
     */
    public void removeFromGuides( int[] indices )
    {

        Vector oldIndices = new Vector(  );

        Vector newTitles = new Vector(  );

        Vector newStarts = new Vector(  );

        Vector newChannelIDs = new Vector(  );

        // Get the indices into a vector
        for( int i = 0; i < indices.length; i++ )
        {
            oldIndices.add( new Integer( indices[i] ) );

        }

        String title;

        int index;

        index = 1;

        title = get( index + ".title" );

        while( title != null )
        {

            // Unless this index is to be deleted, add this choice to new vector
            if( oldIndices.indexOf( new Integer( index ) ) == -1 )
            {
                newTitles.add( title );

                newStarts.add( getDate( index + ".start", null ) );

                newChannelIDs.add( get( index + ".channel_id", "" ) );

            }

            // And delete it from the preferences
            remove( index + ".title", false );

            remove( index + ".start", false );

            remove( index + ".channel_id", false );

            index++;

            title = get( index + ".title" );

        }

        for( int i = 0; i < newTitles.size(  ); i++ )
        {
            put( ( i + 1 ) + ".title", (String)newTitles.get( i ), false );

            putDate( ( i + 1 ) + ".start", (Date)newStarts.get( i ), false );

            put( 
                ( i + 1 ) + ".channel_id", (String)newChannelIDs.get( i ),
                false );

        }

        notifyListeners( "-several-", null );

    }

    /**
     * Description of the Method
     *
     * @param index Description of the Parameter
     */
    public void removeFavourite( int index )
    {

        int orig_index = index;

        // Shift everything down
        index++;

        String name = get( index + ".name" );

        while( name != null )
        {
            put( ( index - 1 ) + ".name", get( index + ".name" ), false );

            put( 
                ( index - 1 ) + ".title_string", get( index + ".title_string" ),
                false );

            put( 
                ( index - 1 ) + ".title_contains",
                get( index + ".title_contains" ), false );

            put( 
                ( index - 1 ) + ".title_regex", get( index + ".title_regex" ),
                false );

            put( 
                ( index - 1 ) + ".channel_id", get( index + ".channel_id" ),
                false );

            put( 
                ( index - 1 ) + ".after_time", get( index + ".after_time" ),
                false );

            put( 
                ( index - 1 ) + ".before_time", get( index + ".before_time" ),
                false );

            put( 
                ( index - 1 ) + ".day_of_week", get( index + ".day_of_week" ),
                false );

            index++;

            name = get( index + ".name" );

        }

        // Remove the last one in the list
        // (so that it's 1 shorter than what we started with)
        remove( ( index - 1 ) + ".name", false );

        remove( ( index - 1 ) + ".title_string", false );

        remove( ( index - 1 ) + ".title_contains", false );

        remove( ( index - 1 ) + ".title_regex", false );

        remove( ( index - 1 ) + ".channel_id", false );

        remove( ( index - 1 ) + ".after_time", false );

        remove( ( index - 1 ) + ".before_time", false );

        remove( ( index - 1 ) + ".day_of_week", false );

        notifyListeners( "" + orig_index, null );

    }

    /**
     * Return an array of strings with keys key+".1", key+".2" etc.
     *
     * @param key Description of the Parameter
     *
     * @return The strings value
     */
    public String[] getStrings( String key )
    {

        Vector ans = new Vector(  );

        int i = 1;

        String line = get( key + "." + i );

        while( line != null )
        {
            ans.add( line );

            i++;

            line = get( key + "." + i );

        }

        return Utils.arrayFromVector_String( ans );

    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param values Description of the Parameter
     */
    public void putStrings( String key, String[] values )
    {

        int i;

        for( i = 0; i < values.length; i++ )
        {
            put( key + "." + ( i + 1 ), values[i], false );

        }

        while( get( key + "." + ( i + 1 ), null ) != null )
        {
            remove( key + "." + ( i + 1 ), false );

            i++;

        }

        notifyListeners( key, values );

    }

    // ------------------------------------------------------------------------

    /**
     * Performs a get using a default value of null
     *
     * @param key Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public String get( String key )
    {

        return prefs.get( key, null );

    }

    // ------------------------------------------------------------------------
    // Special pseudo-wrappers

    /**
     * Gets the integer attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The integer value
     */
    public Integer getInteger( String key, Integer def )
    {

        String ans = get( key );

        if( ans == null )
        {

            return def;

        }

        else
        {

            return Integer.valueOf( ans );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putInteger( String key, Integer value )
    {
        putInteger( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putInteger( String key, Integer value, boolean notify )
    {

        if( value != null )
        {
            putInt( key, value.intValue(  ), false );

        }

        else
        {
            remove( key, false );

        }

        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    /**
     * Gets the date attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The date value
     */
    public Date getDate( String key, Date def )
    {

        String ans = get( key );

        if( ans == null )
        {

            return def;

        }

        else
        {

            return new Date( getLong( key, 0 ) );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putDate( String key, Date value )
    {
        putDate( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putDate( String key, Date value, boolean notify )
    {

        if( value != null )
        {
            putLong( key, value.getTime(  ), false );

        }

        else
        {
            remove( key, false );

        }

        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    /**
     * Gets the color attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The color value
     */
    public Color getColor( String key, Color def )
    {

        try
        {

            int r;

            int g;

            int b;

            String r_str = get( key + ".r" );

            if( r_str == null )
            {
                r = def.getRed(  );

            }

            else
            {
                r = Integer.parseInt( r_str );

            }

            String g_str = get( key + ".g" );

            if( g_str == null )
            {
                g = def.getGreen(  );

            }

            else
            {
                g = Integer.parseInt( g_str );

            }

            String b_str = get( key + ".b" );

            if( b_str == null )
            {
                b = def.getBlue(  );

            }

            else
            {
                b = Integer.parseInt( b_str );

            }

            return new Color( r, g, b );

        }

        catch( NumberFormatException e )
        {
            e.printStackTrace(  );

            return new Color( 0, 0, 0 );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putColor( String key, Color value )
    {
        putColor( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putColor( String key, Color value, boolean notify )
    {

        if( value != null )
        {
            putInt( key + ".r", value.getRed(  ), false );

            putInt( key + ".g", value.getGreen(  ), false );

            putInt( key + ".b", value.getBlue(  ), false );

        }

        else
        {
            remove( key + ".r", false );

            remove( key + ".g", false );

            remove( key + ".b", false );

        }

        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putPattern( String key, Pattern value )
    {
        putPattern( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putPattern( String key, Pattern value, boolean notify )
    {

        if( value != null )
        {
            put( key, value.pattern(  ), false );

        }

        else
        {
            remove( key, false );

        }

        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    /**
     * Gets the freeGuideTime attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     *
     * @return The freeGuideTime value
     */
    public Time getTime( String key )
    {

        return getTime( key, null );

    }

    /**
     * Gets the freeGuideTime attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The freeGuideTime value
     */
    public Time getTime( String key, Time def )
    {

        String ans = get( key );

        if( ans == null )
        {

            return def;

        }

        else
        {

            return new Time( ans );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putTime( String key, Time value )
    {
        putTime( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putTime( String key, Time value, boolean notify )
    {

        if( value != null )
        {
            put( key, value.getHHMMString(  ), false );

        }

        else
        {
            remove( key, false );

        }

        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    /**
     * Gets enough info from the preferences to identify a programme reliably.
     * Note: does not store all the info about a programme, just enough to
     * identify it i.e. title, start time, channel id.
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The freeGuideProgramme value
     */
    public TVProgramme getProgramme( String key, TVProgramme def )
    {

        String title = get( key + ".title" );

        // If the title isn't there, the programme isn't there (by convention)
        if( title == null )
        {

            return def;

        }

        else
        {

            TVProgramme prog = new TVProgramme(  );

            prog.setTitle( title );

            Calendar cal = GregorianCalendar.getInstance(  );

            cal.setTime( getDate( key + ".start", null ) );

            prog.setStart( cal.getTimeInMillis(  ) );

            //TODO prog.setChannel( new Channel( get( key + ".channel_id", "" ) ) );
            return prog;

        }
    }

    /**
     * Gets a FreeGuideFavourite object from this preferences store.
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The freeGuideFavourite value
     */
    public Favourite getFavourite( String key, Favourite def )
    {

        String name = get( key + ".name" );

        // If the title isn't there, the programme isn't there (by convention)
        if( name == null )
        {

            return def;

        }

        else
        {

            Favourite fav = new Favourite(  );

            fav.setName( name );

            fav.setTitleString( get( key + ".title_string" ) );

            fav.setTitleContains( get( key + ".title_contains" ) );

            fav.setTitleRegex( get( key + ".title_regex" ) );

            String channel_id = get( key + ".channel_id" );

            if( ( channel_id == null ) || channel_id.equals( "" ) )
            {
                fav.setChannelID( null );

            }

            else
            {
                fav.setChannelID( channel_id );

            }

            fav.setAfterTime( getTime( key + ".after_time", null ) );

            fav.setBeforeTime( getTime( key + ".before_time", null ) );

            //  fav.setDayOfWeek( getInteger( key + ".day_of_week", null ) );
            return fav;

        }
    }

    /**
     * Puts a FreeGuideFavourite object into this preferences store.
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putFavourite( String key, Favourite value )
    {
        putFavourite( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putFavourite( String key, Favourite value, boolean notify )
    {
        put( key + ".name", value.getName(  ), false );

        put( key + ".title_string", value.getTitleString(  ), false );

        put( key + ".title_contains", value.getTitleContains(  ), false );

        put( key + ".title_regex", value.getTitleRegex(  ), false );

        put( key + ".channel_id", value.getChannelID(  ), false );

        putTime( key + ".after_time", value.getAfterTime(  ), false );

        putTime( key + ".before_time", value.getBeforeTime(  ), false );

        //   putInteger( key + ".day_of_week", value.getDayOfWeek(  ), false );
        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param key
     * @param def
     *
     * @return
     */
    public ChannelSet getChannelSet( String key, ChannelSet def )
    {

        String name = get( key + ".name" );

        if( name == null )
        {

            return def;

        }

        else
        {

            ChannelSet cset = new ChannelSet(  );

            cset.setChannelSetName( name );

            String ids = get( key + ".channelids" );

            if( ids != null )
            {
                cset.addChannelsFromString( ids, null );

            }

            else
            {

                return def;

            }

            return cset;

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putChannelSet( String key, ChannelSetInterface value )
    {
        putChannelSet( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putChannelSet( 
        String key, ChannelSetInterface value, boolean notify )
    {
        put( key + ".name", value.getChannelSetName(  ), false );

        put( 
            key + ".channelids", ChannelSet.toString( value.getChannels(  ) ),
            false );

        if( notify )
        {
            notifyListeners( key, value );

        }
    }

    // ------------------------------------------------------------------------
    // Wrapper methods

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public String get( String key, String def )
    {

        String value = get( key );

        if( value == null )
        {

            return def;

        }

        else
        {

            return value;

        }
    }

    /**
     * Gets the boolean attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The boolean value
     */
    public boolean getBoolean( String key, boolean def )
    {

        String value = get( key );

        if( value == null )
        {

            return def;

        }

        else
        {

            return value.equals( "true" );

        }
    }

    /**
     * Gets the byteArray attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The byteArray value
     */

    /*public byte[] getByteArray(String key, byte[] def) {


    return prefs.getByteArray(key, def);


    }*/

    /**
     * Gets the double attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The double value
     */
    public double getDouble( String key, double def )
    {

        try
        {

            String value = get( key );

            if( value == null )
            {

                return def;

            }

            else
            {

                return Double.parseDouble( value );

            }
        }

        catch( NumberFormatException e )
        {
            e.printStackTrace(  );

            return 0;

        }
    }

    /**
     * Gets the float attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The float value
     */
    public float getFloat( String key, float def )
    {

        try
        {

            String value = get( key );

            if( value == null )
            {

                return def;

            }

            else
            {

                return Float.parseFloat( value );

            }
        }

        catch( NumberFormatException e )
        {
            e.printStackTrace(  );

            return 0;

        }
    }

    /**
     * Gets the int attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The int value
     */
    public int getInt( String key, int def )
    {

        try
        {

            String value = get( key );

            if( value == null )
            {

                return def;

            }

            else
            {

                return Integer.parseInt( value );

            }
        }

        catch( NumberFormatException e )
        {
            e.printStackTrace(  );

            return 0;

        }
    }

    /**
     * Gets the long attribute of the FreeGuidePreferences object
     *
     * @param key Description of the Parameter
     * @param def Description of the Parameter
     *
     * @return The long value
     */
    public long getLong( String key, long def )
    {

        try
        {

            String value = get( key );

            if( value == null )
            {

                return def;

            }

            else
            {

                return Long.parseLong( value );

            }
        }

        catch( NumberFormatException e )
        {
            e.printStackTrace(  );

            return 0;

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void put( String key, String value )
    {
        put( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void put( String key, String value, boolean notify )
    {

        if( value != null )
        {
            prefs.put( key, value );

            if( notify )
            {
                notifyListeners( key, value );

            }

            if( log_prefs )
            {
                logPref( key + "=" + value );

            }
        }

        else
        {
            remove( key, false );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putBoolean( String key, boolean value )
    {
        putBoolean( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putBoolean( String key, boolean value, boolean notify )
    {
        prefs.putBoolean( key, value );

        if( notify )
        {
            notifyListeners( key, new Boolean( value ) );

        }

        if( log_prefs )
        {
            logPref( key + "=" + value );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putByteArray( String key, byte[] value )
    {
        putByteArray( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putByteArray( String key, byte[] value, boolean notify )
    {
        prefs.putByteArray( key, value );

        if( notify )
        {
            notifyListeners( key, value );

        }

        if( log_prefs )
        {
            logPref( key + "=" + value );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putDouble( String key, double value )
    {
        putDouble( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putDouble( String key, double value, boolean notify )
    {
        prefs.putDouble( key, value );

        if( notify )
        {
            notifyListeners( key, new Double( value ) );

        }

        if( log_prefs )
        {
            logPref( key + "=" + value );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putFloat( String key, float value )
    {
        putFloat( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putFloat( String key, float value, boolean notify )
    {
        prefs.putFloat( key, value );

        if( notify )
        {
            notifyListeners( key, new Float( value ) );

        }

        if( log_prefs )
        {
            logPref( key + "=" + value );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putInt( String key, int value )
    {
        putInt( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putInt( String key, int value, boolean notify )
    {
        prefs.putInt( key, value );

        if( notify )
        {
            notifyListeners( key, new Integer( value ) );

        }

        if( log_prefs )
        {
            logPref( key + "=" + value );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     * @param value Description of the Parameter
     */
    public void putLong( String key, long value )
    {
        putLong( key, value, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void putLong( String key, long value, boolean notify )
    {
        prefs.putLong( key, value );

        if( notify )
        {
            notifyListeners( key, new Long( value ) );

        }

        if( log_prefs )
        {
            logPref( key + "=" + value );

        }
    }

    /**
     * Description of the Method
     *
     * @exception BackingStoreException Description of the Exception
     */
    public void clear(  ) throws BackingStoreException
    {
        clear( true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param notify DOCUMENT_ME!
     *
     * @throws BackingStoreException DOCUMENT_ME!
     */
    public void clear( boolean notify ) throws BackingStoreException
    {
        prefs.clear(  );

        if( notify )
        {
            notifyListeners( "", null );

        }

        if( log_prefs )
        {
            logPref( " Cleared." );

        }
    }

    /**
     * Description of the Method
     *
     * @exception BackingStoreException Description of the Exception
     */
    public void flush(  ) throws BackingStoreException
    {
        prefs.flush(  );

        if( log_prefs )
        {
            logPref( " Flushed." );

        }
    }

    /**
     * Description of the Method
     *
     * @param key Description of the Parameter
     */
    public void remove( String key )
    {
        remove( key, true );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param notify DOCUMENT_ME!
     */
    public void remove( String key, boolean notify )
    {
        prefs.remove( key );

        if( notify )
        {
            notifyListeners( key, null );

        }

        if( log_prefs )
        {
            logPref( key + " Removed." );

        }
    }

    /**
     * Description of the Method
     *
     * @return Description of the Return Value
     *
     * @exception BackingStoreException Description of the Exception
     */
    public String[] keys(  ) throws BackingStoreException
    {

        return prefs.keys(  );

    }

    /**
     * Description of the Method
     *
     * @exception BackingStoreException Description of the Exception
     */
    public void sync(  ) throws BackingStoreException
    {
        prefs.sync(  );

        if( log_prefs )
        {
            logPref( " Synced." );

        }
    }

    /**
     * Description of the Method
     *
     * @param os Description of the Parameter
     *
     * @exception java.io.IOException Description of the Exception
     * @exception BackingStoreException Description of the Exception
     */
    public void exportNode( OutputStream os )
        throws java.io.IOException, BackingStoreException
    {
        prefs.exportNode( os );

    }

    /**
     * Description of the Method
     *
     * @param is Description of the Parameter
     *
     * @exception java.io.IOException Description of the Exception
     * @exception BackingStoreException Description of the Exception
     * @exception InvalidPreferencesFormatException Description of the
     *            Exception
     */
    public void importPreferences( InputStream is )
        throws java.io.IOException, BackingStoreException, 
            InvalidPreferencesFormatException
    {
        prefs.importPreferences( is );

        notifyListeners( "", null );

    }

    /**
     * Updates a boolean preference if the value of the preference has changed
     * from that already stored. Returns a boolean value to indicate if the
     * value has been  updated.
     *
     * @param _key The name of the preference to update
     * @param value The <code>boolean</code> value to set
     *
     * @return Returns <code>true</code> if the values has been  updated,
     *         otherwise returns <code>false</code>.
     */
    public boolean updateBoolean( String _key, boolean value )
    {

        String val_str = value ? "true" : "false";

        String temp = get( _key );

        if( ( temp == null ) || !temp.equals( val_str ) )
        {
            putBoolean( _key, value );

            return true;

        }

        return false;

    }

    /**
     * Updates a time preference if the value of the preference has changed
     * from that already stored. Returns a boolean value to indicate if the
     * value has been updated.
     *
     * @param _key The name of the preference to update
     * @param value The <code>Time</code> value to set
     *
     * @return Returns <code>true</code> if the value has been  updated,
     *         otherwise returns <code>false</code>.
     */
    public boolean updateTime( String _key, Time value )
    {

        String temp = get( _key );

        if( ( temp == null ) || !temp.equals( value ) )
        {
            putTime( _key, value );

            return true;

        }

        return false;

    }

    /**
     * Updates a colour preference if the value of the preference has changed
     * from that already stored. Returns a boolean value to indicate if the
     * value has been updated.
     *
     * @param _key The name of the preference to update
     * @param value The <code>Color</code> value to set
     *
     * @return Returns <code>true</code> if the value has been  updated,
     *         otherwise returns <code>false</code>.
     */
    public boolean updateColor( String _key, Color value )
    {

        String temp = get( _key );

        if( ( temp == null ) || !temp.equals( value ) )
        {
            putColor( _key, value );

            return true;

        }

        return false;

    }

    /**
     * Updates an int preference if the value of the preference has changed
     * from that already stored. Returns a boolean value to indicate if the
     * value has been updated.
     *
     * @param _key The name of the preference to update
     * @param value The <code>int</code> value to set
     *
     * @return Returns <code>true</code> if the value has been  updated,
     *         otherwise returns <code>false</code>.
     */
    public boolean updateInt( String _key, int value )
    {

        return update( _key, String.valueOf( value ) );

    }

    /**
     * Updates a string preference if the value of the preference has changed
     * from that already stored. Returns a boolean value to indicate if the
     * value has been  updated.
     *
     * @param _key The name of the preference to update
     * @param value The <code>String</code> value to set
     *
     * @return Returns <code>true</code> if the value has been  updated,
     *         otherwise returns <code>false</code>.
     */
    public boolean update( String _key, String value )
    {

        boolean changed = false;

        String temp = get( _key );

        if( temp == null )
        {

            if( value != null )
            {
                changed = true;

                put( _key, value );

            }
        }

        else if( ( value == null ) || !temp.equals( value ) )
        {
            changed = true;

            put( _key, value );

        }

        return changed;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static StringBuffer getIconCacheDir(  )
    {

        StringBuffer ans = new StringBuffer(  );

        ans.append( FreeGuide.config.workingDirectory );

        ans.append( "/iconCache" );

        return ans;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param listener DOCUMENT_ME!
     */
    public void addFGPreferenceChangeListener( 
        FGPreferenceChangeListener listener )
    {
        listeners.add( listener );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param listener DOCUMENT_ME!
     */
    public void removeFGPreferenceChangeListener( 
        FGPreferenceChangeListener listener )
    {
        listeners.remove( listener );

    }

    private void notifyListeners( String key, Object value )
    {

        Iterator iter = listeners.iterator(  );

        while( iter.hasNext(  ) )
        {
            ( (FGPreferenceChangeListener)( iter.next(  ) ) ).preferenceChange( 
                new FGPreferenceChangeEvent( key, value, this ) );

        }
    }

    private void logPref( String str )
    {
        FreeGuide.log.info( "prefs: " + subNode + "." + str );

    }
}
