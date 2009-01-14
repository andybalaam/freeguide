package freeguide.common.lib.general;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Checks for an xml header at the beginning of a stream declaring its
 * encoding as utf-8 or something else.
 *
 * Assumes, if it can't find any evidence otherwise, that it is utf-8.
 */
public class IsUTF8StreamChecker
{
    public enum IsUTF8Status
    {
        UNDECIDED,
        UTF8,
        OTHER
    }

    private IsUTF8Status isUTF8Status = IsUTF8Status.UNDECIDED;

    private int bytes_checked = 0;
    private static final int MAX_BYTES_BEFORE_OPEN_BRACKET = 10;

    private boolean insideBracket = false;
    private byte previousByte = 0;

    private StringWriter collectedBracket = new StringWriter();

    public boolean checkUTF8( byte[] bytes, int off, int ret )
    {
        if( isUTF8Status != IsUTF8Status.UNDECIDED )
        {
            return ( isUTF8Status == IsUTF8Status.UTF8 );
        }

        for( int i = off; i < off + ret; ++i )
        {
            byte thisByte = bytes[i];

            if( insideBracket )
            {
                collectedBracket.write( thisByte );

                if( thisByte == '>' && previousByte == '?' )
                {
                    isUTF8Status = processBracket(
                        collectedBracket.toString() );
                    break;
                }
            }
            else
            {
                if( thisByte == '?' && previousByte == '<' )
                {
                    collectedBracket.write( previousByte );
                    collectedBracket.write( thisByte );
                    insideBracket = true;
                }
                else
                {
                    ++bytes_checked;
                    if( bytes_checked > MAX_BYTES_BEFORE_OPEN_BRACKET )
                    {
                        isUTF8Status = IsUTF8Status.UTF8;
                        break;
                    }
                }
            }
            previousByte = thisByte;
        }

        return isUTF8Stream();
    }

    public boolean isUTF8Stream()
    {
        return ( isUTF8Status != IsUTF8Status.OTHER );
    }

    private IsUTF8Status processBracket( String bracketString )
    {
        StringReader reader = new StringReader( bracketString );
        StreamTokenizer tok = new StreamTokenizer( reader );

        tok.eolIsSignificant( false );
        tok.wordChars( '=', '=' );

        try
        {
            String currentAttr = new String();
            boolean previousTokenWasEncodingAttr = false;
            while( tok.nextToken() != StreamTokenizer.TT_EOF )
            {
                if( tok.sval != null )
                {
                    if( previousTokenWasEncodingAttr )
                    {
                        return processEncoding( tok.sval );
                    }

                    if( tok.sval == "=" )
                    {
                        currentAttr += tok.sval;
                    }
                    else
                    {
                        currentAttr = tok.sval;
                    }

                    if( currentAttr.equals( "encoding=" ) )
                    {
                        previousTokenWasEncodingAttr = true;
                    }
                }
            }
        }
        catch( IOException e )
        {
            e.printStackTrace();
            // TODO: log properly
        }

        return IsUTF8Status.UTF8;
    }

    private IsUTF8Status processEncoding( String encodingString )
    {
        if( encodingString.toLowerCase().equals( "utf-8" ) )
        {
            return IsUTF8Status.UTF8;
        }
        else
        {
            return IsUTF8Status.OTHER;
        }
    }

    public IsUTF8Status TESTING_ONLY_processBracket( String bracketString )
    {
        return processBracket( bracketString );
    }

    public IsUTF8Status TESTING_ONLY_processEncoding( String encodingString )
    {
        return processEncoding( encodingString );
    }

    public String TESTING_ONLY_getCollectedBracket()
    {
        return collectedBracket.toString();
    }
}
