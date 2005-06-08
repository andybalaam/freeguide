package freeguide.plugins.grabber.kulichki;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;

import freeguide.lib.grabber.HttpBrowser;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleConfigurationUI;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.prefs.Preferences;

import javax.swing.JDialog;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabberKulichki extends BaseModule implements IModuleGrabber
{

    /** DOCUMENT ME! */
    public static final String ID = "grabber-kulichki";
    protected Properties TIME_ZONES;
    protected TimeZone TIME_ZONE_DEFAULT =
        TimeZone.getTimeZone( "Europe/Moscow" );
    protected KulichkiConfig config = new KulichkiConfig(  );

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return ID;

    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVChannelsSet getChannelsList(  ) throws Exception
    {

        final TVChannelsSet result = new TVChannelsSet(  );

        result.add( new TVChannelsSet.Channel( ID, "All" ) );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        HandlerPackets handlerPackets = new HandlerPackets(  );

        HandlerChannels handlerChanels = new HandlerChannels(  );

        browser.loadURL( "http://tv.kulichki.net" );

        browser.parse( handlerPackets );

        String[] weeks = handlerPackets.getWeeks(  );

        String[] packetIDs = handlerPackets.getPacketIDs(  );

        Map request = new TreeMap(  );

        request.put( "week", weeks[0] );

        for( int j = 0; j < packetIDs.length; j++ )
        {
            result.add( 
                new TVChannelsSet.Channel( 
                    ID + "/" + packetIDs[j],
                    (String)handlerPackets.packetList.get( packetIDs[j] ) ) );

            request.put( "pakets", packetIDs[j] );

            browser.loadURL( 
                "http://tv.kulichki.net/cgi-bin/gpack.cgi", request, true );

            browser.parse( handlerChanels );

            Iterator it = handlerChanels.channelList.keySet(  ).iterator(  );

            while( it.hasNext(  ) )
            {

                String key = (String)it.next(  );

                String channelID;

                int pos = key.lastIndexOf( '.' );

                if( pos != -1 )
                {
                    channelID = key.substring( 0, pos );

                }

                else
                {
                    channelID = key;

                }

                result.add( 
                    new TVChannelsSet.Channel( 
                        ID + "/" + packetIDs[j] + "/" + channelID,
                        (String)handlerChanels.channelList.get( key ) ) );

            }
        }

        return result;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( IProgress progress, ILogger logger )
        throws Exception
    {

        if( config.channels.selectedChannelIDs.size(  ) == 0 )
        {

            return null;

        }

        if( TIME_ZONES == null )
        {
            loadTimeZones(  );

        }

        TVData result = new TVData(  );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( "Accept-Language", "ru" );

        browser.setHeader( "Accept-Charset", "windows-1251" );

        HandlerPackets handlerPackets = new HandlerPackets(  );

        HandlerChannels handlerChanels = new HandlerChannels(  );

        HandlerProg handlerProg = new HandlerProg( result, logger );

        /*browser.loadURL("file:///tmp/p1data.html");
        
        
        handlerProg.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        
        
        browser.parse(handlerProg);
        
        
        if (true) return result;*/
        logger.info( "Load initial page" );

        browser.loadURL( "http://tv.kulichki.net" );

        browser.parse( handlerPackets );

        String[] weeks = handlerPackets.getWeeks(  );

        String[] packets = handlerPackets.getPacketIDs(  );

        Map request = new TreeMap(  );

        Map requestChannels = new TreeMap(  );

        requestChannels.put( 
            "day", new String[] { "1", "2", "3", "4", "5", "6", "7" } );

        for( int i = 0; i < weeks.length; i++ )
        {
            request.put( "week", weeks[i] );

            requestChannels.put( "week", weeks[i] );

            for( int j = 0; j < packets.length; j++ )
            {
                request.put( "pakets", packets[j] );

                handlerProg.setChannelIDprefix( ID + "/" + packets[j] + "/" );

                String tzName = TIME_ZONES.getProperty( packets[j] );

                if( tzName != null )
                {
                    handlerProg.setTimeZone( TimeZone.getTimeZone( tzName ) );

                }

                else
                {
                    logger.warning( 
                        "Unknown timezone for packet '" + packets[j]
                        + "'. Will use Europe/Moscow timezone." );

                    handlerProg.setTimeZone( TIME_ZONE_DEFAULT );

                }

                logger.info( 
                    "Load week [" + ( i + 1 ) + "/" + weeks.length
                    + "] packet [" + ( j + 1 ) + "/" + packets.length
                    + "]: channel list" );

                browser.loadURL( 
                    "http://tv.kulichki.net/cgi-bin/gpack.cgi", request, true );

                browser.parse( handlerChanels );

                logger.info( 
                    "Load week [" + ( i + 1 ) + "/" + weeks.length
                    + "] packet [" + ( j + 1 ) + "/" + packets.length
                    + "]: channel data" );

                requestChannels.put( 
                    "chanel", handlerChanels.channelList.keySet(  ) );

                browser.loadURL( 
                    "http://tv.kulichki.net/andgon/cgi-bin/itv.cgi",
                    requestChannels, true );

                browser.parse( handlerProg );

            }
        }

        return result;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {

        return new KulichkiConfigurationUIController( this );

    }

    protected void loadTimeZones(  )
    {
        TIME_ZONES = new Properties(  );

        try
        {
            TIME_ZONES.load( 
                this.getClass(  ).getClassLoader(  ).getResourceAsStream( 
                    this.getClass(  ).getPackage(  ).getName(  ).replace( 
                        '.', '/' ) + "/timezones.properties" ) );

        }
        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE,
                "Error loading timezone settings for tv.kulichki.net", ex );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefs DOCUMENT_ME!
     */
    public void setConfigStorage( Preferences prefs )
    {
        super.setConfigStorage( prefs );

        loadObjectFromPreferences( config );

    }

    /**
     * DOCUMENT_ME!
     */
    public void saveConfig(  )
    {
        saveObjectToPreferences( config );

    }
}
