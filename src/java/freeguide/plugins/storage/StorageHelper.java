package freeguide.plugins.storage;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.IStorage;

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
        final IStorage.EarliestCheckAllow checker )
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
        final IStorage.Info info, final TVProgramme programme )
    {

        if( !info.allChannels.contains( programme.getChannel(  ).getID(  ) ) )
        {
            info.allChannels.add( 
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
     * DOCUMENT_ME!
     *
     * @param info DOCUMENT_ME!
     * @param data DOCUMENT_ME!
     */
    public static void performInInfo( 
        final IStorage.Info info, final TVData data )
    {
        data.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {

                    if( !info.allChannels.contains( channel.getID(  ) ) )
                    {
                        info.allChannels.add( 
                            new TVChannelsSet.Channel( 
                                channel.getID(  ), channel.getDisplayName(  ) ) );
                    }
                }

                protected void onProgramme( TVProgramme programme )
                {

                    if( info.minDate > programme.getStart(  ) )
                    {
                        info.minDate = programme.getStart(  );
                    }

                    if( info.maxDate < programme.getEnd(  ) )
                    {
                        info.maxDate = programme.getEnd(  );
                    }
                }
            } );
    }

    protected static class EarliestIteratorProgrammes
        extends TVIteratorProgrammes
    {

        protected TVProgramme prog;
        final protected IStorage.EarliestCheckAllow checker;
        final protected long minDate;

        /**
         * Creates a new EarliestIteratorProgrammes object.
         *
         * @param minDate DOCUMENT ME!
         * @param checker DOCUMENT ME!
         */
        public EarliestIteratorProgrammes( 
            final long minDate, final IStorage.EarliestCheckAllow checker )
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
