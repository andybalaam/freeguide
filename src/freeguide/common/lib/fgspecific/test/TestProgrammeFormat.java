package freeguide.common.lib.fgspecific.test;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JProgressBar;

import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.ProgrammeFormat;
import freeguide.common.plugininterfaces.IExecutionController;
import freeguide.common.plugininterfaces.IModuleReminder;
import freeguide.common.plugininterfaces.IModuleViewer;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.FGLogger;

public class TestProgrammeFormat
{
    public static void main(String[] args)
    {
        new TestProgrammeFormat().runTests();
    }

    public void runTests()
    {
        testTimeDelta_Now();
        testTimeDelta_OneMinute();
        testTimeDelta_AFewMinutes();
        testTimeDelta_JustUnderOneHour();
        testTimeDelta_JustOverOneHour();
        testTimeDelta_JustUnder2Hours();
        testTimeDelta_JustOver2Hours();
        testTimeDelta_JustUnderADay();
        testTimeDelta_JustOverOneDay();
        testTimeDelta_AFewDays();
    }

    private class FakeApp implements IApplication
    {
        public JFrame getApplicationFrame(  ) { return null; }
        public JFrame getCurrentFrame(  ) { return null; }
        public IModuleStorage getDataStorage(  ) { return null; }
        public IModuleViewer getViewer(  ) { return null; }
        public List getChannelsSetsList(  ) { return null; }
        public void doEditChannelsSets(  ) {}
        public void doStartGrabbers(  ) {}
        public void doPrint(  ) {}
        public Logger getLogger(  ) { return null; }
        public FGLogger getFGLogger(  ) { return null; }
        public TimeZone getTimeZone(  ) { return null; }
        public String getWorkingDirectory(  ) { return null; }
        public String getInstallDirectory(  ) { return null; }
        public boolean isUnix(  ) { return true; }
        public IModuleReminder[] getReminders(  ) { return null; }
        public void saveAllConfigs(  ) {}
        public String getBrowserCommand(  ) { return null; }
        public IMainMenu getMainMenu(  ) { return null; }
        public Locale[] getSupportedLocales(  ) { return null; }
        public IExecutionController getExecutionController() { return null; }
        public JProgressBar getApplicationProgressBar() { return null; }
        public JButton getApplicationForegroundButton() { return null; }

        public String getLocalizedMessage( final String key )
        {
            return key;
        }

        private String joinStrings( final Object[] list )
        {
            StringBuilder ret = new StringBuilder();
            for ( Object it : list )
            {
                ret.append( it.toString() );
            }
            return ret.toString();
        }

        public String getLocalizedMessage(
            final String key, final Object[] params )
        {
            return key + joinStrings( params );
        }
    }


    public void testTimeDelta_Now()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime( 8, 32 );
        long start = getLongTime( 8, 32 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_now" );
    }

    public void testTimeDelta_OneMinute()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime( 8, 32 );
        long start = getLongTime( 8, 33 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_1_minute" );
    }

    public void testTimeDelta_AFewMinutes()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime( 8, 32 );
        long start = getLongTime( 8, 40 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_minutes_template8" );
    }

    public void testTimeDelta_JustUnderOneHour()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime( 8, 32 );
        long start = getLongTime( 9, 28 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_1_hour" );
    }

    public void testTimeDelta_JustOverOneHour()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime( 8, 32 );
        long start = getLongTime( 9, 34 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_1_hour" );
    }

    public void testTimeDelta_JustUnder2Hours()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime(  8, 32 );
        long start = getLongTime( 10, 28 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_hours_template2" );
    }

    public void testTimeDelta_JustOver2Hours()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime(  8, 32 );
        long start = getLongTime( 10, 34 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_hours_template2" );
    }

    public void testTimeDelta_JustUnderADay()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now   = getLongTime(  0, 32 );
        long start = getLongTime( 22, 34 );

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_1_day" );
    }

    public void testTimeDelta_JustOverOneDay()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now = new GregorianCalendar(
            2008, 6, 6, 4, 13 ).getTimeInMillis();

        long start = new GregorianCalendar(
            2008, 6, 7, 4, 15 ).getTimeInMillis();

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_1_day" );
    }

    public void testTimeDelta_AFewDays()
    {
        IApplication app = new FakeApp();
        Application.setInstance( app );

        long now = new GregorianCalendar(
            2008, 6, 6, 4, 13 ).getTimeInMillis();

        long start = new GregorianCalendar(
            2008, 6, 9, 4, 15 ).getTimeInMillis();

        StringBuffer ans = new StringBuffer();
        ProgrammeFormat.calcTimeDeltaBetween( start, now, ans );

        assert ans.toString().equals( "starts_in_days_template3" );
    }

    private long getLongTime( int hour, int minute )
    {
        return new GregorianCalendar(
            2008, 06, 06, hour, minute ).getTimeInMillis();
    }
}

