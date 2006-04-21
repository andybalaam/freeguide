package freeguide.common.plugininterfaces;

import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;

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
     * @param loadInfo filter for loaded data
     *
     * @return data
     *
     * @throws Exception
     */
    TVData get( final Info loadInfo ) throws Exception;

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
     * Cleans up old data (e.g. files that are older than some weeks)
     */
    void cleanup(  );

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public class Info
    {

        /** DOCUMENT ME! */
        public TVChannelsSet channelsList = new TVChannelsSet(  );

        /** DOCUMENT ME! */
        public long minDate = Long.MAX_VALUE;

        /** DOCUMENT ME! */
        public long maxDate = Long.MIN_VALUE;

        /**
         * Clone info object before changing.
         *
         * @return new Info object.
         */
        public Info cloneInfo(  )
        {

            Info result = new Info(  );

            result.channelsList = (TVChannelsSet)channelsList.clone(  );
            result.minDate = minDate;
            result.maxDate = maxDate;

            return result;

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
