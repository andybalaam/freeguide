package freeguide.common.lib.fgspecific.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Class for storage channels list.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class TVChannelsSet
{
    /** Info for loading data by PreferencesHelper. */
    public static final Class channels_TYPE = Channel.class;

    /** Channels set name. */
    public String name;

    /** Channels list. */
    public List<Channel> channels = new ArrayList<Channel>(  );

    /**
     * Check if channels list empty.
     *
     * @return true if channels list empty
     */
    public boolean isEmpty(  )
    {
        return channels.isEmpty(  );
    }

    /**
     * Get position of siteID/channelID in list.
     *
     * @param channelID channel ID
     *
     * @return position, or -1 if not found
     */
    public int getChannelIndex( final String channelID )
    {
        for( int i = 0; i < channels.size(  ); i++ )
        {
            Channel ch = channels.get( i );

            if( channelID.equals( ch.channelID ) )
            {
                return i;

            }
        }

        return -1;

    }

    /**
     * Get channels set name.
     *
     * @return Returns the name.
     */
    public String getName(  )
    {
        return name;

    }

    /**
     * Set channels set name.
     *
     * @param name
     */
    public void setName( final String name )
    {
        this.name = name;

    }

    /**
     * Get channels list.
     *
     * @return channels list
     */
    public Collection getChannels(  )
    {
        return channels;

    }

    /**
     * Add channel.
     *
     * @param ch DOCUMENT ME!
     */
    public void add( Channel ch )
    {
        channels.add( ch );

    }

    /**
     * Check for contains channel.
     *
     * @param channelID
     *
     * @return true if channel contained
     */
    public boolean contains( final String channelID )
    {
        return channels.contains( new Channel( channelID ) );
    }

    /**
     * Check for contains channel.
     *
     * @param channelID
     */
    public void remove( final String channelID )
    {
        channels.remove( new Channel( channelID ) );
    }

    /**
     * Get name for UI elements.
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {
        return name;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param obj DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean equals( Object obj )
    {
        if( obj == null )
        {
            return false;

        }

        TVChannelsSet cs = (TVChannelsSet)obj;

        if( channels.size(  ) != cs.channels.size(  ) )
        {
            return false;

        }

        for( int i = 0; i < channels.size(  ); i++ )
        {
            Channel ch1 = (Channel)channels.get( i );

            Channel ch2 = (Channel)cs.channels.get( i );

            if( !ch1.equals( ch2 ) )
            {
                return false;

            }
        }

        return true;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public TVChannelsSet clone(  )
    {
        final TVChannelsSet result = new TVChannelsSet(  );

        result.name = name;

        result.channels = new ArrayList<Channel>( channels.size(  ) );

        for( int i = 0; i < channels.size(  ); i++ )
        {
            Channel ch = (Channel)channels.get( i );

            result.channels.add( ch.clone(  ) );

        }

        return result;

    }

    /**
     * Storage for siteID/channelID.
     *
     * @author Alex Buloichik (mailto: alex73 at zaval.org)
     */
    public static class Channel
    {
        /** DOCUMENT ME! */
        public String channelID;

        /** DOCUMENT ME! */
        public String displayName;

/**
         * Creates a new Channel object.
         */
        public Channel(  )
        {
        }

/**
         * Creates a new Channel object.
         *
         * @param channelID channe id
         */
        public Channel( final String channelID )
        {
            this.channelID = channelID;
        }

/**
         * Creates a new Channel object.
         *
         * @param channelID DOCUMENT ME!
         * @param displayName DOCUMENT ME!
         */
        public Channel( final String channelID, final String displayName )
        {
            this.channelID = channelID;

            this.displayName = displayName;

        }

/**
         * Creates a new Channel object.
         *
         * @param channel DOCUMENT ME!
         */
        public Channel( final TVChannel channel )
        {
            this.channelID = channel.getID(  );

            this.displayName = channel.getDisplayName(  );

        }

        /**
         * DOCUMENT ME!
         *
         * @return Returns the id.
         */
        public String getChannelID(  )
        {
            return channelID;

        }

        /**
         * DOCUMENT ME!
         *
         * @return Returns the displayName.
         */
        public String getDisplayName(  )
        {
            return displayName;

        }

        /**
         * DOCUMENT_ME!
         *
         * @param obj DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean equals( Object obj )
        {
            if( obj instanceof String )
            {
                return channelID.equals( obj );
            }
            else
            {
                Channel o = (Channel)obj;

                return channelID.equals( o.channelID );
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String toString(  )
        {
            return displayName + ' ' + '(' + channelID + ')';

        }

        protected Channel clone(  )
        {
            return new Channel( channelID, displayName );

        }
    }
}
