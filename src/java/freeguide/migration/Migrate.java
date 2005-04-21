package freeguide.migration;

import freeguide.FreeGuide;

import freeguide.gui.viewer.MainController;

import freeguide.lib.fgspecific.PluginsManager;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.selection.Favourite;
import freeguide.lib.fgspecific.selection.SelectionManager;

import freeguide.lib.general.PreferencesHelper;
import freeguide.lib.general.Time;
import freeguide.lib.general.Version;

import freeguide.plugins.ui.horizontal.HorizontalViewer;

import java.awt.Color;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import java.util.prefs.Preferences;

/**
 * Migration support from previous versions.
 *
 * @author Alex Bulouichik (alex73 at zaval.org)
 */
public class Migrate
{

    File[] xmltvConfigs;

    /**
     * Main method to run.
     *
     * @throws Exception
     */
    public void migrateBeforeWizard(  ) throws Exception
    {

        if( Preferences.userRoot(  ).nodeExists( "/org/freeguide-tv/misc" ) )
        {

            String storedVersionName =
                Preferences.userRoot(  ).node( "/org/freeguide-tv/misc" ).get( 
                    "install_version", null );

            if( storedVersionName == null )
            {

                return;

            }

            if( 
                new Version( storedVersionName ).compareTo( 
                        new Version( 0, 10, 0 ) ) < 0 )
            {
                loadFromOld(  );
                FreeGuide.config.version = storedVersionName;
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void migrateAfterWizard(  ) throws Exception
    {

        if( xmltvConfigs != null )
        {

            for( int i = 0; i < xmltvConfigs.length; i++ )
            {
                loadConfigFile( xmltvConfigs[i] );

            }
        }
    }

    /**
     * Load preferences from less than 0.9 version.
     *
     * @throws Exception
     */
    protected void loadFromOld(  ) throws Exception
    {

        final Preferences root =
            Preferences.userRoot(  ).node( "/org/freeguide-tv" );

        final Preferences nodeFavourites;
        final HorizontalViewer hov =
            (HorizontalViewer)PluginsManager.getViewerByID( "Horizontal" );

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

            FreeGuide.config.countryID =
                nodeMisc.get( "region", FreeGuide.config.countryID );

            hov.config.dayStartTime.setTimeHHMMString( 
                nodeMisc.get( 
                    "day_start_time", hov.config.dayStartTime.getHHMMString(  ) ) );

            FreeGuide.config.privacyInfo =
                nodeMisc.get( "privacy", FreeGuide.config.privacyInfo );

            MainController.config.reminderOn =
                nodeMisc.getBoolean( 
                    "reminders_on", MainController.config.reminderOn );

            MainController.config.reminderGiveUp =
                nodeMisc.getLong( 
                    "reminders_give_up_secs",
                    MainController.config.reminderGiveUp / 1000 ) * 1000;

            MainController.config.reminderWarning =
                nodeMisc.getLong( 
                    "reminders_warning_secs",
                    MainController.config.reminderWarning / 1000 ) * 1000;

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
            hov.config.displayAlignToLeft =
                nodeScreen.getBoolean( 
                    "align_text_to_left", hov.config.displayAlignToLeft );

            hov.config.display24time =
                nodeScreen.getBoolean( 
                    "display_24hour_time", hov.config.display24time );

            hov.config.displayTime =
                nodeScreen.getBoolean( 
                    "display_programme_time", hov.config.displayTime );

            hov.config.displayDelta =
                nodeScreen.getBoolean( 
                    "display_time_delta", hov.config.displayDelta );

            hov.config.displayTooltips =
                nodeScreen.getBoolean( 
                    "display_tooltips", hov.config.displayTooltips );

            hov.config.sizeChannelHeight =
                nodeScreen.getInt( 
                    "channel_height", hov.config.sizeChannelHeight );

            hov.config.sizeChannelPanelWidth =
                nodeScreen.getInt( 
                    "panel_width", hov.config.sizeChannelPanelWidth );

            hov.config.colorHeart =
                readColor( 
                    nodeScreen, "programme_heart_colour", hov.config.colorHeart );

            hov.config.colorMovie =
                readColor( 
                    nodeScreen, "programme_movie_colour", hov.config.colorMovie );

            hov.config.colorNonTicked =
                readColor( 
                    nodeScreen, "programme_normal_colour",
                    hov.config.colorNonTicked );

            hov.config.colorChannel =
                readColor( 
                    nodeScreen, "channel_colour", hov.config.colorChannel );

            hov.config.colorTicked =
                readColor( nodeScreen, "", hov.config.colorTicked );

            hov.config.positionSplitPaneHorizontalTop =
                nodeScreen.getInt( 
                    "viewer_splitpane_horizontal",
                    hov.config.positionSplitPaneHorizontalTop );

            hov.config.positionSplitPaneHorizontalBottom =
                nodeScreen.getInt( 
                    "viewer_splitpane_horizontal_bottom",
                    hov.config.positionSplitPaneHorizontalBottom );

            hov.config.positionSplitPaneVertical =
                nodeScreen.getInt( 
                    "viewer_splitpane_vertical",
                    hov.config.positionSplitPaneVertical );

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

            hov.config.currentChannelSetName =
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

        PreferencesHelper.saveObject( 
            root.node( "mainController" ), "", MainController.config );

        if( hov != null )
        {
            PreferencesHelper.saveObject( 
                root.node( "modules/viewer/" + hov.getID(  ) ), "", hov.config );

        }
    }

    protected static Color readColor( 
        Preferences node, String prefix, Color defaultColor )
    {

        int r = node.getInt( prefix + ".r", defaultColor.getRed(  ) );

        int g = node.getInt( prefix + ".g", defaultColor.getGreen(  ) );

        int b = node.getInt( prefix + ".b", defaultColor.getBlue(  ) );

        return new Color( r, g, b );

    }

    protected void listXMLTVConfigs(  )
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

    protected void loadConfigFile( File f ) throws IOException
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
