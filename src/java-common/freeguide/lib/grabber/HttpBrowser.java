package freeguide.common.lib.grabber;

import org.ccil.cowan.tagsoup.Parser;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for implement some browser's functionality, like cookies,
 * redirection, etc.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class HttpBrowser
{

    /** DOCUMENT ME! */
    public static final String HEADER_PROXY_AUTH = "Proxy-Authorization";

    /** DOCUMENT ME! */
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";

    /** DOCUMENT ME! */
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";

    /** DOCUMENT ME! */
    public static final String HEADER_REFERER = "Referer";

    /** DOCUMENT ME! */
    public static final String HEADER_COOKIE = "Cookie";

    /*static

    {


    ProxySelector ps =

    new ProxySelector(  )

    {

    public List select( URI uri )

    {


    List result = new ArrayList(  );

    result.add(

        new Proxy(

            Proxy.Type.HTTP,

            new InetSocketAddress( "localhost", 3128 ) ) );


    return result;

    }


    public void connectFailed(

    URI uri, SocketAddress sa, IOException ioe )

    {

    ioe.printStackTrace(  );

    }

    };


    ProxySelector.setDefault( ps );

    }   */
    protected Map cookies = new TreeMap(  );
    protected Map headers = new TreeMap(  );
    protected String data;

    /** DOCUMENT ME! */
    public byte[] dataBytes;

    /**
     * Creates a new HttpBrowser object.
     */
    public HttpBrowser(  )
    {
    }

    /**
     * Creates a new HttpBrowser object.
     *
     * @param versionString DOCUMENT ME!
     */
    public HttpBrowser( final String versionString )
    {
        headers.put( "User-Agent", versionString );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getData(  )
    {

        return data;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public byte[] getBinaryData(  )
    {

        return dataBytes;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param url DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void loadURL( final String url ) throws IOException
    {
        loadURL( url, (String)null, false );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param url DOCUMENT_ME!
     * @param params DOCUMENT_ME!
     * @param postMethod DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void loadURL( 
        final String url, final Map params, final boolean postMethod )
        throws IOException
    {
        loadURL( url, mapToParam( params ), postMethod );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param url DOCUMENT_ME!
     * @param param DOCUMENT_ME!
     * @param postMethod DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void loadURL( 
        final String url, final String param, final boolean postMethod )
        throws IOException
    {
        HttpURLConnection.setFollowRedirects( false );

        URL u;

        if( postMethod || ( param == null ) )
        {
            u = new URL( url );
        }
        else
        {
            u = new URL( url + "?" + param );
        }

        URLConnection connAbstract = u.openConnection(  );

        if( connAbstract instanceof HttpURLConnection )
        {

            HttpURLConnection conn = (HttpURLConnection)connAbstract;
            setHeader( HEADER_COOKIE, makeCookies(  ) );
            applyHeaders( conn );
            conn.setUseCaches( false );

            if( postMethod )
            {
                conn.setDoOutput( true );
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( 
                    "Content-Type", "application/x-www-form-urlencoded" );
                conn.setRequestProperty( 
                    "Content-Length", String.valueOf( param.length(  ) ) );

                OutputStream os = conn.getOutputStream(  );
                os.write( param.getBytes(  ) );
                os.flush(  );
                os.close(  );
                os = null;
            }

            // redirect with cookies support
            conn.getResponseCode(  );
            retrieveCookies( conn );

            if( conn.getResponseCode(  ) == HttpURLConnection.HTTP_MOVED_TEMP )
            {

                String newloc = conn.getHeaderField( "Location" );

                if( newloc != null )
                {

                    if( newloc.startsWith( "http://" ) )
                    { // other URL
                        u = new URL( newloc );
                    }
                    else if( newloc.startsWith( "/" ) )
                    { // absolute path

                        int l = url.indexOf( '/', 8 );

                        if( l != -1 )
                        {
                            u = new URL( url.substring( 0, l ) + newloc );
                        }
                    }
                    else
                    { // relative path

                        int l = url.lastIndexOf( "/" );

                        if( l != -1 )
                        {
                            u = new URL( url.substring( 0, l ) + "/" + newloc );
                        }
                    }
                }

                conn = (HttpURLConnection)u.openConnection(  );
                setHeader( HEADER_COOKIE, makeCookies(  ) );
                applyHeaders( conn );
                conn.setUseCaches( false );

                conn.getResponseCode(  );
                retrieveCookies( conn );
            }

            if( conn.getResponseCode(  ) != HttpURLConnection.HTTP_OK )
            {
                throw new IOException( 
                    "Error loadURL : code=" + conn.getResponseCode(  ) + "/"
                    + conn.getResponseMessage(  ) );
            }

            loadFromStream( conn.getInputStream(  ), conn.getContentType(  ) );
            conn.disconnect(  );
        }
        else
        {

            String contentType;

            if( url.endsWith( ".html" ) || url.endsWith( ".htm" ) )
            {
                contentType = "text/html";
            }
            else
            {
                contentType = "application/binary";
            }

            loadFromStream( connAbstract.getInputStream(  ), contentType );
        }
    }

    protected void loadFromStream( 
        final InputStream input, final String contentType )
        throws IOException
    {

        ByteArrayOutputStream bo = new ByteArrayOutputStream(  );
        byte[] buffer = new byte[1024];
        int len;

        while( true )
        {
            len = input.read( buffer );

            if( len < 0 )
            {

                break;
            }

            bo.write( buffer, 0, len );
        }

        dataBytes = bo.toByteArray(  );

        if( ( contentType != null ) && contentType.startsWith( "text/html" ) )
        {

            Parser p = new Parser(  );
            HandlerCharset handler = new HandlerCharset(  );
            p.setContentHandler( handler );

            try
            {
                p.parse( 
                    new InputSource( new ByteArrayInputStream( dataBytes ) ) );
            }
            catch( SAXException ex )
            {
                throw new IOException( 
                    "Error get html encoding: " + ex.getMessage(  ) );
            }

            String charsetName =
                getCharsetByContentType( handler.getContentType(  ) );

            if( charsetName == null )
            {
                charsetName = getCharsetByContentType( contentType );
            }

            if( charsetName != null )
            {
                data = new String( dataBytes, charsetName );
            }
            else
            {
                data = new String( dataBytes );
            }
        }

        input.close(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getCookie( String key )
    {

        return (String)cookies.get( key );
    }

    protected String makeCookies(  )
    {

        if( cookies.size(  ) == 0 )
        {

            return null;
        }

        StringBuffer result = new StringBuffer(  );
        Iterator it = cookies.keySet(  ).iterator(  );
        String separator = "";

        while( it.hasNext(  ) )
        {

            final String key = (String)it.next(  );
            final String value = (String)cookies.get( key );
            result.append( separator );
            result.append( key );
            result.append( '=' );
            result.append( value );
            separator = "; ";
        }

        return result.toString(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param key DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     */
    public void setHeader( String key, String value )
    {

        if( value != null )
        {
            headers.put( key, value );
        }
        else
        {
            headers.remove( key );
        }
    }

    protected void applyHeaders( HttpURLConnection conn )
    {

        Iterator it = headers.keySet(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            final String key = (String)it.next(  );
            final String value = (String)headers.get( key );
            conn.setRequestProperty( key, value );
        }
    }

    protected void retrieveCookies( HttpURLConnection conn )
    {

        if( cookies == null )
        {
            cookies = new TreeMap(  );
        }

        String key;
        String value;
        int i = 1;

        while( ( key = conn.getHeaderFieldKey( i ) ) != null )
        {

            if( "Set-cookie".equalsIgnoreCase( key ) )
            {
                value = conn.getHeaderField( i );

                if( value != null )
                {

                    int r = value.indexOf( ';' );

                    if( r != -1 )
                    {
                        value = value.substring( 0, r );
                    }

                    r = value.indexOf( '=' );

                    if( r != -1 )
                    {
                        cookies.put( 
                            value.substring( 0, r ), value.substring( r + 1 ) );
                    }
                }
            }

            i++;
        }
    }

    protected String mapToParam( final Map map )
    {

        StringBuffer result = new StringBuffer(  );
        String sep = "";
        Iterator it = map.keySet(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            String key = (String)it.next(  );
            Object value = map.get( key );

            if( value instanceof String[] )
            {

                String[] valueCollection = (String[])value;

                for( int i = 0; i < valueCollection.length; i++ )
                {
                    result.append( sep );
                    result.append( key );
                    result.append( '=' );
                    result.append( valueCollection[i] );
                    sep = "&";
                }
            }
            else if( value instanceof Collection )
            {

                Collection valueCollection = (Collection)value;
                Iterator itc = valueCollection.iterator(  );

                while( itc.hasNext(  ) )
                {
                    result.append( sep );
                    result.append( key );
                    result.append( '=' );
                    result.append( itc.next(  ) );
                    sep = "&";
                }
            }
            else
            {
                result.append( sep );
                result.append( key );
                result.append( '=' );
                result.append( value );
                sep = "&";
            }
        }

        return result.toString(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param handler DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     * @throws SAXException DOCUMENT_ME!
     */
    public void parse( final ContentHandler handler )
        throws IOException, SAXException
    {

        Parser p = new Parser(  );
        p.setContentHandler( handler );
        p.parse( new InputSource( new StringReader( data ) ) );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param fileName DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public void saveAs( String fileName ) throws IOException
    {

        OutputStream ou = new FileOutputStream( fileName );
        ou.write( dataBytes );
        ou.flush(  );
        ou.close(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param contentType DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static String getCharsetByContentType( String contentType )
    {

        if( contentType != null )
        {

            int pos = contentType.toLowerCase(  ).indexOf( "charset=" );

            if( pos != -1 )
            {

                return contentType.substring( pos + "charset=".length(  ) );
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
    public static class HandlerCharset extends HtmlHelper.DefaultContentHandler
    {

        protected String contentType = null;

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

            if( "meta".equals( qName ) )
            {

                if( 
                    "Content-Type".equalsIgnoreCase( 
                            atts.getValue( "http-equiv" ) ) )
                {
                    contentType = atts.getValue( "content" );
                }
            }
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String getContentType(  )
        {

            return contentType;
        }
    }
}
