/*
 *  This class is contributed to the FreeGuide project
 *  by Walter Duncan
 *
 *  Copyright (c) 2002 by Walter Duncan
 *
 *  -----------------------------------------------------
 *
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

package freeguidetv.gui.viewer;

import java.io.*;
import java.util.*;

/**
 *  This class provides an object that contains, and maintains a vector of dates
 *  that are a segment of a filename. The date segment currently must be between
 *  the first "-" character, and the first "." character. (FIXME) The directory
 *  path, and filename regex are configurable.
 *
 *@author     Walter Duncan
 *@created    07 July 2003
 *@version    2
 */
public class DateFilesExistList {
    private Vector dates;
    private String path;
    private String filter;
    private boolean dirty = true;


    // Set up a filename filter
    /**
     *  Description of the Class
     *
     *@author     Walter Duncan
     *@created    07 July 2003
     */
    private class fgdNFilter implements FilenameFilter {
        private String filter;


        /**
         *  Constructor for the fgdNFilter object
         *
         *@param  filter  Description of the Parameter
         */
        public fgdNFilter(String filter) {
            this.filter = filter;
            //fgdNFilter();
        }


        /**
         *  Description of the Method
         *
         *@param  dir   Description of the Parameter
         *@param  name  Description of the Parameter
         *@return       Description of the Return Value
         */
        public boolean accept(File dir, String name) {
            if (name.matches(filter)) {
                return true;
            }
            return false;
        }
    }


    /**
     *  Constructor: Set internal path, and filter values, and update the
     *  internal Date list.
     *
     *@param  path    Description of the Parameter
     *@param  filter  Description of the Parameter
     */
    public DateFilesExistList(String path, String filter) {
        this.path = path;
        this.filter = filter;
        dirty = true;
        updateDates();
    }


    /**
     *  Using current path, and filter settings, update internal date list.
     */
    public void updateDates() {

        dates = new Vector();
        Calendar cdt;
        String[] dir = {};

        File f = new File(path);
        FilenameFilter NameFilter = new fgdNFilter(filter);
        dir = f.list(NameFilter);
        if (dir == null) {
            dir = new String[0];
        }

        int ndx;

        // loop through all files in the directory
        // collect the unique date portions of the names
        for (int i = 0; i < dir.length; i++) {

            int s = dir[i].indexOf('-') + 1;
            int e = dir[i].indexOf('.');
            String d = dir[i].substring(s, e);

            // Ignore this file if it doesn't look like a date.
            if( d.length() < 8 ) {
                continue;
            }
            
            ndx = dates.indexOf(d);

            if (ndx < 0) {

                cdt = GregorianCalendar.getInstance();

                try {

                    cdt.setTime(ViewerFrame.fileDateFormat.parse(
                            d.substring(0, 8)));

                    dates.add(new Date(cdt.getTimeInMillis()));

                } catch (java.text.ParseException ex) {
                    // add nothing
                }
            }
        }
        Collections.sort(dates);
        dirty = false;
    }


    /**
     *  Get the current date list. Update the date list before returning if
     *  internal values have changed.
     *
     *@return    The dates value
     */
    public Vector getDates() {
        if (dirty) {
            updateDates();
        }
        return dates;
    }


    /**
     *  Set the internal path.
     *
     *@param  path  The new path value
     */
    public void setPath(String path) {
        this.path = path;
        dirty = true;
    }


    /**
     *  Get the internal path value.
     *
     *@return    The path value
     */
    public String getPath() {
        return path;
    }


    /**
     *  Set the internal filter value.
     *
     *@param  filter  The new filter value
     */
    public void setFilter(String filter) {
        this.filter = filter;
        dirty = true;
    }


    /**
     *  Get the internal filter value.
     *
     *@return    The filter value
     */
    public String getFilter() {
        return filter;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int size() {
        return dates.size();
    }


    /**
     *  Description of the Method
     *
     *@param  i  Description of the Parameter
     *@return    Description of the Return Value
     */
    public Date get(int i) {
        if (dates.size() > 0) {
            return ((Date) dates.get(i));
        } else {
            return null;
        }
    }


    /**
     *  Debugging output the date list to standard output.
     */
    public void toConsole() {
        if (dirty) {
            updateDates();
        }
        if (dates.size() > 0) {
            for (int i = 0; i < dates.size(); i++) {
                //System.out.println(dates.get(i));
                System.out.println(((Date) dates.get(i)).toString());
            }
        }
    }


    /**
     *  Unit testing, and debugging.
     *
     *@param  args  The command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Date List");
        for (int i = 0; i < args.length; i++) {
            System.out.println("Arg[" + i + "]: " + args[i]);
        }
        DateFilesExistList ddl = new DateFilesExistList(
                "/home/wduncan/freeguide-tv/data/",
                "^tv-\\d{8}\\.xmltv$");
        ddl.toConsole();
    }
}

