package freeguide.build.i18n;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
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
    protected static Map<String, String> translatedGlobal;

    /**
     * DOCUMENT_ME!
     *
     * @param args DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void main( String[] args ) throws Exception
    {
        final File mainProps =
            new File( "src/resources/i18n/MessagesBundle.properties" );
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

        translatedGlobal = readPropertiesFile( mainProps );

        System.out.println( 
            "================= Files with untranslated strings =================" );

        for( final File f : propFiles )
        {
            processFileFindUntranslated( f );
        }

        System.out.println( 
            "================= Translation with non-exists strings =================" );

        for( final File f : propFiles )
        {
            processFileFindUnused( f );
        }

        for( final String lang : "be,de,it,fr".split( "," ) )
        {
            System.out.println( 
                "================= Language info: " + lang
                + " =================" );

            for( final File f : propFiles )
            {
                processLang( lang, f );
            }
        }
    }

    protected static void processLang( final String lang, final File propFile )
        throws IOException
    {
        final Map<String, String> baseStrings = readPropertiesFile( propFile );
        final File transFile =
            new File( 
                propFile.getPath(  )
                        .replaceAll( 
                    "\\.properties", '_' + lang + ".properties" ) );

        if( !transFile.exists(  ) )
        {
            System.out.println( 
                "   There is no file : " + transFile.getPath(  ) );

            return;
        }

        final Map<String, String> translatedStrings =
            readPropertiesFile( transFile );

        final Set<String> untranslated =
            new TreeSet<String>( baseStrings.keySet(  ) );
        removeFromSet( untranslated, translatedStrings.keySet(  ) );

        if( untranslated.size(  ) > 0 )
        {
            System.out.println( 
                "   Untranslated strings in " + propFile.getPath(  )
                + " for language '" + lang + "':" );

            for( String str : untranslated )
            {
                System.out.println( "         \"" + str + '"' );
            }
        }

        final Set<String> unused =
            new TreeSet<String>( translatedStrings.keySet(  ) );
        removeFromSet( unused, baseStrings.keySet(  ) );

        if( unused.size(  ) > 0 )
        {
            System.out.println( 
                "   Unused translations in " + propFile.getPath(  )
                + " for language '" + lang + "':" );

            for( String str : unused )
            {
                System.out.println( "         \"" + str + '"' );
            }
        }
    }

    protected static File[] getJavaFilesForPropertiesFile( 
        final File propFile )
    {
        final List<File> allFiles = new ArrayList<File>(  );

        String classesDir = propFile.getName(  );
        classesDir = classesDir.substring( 
                0, classesDir.length(  ) - ".properties".length(  ) );
        classesDir = classesDir.replace( '_', '/' );

        if( classesDir.equals( "MessagesBundle" ) )
        {
            classesDir = "src/freeguide/common/";
            findFile( 
                new File( classesDir ), allFiles,
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {
                        return pathname.getName(  ).endsWith( ".java" );
                    }
                } );
            classesDir = "src/freeguide/plugins/program/";
            findFile( 
                new File( classesDir ), allFiles,
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {
                        return pathname.getName(  ).endsWith( ".java" );
                    }
                } );
        }
        else
        {
            classesDir = "src/freeguide/plugins/" + classesDir;
            findFile( 
                new File( classesDir ), allFiles,
                new FileFilter(  )
                {
                    public boolean accept( File pathname )
                    {
                        return pathname.getName(  ).endsWith( ".java" );
                    }
                } );
        }

        return allFiles.toArray( new File[allFiles.size(  )] );
    }

    protected static String readFile( final File f ) throws IOException
    {
        final StringBuffer buf = new StringBuffer(  );
        final InputStreamReader in =
            new InputStreamReader( new FileInputStream( f ), "UTF-8" );

        try
        {
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
        }
        finally
        {
            in.close(  );
        }

        return buf.toString(  );
    }

    protected static Map<String, String> readPropertiesFile( final File f )
        throws IOException
    {
        final Properties result = new Properties(  );
        result.load( new FileInputStream( f ) );

        return (Map)result;
    }

    protected static Set<String> getStringsForTranslationFromFile( 
        final File f ) throws IOException
    {
        MODE mode = MODE.DATA;
        final Set<String> result = new TreeSet<String>(  );

        StringBuffer str = new StringBuffer(  );
        StringBuffer command = new StringBuffer(  );
        char prevC = '\0';

        final String data = readFile( f );

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
                else if( ( c == '"' ) && ( prevC != '\'' ) )
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

                if( ( c == '"' ) && ( prevC != '\\' ) )
                {
                    mode = MODE.DATA;

                    if( isNeedToLocalize( command.toString(  ) ) )
                    {
                        result.add( str.toString(  ) );
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

        return result;
    }

    protected static boolean isNeedToLocalize( final String command )
    {
        if( command.contains( "static" ) && command.contains( "final" ) )
        {
            return false;
        }

        if( command.contains( "Exception(" ) )
        {
            return false;
        }

        if( 
            command.replaceAll( "\\s+", "" )
                       .contains( "Application.getInstance().getLogger()." ) )
        {
            return false;
        }

        return true;
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

    protected static void processFileFindUntranslated( final File propFile )
        throws Exception
    {
        System.out.println( "Translation '" + propFile.getName(  ) + "' :" );

        final Map<String, String> translated = readPropertiesFile( propFile );

        for( final File f : getJavaFilesForPropertiesFile( propFile ) )
        {
            final Set<String> forTranslation =
                getStringsForTranslationFromFile( f );
            removeFromSet( forTranslation, translated.keySet(  ) );
            removeFromSet( forTranslation, translatedGlobal.keySet(  ) );

            if( forTranslation.size(  ) > 0 )
            {
                System.out.println( 
                    "   Untranslated strings in " + f.getPath(  ) + " :" );

                for( String str : forTranslation )
                {
                    System.out.println( "         \"" + str + '"' );
                }
            }
        }
    }

    protected static void processFileFindUnused( final File propFile )
        throws Exception
    {
        final Map<String, String> translated = readPropertiesFile( propFile );
        final Set<String> keys = translated.keySet(  );

        for( final File f : getJavaFilesForPropertiesFile( propFile ) )
        {
            final Set<String> forTranslation =
                getStringsForTranslationFromFile( f );

            removeFromSet( keys, forTranslation );
        }

        if( keys.size(  ) > 0 )
        {
            System.out.println( 
                "Unused strings in '" + propFile.getName(  ) + "' :" );

            for( String str : keys )
            {
                System.out.println( "         \"" + str + '"' );
            }
        }
    }

    protected static void removeFromSet( 
        final Set<String> sourceSet, final Set<String> removeSet )
    {
        for( final Iterator<String> it = sourceSet.iterator(  );
                it.hasNext(  ); )
        {
            if( removeSet.contains( it.next(  ) ) )
            {
                it.remove(  );
            }
        }
    }
    protected static enum MODE
    {DATA,
        COMMENT,
        LINE_COMMENT,
        STRING;
    }
}
