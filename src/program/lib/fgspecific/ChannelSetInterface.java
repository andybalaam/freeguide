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

package freeguidetv.lib.fgspecific;

import java.util.*;

/**
 *  FreeGuideChannelSet.java FreeGuide J2 Copyright (c) 2002 by Dave Torok
 *  freeguide-tv.sourceforge.net Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY. See the file COPYING for more information.
 *
 *@author     Dave Torok
 *@created    28 June 2003
 */
public interface ChannelSetInterface {
    /**
     *  Gets the channelSetName attribute of the ChannelSet object
     *
     *@return    The channelSetName value
     */
    public String getChannelSetName();


    /**
     *  Sets the channelSetName attribute of the ChannelSet object
     *
     *@param  name  The new channelSetName value
     */
    public void setChannelSetName(String name);


    /**
     *  Gets the channelIDs attribute of the ChannelSet object
     *
     *@return    The channelIDs value
     */
    public Vector getChannelIDs();


    /**
     *  Gets the channelNames attribute of the ChannelSet object
     *
     *@return    The channelNames value
     */
    public Vector getChannelNames();


    /**
     *  Gets the channelName attribute of the ChannelSet object
     *
     *@param  channelID  Description of the Parameter
     *@return            The channelName value
     */
    public String getChannelName(String channelID);


    /**
     *  Gets the channelName attribute of the ChannelSet object
     *
     *@param  i  Description of the Parameter
     *@return    The channelName value
     */
    public String getChannelName(int i);


    /**
     *  Gets the noChannels attribute of the ChannelSet object
     *
     *@return    The noChannels value
     */
    public int getNoChannels();


    /**
     *  Gets the channelNo attribute of the ChannelSet object
     *
     *@param  channelID  Description of the Parameter
     *@return            The channelNo value
     */
    public int getChannelNo(String channelID);
}
