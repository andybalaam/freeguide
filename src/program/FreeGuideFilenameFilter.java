/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * freeguide-tv.sourceforge.net
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.io.File;
import java.util.regex.Pattern;

/**
 * FreeGuideFilenameFilter
 *
 * A simple FileFilter that matches filenames on a regular expression.
 * Doesn't implement FilenameFilter as that makes the user work too
 * hard I reckon.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideFilenameFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {

	/** Blank constructor matches anything */
    public FreeGuideFilenameFilter() {
		this(".*", "All Files", true);
    }
	
	// Construct an unnamed filter
	public FreeGuideFilenameFilter(String regex) {
		this(regex, "Filenames matching /" + regex + "/", true);
	}
	
	// Construct a named filter
	public FreeGuideFilenameFilter(String regex, String description) {
		this(regex, description, true);
	}

	// Construct an unnamed filter specifying whether to include dirs
	public FreeGuideFilenameFilter(String regex, boolean allowDirectories) {
		this(regex, "Filenames matching /" + regex + "/", allowDirectories);
	}
	
	// Construct a named filter specifying whether to include dirs
	public FreeGuideFilenameFilter(String regex, String description, boolean allowDirectories) {
		this.pattern = Pattern.compile(regex);
		this.description = description;
		this.allowDirectories = allowDirectories;
	}
	
	// Accept all directories and files matching the pattern
	public boolean accept(File f) {
		
		if(f.isDirectory()) {
			if(allowDirectories) {
				return true;
			} else {
				return false;
			}
		}
		
		return pattern.matcher(f.getName()).matches();
		
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getPattern() {
		return pattern.pattern();
	}
	
	private Pattern pattern;
	private String description;
	private boolean allowDirectories;
	
}
