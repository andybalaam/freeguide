package freeguide.build.patchallfiles;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchAllFiles
{
    static final String program_plugin_name = "program-freeguide";

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( final String[] args ) throws Exception
    {
        PluginInfo[] plugins = loadPluginsInfo(  );
        PatchBuild.patch( plugins );
        PatchApplication_java.patch( plugins );

        new File( "build/install/windows" ).mkdirs(  );
        new File( "build/install/linux/rpm" ).mkdirs(  );

        PatchFile.patch( "doc/VERSION.php.in", "doc/VERSION.php", plugins );
        PatchFile.patch( 
            "install/windows/freeguide-without-xmltv.nsi.in",
            "build/install/windows/freeguide-without-xmltv.nsi", plugins );
        PatchFile.patch( 
            "install/windows/freeguide-with-xmltv.nsi.in",
            "build/install/windows/freeguide-with-xmltv.nsi", plugins );
        PatchFile.patch( 
            "install/linux/rpm/freeguide.spec.in",
            "build/install/linux/rpm/freeguide.spec", plugins );
        PatchSpec.patch( 
            "build/install/linux/rpm/freeguide.spec",
            "build/install/linux/rpm/freeguide.spec", plugins );

        PatchFile.patch( 
            "install/linux/freeguide.1.in", "build/install/linux/freeguide.1",
            plugins );
    }

    /**
     * Go through all the subdirs of dir and add any plugin.xml files
     * to the files list.
     *
     * @param dir DOCUMENT ME!
     * @param files DOCUMENT ME!
     */
    protected static void findPluginDirs( File dir, List files )
    {
        File[] plugin_files =
            dir.listFiles( 
                new FileFilter(  )
                {
                    public boolean accept( File fl )
                    {
                        return fl.toString(  ).endsWith( "plugin.xml" );
                    }
                } );

        for( int i = 0; i < plugin_files.length; ++i )
        {
            files.add( plugin_files[i] );
        }

        File[] dirs =
            dir.listFiles( 
                new FileFilter(  )
                {
                    public boolean accept( File fl )
                    {
                        return fl.isDirectory(  );
                    }
                } );

        for( int i = 0; i < dirs.length; ++i )
        {
            findPluginDirs( dirs[i], files );
        }
    }

    protected static PluginInfo[] loadPluginsInfo(  ) throws Exception
    {
        SAXParser saxParser =
            SAXParserFactory.newInstance(  ).newSAXParser(  );
        List files = new ArrayList(  );
        findPluginDirs( new File( "src/freeguide/plugins/" ), files );

        List result = new ArrayList( files.size(  ) );

        for( int i = 0; i < files.size(  ); i++ )
        {
            File xmlFile = (File)files.get( i );
            PluginInfo info = new PluginInfo(  );
            saxParser.parse( xmlFile, info );

            if( info.getID(  ) != null )
            {
                result.add( info );
            }
        }

        Collections.sort( 
            result,
            new Comparator(  )
            {
                public int compare( Object o1, Object o2 )
                {
                    final PluginInfo p1 = (PluginInfo)o1;
                    final PluginInfo p2 = (PluginInfo)o2;

                    String id1 = p1.getID(  );
                    String id2 = p2.getID(  );

                    if( id1.equals( program_plugin_name ) )
                    {
                        return -1;
                    }
                    else if( id2.equals( program_plugin_name ) )
                    {
                        return 1;
                    }
                    else
                    {
                        return id1.compareTo( id2 );
                    }
                }
            } );

        return (PluginInfo[])result.toArray( new PluginInfo[result.size(  )] );
    }

    protected static void changeOldFile( String baseFileName )
        throws IOException
    {
        new File( baseFileName + ".bak" ).delete(  );

        if( 
            !new File( baseFileName ).renameTo( 
                    new File( baseFileName + ".bak" ) ) )
        {
            throw new IOException( "Error rename file to .bak" );
        }

        if( 
            !new File( baseFileName + ".new" ).renameTo( 
                    new File( baseFileName ) ) )
        {
            throw new IOException( "Error rename .new to file" );
        }

        new File( baseFileName + ".bak" ).delete(  );
    }
}
