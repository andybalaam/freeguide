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

import java.util.*;

/**
 * FreeGuideChannelSet.java FreeGuide J2 Copyright (c) 2002 by Dave Torok
 * freeguide-tv.sourceforge.net Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY. See the file COPYING for more information.
 *
 * @author Dave Torok
 */
public interface ChannelSetInterface
{

    /**
     * Gets the channelSetName attribute of the ChannelSet object
     *
     * @return The channelSetName value
     */
    public String getChannelSetName(  );

    /**
     * Sets the channelSetName attribute of the ChannelSet object
     *
     * @param name The new channelSetName value
     */
    public void setChannelSetName( String name );

    /**
     * Gets the channels of the ChannelSet object
     *
     * @return The channels objects
     */
    public Vector getChannels(  );

    /**
     * Gets one of the ChannelSet object
     *
     * @param channelID Description of the Parameter
     *
     * @return The channel object
     */
    public Channel getChannel( String channelID );

    /**
     * Gets one of the ChannelSet object
     *
     * @param i indicia of the channel
     *
     * @return The channel object
     */
    public Channel getChannel( int i );

    /**
     * Gets the noChannels attribute of the ChannelSet object
     *
     * @return The noChannels value
     */
    public int getNoChannels(  );

    /**
     * Gets the channelNo attribute of the ChannelSet object
     *
     * @param channel The channel to get the pos for
     *
     * @return The channelNo value
     */
    public int getChannelNo( Channel channel );
}
