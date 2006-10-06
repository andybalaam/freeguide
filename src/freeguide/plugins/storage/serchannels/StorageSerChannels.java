package freeguide.plugins.storage.serchannels;

import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.IModuleStorage;

/**
 * Storage for store data in ser files splitted by channels only.
 *
 * @author Alex Buloichik
 */
public class StorageSerChannels extends BaseModule implements IModuleStorage
{
    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     */
    public void store( final TVData data )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void cleanup(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Info getInfo(  )
    {
        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param minDate DOCUMENT_ME!
     * @param check DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVProgramme findEarliest( long minDate, EarliestCheckAllow check )
        throws Exception
    {
        return null;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param loadInfo DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData get( Info loadInfo ) throws Exception
    {
        return null;
    }
}
