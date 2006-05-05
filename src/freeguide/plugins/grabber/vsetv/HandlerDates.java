package freeguide.plugins.grabber.vsetv;

import freeguide.common.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Set;
import java.util.TreeSet;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class HandlerDates extends HtmlHelper.DefaultContentHandler
{
    protected boolean grab;
    protected Set result = new TreeSet(  );

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
        if( "select".equals( qName ) )
        {
            if( "selectdate".equalsIgnoreCase( atts.getValue( "name" ) ) )
            {
                grab = true;

            }
        }

        else if( grab && "option".equals( qName ) )
        {
            final String value = atts.getValue( "value" );

            if( value != null )
            {
                result.add( value );

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
            grab = false;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getResult(  )
    {
        return (String[])result.toArray( new String[result.size(  )] );

    }
}
