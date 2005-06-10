package freeguide.migration;

import freeguide.FreeGuide;

import freeguide.gui.viewer.MainController;

import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.selection.Favourite;
import freeguide.lib.fgspecific.selection.SelectionManager;

import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.PreferencesHelper;
import freeguide.lib.general.Time;
import freeguide.lib.general.Version;

import freeguide.plugins.reminder.alarm.AlarmReminder;

import freeguide.plugins.ui.horizontal.HorizontalViewerConfig;

import java.awt.Color;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

/**
 * Migration support from previous versions.
 *
 * @author Alex Bulouichik (alex73 at zaval.org)
 */
public class Migrate
{

    protected static File[] xmltvConfigs;
    protected static String storedVersionName;
    protected static boolean firstTime = true;
    protected static boolean needToRunWizard = false;

    /**
     * Private constructor against class creation.
     */
    private Migrate(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isFirstTime(  )
    {

        return firstTime;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isNeedToRunWizard(  )
    {

        return needToRunWizard;
    }

    /**
     * Main method to run.
     *
     * @throws Exception
     */
    public static void migrateBeforeWizard(  ) throws Exception
    {
        firstTime =
            !Preferences.userRoot(  ).nodeExists( "/org/freeguide-tv" );

        if( Preferences.userRoot(  ).nodeExists( "/org/freeguide-tv/misc" ) )
        {
            storedVersionName =
                Preferences.userRoot(  ).node( "/org/freeguide-tv/misc" ).get( 
                    "install_version", null );

        }

        if( Preferences.userRoot(  ).nodeExists( "/org/freeguide-tv" ) )
        {
            storedVersionName =
                Preferences.userRoot(  ).node( "/org/freeguide-tv" ).get( 
                    "version", null );

        }

        if( 
            new Version( storedVersionName ).compareTo( 
                    new Version( 0, 10, 0 ) ) < 0 )
        {
            loadFromOld(  );
            needToRunWizard = true;
        }
        else if( 
            new Version( storedVersionName ).compareTo( 
                    new Version( 0, 10, 1 ) ) == 0 )
        {
            loadFrom_0_10_1(  );
        }
        else if( 
            new Version( storedVersionName ).compareTo( FreeGuide.VERSION ) > 0 )
        {
            needToRunWizard = true;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void migrateAfterWizard(  ) throws Exception
    {

        if( storedVersionName != null )
        {

            if( xmltvConfigs != null )
            {

                for( int i = 0; i < xmltvConfigs.length; i++ )
                {
                    loadConfigFile( xmltvConfigs[i] );

                }

                xmltvConfigs = null;
            }

            storedVersionName = null;
        }
    }

    /**
     * Load preferences from less than 0.9 version.
     *
     * @throws Exception
     */
    protected static void loadFrom_0_10_1(  ) throws Exception
    {
        copyPref( 
            "/org/freeguide-tv/modules/viewer/Horizontal/colorTicked",
            "/org/freeguide-tv/modules/reminder-alarm/colorTicked" );

        renamePrefNode( 
            "/org/freeguide-tv/modules/grabber/cosmostv",
            "/org/freeguide-tv/modules/grabber-cosmostv" );
        renamePrefNode( 
            "/org/freeguide-tv/modules/grabber/ntvplus",
            "/org/freeguide-tv/modules/grabber-ntvplus" );
        renamePrefNode( 
            "/org/freeguide-tv/modules/grabber/vsetv",
            "/org/freeguide-tv/modules/grabber-vsetv" );
        renamePrefNode( 
            "/org/freeguide-tv/modules/grabber/xmltv",
            "/org/freeguide-tv/modules/grabber-xmltv" );
        renamePrefNode( 
            "/org/freeguide-tv/modules/importexport/palm-atv",
            "/org/freeguide-tv/modules/impexp-palmatv" );
        renamePrefNode( 
            "/org/freeguide-tv/modules/viewer/Horizontal",
            "/org/freeguide-tv/modules/ui-horizontal" );
        removePrefNode( "/org/freeguide-tv/modules/viewer" );
        removePrefNode( "/org/freeguide-tv/modules/grabber" );
        removePrefNode( "/org/freeguide-tv/modules/importexport" );
    }

    protected static void renamePrefNode( 
        final String fromPath, final String toPath ) throws Exception
    {

        if( Preferences.userRoot(  ).nodeExists( fromPath ) )
        {

            Preferences nodeFrom = Preferences.userRoot(  ).node( fromPath );
            Preferences nodeTo = Preferences.userRoot(  ).node( toPath );
            String[] keys = nodeFrom.keys(  );

            for( int i = 0; i < keys.length; i++ )
            {
                nodeTo.put( keys[i], nodeFrom.get( keys[i], null ) );
            }
        }
    }

    protected static void copyPref( 
        final String fromPath, final String toPath ) throws Exception
    {

        int posFrom = fromPath.lastIndexOf( '/' );
        int posTo = toPath.lastIndexOf( '/' );

        if( ( posFrom < 0 ) || ( posTo < 0 ) )
        {

            return;
        }

        String fromPathNode = fromPath.substring( 0, posFrom );
        String fromPathKey = fromPath.substring( posFrom + 1 );
        String toPathNode = toPath.substring( 0, posTo );
        String toPathKey = toPath.substring( posTo + 1 );

        if( Preferences.userRoot(  ).nodeExists( fromPathNode ) )
        {

            Preferences nodeFrom =
                Preferences.userRoot(  ).node( fromPathNode );
            Preferences nodeTo = Preferences.userRoot(  ).node( toPathNode );
            nodeTo.put( toPathKey, nodeFrom.get( fromPathKey, null ) );
        }
    }

    protected static void removePrefNode( final String path )
        throws Exception
    {

        if( Preferences.userRoot(  ).nodeExists( path ) )
        {
            Preferences.userRoot(  ).node( path ).removeNode(  );
        }
    }

    /**
     * Load preferences from less than 0.9 version.
     *
     * @throws Exception
     */
    protected static void loadFromOld(  ) throws Exception
    {

        final Preferences root =
            Preferences.userRoot(  ).node( "/org/freeguide-tv" );

        final Preferences nodeFavourites;
        final HorizontalViewerConfig hovConfig =
            new HorizontalViewerConfig(  );
        final AlarmReminder.ConfigAlarm remConfig =
            new AlarmReminder.ConfigAlarm(  );

        if( root.nodeExists( "favourites" ) )
        {
            nodeFavourites = root.node( "favourites" );

        }

        else
        {
            nodeFavourites = null;

        }

        final Preferences nodeCommandline;

        if( root.nodeExists( "commandline" ) )
        {
            nodeCommandline = root.node( "commandline" );

        }

        else
        {
            nodeCommandline = null;

        }

        final Preferences nodeChannelsSet;

        if( root.nodeExists( "channelsets" ) )
        {
            nodeChannelsSet = root.node( "channelsets" );

        }

        else
        {
            nodeChannelsSet = null;

        }

        final Preferences nodeMisc;

        if( root.nodeExists( "misc" ) )
        {
            nodeMisc = root.node( "misc" );

        }

        else
        {
            nodeMisc = null;

        }

        final Preferences nodeScreen;

        if( root.nodeExists( "screen" ) )
        {
            nodeScreen = root.node( "screen" );

        }

        else
        {
            nodeScreen = null;

        }

        if( nodeMisc != null )
        {
            FreeGuide.config.browserName =
                nodeMisc.get( "browser", FreeGuide.config.browserName );

            String region = nodeMisc.get( "region", null );

            if( region != null )
            {

                Map conv = new TreeMap(  );
                LanguageHelper.loadProperties( 
                    Migrate.class.getClassLoader(  ).getResourceAsStream( 
                        "freeguide/migration/regions.0.8.6.properties" ), conv );
                FreeGuide.config.countryID = (String)conv.get( region );
            }

            hovConfig.dayStartTime.setTimeHHMMString( 
                nodeMisc.get( 
                    "day_start_time", hovConfig.dayStartTime.getHHMMString(  ) ) );

            FreeGuide.config.privacyInfo =
                nodeMisc.get( "privacy", FreeGuide.config.privacyInfo );

            remConfig.reminderOn =
                nodeMisc.getBoolean( "reminders_on", remConfig.reminderOn );

            remConfig.reminderGiveUp =
                nodeMisc.getLong( 
                    "reminders_give_up_secs", remConfig.reminderGiveUp / 1000 ) * 1000;

            remConfig.reminderWarning =
                nodeMisc.getLong( 
                    "reminders_warning_secs", remConfig.reminderWarning / 1000 ) * 1000;

        }

        if( nodeCommandline != null )
        {
            FreeGuide.config.browserCommand =
                nodeCommandline.get( 
                    "browser_command.1", FreeGuide.config.browserCommand );

        }

        if( nodeChannelsSet != null )
        {

            for( int i = 1;; i++ )
            { // convert channels sets

                String name = nodeChannelsSet.get( i + ".name", null );

                String channels =
                    nodeChannelsSet.get( i + ".channelids", null );

                if( ( name == null ) || ( channels == null ) )
                {

                    break;

                }

                TVChannelsSet chs = new TVChannelsSet(  );

                chs.setName( name );

                String[] ch = channels.split( "\\|" );

                for( int j = 0; j < ch.length; j++ )
                {
                    chs.add( 
                        new TVChannelsSet.Channel( "xmltv/" + ch[j], ch[j] ) );

                }

                MainController.config.channelsSetsList.add( chs );

            }
        }

        if( nodeFavourites != null )
        {

            for( int i = 1;; i++ )
            { // convert favourites

                String name = nodeFavourites.get( i + ".name", null );

                if( name == null )
                {

                    break;

                }

                Favourite f = new Favourite(  );

                f.setName( name );

                f.setTitleContains( 
                    nodeFavourites.get( i + ".title_contains", null ) );

                f.setTitleString( 
                    nodeFavourites.get( i + ".title_string", null ) );

                f.setTitleRegex( 
                    nodeFavourites.get( i + ".title_regex", null ) );

                String channelId =
                    nodeFavourites.get( i + ".channel_id", null );

                if( channelId != null )
                {
                    f.setChannelID( "xmltv/" + channelId );
                }

                f.setDayOfWeek( 
                    nodeFavourites.getInt( i + ".day_of_week", -1 ) );

                String afterTime =
                    nodeFavourites.get( i + ".after_time", null );

                if( afterTime != null )
                {
                    f.setAfterTime( new Time( afterTime ) );

                }

                String beforeTime =
                    nodeFavourites.get( i + ".before_time", null );

                if( beforeTime != null )
                {
                    f.setBeforeTime( new Time( beforeTime ) );

                }

                SelectionManager.addFavourite( f );

            }
        }

        if( nodeScreen != null )
        {
            hovConfig.displayAlignToLeft =
                nodeScreen.getBoolean( 
                    "align_text_to_left", hovConfig.displayAlignToLeft );

            hovConfig.display24time =
                nodeScreen.getBoolean( 
                    "display_24hour_time", hovConfig.display24time );

            hovConfig.displayTime =
                nodeScreen.getBoolean( 
                    "display_programme_time", hovConfig.displayTime );

            hovConfig.displayDelta =
                nodeScreen.getBoolean( 
                    "display_time_delta", hovConfig.displayDelta );

            hovConfig.displayTooltips =
                nodeScreen.getBoolean( 
                    "display_tooltips", hovConfig.displayTooltips );

            hovConfig.sizeChannelHeight =
                nodeScreen.getInt( 
                    "channel_height", hovConfig.sizeChannelHeight );

            hovConfig.sizeChannelPanelWidth =
                nodeScreen.getInt( 
                    "panel_width", hovConfig.sizeChannelPanelWidth );

            hovConfig.colorMovie =
                readColor( 
                    nodeScreen, "programme_movie_colour", hovConfig.colorMovie );

            hovConfig.colorNonTicked =
                readColor( 
                    nodeScreen, "programme_normal_colour",
                    hovConfig.colorNonTicked );

            hovConfig.colorChannel =
                readColor( 
                    nodeScreen, "channel_colour", hovConfig.colorChannel );

            remConfig.colorTicked =
                readColor( nodeScreen, "", remConfig.colorTicked );

            hovConfig.positionSplitPaneHorizontalTop =
                nodeScreen.getInt( 
                    "viewer_splitpane_horizontal",
                    hovConfig.positionSplitPaneHorizontalTop );

            hovConfig.positionSplitPaneHorizontalBottom =
                nodeScreen.getInt( 
                    "viewer_splitpane_horizontal_bottom",
                    hovConfig.positionSplitPaneHorizontalBottom );

            hovConfig.positionSplitPaneVertical =
                nodeScreen.getInt( 
                    "viewer_splitpane_vertical",
                    hovConfig.positionSplitPaneVertical );

            MainController.config.ui.LFname =
                nodeScreen.get( "look_and_feel", "Metal" );
            MainController.config.ui.mainWindowPosition.x =
                nodeScreen.getInt( 
                    "viewer_left",
                    MainController.config.ui.mainWindowPosition.x );

            MainController.config.ui.mainWindowPosition.y =
                nodeScreen.getInt( 
                    "viewer_top", MainController.config.ui.mainWindowPosition.y );

            MainController.config.ui.mainWindowPosition.width =
                nodeScreen.getInt( 
                    "viewer_width",
                    MainController.config.ui.mainWindowPosition.width );

            MainController.config.ui.mainWindowPosition.height =
                nodeScreen.getInt( 
                    "viewer_height",
                    MainController.config.ui.mainWindowPosition.height );

            hovConfig.currentChannelSetName =
                nodeScreen.get( "viewer_channel_set", null );

        }

        listXMLTVConfigs(  );

        nodeChannelsSet.removeNode(  );

        nodeCommandline.removeNode(  );

        nodeFavourites.removeNode(  );

        nodeMisc.removeNode(  );

        nodeScreen.removeNode(  );

        root.node( "chosenprogs" ).removeNode(  );

        FreeGuide.saveConfig(  );

        PreferencesHelper.save( 
            root.node( "mainController" ), MainController.config );

        PreferencesHelper.save( 
            root.node( "modules/ui-horizontal" ), hovConfig );
        PreferencesHelper.save( 
            root.node( "modules/reminder-alarm" ), remConfig );
    }

    protected static Color readColor( 
        Preferences node, String prefix, Color defaultColor )
    {

        int r = node.getInt( prefix + ".r", defaultColor.getRed(  ) );

        int g = node.getInt( prefix + ".g", defaultColor.getGreen(  ) );

        int b = node.getInt( prefix + ".b", defaultColor.getBlue(  ) );

        return new Color( r, g, b );

    }

    protected static void listXMLTVConfigs(  )
    {

        File dir;

        if( FreeGuide.runtimeInfo.isUnix )
        {
            dir = new File( System.getProperty( "user.home" ) + "/.xmltv/" );
        }
        else
        {
            dir = new File( 
                    FreeGuide.runtimeInfo.installDirectory + "/xmltv/" );
        }

        xmltvConfigs =
            dir.listFiles( 
                new FilenameFilter(  )
                {
                    public boolean accept( File dir, String name )
                    {

                        return name.startsWith( "tv_grab_" )
                        && name.endsWith( ".conf" );

                    }
                } );
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

    protected static String oldNameToCountryID( final String oldName )
    {

        if( "France".equals( oldName ) )
        {

            return "FR";
        }
        else if( "Hungary or Romania".equals( oldName ) )
        {

            return "HU";
        }
        else if( "Germany".equals( oldName ) )
        {

            return "DE";
        }
        else if( "Denmark".equals( oldName ) )
        {

            return "DK";
        }
        else if( "Spain (Digital)".equals( oldName ) )
        {

            return "ES";
        }
        else if( "Spain".equals( oldName ) )
        {

            return "ES";
        }
        else if( "Finland".equals( oldName ) )
        {

            return "FI";
        }
        else if( "Italy".equals( oldName ) )
        {

            return "IT";
        }
        else if( "Japan".equals( oldName ) )
        {

            return "JP";
        }
        else if( "North America (US and Canada)".equals( oldName ) )
        {

            return "US";
        }
        else if( "Netherlands (alternative)".equals( oldName ) )
        {

            return "NL";
        }
        else if( "Netherlands".equals( oldName ) )
        {

            return "NL";
        }
        else if( "Norway".equals( oldName ) )
        {

            return "NO";
        }
        else if( "Portugal".equals( oldName ) )
        {

            return "PT";
        }
        else if( "Sweden".equals( oldName ) )
        {

            return "SW";
        }
        else if( "UK".equals( oldName ) )
        {

            return "UK";
        }
        else if( "UK (slow but more detail)".equals( oldName ) )
        {

            return "UK";
        }

        return null;
    }
}
