package freeguide.plugins.ui.vertical.simple;

import freeguide.common.lib.fgspecific.data.TVProgramme;

import java.util.Comparator;

/**
 * Compares two programs by their starting time. If they start at the same
 * time, it compares the end time.
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class ProgrammeTimeComparator implements Comparator
{
    /**
     * DOCUMENT_ME!
     *
     * @param object1 DOCUMENT_ME!
     * @param object2 DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int compare( Object object1, Object object2 )
    {
        TVProgramme p1 = (TVProgramme)object1;
        TVProgramme p2 = (TVProgramme)object2;

        if( p1.getStart(  ) > p2.getStart(  ) )
        {
            return 1;
        }
        else if( p1.getStart(  ) < p2.getStart(  ) )
        {
            return -1;
        }
        else if( p1.getEnd(  ) > p2.getEnd(  ) )
        {
            return 1;
        }
        else if( p1.getEnd(  ) < p2.getEnd(  ) )
        {
            return -1;
        }
        else
        {
            return 0;
        }
    }

    //public int compare(Object object1, Object object2)
}
//public class ProgrammeTimeComparator
