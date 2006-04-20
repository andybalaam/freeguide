package freeguide.plugins.grabber.xmltv;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.lib.general.LanguageHelper;
import freeguide.common.lib.general.StringHelper;
import freeguide.common.lib.general.Utils;

import freeguide.common.lib.impexp.XMLTVImport;

import freeguide.common.plugins.BaseModule;
import freeguide.common.plugins.ILogger;
import freeguide.common.plugins.IModuleConfigurationUI;
import freeguide.common.plugins.IModuleConfigureFromWizard;
import freeguide.common.plugins.IModuleGrabber;
import freeguide.common.plugins.IProgress;
import freeguide.common.plugins.IStoragePipe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

/**
 * Grabber implementation for XMLTV.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class GrabberXMLTV extends BaseModule implements IModuleGrabber,
    IModuleConfigureFromWizard
{

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_ALWAYS = 0;

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_NEVER = 1;

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_ASK = 2;

    // execExternal
    // ----------------------------------------------------------------------
    protected static Properties cmds;

    /** DOCUMENT ME! */
    public XMLTVConfig config = new XMLTVConfig(  );
    boolean isStopped = true;
    private Process pr;
    protected XMLTVConfigureUIPanelModule confUI;
    protected CountryInfo[] countryInfos;

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {

        final JMenuItem menuLine = new JMenuItem(  );
        menuLine.setText( 
            getLocalizer(  ).getLocalizedMessage( "Menu.Tools.ChooseChannels" ) );
        Application.getInstance(  ).getMainMenu(  ).getTools(  ).insert( 
            menuLine, 0 );

        menuLine.addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    new Thread(  )
                        {
                            public void run(  )
                            {

                                final List modules = new ArrayList(  );

                                synchronized( config.modules )
                                {
                                    modules.addAll( config.modules );
                                }

                                for( int i = 0; i < modules.size(  ); i++ )
                                {

                                    final XMLTVConfig.ModuleInfo moduleInfo =
                                        (XMLTVConfig.ModuleInfo)modules.get( 
                                            i );
                                    configureChannels( moduleInfo );
                                }
                            }
                        }.start(  );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getConfig(  )
    {

        return config;
    }

    /**
     * DOCUMENT_ME!
     */
    public void stopGrabbing(  )
    {
        isStopped = true;

        if( pr != null )
        {
            pr.destroy(  );

            pr = null;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param progress DOCUMENT ME!
     * @param logger DOCUMENT ME!
     * @param storage DOCUMENT ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public void grabData( 
        final IProgress progress, final ILogger logger,
        final IStoragePipe storage ) throws Exception
    {
        isStopped = false;

        synchronized( config.modules )
        {

            for( int i = 0; i < config.modules.size(  ); i++ )
            {

                XMLTVConfig.ModuleInfo moduleInfo =
                    (XMLTVConfig.ModuleInfo)config.modules.get( i );
                grabOne( storage, moduleInfo, progress, logger );
            }
        }
    }

    protected void configureChannels( final XMLTVConfig.ModuleInfo moduleInfo )
    {
        new File( 
            Application.getInstance(  ).getWorkingDirectory(  )
            + "/xmltv-configs/" ).mkdirs(  );

        String cmd = getCommand( moduleInfo.moduleName, "cfg" );

        if( cmd == null )
        {
            Application.getInstance(  ).getLogger(  ).severe( 
                "Command not defined for " + moduleInfo.moduleName );

            return;

        }

        cmd = StringHelper.replaceAll( 
                cmd, "%config_file%",
                Application.getInstance(  ).getWorkingDirectory(  )
                + "/xmltv-configs/" + moduleInfo.configFileName );
        cmd = StringHelper.replaceAll( 
                cmd, "%xmltv_path%",
                Application.getInstance(  ).getInstallDirectory(  ) + "/xmltv" );

        Application.getInstance(  ).getLogger(  ).finest( 
            "Run command: " + cmd );

        int resultCode = execCmdSimple( Utils.parseCommand( cmd ) );
        Application.getInstance(  ).getLogger(  ).finest( 
            "Result code = " + resultCode );
    }

    protected static synchronized Map getCommands(  )
    {

        if( cmds == null )
        {
            cmds = new Properties(  );

            try
            {
                cmds.load( 
                    LanguageHelper.getUncachedStream( 
                        GrabberXMLTV.class.getPackage(  ).getName(  ).replace( 
                            '.', '/' ) + "/commands.properties" ) );

            }
            catch( IOException ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.SEVERE, "Error loading commands settings for xmltv",
                    ex );
            }
        }

        return cmds;

    }

    protected static String getCommand( 
        final String modName, final String suffix )
    {

        return (String)getCommands(  ).get( 
            modName + "." + suffix + "."
            + ( Application.getInstance(  ).isUnix(  ) ? "lin" : "win" ) );

    }

    protected static String[] getMods( 
        final String prefix, final String suffix )
    {

        List result = new ArrayList(  );

        Map cs = getCommands(  );

        Iterator it = cs.keySet(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            String key = (String)it.next(  );

            if( key.startsWith( prefix ) && key.endsWith( suffix ) )
            {
                result.add( 
                    key.substring( 
                        prefix.length(  ), key.length(  ) - suffix.length(  ) ) );
            }
        }

        return (String[])result.toArray( new String[result.size(  )] );

    }

    protected void grabOne( 
        final IStoragePipe storage, final XMLTVConfig.ModuleInfo moduleInfo,
        final IProgress progress, final ILogger logger )
    {
        progress.setProgressMessage( 
            Application.getInstance(  ).getLocalizedMessage( "downloading" ) );

        String cmd = moduleInfo.commandToRun;

        if( cmd == null )
        {
            cmd = getCommand( moduleInfo.moduleName, "run" );

        }

        if( cmd == null )
        {
            Application.getInstance(  ).getLogger(  ).severe( 
                "Command not defined for " + moduleInfo.moduleName );

            logger.error( "Command not defined for " + moduleInfo.moduleName );

            return;

        }

        cmd = StringHelper.replaceAll( 
                cmd, "%config_file%",
                Application.getInstance(  ).getWorkingDirectory(  )
                + "/xmltv-configs/" + moduleInfo.configFileName );
        cmd = StringHelper.replaceAll( 
                cmd, "%xmltv_path%",
                Application.getInstance(  ).getInstallDirectory(  ) + "/xmltv" );

        Application.getInstance(  ).getLogger(  ).finest( 
            "Run command: " + cmd );

        logger.info( "Running command: " + cmd );

        int resultCode =
            execCmd( storage, Utils.parseCommand( cmd ), progress, logger );

        Application.getInstance(  ).getLogger(  ).finest( 
            "Result code = " + resultCode );

        logger.info( "Result code = " + resultCode );
    }

    protected int execCmd( 
        final IStoragePipe storage, final String[] args,
        final IProgress progress, final ILogger logger )
    {

        try
        {
            pr = Runtime.getRuntime(  ).exec( args );

        }

        catch( IOException ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error execute xmltv grabber", ex );

            return -1;
        }

        // Get the input and output streams of this process
        BufferedReader prErr =
            new BufferedReader( 
                new InputStreamReader( pr.getErrorStream(  ) ) );

        Thread threadData =
            new ReadOutput( storage, pr.getInputStream(  ), logger );

        threadData.start(  );

        new ReadErrors( prErr, logger ).start(  );

        int res;

        try
        {
            res = pr.waitFor(  );

            while( threadData.isAlive(  ) )
            {
                Thread.sleep( 200 );

            }
        }

        catch( InterruptedException ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.SEVERE, "Interrupted xmltv process", ex );

            res = -1;

        }

        return res;

    }

    protected int execCmdSimple( final String[] args )
    {

        try
        {
            pr = Runtime.getRuntime(  ).exec( args );
            new ReadProcess( pr.getInputStream(  ), Level.FINEST ).start(  );
            new ReadProcess( pr.getErrorStream(  ), Level.FINE ).start(  );

            return pr.waitFor(  );

        }

        catch( Exception ex )
        {
            Application.getInstance(  ).getLogger(  ).log( 
                Level.WARNING, "Error execute xmltv grabber", ex );

            return -1;
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param parentDialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {

        return new XMLTVConfigureUIController( this );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param regionName DOCUMENT_ME!
     * @param runSelectChannels DOCUMENT ME!
     */
    public void configureFromWizard( 
        final String regionName, final boolean runSelectChannels )
    {

        XMLTVConfig.ModuleInfo info = new XMLTVConfig.ModuleInfo(  );
        info.moduleName =
            (String)getCommands(  ).get( "region." + regionName + ".grabber" );
        info.configFileName =
            (String)getCommands(  ).get( "region." + regionName + ".grabber" )
            + ".conf";

        synchronized( config.modules )
        {
            config.modules.clear(  );
            config.modules.add( info );
        }

        if( runSelectChannels )
        {
            configureChannels( info );
        }
    }

    protected String listToString( final List list )
    {

        final StringBuffer result = new StringBuffer(  );

        for( int i = 0; i < list.size(  ); i++ )
        {
            result.append( (String)list.get( i ) );

            result.append( ' ' );

        }

        result.setLength( result.length(  ) - 1 );

        return result.toString(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public CountryInfo[] getSupportedCountries(  )
    {

        if( countryInfos == null )
        {

            String[] mods = getMods( "region.", ".grabber" );
            countryInfos = new CountryInfo[mods.length];

            for( int i = 0; i < mods.length; i++ )
            {
                countryInfos[i] =
                    new CountryInfo( 
                        mods[i], 0,
                        "true".equalsIgnoreCase( 
                            (String)getCommands(  ).get( 
                                "region." + mods[i] + ".allowChannelsSelect" ) ) );
            }
        }

        return countryInfos;
    }

    protected class ReadProcess extends Thread
    {

        final protected BufferedReader stream;
        final protected Level logLevel;

        /**
         * Creates a new ReadProcess object.
         *
         * @param stream DOCUMENT ME!
         * @param level DOCUMENT ME!
         */
        public ReadProcess( final InputStream stream, final Level level )
        {
            this.stream =
                new BufferedReader( new InputStreamReader( stream ) );
            this.logLevel = level;
        }

        /**
         * DOCUMENT_ME!
         */
        public void run(  )
        {

            String line;

            try
            {

                while( ( line = stream.readLine(  ) ) != null )
                {
                    Application.getInstance(  ).getLogger(  ).log( 
                        logLevel, line );
                }
            }
            catch( IOException ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING, "Error on read xmltv console stream", ex );
            }
        }
    }

    protected static class ReadErrors extends Thread
    {

        BufferedReader rd;
        ILogger logger;

        /**
         * Creates a new Read object.
         *
         * @param rd DOCUMENT ME!
         * @param logger DOCUMENT ME!
         */
        public ReadErrors( BufferedReader rd, final ILogger logger )
        {
            this.rd = rd;

            this.logger = logger;

        }

        /**
         * DOCUMENT_ME!
         */
        public void run(  )
        {

            String line;

            try
            {

                while( ( line = rd.readLine(  ) ) != null )
                {
                    logger.warning( line );

                }
            }

            catch( IOException ex )
            {
                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING, "Error read output from xmltv grabber", ex );
            }
        }
    }

    protected class ReadOutput extends Thread
    {

        final protected ILogger logger;
        final protected InputStream in;
        final protected IStoragePipe storage;

        /**
         * Creates a new ReadOutputData object.
         *
         * @param storage DOCUMENT ME!
         * @param in DOCUMENT ME!
         * @param logger DOCUMENT ME!
         */
        public ReadOutput( 
            final IStoragePipe storage, InputStream in, ILogger logger )
        {
            this.in = in;

            this.logger = logger;

            this.storage = storage;
        }

        /**
         * DOCUMENT_ME!
         */
        public void run(  )
        {

            try
            {
                new XMLTVImport(  ).process( 
                    in, storage, new XMLTVImport.Filter(  ), "xmltv/" );
            }

            catch( Exception ex )
            {
                logger.error( "Error execute grabber: " + ex.getMessage(  ) );

                Application.getInstance(  ).getLogger(  ).log( 
                    Level.WARNING,
                    "Error execute grabber: " + ex.getMessage(  ), ex );
            }
        }
    }
}
