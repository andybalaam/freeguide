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
import java.util.Vector;

/**
 *  Processes and stores the command line arguments passed to an application.
 *  Arguments beginning with "--" are treated as word-length flags while
 *  arguments beginning with "-" are treated as groups of character- length
 *  arguments. By default all arguments are treated as if they expect some data.
 *  Arguments which should be treated as boolean flags must be passed to the
 *  constructor. Flags requiring data must be immediately followed by that data
 *  (after a space).
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    2
 */
public class CmdArgs {

    /**
     *  Constructs a new set of command line arguments for use in an
     *  application.
     *
     *@param  args  the command line arguments
     */
    public CmdArgs(String[] args) {

        /*
         *  Calls the standard constructor stating that
         *  there are no boolean flags.
         */
        this(args, new Vector());

    }


    /**
     *  Constructs a new set of command line arguments for use in an
     *  application.
     *
     *@param  args          the command line arguments
     *@param  booleanFlags  the array of names of flags which require no
     *      argument since they are boolean
     */
    public CmdArgs(String[] args, Vector booleanFlags) {

        // Initialise the vector that will hold the info
        names = new Vector();
        values = new Vector();
        blankValues = new Vector();

        // Initialise the indicator of whether we've found a flag
        boolean foundFlag = false;

        // Go through each element of the arguments
        for (int i = 0; i < args.length; i++) {

            String arg = args[i];

            if (arg.startsWith("--")) {
                // a word flag

                // Add this word as a flag
                addFlag(arg.substring(2));

                // If this wasn't a boolean flag (i.e. it expects an
                // argument), turn the foundFlag argument on, otherwise
                // turn it off.
                foundFlag = !booleanFlags.contains(arg);

            } else if (arg.startsWith("-")) {
                // single-character flag(s)

                // Add each character after the - as a separate flag
                for (int j = 0; j < arg.length(); j++) {

                    // Add this character as a flag
                    addFlag(arg.charAt(j));

                    // If this wasn't a boolean flag (i.e. it expects an
                    // argument), turn the foundFlag argument on, otherwise
                    // turn it off.
                    foundFlag = !booleanFlags.contains(arg);

                }
                //for

            } else if (foundFlag) {
                // some data for a flag

                // Add the data to the latest flag
                addData(arg);

                // Reset the indicator
                foundFlag = false;

            } else {
                // An argument without an associated flag

                // File this one as a blank value
                blankValues.add(arg);

            }
            //if

        }
        //for

    }
    //FreeGuideCmdArgs


    //------------------------------------------------------------------------

    /**
     *  noErrors Reports on whether argument processing was successful.
     *
     *@return     Description of the Return Value
     *@returns    true unless errors were encountered in the course of argument
     *      processing
     */
    public boolean noErrors() {

        // Always returns true as errors are handled by mis-processing input
        // rather than halting (e.g. "--fr og 3" will say that there is an
        // option --fr with value og, and 3 is an argument, rather than
        // noticing that --fr is unrecognised and quitting.
        return true;
    }


    /**
     *  Returns the data associated with a given flag
     *
     *@param  name  the String name of the flag required
     *@return       The value value
     *@returns      the data for a flag or null if the flag was omitted or had
     *      no data associated with it
     */
    public String getValue(String name) {

        int i = names.indexOf(name);

        if (i == -1) {
            // If this flag was omitted

            return null;
        } else {
            // If not (note

            return (String) values.get(i);
        }
        //if

    }
    //getValue


    /**
     *  Indicates whether a command line flag was used
     *
     *@param  name  the String name of the flag required
     *@return       The set value
     *@returns      true if the flag was used at the command line, false
     *      otherwise
     */
    public boolean isSet(String name) {

        return names.contains(name);
    }
    //isSet


    /**
     *  Provides all the command line arguments which were not given as data to
     *  a flag.
     *
     *@return     The blankValues value
     *@returns    the "blank" command line arguments not associated with a flag
     */
    public Vector getBlankValues() {

        return blankValues;
    }
    //getBlankValues


    //------------------------------------------------------------------------

    /**
     *  Add a flag to the list of flags and give it a null argument. Takes a
     *  char argument and adds it as a String.
     *
     *@param  newFlag  the char flag to add
     */
    private void addFlag(char newFlag) {

        // Converts the char to a String
        addFlag(new Character(newFlag).toString());

    }
    //addFlag


    /**
     *  Add a flag to the list of flags and give it a null argument.
     *
     *@param  newFlag  the String flag to add
     */
    private void addFlag(String newFlag) {

        // Add the flag
        names.add(newFlag);

        // make a blank value for it which may be filled later
        values.add(null);

    }
    //addFlag


    /**
     *  Add some data to the last flag we added.
     *
     *@param  newData  the String data to add
     */
    private void addData(String newData) {

        // Set the last piece of data to the required value
        values.setElementAt(newData, values.size() - 1);

    }
    //addData


    //------------------------------------------------------------------------

    private Vector names;
    // The names of flags passed as arguments
    private Vector values;
    // The values of arguments to each flag
    private Vector blankValues;
    // The arguments not associated with any flag

}
//class
