package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVProgramme;

/**
 * Interface to TVData storage implementation.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleStorage extends IModule
{

    /**
     * Get info about data in storage.
     *
     * @return info
     */
    Info getInfo(  );

    /**
     * Load data from external storage to in-memory storage.
     *
     * @param channels filter for loaded data
     * @param minDate DOCUMENT ME!
     * @param maxDate DOCUMENT ME!
     *
     * @return data
     *
     * @throws Exception
     */
    TVData get( TVChannelsSet channels, long minDate, long maxDate )
        throws Exception;

    /**
     * Find earliest programme. Uses for remonder.
     *
     * @param minDate
     * @param check
     *
     * @return
     *
     * @throws Exception
     */
    TVProgramme findEarliest( long minDate, final EarliestCheckAllow check )
        throws Exception;

    /**
     * Add data to storage. Implementation can change data object.
     *
     * @param data
     *
     * @throws Exception
     */
    void store( TVData data ) throws Exception;

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public class Info
    {

        /** DOCUMENT ME! */
        public TVChannelsSet allChannels = new TVChannelsSet(  );

        /** DOCUMENT ME! */
        public long minDate = Long.MAX_VALUE;

        /** DOCUMENT ME! */
        public long maxDate = Long.MIN_VALUE;

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Object clone(  )
        {

            Info result = new Info(  );

            //result.allChannels = (TVChannelsSet)allChannels.clone();
            result.minDate = minDate;

            result.maxDate = maxDate;

            return result;

        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public boolean isEmpty(  )
        {

            return allChannels.channels.isEmpty(  );
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    abstract public static class EarliestCheckAllow
    {

        /**
         * DOCUMENT_ME!
         *
         * @param programme DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        abstract public boolean isAllow( final TVProgramme programme );
    }
}
