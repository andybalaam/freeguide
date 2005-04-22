/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
/*
 *  parseCommand() method Copyright (c) by Slava Pestov
 *
 *  from the Jedit project: www.jedit.org
 *
 */
package freeguide.lib.general;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.FGDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import java.io.StreamTokenizer;
import java.io.StringReader;

import java.util.Vector;
import java.util.logging.Level;

import javax.swing.JDialog;

/**
 * Some static global methods used in various parts of FreeGuide.
 *
 * @author Andy Balaam
 * @version 3
 */
public class Utils
{

    // ----------------------------------------------
    private final static char dosSlash = 127;

    /**
     * Execute an external command, without waiting for it to finish.
     *
     * @param cmds Description of the Parameter
     */
    public static void execNoWait( String[] cmds )
    {

        //  execNoWait( cmds, null );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param cmd DOCUMENT_ME!
     */
    public static void execNoWait( String cmd )
    {
        FreeGuide.log.info( "Execute system command: " + cmd );

        try
        {

            // Parse the command into arguments and execute
            Runtime.getRuntime(  ).exec( parseCommand( cmd ) );

        }

        catch( java.io.IOException ex )
        {
            FreeGuide.log.log( 
                Level.WARNING, "Error execute command : " + cmd, ex );
        }
    }

    /**
     * Convert a command into an array of arguments. Adapted from a method
     * written by Slava Pestov for the JEdit project www.jedit.org Thanks
     * Slava!
     *
     * @param command Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public static String[] parseCommand( String command )
    {

        Vector args = new Vector(  );

        String[] ans;

        // We replace \ with a non-printable char because
        // StreamTokenizer handles \ specially, which causes
        // problems on Windows as \ is the file separator
        // there.
        // After parsing is done, the non printable char is
        // changed to \ once again.
        // StreamTokenizer needs a way to disable backslash
        // handling...
        command = command.replace( '\\', dosSlash );

        StreamTokenizer st =
            new StreamTokenizer( new StringReader( command ) );

        st.resetSyntax(  );

        st.wordChars( '!', 255 );

        st.whitespaceChars( 0, ' ' );

        st.quoteChar( '"' );

        st.quoteChar( '\'' );

        try
        {
loop: 

            while( true )
            {

                switch( st.nextToken(  ) )
                {

                case StreamTokenizer.TT_EOF:
                    break loop;

                case StreamTokenizer.TT_WORD:
                case '"':

                case '\'':
                    args.addElement( st.sval.replace( dosSlash, '\\' ) );

                    break;
                }
            }
        }

        catch( java.io.IOException io )
        {

            // won't happen
        }

        ans = new String[args.size(  )];

        args.copyInto( ans );

        return ans;

    }

    // --------------------------------------------------------------------

    /**
     * In each of the string in str, replace any occurences of oldStr with
     * newStr.
     *
     * @param str Description of the Parameter
     * @param oldStr Description of the Parameter
     * @param newStr Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public static String[] substitute( 
        String[] str, String oldStr, String newStr )
    {

        String[] ans = new String[str.length];

        // Go through each string we're processing
        for( int i = 0; i < str.length; i++ )
        {

            // Copy it into the output array
            ans[i] = str[i];

            // Find the first occurence of the string to be replaced
            int j = ans[i].indexOf( oldStr );

            int k;

            // Keep replacing until there are no more.
            while( j != -1 )
            {
                k = j + oldStr.length(  );

                if( k < ans[i].length(  ) )
                {
                    ans[i] =
                        ans[i].substring( 0, j ) + newStr
                        + ans[i].substring( k );

                }

                else
                {
                    ans[i] = ans[i].substring( 0, j ) + newStr;

                }

                j = ans[i].indexOf( oldStr );

            }
        }

        return ans;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param parent DOCUMENT_ME!
     * @param dialog DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean centreDialogAndRun( 
        final Component parent, FGDialog dialog )
    {

        Dimension thisSize = parent.getSize(  );

        Dimension dialogSize = dialog.getSize(  );

        Point thisLocation = parent.getLocation(  );

        dialog.setLocation( 
            thisLocation.x + ( ( thisSize.width - dialogSize.width ) / 2 ),
            thisLocation.y + ( ( thisSize.height - dialogSize.height ) / 2 ) );

        return dialog.showDialog(  );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param parent DOCUMENT_ME!
     * @param dialog DOCUMENT_ME!
     */
    public static void centreDialog( final Component parent, JDialog dialog )
    {

        Dimension thisSize = parent.getSize(  );

        Dimension dialogSize = dialog.getSize(  );

        Point thisLocation = parent.getLocation(  );

        dialog.setLocation( 
            thisLocation.x + ( ( thisSize.width - dialogSize.width ) / 2 ),
            thisLocation.y + ( ( thisSize.height - dialogSize.height ) / 2 ) );
    }
}
