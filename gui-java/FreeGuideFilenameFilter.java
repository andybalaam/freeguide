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
//import java.io.FileFilter;
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
public class FreeGuideFilenameFilter implements java.io.FileFilter {

	/** Blank constructor matches anything */
    public FreeGuideFilenameFilter() {
		this(".*", "All Files");
    }
	
	// Construct an unnamed filter
	public FreeGuideFilenameFilter(String regex) {
		
		this.pattern = Pattern.compile(regex);
		this.description = "Filenames matching /" + regex + "/";
		
	}
	
	// Construct a named filter
	public FreeGuideFilenameFilter(String regex, String description) {
		
		this.pattern = Pattern.compile(regex);
		this.description = description;
		
	}

	// Accept all directories and files matching the pattern
	public boolean accept(File f) {
		
		if(f.isDirectory()) {
			return true;
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
	
}
