/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
import java.io.File;
import java.util.regex.Pattern;

/**
 *  FreeGuideFilenameFilter A simple FileFilter that matches filenames on a
 *  regular expression. Doesn't implement FilenameFilter as that makes the user
 *  work too hard I reckon.
 *
 *@author     Andy Balaam
 *@created    28 June 2003
 *@version    1
 */
public class FilenameFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

    /**
     *  Blank constructor matches anything
     */
    public FilenameFilter() {
        this(".*", "All Files", true);
    }


    // Construct an unnamed filter
    /**
     *  Constructor for the FilenameFilter object
     *
     *@param  regex  Description of the Parameter
     */
    public FilenameFilter(String regex) {
        this(regex, "Filenames matching /" + regex + "/", true);
    }


    // Construct a named filter
    /**
     *  Constructor for the FilenameFilter object
     *
     *@param  regex        Description of the Parameter
     *@param  description  Description of the Parameter
     */
    public FilenameFilter(String regex, String description) {
        this(regex, description, true);
    }


    // Construct an unnamed filter specifying whether to include dirs
    /**
     *  Constructor for the FilenameFilter object
     *
     *@param  regex             Description of the Parameter
     *@param  allowDirectories  Description of the Parameter
     */
    public FilenameFilter(String regex, boolean allowDirectories) {
        this(regex, "Filenames matching /" + regex + "/", allowDirectories);
    }


    // Construct a named filter specifying whether to include dirs
    /**
     *  Constructor for the FilenameFilter object
     *
     *@param  regex             Description of the Parameter
     *@param  description       Description of the Parameter
     *@param  allowDirectories  Description of the Parameter
     */
    public FilenameFilter(String regex, String description, boolean allowDirectories) {
        this.pattern = Pattern.compile(regex);
        this.description = description;
        this.allowDirectories = allowDirectories;
    }


    // Accept all directories and files matching the pattern
    /**
     *  Description of the Method
     *
     *@param  f  Description of the Parameter
     *@return    Description of the Return Value
     */
    public boolean accept(File f) {

        if (f.isDirectory()) {
            if (allowDirectories) {
                return true;
            } else {
                return false;
            }
        }

        return pattern.matcher(f.getName()).matches();
    }


    /**
     *  Gets the description attribute of the FilenameFilter object
     *
     *@return    The description value
     */
    public String getDescription() {
        return description;
    }


    /**
     *  Gets the pattern attribute of the FilenameFilter object
     *
     *@return    The pattern value
     */
    public String getPattern() {
        return pattern.pattern();
    }


    private Pattern pattern;
    private String description;
    private boolean allowDirectories;

}
