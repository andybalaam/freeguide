package freeguide.plugins.grabber.hallmark;

import freeguide.lib.general.StringHelper;

import freeguide.lib.grabber.HtmlHelper;
import freeguide.lib.grabber.HttpBrowser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Prepare information about all hallmark sites.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PrepareInfo
{

    protected static Writer wr;
    protected static Pattern RE_CNTRY_URL =
        Pattern.compile( "http://([a-z]{2}).hallmarkchannel.com" );
    protected static Pattern RE_LANG = Pattern.compile( "LANG=([A-Z0-9_]+)" );
    protected static String STR_CNTRY_USA = "http://www.hallmarkchannel.com";

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {

        /*String[] tzn = TimeZone.getAvailableIDs(  );
        Arrays.sort( tzn );

        Date today = new Date(  );

        for( int i = 0; i < tzn.length; i++ )
        {

            TimeZone tz = TimeZone.getTimeZone( tzn[i] );
            String shortName =
                tz.getDisplayName( tz.inDaylightTime( today ), TimeZone.SHORT );
            String longName =
                tz.getDisplayName( tz.inDaylightTime( today ), TimeZone.LONG );

            System.out.println( tzn[i] + " - " + shortName + "/" + longName );
        }

        System.exit( 1 );*/
        final HttpBrowser browser = new HttpBrowser(  );
        browser.loadURL( "http://www.hallmarkchannel.com/chooseCountry.jsp" );

        HandlerCountries countries = new HandlerCountries(  );
        browser.parse( countries );
        System.out.println(  );

        wr = new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( 
                        "src/plugins/grabber-hallmark/java/freeguide/plugins/grabber/hallmark/info.xml" ),
                    "UTF-8" ) );
        writeHeader(  );

        int i = 1;

        for( 
            Iterator it = countries.countries.keySet(  ).iterator(  );
                it.hasNext(  ); i++ )
        {

            String country = (String)it.next(  );
            String url = (String)countries.countries.get( country );
            System.out.println( 
                "Country " + country + " (" + i + "/"
                + countries.countries.size(  ) + ") - " + url );

            /*if( !"BELARUS".equals( country ) )
            {

                continue;
            }*/
            final String cntry = getCntry( url );
            System.out.println( "url = " + url + "   cntry = " + cntry );

            if( cntry == null )
            {
                System.out.println( "Error read url: " + url );

                continue;
            }

            writeHeaderCountry( cntry, country, url );

            Map langs = new TreeMap(  );

            try
            {
                langs = getLanguages( url, cntry );
            }
            catch( Exception ex )
            {
                System.err.println( 
                    "Error load from " + url + ": " + ex.getMessage(  ) );
                ex.printStackTrace(  );
            }

            for( Iterator it2 = langs.keySet(  ).iterator(  );
                    it2.hasNext(  ); )
            {

                String langName = (String)it2.next(  );
                String langParam = (String)langs.get( langName );
                writeLanguage( langName, langParam );
            }

            writeFooterCountry(  );

            wr.flush(  );
        }

        writeFooter(  );

        wr.flush(  );
        wr.close(  );
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

                return "US";
            }
            else
            {

                return null;
            }
        }
    }

    protected static Map getLanguages( final String url, final String cntry )
        throws Exception
    {

        final HttpBrowser browser = new HttpBrowser(  );
        browser.loadURL( 
            url + "/framework.jsp?BODY=weekSchedCal.jsp&CNTRY=" + cntry );

        HandlerLanguages h = new HandlerLanguages(  );
        browser.parse( h );

        Map langs = h.getLanguages(  );
        Map result = new TreeMap(  );

        for( Iterator it = langs.keySet(  ).iterator(  ); it.hasNext(  ); )
        {

            String langName = (String)it.next(  );
            String langParam = (String)langs.get( langName );
            Matcher m = RE_LANG.matcher( langParam );

            if( m.find(  ) )
            {
                result.put( langName, m.group( 1 ) );
            }
            else
            {
                System.err.println( "Invalid language: " + langParam );
            }
        }

        HallmarkParserSchedule parserTimeZone =
            new HallmarkParserSchedule( null, null, cntry.equals( "US" ) );
        browser.parse( parserTimeZone );

        return result;
    }

    protected static void writeHeader(  ) throws IOException
    {
        wr.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" );
        wr.write( "\n" );
        wr.write( "<hallmark>\n" );
    }

    protected static void writeFooter(  ) throws IOException
    {
        wr.write( "</hallmark>\n" );
    }

    protected static void writeHeaderCountry( 
        final String id, final String country, final String url )
        throws IOException
    {
        wr.write( 
            "  <country id=\"" + id + "\" country=\""
            + StringHelper.toXML( country ) + "\" url=\"" + url + "\">\n" );
    }

    protected static void writeFooterCountry(  ) throws IOException
    {
        wr.write( "  </country>\n" );
    }

    protected static void writeLanguage( final String name, final String id )
        throws IOException
    {
        wr.write( "    <language name=\"" + name + "\" id=\"" + id + "\"/>\n" );
    }

    protected static class HandlerCountries
        extends HtmlHelper.DefaultContentHandler
    {

        Map countries = new TreeMap(  );
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

    protected static class HandlerLanguages
        extends HtmlHelper.DefaultContentHandler
    {

        Map languages = new TreeMap(  );
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
        public Map getLanguages(  )
        {

            return languages;
        }
    }
}
