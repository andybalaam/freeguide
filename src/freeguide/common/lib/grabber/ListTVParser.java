package freeguide.common.lib.grabber;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugins.ILogger;
import freeguide.common.plugins.IProgress;
import freeguide.common.plugins.IStoragePipe;

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
    protected String currentChannelID;
    protected TVProgramme[] currentProgs = null;
    protected long prevTime;
    protected final String channelPrefix;
    protected final IStoragePipe storage;

    /**
     * Creates a new ListTVParser object.
     *
     * @param channelPrefix DOCUMENT ME!
     * @param storage DOCUMENT ME!
     */
    public ListTVParser( 
        final String channelPrefix, final IStoragePipe storage )
    {
        this.channelPrefix = channelPrefix;
        this.storage = storage;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param urls DOCUMENT_ME!
     * @param tz DOCUMENT_ME!
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void parseZips( 
        String[] urls, TimeZone tz, IProgress progress, ILogger logger )
        throws Exception
    {

        HttpBrowser browser = new HttpBrowser(  );

        progress.setStepCount( urls.length * 2 );

        // walk by urls
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

            // walk by files in zip
            while( zip.getNextEntry(  ) != null )
            {

                // read file to memory
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

                // parse file
                parseListTV( out.toByteArray(  ), tz, logger );
            }
        }

        progress.setStepNumber( urls.length * 2 );
    }

    /**
     * Parse one ListTV text format file.
     *
     * @param data file data
     * @param tz timezone
     * @param logger logger
     *
     * @throws Exception
     */
    protected void parseListTV( byte[] data, TimeZone tz, ILogger logger )
        throws Exception
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
                finishProgrammes(  );

                continue;
            }

            if( ( currentProgs == null ) && testForDate( tz, line, logger ) )
            {

                continue;
            }

            if( currentChannelID != null )
            {

                if( LineProgrammeHelper.isProgram( line ) )
                {

                    try
                    {
                        finishProgrammes(  );
                        currentProgs =
                            LineProgrammeHelper.parse( 
                                logger, line, currentDate, prevTime );
                        prevTime = currentProgs[0].getStart(  );
                    }
                    catch( ParseException ex )
                    {
                        Application.getInstance(  ).getLogger(  ).log( 
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

        finishProgrammes(  );
        storage.finishBlock(  );
    }

    protected void finishProgrammes(  ) throws Exception
    {

        if( currentProgs != null )
        {
            storage.addProgrammes( currentChannelID, currentProgs );
            currentProgs = null;
        }
    }

    protected boolean testForDate( TimeZone tz, String line, ILogger logger )
        throws Exception
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
                finishProgrammes(  );
                currentChannelID = null;
            }
        }
        else
        {
            mDate = DATE_PATTERN2.matcher( line );

            if( mDate.matches(  ) )
            {
                finishProgrammes(  );

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
                    finishProgrammes(  );
                    currentChannelID = null;
                }
            }
        }

        if( channelName != null )
        {
            finishProgrammes(  );
            storage.finishBlock(  );

            currentChannelID = channelPrefix + channelName.replace( '/', '_' );
            storage.addChannel( 
                new TVChannel( currentChannelID, channelName ) );
            prevTime = 0;

            return true;
        }
        else
        {

            return false;
        }
    }
}
