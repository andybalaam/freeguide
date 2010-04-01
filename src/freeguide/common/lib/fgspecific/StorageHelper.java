package freeguide.common.lib.fgspecific;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVIteratorChannels;
import freeguide.common.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleStorage;

import java.util.Iterator;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class StorageHelper
{
    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     * @param minDate DOCUMENT_ME!
     * @param checker DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static TVProgramme findEarliest(
        final TVData data, final long minDate,
        final IModuleStorage.EarliestCheckAllow checker )
    {
        EarliestIteratorProgrammes iterator =
            new EarliestIteratorProgrammes( minDate, checker );
        data.iterate( iterator );

        return iterator.prog;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param info DOCUMENT_ME!
     * @param programme DOCUMENT_ME!
     */
    public static void performInInfo(
        final IModuleStorage.Info info, final TVProgramme programme )
    {
        if( !info.channelsList.contains( programme.getChannel(  ).getID(  ) ) )
        {
            info.channelsList.add(
                new TVChannelsSet.Channel(
                    programme.getChannel(  ).getID(  ),
                    programme.getChannel(  ).getDisplayName(  ) ) );
        }

        if( info.minDate > programme.getStart(  ) )
        {
            info.minDate = programme.getStart(  );
        }

        if( info.maxDate < programme.getEnd(  ) )
        {
            info.maxDate = programme.getEnd(  );
        }
    }

    /**
     * Perform data for info about all channels.
     *
     * @param info DOCUMENT_ME!
     * @param data DOCUMENT_ME!
     */
    public static void performInInfo(
        final IModuleStorage.Info info, final TVData data )
    {
        data.iterate(
            new TVIteratorChannels(  )
            {
                protected void onChannel( TVChannel channel )
                {
                    performInInfo( info, channel );
                }
            } );
    }

    /**
     * Perform channel info for info about all channels.
     *
     * @param info
     * @param channel
     */
    public static void performInInfo(
        final IModuleStorage.Info info, final TVChannel channel )
    {
        if( !info.channelsList.contains( channel.getID(  ) ) )
        {
            info.channelsList.add(
                new TVChannelsSet.Channel(
                    channel.getID(  ), channel.getDisplayName(  ) ) );
        }

        for(
            final Iterator it = channel.getProgrammes(  ).iterator(  );
                it.hasNext(  ); )
        {
            final TVProgramme programme = (TVProgramme)it.next(  );

            if( info.minDate > programme.getStart(  ) )
            {
                info.minDate = programme.getStart(  );
            }

            if( info.maxDate < programme.getEnd(  ) )
            {
                info.maxDate = programme.getEnd(  );
            }
        }
    }

    protected static class EarliestIteratorProgrammes
        extends TVIteratorProgrammes
    {
        protected TVProgramme prog;
        final protected IModuleStorage.EarliestCheckAllow checker;
        final protected long minDate;

        /**
         * Creates a new EarliestIteratorProgrammes object.
         *
         * @param minDate DOCUMENT ME!
         * @param checker DOCUMENT ME!
         */
        public EarliestIteratorProgrammes(
            final long minDate, final IModuleStorage.EarliestCheckAllow checker )
        {
            this.checker = checker;

            this.minDate = minDate;

        }

        protected void onChannel( TVChannel channel )
        {
        }

        protected void onProgramme( TVProgramme programme )
        {
            if( programme.getStart(  ) < minDate )
            {
                return;

            }

            if( prog != null )
            {
                if( programme.getStart(  ) < prog.getStart(  ) )
                {
                    if( checker.isAllow( programme ) )
                    {
                        prog = programme;

                        stopIterateChanel(  );

                    }
                }

                else
                {
                    stopIterateChanel(  );

                }
            }

            else
            {
                if( checker.isAllow( programme ) )
                {
                    prog = programme;

                    stopIterateChanel(  );

                }
            }
        }
    }
}
