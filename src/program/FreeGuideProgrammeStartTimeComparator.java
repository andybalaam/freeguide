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

import java.util.*;

public class FreeGuideProgrammeStartTimeComparator implements Comparator {

    public FreeGuideProgrammeStartTimeComparator() {
    }
    
    public int compare(Object obj1, Object obj2) {
	
	// First is earlier => return -1
	// First is later   => return  1
	
	FreeGuideProgramme prog1 = (FreeGuideProgramme)obj1;
	FreeGuideProgramme prog2 = (FreeGuideProgramme)obj2;
	
	if(prog1.getStart().before(prog2.getStart())) {
	    
	    return -1;
	    
	} else if(prog1.getStart().after(prog2.getStart())) {
	    
	    return 1;
	    
	} else {    // If they start at exactly the same time
	    
	    return 0;
	    
	}
	
    }   

}
