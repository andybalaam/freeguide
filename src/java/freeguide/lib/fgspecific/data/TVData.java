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
    public void mergeFrom( final TVData other )
    {

        synchronized( this )
        {
            timeCalculated = false;

            Iterator it = other.getChannelsIterator(  );

            while( it.hasNext(  ) )
            {

                TVChannel ch = (TVChannel)it.next(  );

                TVChannel myCh = get( ch.getID(  ) );

                myCh.loadHeadersFrom( ch );

                myCh.mergeFrom( ch );

            }
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

        synchronized( this )
        {

            Iterator itCh = channels.values(  ).iterator(  );

            while( itCh.hasNext(  ) )
            {

                TVChannel ch = (TVChannel)itCh.next(  );

                if( iterator instanceof TVIteratorChannels )
                {

                    TVIteratorChannels iteratorChannels =
                        (TVIteratorChannels)iterator;
                    iteratorChannels.onChannel( ch );
                }
                else if( iterator instanceof TVIteratorProgrammes )
                {

                    TVIteratorProgrammes iteratorProgrammes =
                        (TVIteratorProgrammes)iterator;
                    iteratorProgrammes.needToIterateChannel = true;
                    iteratorProgrammes.onChannel( ch );
                    iteratorProgrammes.currentChannel = ch;

                    if( iteratorProgrammes.needToIterateChannel )
                    {

                        Iterator itPr = ch.programmes.iterator(  );

                        while( itPr.hasNext(  ) )
                        {

                            TVProgramme pr = (TVProgramme)itPr.next(  );

                            if( !iteratorProgrammes.needToIterateChannel )
                            {

                                break;
                            }

                            iteratorProgrammes.onProgramme( pr );
                        }
                    }
                }
            }
        }
    }
}
