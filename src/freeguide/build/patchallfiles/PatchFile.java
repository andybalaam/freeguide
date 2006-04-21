package freeguide.build.patchallfiles;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;

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

        final String strData = replaceAppInfo( plugins[0], data.toString(  ) );

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

            String result = replaceAppInfo( plugins[0], strData );

            result =
                result.replaceAll( 
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

    protected static String replaceAppInfo( 
        final PluginInfo appInfo, final String in )
    {

        String strData = in;
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_MAJOR__", "" + appInfo.getVersion(  ).major );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_MINOR__", "" + appInfo.getVersion(  ).minor );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_REVISION__",
                "" + appInfo.getVersion(  ).revision );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_BUILD__", "" + appInfo.getVersion(  ).build );
        strData =
            strData.replaceAll( 
                "__ANT_NAME_VERSION__",
                "freeguide-" + appInfo.getVersion(  ).getDotFormat(  ) );
        strData =
            strData.replaceAll( 
                "__ANT_VERSION_DOTTED__",
                appInfo.getVersion(  ).getDotFormat(  ) );

        return strData;
    }
}
