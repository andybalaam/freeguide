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

import java.util.*;

/**
 *  Compares 2 programmes by theit start time
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    1
 */
public class StartTimeComparator implements Comparator {

    /**
     *  Constructor for the FreeGuideProgrammeStartTimeComparator object
     */
    public StartTimeComparator() { }


    /**
     *  Description of the Method
     *
     *@param  obj1  Description of the Parameter
     *@param  obj2  Description of the Parameter
     *@return       Description of the Return Value
     */
    public int compare(Object obj1, Object obj2) {
        Programme prog1 = (Programme) obj1;
        Programme prog2 = (Programme) obj2;

        if (prog1.getStart().before(prog2.getStart())) {

            return -1;
        } else if (prog1.getStart().after(prog2.getStart())) {

            return 1;
        } else {
            // If they start at exactly the same time

            return 0;
        }

    }

}
