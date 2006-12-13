package freeguide.common.lib.grabber;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.StringHelper;

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

import java.nio.channels.ClosedByInterruptException;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
    public static final String HEADER_USER_AGENT = "User-Agent";

    /** DOCUMENT ME! */
    public static final String HEADER_COOKIE = "Cookie";

    /** DOCUMENT ME! */
    public static final String HEADER_COOKIE_SET = "Set-cookie";

    /** DOCUMENT ME! */
    public static final String HEADER_LOCATION = "Location";

    /** DOCUMENT ME! */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /** DOCUMENT ME! */
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";

    /** DOCUMENT ME! */
    public static final String METHOD_GET = "GET";

    /** DOCUMENT ME! */
    public static final String METHOD_POST = "POST";

    /** DOCUMENT ME! */
    public static final String HTTP_PREFIX = "http://";
    protected static final String EXT_HTML = ".html";
    protected static final String EXT_HTM = ".htm";
    protected static final String MIME_URLENCODED =
        "application/x-www-form-urlencoded";
    protected static final String MIME_BINARY = "application/binary";
    protected static final String MIME_HTML = "text/html";
    protected static final String PREFIX_CHARSET = "charset=";
    protected static final String COOKIES_SEPARATOR = "; ";
    protected static final String PARAMS_SEPARATOR = "&";
    protected static final String TAG_META = "meta";
    protected static final String ATTR_VALUE_CONTENT_TYPE = "Content-Type";
    protected static final String ATTR_HTTP_EQUIV = "http-equiv";
    protected static final String ATTR_CONTENT = "content";
    protected static final String DEBUG_OUT_PATH = "/tmp/HttpBrowser.error";

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
    protected Map<String, String> cookies = new TreeMap<String, String>(  );
    protected Map<String, String> headers = new TreeMap<String, String>(  );
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
        headers.put( HEADER_USER_AGENT, versionString );
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
            u = new URL( url + '?' + param );
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
                conn.setRequestMethod( METHOD_POST );
                conn.setRequestProperty( HEADER_CONTENT_TYPE, MIME_URLENCODED );
                conn.setRequestProperty( 
                    HEADER_CONTENT_LENGTH, String.valueOf( param.length(  ) ) );

                OutputStream os = conn.getOutputStream(  );
                os.write( param.getBytes(  ) );
                os.flush(  );
                os.close(  );
                os = null;
            }

            // redirect with cookies support
            conn.getResponseCode(  );
            retrieveCookies( conn );

            for( 
                int i = 0;
                    ( i < 3 )
                    && ( conn.getResponseCode(  ) == HttpURLConnection.HTTP_MOVED_TEMP );
                    i++ )
            {
                String newloc = conn.getHeaderField( HEADER_LOCATION );

                if( newloc != null )
                {
                    if( newloc.startsWith( HTTP_PREFIX ) )
                    { // other URL
                        u = new URL( newloc );
                    }
                    else if( 
                        ( newloc.length(  ) > 0 )
                            && ( newloc.charAt( 0 ) == '/' ) )
                    { // absolute path

                        int l = url.indexOf( '/', 8 );

                        if( l != -1 )
                        {
                            u = new URL( url.substring( 0, l ) + newloc );
                        }
                    }
                    else
                    { // relative path

                        int l = url.lastIndexOf( '/' );

                        if( l != -1 )
                        {
                            u = new URL( url.substring( 0, l ) + '/' + newloc );
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
                //dump(conn);
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

            if( url.endsWith( EXT_HTML ) || url.endsWith( EXT_HTM ) )
            {
                contentType = MIME_HTML;
            }
            else
            {
                contentType = MIME_BINARY;
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

            if( Thread.interrupted(  ) )
            {
                throw new ClosedByInterruptException(  );
            }

            if( len < 0 )
            {
                break;
            }

            bo.write( buffer, 0, len );
        }

        dataBytes = bo.toByteArray(  );

        if( ( contentType != null ) && contentType.startsWith( MIME_HTML ) )
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
        String separator = StringHelper.EMPTY_STRING;

        while( it.hasNext(  ) )
        {
            final String key = (String)it.next(  );
            final String value = (String)cookies.get( key );
            result.append( separator );
            result.append( key );
            result.append( '=' );
            result.append( value );
            separator = COOKIES_SEPARATOR;
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
            cookies = new TreeMap<String, String>(  );
        }

        String key;
        String value;
        int i = 1;

        while( ( key = conn.getHeaderFieldKey( i ) ) != null )
        {
            if( HEADER_COOKIE_SET.equalsIgnoreCase( key ) )
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
        String sep = StringHelper.EMPTY_STRING;
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
                    sep = PARAMS_SEPARATOR;
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
                    sep = PARAMS_SEPARATOR;
                }
            }
            else
            {
                result.append( sep );
                result.append( key );
                result.append( '=' );
                result.append( value );
                sep = PARAMS_SEPARATOR;
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
     * @param conn DOCUMENT_ME!
     */
    public void dump( HttpURLConnection conn )
    {
        try
        {
            OutputStream ou = new FileOutputStream( DEBUG_OUT_PATH );

            try
            {
                ou.write( 
                    ( Integer.toString( conn.getResponseCode(  ) ) + '\n' )
                    .getBytes(  ) );

                for( Map.Entry<String, List<String>> hf : conn.getHeaderFields(  )
                                                              .entrySet(  ) )
                {
                    for( String line : hf.getValue(  ) )
                    {
                        ou.write( 
                            ( hf.getKey(  ) + ':' + ' ' + line + '\n' )
                            .getBytes(  ) );
                    }
                }

                ou.write( Character.toString( '\n' ).getBytes(  ) );
                FileHelper.copy( conn.getInputStream(  ), ou );
                ou.write( dataBytes );
                ou.flush(  );
            }
            finally
            {
                ou.close(  );
            }
        }
        catch( Exception ex )
        {
        }
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
            int pos = contentType.toLowerCase(  ).indexOf( PREFIX_CHARSET );

            if( pos != -1 )
            {
                return contentType.substring( pos + PREFIX_CHARSET.length(  ) );
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
            if( TAG_META.equals( qName ) )
            {
                if( 
                    ATTR_VALUE_CONTENT_TYPE.equalsIgnoreCase( 
                            atts.getValue( ATTR_HTTP_EQUIV ) ) )
                {
                    contentType = atts.getValue( ATTR_CONTENT );
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
