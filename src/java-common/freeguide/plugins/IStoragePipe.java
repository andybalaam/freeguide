package freeguide.common.plugins;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;

/**
 * Interface for declare methods for store partially data from grabbers.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IStoragePipe
{
    void addChannel( final TVChannel channel ) throws Exception;

    void addProgramme( final String channelID, final TVProgramme programme )
        throws Exception;

    void addProgrammes( 
        final String channelID, final TVProgramme[] programmes )
        throws Exception;

    void addData( final TVData data ) throws Exception;

    void finishBlock(  ) throws Exception;
}
