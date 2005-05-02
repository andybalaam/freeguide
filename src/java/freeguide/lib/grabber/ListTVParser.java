package freeguide.lib.grabber;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ILogger;
import freeguide.plugins.IProgress;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.text.ParseException;

import java.util.TimeZone;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipInputStream;

/**
 * Helper for parse TV listing in simple ListTV text format.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class ListTVParser
{

    protected static final String CHARSET = "Cp1251";
    protected static final Pattern DATE_PATTERN1 =
        Pattern.compile( "(\\d{1,2})\\s+(\\S+)\\s+(\\d{4})\\s*\\.(.+)" );
    protected static final Pattern DATE_PATTERN2 =
        Pattern.compile( "(\\d{1,2})\\.(\\d{1,2})\\.(\\d{4})\\s*(.+)" );
    protected long currentDate = 0;
    protected TVChannel currentChannel = null;
    protected TVProgramme[] currentProgs = null;
    protected long prevTime;
    protected TVData result;
    protected final String channelPrefix;

    /**
     * Creates a new ListTVParser object.
     *
     * @param channelPrefix DOCUMENT ME!
     */
    public ListTVParser( final String channelPrefix )
    {
        this.channelPrefix = channelPrefix;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param urls DOCUMENT_ME!
     * @param tz DOCUMENT_ME!
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public TVData parseZips( 
        String[] urls, TimeZone tz, IProgress progress, ILogger logger )
        throws IOException
    {
        result = new TVData(  );

        HttpBrowser browser = new HttpBrowser(  );

        progress.setStepCount( urls.length * 2 );

        for( int i = 0; i < urls.length; i++ )
        {
            progress.setStepNumber( i * 2 );

            if( urls.length > 1 )
            {
                progress.setProgressMessage( 
                    "Load data page [" + ( i + 1 ) + "/" + urls.length + "]" );

            }

            else
            {
                progress.setProgressMessage( "Load data page" );

            }

            browser.loadURL( urls[i] );

            progress.setStepNumber( ( i * 2 ) + 1 );

            ZipInputStream zip =
                new ZipInputStream( 
                    new ByteArrayInputStream( browser.getBinaryData(  ) ) );

            while( zip.getNextEntry(  ) != null )
            {

                ByteArrayOutputStream out = new ByteArrayOutputStream(  );

                int len;

                byte[] buffer = new byte[16 * 1024];

                while( true )
                {
                    len = zip.read( buffer );

                    if( len < 0 )
                    {

                        break;

                    }

                    out.write( buffer, 0, len );

                }

                parseListTV( out.toByteArray(  ), tz, logger );

            }
        }

        progress.setStepNumber( urls.length * 2 );

        return result;

    }

    protected void parseListTV( byte[] data, TimeZone tz, ILogger logger )
        throws IOException
    {

        BufferedReader rd =
            new BufferedReader( 
                new InputStreamReader( 
                    new ByteArrayInputStream( data ), CHARSET ) );

        String line;
        prevTime = 0;

        while( ( line = rd.readLine(  ) ) != null )
        {
            line = line.trim(  );

            if( "".equals( line ) )
            {
                currentProgs = null;

                continue;

            }

            if( ( currentProgs == null ) && testForDate( tz, line, logger ) )
            {

                continue;
            }

            if( currentChannel != null )
            {

                if( LineProgrammeHelper.isProgram( line ) )
                {

                    try
                    {
                        currentProgs =
                            LineProgrammeHelper.parse( 
                                logger, line, currentDate, prevTime );
                        prevTime = currentProgs[0].getStart(  );

                        currentChannel.put( currentProgs );

                    }

                    catch( ParseException ex )
                    {
                        FreeGuide.log.log( 
                            Level.FINE, "Error parse programme line : " + line,
                            ex );
                    }
                }

                else
                {

                    if( currentProgs != null )
                    {

                        for( int i = 0; i < currentProgs.length; i++ )
                        {
                            currentProgs[i].addDesc( line );

                        }
                    }
                }
            }
        }
    }

    protected boolean testForDate( TimeZone tz, String line, ILogger logger )
    {

        Matcher mDate;

        String channelName = null;

        mDate = DATE_PATTERN1.matcher( line );

        if( mDate.matches(  ) )
        {

            try
            {
                currentDate =
                    TimeHelper.getBaseDate( 
                        tz, mDate.group( 1 ), mDate.group( 2 ),
                        mDate.group( 3 ), null );

                channelName = mDate.group( 4 ).trim(  );

            }

            catch( ParseException ex )
            {
                logger.warning( "Error parse date string: " + line );

                currentChannel = null;

                currentProgs = null;

            }
        }

        else
        {
            mDate = DATE_PATTERN2.matcher( line );

            if( mDate.matches(  ) )
            {

                try
                {
                    currentDate =
                        TimeHelper.getBaseDate( 
                            tz, mDate.group( 1 ), mDate.group( 2 ),
                            mDate.group( 3 ), null );

                    channelName = mDate.group( 4 ).trim(  );

                }

                catch( ParseException ex )
                {
                    logger.warning( "Error parse date string: " + line );

                    currentChannel = null;

                    currentProgs = null;

                }
            }
        }

        if( channelName != null )
        {
            currentChannel =
                result.get( channelPrefix + channelName.replace( '/', '_' ) );

            currentChannel.setDisplayName( channelName );

            currentProgs = null;
            prevTime = 0;

            return true;

        }

        else
        {

            return false;

        }
    }
}
