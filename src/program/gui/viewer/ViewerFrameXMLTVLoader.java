/*
 *  FreeGuide
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

/**
 *  XMLTVLoader Loads the required XMLTV files for a given date into a Vector of
 *  programmes and stores other relevant details e.g. channel details
 *
 *@author     andy
 *@created    28 June 2003
 *@version    3
 */

class ViewerFrameXMLTVLoader extends DefaultHandler implements ChannelSetInterface {

    /**
     *  Loads the programme data from a file and stores it in a class structure
     *  ready for display on the screen.
     *
     *@param  nowDate  The date and time for which to load programmes
     */
    public void loadProgrammeData( Calendar nowDate ) {

        thereAreEarlyProgs = false;
        thereAreLateProgs = false;

        // Find out the span of time for this day (using day_start_time)
        // and alter the date if we're actually asking for a time that falls
        // on the previous date
        updateDaySpan( nowDate );

        setupHasDataStuff( nowDate );

        // Prepare the vectors that will contain the parsed data
        programmes = new Vector();
        channelIDs = new Vector();
        channelNames = new Vector();

        // Now date is the actual date we want (not the time) so we can work out
        // what days we need to ask for from the grabber

        // Get a reference to yesterday's date (day before "date")
        Calendar yesterday = (Calendar) date.clone();
        yesterday.add(Calendar.DAY_OF_YEAR, -1);

        // Similarly get tomorrow
        Calendar tomorrow = (Calendar) date.clone();
        tomorrow.add(Calendar.DAY_OF_YEAR, 1);

        String date1str;
        String date2str;

        //FreeGuide.log.info( "grabber_start_time=" + grabber_start_time +
        //	" day_start_time=" + day_start_time );

        // See whether we need to grab yesterday or tomorrow
        if ( grabber_start_time.before( day_start_time,
                new Time(0, 0) ) ) {

            // Example:
            // grabber grabs midnight to midnight and we want to see 02:00 today
            // to 02:00 tomorrow: need to grab today and tomorrow

            date1str = ViewerFrame.fileDateFormat.format(date.getTime());
            date2str = ViewerFrame.fileDateFormat.format(
                    tomorrow.getTime());

            //FreeGuide.log.info( "Getting tomorrow data" );

        } else if( grabber_start_time.after( day_start_time,
                new Time(0, 0) ) ) {

            // Example:
            // grabber grabs 06:00 today to 06:00 tomorrow and we want to see
            // midnight to midnight: need to grab today and yesterday

            date1str = ViewerFrame.fileDateFormat.format(
                    yesterday.getTime());
            date2str = ViewerFrame.fileDateFormat.format(
                    date.getTime());

            //FreeGuide.log.info( "Getting yesterday data" );

        } else {
			
			// The days are perfectly matched so only parse one day.
			
			date1str = null;
			date2str = ViewerFrame.fileDateFormat.format(
                    date.getTime() );
			
		}

		String day1Filename;
		String day2Filename;
		
		if( date1str != null ) {
			day1Filename = working_directory + fs + "tv-" +
                date1str + ".xmltv";
		} else {
			day1Filename = null;
		}

        day2Filename = working_directory + fs + "tv-" +
                date2str + ".xmltv";

        String unprocFilename = working_directory + fs + "tv-unprocessed.xmltv";

		File day1File;
		File day2File;
		File unprocFile;
		
		if( day1Filename != null ) {
			day1File = new File(day1Filename);
		} else {
			day1File = null;
		}
		
        day2File = new File(day2Filename);
        unprocFile = new File(unprocFilename);

        // Parse any files that exist

        try {
            //ParserExceptions etc

            //DefaultHandler handler = new FreeGuideSAXHandler( this );

            SAXParserFactory factory = SAXParserFactory.newInstance();

            SAXParser saxParser = factory.newSAXParser();

			boolean day1FileExists;
			boolean day2FileExists;
			
			if( day1File != null ) {
				day1FileExists = day1File.exists();
			} else {
				day1FileExists = false;
			}
			
            day2FileExists = day2File.exists();

            // If either day file exists (or both), parse it/them.  Otherwise
            // get the unproc. listings if they exist.

            if (day1FileExists) {

				//FreeGuide.log.info( "Parsing " + day1Filename );
				
                saxParser.parse(day1Filename, this);

            }

            if (day2FileExists) {

				//FreeGuide.log.info( "Parsing " + day2Filename );
				
                saxParser.parse(day2Filename, this);

            }

            if ((!day1FileExists) && (!day2FileExists) && unprocFile.exists()) {

                // The grabber must not be able to split into days,
                // so we'll deal with the unprocessed data.
                saxParser.parse(unprocFilename, this);

            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            // FIXME - error dialog!
        } catch (SAXException e) {
            e.printStackTrace();
            // FIXME - error dialog!
        } catch (java.io.IOException e) {
            e.printStackTrace();
            // FIXME - error dialog!
        }
        //try

    }

    // 
    /**
     *  Returns true if there was "enough" data for today and false if some was
     * missing.
     *
     *@return    true if there was enough data for today
     */
    public boolean hasData() {

        // Normal:
        return (thereAreEarlyProgs && thereAreLateProgs);
    }


    // ----------------------------------------------------------------------

    /**
     *  Alter date to reflect if we are in another day's data (i.e. before the
     *  day start time) and set earliest and latest to the right values
     *  according to the day start time
     *
     *@param  nowDate  Description of the Parameter
     */
    public void updateDaySpan( Calendar nowDate ) {

        day_start_time = FreeGuide.prefs.misc.getTime(
                "day_start_time", new Time(6, 0));

        grabber_start_time = FreeGuide.prefs.misc.getTime(
                "grabber_start_time", new Time(0, 0));

        working_directory = FreeGuide.prefs.performSubstitutions(
                FreeGuide.prefs.misc.get("working_directory"));

        //earliest = GregorianCalendar.getInstance();
        //latest = GregorianCalendar.getInstance();

        date = (Calendar) nowDate.clone();

        // If we're before the day start time we actually want the previous day.
        //FreeGuideTime nowTime = new FreeGuideTime( date );

        //if( nowTime.before( day_start_time, new FreeGuideTime( 0, 0 ) ) ) {

        // If we need to adjust because our day start is before our grabber's
        //if( day_start_time.before(
        //		grabber_start_time, new FreeGuideTime( 0, 0 ) ) ) {

        // Set the time to the previous day, 1 hour after the day start time
        //	date.add( Calendar.DATE, -1 );
        //	date.set( Calendar.HOUR, day_start_time.getHours() + 1 );

        //}

        // Set earliest to the start time on the date
        earliest = (Calendar) date.clone();

        day_start_time.adjustCalendar(earliest);

        // Set latest to the start time on the day after the date
        latest = (Calendar) date.clone();

        latest.add(Calendar.DAY_OF_YEAR, 1);

        day_start_time.adjustCalendar(latest);

    }


    /**
     *  Description of the Method
     *
     *@param  nowDate  Description of the Parameter
     */
    private void setupHasDataStuff(Calendar nowDate) {

        // There must be a programme crossing over both of these times in order
        // for the day to be "covered" i.e. we don't need to download more.
        hasDataEarliest = (Calendar) earliest.clone();
        hasDataLatest = (Calendar) latest.clone();

        // If it's today then hasDataEarliest is now-ish
        if (dayIsToday(nowDate)) {

            hasDataEarliest.setTimeInMillis(nowDate.getTimeInMillis());

        }

        // Now add an hour's grace to start time
        hasDataEarliest.add(Calendar.HOUR, 1);

        // and remove an hour from the end time
        hasDataLatest.add(Calendar.HOUR, -1);

    }


    /**
     *  Description of the Method
     *
     *@param  nowDate  Description of the Parameter
     *@return          Description of the Return Value
     */
    private boolean dayIsToday(Calendar nowDate) {

        Calendar today = GregorianCalendar.getInstance();
        Time nowTime = new Time(today);

        // If we're before the day start time then go to the previous day
        if (nowTime.before(day_start_time, new Time(0, 0))) {

            today.add(Calendar.DATE, -1);

        }

        // Now check whether the dates are equal.
        return (nowDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                nowDate.get(Calendar.DAY_OF_YEAR) ==
                today.get(Calendar.DAY_OF_YEAR));
    }


    // ----------------------------------------------------------------------

    /**
     *  Description of the Method
     */
    public void startDocument() {
        saxLoc = new String();
        channelIcons = new Hashtable();
    }


    //startDocument

    /**
     *  Description of the Method
     */
    public void endDocument() {
        saxLoc = null;
    }


    //endDocument

    /**
     *  Description of the Method
     *
     *@param  namespaceURI  Description of the Parameter
     *@param  sName         Description of the Parameter
     *@param  name          Description of the Parameter
     *@param  attrs         Description of the Parameter
     */
    public void startElement(String namespaceURI, String sName, String name,
            Attributes attrs) {

        saxLoc += ":" + name;
		
		data = "";

        //FreeGuide.log.info( saxLoc );

        if (saxLoc.equals(":tv:programme")) {

            currentProgramme = new Programme();

            // Prepare GregorianCalendars for start and end
            Calendar start = GregorianCalendar.getInstance();
            Calendar end = GregorianCalendar.getInstance();

            // Assume it has a channel
            String channelID = attrs.getValue("channel");
            currentProgramme.setChannelID(channelID);
            currentProgramme.addToChannelName(getChannelName(channelID));

            try {

                // Assume is has a start time
                start = parseDate(attrs.getValue("start"));

                // Don't assume it has an end time
                if (attrs.getIndex("stop") == -1
                        || attrs.getValue("stop").equals("+0100")) {
                    // Also hack around bug in de grabber

                    // Give it a fake end time, half an hour after the start
                    end.setTimeInMillis(start.getTimeInMillis());
                    end.add(Calendar.MINUTE, 30);

                } else {

                    end = parseDate(attrs.getValue("stop"));
                    //Watch out for missing end dates!
                    if (end.before(start)) {
                        // Give it a fake end time, half an hour after the start
                        end.setTimeInMillis(start.getTimeInMillis());
                        end.add(Calendar.MINUTE, 30);
                    }
                }

            } catch (java.text.ParseException e) {
                e.printStackTrace();
                currentProgramme = null;

                return;
            }

            if (start.before(hasDataEarliest)) {

                thereAreEarlyProgs = true;

            }

            if (end.after(hasDataLatest)) {

                thereAreLateProgs = true;

            }

            currentProgramme.setStart(start);
            currentProgramme.setEnd(end);

        } else if (saxLoc.equals(":tv:channel")) {

            String id = attrs.getValue("id");

            tmpChannelID = id;

        } else if (saxLoc.equals(":tv:channel:icon")) {

        	String URL = attrs.getValue("src");
        	if (URL != null)
        		channelIcons.put(tmpChannelID,URL);

        } else if (saxLoc.equals(":tv:programme:previously-shown")) {

            if (currentProgramme != null) {
                currentProgramme.setPreviouslyShown(true);
            }
        } else if (saxLoc.equals(":tv:programme:rating")) {

            if (currentProgramme != null) {
                String ratingsystem = attrs.getValue("system");
                if (ratingsystem != null && ratingsystem.equalsIgnoreCase("MPAA")) {
                    currentProgramme.setIsMovie(true);
                }
            }
		} else if (saxLoc.equals(":tv:programme:subtitles")) {

            if (currentProgramme != null) {
                
                currentProgramme.setSubtitled( true );
                
            }
        
		}
        //if

    }


    //startElement

    /**
     *  Description of the Method
     *
     *@param  strDate                       Description of the Parameter
     *@return                               Description of the Return Value
     *@exception  java.text.ParseException  Description of the Exception
     */
    private Calendar parseDate(String strDate)
             throws java.text.ParseException {

        Calendar ans = GregorianCalendar.getInstance();
                 
        // First check for a time without any timezone or seconds
        if( strDate.matches( "\\A\\d{12}\\z" ) ) {
            
            ans.setTime(
                new SimpleDateFormat( "yyyyMMddHHmm" ).parse( strDate ) );
            
            // Now try without timezone or seconds
        } else if( strDate.matches( "\\A\\d{14}\\z" ) ) {
            
            ans.setTime(
                new SimpleDateFormat( "yyyyMMddHHmmss" ).parse( strDate ) );
            
        } else {
            
            try {
                
                ans.setTime(
                    new SimpleDateFormat("yyyyMMddHHmmss z").parse( strDate ) );
                
             } catch (java.text.ParseException g) {
                 
                 ans.setTime(
                    new SimpleDateFormat("yyyyMMddHHmmss Z").parse( strDate ) );
                 
             }
        }

        return ans;
    }


    /**
     *  Description of the Method
     *
     *@param  namespaceURI  Description of the Parameter
     *@param  sName         Description of the Parameter
     *@param  name          Description of the Parameter
     */
    public void endElement(String namespaceURI, String sName, String name) {

        //FreeGuide.log.info(name);

        if (saxLoc.equals(":tv:programme")) {

            if (currentProgramme.getEnd().after(earliest) &&
                    currentProgramme.getStart().before(latest)) {

                programmes.add(currentProgramme);
            }

            currentProgramme = null;
			
        } else if (saxLoc.equals(":tv:programme:title")) {

            if (currentProgramme != null) {
                currentProgramme.setTitle( data );
            }
		} else if (saxLoc.equals(":tv:programme:sub-title")) {

            if (currentProgramme != null) {
                currentProgramme.setSubTitle( data );
            }
        } else if (saxLoc.equals(":tv:programme:desc")) {

            if (currentProgramme != null) {
                currentProgramme.addDesc( data );
            }
        } else if (saxLoc.equals(":tv:programme:category")) {

            if (currentProgramme != null) {
                currentProgramme.addCategory( data );
                if (data.equalsIgnoreCase("Film")) {
                    currentProgramme.setIsMovie(true);
                }
            }
        } else if (saxLoc.equals(":tv:programme:star-rating:value")) {

            if (currentProgramme != null) {
                currentProgramme.setStarRating( data );
            }
		//} else if (saxLoc.equals(":tv:programme:episode-num")) {
			// FIXME - fill in here

        } else if (saxLoc.equals(":tv:programme:url")) {

			if (currentProgramme != null) {
				
				try {
					
                	currentProgramme.setLink(new URL(data));
				
				} catch(java.net.MalformedURLException e) {
					e.printStackTrace();
				}
            }

		} else if (saxLoc.equals(":tv:channel:display-name")) {

            // Remember the name of the channel we're looking at
            addChannelName(tmpChannelID, data);

		}
		
		if (saxLoc.endsWith(name)) {

            saxLoc = saxLoc.substring(0, saxLoc.length() - (name.length() + 1));

        } else {
            parseError();
        }
        //if

        //FreeGuide.log.info("endElement END");

    }


    //endElement

    /**
     *  Description of the Method
     *
     *@param  ch      Description of the Parameter
     *@param  start   Description of the Parameter
     *@param  length  Description of the Parameter
     */
    public void characters(char[] ch, int start, int length) {
        data += new String(ch, start, length);
    }


    //characters

    // -----------------------------------------------------------------------

    /**
     *  Gets the channelIDs attribute of the XMLTVLoader object
     *
     *@return    The channelIDs value
     */
    public Vector getChannelIDs() {
        return channelIDs;
    }


    /**
     *  Gets the channelNames attribute of the XMLTVLoader object
     *
     *@return    The channelNames value
     */
    public Vector getChannelNames() {
        return channelNames;
    }


    /**
     *  Adds a feature to the ChannelName attribute of the XMLTVLoader object
     *
     *@param  channelID    The feature to be added to the ChannelName attribute
     *@param  channelName  The feature to be added to the ChannelName attribute
     */
    private void addChannelName(String channelID, String channelName) {

		int i = channelIDs.indexOf(channelID);
		
        if ( i == -1) {

            channelIDs.add(channelID);
            channelNames.add(channelName);

        }
    }


    /**
     *  Gets the channelName attribute of the XMLTVLoader object
     *
     *@param  i  Description of the Parameter
     *@return    The channelName value
     */
    public String getChannelName(int i) {

        return (String) channelNames.get(i);
    }


    /**
     *  Gets the noChannels attribute of the XMLTVLoader object
     *
     *@return    The noChannels value
     */
    public int getNoChannels() {

        return channelIDs.size();
    }


    /**
     *  Gets the channelNo attribute of the XMLTVLoader object
     *
     *@param  channelID  Description of the Parameter
     *@return            The channelNo value
     */
    public int getChannelNo(String channelID) {

        return channelIDs.indexOf(channelID);
    }


    /**
     *  Sets the channelSetName attribute of the XMLTVLoader object
     *
     *@param  name  The new channelSetName value
     */
    public void setChannelSetName(String name) {
        return;
    }


    /**
     *  Gets the channelSetName attribute of the XMLTVLoader object
     *
     *@return    The channelSetName value
     */
    public String getChannelSetName() {
        return ViewerFrame.CHANNEL_SET_ALL_CHANNELS;
    }


    /**
     *  Returns the name of the channel whose ID is supplied.
     *
     *@param  channelID  Description of the Parameter
     *@return            The channelName value
     */
    public String getChannelName(String channelID) {
		
        int ch = channelIDs.indexOf(channelID);

        if (ch == -1) {

            addChannelName(channelID, channelID);

            return channelID;
        } else {

            String chName = (String) channelNames.get(ch);

            if (chName == null) {

                return channelID;
            } else {

                return chName;
            }

        }

    }
    
    /**
     * to get the Icon URL of the channel
     * @param channelID the Id of the channel to get URL for
     * @return the URL as a string;
     */
    public String getChannelIcon(String channelID){
    	if (channelID == null) return null;
    	return (String)channelIcons.get(channelID);
    }


    /**
     *  Description of the Method
     */
    private void parseError() {
        FreeGuide.log.severe("ViewerFrame - Error parsing XML.");
        System.exit(1);
    }


    // ---------------------------------------------------------------

    private String saxLoc = "";
    // Holds our current pos in the XML hierarchy
    private String tmpChannelID;
	private String data;

    private Programme currentProgramme;
    // The programme we're loading in now

    String fs = System.getProperty("file.separator");

    private Time day_start_time;

    private Time grabber_start_time;

    private String working_directory;

    /**
     *  Description of the Field
     */
    public Vector programmes;
    // Vector of loaded FreeGuideProgrammes
    private Vector channelIDs;
    // The IDs of the channels
    private Vector channelNames;
    // The names of the channels
    private Hashtable channelIcons;

    /**
     *  Description of the Field
     */
    public Calendar date;
    // The actual date we want (YMD, ignore time)

    /**
     *  Description of the Field
     */
    public Calendar earliest;
    // The time of the start of this day
    /**
     *  Description of the Field
     */
    public Calendar latest;
    // The time of the end of this day

    private boolean thereAreEarlyProgs;
    // 2 flags which indicate whether or
    private boolean thereAreLateProgs;
    // not there are programmes at the
    // beg. and end of today.

    private Calendar hasDataEarliest;
    // The start of the day in terms of
    // Whether there is enough data today
    private Calendar hasDataLatest;
    // Similarly the end

}

