/*
 * FreeGuide J2
 *
 * Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

/*
 * parseCommand() method Copyright (c) by Slava Pestov
 *
 * from the Jedit project: www.jedit.org
 *
 */

import java.awt.Color;
import java.lang.reflect.Array;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.Vector;
import java.util.Calendar;
import javax.swing.JComboBox;
import javax.swing.JTextArea;

/**
 * Some static global methods used in various parts of FreeGuide.
 *
 * @author  Andy Balaam
 * @version 3
 */

public class FreeGuideUtils {
	
	/**
	 * Execute an external command, without waiting for it to finish.
	 */
	public static void execNoWait(String[] cmds) {
		
		// Step through each command in the list
		for(int i=0;i<cmds.length;i++) {
			
			// Substitute in any system variables for this command
			String cmdstr = FreeGuide.prefs.performSubstitutions(cmds[i]);
			
			// Log what we're doing
			FreeGuide.log.info("Executing system command in background: "+cmdstr);
			
			try {
			
				// Parse the command into arguments and execute
				Runtime.getRuntime().exec(parseCommand(cmdstr));
				
			} catch(java.io.IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	/** 
	 * Execute an external command and wait for it to finish, providing visual
	 * feedback to the user.
	 */
	public static void execAndWait(String[] cmds, String commandType, FreeGuideLauncher launcher, Calendar date) {
		// Show the command execution window
		launcher.setVisible(false);
		FreeGuideExecutor executor = new FreeGuideExecutor(launcher, cmds, commandType, date);
		executor.setVisible(true);
	}
	public static void execAndWait(String[] cmds, String commandType, FreeGuideLauncher launcher) {
		// Show the command execution window
		launcher.setVisible(false);
		FreeGuideExecutor executor = new FreeGuideExecutor(launcher, cmds, commandType);
		executor.setVisible(true);
	}
	
	// parse
	/**
	 * Convert a command into an array of arguments.
	 *
	 * Adapted from a method written by Slava Pestov
	 * for the JEdit project www.jedit.org
	 *
	 * Thanks Slava!
	 */
	public static String[] parseCommand(String command)
	{
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
		command = command.replace('\\',dosSlash);

		StreamTokenizer st = new StreamTokenizer(new StringReader(command));
		st.resetSyntax();
		st.wordChars('!',255);
		st.whitespaceChars(0,' ');
		st.quoteChar('"');
		st.quoteChar('\'');

		try
			{
loop:			while(true)
				{
					switch(st.nextToken())
					{
					case StreamTokenizer.TT_EOF:
						break loop;
					case StreamTokenizer.TT_WORD:
					case '"':
					case '\'':
						args.addElement(st.sval.replace(dosSlash,'\\'));
					break;
				}
			}
		}
		catch(java.io.IOException io)
		{
			// won't happen
		}

		ans = new String[args.size()];
		args.copyInto(ans);
			
		return ans;
	}
	
	// --------------------------------------------------------------------
	
	/**
	 * In each of the string in str, replace any occurences of oldStr with
	 * newStr.
	 */
	public static String[] substitute(String[] str, String oldStr, String newStr) {
		
		String[] ans = new String[str.length];
		
		// Go through each string we're processing
		for(int i=0;i<str.length;i++) {
			
			// Copy it into the output array
			ans[i] = str[i];
			
			// Find the first occurence of the string to be replaced
			int j = ans[i].indexOf(oldStr);
			int k;
			
			// Keep replacing until there are no more.
			while(j!=-1) {
				
				k = j + oldStr.length();
				if(k<ans[i].length()) {
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
	 * Convert a Vector of FreeGuideProgrammes to an array.
	 */
	public static FreeGuideProgramme[] arrayFromVector_FreeGuideProgramme(Vector vector) {
		FreeGuideProgramme[] ans = new FreeGuideProgramme[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Convert a Vector of FreeGuideFavourites to an array.
	 */
	public static FreeGuideFavourite[] arrayFromVector_FreeGuideFavourite(Vector vector) {
		FreeGuideFavourite[] ans = new FreeGuideFavourite[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
        
        public static FreeGuideChannelSet[] arrayFromVector_FreeGuideChannelSet(Vector vector) {
            FreeGuideChannelSet[] ans = new FreeGuideChannelSet[vector.size()];
            vector.copyInto(ans);
            return ans;
        }
	
	/**
	 * Convert a Vector of Strings to an array.
	 */
	public static String[] arrayFromVector_String(Vector vector) {
		String[] ans = new String[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Convert a Vector of Calendars to an array.
	 */
	public static Calendar[] arrayFromVector_Calendar(Vector vector) {
		Calendar[] ans = new Calendar[vector.size()];
		vector.copyInto(ans);
		return ans;
	}
	
	/**
	 * Convert a Vector of Integers to an array of ints.
	 */
	public static int[] arrayFromVector_int(Vector vector) {
		int[] ans = new int[vector.size()];
		for(int i=0;i<ans.length;i++) {
			ans[i] = ((Integer)vector.get(i)).intValue();
		}
		return ans;
	}
	
	// ----------------------------------------------
	
	private static final char dosSlash = 127;
	
}
