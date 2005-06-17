package freeguide.plugins.grabber.newsvm;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.lib.general.LanguageHelper;

import freeguide.lib.grabber.HttpBrowser;
import freeguide.lib.grabber.LineProgrammeHelper;
import freeguide.lib.grabber.TimeHelper;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.text.ParseException;

import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class GrabberNewsvm extends BaseModule implements IModuleGrabber
{

    protected static Pattern reDate =
        Pattern.compile( 
            "<b>(\\p{L}+)\\s*,\\s*(\\d{1,2})\\s+(\\p{L}+)</b>",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected static Pattern reChannel =
        Pattern.compile( 
            "<br><b>\"?(.+?)\"?</b>",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected static Pattern reProgram =
        Pattern.compile( 
            "<br>(\\d{1,2}\\.\\d{2}[\\ |,]\\s*.+)",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE );
    protected static TimeZone tz = TimeZone.getTimeZone( "Europe/Minsk" );
    protected static final String[] DAYS =
    { "mo", "tu", "we", "th", "fr", "sa", "su" };
    boolean isStopped;

    /**
     * DOCUMENT_ME!
     *
     * @param progress DOCUMENT_ME!
     * @param logger DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( IProgress progress, ILogger logger )
        throws Exception
    {
        progress.setProgressValue( 0 );

        TVData result = new TVData(  );

        HttpBrowser browser = new HttpBrowser(  );

        browser.setHeader( HttpBrowser.HEADER_ACCEPT_LANGUAGE, "ru" );

        browser.setHeader( HttpBrowser.HEADER_ACCEPT_CHARSET, "windows-1251" );

        isStopped = false;

        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        for( int i = 0; ( i < DAYS.length ) && !isStopped; i++ )
        {
            progress.setProgressValue( ( i * 100 ) / 7 );

            //            progress.setProgressMessage(  "Load page [" + ( i + 1 ) + "/" + DAYS.length + "]" );
            browser.loadURL( "http://newsvm.com/tv/" + DAYS[i] + ".shtml" );

            parse( logger, browser.getData(  ), result );

        }

        progress.setProgressValue( 100 );
        patch( result );

        return result;

    }

    protected void parse( ILogger logger, final String data, TVData result )
        throws ParseException, IOException
    {

        BufferedReader rd = new BufferedReader( new StringReader( data ) );

        long basedate = 0;
        long prevTime = 0;

        TVChannel currentChannel = null;

        String line;

        while( ( line = rd.readLine(  ) ) != null )
        {
            line = line.trim(  );

            if( basedate == 0 )
            {

                Matcher mDate = reDate.matcher( line );

                if( mDate.matches(  ) )
                {
                    basedate =
                        TimeHelper.getBaseDate( 
                            tz, mDate.group( 2 ), mDate.group( 3 ), null,
                            mDate.group( 1 ) );

                }
            }

            else
            {

                Matcher mChannel = reChannel.matcher( line );

                if( mChannel.matches(  ) )
                {

                    String channelName = mChannel.group( 1 );

                    if( 
                        channelName.toLowerCase(  ).indexOf( 
                                "перепечатка" ) != -1 )
                    {
                        basedate = 0;

                    }

                    else
                    {
                        currentChannel =
                            result.get( 
                                "newsvm/" + channelName.replace( '/', '_' ) );

                        currentChannel.setDisplayName( channelName );

                    }

                    prevTime = 0;
                }

                else
                {

                    Matcher mProg = reProgram.matcher( line );

                    if( mProg.matches(  ) )
                    {

                        TVProgramme[] programmes =
                            LineProgrammeHelper.parse( 
                                logger, mProg.group( 1 ).trim(  ), basedate,
                                prevTime );
                        prevTime = programmes[0].getStart(  );
                        currentChannel.put( programmes );

                    }
                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
        isStopped = true;

    }

    protected void patch( final TVData data ) throws IOException
    {

        final String[] nen =
            LanguageHelper.loadStrings( 
                getClass(  ).getClassLoader(  ).getResourceAsStream( 
                    getClass(  ).getPackage(  ).getName(  ).replace( '.', '/' )
                    + "/nen.utf8.list" ) );
        data.iterateProgrammes( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {

                    if( !nen[0].equals( channel.getDisplayName(  ) ) )
                    {
                        stopIterateChanel(  );
                    }
                }

                protected void onProgramme( TVProgramme programme )
                {

                    if( programme.getTitle(  ) == null )
                    {

                        return;
                    }

                    for( int i = 2; i < nen.length; i++ )
                    {

                        if( programme.getTitle(  ).indexOf( nen[i] ) != -1 )
                        {
                            programme.setTitle( nen[1] );

                            break;
                        }
                    }
                }
            } );
    }
}
