import freeguide.lib.fgspecific.PluginInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Locale;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchFile
{

    /**
     * DOCUMENT_ME!
     *
     * @param inFileName DOCUMENT_ME!
     * @param outFileName DOCUMENT_ME!
     * @param plugins DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void patch( 
        final String inFileName, final String outFileName,
        final PluginInfo[] plugins ) throws IOException
    {

        BufferedReader rd =
            new BufferedReader( 
                new InputStreamReader( 
                    new FileInputStream( inFileName ), "UTF-8" ) );
        StringBuffer data = new StringBuffer(  );

        while( true )
        {

            String line = rd.readLine(  );

            if( line == null )
            {

                break;
            }

            data.append( line );
            data.append( '\n' );
        }

        rd.close(  );

        String strData = data.toString(  );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_MAJOR__", "" + plugins[0].getVersion(  ).major );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_MINOR__", "" + plugins[0].getVersion(  ).minor );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_REVISION__",
                "" + plugins[0].getVersion(  ).revision );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_BUILD__", "" + plugins[0].getVersion(  ).build );
        strData =
            strData.replaceAll( 
                "__ANT_NAME_VERSION__",
                "freeguide-" + plugins[0].getVersion(  ).getDotFormat(  ) );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_DOTTED__",
                plugins[0].getVersion(  ).getDotFormat(  ) );

        BufferedWriter wr =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( outFileName ), "UTF-8" ) );
        wr.write( strData );
        wr.flush(  );
        wr.close(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param templateFileName DOCUMENT_ME!
     * @param prefix DOCUMENT_ME!
     * @param suffix DOCUMENT_ME!
     * @param plugins DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void patchAllPlugins( 
        final String templateFileName, final String prefix, final String suffix,
        final PluginInfo[] plugins ) throws IOException
    {

        BufferedReader rd =
            new BufferedReader( 
                new InputStreamReader( 
                    new FileInputStream( templateFileName ), "UTF-8" ) );
        StringBuffer data = new StringBuffer(  );

        while( true )
        {

            String line = rd.readLine(  );

            if( line == null )
            {

                break;
            }

            data.append( line );
            data.append( '\n' );
        }

        rd.close(  );

        String strData = data.toString(  );

        for( int i = 0; i < plugins.length; i++ )
        {

            String result =
                strData.replaceAll( 
                    "__ANT_PLUGIN_NAME__", "" + plugins[i].getID(  ) );
            result =
                result.replaceAll( 
                    "__ANT_PLUGIN_VERSION_DOTTED__",
                    "" + plugins[i].getVersion(  ).getDotFormat(  ) );
            result =
                result.replaceAll( 
                    "__ANT_PLUGIN_DESCRIPTION__",
                    "" + plugins[i].getDescription( Locale.ENGLISH ) );

            BufferedWriter wr =
                new BufferedWriter( 
                    new OutputStreamWriter( 
                        new FileOutputStream( 
                            prefix + plugins[i].getID(  ) + suffix ), "UTF-8" ) );
            wr.write( result );
            wr.flush(  );
            wr.close(  );
        }
    }
}
