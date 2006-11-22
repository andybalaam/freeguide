package freeguide.plugins.grabber.hallmark;

import freeguide.common.lib.grabber.HtmlHelper;
import freeguide.common.lib.grabber.HttpBrowser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.File;

import java.text.MessageFormat;

import java.util.Arrays;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Prepare information about all hallmark sites.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PrepareInfo
{
    protected static final Pattern RE_CNTRY_URL =
        Pattern.compile( "http://([a-z]{2}).hallmarkchannel.com" );
    protected static final Pattern RE_LANG =
        Pattern.compile( "LANG=([A-Z0-9_]+)" );
    protected static final String STR_CNTRY_USA =
        "http://www.hallmarkchannel.com";
    protected static final String URL_CHOOSE_COUNTRY =
        "http://www.hallmarkchannel.com/chooseCountry.jsp";
    protected static final String SRC_INFO_FILE_PATH =
        "src/resources/plugins/grabber/hallmark/info.xml";
    protected static final String UTF8_CHARSET = "UTF-8";

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        String[] timezones = TimeZone.getAvailableIDs(  );
        Arrays.sort( timezones );

        for( String tz : timezones )
        {
            System.out.println( tz );
        }

        final HttpBrowser browser = new HttpBrowser(  );
        browser.loadURL( URL_CHOOSE_COUNTRY );

        HandlerCountries countries = new HandlerCountries(  );
        browser.parse( countries );
        System.out.println(  );

        Document doc =
            DocumentBuilderFactory.newInstance(  ).newDocumentBuilder(  )
                                  .newDocument(  );
        final Element docHallmark = doc.createElement( "hallmark" );
        doc.appendChild( docHallmark );

        int i = 1;

        for( final Map.Entry<String, String> entry : countries.countries
            .entrySet(  ) )
        {
            String country = entry.getKey(  );
            String url = entry.getValue(  );

            System.out.println( 
                MessageFormat.format( 
                    "Country {0} ({1}/{2}) - {3}",
                    new Object[]
                    {
                        country, new Integer( i ),
                        new Integer( countries.countries.size(  ) ), url
                    } ) );

            final String id = getCntry( url );
            System.out.println( "url = " + url + "   cntry = " + id );

            if( id == null )
            {
                System.out.println( "Error read url: " + url );

                continue;
            }

            final Element docCountry = doc.createElement( "country" );
            docCountry.setAttribute( "id", id );
            docCountry.setAttribute( "country", country );
            docCountry.setAttribute( "url", url );

            for( final Map.Entry<String, String> lang : getLanguages( url, id )
                                                            .entrySet(  ) )
            {
                final Element docLanguage = doc.createElement( "language" );
                docLanguage.setAttribute( "name", lang.getKey(  ) );
                docLanguage.setAttribute( "id", lang.getValue(  ) );
                docCountry.appendChild( docLanguage );
            }

            docHallmark.appendChild( docCountry );
            i++;
        }

        final Transformer xformer =
            TransformerFactory.newInstance(  ).newTransformer(  );
        xformer.setOutputProperty( "indent", "yes" );

        xformer.transform( 
            new DOMSource( doc ),
            new StreamResult( new File( SRC_INFO_FILE_PATH ) ) );

    }

    protected static String getCntry( final String url )
        throws Exception
    {
        Matcher m = RE_CNTRY_URL.matcher( url );

        if( m.matches(  ) )
        {
            return m.group( 1 ).toUpperCase(  );
        }
        else
        {
            if( STR_CNTRY_USA.equals( url ) )
            {
                return GrabberHallmark.US_COUNTRY_CODE;
            }
            else
            {
                return null;
            }
        }
    }

    protected static Map<String, String> getLanguages( 
        final String url, final String cntry ) throws Exception
    {
        final HttpBrowser browser = new HttpBrowser(  );
        browser.loadURL( url + GrabberHallmark.URL_COUNTRY_PREFIX + cntry );

        HandlerLanguages h = new HandlerLanguages(  );
        browser.parse( h );

        Map<String, String> langs = h.getLanguages(  );
        Map<String, String> result = new TreeMap<String, String>(  );

        for( final Map.Entry<String, String> entry : langs.entrySet(  ) )
        {
            Matcher m = RE_LANG.matcher( entry.getValue(  ) );

            if( m.find(  ) )
            {
                result.put( entry.getKey(  ), m.group( 1 ) );
            }
            else
            {
                System.err.println( "Invalid language: " + entry.getValue(  ) );
            }
        }

        /*HallmarkParserSchedule parserTimeZone =
            new HallmarkParserSchedule( null, null, cntry.equals( "US" ) );
        browser.parse( parserTimeZone );*/
        return result;
    }

    protected static class HandlerCountries extends HtmlHelper.DefaultContentHandler
    {
        Map<String, String> countries = new TreeMap<String, String>(  );
        protected boolean process = false;
        protected String currentOptionValue;
        protected StringBuffer currentText = new StringBuffer(  );

        /**
         * DOCUMENT_ME!
         *
         * @param uri DOCUMENT_ME!
         * @param localName DOCUMENT_ME!
         * @param qName DOCUMENT_ME!
         * @param atts DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void startElement( 
            String uri, String localName, String qName, Attributes atts )
            throws SAXException
        {
            if( 
                "select".equals( qName )
                    && "CNTRY".equals( atts.getValue( "name" ) ) )
            {
                process = true;
            }
            else if( process && "option".equals( qName ) )
            {
                currentOptionValue = atts.getValue( "value" );
                currentText.setLength( 0 );

                if( 
                    ( currentOptionValue == null )
                        || "".equals( currentOptionValue )
                        || !currentOptionValue.endsWith( 
                            "hallmarkchannel.com" ) )
                {
                    currentOptionValue = null;
                }
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param uri DOCUMENT_ME!
         * @param localName DOCUMENT_ME!
         * @param qName DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void endElement( String uri, String localName, String qName )
            throws SAXException
        {
            if( "select".equals( qName ) )
            {
                process = false;
            }
            else if( 
                process && "option".equals( qName )
                    && ( currentOptionValue != null ) )
            {
                countries.put( currentText.toString(  ), currentOptionValue );
                currentOptionValue = null;
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param ch DOCUMENT_ME!
         * @param start DOCUMENT_ME!
         * @param length DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void characters( char[] ch, int start, int length )
            throws SAXException
        {
            if( currentOptionValue != null )
            {
                currentText.append( ch, start, length );
            }
        }
    }

    protected static class HandlerLanguages extends HtmlHelper.DefaultContentHandler
    {
        protected Map<String, String> languages =
            new TreeMap<String, String>(  );
        protected boolean process = false;
        protected String currentOptionValue;
        protected StringBuffer currentText = new StringBuffer(  );

        /**
         * DOCUMENT_ME!
         *
         * @param uri DOCUMENT_ME!
         * @param localName DOCUMENT_ME!
         * @param qName DOCUMENT_ME!
         * @param atts DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void startElement( 
            String uri, String localName, String qName, Attributes atts )
            throws SAXException
        {
            if( 
                "select".equals( qName )
                    && "LANG".equals( atts.getValue( "name" ) ) )
            {
                process = true;
            }
            else if( process && "option".equals( qName ) )
            {
                currentOptionValue = atts.getValue( "value" );
                currentText.setLength( 0 );

                if( 
                    ( currentOptionValue == null )
                        || "".equals( currentOptionValue ) )
                {
                    currentOptionValue = null;
                }
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param uri DOCUMENT_ME!
         * @param localName DOCUMENT_ME!
         * @param qName DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void endElement( String uri, String localName, String qName )
            throws SAXException
        {
            if( "select".equals( qName ) )
            {
                process = false;
            }
            else if( 
                process && "option".equals( qName )
                    && ( currentOptionValue != null ) )
            {
                languages.put( 
                    currentText.toString(  ).trim(  ), currentOptionValue );
                currentOptionValue = null;
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @param ch DOCUMENT_ME!
         * @param start DOCUMENT_ME!
         * @param length DOCUMENT_ME!
         *
         * @throws SAXException DOCUMENT_ME!
         */
        public void characters( char[] ch, int start, int length )
            throws SAXException
        {
            if( currentOptionValue != null )
            {
                currentText.append( ch, start, length );
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Map<String, String> getLanguages(  )
        {
            return languages;
        }
    }
}
