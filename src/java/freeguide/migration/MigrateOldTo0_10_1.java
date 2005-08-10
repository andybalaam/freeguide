package freeguide.migration;

import freeguide.FreeGuide;

import freeguide.lib.general.LanguageHelper;

import java.awt.Color;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.BackingStoreException;

/**
 * Migration from old versions to 0.10.1
 *
 * @author Alex Bulouichik (alex73 at zaval.org)
 */
public class MigrateOldTo0_10_1 extends MigrationProcessBase
{

    protected static String xmltvConfig;

    /**
     * Creates a new MigrateOldTo0_10_1 object.
     *
     * @param source DOCUMENT ME!
     */
    public MigrateOldTo0_10_1( final Map source )
    {
        super( source );
    }

    /**
     * Creates a new MigrateOldTo0_10_1 object.
     *
     * @param nodeName DOCUMENT ME!
     *
     * @throws BackingStoreException DOCUMENT ME!
     */
    public MigrateOldTo0_10_1( final String nodeName )
        throws BackingStoreException
    {
        super( nodeName );
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void migrate(  ) throws IOException
    {
        moveKey( "misc/working_directory", "workingDirectory" );
        moveKey( "misc/browser", "browserName" );
        moveKey( "commandline/browser_command.1", "browserCommand" );

        String region = getAndRemoveKey( "misc/region" );

        if( region != null )
        {

            Map conv = new TreeMap(  );
            LanguageHelper.loadProperties( 
                "freeguide/migration/regions.0.8.6.properties", conv );
            putKey( "countryID", (String)conv.get( region ) );
        }

        moveKey( 
            "misc/day_start_time", "modules/viewer/Horizontal/day_start_time" );
        moveKey( "misc/privacy", "privacyInfo" );
        moveKey( "misc/reminders_on", "mainController/reminderOn" );
        putKey( 
            "mainController/reminderGiveUp",
            getAndRemoveKey( "misc/reminders_give_up_secs" ) + "000" );
        putKey( 
            "mainController/reminderWarning",
            getAndRemoveKey( "misc/reminders_warning_secs" ) + "000" );

        int i;

        for( i = 0;; i++ )
        { // convert channels sets

            String name =
                getAndRemoveKey( "channelsets/" + ( i + 1 ) + ".name" );
            String channels =
                getAndRemoveKey( "channelsets/" + ( i + 1 ) + ".channelids" );

            if( ( name == null ) || ( channels == null ) )
            {

                break;
            }

            putKey( "mainController/channelsSetsList." + i + ".name", name );

            String[] ch = channels.split( "\\|" );
            putKey( 
                "mainController/channelsSetsList." + i + ".channels.size",
                "" + ch.length );

            for( int j = 0; i < ch.length; j++ )
            {
                putKey( 
                    "mainController/channelsSetsList." + i + ".channels." + j
                    + ".channelID", "xmltv/" + ch[j] );
                putKey( 
                    "mainController/channelsSetsList." + i + ".channels." + j
                    + ".displayName", ch[j] );
            }
        }

        putKey( "mainController/channelsSetsList.size", "" + i );

        for( i = 0;; i++ )
        { // convert favourites

            String name =
                getAndRemoveKey( "favourites/" + ( i - 1 ) + ".name" );

            if( name == null )
            {

                break;
            }

            moveKey( 
                "favourites/" + ( i - 1 ) + ".name",
                "mainController/selection/favouritesList." + i + ".name" );
            moveKey( 
                "favourites/" + ( i - 1 ) + ".title_contains",
                "mainController/selection/favouritesList." + i
                + ".titleContains" );
            moveKey( 
                "favourites/" + ( i - 1 ) + ".title_string",
                "mainController/selection/favouritesList." + i
                + ".titleString" );
            moveKey( 
                "favourites/" + ( i - 1 ) + ".title_regex",
                "mainController/selection/favouritesList." + i + ".titleRegex" );

            String channelId =
                getAndRemoveKey( "favourites/" + ( i - 1 ) + ".channel_id" );

            if( channelId != null )
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".channelID", "xmltv/" + channelId );
            }

            String dayOfWeek =
                getAndRemoveKey( "favourites/" + ( i - 1 ) + ".day_of_week" );

            if( dayOfWeek != null )
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".dayOfWeek", dayOfWeek );
            }
            else
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".dayOfWeek", "-1" );
            }

            String afterTime =
                getAndRemoveKey( "favourites/" + ( i - 1 ) + ".after_time" );

            if( afterTime != null )
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".afterTime.milliseconds", afterTime );
            }
            else
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".afterTime.milliseconds", "-1" );
            }

            String beforeTime =
                getAndRemoveKey( "favourites/" + ( i - 1 ) + ".before_time" );

            if( beforeTime != null )
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".beforeTime.milliseconds", beforeTime );
            }
            else
            {
                putKey( 
                    "mainController/selection/favouritesList." + i
                    + ".beforeTime.milliseconds", "-1" );
            }
        }

        putKey( "mainController/selection/favouritesList.size", "" + i );

        moveKey( 
            "screen/align_text_to_left",
            "modules/viewer/Horizontal/displayAlignToLeft" );
        moveKey( 
            "screen/display_24hour_time",
            "modules/viewer/Horizontal/display24time" );
        moveKey( 
            "screen/display_programme_time",
            "modules/viewer/Horizontal/displayTime" );
        moveKey( 
            "screen/display_time_delta",
            "modules/viewer/Horizontal/displayDelta" );
        moveKey( 
            "screen/display_tooltips",
            "modules/viewer/Horizontal/displayTooltips" );
        moveKey( 
            "screen/channel_height",
            "modules/viewer/Horizontal/sizeChannelHeight" );
        moveKey( 
            "screen/panel_width",
            "modules/viewer/Horizontal/sizeChannelPanelWidth" );

        copyColor( 
            "modules/viewer/Horizontal/colorHeart",
            "screen/programme_heart_colour" );
        copyColor( 
            "modules/viewer/Horizontal/colorMovie",
            "screen/programme_movie_colour" );
        copyColor( 
            "modules/viewer/Horizontal/colorNonTicked",
            "screen/programme_normal_colour" );
        copyColor( 
            "modules/viewer/Horizontal/colorChannel", "screen/channel_colour" );
        copyColor( "modules/viewer/Horizontal/colorTicked", "screen/" );

        moveKey( 
            "screen/viewer_splitpane_horizontal",
            "modules/viewer/Horizontal/positionSplitPaneHorizontalTop" );
        moveKey( 
            "screen/viewer_splitpane_horizontal_bottom",
            "modules/viewer/Horizontal/positionSplitPaneHorizontalBottom" );
        moveKey( 
            "screen/viewer_splitpane_vertical",
            "modules/viewer/Horizontal/positionSplitPaneVertical" );

        moveKey( "screen/font_name", "modules/viewer/Horizontal/fontName" );
        moveKey( "screen/font_size", "modules/viewer/Horizontal/fontSize" );
        moveKey( "screen/font_style", "modules/viewer/Horizontal/fontStyle" );

        moveKey( "screen/look_and_feel", "mainController/ui.LFname" );
        moveKey( 
            "screen/viewer_left", "mainController/ui.mainWindowPosition.x" );
        moveKey( 
            "screen/viewer_top", "mainController/ui.mainWindowPosition.y" );
        moveKey( 
            "screen/viewer_width", "mainController/ui.mainWindowPosition.width" );
        moveKey( 
            "screen/viewer_height",
            "mainController/ui.mainWindowPosition.height" );
        moveKey( 
            "screen/viewer_channel_set",
            "modules/viewer/Horizontal/currentChannelSetName" );

        xmltvConfig = getAndRemoveKey( "misc/grabber_config" );

        if( xmltvConfig != null )
        {
            xmltvConfig =
                xmltvConfig.replaceAll( 
                    "%home%", System.getProperty( "user.home" ) );
        }

        // remove unused keys
        getAndRemoveKey( "misc/install_directory" );
        getAndRemoveKey( "misc/install_version" );
        getAndRemoveKey( "misc/xmltv_directory" );
        getAndRemoveKey( "misc/doc_directory" );

        for( i = 1; i < 9; i++ )
        {
            getAndRemoveKey( "commandline/browser_command." + i + ".1" );
            getAndRemoveKey( "misc/browser." + i );
        }

        for( i = 1; i < 5; i++ )
        {
            getAndRemoveKey( "commandline/tv_config." + i );
            getAndRemoveKey( "commandline/tv_grab." + i );
        }

        getAndRemoveKey( "misc/days_to_grab" );
        getAndRemoveKey( "misc/re_download" );
        getAndRemoveKey( "screen/programme_chosen_colour.r" );
        getAndRemoveKey( "screen/programme_chosen_colour.g" );
        getAndRemoveKey( "screen/programme_chosen_colour.b" );

        getAndRemoveKey( "misc/grabber_start_today" );
        getAndRemoveKey( "misc/grabber_today_offset" );
        getAndRemoveKey( "screen/executor_modal" );
        getAndRemoveKey( "misc/grabber_start_time" );

        putKey( "version", "0.10.1" );
    }

    protected void copyColor( final String newKey, final String oldPrefix )
    {

        final String color = readColor( oldPrefix );

        if( color != null )
        {
            putKey( newKey, color );
        }
    }

    protected String readColor( String prefix )
    {

        String r = getAndRemoveKey( prefix + ".r" );

        String g = getAndRemoveKey( prefix + ".g" );

        String b = getAndRemoveKey( prefix + ".b" );

        if( ( r != null ) && ( ( g != null ) & ( b != null ) ) )
        {

            return Integer.toString( 
                new Color( 
                    Integer.parseInt( r ), Integer.parseInt( g ),
                    Integer.parseInt( b ) ).getRGB(  ) );
        }
        else
        {

            return null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void migrateAfterWizard(  ) throws Exception
    {

        if( xmltvConfig != null )
        {
            loadConfigFile( new File( xmltvConfig ) );

            xmltvConfig = null;
        }
    }

    protected static void loadConfigFile( File f ) throws IOException
    {

        String fn = FreeGuide.config.workingDirectory + "/xmltv-configs/";
        new File( fn ).mkdirs(  );

        fn += f.getName(  ).substring( "tv_grab_".length(  ) );

        File outFile = new File( fn );

        BufferedInputStream in =
            new BufferedInputStream( new FileInputStream( f ) );

        BufferedOutputStream out =
            new BufferedOutputStream( new FileOutputStream( outFile ) );

        byte[] buf = new byte[16384];

        while( true )
        {

            int len = in.read( buf );

            if( len < 0 )
            {

                break;

            }

            out.write( buf, 0, len );

        }

        out.flush(  );

        out.close(  );

        in.close(  );

    }
}
