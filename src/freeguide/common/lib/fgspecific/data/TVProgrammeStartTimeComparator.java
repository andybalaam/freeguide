package freeguide.common.lib.fgspecific.data;

public class TVProgrammeStartTimeComparator implements java.util.Comparator
{

    /**
     * Compare TV programmes based purely on their start times.
     *
     * @param prog1 A TVProgramme
     * @param prog2 another TVProgramme
     *
     * @return -1 if prog1 starts before prog2
     *          1 if prog1 starts after prog2
     *          0 if they start at the same time
     */
    public int compare( Object prog1, Object prog2 )
    {
        TVProgramme p1 = (TVProgramme)( prog1 );
        TVProgramme p2 = (TVProgramme)( prog2 );

        if( p1.getStart() < p2.getStart() )
        {
            return -1;
        }
        else if( p1.getStart() > p2.getStart() )
        {
            return 1;
        }

        return 0;
    }

}
