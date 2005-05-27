package freeguide.commandline;

import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.StringHelper;

import freeguide.lib.updater.RepositoryUtils;
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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchRepository
{

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

        PluginsRepository repository =
            RepositoryUtils.parse( 
                new InputSource( new FileInputStream( args[0] ) ), "" );

        final BufferedWriter out =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( args[1] ), "UTF-8" ) );

        writeHeader( out );
        listPackages( out, repository.getAllPackages(  ) );
        writeFooter( out );
        out.flush(  );

        out.close(  );
        list( new File( "." ) );
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
                dir.getPath(  ).substring( 2 ).replace( '\\', '/' );

            if( !allFiles.contains( fileName ) )
            {
                System.out.println( "File not in repository: " + fileName );
            }
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
            "      <file path=\"" + file.getPath(  ) + "\" size=\""
            + file.getSize(  ) + "\" md5sum=\"" + file.getMd5sum(  )
            + "\"/>\n" );
        allFiles.add( file.getPath(  ) );
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
