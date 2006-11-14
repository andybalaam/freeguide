package freeguide.plugins.program.freeguide.migration;

import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class Migrate0_10_4To0_10_5 extends MigrationProcessBase
{
    /**
     * Creates a new Migrate0_10_4To0_10_5 object.
     *
     * @param source DOCUMENT ME!
     */
    public Migrate0_10_4To0_10_5( final Map source )
    {
        super( source );
    }

    /**
     * Creates a new Migrate0_10_4To0_10_5 object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public Migrate0_10_4To0_10_5( final String nodeName )
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
        moveNode( "modules/reminder-alarm/", "modules/reminder-advanced/" );

        final String colorTicked =
            getKey( "modules/ui-horizontal/colorTicked" );
        final String[] favs =
            listKeysTo( 
                Pattern.compile( 
                    "modules\\/reminder-advanced\\/favouritesList\\.(\\d)+\\.name" ) );

        for( final String fav : favs )
        {
            final String base = fav.substring( 0, fav.length(  ) - 4 );
            putKey( base + "selectedColor", colorTicked );
            putKey( base + "reminders.size", "1" );
            putKey( base + "reminders.0", "default" );
        }

        final String[] sels =
            listKeysTo( 
                Pattern.compile( 
                    "modules/reminder-advanced/manualSelectionList\\.(\\d)+\\.selected" ) );

        for( final String sel : sels )
        {
            final String base = sel.substring( 0, sel.length(  ) - 8 );
            final String selected = prefTo.remove( sel );
            putKey( base + "reminders.size", "1" );
            putKey( base + "reminders.0.key", "default" );
            putKey( base + "reminders.0.value", selected );
        }

        putKey( "modules/reminder-advanced/reminders.size", "1" );
        putKey( 
            "modules/reminder-advanced/reminders.0.isPopup",
            prefTo.remove( "modules/reminder-advanced/reminderOn" ) );

        long warn;
        long giveUp;

        try
        {
            warn = Long.parseLong( 
                    prefTo.remove( 
                        "modules/reminder-advanced/reminderWarning" ) );
        }
        catch( Exception ex )
        {
            warn = 600000;
        }

        try
        {
            giveUp = Long.parseLong( 
                    prefTo.remove( "modules/reminder-advanced/reminderGiveUp" ) );
        }
        catch( Exception ex )
        {
            giveUp = 600000;
        }

        putKey( 
            "modules/reminder-advanced/reminders.0.popupOpenTime",
            Long.toString( -warn ) );
        putKey( 
            "modules/reminder-advanced/reminders.0.popupCloseTime",
            Long.toString( -warn + giveUp ) );

        putKey( "modules/reminder-advanced/reminders.size", "1" );
        putKey( "modules/reminder-advanced/reminders.0.name", "default" );
        getAndRemoveKey( "version" );
        putKey( "version", "0.10.5" );
    }
}
