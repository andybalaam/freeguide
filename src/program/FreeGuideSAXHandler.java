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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class FreeGuideSAXHandler extends DefaultHandler {

	/**
	 * A SAX handler for processing XMLTV listings (not particularly
	 * comprehensively or cleverly).
	 *
	 * @param theDate 	a Calendar representing the date we're interested in in
	 * 					terms of whether there is a full set of listings for
	 *					that day.
	 */
    public FreeGuideSAXHandler(FreeGuideViewer viewer ) {
		
		this.viewer = viewer;
		
		viewer.programmes = new Vector();
		
		theDateStart 	= GregorianCalendar.getInstance();
		theDateEnd 		= GregorianCalendar.getInstance();
		
		theDateStart.setTime( viewer.theDate.getTime() );
		
		FreeGuideTime dayStartTime = FreeGuide.prefs.misc.getFreeGuideTime(
			"day_start_time", new FreeGuideTime(6, 0) );
			
		theDateStart.set( Calendar.HOUR_OF_DAY, dayStartTime.getHours() );
		theDateStart.set( Calendar.MINUTE, 		dayStartTime.getMinutes() );
		
		theDateEnd.setTime( theDateStart.getTime() );
		theDateEnd.add( Calendar.DATE, 1 );
		
		viewer.missingFiles = true;
		// Sets this to false when this day gets an early and a late programme
		
		dataMissingStart = true;
		dataMissingEnd = true;
		
		viewer.channelNamed = new Vector();
		for(int i=0;i<viewer.channelIDs.size();i++) {
			
			viewer.channelNamed.add( new Boolean("false") );
			
		}
    }

    public void startDocument() {  
		saxLoc = new String();
	}//startDocument
    
    public void endDocument() {
		saxLoc=null;
    }//endDocument
    
	public void startElement(String namespaceURI, String sName, String name, Attributes attrs) {

		saxLoc+=":"+name;
	
		//FreeGuide.log.info( saxLoc );
	
		if(saxLoc.equals(":tv:programme")) {
	    
			currentProgramme = new FreeGuideProgramme();

			// Prepare GregorianCalendars for start and end
			Calendar start = GregorianCalendar.getInstance();
			Calendar end = GregorianCalendar.getInstance();
			
			// Assume it has a channel
			String channelID = attrs.getValue("channel");
			currentProgramme.setChannelID(channelID);
			currentProgramme.addToChannelName(getChannelName(channelID));

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
		
		if(saxLoc.equals(":tv:programme")) {
			
			if (currentProgramme!=null && 
					currentProgramme.getEnd().after(viewer.earliest) &&
					currentProgramme.getStart().before(viewer.latest)) {
				
				if( currentProgramme.getStart().before(theDateStart) && 
						currentProgramme.getEnd().after(theDateStart) ) {
					
					dataMissingStart = false;
					
				}
				
				if( currentProgramme.getStart().before(theDateEnd) && 
						currentProgramme.getEnd().after(theDateEnd) ) {
					
					dataMissingEnd = false;
					
				}
				
				if((!dataMissingEnd) && (!dataMissingStart)) {
					
					viewer.missingFiles = false;
					
				}
				
				viewer.programmes.add(currentProgramme);
				
			}
			currentProgramme = null;
		}
		
		if(saxLoc.endsWith(name)) {
	    
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

			// If it's a channel we're interested in
			// and it's not been named already, remember the name
			int i = viewer.channelIDs.indexOf(tmpChannelID);
			//System.out.println(i + " - " +tmpChannelID+" - "+data);
			if(i!=-1) {
				if (viewer.channelNamed.get(i).equals(Boolean.FALSE)) {
					viewer.channelNames.set(viewer.channelIDs.indexOf(tmpChannelID),new String(data));
					viewer.channelNamed.set(viewer.channelIDs.indexOf(tmpChannelID),new Boolean("true"));
					//System.out.println("set: "+viewer.channelNames.get(viewer.channelIDs.indexOf(tmpChannelID)));
				}
				
				// FIXME - What are the viewer.channelLoaded criteria?
				// For now if it's in the file, it's loaded.
				viewer.channelLoaded.set(i,Boolean.TRUE);
			}
			// if it's a new channel, we can add it here
			if(i==-1) {
				viewer.channelIDs.add( tmpChannelID );
				viewer.channelNames.add( data );
				viewer.channelNamed.add( new Boolean(true) );
				viewer.channelLoaded.add( new Boolean(true) );
			}

		}//if

		//FreeGuide.log.info("characters END");

    }//characters

	// ------------------------------

	/**
	 * Returns the name of the channel whose ID is supplied.
	 */
	private String getChannelName(String channelID) {

		// If the ID exists
		int i = viewer.channelIDs.indexOf(channelID);
		if(i != -1) {
			
			return viewer.channelNames.get(i).toString();
			
		} else {
			
			// Deal with unnamed channels by naming them after their IDs
			viewer.channelIDs.add( channelID );
			viewer.channelNames.add( channelID );
			viewer.channelNamed.add( new Boolean(true) );
			viewer.channelLoaded.add( new Boolean(true) );
			
			return channelID;
			
		}

	}

	private void parseError() {
		FreeGuide.log.severe("FreeGuideViewer - Error parsing XML.");
		System.exit(1);
    }

	// ----------------------------------

	private String saxLoc;  // Holds our current pos in the XML hierarchy

	private String tmpChannelID;	// A temporary variable storing the channel ID

	private FreeGuideProgramme currentProgramme;
		// The programme we're loading in now

	//private Vector programmes;	// The vector of programmes we're filling
	//private String[] viewer.channelIDs;
	//private Vector viewer.channelIDs;
		// The IDs of the channels the user has chosen
	//private String[] viewer.channelNames;
	//private Vector viewer.channelNames;
		// The names of the channels the user has chosen
	//private boolean[] viewer.channelNamed;
	//private Vector viewer.channelNamed;
		// Has this channel had its name set?
	//private boolean[] viewer.channelLoaded;
	//private Vector viewer.channelLoaded;
	
	private FreeGuideViewer viewer;

	//Calendar earliest;
	//Calendar latest;
	
	Calendar theDateStart;
	Calendar theDateEnd;
	
	boolean dataMissingStart;
	boolean dataMissingEnd;
	
}
