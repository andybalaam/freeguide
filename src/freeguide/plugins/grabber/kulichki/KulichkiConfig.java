package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.fgspecific.data.TVChannelsSelection;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;

import java.util.ResourceBundle;

/**
 * Class for store config information.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class KulichkiConfig
{
    /** DOCUMENT ME! */
    public TVChannelsSelection channels = new TVChannelsSelection(  );

/**
     * Creates a new Config object.
     */
    public KulichkiConfig( final ResourceBundle i18n )
    {
        if( i18n != null )
        {
            channels.allChannels.add( 
                new TVChannelsSet.Channel( 
                    GrabberKulichki.CHANNEL_PREFIX_ID,
                    i18n.getString( "MainChannelName" ) ) );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object clone(  )
    {
        KulichkiConfig result = new KulichkiConfig( null );

        result.channels = (TVChannelsSelection)channels.clone(  );

        return result;

    }
}
