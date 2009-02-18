package freeguide.plugins.grabber.hallmark;

import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.grabber.HttpBrowser;

import freeguide.common.plugininterfaces.BaseModule;
import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.common.plugininterfaces.IStoragePipe;

import freeguide.plugins.grabber.hallmark.HallmarkInfo.Language;
import freeguide.plugins.grabber.xmltv.XMLTVConfig.ModuleInfo;

import org.xml.sax.SAXException;

import java.io.IOException;

import java.text.MessageFormat;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;

/**
 * Parser for hallmarkchannel.com.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class GrabberHallmark extends BaseModule implements IModuleGrabber
{
    /** DOCUMENT ME! */
    public static final String URL_COUNTRY_PREFIX =
        "/framework.jsp?BODY=weekSchedCal.jsp&CNTRY=";
    protected static final String URL_PROGRAMME_PREFIX =
        "/program.jsp?CONTENT=";
    protected static final String LANG_PARAM = "LANG";

    /** DOCUMENT ME! */
    public static final String CHANNELS_PREFIX = "hallmark/";
    protected static final String DEFAULT_LANGUAGE_NAME = "Default";
    protected static final String CHANNEL_NAME = "Hallmark Channel";

    /** DOCUMENT ME! */
    public static final String US_COUNTRY_CODE = "US";
    protected final Config config = new Config(  );

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
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     * @param storage DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public boolean grabData( 
        IProgress progress, ILogger logger, final IStoragePipe storage )
        throws Exception
    {
        final HttpBrowser browser = new HttpBrowser(  );
        HallmarkInfo.Country country =
            HallmarkInfo.getCountry( config.countryId );
        HallmarkInfo.Language lang =
            country.getLanguage( config.languageName );

        logger.info( i18n.getString( "Logging.Schedule" ) );

        /*browser.loadURL("http://by.hallmarkchannel.com");
        System.out.println("p1");
        browser.loadURL("http://by.hallmarkchannel.com/framework.jsp?BODY=weekSchedCal.jsp&CNTRY=BY&LANG=DAM_LANGUAGE_137844");
        System.out.println("p3");
        browser.saveAs("e:/hallmark.html");*/
        browser.loadURL( country.url );

        StringBuffer urlSched = new StringBuffer(  );
        urlSched.append( country.url );
        urlSched.append( URL_COUNTRY_PREFIX ).append( country.id );

        if( lang != null )
        {
            urlSched.append( '&' ).append( LANG_PARAM ).append( '=' )
                    .append( lang.id );
        }

        browser.loadURL( urlSched.toString(  ) );

        final String channelID =
            CHANNELS_PREFIX + config.countryId + '/'
            + ( ( lang != null ) ? lang.name : DEFAULT_LANGUAGE_NAME );
        TVChannel channel = new TVChannel( channelID, CHANNEL_NAME );

        Map<String, List<TVProgramme>> descriptions =
            new TreeMap<String, List<TVProgramme>>(  );
        HallmarkParserSchedule parser =
            new HallmarkParserSchedule( 
                channel, descriptions, country.id.equals( US_COUNTRY_CODE ),
                i18n, logger );
        browser.parse( parser );

        int di = 0;

        for( final Map.Entry<String, List<TVProgramme>> entry : descriptions
            .entrySet(  ) )
        {
            di++;
            logger.info( 
                MessageFormat.format( 
                    i18n.getString( "Logging.Description" ), di,
                    descriptions.size(  ) ) );

            String key = (String)entry.getKey(  );
            String description = loadDescription( country.url, lang, key );
            List list = (List)entry.getValue(  );

            for( int i = 0; i < list.size(  ); i++ )
            {
                TVProgramme prog = (TVProgramme)list.get( i );

                if( prog.getDescription(  ) != null )
                {
                    prog.setDescription( 
                        description + '\n' + prog.getDescription(  ) );
                }
                else
                {
                    prog.setDescription( description );
                }
            }
        }

        storage.addChannel( channel );
        storage.finishBlock(  );

        return true;
    }

    protected String loadDescription( 
        final String url, final Language lang, final String key )
        throws IOException, SAXException
    {
        final HttpBrowser browser = new HttpBrowser(  );
        StringBuffer urlSched = new StringBuffer(  );
        urlSched.append( url );
        urlSched.append( URL_PROGRAMME_PREFIX ).append( key );

        if( lang != null )
        {
            urlSched.append( '&' ).append( LANG_PARAM ).append( '=' )
                    .append( lang.id );
        }

        //browser.loadURL(url+"/program.jsp?LANG="+lang+"&CONTENT="+key);
        browser.loadURL( urlSched.toString(  ) );

        HallmarkParserDescription parser = new HallmarkParserDescription(  );
        browser.parse( parser );

        return parser.getResult(  );
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
        return new HallmarkConfigurationUIController( this );
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Config
    {
        /** Selected Country ID. */
        public String countryId;

        /** Selected language name. */
        public String languageName;

        /** Number of weeks for download. */
        public int weeksNumber = 2;
    }

    public boolean chooseChannels( IProgress progress, ILogger logger )
    {
        // No need to choose channels for this grabber
        return false;
    }
    
    public int chooseChannelsOne( ModuleInfo moduleInfo, IProgress progress, ILogger logger )
    {
        // No need to choose channels for this grabber
        return -1;
    }
}
