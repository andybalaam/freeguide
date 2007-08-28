package freeguide.plugins.program.freeguide.migration;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.util.Map;
import java.util.prefs.BackingStoreException;

/**
 * Migration from 0.10.1 to 0.10.2
 *
 * @author Alex Bulouichik (alex73 at zaval.org)
 */
public class Migrate0_10_1To0_10_2 extends MigrationProcessBase
{
/**
     * Creates a new Migrate0_10_1To0_10_2 object.
     *
     * @param source DOCUMENT ME!
     */
    public Migrate0_10_1To0_10_2( final Map source )
    {
        super( source );
    }

/**
     * Creates a new Migrate0_10_1To0_10_2 object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public Migrate0_10_1To0_10_2( final String nodeName )
        throws BackingStoreException
    {
        super( nodeName );
    }

    /**
     * DOCUMENT_ME!
     */
    public void migrate(  )
    {
        FreeGuide.log.info( "Upgrading preferences 0.10.1 -> 0.10.2" );

        moveKey( "workingDirectory" );
        moveKey( "browserCommand" );
        moveKey( "browserName" );
        moveKey( "countryID" );
        moveKey( "privacyInfo" );

        moveNode( "mainController/selection/", "modules/reminder-alarm/" );
        moveKey( 
            "mainController/reminderOn", "modules/reminder-alarm/reminderOn" );
        moveKey( 
            "mainController/reminderGiveUp",
            "modules/reminder-alarm/reminderGiveUp" );
        moveKey( 
            "mainController/reminderWarning",
            "modules/reminder-alarm/reminderWarning" );
        moveNode( "mainController/" );
        moveKey( 
            "modules/viewer/Horizontal/colorTicked",
            "modules/reminder-alarm/colorTicked" );

        moveNode( "modules/grabber/cosmostv/", "modules/grabber-cosmostv/" );
        moveNode( "modules/grabber/ntvplus/", "modules/grabber-ntvplus/" );
        moveNode( "modules/grabber/vsetv/", "modules/grabber-vsetv/" );
        moveNode( "modules/grabber/xmltv/", "modules/grabber-xmltv/" );
        moveNode( "modules/importexport/palm-atv/", "modules/impexp-palmatv/" );
        moveNode( "modules/viewer/Horizontal/", "modules/ui-horizontal/" );

        patchGrabbersList(  );

        getAndRemoveKey( "version" );
        putKey( "version", "0.10.2" );
    }

    protected void patchGrabbersList(  )
    {
        String size =
            getAndRemoveKey( "mainController/activeGrabberIDs.size" );

        if( size != null )
        {
            int sizeI = Integer.parseInt( size );

            for( int i = 0; i < sizeI; i++ )
            {
                String gr =
                    getAndRemoveKey( "mainController/activeGrabberIDs." + i );
                putKey( 
                    "mainController/activeGrabberIDs." + i, "grabber-" + gr );
            }
        }
    }
}
