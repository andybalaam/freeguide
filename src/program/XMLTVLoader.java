/*
 * FreeGuide
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
//import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;


//import java.util.Arrays;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.Vector;

//import org.xml.sax.helpers.DefaultHandler;
 
/**
 * XMLTVLoader
 *
 * Loads the required XMLTV files for a given date into a Vector of programmes
 * and stores other relevant details e.g. channel details
 *
 */
 

class XMLTVLoader extends DefaultHandler {
	
	/**
	 * Loads the programme data from a file and stores it in a class
	 * structure ready for display on the screen.
	 *
	 * @param nowDate		The date and time for which to load programmes
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
		Calendar yesterday = (Calendar)date.clone();
		yesterday.add( Calendar.DAY_OF_YEAR, -1 );
		
		// Similarly get tomorrow
		Calendar tomorrow = (Calendar)date.clone();
		tomorrow.add( Calendar.DAY_OF_YEAR, 1 );
		
		String date1str;
		String date2str;
		
		//FreeGuide.log.info( "grabber_start_time=" + grabber_start_time + 
		//	" day_start_time=" + day_start_time );
		
		// See whether we need to grab yesterday or tomorrow
		if( grabber_start_time.before( day_start_time,
				new FreeGuideTime(0,0) ) ) {
			
			// Example:
			// grabber grabs midnight to midnight and we want to see 02:00 today
			// to 02:00 tomorrow: need to grab today and tomorrow
			
			date1str = FreeGuideViewer.fileDateFormat.format( date.getTime() );
			date2str = FreeGuideViewer.fileDateFormat.format(
				tomorrow.getTime() );
			
			//FreeGuide.log.info( "Getting tomorrow data" );
			
		} else {
			
			// Example:
			// grabber grabs 06:00 today to 06:00 tomorrow and we want to see
			// midnight to midnight: need to grab today and yesterday
			
			date1str = FreeGuideViewer.fileDateFormat.format(
				yesterday.getTime() );
			date2str = FreeGuideViewer.fileDateFormat.format(
				date.getTime() );
				
			//FreeGuide.log.info( "Getting yesterday data" );
			
		}
		
		String day1Filename  = working_directory + fs + "tv-" +
			date1str + ".xmltv";
		
		String day2Filename = working_directory + fs + "tv-" + 
			date2str + ".xmltv";
		
		String unprocFilename = working_directory + fs + "tv-unprocessed.xmltv";

		File day1File = new File( day1Filename );
		File day2File = new File( day2Filename );
		File unprocFile = new File( unprocFilename );
			
		// Parse any files that exist
		
		try {//ParserExceptions etc

			//DefaultHandler handler = new FreeGuideSAXHandler( this );
			
			SAXParserFactory factory = SAXParserFactory.newInstance();

			SAXParser saxParser = factory.newSAXParser();

			boolean day1FileExists = day1File.exists();
			boolean day2FileExists = day2File.exists();
			
			// If either day file exists (or both), parse it/them.  Otherwise
			// get the unproc. listings if they exist.
			
			if( day1FileExists ) {
				
				saxParser.parse( day1Filename, this );
				
			}
			
			if( day2FileExists ) {
				
				saxParser.parse( day2Filename, this );
				
			}
			
			if( (!day1FileExists) && (!day2FileExists) && unprocFile.exists() ){
				
					// The grabber must not be able to split into days,
					// so we'll deal with the unprocessed data.
					saxParser.parse( unprocFilename, this );
				
			}

		} catch(ParserConfigurationException e) {
			e.printStackTrace();
			// FIXME - error dialog!
		} catch(SAXException e) {
			e.printStackTrace();
			// FIXME - error dialog!
		} catch(java.io.IOException e) {
			e.printStackTrace();
			// FIXME - error dialog!
		}//try
		
	}//loadProgrammeData

	// Returns true if there was "enough" data for today and false if some was
	// missing.
	public boolean hasData() {
		
		// Normal:
		return (thereAreEarlyProgs && thereAreLateProgs);
		
	}
	
	// ----------------------------------------------------------------------

	/**
	 * Alter date to reflect if we are in another day's data (i.e. before the
	 * day start time) and set earliest and latest to the right values according
	 * to the day start time
	 */
	public void updateDaySpan( Calendar nowDate ) {

		day_start_time = FreeGuide.prefs.misc.getFreeGuideTime(
			"day_start_time", new FreeGuideTime(6, 0) );
		
		grabber_start_time = FreeGuide.prefs.misc.getFreeGuideTime(
			"grabber_start_time", new FreeGuideTime(0, 0) );
		
		working_directory = FreeGuide.prefs.performSubstitutions(
			FreeGuide.prefs.misc.get("working_directory") );
		
		//earliest = GregorianCalendar.getInstance();
		//latest = GregorianCalendar.getInstance();
		
		date = (Calendar)nowDate.clone();
		
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
		earliest = (Calendar)date.clone();
		
		day_start_time.adjustCalendar( earliest );
		
		// Set latest to the start time on the day after the date
		latest = (Calendar)date.clone();
		
		latest.add( Calendar.DAY_OF_YEAR, 1);
		
		day_start_time.adjustCalendar( latest );

    }
	
	private void setupHasDataStuff( Calendar nowDate ) {
		
		// There must be a programme crossing over both of these times in order
		// for the day to be "covered" i.e. we don't need to download more.
		hasDataEarliest = (Calendar)earliest.clone();
		hasDataLatest = (Calendar)latest.clone();
		
		// If it's today then hasDataEarliest is now-ish
		if( dayIsToday( nowDate ) ) { 
			
			hasDataEarliest.setTimeInMillis( nowDate.getTimeInMillis() );
			
		}
		
		// Now add an hour's grace to start time
		hasDataEarliest.add( Calendar.HOUR, 1 );
		
		// and remove an hour from the end time
		hasDataLatest.add( Calendar.HOUR, -1 );
		
	}
	
	private boolean dayIsToday( Calendar nowDate ) {
		
		Calendar today = GregorianCalendar.getInstance();
		
		FreeGuideTime nowTime = new FreeGuideTime( today );
		
		// If we're before the day start time then go to the previous day
		if( nowTime.before( day_start_time, new FreeGuideTime( 0, 0 ) ) ) {
			
			today.add( Calendar.DATE, -1 );
			
		}
		
		// Now check whether the dates are equal.
		return( nowDate.get( Calendar.YEAR ) == today.get( Calendar.YEAR ) &&
			nowDate.get( Calendar.DAY_OF_YEAR ) == 
				today.get( Calendar.DAY_OF_YEAR ) );
		
	}
	
	// ----------------------------------------------------------------------
	
	public void startDocument() {  
		saxLoc = new String();
	}//startDocument
    
    public void endDocument() {
		saxLoc=null;
    }//endDocument
    
	public void startElement(String namespaceURI, String sName, String name,
			Attributes attrs) {

		saxLoc += ":" + name;
	
		//FreeGuide.log.info( saxLoc );
	
		if( saxLoc.equals( ":tv:programme" ) ) {
	    
			currentProgramme = new FreeGuideProgramme();

			// Prepare GregorianCalendars for start and end
			Calendar start = GregorianCalendar.getInstance();
			Calendar end = GregorianCalendar.getInstance();
			
			// Assume it has a channel
			String channelID = attrs.getValue( "channel" );
			currentProgramme.setChannelID( channelID );
			currentProgramme.addToChannelName( getChannelName( channelID ) );

			try {
				
				// Assume is has a start time
				start = parseDate( attrs.getValue("start") );
				
				// Don't assume it has an end time
				if( attrs.getIndex("stop") == -1
						|| attrs.getValue("stop").equals("+0100") ) {
							// Also hack around bug in de grabber
					
					// Give it a fake end time, half an hour after the start
					end.setTimeInMillis(start.getTimeInMillis());
					end.add(Calendar.MINUTE, 30);
					
					
				} else {
					
					end = parseDate( attrs.getValue("stop") );
					
				}
			
			} catch(java.text.ParseException e) {
				e.printStackTrace();
				currentProgramme = null;
				
				return;
			}

			if( start.before( hasDataEarliest ) ) {
						
				thereAreEarlyProgs = true;
						
			}
			
			if( end.after( hasDataLatest ) ) {
						
				thereAreLateProgs = true;
						
			}
			
			currentProgramme.setStart(start);
			currentProgramme.setEnd(end);
	    
		} else if(saxLoc.equals(":tv:channel")) {
			
			String id = attrs.getValue("id");

			tmpChannelID = id;
			
		}//if

		
    }//startElement
  
  	private Calendar parseDate( String strDate )
			throws java.text.ParseException {
	 
	 SimpleDateFormat normalFmt = new SimpleDateFormat("yyyyMMddHHmmss z");
	 SimpleDateFormat deFmt = new SimpleDateFormat("yyyyMMddHHmm Z");
	 
	 Calendar ans = GregorianCalendar.getInstance();
	 
	 try {
		 
		 ans.setTime( normalFmt.parse( strDate ) );
		 
	 } catch(java.text.ParseException e) {
		 
		 ans.setTime( deFmt.parse( strDate ) );
		 
	 }
	 
	 return ans;
	 
	}
  
  	public void endElement(String namespaceURI, String sName, String name) {  
		
		//FreeGuide.log.info(name);
		
		if( saxLoc.equals( ":tv:programme" ) ) {
				
			if( currentProgramme.getEnd().after( earliest ) &&
					currentProgramme.getStart().before( latest ) ) {
				
				programmes.add(currentProgramme);
			}
			
			currentProgramme = null;
		}
		
		if( saxLoc.endsWith( name ) ) {
	    
			saxLoc = saxLoc.substring(0, saxLoc.length() - (name.length()+1));
	    
		} else {
			parseError();
		}//if
	
		//FreeGuide.log.info("endElement END");
		
    }//endElement
    
	public void characters(char[] ch, int start, int length) {
	
		String data = new String(ch, start, length);
	
		//FreeGuide.log.info("characters "+ data + " START");
		
		if(saxLoc.equals(":tv:programme:title")) {
	    
			if(currentProgramme!=null) {
				currentProgramme.addToTitle(data);
			}
	    
		} else if (saxLoc.equals(":tv:programme:desc")) {
	    
			if(currentProgramme!=null) {
				currentProgramme.addDesc(data);
			}
	    
		} else if (saxLoc.equals(":tv:programme:category")) {
	    
			if(currentProgramme!=null) {
				currentProgramme.addCategory(data);
			}

		} else if (saxLoc.equals(":tv:channel:display-name")) {

			// Remember the name of the channel we're looking at

			addChannelName( tmpChannelID, data );
			//channelLoaded.put( tmpChannelID, Boolean.FALSE );
			
			/*String chName = channels.get( tmpChannelID );
			
			// If it's a channel we're interested in
			// and it's not been named already, remember the name
			int i = channelIDs.indexOf(tmpChannelID);
			//System.out.println(i + " - " +tmpChannelID+" - "+data);
			if(i!=-1) {
				if (channelNamed.get(i).equals(Boolean.FALSE)) {
					channelNames.set(channelIDs.indexOf(tmpChannelID),new String(data));
					channelNamed.set(channelIDs.indexOf(tmpChannelID),new Boolean("true"));
					//System.out.println("set: "+channelNames.get(channelIDs.indexOf(tmpChannelID)));
				}
				
				// FIXME - What are the channelLoaded criteria?
				// For now if it's in the file, it's loaded.
				channelLoaded.set(i,Boolean.TRUE);
			}
			// if it's a new channel, we can add it here
			if(i==-1) {
				channelIDs.add( tmpChannelID );
				channelNames.add( data );
				channelNamed.add( new Boolean(true) );
				channelLoaded.add( new Boolean(true) );
			}*/

		}//if

		//FreeGuide.log.info("characters END");

    }//characters

	// -----------------------------------------------------------------------

	public Vector getChannelIDs() {
		return channelIDs;
	}
	
	public Vector getChannelNames() {
		return channelNames;
	}
	
	private void addChannelName( String channelID, String channelName ) {
		
		if( channelIDs.indexOf( channelID ) == -1 ) {
		
			channelIDs.add( channelID );
			channelNames.add( channelName );
			
		}
		
	}
	
	public String getChannelName( int i ) {
		
		return (String)channelNames.get( i );
		
	}
	
	public int getNoChannels() {
		
		return channelIDs.size();
		
	}
	
	public int getChannelNo( String channelID ) {
		
		return channelIDs.indexOf( channelID );
		
	}
	
	/**
	 * Returns the name of the channel whose ID is supplied.
	 */
	public String getChannelName( String channelID ) {

		int ch = channelIDs.indexOf( channelID );
		
		if( ch == -1 ) {

			addChannelName( channelID, channelID );
			
			return channelID;
			
		} else {
			
			String chName = (String)channelNames.get( ch );
			
			if( chName == null ) {
			
				return channelID;
			
			} else {
				
				return chName;
				
			}
			
		}
		
	}

	private void parseError() {
		FreeGuide.log.severe("FreeGuideViewer - Error parsing XML.");
		System.exit(1);
    }

	// ---------------------------------------------------------------

	private String saxLoc = "";  // Holds our current pos in the XML hierarchy
	private String tmpChannelID;
	
	private FreeGuideProgramme currentProgramme;
		// The programme we're loading in now
	
	String fs = System.getProperty("file.separator");
	
	private FreeGuideTime day_start_time; 
		
	private FreeGuideTime grabber_start_time;
	
	private String working_directory;
	
	public Vector programmes;	// Vector of loaded FreeGuideProgrammes
	private Vector channelIDs;	// The IDs of the channels
	private Vector channelNames;	// The names of the channels
	
	public Calendar date;		// The actual date we want (YMD, ignore time)
	
	public Calendar earliest;	// The time of the start of this day
	public Calendar latest;	// The time of the end of this day
	
	private boolean thereAreEarlyProgs;	// 2 flags which indicate whether or
	private boolean thereAreLateProgs;	// not there are programmes at the
										// beg. and end of today.
										
	private Calendar hasDataEarliest;	// The start of the day in terms of
										// Whether there is enough data today
	private Calendar hasDataLatest;		// Similarly the end
	
}


