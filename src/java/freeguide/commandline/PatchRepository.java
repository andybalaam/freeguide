package freeguide.commandline;

import freeguide.FreeGuide;

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
    public static final String PATH_BASE = "build/";
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

        if( args.length != 2 )
        {
            System.err.println( "Usage: <input file> <output file>" );
        }

        FreeGuide.log = Logger.getLogger( "org.freeguide-tv" );
        FreeGuide.setLocale( Locale.ENGLISH );
        PluginsManager.loadModules(  );

        PluginsRepository repository =
            RepositoryUtils.parse( 
                new InputSource( new FileInputStream( args[0] ) ), PATH_BASE );

        final BufferedWriter out =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( args[1] ), "UTF-8" ) );

        writeHeader( out );
        listMirrors( out, repository.getAllMirrors(  ) );
        listPackages( out, repository.getAllPackages(  ) );
        writeFooter( out );
        out.flush(  );

        out.close(  );
        list( new File( PATH_BASE ) );
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
        final BufferedWriter out, final List list ) throws Exception
    {

        for( int i = 0; i < list.size(  ); i++ )
        {

            PluginPackage pkg = (PluginPackage)list.get( i );
            out.write( 
                "  <package id=\"" + pkg.getID(  ) + "\" version=\""
                + pkg.getVersion(  ) + "\" type=\"" + pkg.getType(  )
                + "\">\n" );

            writeTexts( out, pkg );

            List files = pkg.getFiles(  );

            for( int j = 0; j < files.size(  ); j++ )
            {

                PluginPackage.PackageFile file =
                    (PluginPackage.PackageFile)files.get( j );
                File localFile = new File( PATH_BASE, file.getLocalPath(  ) );

                if( localFile.exists(  ) )
                {
                    file.loadData(  );
                }
                else
                {
                    System.out.println( 
                        "File not found : " + file.getLocalPath(  ) );
                }

                writeFile( out, file );
            }

            out.write( "  </package>\n" );
        }
    }

    protected static void writeTexts( 
        final BufferedWriter out, final PluginPackage pkg )
        throws Exception
    {

        IModule mod = PluginsManager.cloneModule( pkg.getID(  ) );

        if( mod == null )
        {
            throw new Exception( "Module '" + pkg.getID(  ) + "' not defined" );
        }

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

    protected static void writeFile( 
        final BufferedWriter out, final PluginPackage.PackageFile file )
        throws IOException
    {
        out.write( 
            "      <file localPath=\"" + file.getLocalPath(  )
            + "\" repositoryPath=\"" + file.getRepositoryPath(  )
            + "\" size=\"" + file.getSize(  ) + "\" md5sum=\""
            + file.getMd5sum(  ) + "\"/>\n" );
        allFiles.add( file.getLocalPath(  ) );
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
