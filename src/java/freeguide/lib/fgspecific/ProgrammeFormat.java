/*

 *  FreeGuide J2

 *

 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors

 *

 *  freeguide-tv.sourceforge.net

 *

 *  Released under the GNU General Public License

 *  with ABSOLUTELY NO WARRANTY.

 *

 *  See the file COPYING for more information.

 */
package freeguide.lib.fgspecific;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ui.horizontal.HTMLGuideListener;

import java.io.File;

import java.net.MalformedURLException;

import java.text.DateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Formats Programme information.
 */
public class ProgrammeFormat
{

    /** Plain text format */
    public final static int TEXT_FORMAT = 0;

    /**
     * HTML format including the "<code>&lt;html&gt;&lt;body&gt; ...
     * &lt;/body&gt;&lt;/html&gt;</code>" tags
     */
    public final static int HTML_FORMAT = 1;

    /**
     * HTML format without the <code>&lt;html&gt;&lt;body&gt; ...
     * &lt;/body&gt;&lt;/html&gt;</code> tags (for use in building a page
     * from many fragments)
     */
    public final static int HTML_FRAGMENT_FORMAT = 2;

    /** DOCUMENT ME! */
    public final static String LINE_FEED =
        System.getProperty( "line.separator", "\r\n" );
    private final static int MARGIN = 78;
    private String newline = LINE_FEED;
    private final Pattern defaultWrapPattern =
        Pattern.compile( "(.{1," + MARGIN + "})(?:\\s|$)" );
    private int outputFormat = TEXT_FORMAT;
    private boolean printTimeDelta = false;
    private DateFormat dateFormat = null;
    private boolean wrap = false;
    private boolean onScreen = true;

    /**
     * Creates an object to format Programme information.  If the
     * <code>dateFormat</code> argument is null, no date information will be
     * output.
     *
     * @param outputFormat desired output format
     * @param dateFormat DateFormat for formatting start and end times
     * @param printDelta append the delta from now in the summary
     */
    public ProgrammeFormat( 
        int outputFormat, DateFormat dateFormat, boolean printDelta )
    {
        setFormat( outputFormat );

        setDateFormat( dateFormat );

        setPrintTimeDelta( printDelta );

    }

    /**
     * Creates an object to format Programme information without date
     * information.
     *
     * @param outputFormat desired output format
     */
    public ProgrammeFormat( int outputFormat )
    {
        this( outputFormat, null, false );

    }

    /**
     * Creates a new ProgrammeFormat object.
     */
    public ProgrammeFormat(  )
    {
    }

    /**
     * Appends a formatted "short" representation of the Programme to the
     * supplied StringBuffer.  The following information is returned as
     * shown:
     * <pre>
     * 
     *  startTime title: subtitle (starString) (R)
     * 
     *  </pre>
     * where starString is only shown if the programme is a movie and (R) is
     * only displayed if the programme has been previously shown.
     *
     * @param programme the programme to format
     * @param toAppendTo where the new programme text should be appended
     *
     * @return a formatted representation of the Programme appended to the
     *         StringBuffer
     */
    public StringBuffer formatForMainGuide( 
        TVProgramme programme, StringBuffer toAppendTo )
    {

        long programmeStart = programme.getStart(  );

        String programmeTitle = programme.getTitle(  );

        String programmeSubTitle = programme.getSubTitle(  );

        String programmeStarString = programme.getStarString(  );

        if( dateFormat != null )
        {
            toAppendTo.append( 
                dateFormat.format( new Date( programmeStart ) ) ).append( " " );

        }

        toAppendTo.append( programmeTitle );

        if( programmeSubTitle != null )
        {
            toAppendTo.append( ": " ).append( programmeSubTitle );

        }

        if( programme.getIsMovie(  ) && ( programmeStarString != null ) )
        {
            toAppendTo.append( " " ).append( programmeStarString );

        }

        if( programme.getPreviouslyShown(  ) )
        {
            toAppendTo.append( " " );

            toAppendTo.append( FreeGuide.msg.getString( "r" ) );

        }

        return toAppendTo;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String formatForMainGuide( TVProgramme programme )
    {

        return formatForMainGuide( programme, new StringBuffer( 75 ) )
                   .toString(  );

    }

    /**
     * Returns a String representation of the Programme. Appends a formatted
     * "long" representation of the Programme to the supplied StringBuffer.
     * The following information is returned as shown:
     * <pre>
     * 
     *  startTime - title: subtitle
     * 
     *  channelName, ends endTime
     * 
     *  longDesc (Repeat) starString
     * 
     *  </pre>
     * where starString is only shown if the programme is a movie and (Repeat)
     * is only displayed if the programme has been previously shown.
     *
     * @param programme the programme to format
     * @param toAppendTo where the new programme text should be appended
     *
     * @return a formatted representation of the Programme appended to the
     *         StringBuffer
     */
    public StringBuffer formatLong( 
        TVProgramme programme, StringBuffer toAppendTo )
    {

        if( HTML_FRAGMENT_FORMAT != outputFormat )
        {
            toAppendTo.append( "<html><body>" ).append( LINE_FEED );

        }

        do_formatLong( programme, toAppendTo );

        if( HTML_FRAGMENT_FORMAT != outputFormat )
        {
            toAppendTo.append( "</body></html>" ).append( LINE_FEED );

        }

        return toAppendTo;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String formatLong( TVProgramme programme )
    {

        return formatLong( programme, new StringBuffer( 200 ) ).toString(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param buff DOCUMENT_ME!
     */
    public static void appendStyleSheet( StringBuffer buff )
    {
        buff.append( "<html>" ).append( LINE_FEED );

        buff.append( "<head>" ).append( LINE_FEED );

        buff.append( "  <style type='text/css'>" ).append( LINE_FEED );

        buff.append( "    body {" ).append( LINE_FEED );

        buff.append( "        font-family: helvetica, helv, arial;" ).append( 
            LINE_FEED );

        buff.append( "        font-size: small;" ).append( LINE_FEED );

        buff.append( "    }" ).append( LINE_FEED );

        buff.append( "  </style>" ).append( LINE_FEED );

        buff.append( "</head>" ).append( LINE_FEED );

        buff.append( "<body>" ).append( LINE_FEED );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String formatForProgrammeDetailsJPanel( TVProgramme programme )
    {

        StringBuffer buff = new StringBuffer(  );

        if( HTML_FRAGMENT_FORMAT != outputFormat )
        {
            appendStyleSheet( buff );

        }

        buff.append( 
            "<table width='100%' height='100%' cellpadding='0' cellspacing='0' border='0'><tr height='100%'><td height='100%' colspan='2'>" );

        do_formatLong( programme, buff );

        Hashtable extraTags = (Hashtable)programme.getExtraTags(  );

        if( extraTags != null )
        {
            buff.append( 
                "<br><table cellpadding='1' cellspacing='0' border='0'>" )
                .append( LINE_FEED );

            for( 
                Iterator it = extraTags.entrySet(  ).iterator(  );
                    it.hasNext(  ); )
            {

                Map.Entry entry = (Map.Entry)it.next(  );

                Hashtable hashOfAttrs = (Hashtable)entry.getValue(  );

                String key = (String)hashOfAttrs.get( "" );

                buff.append( "    <tr><td><b>" )
                    .append( (String)entry.getKey(  ) ).append( 
                    "</b></td><td>" );

                if( key != null )
                {
                    buff.append( key );

                }

                buff.append( "</td></tr>" );

                for( 
                    Iterator it2 = hashOfAttrs.entrySet(  ).iterator(  );
                        it2.hasNext(  ); )
                {

                    Map.Entry entry2 = (Map.Entry)it2.next(  );

                    if( !entry2.getKey(  ).equals( "" ) )
                    {
                        buff.append( "    <tr><td></td><td>" )
                            .append( entry2.getKey(  ) ).append( ": " )
                            .append( entry2.getValue(  ) ).append( 
                            "</td></tr>" );

                    }
                }
            }

            buff.append( "</table>" ).append( LINE_FEED );

        }

        buff.append( "</td></tr><tr><td align=\"left\" width='100%'>" );

        String programmeIconUrl = null;

        String channelIconURL = null;

        programmeIconUrl = programme.getIconURL(  );

        if( programmeIconUrl != null )
        {
            buff.append( "<img src=\"" ).append( programmeIconUrl )
                .append( "\" alt=\"" ).append( programme.getTitle(  ) ).append( 
                "\"/>" );

        }

        buff.append( "</td><td>" );

        if( programme.getChannel(  ).getIconFileName(  ) != null )
        {

            try
            {
                channelIconURL =
                    new File( programme.getChannel(  ).getIconFileName(  ) ).toURL(  )
                                                                            .toString(  );

            }
            catch( MalformedURLException ex )
            {
                FreeGuide.log.log( 
                    Level.WARNING,
                    "Invalid channel icon URL for channel "
                    + programme.getChannel(  ).getID(  ), ex );
            }
        }

        if( channelIconURL != null )
        {
            buff.append( "<img src=\"" ).append( channelIconURL )
                .append( "\" alt=\"" )
                .append( programme.getChannel(  ).getDisplayName(  ) ).append( 
                "\">" );

        }

        buff.append( "</td></tr></table>" );

        if( HTML_FRAGMENT_FORMAT != outputFormat )
        {
            buff.append( "</body></html>" ).append( LINE_FEED );

        }

        return buff.toString(  );

    }

    // -----------------------------------------------------------------------
    private StringBuffer do_formatLong( 
        TVProgramme programme, StringBuffer toAppendTo )
    {
        toAppendTo.append( "<p><b>" ).append( LINE_FEED );

        long programmeStart = programme.getStart(  );

        String programmeDescription = programme.getDescription(  );

        if( ( programmeDescription != null ) && ( wrap ) )
        {
            programmeDescription =
                wrap( programmeDescription, MARGIN ).toString(  );

        }

        String programmeTitle = programme.getTitle(  );

        String programmeSubTitle = programme.getSubTitle(  );

        String programmeStarString = programme.getStarString(  );

        String programmeCategory = programme.getCategory(  );

        if( dateFormat != null )
        {
            toAppendTo.append( 
                dateFormat.format( new Date( programme.getStart(  ) ) ) );

            toAppendTo.append( " - " );

        }

        if( onScreen )
        {

            String ref = HTMLGuideListener.createLinkReference( programme );

            toAppendTo.append( 
                "<a href=\"#" + ref + "\" name=\"" + ref + "\">" );

        }

        toAppendTo.append( programme.getTitle(  ) );

        if( programmeSubTitle != null )
        {
            toAppendTo.append( ": " + programmeSubTitle );

        }

        if( onScreen )
        {
            toAppendTo.append( "</a>" );

        }

        toAppendTo.append( "</b><br>" );

        toAppendTo.append( programme.getChannel(  ).getDisplayName(  ) );

        if( dateFormat != null )
        {
            toAppendTo.append( "," );

        }

        if( dateFormat != null )
        {
            toAppendTo.append( " " );

            Object[] messageArguments =
            { dateFormat.format( new Date( programme.getEnd(  ) ) ) };

            toAppendTo.append( 
                FreeGuide.msg.getLocalizedMessage( 
                    "ends_template", messageArguments ) );

        }

        if( programmeCategory != null )
        {
            toAppendTo.append( " " ).append( programmeCategory );

        }

        if( printTimeDelta )
        {
            toAppendTo.append( " <i>(" );

            calcTimeDelta( programme.getStart(  ), toAppendTo );

            toAppendTo.append( ")</i>" );

        }

        toAppendTo.append( "<br>" ).append( LINE_FEED );

        if( programmeDescription != null )
        {
            toAppendTo.append( programmeDescription );

        }

        if( programme.getPreviouslyShown(  ) )
        {
            toAppendTo.append( " " );

            toAppendTo.append( FreeGuide.msg.getString( "repeat" ) );

        }

        if( 
            programme.getIsMovie(  ) && ( programme.getStarRating(  ) != null ) )
        {
            toAppendTo.append( " " );

            toAppendTo.append( FreeGuide.msg.getString( "rating" ) );

            toAppendTo.append( ": " );

            toAppendTo.append( programme.getStarRating(  ) );

        }

        toAppendTo.append( "<br>" );

        toAppendTo.append( "</p>" ).append( LINE_FEED );

        return toAppendTo;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param outputFormat DOCUMENT_ME!
     */
    public void setFormat( int outputFormat )
    {
        this.outputFormat = outputFormat;

        if( HTML_FORMAT == outputFormat )
        {
            newline = "<br>" + LINE_FEED;

        }

        else if( TEXT_FORMAT == outputFormat )
        {
            newline = LINE_FEED;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param dateFormat DOCUMENT_ME!
     */
    public void setDateFormat( DateFormat dateFormat )
    {
        this.dateFormat = dateFormat;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param onScreen DOCUMENT_ME!
     */
    public void setOnScreen( boolean onScreen )
    {
        this.onScreen = onScreen;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param wrap DOCUMENT_ME!
     */
    public void setWrap( boolean wrap )
    {
        this.wrap = wrap;

    }

    private StringBuffer wrap( CharSequence input, int preferredMargin )
    {

        int newlineLength = newline.length(  );

        Pattern wrapPattern = defaultWrapPattern;

        if( preferredMargin != MARGIN )
        {
            wrapPattern = Pattern.compile( "(.{1," + MARGIN + "})(?:\\s|$)" );

        }

        Matcher m = wrapPattern.matcher( input );

        StringBuffer value = new StringBuffer( 300 );

        while( m.find(  ) )
        {
            m.appendReplacement( value, "$0" + newline );

        }

        // Shouldn't ever happen, but...
        m.appendTail( value );

        // Strip the trailing newline
        int length = value.length(  );

        int possibleStart = length - newlineLength;

        if( value.lastIndexOf( newline ) == possibleStart )
        {
            value.delete( possibleStart, length );

        }

        return value;

    }

    /**
     * DOCUMENT ME!
     *
     * @param printTimeDelta Sets wether to print the time delta from now.
     */
    public void setPrintTimeDelta( boolean printTimeDelta )
    {
        this.printTimeDelta = printTimeDelta;

    }

    /**
     * Function that returns the time difference from now in a format like "2
     * hours and 1 minute"
     *
     * @param startTime starting time of the program
     * @param toAppend StringBuffer the resulting string gets added to
     */
    private void calcTimeDelta( long startTime, StringBuffer toAppend )
    {

        // Get the current time and calculates the difference in minutes
        // from the starting time.  >0 means in the future
        long delta = startTime - System.currentTimeMillis(  );

        delta /= 60000;

        // If delta = 0 then it starts now and we leave as there's
        // nothing else to do
        if( delta == 0 )
        {
            toAppend.append( FreeGuide.msg.getString( "starts_now" ) );

            return;

        }

        // Split delta in meaningful fields
        int days = (int)( delta / ( 24 * 60 ) );

        int hours = (int)( ( delta / 60 ) % 60 );

        int minutes = (int)( delta % 60 );

        if( delta > 0 )
        {

            if( days == 1 )
            {
                toAppend.append( FreeGuide.msg.getString( "starts_in_1_day" ) );

            }

            else if( days > 1 )
            {

                Object[] messageArguments = { new Integer( days ) };

                toAppend.append( 
                    FreeGuide.msg.getLocalizedMessage( 
                        "starts_in_days_template", messageArguments ) );

            }

            else if( hours == 1 )
            {
                toAppend.append( 
                    FreeGuide.msg.getString( "starts_in_1_hour" ) );

            }

            else if( hours > 1 )
            {

                Object[] messageArguments = { new Integer( hours ) };

                toAppend.append( 
                    FreeGuide.msg.getLocalizedMessage( 
                        "starts_in_hours_template", messageArguments ) );

            }

            else if( minutes == 1 )
            {
                toAppend.append( 
                    FreeGuide.msg.getString( "starts_in_1_minute" ) );

            }

            else
            {

                Object[] messageArguments = { new Integer( minutes ) };

                toAppend.append( 
                    FreeGuide.msg.getLocalizedMessage( 
                        "starts_in_minutes_template", messageArguments ) );

            }
        }

        else
        {

            if( days == -1 )
            {
                toAppend.append( 
                    FreeGuide.msg.getString( "started_1_day_ago" ) );

            }

            else if( days < -1 )
            {

                Object[] messageArguments = { new Integer( -days ) };

                toAppend.append( 
                    FreeGuide.msg.getLocalizedMessage( 
                        "started_days_ago_template", messageArguments ) );

            }

            else if( hours == -1 )
            {
                toAppend.append( 
                    FreeGuide.msg.getString( "started_1_hour_ago" ) );

            }

            else if( hours < -1 )
            {

                Object[] messageArguments = { new Integer( -hours ) };

                toAppend.append( 
                    FreeGuide.msg.getLocalizedMessage( 
                        "started_hours_ago_template", messageArguments ) );

            }

            else if( minutes == -1 )
            {
                toAppend.append( 
                    FreeGuide.msg.getString( "started_1_minute_ago" ) );

            }

            else
            {

                Object[] messageArguments = { new Integer( -minutes ) };

                toAppend.append( 
                    FreeGuide.msg.getLocalizedMessage( 
                        "started_minutes_ago_template", messageArguments ) );

            }
        }
    }
}
