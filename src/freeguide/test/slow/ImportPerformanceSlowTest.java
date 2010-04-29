package freeguide.test.slow;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.xml.sax.helpers.AttributesImpl;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVData;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.data.TVChannelsSet.Channel;
import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.importexport.XMLTVImport;
import freeguide.common.lib.importexport.XMLTVImportHandler;
import freeguide.common.plugininterfaces.FGLogger;
import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.plugininterfaces.IExecutionController;
import freeguide.common.plugininterfaces.IModuleReminder;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IModuleViewer;
import freeguide.common.plugininterfaces.IStoragePipe;
import freeguide.plugins.program.freeguide.lib.fgspecific.StoragePipe;
import freeguide.plugins.storage.serfiles.StorageSerFilesByDay;
import freeguide.test.FreeGuideTest;

public class ImportPerformanceSlowTest
{
    /** A number suitable for doing a lot of in-memory things */
    private static final int LARGE_NUMBER = 1000000;

    /** A number suitable for doing a lot of on-disk things */
    private static final int SOMEWHAT_LARGE_NUMBER = 10000;

    private class NullStoragePipe implements IStoragePipe
    {
        public void addChannel( TVChannel channel )
        {
        }

        public void addData( TVData data )
        {
        }

        public void addProgramme( String channelID, TVProgramme programme )
        {
        }

        public void addProgrammes( String channelID, TVProgramme[] programmes )
        {
        }

        public void finishBlock()
        {
        }
    }

    private class NullProgrammesCountCallback extends XMLTVImport.ProgrammesCountCallback
    {
        public void onProgramme( int count )
        {
        }
    }

    private class NullLogHandler extends Handler
    {
        public void close() throws SecurityException
        {
        }

        public void flush()
        {
        }

        public void publish( LogRecord arg0 )
        {
        }

    }

    private class NullApplication implements IApplication
    {

        public void doEditChannelsSets()
        {
        }

        public void doPrint()
        {
        }

        public void doStartGrabbers()
        {

        }

        public JButton getApplicationForegroundButton()
        {
            return null;
        }

        public JFrame getApplicationFrame()
        {
            return null;
        }

        public JProgressBar getApplicationProgressBar()
        {
            return null;
        }

        public String getBrowserCommand()
        {
            return null;
        }

        public List getChannelsSetsList()
        {
            return null;
        }

        public JFrame getCurrentFrame()
        {
            return null;
        }

        public IModuleStorage getDataStorage()
        {
            return null;
        }

        public IExecutionController getExecutionController()
        {
            return null;
        }

        public FGLogger getFGLogger()
        {
            return null;
        }

        public String getInstallDirectory()
        {
            return null;
        }

        public String getLibDirectory()
        {
            return null;
        }

        public String getDocsDirectory()
        {
            return null;
        }

        public String getLocalizedMessage( String key )
        {
            return null;
        }

        public String getLocalizedMessage( String key, Object[] params )
        {
            return null;
        }

        public Logger getLogger()
        {
            Logger logger = Logger.getAnonymousLogger();
            logger.addHandler( new NullLogHandler() );
            logger.setUseParentHandlers( false );
            return logger;
        }

        public IMainMenu getMainMenu()
        {
            return null;
        }

        public IModuleReminder[] getReminders()
        {
            return null;
        }

        public Locale[] getSupportedLocales()
        {
            return null;
        }

        public TimeZone getTimeZone()
        {
            return null;
        }

        public IModuleViewer getViewer()
        {
            return null;
        }

        public String getWorkingDirectory()
        {
            return null;
        }

        public boolean isUnix()
        {
            return false;
        }

        public void saveAllConfigs()
        {
        }
    }

    public void run() throws Exception
    {
        test_StorageSaveManyProgrammes();
        test_StorageLoadManyProgrammes();
        test_XMLTVHandlerManyProgrammes();
    }

    /**
     * Check that when we throw many programmes at the StorageSerFilesByDay
     * and StoragePipe classes they don't blow up.
     *
     * @throws Exception
     */
    private void test_StorageSaveManyProgrammes() throws Exception
    {
        Application.setInstance( new NullApplication() );

        File tmp_dir = new File( "tmp_dir_test_StorageManyProgrammes" );

        String timePerProg = saveManyProgrammes( tmp_dir );

        System.out.println( "test_StorageSaveManyProgrammes: "
            + timePerProg + " secs for " + SOMEWHAT_LARGE_NUMBER
            + " programmes" );

        FileHelper.deleteDir( tmp_dir );
    }

    /**
     * Check that when we read many programmes from the StorageSerFilesByDay
     * and StoragePipe classes they don't blow up or be slow.
     *
     * @throws Exception
     */
    private void test_StorageLoadManyProgrammes() throws Exception
    {
        Application.setInstance( new NullApplication() );

        File tmp_dir = new File( "tmp_dir_test_StorageManyProgrammes" );

        saveManyProgrammes( tmp_dir );
        String timePerProg = loadManyProgrammes( tmp_dir );

        System.out.println( "test_StorageLoadManyProgrammes: "
            + timePerProg + " secs for " + SOMEWHAT_LARGE_NUMBER
            + " programmes" );

        FileHelper.deleteDir( tmp_dir );
    }

    private String saveManyProgrammes( File tmp_dir )
    {
        StorageSerFilesByDay storage = new StorageSerFilesByDay( tmp_dir );
        storage.debugIncludeOldProgrammes = true;
        StoragePipe pipe = new StoragePipe( storage );

        Calendar start = Calendar.getInstance();

        pipe.addChannel( new TVChannel( "channel1" ) );

        for( int i = 0; i < SOMEWHAT_LARGE_NUMBER; ++i )
        {
            TVProgramme prog = new TVProgramme();
            prog.setStart( i*10 );
            prog.setEnd( (i+1)*10 - 1 );
            prog.setTitle( "prog" + i );
            pipe.addProgramme( "channel1", prog );
        }

        pipe.finish();

        Calendar end = Calendar.getInstance();

        return FreeGuideTest.Cals2SecsInterval( start, end );
    }

    private String loadManyProgrammes( File tmp_dir )
    throws Exception
    {
        final IModuleStorage.Info info = new IModuleStorage.Info();
        Channel chan = new Channel();
        chan.channelID = "channel1";
        info.channelsList.add( chan );
        info.minDate = 0;
        info.maxDate = (SOMEWHAT_LARGE_NUMBER*10) + 1;

        StorageSerFilesByDay storage = new StorageSerFilesByDay( tmp_dir );
        storage.debugIncludeOldProgrammes = true;

        Calendar start = Calendar.getInstance();
        TVData data = storage.get(  info  );
        Calendar end = Calendar.getInstance();

        FreeGuideTest.my_assert(
            data.getProgrammesCount() == SOMEWHAT_LARGE_NUMBER );

        return FreeGuideTest.Cals2SecsInterval( start, end );
    }

    /**
     * Check that when we throw many programmes at the XMLTVImportHandler
     * class it doesn't blow up.
     *
     * @throws Exception
     */
    private void test_XMLTVHandlerManyProgrammes() throws Exception
    {
        NullStoragePipe storage = new NullStoragePipe();
        NullProgrammesCountCallback countCallback = new NullProgrammesCountCallback();
        XMLTVImport.Filter filter = new XMLTVImport.Filter();

        XMLTVImportHandler handler = new XMLTVImportHandler( storage, countCallback, filter );

        AttributesImpl emptyAttrs = new AttributesImpl();

        handler.startDocument();

        handler.startElement( "", "", "tv", emptyAttrs );

        AttributesImpl channelAttrs = new AttributesImpl();
        channelAttrs.addAttribute( "", "", "id", "", "channel1" );

        handler.startElement( "", "", "channel", channelAttrs );

        AttributesImpl progAttrs = new AttributesImpl();
        progAttrs.addAttribute( "", "", "", "", "" );
        for( int i = 0; i < LARGE_NUMBER; ++i )
        {
            handler.startElement( "", "", "programme", progAttrs );
            handler.endElement( "", "", "programme" );
        }

        handler.endElement( "", "", "channel" );

        handler.endElement( "", "", "tv" );

        handler.endDocument();
    }
}
