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

import java.util.Date;

public class FreeGuideProgramme {

    /* A class that holds info about a particular programme */
    
    public FreeGuideProgramme() {
    }

    public void setStart(Date start) {
	this.start=start;
    }
    public Date getStart() {
	return start;
    }
    
    public void setEnd(Date end) {
	this.end=end;
    }
    public Date getEnd() {
	return end;
    }
    public Date getSomeEnd() {
	// Return some end time, either real or made-up
	if(getEnd()!=null) {
	    return getEnd();
	} else {
		// The start time plus an hour
	    return new Date(getStart().getTime() + 1*60*60*1000);
	}
    }
    
    public void setTitle(String title) {
	getStart();
	this.title=title;
    }
    public String getTitle() {
	return title;
    }
    
    public void setDesc(String desc) {
	this.desc=desc;
    }
    public String getDesc() {
	return desc;
    }
    
    public void setChannel(String channel) {
	this.channel=channel;
    }
    public String getChannel() {
	return channel;
    }
    
    public void setCategory(String category) {
	this.category=category;
    }
    public String getCategory() {
	return category;
    }
    
    private Date start;		// The start time
    private Date end;		// The end time
    private String title;	// The programme title
    private String desc;	// The programme description
    private String channel;	// The channel the prog's on
    private String category;	// The category it fits into
    
}
