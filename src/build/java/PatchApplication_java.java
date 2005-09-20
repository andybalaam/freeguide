import freeguide.lib.fgspecific.PluginInfo;

import freeguide.lib.general.Version;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class PatchApplication_java
{

    protected static final String FILENAME =
        "src/java-common/freeguide/lib/fgspecific/Application.java";

    /**
     * DOCUMENT_ME!
     *
     * @param plugins DOCUMENT_ME!
     *
     * @throws IOException DOCUMENT_ME!
     */
    public static void patch( final PluginInfo[] plugins )
        throws IOException
    {

        BufferedReader rd =
            new BufferedReader( 
                new InputStreamReader( 
                    new FileInputStream( FILENAME ), "UTF-8" ) );
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
                "\\/\\*VER_BEG\\*\\/.*\\/\\*VER_END\\*\\/",
                "/*VER_BEG*/" + getVersionCos( plugins[0].getVersion(  ) )
                + "/*VER_END*/" );

        BufferedWriter wr =
            new BufferedWriter( 
                new OutputStreamWriter( 
                    new FileOutputStream( FILENAME + ".new" ), "UTF-8" ) );
        wr.write( strData );
        wr.flush(  );
        wr.close(  );
        PatchAllFiles.changeOldFile( FILENAME );
    }

    protected static String getVersionCos( final Version ver )
    {

        return ver.getDotFormat(  ).replaceAll( "\\.", ", " );
    }
}
