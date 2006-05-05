package freeguide.plugins.program.freeguide.lib.fgspecific;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IStoragePipe;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class StoragePipe implements IStoragePipe
{
    protected static final int MAX_CACHE_SIZE = 5000;
    protected final IModuleStorage storage;
    protected final TVData cache;
    protected final TVData normalizedCache;
    protected int cacheCount;
    protected int normalizedCacheCount;

/**
     * Creates a new StoragePipe object.
     */
    public StoragePipe(  )
    {
        storage = Application.getInstance(  ).getDataStorage(  );
        cache = new TVData(  );
        normalizedCache = new TVData(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channel DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void addChannel( final TVChannel channel ) throws Exception
    {
        cacheCount += channel.getProgrammesCount(  );

        final TVChannel cachedChannel = cache.get( channel.getID(  ) );
        cachedChannel.moveFrom( channel );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channelID DOCUMENT_ME!
     * @param programme DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void addProgramme( String channelID, TVProgramme programme )
        throws Exception
    {
        final TVChannel cachedChannel = cache.get( channelID );
        cachedChannel.put( programme );
        cacheCount++;
        checkForMaxCache(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param channelID DOCUMENT_ME!
     * @param programmes DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void addProgrammes( String channelID, TVProgramme[] programmes )
        throws Exception
    {
        final TVChannel cachedChannel = cache.get( channelID );
        cachedChannel.put( programmes );
        cacheCount += programmes.length;
        checkForMaxCache(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void addData( TVData data ) throws Exception
    {
        cacheCount += data.getProgrammesCount(  );
        cache.moveFrom( data );
    }

    /**
     * Normalize programmes time.
     *
     * @throws Exception DOCUMENT ME!
     */
    public void finishBlock(  ) throws Exception
    {
        normalizeCache(  );
    }

    protected void checkForMaxCache(  ) throws Exception
    {
        if( ( cacheCount + normalizedCacheCount ) > MAX_CACHE_SIZE )
        {
            if( normalizedCacheCount == 0 )
            {
                Application.getInstance(  ).getLogger(  )
                           .warning( "Cache was forced normalized" );
                normalizeCache(  );
            }

            flushNormalized(  );
        }
    }

    protected void normalizeCache(  )
    {
        cache.normalizeTime(  );
        normalizedCache.moveFrom( cache );
        normalizedCacheCount += cacheCount;
        cacheCount = 0;
    }

    protected void flushNormalized(  ) throws Exception
    {
        storage.store( normalizedCache );
        normalizedCache.clearProgrammes(  );
        normalizedCacheCount = 0;
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void finish(  ) throws Exception
    {
        if( cacheCount > 0 )
        {
            Application.getInstance(  ).getLogger(  )
                       .warning( "Cache was forced normalized" );
            normalizeCache(  );
        }

        flushNormalized(  );
    }
}
