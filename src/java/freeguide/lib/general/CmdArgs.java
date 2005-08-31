/*
 *  FreeGUide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.lib.general;

import freeguide.FreeGuide;

import java.util.Properties;

/**
 * Processes and stores the command line arguments passed to an application.
 * Arguments should be in GNU style. It means arguments can be like
 * --key=data or  --key style.
 *
 * @author Andy Balaam
 * @author Alex Buloichik (alex73 at zaval.org)
 * @version 3
 */
public class CmdArgs
{

    /**
     * Constructs a new set of command line arguments for use in an
     * application.
     *
     * @param args the command line arguments
     *
     * @return DOCUMENT_ME!
     */
    public static Properties parse( final String[] args )
    {

        final Properties result = new Properties(  );

        // Go through each element of the arguments
        for( int i = 0; i < args.length; i++ )
        {

            if( args[i].startsWith( "--" ) )
            {

                final int pos = args[i].indexOf( '=' );
                final String key;
                final String value;

                if( pos >= 0 )
                {
                    key = args[i].substring( 2, pos );
                    value = args[i].substring( pos + 1 );
                }
                else
                {
                    key = args[i].substring( 2 );

                    if( ( i + 1 ) < args.length )
                    {
                        value = args[i + 1];
                        i++;
                    }
                    else
                    {
                        value = "";
                    }
                }

                result.put( key, value );
            }
            else
            {
                FreeGuide.log.warning( "Invalid argument: " + args[i] );
            }
        }

        return result;
    }
}
