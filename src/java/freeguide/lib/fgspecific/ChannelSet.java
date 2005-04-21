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

import freeguide.*;

import java.util.*;

/**
 * Standard implementation of FreeGuideChannelSet. XMLTVLoader is also a
 * FreeGuideChannelSet
 *
 * @author Andy Balaam
 */
public class ChannelSet implements ChannelSetInterface
{

    private Vector channels;
    private String name;

    /**
     * Constructor for the FreeGuideChannelSetImpl object
     */
    public ChannelSet(  )
    {
        channels = new Vector(  );

        name = FreeGuide.msg.getString( "new_channel_set" );

    }

    /**
     * Gets the channelSetName attribute of the FreeGuideChannelSetImpl object
     *
     * @return The channelSetName value
     */
    public String getChannelSetName(  )
    {

        return name;

    }

    /**
     * Sets the channelSetName attribute of the FreeGuideChannelSetImpl object
     *
     * @param name The new channelSetName value
     */
    public void setChannelSetName( String name )
    {
        this.name = name;

    }

    /**
     * Gets the channels attribute of the FreeGuideChannelSetImpl object
     *
     * @return The channels value
     */
    public Vector getChannels(  )
    {

        return channels;

    }

    /**
     * convenience function, since channel set names are not available when
     * retrieving from the Preferences
     *
     * @param nameprovider Description of the Parameter
     */
    public void updateChannelNames( ChannelSetInterface nameprovider )
    {

        if( nameprovider == null )
        {

            return;

        }

        for( int i = 0; i < channels.size(  ); i++ )
        {

            Channel anonChan = (Channel)channels.elementAt( i );

            Channel namedChan = nameprovider.getChannel( anonChan.getID(  ) );

            if( 
                anonChan.getID(  ).equals( anonChan.getName(  ) )
                    && ( namedChan != null )
                    && !namedChan.getID(  ).equals( namedChan.getName(  ) ) )
            {
                channels.set( i, namedChan );

            }
        }
    }

    /**
     * Gets the channel attribute of the FreeGuideChannelSetImpl object
     *
     * @param channelID Description of the Parameter
     *
     * @return The channelName value
     */
    public Channel getChannel( String channelID )
    {

        int ch = channels.indexOf( channelID );

        if( ch == -1 )
        {

            Channel c = new Channel( channelID, channelID, null );

            addChannel( c );

            return c;

        }

        else
        {

            Channel c = (Channel)channels.get( ch );

            if( c == null )
            {

                return new Channel( channelID, channelID, null );

            }

            else
            {

                return c;

            }
        }
    }

    /**
     * Gets the channelName attribute of the FreeGuideChannelSetImpl object
     *
     * @param i Description of the Parameter
     *
     * @return The channelName value
     */
    public Channel getChannel( int i )
    {

        //System.out.println( "getChannelName(" + i + ") = " +  channelNames.get(i));
        return (Channel)channels.get( i );

    }

    /**
     * Gets the noChannels attribute of the FreeGuideChannelSetImpl object
     *
     * @return The noChannels value
     */
    public int getNoChannels(  )
    {

        return channels.size(  );

    }

    /**
     * Gets the channelNo attribute of the FreeGuideChannelSetImpl object
     *
     * @param channel Description of the Parameter
     *
     * @return The channelNo value
     */
    public int getChannelNo( Channel channel )
    {

        return channels.indexOf( channel );

    }

    /**
     * Adds a feature to the ChannelName attribute of the
     * FreeGuideChannelSetImpl object
     *
     * @param channel The feature to be added to the Channels attribute
     */
    public void addChannel( Channel channel )
    {

        if( channels.indexOf( channel ) == -1 )
        {
            channels.add( channel );

        }
    }

    /**
     * add a set of channels and channel names from a pipe-separated String. A
     * channel set provider (if supplied) will be used to look-up channel
     * names.
     *
     * @param channels The feature to be added to the ChannelsFromString
     *        attribute
     * @param nameprovider The feature to be added to the ChannelsFromString
     *        attribute
     */
    public void addChannelsFromString( 
        String channels, ChannelSetInterface nameprovider )
    {

        if( channels == null )
        {

            return;

        }

        StringTokenizer st = new StringTokenizer( channels, "|" );

        while( st.hasMoreTokens(  ) )
        {

            String id = st.nextToken(  );

            if( nameprovider != null )
            {
                this.addChannel( nameprovider.getChannel( id ) );

            }

            else
            {
                this.addChannel( new Channel( id, id, null ) );

            }
        }
    }

    /**
     * Write out a pipe-separated string of channel IDs. This is primarily for
     * persisting into a preferences string
     *
     * @return Description of the Return Value
     */
    public String toString(  )
    {

        return toString( channels );

    }

    /**
     * Convert a Vector of channels to a string of channel IDs
     *
     * @param channels a vector of channels
     *
     * @return a string of channel IDs
     */
    public static String toString( Vector channels )
    {

        StringBuffer sb = new StringBuffer(  );

        Iterator it = channels.iterator(  );

        while( it.hasNext(  ) )
        {
            sb.append( ( (Channel)( it.next(  ) ) ).getID(  ) );

            sb.append( "|" );

        }

        return sb.toString(  );

    }

    /**
     * Description of the Method
     */
    public void clearChannels(  )
    {
        this.channels = new Vector(  );

    }
}
