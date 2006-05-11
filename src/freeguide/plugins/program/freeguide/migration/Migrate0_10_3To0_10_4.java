package freeguide.plugins.program.freeguide.migration;

import freeguide.common.lib.general.StringHelper;

import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * DOCUMENT ME!
 *
 * @author Andy Balaam
 * @version 1
 */
public class Migrate0_10_3To0_10_4 extends MigrationProcessBase
{
/**
     *
     *
     * @param source DOCUMENT ME!
     */
    public Migrate0_10_3To0_10_4( final Map source )
    {
        super( source );
    }

/**
     * 
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public Migrate0_10_3To0_10_4( final String nodeName )
        throws BackingStoreException
    {
        super( nodeName );
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void migrate(  ) throws Exception
    {
        // TODO: delete .ser files from working dir
        
        moveNode( "modules/impexp-html/", "modules/importexport-html/" );
        moveNode( "modules/impexp-jtv/", "modules/importexport-jtv/" );
        moveNode( "modules/impexp-palmatv/", "modules/importexport-palmatv/" );
        moveNode( "modules/impexp-xmltv/", "modules/importexport-xmltv/" );

        moveNode( "" );

        getAndRemoveKey( "version" );
        putKey( "version", "0.10.4" );
    }
}
