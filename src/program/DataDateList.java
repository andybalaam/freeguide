/*
 * This class is contributed to the freeguide-tv project
 * by Walter Duncan
 *
 * Copyright (c) 2002 by Walter Duncan
 *
 * -----------------------------------------------------
 *
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
import java.io.FilenameFilter;
import java.util.Vector;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Collections;

/**
 *  This class provides an object that contains, and maintains a vector
 *  of dates that are a segment of a filename.
 *  The date segment currently must be between the first "-" character, and the
 *  first "." character. (FIXME)
 *  The directory path, and filename regex are configurable.
 *
 * @author  Walter Duncan
 * @version 0
 */
public class DataDateList {
	private Vector dates;
	private String path;
	private String filter;
	private boolean dirty=true;

	// Set up a filename filter 
	private class fgdNFilter implements FilenameFilter {
		private String filter;
		public fgdNFilter(String filter) {
			this.filter=filter;
			//fgdNFilter();
		}
		public boolean accept(File dir, String name) {
			if (name.matches(filter)) {
				return true;
			}
			return false;
		}
	}
	/**
	*   Constructor:  Set internal path, and filter values, and
	*   update the internal Date list.
	*/
	public DataDateList(String path, String filter) {
		this.path=path;
		this.filter=filter;
		dirty=true;
		updateDates();
	}

	/** Using current path, and filter settings, update 
	*   internal date list.
	*/
	public void updateDates() {
		dates = new Vector();
		Calendar cdt;
		Date dt;
		File f = new File(path);
		FilenameFilter NameFilter = new fgdNFilter(filter);
		String[] dir = f.list(NameFilter);
		//System.out.println("f.getName() : "+f.getName());
		int ndx;

		// loop through all files in the directory
		// collect the unique date portions of the names
		for(int i=0; i < dir.length;i++) {
		//	System.out.println("  " + dir[i]);
			int s=dir[i].indexOf('-')+1;
			int e=dir[i].indexOf('.');
			String d=dir[i].substring(s,e);
		//	System.out.println("  Datestr: "+d);
			ndx = dates.indexOf(d);
			if (ndx < 0) {
				cdt = new GregorianCalendar();
				//dt = new Calendar();
				//dt = new Date();
				cdt.set(Integer.parseInt(d.substring(0,4)),
					Integer.parseInt(d.substring(4,6))-1,
					Integer.parseInt(d.substring(6,8)),
					0,0,0);
				//dates.add(new String(d));
				dates.add(new Date(cdt.getTimeInMillis()));
			}
		}
		Collections.sort(dates);
		dirty=false;
	}

	/** Get the current date list.  Update the date list before
	*   returning if internal values have changed.
	*/
	public Vector getDates() {
		if (dirty) { updateDates(); }
		return dates;
	}

	/** Set the internal path.
	*
	*/
	public void setPath(String path) {
		this.path=path;
		dirty=true;
	}

	/** Get the internal path value.
	*/
	public String getPath() {
		return path;
	}

	/** Set the internal filter value.
	*/
	public void setFilter(String filter) {
		this.filter=filter;
		dirty=true;
	}

	/** Get the internal filter value.
	*/
	public String getFilter() {
		return filter;
	}

	
	public int size() {
		return dates.size();
	}
	
	public Date get(int i) {
		return ((Date)dates.get(i));
	}
	
	/** Debugging output the date list to standard output.
	*/
	public void toConsole() {
		if (dirty) { updateDates(); }
		for(int i=0;i<dates.size();i++) {
			//System.out.println(dates.get(i));
			System.out.println(((Date)dates.get(i)).toString());
		}
	}

	/** Unit testing, and debugging.
	*/
	public static void main(String[] args) {
		System.out.println("Date List");
		for(int i=0; i < args.length; i++) {
			System.out.println("Arg["+i+"]: "+args[i]);
		}
		DataDateList ddl = new DataDateList(
			"/home/wduncan/freeguide-tv/data/",
			"^tv-.*\\.xmltv$");
		ddl.toConsole();
	}
}

