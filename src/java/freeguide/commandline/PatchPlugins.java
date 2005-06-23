package freeguide.commandline;

import freeguide.lib.fgspecific.PluginInfo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchPlugins
{

    /** Path for repository root. */
    public static final String PATH_BASE = "dist/repository/";
    protected static final Pattern ZIP_NAME_MASK =
        Pattern.compile( ".+[/|\\\\]package-(.+)-[0-9\\.]+\\.zip" );

    //    protected static Set allFiles = new TreeSet(  );
    protected static SAXParser saxParser;

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        saxParser = SAXParserFactory.newInstance(  ).newSAXParser(  );

        File[] packages =
            new File( PATH_BASE ).listFiles( 
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {

                        return pathname.getPath(  ).endsWith( ".zip" );
                    }
                } );

        if( packages != null )
        {

            for( int i = 0; i < packages.length; i++ )
            {
                patchByZip( new ZipFile( packages[i] ) );
            }
        }
    }

    protected static void patchByZip( final ZipFile zip )
        throws Exception
    {

        final Matcher m = ZIP_NAME_MASK.matcher( zip.getName(  ) );

        if( !m.matches(  ) )
        {
            System.err.println( 
                "Invalid name for package : " + zip.getName(  ) );

            return;
        }

        System.out.println( "Process " + zip.getName(  ) );

        final String pkgName = m.group( 1 );
        final File dir;

        if( "FreeGuide".equals( pkgName ) )
        {
            dir = new File( "src/java/" );
        }
        else
        {
            dir = new File( "src/plugins/" + pkgName + "/java/" );
        }

        final PluginInfo info = loadFrom( new File( dir, "plugin.xml" ) );
        new File( dir, "plugin.backup.xml" ).delete(  );

        if( 
            !new File( dir, "plugin.xml" ).renameTo( 
                    new File( dir, "plugin.backup.xml" ) ) )
        {
            System.err.println( "Error backup old file" );

            return;
        }

        List files = new ArrayList(  );
        Enumeration enu = zip.entries(  );

        while( enu.hasMoreElements(  ) )
        {

            ZipEntry entry = (ZipEntry)enu.nextElement(  );

            // in reverse order, because we should remove directory at end
            files.add( 0, entry.getName(  ) );
        }

        info.setFiles( files );
        saveTo( new File( dir, "plugin.xml" ), info );
    }

    protected static PluginInfo loadFrom( final File xmlFile )
        throws Exception
    {

        PluginInfo handler = new PluginInfo(  );

        saxParser.parse( xmlFile, handler );

        return handler;
    }

    protected static void saveTo( final File xmlFile, final PluginInfo info )
        throws IOException
    {

        BufferedWriter out =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( xmlFile ), "UTF-8" ) );

        try
        {
            out.write( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" );
            out.write( "<plugin id=\"" + info.getID(  ) + "\"\n" );
            out.write( 
                "        version=\"" + info.getVersion(  ).getDotFormat(  )
                + "\"\n" );
            out.write( "        class=\"" + info.getClassName(  ) + "\">\n\n" );

            iterateTo( info.getNames(  ), out, "name" );
            iterateTo( info.getDescriptions(  ), out, "description" );

            out.write( "\n  <files>\n" );

            List files = info.getFiles(  );

            for( int i = 0; i < files.size(  ); i++ )
            {
                out.write( "    <file path=\"" + files.get( i ) + "\"/>\n" );
            }

            out.write( "  </files>\n" );
            out.write( "</plugin>\n" );
        }
        finally
        {
            out.flush(  );
            out.close(  );
        }

        /*<?xml version="1.0" encoding="UTF-8"?>
        
        <plugin id="FreeGuide"
                version="0.10.2"
                class="freeguide.gui.viewer.MainController">
        
            <name lang="en">FreeGuide-TV</name>
            <description lang="en">Main FreeGuide-TV application.</description>
        */
    }

    protected static void iterateTo( 
        final Map map, final BufferedWriter out, final String tag )
        throws IOException
    {

        final Iterator it = map.keySet(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            Object key = it.next(  );
            out.write( 
                "  <" + tag + " lang=\"" + key.toString(  ) + "\">"
                + map.get( key ) + "</" + tag + ">\n" );

            Object value = map.get( key );
        }
    }
}
