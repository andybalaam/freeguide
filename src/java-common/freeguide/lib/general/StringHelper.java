package freeguide.lib.general;

/**
 * Helper for some string operations.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class StringHelper
{

    /**
     * DOCUMENT_ME!
     *
     * @param in DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static String toXML( final String in )
    {

        if( in == null )
        {

            return null;
        }

        String result = replaceAll( in, "&", "&amp;" );
        result = replaceAll( result, "\n", "&#13;" );
        result = replaceAll( result, "<", "&lt;" );
        result = replaceAll( result, ">", "&gt;" );
        result = replaceAll( result, "\"", "&quot;" );
        result = replaceAll( result, "'", "&apos;" );

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param in DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static String encodeURL( final String in )
    {

        String result = replaceAll( in, " ", "%20" );

        return result;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param in DOCUMENT_ME!
     * @param mask DOCUMENT_ME!
     * @param to DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static String replaceAll( 
        final String in, final String mask, final String to )
    {

        String result = in;
        int pos = -1;

        while( true )
        {
            pos = result.indexOf( mask, pos + 1 );

            if( pos == -1 )
            {

                break;
            }

            result =
                result.substring( 0, pos ) + to
                + result.substring( pos + mask.length(  ) );
        }

        return result;
    }
}
