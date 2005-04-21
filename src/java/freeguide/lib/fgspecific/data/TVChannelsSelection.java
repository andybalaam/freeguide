package freeguide.lib.fgspecific.data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class for store channels selection. For example, for grabbers.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class TVChannelsSelection
{

    /** DOCUMENT ME! */
    public static final Class selectedChannelIDs_TYPE = String.class;

    /** DOCUMENT ME! */
    public TVChannelsSet allChannels = new TVChannelsSet(  );

    /** DOCUMENT ME! */
    public Set selectedChannelIDs = new TreeSet(  );

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object clone(  )
    {

        TVChannelsSelection result = new TVChannelsSelection(  );

        result.allChannels = allChannels;

        result.selectedChannelIDs.addAll( selectedChannelIDs );

        return result;

    }

    /**
     * DOCUMENT_ME!
     */
    public void normalize(  )
    {

        Set ids = new TreeSet(  );

        Iterator it = allChannels.getChannels(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            TVChannelsSet.Channel ch = (TVChannelsSet.Channel)it.next(  );

            ids.add( ch.getChannelID(  ) );

        }

        it = selectedChannelIDs.iterator(  );

        while( it.hasNext(  ) )
        {

            if( !ids.contains( it.next(  ) ) )
            {
                it.remove(  );

            }
        }
    }
}
