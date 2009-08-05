package freeguide.plugins.importexport.xmltv;

import java.util.Collection;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVChannelsSet.Channel;

public interface ITVDataIterators
{
    public Collection getChannels(  );
    public Collection getProgrammes( final TVChannelsSet.Channel channel ) throws Exception;
    public TVChannel getRealChannel( Channel chinfo );
}
