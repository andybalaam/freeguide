package freeguide.plugins.program.freeguide.migration;

import freeguide.common.lib.fgspecific.TVChannelIconHelper;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.StringHelper;
import freeguide.common.lib.general.Time;

import freeguide.plugins.program.freeguide.FreeGuide;

import java.awt.Color;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
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
    protected static Map icons;

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
        FreeGuide.log.info( "Upgrading preferences old version -> 0.10.1" );

        String workDir = getAndRemoveKey( "misc/working_directory" );

        if( workDir != null )
        {
            workDir = StringHelper.replaceAll(
                    workDir, "%home%", System.getProperty( "user.home" ) );
            workDir = StringHelper.replaceAll(
                    workDir, "%misc.install_directory%",
                    FreeGuide.runtimeInfo.installDirectory );
            putKey( "workingDirectory", workDir );

            final File[] dataFiles =
                new File( workDir ).listFiles(
                    new FileFilter(  )
                    {
                        public boolean accept( File pathname )
                        {
                            return !pathname.isDirectory(  )
                            && pathname.getName(  ).endsWith( ".xmltv" );
                        }
                    } );

            if( dataFiles != null )
            {
                for( int i = 0; i < dataFiles.length; i++ )
                {
                    dataFiles[i].delete(  );
                }
            }
        }

        moveKey( "misc/browser", "browserName" );
        moveKey( "commandline/browser_command.1", "browserCommand" );

        String region = getAndRemoveKey( "misc/region" );

        if( region != null )
        {
            Properties conv = new Properties(  );
            conv.load(
                MigrateOldTo0_10_1.class.getClassLoader(  )
                                        .getResourceAsStream(
                    "resources/plugins/program/freeguide/migration/regions.0.8.6.properties" ) );
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

            for( int j = 0; j < ch.length; j++ )
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
                getAndRemoveKey( "favourites/" + ( i + 1 ) + ".name" );

            if( name == null )
            {
                break;
            }

            putKey(
                "mainController/selection/favouritesList." + i + ".name", name );
            moveKey(
                "favourites/" + ( i + 1 ) + ".title_contains",
                "mainController/selection/favouritesList." + i
                + ".titleContains" );
            moveKey(
                "favourites/" + ( i + 1 ) + ".title_string",
                "mainController/selection/favouritesList." + i
                + ".titleString" );
            moveKey(
                "favourites/" + ( i + 1 ) + ".title_regex",
                "mainController/selection/favouritesList." + i + ".titleRegex" );

            String channelId =
                getAndRemoveKey( "favourites/" + ( i + 1 ) + ".channel_id" );

            if( channelId != null )
            {
                putKey(
                    "mainController/selection/favouritesList." + i
                    + ".channelID", "xmltv/" + channelId );
            }

            String dayOfWeek =
                getAndRemoveKey( "favourites/" + ( i + 1 ) + ".day_of_week" );

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
                getAndRemoveKey( "favourites/" + ( i + 1 ) + ".after_time" );

            if( afterTime != null )
            {
                final Time t = new Time( afterTime );
                putKey(
                    "mainController/selection/favouritesList." + i
                    + ".afterTime.milliseconds",
                    Long.toString( t.getLongValue(  ) ) );
            }
            else
            {
                putKey(
                    "mainController/selection/favouritesList." + i
                    + ".afterTime.milliseconds", "-1" );
            }

            String beforeTime =
                getAndRemoveKey( "favourites/" + ( i + 1 ) + ".before_time" );

            if( beforeTime != null )
            {
                final Time t = new Time( beforeTime );
                putKey(
                    "mainController/selection/favouritesList." + i
                    + ".beforeTime.milliseconds",
                    Long.toString( t.getLongValue(  ) ) );
            }
            else
            {
                putKey(
                    "mainController/selection/favouritesList." + i
                    + ".beforeTime.milliseconds", "-1" );
            }
        }

        putKey( "mainController/selection/favouritesList.size", "" + i );

        final String[] iconKeys = listKeys( "screen/customIcon." );
        icons = new TreeMap(  );

        for( i = 0; i < iconKeys.length; i++ )
        {
            final String key =
                "xmltv/"
                + iconKeys[i].substring( "screen/customIcon.".length(  ) );
            final String value = getAndRemoveKey( iconKeys[i] );
            icons.put( key, value );
        }

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
            "modules/viewer/Horizontal/sizeProgrammePanelWidth" );

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
            xmltvConfig = StringHelper.replaceAll(
                    xmltvConfig, "%home%", System.getProperty( "user.home" ) );
            xmltvConfig = StringHelper.replaceAll(
                    xmltvConfig, "%misc.install_directory%",
                    FreeGuide.runtimeInfo.installDirectory );
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
        if( icons != null )
        {
            for( Iterator it = icons.keySet(  ).iterator(  ); it.hasNext(  ); )
            {
                final String key = (String)it.next(  );
                final File fromPath = new File( (String)icons.get( key ) );
                final String toPath =
                    TVChannelIconHelper.getIconFileName( new TVChannel( key ) );

                try
                {
                    FileHelper.copy( fromPath, new File( toPath ) );
                    fromPath.delete(  );
                }
                catch( IOException ex )
                {
                }
            }
        }

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
