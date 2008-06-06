package freeguide.common.lib.fgspecific.data;

public class TVProgrammeOverlapIsEqualComparator implements java.util.Comparator
{

    /**
     * Compare two programmes by their datetimes.
     *
     * @param prog1 A TVProgramme
     * @param prog2 another TVProgramme
     *
     * @return -1 if prog1 comes first,
     *          1 if prog2 comes first, and
     *          0 if they overlap.
     */
    public int compare( Object prog1, Object prog2 )
    {
        TVProgramme p1 = (TVProgramme)( prog1 );
        TVProgramme p2 = (TVProgramme)( prog2 );

        // If the end times have not been downloaded, we
        // must compare purely by start time.
        // If both end times are valid, we delare them equal if
        // They overlap at all, so we don't get overlapping
        // programmes showing.
        if( p1.getEnd() == 0 || p2.getEnd() == 0 )
        {
            if( p1.getStart() < p2.getStart() )
            {
                return -1;
            }
            else if( p1.getStart() > p2.getStart() )
            {
                return 1;
            }
            else
            {
                // If the start times are the same, they definitely overlap!
                return 0;
            }
        }
        else if( p1.getEnd() <= p2.getStart() )
        {
            return -1;
        }
        else if( p2.getEnd() <= p1.getStart() )
        {
            return 1;
        }

        return 0;
    }

}
