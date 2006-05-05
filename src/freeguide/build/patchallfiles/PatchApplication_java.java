package freeguide.build.patchallfiles;

import freeguide.common.lib.general.Version;

import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        "src/freeguide/common/lib/fgspecific/Application.java";
    protected static final String TAG_BEG = "/*VER_BEG*/";
    protected static final String TAG_END = "/*VER_END*/";

    /**
     * DOCUMENT_ME!
     *
     * @param plugins DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void patch( final PluginInfo[] plugins )
        throws Exception
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

        final int posBeg = strData.indexOf( TAG_BEG );
        final int posEnd = strData.indexOf( TAG_END );

        if( ( posBeg < 0 ) || ( posEnd < 0 ) || ( posEnd <= posBeg ) )
        {
            throw new Exception( 
                "Application.java must include /*VER_BEG*/ and /*VER_END*/ tags" );
        }

        strData = strData.substring( 0, posBeg + TAG_BEG.length(  ) )
            + getVersionCos( plugins[0].getVersion(  ) )
            + strData.substring( posEnd );

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
        return " " + ver.getDotFormat(  ).replaceAll( "\\.", ", " ) + " ";
    }
}
