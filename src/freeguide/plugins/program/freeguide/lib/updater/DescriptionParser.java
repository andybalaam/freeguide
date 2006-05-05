package freeguide.plugins.program.freeguide.lib.updater;

import freeguide.plugins.program.freeguide.lib.updater.data.PluginPackage;
import freeguide.plugins.program.freeguide.lib.updater.data.PluginsRepository;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class for parse repository description XML.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class DescriptionParser extends DefaultHandler
{
    protected final PluginsRepository repository;
    protected PluginPackage currentPackage;
    protected String lang;
    protected String text;

/**
     * Creates a new Handler object.
     *
     * @param repository
     */
    public DescriptionParser( final PluginsRepository repository )
    {
        this.repository = repository;
    }

    /**
     * startElement handler
     *
     * @param uri DOCUMENT ME!
     * @param localName DOCUMENT ME!
     * @param qName DOCUMENT ME!
     * @param attributes DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void startElement( 
        String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        if( "package".equals( qName ) )
        {
            currentPackage = new PluginPackage( 
                    attributes.getValue( "id" ), repository );
            currentPackage.setType( attributes.getValue( "type" ) );
            currentPackage.setVersion( attributes.getValue( "version" ) );
            currentPackage.setRepositoryPath( 
                attributes.getValue( "repositoryPath" ) );
        }
        else if( "mirror".equals( qName ) )
        {
            parseMirror( attributes );
        }
        else if( currentPackage != null )
        {
            parseStartPackage( qName, attributes );
        }
    }

    protected void parseMirror( Attributes attributes )
    {
        repository.addMirror( 
            attributes.getValue( "location" ), attributes.getValue( "path" ) );
    }

    protected void parseStartPackage( String qName, Attributes attributes )
    {
        if( "name".equals( qName ) && ( currentPackage != null ) )
        {
            lang = attributes.getValue( "lang" );

            if( lang == null )
            {
                lang = "en";
            }
        }
        else if( "description".equals( qName ) && ( currentPackage != null ) )
        {
            lang = attributes.getValue( "lang" );

            if( lang == null )
            {
                lang = "en";
            }
        }
    }

    /**
     * endElement handler
     *
     * @param uri DOCUMENT ME!
     * @param localName DOCUMENT ME!
     * @param qName DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        if( "package".equals( qName ) )
        {
            if( currentPackage != null )
            {
                repository.addPackage( currentPackage );
                currentPackage = null;
            }
        }
        else if( currentPackage != null )
        {
            parseEndPackage( qName );
        }
    }

    protected void parseEndPackage( String qName )
    {
        if( "name".equals( qName ) && ( currentPackage != null ) )
        {
            currentPackage.setName( lang, text );
            lang = null;
        }
        else if( "description".equals( qName ) && ( currentPackage != null ) )
        {
            currentPackage.setDescription( lang, text );
            lang = null;
        }
    }

    /**
     * characters handler
     *
     * @param ch DOCUMENT ME!
     * @param start DOCUMENT ME!
     * @param length DOCUMENT ME!
     *
     * @throws SAXException DOCUMENT ME!
     */
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        if( lang != null )
        {
            text = new String( ch, start, length );
        }
    }
}
