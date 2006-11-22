package freeguide.build.i18n;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class CheckLocalization
{
    protected static Pattern PATTERN_MAIN =
        Pattern.compile( ".+[A-Za-z]{3,}\\.properties" );
    protected static MODE mode;
    protected static Properties translatedGlobal = new Properties(  );

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( String[] args ) throws Exception
    {
        final File[] propFiles =
            new File( "src/resources/i18n/" ).listFiles( 
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {
                        return PATTERN_MAIN.matcher( pathname.getName(  ) )
                                           .matches(  );
                    }
                } );

        translatedGlobal.load( 
            new FileInputStream( 
                "src/resources/i18n/MessagesBundle.properties" ) );

        for( final File f : propFiles )
        {
            processFile( f );
        }
    }

    protected static void findFile( 
        final File dir, final List<File> files, final FileFilter filter )
    {
        final File[] fs = dir.listFiles(  );

        if( fs == null )
        {
            return;
        }

        for( final File f : fs )
        {
            if( f.isFile(  ) )
            {
                if( filter.accept( f ) )
                {
                    files.add( f );
                }
            }
            else
            {
                if( !f.getName(  ).equals( ".svn" ) )
                {
                    findFile( f, files, filter );
                }
            }
        }
    }

    protected static void processFile( final File propFile )
        throws Exception
    {
        System.out.println( "Process file " + propFile.getName(  ) );

        final Properties translated = new Properties(  );
        translated.load( new FileInputStream( propFile ) );

        String classesDir = propFile.getName(  );
        classesDir = classesDir.substring( 
                0, classesDir.length(  ) - ".properties".length(  ) );
        classesDir = classesDir.replace( '_', '/' );
        classesDir = "src/freeguide/plugins/" + classesDir;

        final List<File> allFiles = new ArrayList<File>(  );
        findFile( 
            new File( classesDir ), allFiles,
            new FileFilter(  )
            {
                public boolean accept( File pathname )
                {
                    return pathname.getName(  ).endsWith( ".java" );
                }
            } );

        for( final File f : allFiles )
        {
            System.out.println( "   " + f.getPath(  ) );

            StringBuffer buf = new StringBuffer(  );
            InputStreamReader in =
                new InputStreamReader( new FileInputStream( f ), "UTF-8" );
            char[] buffer = new char[65536];

            while( true )
            {
                int len = in.read( buffer );

                if( len < 0 )
                {
                    break;
                }

                buf.append( buffer, 0, len );
            }

            processData( translated, buf.toString(  ) );
        }
    }

    protected static void processData( 
        final Properties translated, String data ) throws IOException
    {
        mode = MODE.DATA;

        StringBuffer str = new StringBuffer(  );
        StringBuffer command = new StringBuffer(  );
        char prevC = '\0';

        for( int i = 0; i < data.length(  ); i++ )
        {
            char c = data.charAt( i );

            switch( mode )
            {
            case DATA:

                if( ( prevC == '/' ) && ( c == '*' ) )
                {
                    mode = MODE.COMMENT;
                }
                else if( ( prevC == '/' ) && ( c == '/' ) )
                {
                    mode = MODE.LINE_COMMENT;
                }
                else if( c == '"' )
                {
                    mode = MODE.STRING;
                }

                break;

            case COMMENT:

                if( ( prevC == '*' ) && ( c == '/' ) )
                {
                    mode = MODE.DATA;
                    command.setLength( 0 );
                }

                break;

            case LINE_COMMENT:

                if( c == '\n' )
                {
                    mode = MODE.DATA;
                }

                break;

            case STRING:

                if( c == '"' )
                {
                    mode = MODE.DATA;

                    if( 
                        !translated.containsKey( str.toString(  ) )
                            && !translatedGlobal.containsKey( 
                                str.toString(  ) ) )
                    {
                        String lines = command.toString(  );

                        if( 
                            !lines.contains( "static" )
                                || !lines.contains( "final" ) )
                        {
                            System.out.println( "         " + str );
                        }
                    }

                    str.setLength( 0 );
                }
                else
                {
                    str.append( c );
                }

                break;
            }

            if( mode == MODE.DATA )
            {
                if( ( c == ';' ) || ( c == '{' ) || ( c == '}' ) )
                {
                    command.setLength( 0 );
                }
                else
                {
                    command.append( c );
                }
            }

            prevC = c;
        }
    }
    protected static enum MODE
    {DATA,
        COMMENT,
        LINE_COMMENT,
        STRING;
    }
}
