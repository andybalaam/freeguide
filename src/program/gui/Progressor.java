/**
 *  Progressor.java
 *
 *  FreeGuide J2
 * 
 *  Copyright (c) 2002 by Andy Balaam
 *  
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY. See the file COPYING for more information.
 *
 *  A screen which has a progress bar.
 * 
 *@author     Andy Balaam
 *@created    08 July 2003
 */

package freeguide.gui;

 
public interface Progressor {
	
    /**
     * Sets the progress to a percentage
     *
     */
    public void setProgress( int percent );

}
