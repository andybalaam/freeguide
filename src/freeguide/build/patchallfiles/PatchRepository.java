package freeguide.build.patchallfiles;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;

import freeguide.common.lib.general.StringHelper;

import freeguide.plugins.program.freeguide.lib.updater.RepositoryUtils;
import freeguide.plugins.program.freeguide.lib.updater.data.PluginsRepository;

import org.xml.sax.InputSource;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.Iterator;
import java.util.Map;

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
                    new FileInputStream( "src/repositoryInfo.xml" ) ),
                PATH_BASE );
        final BufferedWriter out =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( "src/repositoryInfo.xml.new" ),
                    "UTF-8" ) );

        writeHeader( out );
        listMirrors( out, repository, repository.getMirrorLocations(  ) );
        listPackages( out, plugins );
        writeFooter( out );
        out.flush(  );

        out.close(  );
        PatchAllFiles.changeOldFile( "src/repositoryInfo.xml" );
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

            if( "freeguide".equals( plugins[i].getID(  ) ) )
            {
                packageType = "application";
            }
            else if( plugins[i].getID(  ).startsWith( "grabber-" ) )
            {
                packageType = "plugin-grabber";
            }
            else if( plugins[i].getID(  ).startsWith( "impexp-" ) )
            {
                packageType = "plugin-impexp";
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

            writeTexts( out, plugins[i] );

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

    protected static void writeTexts( 
        final BufferedWriter out, final PluginInfo plugin )
        throws Exception
    {

        final Map names = plugin.getNames(  );
        final Map descriptions = plugin.getDescriptions(  );

        final Iterator it = names.keySet(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            final String locale = (String)it.next(  );
            final String name = (String)names.get( locale );
            final String desc = (String)descriptions.get( locale );
            out.write( 
                "    <name lang=\"" + locale + "\">"
                + StringHelper.toXML( name ) + "</name>\n" );
            out.write( 
                "    <description lang=\"" + locale + "\">"
                + StringHelper.toXML( desc ) + "</description>\n" );
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
