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
import java.awt.*;
import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 *  Some static global methods used in various parts of FreeGuide.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    3
 */

public class Utils {

	public static void execNoWait( String cmd, PreferencesGroup prefs ) {
		
		String[] cmds = new String[1];
		cmds[0] = cmd;
		execNoWait( cmds, prefs );
		
	}
	
	/**
     *  Execute an external command, without waiting for it to finish.
     *
     *@param  cmds  Description of the Parameter
     */
    public static void execNoWait(String[] cmds) {
		execNoWait( cmds, FreeGuide.prefs );
	}
    /**
     *  Execute an external command, without waiting for it to finish.
     *
     *@param  cmds  Description of the Parameter
     */
    public static void execNoWait(String[] cmds, PreferencesGroup prefs) {

        // Step through each command in the list
        for (int i = 0; i < cmds.length; i++) {

            // Substitute in any system variables for this command
            String cmdstr = prefs.performSubstitutions(cmds[i]);
			
            // Log what we're doing
			if( FreeGuide.log != null ) {
				FreeGuide.log.info("Executing system command in background: "
					+ cmdstr);
			} else {
				System.err.println( "Executing system command in background: "
					+ cmdstr );
			}

            try {

                // Parse the command into arguments and execute
                Runtime.getRuntime().exec(parseCommand(cmdstr));

            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     *  Execute an external command and wait for it to finish, providing visual
     *  feedback to the user.
     *
     *@param  cmds         Description of the Parameter
     *@param  commandType  Description of the Parameter
     *@param  launcher     Description of the Parameter
     *@param  date         Description of the Parameter
     */
    public static void execAndWait(JFrame owner, String[] cmds,
			String commandType, Calendar date) {

        ExecutorDialog executor = new ExecutorDialog(owner, cmds, commandType,
			date);
        executor.setVisible(true);

    }


    /**
     *  Description of the Method
     *
     *@param  cmds         Description of the Parameter
     *@param  commandType  Description of the Parameter
     *@param  launcher     Description of the Parameter
     */
    public static void execAndWait(JFrame owner, String[] cmds,
			String commandType) {

        // Show the command execution window
        ExecutorDialog executor = new ExecutorDialog(owner, cmds, commandType);
        executor.setVisible(true);

    }

	public static void execAndWait(JFrame owner, String[] cmds, String commandType,
			PreferencesGroup prefs) {

        // Show the command execution window
        ExecutorDialog executor = new ExecutorDialog(owner, cmds, commandType,
			prefs);
        executor.setVisible(true);

    }

    // parse
    /**
     *  Convert a command into an array of arguments. Adapted from a method
     *  written by Slava Pestov for the JEdit project www.jedit.org Thanks
     *  Slava!
     *
     *@param  command  Description of the Parameter
     *@return          Description of the Return Value
     */
    public static String[] parseCommand(String command) {
        Vector args = new Vector();
        String[] ans;

        // We replace \ with a non-printable char because
        // StreamTokenizer handles \ specially, which causes
        // problems on Windows as \ is the file separator
        // there.

        // After parsing is done, the non printable char is
        // changed to \ once again.

        // StreamTokenizer needs a way to disable backslash
        // handling...
        command = command.replace('\\', dosSlash);

        StreamTokenizer st = new StreamTokenizer(new StringReader(command));
        st.resetSyntax();
        st.wordChars('!', 255);
        st.whitespaceChars(0, ' ');
        st.quoteChar('"');
        st.quoteChar('\'');

        try {
            loop :
            while (true) {
                switch (st.nextToken()) {
                    case StreamTokenizer.TT_EOF:
                        break loop;
                    case StreamTokenizer.TT_WORD:
                    case '"':
                    case '\'':
                        args.addElement(st.sval.replace(dosSlash, '\\'));
                        break;
                }
            }
        } catch (java.io.IOException io) {
            // won't happen
        }

        ans = new String[args.size()];
        args.copyInto(ans);

        return ans;
    }


    // --------------------------------------------------------------------

    /**
     *  In each of the string in str, replace any occurences of oldStr with
     *  newStr.
     *
     *@param  str     Description of the Parameter
     *@param  oldStr  Description of the Parameter
     *@param  newStr  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static String[] substitute(String[] str, String oldStr, String newStr) {

        String[] ans = new String[str.length];

        // Go through each string we're processing
        for (int i = 0; i < str.length; i++) {

            // Copy it into the output array
            ans[i] = str[i];

            // Find the first occurence of the string to be replaced
            int j = ans[i].indexOf(oldStr);
            int k;

            // Keep replacing until there are no more.
            while (j != -1) {

                k = j + oldStr.length();
                if (k < ans[i].length()) {
                    ans[i] = ans[i].substring(0, j) + newStr + ans[i].substring(k);
                } else {
                    ans[i] = ans[i].substring(0, j) + newStr;
                }
                j = ans[i].indexOf(oldStr);

            }

        }

        return ans;
    }


    // -------------------------------------------------

    /**
     *  Convert a Vector of FreeGuideProgrammes to an array.
     *
     *@param  vector  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static Programme[] arrayFromVector_Programme(Vector vector) {
        Programme[] ans = new Programme[vector.size()];
        vector.copyInto(ans);
        return ans;
    }


    /**
     *  Description of the Method
     *
     *@param  vector  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static ChannelSet[] arrayFromVector_ChannelSet(Vector vector) {
        ChannelSet[] ans = new ChannelSet[vector.size()];
        vector.copyInto(ans);
        return ans;
    }


    /**
     *  Convert a Vector of Strings to an array.
     *
     *@param  vector  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static String[] arrayFromVector_String(Vector vector) {
        String[] ans = new String[vector.size()];
        vector.copyInto(ans);
        return ans;
    }


    /**
     *  Convert a Vector of Calendars to an array.
     *
     *@param  vector  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static Calendar[] arrayFromVector_Calendar(Vector vector) {
        Calendar[] ans = new Calendar[vector.size()];
        vector.copyInto(ans);
        return ans;
    }


    /**
     *  Convert a Vector of Integers to an array of ints.
     *
     *@param  vector  Description of the Parameter
     *@return         Description of the Return Value
     */
    public static int[] arrayFromVector_int(Vector vector) {
        int[] ans = new int[vector.size()];
        for (int i = 0; i < ans.length; i++) {
            ans[i] = ((Integer) vector.get(i)).intValue();
        }
        return ans;
    }
	
	
    // ----------------------------------------------

    private final static char dosSlash = 127;

}
