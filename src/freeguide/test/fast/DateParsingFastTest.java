package freeguide.test.fast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import freeguide.common.lib.importexport.XMLTVImportHandler;

import freeguide.test.FreeGuideTest;

public class DateParsingFastTest
{
    public void run() throws Exception
    {
        test_NoTimeZone();
        test_NamedTimeZone();
        test_NumericTimeZone();
    }

    private long getTimeLocal( int year, int month, int day,
        int hour, int minute, int second )
    {
        Calendar calAns = new GregorianCalendar( year, month, day,
            hour, minute, second );
        return calAns.getTimeInMillis();
    }

    private long getTimeGMT( int year, int month, int day,
        int hour, int minute, int second )
    {
        TimeZone tz0 = TimeZone.getTimeZone( "GMT+00:00" );
        Calendar calAns = new GregorianCalendar( year, month, day, hour, minute, second );
        calAns.setTimeZone( tz0 );
        return calAns.getTimeInMillis();
    }

    private void assertDateParsesTo( String dateString, long expectedAnswer )
    throws Exception
    {
        FreeGuideTest.my_assert( XMLTVImportHandler.parseDate( dateString )
            == expectedAnswer );
    }

    private void test_NoTimeZone() throws Exception
    {
        // Simply don't throw for all the formats we do support.

        long answerLocal = getTimeLocal(  2010, Calendar.JUNE, 17, 5, 16, 0 );

        assertDateParsesTo( "201006170516",   answerLocal );
        assertDateParsesTo( "20100617051600", answerLocal );
    }

    private void test_NamedTimeZone() throws Exception
    {
        long answerAbsolute = getTimeGMT( 2010, Calendar.JUNE, 17, 5, 16, 0 );

        assertDateParsesTo( "201006170716 CEST",   answerAbsolute );
        assertDateParsesTo( "20100617071600 CEST", answerAbsolute );
    }

    private void test_NumericTimeZone() throws Exception
    {
        long answerAbsolute = getTimeGMT( 2010, Calendar.JUNE, 17, 5, 16, 0 );

        assertDateParsesTo( "201006170516 +0000", answerAbsolute );
        assertDateParsesTo( "201006170616 +0100", answerAbsolute );
        assertDateParsesTo( "201006170416 -0100", answerAbsolute );

        assertDateParsesTo( "20100617051600 +0000", answerAbsolute );
        assertDateParsesTo( "20100617061600 +0100", answerAbsolute );
        assertDateParsesTo( "20100617041600 -0100", answerAbsolute );
    }

    // Not (yet?) supported:
    //private void test_GMTOffsetTimeZone() throws Exception
    //{
    //    long answerAbsolute = getTimeGMT( 2010, Calendar.JUNE, 17, 5, 16, 0 );
    //
    //    assertDateParsesTo( "201006170616 GMT+0100",   answerAbsolute );
    //    assertDateParsesTo( "201006170416 GMT-0100",   answerAbsolute );
    //    assertDateParsesTo( "20100617061600 GMT+0100", answerAbsolute );
    //    assertDateParsesTo( "20100617041600 GMT-0100", answerAbsolute );
    //}
}
