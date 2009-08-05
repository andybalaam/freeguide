package freeguide.test.fast;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;

public class ModifiableTVData extends TVData
{
    public void addChannel( TVChannel channel )
    {
        channels.put( channel.getID(), channel );
    }
}
