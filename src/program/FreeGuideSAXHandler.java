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

    public FreeGuideSAXHandler(Vector programmes, String[] channelIDs, String[] channelNames) {
		this.programmes = programmes;
		this.channelIDs = channelIDs;
		this.channelNames = channelNames;
		channelNamed = new boolean[channelIDs.length];
    }

    public void startDocument() {  
		saxLoc = new String();
	}//startDocument
    
    public void endDocument() {
		saxLoc=null;
    }//endDocument
    
	public void startElement(String namespaceURI, String sName, String name, Attributes attrs) {
		
		//FreeGuide.log.info("startElement " + name);
		
		saxLoc+=":"+name;
	
		if(saxLoc.equals(":tv:programme")) {
	    
			//assert currentProgramme == null;
			
			currentProgramme = new FreeGuideProgramme();
			
			// Prepare a date formatter
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss z");
			
			// Prepare GregorianCalendars for start and end
			Calendar start = GregorianCalendar.getInstance();
			Calendar end = GregorianCalendar.getInstance();
			
			// Assume it has a channel
			String channelID = attrs.getValue("channel");
			currentProgramme.setChannelID(channelID);
			currentProgramme.addToChannelName(getChannelName(channelID));
			
			try {
			
				// Assume it has a start time
				start.setTime(fmt.parse(attrs.getValue("start")));
						
				// Could really assume it has an end time, since it's gone through
				// tv_split, but anyway, don't.
				if(attrs.getIndex("stop") != -1) {
					end.setTime(fmt.parse(attrs.getValue("stop")));
				} else {
					// Give it a fake end time, half an hour after the start
					end.setTimeInMillis(start.getTimeInMillis());
					end.add(Calendar.MINUTE, 30);
				}
				
			} catch(java.text.ParseException e) {
				e.printStackTrace();
			}
			
			currentProgramme.setStart(start);
			currentProgramme.setEnd(end);
	    
		} else if(saxLoc.equals(":tv:channel")) {
			
			String id = attrs.getValue("id");
			
			tmpChannelID = id;
			
		}//if
		
		//FreeGuide.log.info("startElement END");
		
    }//startElement
  
  	public void endElement(String namespaceURI, String sName, String name) {  
		
		//FreeGuide.log.info(name);
		
		if(saxLoc.equals(":tv:programme")) {
			programmes.add(currentProgramme);
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
	    
			//System.out.println(data);
		
			currentProgramme.addToTitle(data);
	    
		} else if (saxLoc.equals(":tv:programme:desc")) {
	    
			currentProgramme.addDesc(data);
	    
		} else if (saxLoc.equals(":tv:programme:category")) {
	    
			currentProgramme.addCategory(data);
	    
		} else if (saxLoc.equals(":tv:channel:display-name")) {
			
			// Remember the name of the channel we're looking at
			
			// Get the channelIDs into a Vector
			Vector tmpChannelIDs = new Vector(Arrays.asList(channelIDs));
			
			// If it's a channel we're interested in
			// and it's not been named already, remember the name
			int i = tmpChannelIDs.indexOf(tmpChannelID);
			if(i!=-1 && !channelNamed[i]) {
				channelNames[i] = data;
				channelNamed[i] = true;
			}
			
		}//if
	
		//FreeGuide.log.info("characters END");
		
    }//characters

	// ------------------------------
	
	/**
	 * Returns the name of the channel whose ID is supplied.
	 */
	private String getChannelName(String channelID) {
		
		// Get the channelIDs into a Vector
		Vector tmpChannelIDs = new Vector();
		tmpChannelIDs.addAll(Arrays.asList(channelIDs));
		
		// If the ID exists
		int i = tmpChannelIDs.indexOf(tmpChannelID);
		if(i != -1) {
			return channelNames[i];
		} else {
			FreeGuide.log.warning("Unknown channel ID in request for channel name.");
			return "Unknown Channel";
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
		
	private Vector programmes;	// The vector of programmes we're filling
	private String[] channelIDs;
		// The IDs of the channels the user has chosen
    private String[] channelNames;
		// The names of the channels the user has chosen
	private boolean[] channelNamed;
		// Has this channel had its name set?
	
}
