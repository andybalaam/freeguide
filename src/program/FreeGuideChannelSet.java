import java.util.*;
/**
 * FreeGuideChannelSet.java
 * FreeGuide J2
 *
 * Copyright (c) 2002 by Dave Torok
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */
public interface FreeGuideChannelSet {
    public String getChannelSetName();
    public void setChannelSetName(String name);
    public Vector getChannelIDs();
    public Vector getChannelNames();
    public String getChannelName( String channelID );
    public String getChannelName( int i );
    public int getNoChannels();
    public int getChannelNo( String channelID );
}
