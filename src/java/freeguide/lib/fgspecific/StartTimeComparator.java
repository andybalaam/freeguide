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

import freeguide.lib.fgspecific.data.TVProgramme;

import java.util.Comparator;

/**
 * Compares 2 programmes by theit start time
 *
 * @author Andy Balaam
 * @version 1
 */
public class StartTimeComparator implements Comparator
{

    /**
     * Constructor for the FreeGuideProgrammeStartTimeComparator object
     */
    public StartTimeComparator(  )
    {
    }

    /**
     * Description of the Method
     *
     * @param obj1 Description of the Parameter
     * @param obj2 Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public int compare( Object obj1, Object obj2 )
    {

        TVProgramme prog1 = (TVProgramme)obj1;

        TVProgramme prog2 = (TVProgramme)obj2;

        if( prog1.getStart(  ) < prog2.getStart(  ) )
        {

            return -1;

        }

        else if( prog1.getStart(  ) > prog2.getStart(  ) )
        {

            return 1;

        }

        else
        {

            // If they start at exactly the same time
            return 0;

        }
    }
}
