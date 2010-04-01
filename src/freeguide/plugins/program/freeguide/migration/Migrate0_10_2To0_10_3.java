package freeguide.plugins.program.freeguide.migration;

import freeguide.common.lib.general.StringHelper;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.util.prefs.BackingStoreException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class Migrate0_10_2To0_10_3 extends MigrationProcessBase
{
    /**
     * Creates a new Migrate0_10_1To0_10_2 object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public Migrate0_10_2To0_10_3( final String nodeName )
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
        FreeGuide.log.info( "Upgrading preferences 0.10.2 -> 0.10.3" );

        String browserCommand = getAndRemoveKey( "browserCommand" );
        browserCommand = StringHelper.replaceAll(
                browserCommand, "file://%filename%", "%url%" );
        browserCommand = StringHelper.replaceAll(
                browserCommand, "%filename%", "%url%" );
        putKey( "browserCommand", browserCommand );

        getAndRemoveKey( "version" );

        moveNode( "" );

        putKey( "version", "0.10.3" );
    }
}
