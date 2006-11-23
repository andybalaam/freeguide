package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.grabber.HttpBrowser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;

import javax.swing.JDialog;

/**
 * Grabber for tv.kulichki.net.
 *
 * @author Alex Buloichik
 */
public class GrabberKulichki extends BaseModule implements IModuleGrabber
{
    protected static final TimeZone TIME_ZONE_DEFAULT =
        TimeZone.getTimeZone( "Europe/Moscow" );
    protected static final String FILE_GROUPNAMES =
        "resources/plugins/grabber/kulichki/groupnames.properties";
    protected static final String FILE_TIMEZONES =
        "resources/plugins/grabber/kulichki/timezones.properties";
    protected static final String VALUE_ACCEPT_LANGUAGE = "ru";
    protected static final String VALUE_ACCEPT_CHARSET = "windows-1251";
    protected static final String URL_START = "http://tv.kulichki.net";
    protected static final String URL_PACKET =
        "http://tv.kulichki.net/cgi-bin/gpack.cgi";
    protected static final String URL_DATA =
        "http://tv.kulichki.net/andgon/cgi-bin/itv.cgi";
    protected static final String PARAM_WEEK = "week";
    protected static final String PARAM_PACKET = "pakets";
    protected static final String CHANNEL_PREFIX_ID = "kulichki";
    protected static final String CHANNEL_PREFIX = "kulichki/";
    protected Properties TIME_ZONES;
    protected Properties GROUP_NAMES;
    protected KulichkiConfig config = new KulichkiConfig(  );

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {
        return config;
    }

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {
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

        result.add( new TVChannelsSet.Channel( CHANNEL_PREFIX_ID, "All" ) );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_CHARSET, VALUE_ACCEPT_CHARSET );

        HandlerPackets handlerPackets = new HandlerPackets(  );

        HandlerChannels handlerChanels = new HandlerChannels(  );

        browser.loadURL( URL_START );

        browser.parse( handlerPackets );

        String[] weeks = handlerPackets.getWeeks(  );

        final Collection packetIDs = handlerPackets.getPacketIDs(  );

        final Map<String, String> request = new TreeMap<String, String>(  );

        request.put( PARAM_WEEK, weeks[0] );

        for( final Iterator itPa = packetIDs.iterator(  ); itPa.hasNext(  ); )
        {
            final String packetName = (String)itPa.next(  );
            result.add( 
                new TVChannelsSet.Channel( 
                    CHANNEL_PREFIX + packetName,
                    (String)handlerPackets.packetList.get( packetName ) ) );

            request.put( PARAM_PACKET, packetName );

            browser.loadURL( URL_PACKET, request, true );

            browser.parse( handlerChanels );

            Iterator it = handlerChanels.channelList.keySet(  ).iterator(  );

            while( it.hasNext(  ) )
            {
                String key = (String)it.next(  );

                String channelID = getChannelIdByTag( key );

                result.add( 
                    new TVChannelsSet.Channel( 
                        CHANNEL_PREFIX + packetName + '/' + channelID,
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
     * @param storage DOCUMENT ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void grabData( 
        IProgress progress, ILogger logger, final IStoragePipe storage )
        throws Exception
    {
        if( config.channels.selectedChannelIDs.size(  ) == 0 )
        {
            return;
        }

        if( TIME_ZONES == null )
        {
            loadTimeZones(  );
        }

        if( GROUP_NAMES == null )
        {
            loadGroupNames(  );
        }

        for( Iterator it = GROUP_NAMES.entrySet(  ).iterator(  );
                it.hasNext(  ); )
        {
            Map.Entry entry = (Map.Entry)it.next(  );
            storage.addChannel( 
                new TVChannel( 
                    CHANNEL_PREFIX + (String)entry.getKey(  ),
                    (String)entry.getValue(  ) ) );
        }

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_LANGUAGE, VALUE_ACCEPT_LANGUAGE );

        browser.setHeader( 
            HttpBrowser.HEADER_ACCEPT_CHARSET, VALUE_ACCEPT_CHARSET );

        HandlerPackets handlerPackets = new HandlerPackets(  );

        HandlerChannels handlerChanels = new HandlerChannels(  );

        HandlerProg handlerProg = new HandlerProg( storage, logger );

        logger.info( "Load initial page" );

        browser.loadURL( URL_START );

        browser.parse( handlerPackets );

        final String[] weeks = handlerPackets.getWeeks(  );

        final List packetsList =
            new ArrayList( handlerPackets.getPacketIDs(  ) );

        for( final Iterator it = packetsList.iterator(  ); it.hasNext(  ); )
        {
            final String packetName = (String)it.next(  );

            if( 
                !config.channels.isSelected( CHANNEL_PREFIX + packetName )
                    && !config.channels.isChildSelected( 
                        CHANNEL_PREFIX + packetName ) )
            {
                it.remove(  );
            }
        }

        final String[] packets =
            (String[])packetsList.toArray( new String[packetsList.size(  )] );

        Map request = new TreeMap(  );

        final Map<String, Object> requestChannels =
            new TreeMap<String, Object>(  );

        requestChannels.put( 
            "day", new String[] { "1", "2", "3", "4", "5", "6", "7" } );

        progress.setStepCount( weeks.length * packets.length );
        progress.setStepNumber( 0 );

        for( int i = 0; i < weeks.length; i++ )
        {
            request.put( PARAM_WEEK, weeks[i] );

            requestChannels.put( "week", weeks[i] );

            for( int j = 0; j < packets.length; j++ )
            {
                request.put( PARAM_PACKET, packets[j] );

                handlerProg.setChannelIDprefix( 
                    CHANNEL_PREFIX + packets[j] + '/' );

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

                browser.loadURL( URL_PACKET, request, true );

                browser.parse( handlerChanels );

                logger.info( 
                    "Load week [" + ( i + 1 ) + "/" + weeks.length
                    + "] packet [" + ( j + 1 ) + "/" + packets.length
                    + "]: channel data" );

                for( 
                    Iterator it =
                        handlerChanels.channelList.keySet(  ).iterator(  );
                        it.hasNext(  ); )
                {
                    String channelID =
                        CHANNEL_PREFIX + packets[j] + '/'
                        + getChannelIdByTag( (String)it.next(  ) );

                    if( !config.channels.isSelected( channelID ) )
                    {
                        it.remove(  );
                    }
                }

                requestChannels.put( 
                    "chanel", handlerChanels.channelList.keySet(  ) );

                browser.loadURL( URL_DATA, requestChannels, true );

                browser.parse( handlerProg );
                storage.finishBlock(  );

                progress.setStepNumber( ( i * packets.length ) + j + 1 );
            }
        }
    }

    protected String getChannelIdByTag( final String tag )
    {
        int pos = tag.lastIndexOf( '.' );

        if( pos != -1 )
        {
            return tag.substring( 0, pos );
        }
        else
        {
            return tag;
        }
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

        final InputStream in =
            GrabberKulichki.class.getClassLoader(  )
                                 .getResourceAsStream( FILE_TIMEZONES );

        try
        {
            if( in == null )
            {
                throw new FileNotFoundException(  );
            }

            TIME_ZONES.load( in );
            in.close(  );
        }
        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( 
                Level.SEVERE,
                "Error loading timezone settings for tv.kulichki.net", ex );
        }
    }

    protected void loadGroupNames(  )
    {
        GROUP_NAMES = new Properties(  );

        final InputStream in =
            GrabberKulichki.class.getClassLoader(  )
                                 .getResourceAsStream( FILE_GROUPNAMES );

        try
        {
            if( in == null )
            {
                throw new FileNotFoundException(  );
            }

            GROUP_NAMES.load( in );
            in.close(  );
        }
        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  )
                       .log( 
                Level.SEVERE,
                "Error loading groupnames settings for tv.kulichki.net", ex );
        }
    }
}
