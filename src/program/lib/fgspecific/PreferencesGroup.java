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

import freeguidetv.*;
import freeguidetv.gui.viewer.*;
import freeguidetv.lib.general.*;
import java.text.*;
import java.io.*;
import java.util.*;
import java.util.prefs.Preferences;

/**
 *  FreeGuidePreferencesGroup Provides a place to hold the various
 *  FreeGuidePreferences objects needed for FreeGuide, and some convenience
 *  methods for dealing with them.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    3
 */
public class PreferencesGroup {

    /**
     *  The constructor
     */
    public PreferencesGroup() {

        screen = new FGPreferences("screen");
        commandline = new FGPreferences("commandline");
        misc = new FGPreferences("misc");
        favourites = new FGPreferences("favourites");
        chosen_progs = new FGPreferences("chosenprogs");
        channelsets = new FGPreferences("channelsets");

    }


    /**
     *  noErrors Reports on whether setting up preferences was successful.
     *
     *@return     Description of the Return Value
     *@returns    true unless errors were encountered in the course of
     *      configuration processing
     */
    public boolean noErrors() {

        // Always returns true as assumes nothing can go wrong.  Errors are
        // dealt with gracefully by the Java Preferences implementation.
        return true;
    }


    // ------------------------------------------------------------------------

    /**
     *  Description of the Method
     */
    public void flushAll() {
        try {
            screen.flush();
            commandline.flush();
            misc.flush();
            favourites.flush();
            chosen_progs.flush();
            channelsets.flush();
        } catch (java.util.prefs.BackingStoreException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Gets the offsetForDate attribute of the FreeGuidePreferencesGroup object
     *
     *@param  date  Description of the Parameter
     *@return       The offsetForDate value
     */
    public int getOffsetForDate(Calendar date) {

        //  get today so we can calculate grabber offset
        Calendar realToday = GregorianCalendar.getInstance();
        int realDOY = realToday.get(Calendar.DAY_OF_YEAR);
        int realY = realToday.get(Calendar.YEAR);

        //  Get freeguide visible day (this is the one we will grab)
        int visibleDOY = date.get(Calendar.DAY_OF_YEAR);
        int visibleY = date.get(Calendar.YEAR);

        // if today, and freeguideDay are in different years compensate
        if (Math.abs(visibleY - realY) > 1) {
            FreeGuide.log.severe("getOffsetForDate():\n" +
                    "       Trying to fetch a date greater than\n" +
                    "       1 year from the present day is not supported.");
            return (0);
        }
        if (visibleY > realY) {
            // add a real year number of days to the visible day of year
            // to compensate for the change in year
            visibleDOY += realToday.getActualMaximum(Calendar.DAY_OF_YEAR);
        } else if (visibleY < realY) {
            // add a visible year number of days to the real day of year
            // to compensate for the change in year
            realDOY += date.getActualMaximum(Calendar.DAY_OF_YEAR);
        }

        // visible day after real day is future offset (positive)
        // visible day before real day is past offset (negative)
        int offset = visibleDOY - realDOY;

        //return(offset+misc.getInt("grabber_today_offset",1)); // _uk
        return (offset + misc.getInt("grabber_today_offset", 0));
        // _na
    }


    /**
     *  Description of the Method
     *
     *@param  in  Description of the Parameter
     *@return     Description of the Return Value
     */
    public String performSubstitutions(String in) {
        return performSubstitutions(in, false);
    }


    /**
     *  Substitute any preference values within the string in and return the
     *  result. Assumes any dates should be today, and any "offset" is 1.
     *
     *@param  preserveDoublePercents  if this is true any %%'s we encounter
     *      remain %%'s instead of being changed to a single %.
     *@param  in                      Description of the Parameter
     *@return                         Description of the Return Value
     */
    public String performSubstitutions(String in,
            boolean preserveDoublePercents) {
        return performSubstitutions(in, GregorianCalendar.getInstance(),
                preserveDoublePercents);
    }


    /**
     *  Description of the Method
     *
     *@param  in    Description of the Parameter
     *@param  date  Description of the Parameter
     *@return       Description of the Return Value
     */
    public String performSubstitutions(String in, Calendar date) {

        return performSubstitutions(in, date, false);
    }


    /**
     *  Substitute any preference values within the string in and return the
     *  result. Replaces "date" with the date given (formatted as
     *  FreeGuideViewer.fileDateFormat) and "offset" with the number given.
     *
     *@param  preserveDoublePercents  if this is true any %%'s we encounter
     *      remain %%'s instead of being changed to a single %.
     *@param  in                      Description of the Parameter
     *@param  date                    Description of the Parameter
     *@return                         Description of the Return Value
     */
    public String performSubstitutions(String in, Calendar date,
            boolean preserveDoublePercents) {

        if (in == null) {
            return null;
        }

        String ans = new String(in);
        int offset = getOffsetForDate(date);
        int i = ans.indexOf('%');
        while (i != -1) {

            int j = ans.indexOf('%', i + 1);

            // If this was a %% to escape a %, deal with it
            if (j == i + 1) {
                ans = ans.substring(0, i) + "---FREEGUIDE_PERCENT---" + ans.substring(j + 1);
            } else {
                // Otherwise this is a keyword
                String ref = ans.substring(i + 1, j);
                int k = ref.indexOf('.');
                FGPreferences thePref;
                String theKey;
                if (k == -1) {
                    thePref = null;
                    theKey = ref;
                } else {
                    String node = ref.substring(0, k);
                    if (node.equals("screen")) {
                        thePref = screen;
                    } else if (node.equals("commandline")) {
                        thePref = commandline;
                    } else if (node.equals("misc")) {
                        thePref = misc;
                    } else if (node.equals("favourites")) {
                        thePref = favourites;
                    } else if (node.equals("chosenprogs")) {
                        thePref = chosen_progs;
                    } else if (node.equals("channelsets")) {
                        thePref = channelsets;
                    } else {
                        thePref = null;
                    }
                    theKey = ref.substring(k + 1);
                }
                //if

                String sub = "";

                if (thePref == null) {

                    if (theKey.toLowerCase().equals("home")) {
                        sub = System.getProperty("user.home");
                    } else if (theKey.toLowerCase().equals("date")) {
                        sub = ViewerFrame.fileDateFormat.format(
                                date.getTime());
                    } else if (theKey.toLowerCase().equals("offset")) {
                        sub = String.valueOf(offset);
                    } else {
                        sub = "!!!Unknown preference!!!";
                    }

                } else {

                    sub = thePref.get(theKey);
                }
                ans = ans.substring(0, i) + sub + ans.substring(j + 1);
            }
            //if

            i = ans.indexOf('%');

        }
        //while

        // Put the %'s back in
        i = ans.indexOf("---FREEGUIDE_PERCENT---");
        while (i != -1) {

            String newPercent;
            if (preserveDoublePercents) {
                newPercent = "%%";
            } else {
                newPercent = "%";
            }

            ans = ans.substring(0, i) + newPercent + ans.substring(i + 23);

            i = ans.indexOf("---FREEGUIDE_PERCENT---");

        }

        return ans;
    }


    //performSubstitutions

    // ------------------------------------------------------------------------
    // Convenience methods


    /**
     *  Has the user chosen any programmes for today?
     *
     *@param  date  Description of the Parameter
     *@return       Description of the Return Value
     */
    public boolean chosenAnything(Calendar date) {
        String dateStr = "day-" + chosenDateFormat.format(date.getTime());
        return chosen_progs.getBoolean(dateStr, false);
        //date.get(Calendar.YEAR) + "-" + date.get(Calendar.MONTH) + "-" + date.get(Calendar.DAY_OF_MONTH), false );
    }


    /**
     *  Get a list of all days that have been chosen.
     *
     *@return    The allChosenDays value
     */
    public Calendar[] getAllChosenDays() {
        Vector ans = new Vector();

        try {

            String[] keys = chosen_progs.keys();

            for (int i = 0; i < keys.length; i++) {
                if (keys[i].startsWith("day-")) {
                    Calendar cal = GregorianCalendar.getInstance();
                    try {
                        cal.setTime(chosenDateFormat.parse(keys[i].substring(4)));
                        ans.add(cal);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (java.util.prefs.BackingStoreException e) {
            e.printStackTrace();
        }

        return Utils.arrayFromVector_Calendar(ans);
    }


    /**
     *  Remember that we have made a choice for today.
     *
     *@param  date  The date for which we have made a choice
     */
    public void chosenSomething(Calendar date) {
        chosenSomething(date, true);
    }


    /**
     *  Remember that we have or have not made a choice for today.
     *
     *@param  date  The date we are interested in
     *@param  yes   Whether we are saying we have, or have not, made a choice
     */
    public void chosenSomething(Calendar date, boolean yes) {
		
		//FreeGuide.log.info( "begin" );
		
        String dateStr = "day-" + chosenDateFormat.format(date.getTime());
        if (yes) {
			
            chosen_progs.putBoolean(dateStr, true);
			
        } else {
			
			//FreeGuide.log.info( "1" );
			
            // Remove the choices for this day
            int[] chosenProgKeys = getChosenProgKeys( date );
			
			//FreeGuide.log.info( "length=" + chosenProgKeys.length );
			
			chosen_progs.removeChoices( chosenProgKeys );
			
            // Remove the fact that this day is chosen
            chosen_progs.remove(dateStr);
			
			//FreeGuide.log.info( "4" );
        }

		//FreeGuide.log.info( "end" );
		
    }


    /**
     *  Returns all the choices the user has made for one day. NOTE: actually
     *  returns all choice _if_ there are any choices for today.
     *
     *@param  date  Description of the Parameter
     *@return       null if there are no choices for today, or all chosen
     *      programmes
     */
    public Vector getChosenProgs(Calendar date) {

        if (!chosenAnything(date)) {
            return null;
        }

        Vector ans = new Vector();

        int i = 1;
        Programme prog = chosen_progs.getProgramme(
                String.valueOf(i), null);

        while (prog != null) {

            ans.add(prog);
            i++;
            prog = chosen_progs.getProgramme(String.valueOf(i), null);

        }

        if (ans.size() > 0) {
            return ans;
        } else {
            return null;
        }

    }


    /**
     *  Returns the keys of all the choices the user has made for one day. NOTE:
     *  this returns programmes actually on that date, not adjusted for the
     *  day_start_time parameter.
     *
     *@param  date  Description of the Parameter
     *@return       null if there are no choices for today, or all chosen
     *      programmes
     */
    public int[] getChosenProgKeys(Calendar date) {

        Vector ans = new Vector();

        if ( chosenAnything(date) ) {

            int i = 1;
            Programme prog = chosen_progs.getProgramme(String.valueOf(i), null);
			Calendar progStart;
			Time progTime;
			Time day_start_time = FreeGuide.prefs.misc.getTime(
				"day_start_time" );
			
			
            while (prog != null) {

                progStart = prog.getStart();
				progTime = new Time( progStart );
				
				if ( progTime.before( day_start_time, new Time(0, 0) ) ) {
					
					progStart.add( Calendar.DAY_OF_YEAR, -1 );

				} 
				
                if ( (progStart.get( Calendar.DAY_OF_YEAR )
						== date.get( Calendar.DAY_OF_YEAR ) ) 
					&& (progStart.get( Calendar.YEAR )
						== date.get( Calendar.YEAR ) ) ) {
							
                    ans.add( new Integer(i) );
                    
                }
				
				i++;
                prog = chosen_progs.getProgramme( String.valueOf(i), null );
            }
			
        }
		
        return Utils.arrayFromVector_int(ans);
    }


    /**
     *  Adds a feature to the Choice attribute of the FreeGuidePreferencesGroup
     *  object
     *
     *@param  prog  The feature to be added to the Choice attribute
     *@param  date  The feature to be added to the Choice attribute
     */
    public void addChoice(Programme prog, Calendar date) {

        if (!chosenAnything(date)) {
            chosenSomething(date);
        }

        chosen_progs.appendProgramme(prog);
    }


    /**
     *  Description of the Method
     *
     *@param  prog  Description of the Parameter
     */
    public void removeChoice(Programme prog) {

        int i = chosen_progs.findProgramme(prog);

        if (i != -1) {
            chosen_progs.removeChoice(i);
        }
    }


    /**
     *  Gets the favourites attribute of the FreeGuidePreferencesGroup object
     *
     *@return    The favourites value
     */
    public List getFavourites() {

        ArrayList ans = new ArrayList();

        int i = 1;
        Favourite fav;

        while ((fav = favourites.getFavourite(String.valueOf(i), null)) != null) {

            ans.add(fav);
            i++;
        }

        return ans;
    }

	/**
     *  Gets the possible browsers the user may choose
     *
     *@return    An array of the possible browsers available
     */
	public String[] getBrowsers() {
		
		Vector ans = new Vector();
		
		int i = 1;
        String browser = misc.get("browser." + i, null );

        while (browser != null) {

            ans.add(browser);
            i++;
            browser = misc.get("browser." + i, null );
        }

        return Utils.arrayFromVector_String( ans );
		
	}

    /**
     *  
     *
     *@return    All the channel sets in the preferences
     */
    public ChannelSet[] getChannelSets() {
        Vector ans = new Vector();

        int i = 1;
        ChannelSet cset = channelsets.getChannelSet( String.valueOf(i),
			null );

        while (cset != null) {

            ans.add(cset);
            i++;
            cset = channelsets.getChannelSet(String.valueOf(i), null);
        }

        return Utils.arrayFromVector_ChannelSet( ans );
    }


    /**
     *  Description of the Method
     *
     *@param  favs  Description of the Parameter
     */
    public void replaceFavourites(List favs) {

        try {

            int size = favs.size();
            String[] keys = new String[size];
            for (int i = 0; i < size; i++) {
                keys[i] = String.valueOf(i + 1);
            }
            favourites.replaceAllFavourites(keys, favs);

        } catch (java.util.prefs.BackingStoreException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Description of the Method
     *
     *@param  csets  Description of the Parameter
     */
    public void replaceChannelSets(ChannelSetInterface[] csets) {

        try {

            int size = csets.length;
            String[] keys = new String[size];
            for (int i = 0; i < size; i++) {
                keys[i] = String.valueOf(i + 1);
            }
            channelsets.replaceAllChannelSets(keys, csets);

        } catch (java.util.prefs.BackingStoreException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Gets the commands attribute of the FreeGuidePreferencesGroup object
     *
     *@param  key  Description of the Parameter
     *@return      The commands value
     */
    public String[] getCommands(String key) {

        return commandline.getStrings(key);
    }

    
    /**
     * Given a line cat.key=value, returns an FGPreferences object representing
     * the category, a string for the key and a string for the value.
     */
    private Vector processPrefLine( String line ) {
        
        // Split this string into its constituent parts
        int i = line.indexOf('=');
        if( i == -1 ) {
            FreeGuide.die( "Invalid preference string applied - no '='." );
        }
        String key = line.substring(0, i);
        String value = line.substring(i + 1);

        i = key.indexOf('.');
        if( i == -1 ) {
            FreeGuide.die( "Invalid preference string applied - no '.'." );
        }
        String keyCategory = key.substring(0, i);
        key = key.substring(i + 1);

        // Find out what preferences category we're dealing with
        FGPreferences pr;
        if( keyCategory.equals("misc") ) {
            pr = misc;
        } else if( keyCategory.equals("commandline") ) {
            pr = commandline;
        } else {
            // If needed we could add the other categories here, but for now...
            pr = misc;
            FreeGuide.die("Unknown preferences group: " + keyCategory
                + " - Aborting");
        }
        
        Vector ans = new Vector();
        ans.add( pr );
        ans.add( key );
        ans.add( value );
        
        return ans;
        
    }

    /**
     * Takes a line in a form like this:
     *
     * misc.install_directory=C:\Program Files\FreeGuide
     *
     * and translates that to place the relevant entry into the correct
     * user preferences node..
     *
     *@param line    the String line to interpret
     */
    public void put( String line) {
        
        Vector ans = processPrefLine( line );
        
        FGPreferences pr = (FGPreferences)ans.get(0);
        String key = (String)ans.get(1);
        String value = (String)ans.get(2);
        
        pr.put( key, value );
        
    }
    
    /**
     * Takes a line in a form like this:
     *
     * misc.install_directory=C:\Program Files\FreeGuide
     *
     * and translates that to place the relevant entry into the correct
     * system preferences node.  Also places a default value using the key with
     * "default-" prepended to it.
     *
     *@param line    the String line to interpret
     */
    public void putSystem( String line) {
        
        Vector ans = processPrefLine( line );
        
        FGPreferences pr = (FGPreferences)ans.get(0);
        String key = (String)ans.get(1);
        String value = (String)ans.get(2);
        
        pr.put( key, value );
        
        // Set the default value
        pr.putSystem("default-" + key, value);

        // And the real value
        pr.putSystem(key, value);
        
    }
    
    public FGPreferences screen;
    // The screen dimensions etc.
    /**
     *  Description of the Field
     */
    public FGPreferences commandline;
    // Grabber commands and options
    /**
     *  Description of the Field
     */
    public FGPreferences misc;
    // Other prefs
    /**
     *  Description of the Field
     */
    public FGPreferences favourites;
    // The user's favourite progs
    /**
     *  Description of the Field
     */
    public FGPreferences chosen_progs;
    // The selected progs
    /**
     *  Description of the Field
     */
    public FGPreferences channelsets;
    //Sets of channel customization

    private final static SimpleDateFormat chosenDateFormat = new SimpleDateFormat("yyyyMMdd");

}
