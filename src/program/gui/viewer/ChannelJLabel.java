/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 *  Created on Jun 24, 2004
 */

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

/**
 * @author Yann Coupin
 * @version 2
 *
 * A class to store the channel information and maintain them together
 */
public class ChannelJLabel extends JLabel implements ComponentListener{

	private String id;
	private String name;
	private String cacheFileName;
	private String currentIconFileName;

	/**
	 * Reset the icon to the default one of the channel (or none if no default icon)
	 */
	public void setDefaultIcon() {
    	setIcon(cacheFileName);
    	currentIconFileName = cacheFileName;
	}
	
	/**
	 * Get the cache file path that stores the default icon
	 * @return the path to the cache file
	 */
	public String getCacheIconPath() {
		return cacheFileName;
	}
	
	/**
	 * Set the icon of the channel
	 * @param fileName the path to the image to use as an icon
	 */
	public void setIcon(String fileName) {
		currentIconFileName = fileName;
		// Create the icon from the file
		ImageIcon icon = new ImageIcon(fileName);
		/* Verify the icon isn't taller than the pannel, resize otherwise.
		 * We don't check the width as it isn't usually a problem
		 */ 
		int width;
		int maxheight = this.getHeight()-this.getInsets().top-this.getInsets().bottom;
		//System.err.println(icon.getIconHeight()+" - "+maxheight);
		if (icon.getIconHeight()>maxheight) {
			width =icon.getIconWidth()*maxheight/icon.getIconHeight();
			super.setIcon(new ImageIcon(icon.getImage().getScaledInstance(width,maxheight,Image.SCALE_AREA_AVERAGING)));
		}
		else
			super.setIcon(icon);
	}
	
	/**
	 * @param id the Id of the channel
	 * @param name the displayed name of the channel
	 */
	public ChannelJLabel(String id, String name) {
		super(name);
		this.id = id;
		this.name = name;
		
		// Compute the cache fileName
		StringBuffer sb = getIconCacheDir();
		sb.append( id.replace( '.', '_' ).replaceAll( "[^a-zA-Z0-9_]","-" ) );
		cacheFileName = sb.toString();
		addComponentListener(this);
	}
    
    public static StringBuffer getIconCacheDir() {
        
        StringBuffer ans = new StringBuffer(
            FreeGuide.prefs.performSubstitutions(
				FreeGuide.prefs.misc.get("working_directory") ) );
		ans.append(File.separatorChar).append("iconcache")
            .append(File.separatorChar);
        
        return ans;
        
    }
    
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return Returns the name.
	 */
	public String toString() {
		return name;
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent e) {
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
	 */
	public void componentResized(ComponentEvent e) {
		if (currentIconFileName != null)
			setIcon(currentIconFileName);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent e) {
	}

	/* (non-Javadoc)
	 * @see javax.swing.JLabel#setIcon(javax.swing.Icon)
	 */
	public void setIcon(Icon icon) {
		currentIconFileName = null;
		super.setIcon(icon);
	}
    
    public int getRequiredWidth() {
        
        FontMetrics myFM = this.getFontMetrics( getFont() );
        int ans = myFM.stringWidth( getText() );
        
        Icon ic = getIcon();
        if( ic != null ) {
            ans += ic.getIconWidth();
        }
        
        return ans;
        
    }
    
}
