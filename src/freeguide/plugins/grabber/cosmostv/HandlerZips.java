package freeguide.plugins.grabber.cosmostv;

import freeguide.common.lib.grabber.HtmlHelper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Set;
import java.util.TreeSet;

/**
 * Gets zip files from page.
 *
 * @author Alex Buloichik
 */
public class HandlerZips extends HtmlHelper.DefaultContentHandler
{
    protected static final String TAG_A = "a";
    protected static final String ATTR_HREF = "href";
    protected static final String FILENAME_ENDS = ".zip";
    protected Set<String> zips = new TreeSet<String>(  );

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
        if( TAG_A.equals( qName ) )
        {
            String href = atts.getValue( ATTR_HREF );

            if( href != null )
            {
                if( href.trim(  ).toLowerCase(  ).endsWith( FILENAME_ENDS ) )
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
