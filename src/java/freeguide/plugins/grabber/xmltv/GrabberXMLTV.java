package freeguide.plugins.grabber.xmltv;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorChannels;

import freeguide.lib.general.StringHelper;
import freeguide.lib.general.Utils;

import freeguide.lib.impexp.XMLTVImport;

import freeguide.plugins.BaseModule;
import freeguide.plugins.ILogger;
import freeguide.plugins.IModuleConfigurationUI;
import freeguide.plugins.IModuleConfigureFromWizard;
import freeguide.plugins.IModuleGrabber;
import freeguide.plugins.IProgress;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.prefs.Preferences;

import javax.swing.JDialog;

/**
 * Grabber implementation for XMLTV.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class GrabberXMLTV extends BaseModule implements IModuleGrabber,
    IModuleConfigureFromWizard
{

    protected final static String ID = "xmltv";

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_ALWAYS = 0;

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_NEVER = 1;

    /** DOCUMENT ME! */
    public final static int REDOWNLOAD_ASK = 2;

    // execExternal
    // ----------------------------------------------------------------------
    private final static String lb = System.getProperty( "line.separator" );
    protected static Properties cmds;

    /** DOCUMENT ME! */
    public Config config = new Config(  );
    boolean isStopped = true;
    private Calendar date;
    private Process pr;
    private StreamReaderThread readOutput;
    private StreamReaderThread readError;
    protected ConfigureUIPanel confUI;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getID(  )
    {

        return ID;

    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
        System.out.println( "XMLTVGrabber stopped" );

        isStopped = true;

        if( pr != null )
        {
            pr.destroy(  );

            pr = null;

            System.out.println( "pr destroyed" );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param progress DOCUMENT ME!
     * @param logger DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public TVData grabData( final IProgress progress, final ILogger logger )
        throws Exception
    {
        isStopped = false;

        final TVData result = new TVData(  );

        Iterator it = config.needToRun.iterator(  );

        while( it.hasNext(  ) )
        {
            grabOne( result, (String)it.next(  ), progress, logger );

        }

        return result;

    }

    protected void configureChannels( final String modName )
    {
        new File( FreeGuide.config.workingDirectory + "/xmltv-configs/" )
        .mkdirs(  );

        String cmd = getCommand( modName, "cfg" );

        if( cmd == null )
        {
            System.err.println( "Command not defined for " + modName );

            return;

        }

        cmd = StringHelper.replaceAll( 
                cmd, "%config_file%",
                FreeGuide.config.workingDirectory + "/xmltv-configs/"
                + modName + ".conf" );
        cmd = StringHelper.replaceAll( 
                cmd, "%xmltv_path%",
                FreeGuide.runtimeInfo.installDirectory + "/xmltv" );

        System.err.println( "Run command: " + cmd );

        int resultCode = execCmdSimple( Utils.parseCommand( cmd ) );

        System.err.println( "Result code = " + resultCode );

    }

    protected static synchronized Map getCommands(  )
    {

        if( cmds == null )
        {
            cmds = new Properties(  );

            try
            {
                cmds.load( 
                    GrabberXMLTV.class.getClassLoader(  ).getResourceAsStream( 
                        GrabberXMLTV.class.getPackage(  ).getName(  ).replace( 
                            '.', '/' ) + "/commands.properties" ) );

            }

            catch( IOException ex )
            {
                ex.printStackTrace(  );

            }
        }

        return cmds;

    }

    protected static String getCommand( 
        final String modName, final String suffix )
    {

        return (String)getCommands(  ).get( 
            modName + "." + suffix + "."
            + ( FreeGuide.runtimeInfo.isUnix ? "lin" : "win" ) );

    }

    protected static String[] getMods(  )
    {

        String suffix =
            ".run." + ( FreeGuide.runtimeInfo.isUnix ? "lin" : "win" );

        List result = new ArrayList(  );

        Map cs = getCommands(  );

        Iterator it = cs.keySet(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            String key = (String)it.next(  );

            if( key.endsWith( suffix ) )
            {
                result.add( 
                    key.substring( 0, key.length(  ) - suffix.length(  ) ) );

            }
        }

        return (String[])result.toArray( new String[result.size(  )] );

    }

    protected void grabOne( 
        final TVData result, final String modName, final IProgress progress,
        final ILogger logger )
    {
        progress.setProgressMessage( FreeGuide.msg.getString( "downloading" ) );

        String cmd = (String)config.commandsRun.get( modName );

        if( cmd == null )
        {
            cmd = getCommand( modName, "run" );

        }

        if( cmd == null )
        {
            System.err.println( "Command not defined for " + modName );

            logger.error( "Command not defined for " + modName );

            return;

        }

        cmd = StringHelper.replaceAll( 
                cmd, "%config_file%",
                FreeGuide.config.workingDirectory + "/xmltv-configs/"
                + modName + ".conf" );
        cmd = StringHelper.replaceAll( 
                cmd, "%xmltv_path%",
                FreeGuide.runtimeInfo.installDirectory + "/xmltv" );

        System.err.println( "Run command: " + cmd );

        logger.info( "Running command: " + cmd );

        int resultCode =
            execCmd( result, Utils.parseCommand( cmd ), progress, logger );

        System.err.println( "Result code = " + resultCode );

        logger.info( "Result code = " + resultCode );

    }

    protected int execCmd( 
        final TVData result, final String[] args, final IProgress progress,
        final ILogger logger )
    {

        try
        {
            pr = Runtime.getRuntime(  ).exec( args );

        }

        catch( IOException ex )
        {
            ex.printStackTrace(  );

            return -1;
        }

        // Get the input and output streams of this process
        BufferedReader prErr =
            new BufferedReader( 
                new InputStreamReader( pr.getErrorStream(  ) ) );

        Thread threadData =
            new ReadOutput( result, pr.getInputStream(  ), logger );

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
            ex.printStackTrace(  );

            res = -1;

        }

        return res;

    }

    protected int execCmdSimple( final String[] args )
    {

        try
        {
            pr = Runtime.getRuntime(  ).exec( args );

            return pr.waitFor(  );

        }

        catch( Exception ex )
        {
            ex.printStackTrace(  );

            return -1;
        }
    }

    private void clearUp(  )
    {

        if( pr != null )
        {
            pr.destroy(  );

        }

        if( readOutput != null )
        {
            readOutput.stop(  );

        }

        if( readError != null )
        {
            readError.stop(  );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param prefs DOCUMENT_ME!
     */
    public void setConfigStorage( Preferences prefs )
    {
        super.setConfigStorage( prefs );

        loadObjectFromPreferences( config );

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

        return new ConfigureUIController( this );

    }

    /**
     * DOCUMENT_ME!
     */
    public void saveConfig(  )
    {
        saveObjectToPreferences( config );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param regionTree DOCUMENT_ME!
     * @param runSelectChannels DOCUMENT ME!
     */
    public void configureFromWizard( 
        final String regionTree, final boolean runSelectChannels )
    {
        config.needToRun.clear(  );

        if( !regionTree.startsWith( ID + '/' ) )
        {

            return;

        }

        String modName = regionTree.substring( ID.length(  ) + 1 );

        config.needToRun.add( modName );

        if( runSelectChannels )
        {
            configureChannels( modName );

        }

        saveConfig(  );

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
                ex.printStackTrace(  );

            }
        }
    }

    protected class ReadOutput extends Thread
    {

        final protected ILogger logger;
        final protected InputStream in;
        final TVData result;

        /**
         * Creates a new ReadOutputData object.
         *
         * @param result DOCUMENT ME!
         * @param in DOCUMENT ME!
         * @param logger DOCUMENT ME!
         */
        public ReadOutput( 
            final TVData result, InputStream in, ILogger logger )
        {
            this.in = in;

            this.logger = logger;

            this.result = result;

        }

        /**
         * DOCUMENT_ME!
         */
        public void run(  )
        {

            TVData newData = new TVData(  );

            try
            {
                new XMLTVImport(  ).process( 
                    in, newData, new XMLTVImport.Filter(  ) );

                newData.iterate( 
                    new TVIteratorChannels(  )
                    {
                        protected void onChannel( TVChannel channel )
                        {
                            channel.setID( ID + "/" + channel.getID(  ) );
                        }
                    } );

                result.mergeFrom( newData );

            }

            catch( Exception ex )
            {
                logger.error( "Error execute grabber: " + ex.getMessage(  ) );

                System.err.println( 
                    "Error execute grabber: " + ex.getMessage(  ) );

                ex.printStackTrace(  );

            }
        }
    }
}
