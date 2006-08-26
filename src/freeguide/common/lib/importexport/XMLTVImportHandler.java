package freeguide.common.lib.importexport;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.LanguageHelper;
import freeguide.common.lib.general.ResourceHelper;
import freeguide.common.lib.importexport.XMLTVImport;

import freeguide.common.plugininterfaces.IStoragePipe;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;

import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler for parse XMLTV file.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
class XMLTVImportHandler extends DefaultHandler
{
    final protected IStoragePipe storage;
    final protected XMLTVImport.ProgrammesCountCallback countCallback;
    final protected XMLTVImport.Filter filter;
    final protected String channelPrefix;
    protected String currentSite;
    protected TVProgramme currentProgramme;
    protected TVChannel currentChannel;
    protected String currentChannelID;
    protected boolean isStarRating;
    protected StringBuffer charData = new StringBuffer(  );
    private Calendar ans = GregorianCalendar.getInstance(  );
    private Pattern p1 = Pattern.compile( "\\A\\d{12}\\z" );
    private Pattern p2 = Pattern.compile( "\\A\\d{14}\\z" );
    private SimpleDateFormat f1 = new SimpleDateFormat( "yyyyMMddHHmm" );
    private SimpleDateFormat f2 = new SimpleDateFormat( "yyyyMMddHHmmss" );
    private SimpleDateFormat f3 = new SimpleDateFormat( "yyyyMMddHHmmss z" );
    private SimpleDateFormat f4 = new SimpleDateFormat( "yyyyMMddHHmmss Z" );
    protected int programmesCount;

/**
     * Creates a new Handler object.
     *
     * @param storage variable for store results
     * @param filter filter
     * @param channelPrefix DOCUMENT ME!
     */
    public XMLTVImportHandler( 
        final IStoragePipe storage,
        final XMLTVImport.ProgrammesCountCallback countCallback,
        final XMLTVImport.Filter filter, final String channelPrefix )
    {
        this.storage = storage;
        this.countCallback = countCallback;
        this.filter = filter;
        this.channelPrefix = channelPrefix;
    }

    /**
     * On start document method.
     *
     * @throws SAXException
     */
    public void startDocument(  ) throws SAXException
    {
        currentSite = null;
        currentChannel = null;
        currentProgramme = null;
        isStarRating = false;
        programmesCount = 0;
    }

    /**
     * On start element method.
     *
     * @param uri
     * @param localName
     * @param qName
     * @param attributes
     *
     * @throws SAXException
     */
    public void startElement( 
        String uri, String localName, String qName, Attributes attributes )
        throws SAXException
    {
        charData.setLength( 0 );

        if( currentProgramme != null )
        {
            parseStartProgramme( qName, attributes );
        }
        else if( currentChannel != null )
        {
            parseStartChannel( qName, attributes );
        }
        else if( "tv".equals( qName ) )
        { // tv
            currentSite = attributes.getValue( "generator-info-name" );

            // Files prepared by XMLTVGui don't have generator-info-name
            // which screws everything up, so we use generator-url instead.
            if( currentSite == null )
            {
                currentSite = attributes.getValue( "generator-url" );
            }

            currentChannel = null;
            currentProgramme = null;
        }
        else if( "channel".equals( qName ) && ( currentSite != null ) )
        { // tv:channel

            final String channelID =
                channelPrefix + attributes.getValue( "id" );

            if( filter.checkChannelStart( channelID ) )
            {
                currentChannel = new TVChannel( channelID );
            }
        }
        else if( "programme".equals( qName ) )
        { // tv:programme
            currentProgramme = new TVProgramme(  );

            programmesCount++;

            if( countCallback != null )
            {
                countCallback.onProgramme( programmesCount );
            }

            try
            {
                String start = attributes.getValue( "start" );
                String stop = attributes.getValue( "stop" );

                if( start == null )
                {
                    throw new SAXException( 
                        "Time of program start not defined !" );
                }

                currentProgramme.setStart( 
                    parseDate( start ).getTimeInMillis(  ) );

                if( stop != null )
                {
                    currentProgramme.setEnd( 
                        parseDate( stop ).getTimeInMillis(  ) );
                }

                if( !filter.checkProgrammeStart( currentProgramme ) )
                {
                    currentProgramme = null;
                }
                else
                {
                    currentChannelID = channelPrefix
                        + attributes.getValue( "channel" );
                }
            }
            catch( ParseException ex )
            {
                currentProgramme = null;
                Application.getInstance(  ).getLogger(  )
                           .log( Level.FINE, "Error parse XMLTV data", ex );
            }
        }
    }

    protected void parseStartChannel( 
        final String tag, final Attributes attributes )
    {
        if( "icon".equals( tag ) )
        { // tv:channel:icon
            currentChannel.setIconURL( attributes.getValue( "src" ) );
        }
    }

    protected void parseStartProgramme( 
        final String tag, final Attributes attributes )
    {
        if( "previously-shown".equals( tag ) )
        {
            // Note: we are potentially throwing away the time
            // and channel where this was shown.
            currentProgramme.setPreviouslyShown( true );
        }
        else if( "title".equals( tag ) )
        {
            // Handled in parseEndProgramme
        }
        else if( "sub-title".equals( tag ) )
        {
            // Handled in parseEndProgramme
        }
        else if( "desc".equals( tag ) )
        {
            // Handled in parseEndProgramme
        }
        else if( "category".equals( tag ) )
        {
            // Handled in parseEndProgramme
        }
        else if( "rating".equals( tag ) )
        { // tv:programme:rating

            if( "MPAA".equalsIgnoreCase( attributes.getValue( "system" ) ) )
            {
                currentProgramme.setIsMovie( true );
            }
        }
        else if( "subtitles".equals( tag ) )
        { // tv:programme:subtitles
            currentProgramme.setSubtitled( true );
        }
        else if( "icon".equals( tag ) )
        { // tv:programme:icon
            currentProgramme.setIconURL( attributes.getValue( "src" ) );
        }
        else if( "star-rating".equals( tag ) )
        {
            isStarRating = true;
        }
        else
        {
            programmeStartExtraTag( tag, attributes );
        }
    }

    protected void parseEndChannel( final String tag )
    {
        if( "display-name".equals( tag ) )
        {
            if( 
                ( currentChannel.getDisplayName(  ) == null )
                    || "".equals( currentChannel.getDisplayName(  ) ) )
            {
                currentChannel.setDisplayName( charData.toString(  ) );
            }
        }
    }

    protected void parseEndProgramme( final String tag )
    {
        if( "title".equals( tag ) )
        {
            if( currentProgramme.getTitle(  ) == null )
            {
                currentProgramme.setTitle( charData.toString(  ) );
            }
        }
        else if( "sub-title".equals( tag ) )
        {
            if( currentProgramme.getSubTitle(  ) == null )
            {
                currentProgramme.setSubTitle( charData.toString(  ) );
            }
        }
        else if( "desc".equals( tag ) )
        {
            currentProgramme.addDesc( charData.toString(  ) );
        }
        else if( "category".equals( tag ) )
        {
            String category = charData.toString(  );
            currentProgramme.addCategory( category );

            if( 
                "Film".equalsIgnoreCase( category )
                    || "CINE".equalsIgnoreCase( category ) )
            {
                currentProgramme.setIsMovie( true );
            }
        }
        else if( "star-rating".equals( tag ) )
        {
            isStarRating = false;
        }
        else if( "value".equals( tag ) && isStarRating )
        {
            currentProgramme.setStarRating( charData.toString(  ) );
        }
        else if( "url".equals( tag ) )
        {
            try
            {
                currentProgramme.setLink( new URL( charData.toString(  ) ) );

            }
            catch( java.net.MalformedURLException ex )
            {
                Application.getInstance(  ).getLogger(  )
                           .log( 
                    Level.FINE,
                    "Invalid URL for programme : " + charData.toString(  ), ex );
            }
        }
        else
        {
            if( charData.length(  ) > 0 )
            {
                programmeEndExtraTag( tag, "", charData.toString(  ) );
            }
        }
    }

    /**
     * On end element method.
     *
     * @param uri
     * @param localName
     * @param qName
     *
     * @throws SAXException
     */
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        try
        {
            if( "tv".equals( qName ) )
            {
                currentSite = null;
                currentChannel = null;
                currentProgramme = null;
            }
            else if( "channel".equals( qName ) && ( currentChannel != null ) )
            {
                filter.performChannelEnd( currentChannel );
                storage.addChannel( currentChannel );
                currentChannel = null;
            }
            else if( 
                "programme".equals( qName ) && ( currentProgramme != null ) )
            {
                storage.addProgramme( currentChannelID, currentProgramme );
                currentProgramme = null;
                currentChannelID = null;
            }
            else if( currentProgramme != null )
            {
                parseEndProgramme( qName );
            }
            else if( currentChannel != null )
            {
                parseEndChannel( qName );
            }

            charData.setLength( 0 );
        }
        catch( Exception ex )
        {
            ex.printStackTrace(  );
        }
    }

    /**
     * On text data method.
     *
     * @param ch
     * @param start
     * @param length
     *
     * @throws SAXException
     */
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        charData.append( ch, start, length );
    }

    protected void programmeStartExtraTag( 
        final String name, final Attributes attrs )
    {
        for( int i = 0; i < attrs.getLength(  ); i++ )
        {
            currentProgramme.setExtraTag( 
                name, attrs.getQName( i ), attrs.getValue( i ) );
        }
    }

    protected void programmeEndExtraTag( 
        final String mainTag, final String subTag, final String data )
    {
        currentProgramme.setExtraTag( mainTag, subTag, data );
    }

    /**
     * Parse data string.
     *
     * @param strDate
     *
     * @return
     *
     * @throws ParseException
     */
    private Calendar parseDate( String strDate ) throws ParseException
    {
        Matcher m;

        m = p1.matcher( strDate );

        if( m.matches(  ) )
        {
            ans.setTime( f1.parse( strDate ) );

            return ans;
        }

        m = p2.matcher( strDate );

        if( m.matches(  ) )
        {
            ans.setTime( f2.parse( strDate ) );

            return ans;
        }

        try
        {
            ans.setTime( f3.parse( strDate ) );
        }
        catch( ParseException ex )
        {
            ans.setTime( f4.parse( strDate ) );
        }

        return ans;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param publicId DOCUMENT_ME!
     * @param systemId DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws SAXException DOCUMENT_ME!
     */
    public InputSource resolveEntity( String publicId, String systemId )
        throws SAXException
    {
        if( systemId.endsWith( "xmltv.dtd" ) )
        {
            try
            {
                return new InputSource( 
                    ResourceHelper.getUncachedStream( 
                        "resources/main/xmltv.dtd" ) );
            }
            catch( IOException ex )
            {
                ex.printStackTrace(  );
            }
        }

        return null;
    }
}
