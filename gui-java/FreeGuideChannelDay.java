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

import java.text.*;
import java.util.*;

public class FreeGuideChannelDay {

    public FreeGuideChannelDay(String name) {
	this.name=name;
	programmes = new Vector();
    }

    public void addProgramme(String startTime) {
	addProgramme(startTime, null);
    }
    
    public void addProgramme(String startTime, String endTime) {
        
	FreeGuideProgramme prog = new FreeGuideProgramme();
	
	prog.setChannel(name);
	
	/*int year = Integer.parseInt(startTime.substring(0,4));
	int month = Integer.parseInt(startTime.substring(4,6));
	int day = Integer.parseInt(startTime.substring(6,8));
	int hour = Integer.parseInt(startTime.substring(8,10));
	int minute = Integer.parseInt(startTime.substring(10,12));
	 prog.setStart(new Date(year-1900,month-1,day,hour,minute));*/
	
	// Capture the start time
	SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmm");
	
	ParsePosition pos = new ParsePosition(0);

	prog.setStart(fmt.parse(startTime, pos));
	    
	
	// and the end time if they've given it
	if(endTime!=null && !endTime.equals("")) {
	    /*year = Integer.parseInt(endTime.substring(0,4));
	    month = Integer.parseInt(endTime.substring(4,6));
	    day = Integer.parseInt(endTime.substring(6,8));
	    hour = Integer.parseInt(endTime.substring(8,10));
	    minute = Integer.parseInt(endTime.substring(10,12));
	    prog.setEnd(new Date(year,month,day,hour,minute));*/
	    
	    ParsePosition endpos = new ParsePosition(0);
	    prog.setEnd(fmt.parse(endTime, endpos));
	}
	
	setPreviousEndTime(prog);
	
	programmes.add(prog);
	
    }
    
    private void setPreviousEndTime(FreeGuideProgramme newProg) {
	// If no end time was given for the previous programme, we can
	// now get one from this one
	
	int progLen = programmes.size();
	
	// If we're on the first programme then there isn't one before us
	if(progLen>0) {
	    
	    FreeGuideProgramme prevProg = (FreeGuideProgramme)programmes.lastElement();
	    
	    // If the previous programme has no end time
	    if(prevProg.getEnd()==null) {
		prevProg.setEnd(new Date(newProg.getStart().getTime()));
	    }
	    
	}
	
    }
    
    public FreeGuideProgramme getLatestProg() {
	return (FreeGuideProgramme)programmes.lastElement();
    }
    
    public Date getStart() {
	
	if(programmes!=null && programmes.size()>0) {
	    
	    return new Date(((FreeGuideProgramme)programmes.get(0)).getStart().getTime());
	    
	} else {
	    return null; 
	}
	
    }
    
    public Date getEnd() {
	
	if(programmes!=null && programmes.size()>0) {
	    
	    FreeGuideProgramme prog = (FreeGuideProgramme)programmes.lastElement();
	    return prog.getSomeEnd();
	    
	} else {
	    return null; 
	}
	
    }
    
    public Vector getProgrammes() {
	return programmes;
    }
    
    public String getName() {
	return name;
    }
    
    private String name;    // The name of this channel
    
    private Vector programmes;
	// The FreeGuideProgramme objects that hold the programmes
}
