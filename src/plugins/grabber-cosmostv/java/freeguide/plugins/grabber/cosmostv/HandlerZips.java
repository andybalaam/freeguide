package freeguide.plugins.grabber.cosmostv;

import freeguide.lib.grabber.HtmlHelper;

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
public class HandlerZips extends HtmlHelper.DefaultContentHandler
{

    protected Set zips = new TreeSet(  );

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

        if( "a".equals( qName ) )
        {

            String href = atts.getValue( "href" );

            if( href != null )
            {

                if( href.trim(  ).toLowerCase(  ).endsWith( ".zip" ) )
                {
                    zips.add( href );

                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getZips(  )
    {

        return (String[])zips.toArray( new String[zips.size(  )] );

    }
}
