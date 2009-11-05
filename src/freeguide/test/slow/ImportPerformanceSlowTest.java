package freeguide.test.slow;

import java.io.File;
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

public class ImportPerformanceSlowTest
{
    private static final int LARGE_NUMBER = 1000000;

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
        test_StorageManyProgrammes();
        test_XMLTVHandlerManyProgrammes();
    }

    /**
     * Check that when we throw many programmes at the StorageSerFilesByDay
     * and StoragePipe classes they don't blow up.
     *
     * @throws Exception
     */
    private void test_StorageManyProgrammes() throws Exception
    {
        Application.setInstance( new NullApplication() );

        File tmp_dir = new File( "tmp_dir_test_StorageManyProgrammes" );

        StorageSerFilesByDay storage = new StorageSerFilesByDay( tmp_dir );
        StoragePipe pipe = new StoragePipe( storage );
        pipe.addChannel( new TVChannel( "channel1" ) );

        for( int i = 0; i < LARGE_NUMBER; ++i )
        {
            TVProgramme prog = new TVProgramme();
            prog.setStart( i );
            prog.setEnd( i + 1 );
            prog.setTitle( "prog" + i );
            pipe.addProgramme( "channel1", new TVProgramme() );
        }

        FileHelper.deleteDir( tmp_dir );
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
