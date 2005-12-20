package freeguide.lib.fgspecific.data;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for storage all channels.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class TVData implements Serializable
{

    private final static long serialVersionUID = 10;
    protected Map channels = new TreeMap(  );
    protected long timeEarliest;
    protected long timeLatest;
    protected boolean timeCalculated = false;

    /**
     * Get channel by ID. If channel not exists, it will be created.
     *
     * @param channelID channel ID
     *
     * @return TVChannel
     */
    public TVChannel get( final String channelID )
    {

        synchronized( this )
        {

            TVChannel result = (TVChannel)channels.get( channelID );

            if( result == null )
            {
                result = new TVChannel( channelID );

                channels.put( channelID, result );

            }

            return result;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channelID DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean containsChannel( final String channelID )
    {

        synchronized( this )
        {

            return channels.containsKey( channelID );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param other DOCUMENT_ME!
     */
    public synchronized void moveFrom( final TVData other )
    {
        timeCalculated = false;

        Iterator it = other.getChannelsIterator(  );

        while( it.hasNext(  ) )
        {

            TVChannel ch = (TVChannel)it.next(  );

            TVChannel myCh = get( ch.getID(  ) );
            myCh.moveFrom( ch );
        }
    }

    /**
     * Calculate programmes count in all channels.
     *
     * @return DOCUMENT_ME!
     */
    public synchronized int getProgrammesCount(  )
    {

        int result = 0;
        Iterator it = getChannelsIterator(  );

        while( it.hasNext(  ) )
        {

            TVChannel ch = (TVChannel)it.next(  );
            result += ch.getProgrammesCount(  );
        }

        return result;
    }

    /**
     * Normalize time for all channels.
     */
    public synchronized void normalizeTime(  )
    {
        timeCalculated = false;

        Iterator it = getChannelsIterator(  );

        while( it.hasNext(  ) )
        {

            TVChannel ch = (TVChannel)it.next(  );
            ch.normalizeTime(  );
        }
    }

    /**
     * Clear programmes from all channels.
     */
    public synchronized void clearProgrammes(  )
    {
        timeCalculated = false;

        Iterator it = getChannelsIterator(  );

        while( it.hasNext(  ) )
        {

            TVChannel ch = (TVChannel)it.next(  );
            ch.clearProgrammes(  );
        }
    }

    /**
     * Get channels count.
     *
     * @return channels count
     */
    public int getChannelsCount(  )
    {

        return channels.size(  );

    }

    /**
     * Check for not empty.
     *
     * @return true if any data exists
     */
    public boolean hasData(  )
    {

        return channels.size(  ) > 0;

    }

    /**
     * Get channels ID list.
     *
     * @return
     */
    public String[] getChannelIDs(  )
    {

        synchronized( this )
        {

            return (String[])channels.keySet(  ).toArray( 
                new String[channels.keySet(  ).size(  )] );

        }
    }

    /**
     * Get iterator by channels.
     *
     * @return iterator
     */
    public Iterator getChannelsIterator(  )
    {

        return channels.values(  ).iterator(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param iterator DOCUMENT_ME!
     */
    public void iterate( final TVIterator iterator )
    {

        if( iterator instanceof TVIteratorChannels )
        {
            iterateChannels( (TVIteratorChannels)iterator );
        }
        else
        {
            iterateProgrammes( (TVIteratorProgrammes)iterator );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param iterator DOCUMENT_ME!
     */
    public void iterateChannels( final TVIteratorChannels iterator )
    {

        synchronized( this )
        {
            iterator.it = channels.values(  ).iterator(  );

            while( iterator.it.hasNext(  ) )
            {

                TVChannel ch = (TVChannel)iterator.it.next(  );

                iterator.onChannel( ch );
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param iterator DOCUMENT_ME!
     */
    public void iterateProgrammes( final TVIteratorProgrammes iterator )
    {

        synchronized( this )
        {
            iterator.itChannels = channels.values(  ).iterator(  );

            while( iterator.itChannels.hasNext(  ) )
            {

                TVChannel ch = (TVChannel)iterator.itChannels.next(  );

                iterator.needToIterateChannel = true;
                iterator.onChannel( ch );
                iterator.currentChannel = ch;

                if( iterator.needToIterateChannel )
                {
                    iterator.itProgrammes = ch.programmes.iterator(  );

                    while( iterator.itProgrammes.hasNext(  ) )
                    {

                        TVProgramme pr =
                            (TVProgramme)iterator.itProgrammes.next(  );

                        if( !iterator.needToIterateChannel )
                        {

                            break;
                        }

                        iterator.onProgramme( pr );
                    }
                }

                iterator.onChannelFinish(  );
            }
        }
    }
}
