package freeguide.build.patchallfiles;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.updater.RepositoryUtils;
import freeguide.plugins.program.freeguide.lib.updater.data.PluginsRepository;

import org.xml.sax.InputSource;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchRepository
{
    protected static final String PATH_BASE = "dist/repository/";

    static void patch( final PluginInfo[] plugins ) throws Exception
    {
        PluginsRepository repository =
            RepositoryUtils.parse( 
                new InputSource( 
                    new FileInputStream( "repository/repositoryInfo.xml" ) ),
                PATH_BASE );
        final BufferedWriter out =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( "repository/repositoryInfo.xml.new" ),
                    "UTF-8" ) );

        writeHeader( out );
        listMirrors( out, repository, repository.getMirrorLocations(  ) );
        listPackages( out, plugins );
        writeFooter( out );
        out.flush(  );

        out.close(  );
        PatchAllFiles.changeOldFile( "repository/repositoryInfo.xml" );
    }

    protected static void listMirrors( 
        final BufferedWriter out, final PluginsRepository repository,
        final String[] list ) throws Exception
    {
        for( int i = 0; i < list.length; i++ )
        {
            out.write( 
                "  <mirror location=\"" + list[i] + "\" path=\""
                + repository.getPathByMirrorsLocation( list[i] ) + "\"/>\n" );
        }
    }

    protected static void listPackages( 
        final BufferedWriter out, PluginInfo[] plugins )
        throws Exception
    {
        for( int i = 0; i < plugins.length; i++ )
        {
            String packageType;

            if( "program-freeguide".equals( plugins[i].getID(  ) ) )
            {
                packageType = "application";
            }
            else if( plugins[i].getID(  ).startsWith( "grabber-" ) )
            {
                packageType = "plugin-grabber";
            }
            else if( plugins[i].getID(  ).startsWith( "importexport-" ) )
            {
                packageType = "plugin-importexport";
            }
            else if( plugins[i].getID(  ).startsWith( "reminder-" ) )
            {
                packageType = "plugin-reminder";
            }
            else if( plugins[i].getID(  ).startsWith( "storage-" ) )
            {
                packageType = "plugin-storage";
            }
            else if( plugins[i].getID(  ).startsWith( "ui-" ) )
            {
                packageType = "plugin-ui";
            }
            else if( plugins[i].getID(  ).startsWith( "other-" ) )
            {
                packageType = "other";
            }
            else
            {
                throw new Exception( 
                    "Unknown plugin type for plugin " + plugins[i].getID(  ) );
            }

            out.write( 
                "  <package id=\"" + plugins[i].getID(  ) + "\" version=\""
                + plugins[i].getVersion(  ).getDotFormat(  ) + "\" type=\""
                + packageType + "\" repositoryPath=\""
                + getRepositoryPath( plugins[i] ) + "\">\n" );

            out.write( "  </package>\n" );

            /*if(
                !new File( PATH_BASE + getRepositoryPath( plugins[i] ) )
                    .exists(  ) )
            {
                System.err.println(
                    "package '" + getRepositoryPath( plugins[i] )
                    + "' not exists" );
            }*/
        }
    }

    protected static String getRepositoryPath( final PluginInfo plugin )
    {
        return "package-" + plugin.getID(  ) + "-"
        + plugin.getVersion(  ).getDotFormat(  ) + ".zip";
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
