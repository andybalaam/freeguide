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

package freeguide.lib.fgspecific;

/**
 * @author ycoupin
 *
 * A class to store the info of a channel (named : ID, name, Icon's URL)
 */
public class Channel implements Comparable{
	
	private String ID;
	private String Name;
	private String IconURL;
	private String cacheIconFileName;
	private String currentIconFileName;

	/**
	 * Creates a Channel object with it's id, name and icon's URL
	 * @param id the channel's id as in the xmltv's file
	 * @param name name of the channel
	 * @param iconURL icon's URL
	 */
	public Channel(String id, String name, String iconURL) {
		ID = id;
		Name = name;
		IconURL = iconURL;
		
		// Compute the cache fileName
		if (IconURL != null) {
			StringBuffer sb = FGPreferences.getIconCacheDir();
			sb.append( ID.replace( '.', '_' ).replaceAll( "[^a-zA-Z0-9_]","-" ) );
			cacheIconFileName = sb.toString();
			currentIconFileName = cacheIconFileName;
		}
		else {
			cacheIconFileName = null;
			currentIconFileName = null;
		}
		
	}
	/**
	 * Creates a channel object using it's sole id, name it using the id,
	 * icon is null
	 * @param id the channel's id as in the xmltv's file
	 */
	public Channel(String id) {
		this(id, id, null);
	}
	/**
	 * @return Returns the iconURL.
	 */
	public String getIconURL() {
		return IconURL;
	}
	/**
	 * @return Returns the iD.
	 */
	public String getID() {
		return ID;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return Name;
	}
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		if (arg0 != null) {
			if (arg0 instanceof Channel)
				return ((Channel)arg0).getID().compareTo(ID);
			if (arg0 instanceof String)
				return ((String)arg0).compareTo(ID);
		}
		return 0;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (arg0 != null) {
			if (arg0 instanceof Channel)
				return ((Channel)arg0).getID().equals(ID);
			if (arg0 instanceof String)
				return ((String)arg0).equals(ID);
		}
		return false;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return Name;
	}
	/**
	 * @return Returns the cacheIconFileName.
	 */
	public String getIconFileName() {
		return currentIconFileName;
	}
	
	public void deleteCustomIcon() {
		currentIconFileName = cacheIconFileName;
	}
	
	public void setCustomIcon(String fileName) {
		currentIconFileName = fileName;
	}
}
