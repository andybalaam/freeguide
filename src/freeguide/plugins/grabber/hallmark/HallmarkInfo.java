package freeguide.plugins.grabber.hallmark;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HallmarkInfo extends DefaultHandler
{
    protected static Country[] countriesList;
    protected final List countries = new ArrayList(  );
    protected final List languages = new ArrayList(  );
    protected Country currentCountry;

    /**
     * DOCUMENT_ME!
     *
     * @param uri DOCUMENT_ME!
     * @param localName DOCUMENT_ME!
     * @param qName DOCUMENT_ME!
     * @param attributes DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public void startElement( 
        String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        if( "country".equals( qName ) )
        {
            currentCountry = new Country(  );
            currentCountry.id = attributes.getValue( "id" );
            currentCountry.name = attributes.getValue( "country" );
            currentCountry.url = attributes.getValue( "url" );
            countries.add( currentCountry );
            languages.clear(  );
        }
        else if( "language".equals( qName ) )
        {
            Language info = new Language(  );
            info.id = attributes.getValue( "id" );
            info.name = attributes.getValue( "name" );
            languages.add( info );
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
        if( "country".equals( qName ) )
        {
            currentCountry.languages = (Language[])languages.toArray( 
                    new Language[languages.size(  )] );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public synchronized static Country[] getCountriesList(  )
    {
        if( countriesList == null )
        {
            countriesList = parseInfo(  );
        }

        return countriesList;
    }

    protected static Country[] parseInfo(  )
    {
        HallmarkInfo handler = new HallmarkInfo(  );
        InputSource ins =
            new InputSource( 
                HallmarkInfo.class.getClassLoader(  )
                                  .getResourceAsStream( 
                    "resources/plugins/grabber/hallmark/info.xml" ) );
        SAXParserFactory factory = SAXParserFactory.newInstance(  );

        try
        {
            SAXParser saxParser = factory.newSAXParser(  );
            saxParser.parse( ins, handler );
        }
        catch( Exception ex )
        {
            ex.printStackTrace(  );
        }

        return (Country[])handler.countries.toArray( 
            new Country[handler.countries.size(  )] );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param id DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Country getCountry( final String id )
    {
        Country[] countries = getCountriesList(  );

        for( int i = 0; i < countries.length; i++ )
        {
            if( countries[i].id.equals( id ) )
            {
                return countries[i];
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Country
    {
        /** Country ID. */
        public String id;

        /** Country Name. */
        public String name;

        /** Host URL for country. */
        public String url;

        /** Supported languages for country. */
        public Language[] languages;

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String toString(  )
        {
            return name;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param name DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public Language getLanguage( final String name )
        {
            for( int j = 0; j < languages.length; j++ )
            {
                if( languages[j].name.equals( name ) )
                {
                    return languages[j];
                }
            }

            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Language
    {
        /** Language parameter ID. */
        public String id;

        /** Language name. */
        public String name;

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String toString(  )
        {
            return name;
        }
    }
}
