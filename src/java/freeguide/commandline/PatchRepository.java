package freeguide.commandline;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.StringHelper;

import freeguide.lib.updater.RepositoryUtils;
import freeguide.lib.updater.data.PluginMirror;
import freeguide.lib.updater.data.PluginPackage;
import freeguide.lib.updater.data.PluginsRepository;

import freeguide.plugins.IModule;

import org.xml.sax.InputSource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchRepository
{

    /** Path for repository root. */
    public static final String PATH_BASE = "dist/repository/";
    protected static Set allFiles = new TreeSet(  );

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        FreeGuide.log = Logger.getLogger( "org.freeguide-tv" );
        FreeGuide.setLocale( Locale.ENGLISH );
        PluginsManager.loadModules(  );

        for( int i = 0; i < args.length; i++ )
        {

            PluginsRepository repository =
                RepositoryUtils.parse( 
                    new InputSource( 
                        new FileInputStream( 
                            args[i] + "/repositoryInfoTemplate.xml" ) ),
                    PATH_BASE );

            final BufferedWriter out =
                new BufferedWriter( 
                    new OutputStreamWriter( 
                        new FileOutputStream( args[i] + "/repositoryInfo.xml" ),
                        "UTF-8" ) );

            writeHeader( out );
            listMirrors( out, repository.getAllMirrors(  ) );
            listPackages( 
                out, "application",
                new IModule[] { Application.getApplicationModule(  ) } );
            listPackages( 
                out, "plugin-grabber", PluginsManager.getGrabbers(  ) );
            listPackages( 
                out, "plugin-impexp",
                PluginsManager.getImportersAndExporters(  ) );
            listPackages( 
                out, "plugin-reminder", PluginsManager.getReminders(  ) );
            listPackages( 
                out, "plugin-storage", PluginsManager.getStorages(  ) );
            listPackages( out, "plugin-ui", PluginsManager.getViewers(  ) );
            writeFooter( out );
            out.flush(  );

            out.close(  );
        }

        // list( new File( PATH_BASE ) );
    }

    protected static void list( final File dir )
    {

        if( dir.isDirectory(  ) )
        {

            File[] files = dir.listFiles(  );

            if( files != null )
            {

                for( int i = 0; i < files.length; i++ )
                {
                    list( files[i] );
                }
            }
        }
        else
        {

            String fileName =
                dir.getPath(  ).substring( PATH_BASE.length(  ) ).replace( 
                    '\\', '/' );

            if( !allFiles.contains( fileName ) )
            {
                System.out.println( "File not in repository: " + fileName );
            }
        }
    }

    protected static void listMirrors( 
        final BufferedWriter out, final List list ) throws Exception
    {

        for( int i = 0; i < list.size(  ); i++ )
        {

            PluginMirror mirror = (PluginMirror)list.get( i );
            out.write( 
                "  <mirror location=\"" + mirror.getLocation(  )
                + "\" path=\"" + mirror.getPath(  ) + "\"/>\n" );
        }
    }

    protected static void listPackages( 
        final BufferedWriter out, final String packageType, IModule[] modules )
        throws Exception
    {

        for( int i = 0; i < modules.length; i++ )
        {

            final IModule module = modules[i];

            out.write( 
                "  <package id=\"" + module.getID(  ) + "\" version=\""
                + module.getVersion(  ).getDotFormat(  ) + "\" type=\""
                + packageType + "\" repositoryPath=\""
                + getRepositoryPath( module ) + "\">\n" );

            writeTexts( out, module );

            out.write( "  </package>\n" );

            if( 
                !new File( PATH_BASE + getRepositoryPath( module ) ).exists(  ) )
            {
                System.err.println( 
                    "package '" + getRepositoryPath( module ) + "' not exists" );
            }
        }
    }

    protected static String getRepositoryPath( IModule module )
    {

        return "package-" + module.getID(  ) + "-"
        + module.getVersion(  ).getDotFormat(  ) + ".zip";
    }

    protected static void writeTexts( 
        final BufferedWriter out, final IModule mod ) throws Exception
    {

        Locale[] locales = mod.getSuppotedLocales(  );

        for( int i = 0; i < locales.length; i++ )
        {
            mod.setLocale( locales[i] );
            out.write( 
                "    <name lang=\"" + locales[i].toString(  ) + "\">"
                + StringHelper.toXML( mod.getName(  ) ) + "</name>\n" );
            out.write( 
                "    <description lang=\"" + locales[i].toString(  ) + "\">"
                + StringHelper.toXML( mod.getDescription(  ) )
                + "</description>\n" );
        }
    }

    protected static void writeHeader( final BufferedWriter out )
        throws IOException
    {
        out.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" );

        out.write( "<packages>\n" );
    }

    protected static void writeFooter( final BufferedWriter out )
        throws IOException
    {
        out.write( "</packages>\n" );
    }
}
