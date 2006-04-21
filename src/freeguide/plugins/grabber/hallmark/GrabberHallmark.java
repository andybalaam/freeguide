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

import org.xml.sax.SAXException;

import java.io.IOException;

import java.util.Iterator;
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
     * @throws Exception DOCUMENT_ME!
     */
    public void grabData( 
        IProgress progress, ILogger logger, final IStoragePipe storage )
        throws Exception
    {

        final HttpBrowser browser = new HttpBrowser(  );
        HallmarkInfo.Country country =
            HallmarkInfo.getCountry( config.countryId );
        HallmarkInfo.Language lang =
            country.getLanguage( config.languageName );

        logger.info( "Load schedule page" );

        /*browser.loadURL("http://by.hallmarkchannel.com");
        System.out.println("p1");
        browser.loadURL("http://by.hallmarkchannel.com/framework.jsp?BODY=weekSchedCal.jsp&CNTRY=BY&LANG=DAM_LANGUAGE_137844");
        System.out.println("p3");
        browser.saveAs("e:/hallmark.html");*/
        browser.loadURL( country.url );

        StringBuffer urlSched = new StringBuffer(  );
        urlSched.append( country.url );
        urlSched.append( "/framework.jsp?BODY=weekSchedCal.jsp&CNTRY=" )
                .append( country.id );

        if( lang != null )
        {
            urlSched.append( "&LANG=" ).append( lang.id );
        }

        browser.loadURL( urlSched.toString(  ) );

        final String channelID =
            "hallmark/" + config.countryId + "/"
            + ( ( lang != null ) ? lang.name : "Default" ) + "/hallmark";
        TVChannel channel = new TVChannel( channelID, "Hallmark Channel" );

        Map descriptions = new TreeMap(  );
        HallmarkParserSchedule parser =
            new HallmarkParserSchedule( 
                channel, descriptions, country.id.equals( "US" ) );
        browser.parse( parser );

        int di = 0;

        for( 
            Iterator it = descriptions.entrySet(  ).iterator(  );
                it.hasNext(  ); di++ )
        {
            logger.info( 
                "Load description [" + di + "/" + descriptions.size(  ) + "]" );

            Map.Entry entry = (Map.Entry)it.next(  );
            String key = (String)entry.getKey(  );
            String description = loadDescription( country.url, lang, key );
            List list = (List)entry.getValue(  );

            for( int i = 0; i < list.size(  ); i++ )
            {

                TVProgramme prog = (TVProgramme)list.get( i );

                if( prog.getDescription(  ) != null )
                {
                    prog.setDescription( 
                        description + "\n" + prog.getDescription(  ) );
                }
                else
                {
                    prog.setDescription( description );
                }
            }
        }

        storage.addChannel( channel );
        storage.finishBlock(  );
    }

    protected String loadDescription( 
        final String url, final Language lang, final String key )
        throws IOException, SAXException
    {

        final HttpBrowser browser = new HttpBrowser(  );
        StringBuffer urlSched = new StringBuffer(  );
        urlSched.append( url );
        urlSched.append( "/program.jsp?CONTENT=" ).append( key );

        if( lang != null )
        {
            urlSched.append( "&LANG=" ).append( lang.id );
        }

        //browser.loadURL(url+"/program.jsp?LANG="+lang+"&CONTENT="+key);
        browser.loadURL( urlSched.toString(  ) );

        HallmarkParserDescription parser = new HallmarkParserDescription(  );
        browser.parse( parser );

        return parser.getResult(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void stopGrabbing(  )
    {
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
}
