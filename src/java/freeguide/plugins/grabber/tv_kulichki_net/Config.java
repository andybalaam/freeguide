package freeguide.plugins.grabber.tv_kulichki_net;

import freeguide.lib.fgspecific.data.TVChannelsSelection;
import freeguide.lib.fgspecific.data.TVChannelsSet;

/**
 * Class for store config information.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class Config
{

    /** DOCUMENT ME! */
    public TVChannelsSelection channels = new TVChannelsSelection(  );

    /**
     * Creates a new Config object.
     */
    public Config(  )
    {
        channels.allChannels.add( 
            new TVChannelsSet.Channel( GrabberKulichki.ID, "All" ) );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object clone(  )
    {

        Config result = new Config(  );

        result.channels = (TVChannelsSelection)channels.clone(  );

        return result;

    }
}
