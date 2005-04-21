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
package freeguide.plugins.ui.horizontal;

import freeguide.lib.fgspecific.data.TVChannel;

import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * DOCUMENT ME!
 *
 * @author Yann Coupin
 * @version 2 A class to store the channel information and maintain them together
 */
public class ChannelJLabel extends JLabel implements ComponentListener
{

    private TVChannel chan;

    /**
     * DOCUMENT ME!
     *
     * @param chan the Id of the channel
     */
    public ChannelJLabel( TVChannel chan )
    {
        super( chan.getDisplayName(  ) );

        this.chan = chan;

        addComponentListener( this );

    }

    /**
     * Reset the icon to the default one of the channel (or none if no default
     * icon)
     */
    public void setDefaultIcon(  )
    {

        //todo chan.deleteCustomIcon(  );
        //todo setIcon( chan.getIconFileName(  ) );
    }

    /**
     * Get the cache file path that stores the default icon
     *
     * @param fileName DOCUMENT ME!
     * @param height DOCUMENT ME!
     */

    /*    public String getCacheIconPath() {


        return chan.getIconFileName();


    }*/

    /**
     * Set the icon of the channel
     *
     * @param fileName the path to the image to use as an icon
     * @param height DOCUMENT ME!
     */
    public void setIcon( String fileName, final int height )
    {

        //doto chan.setCustomIcon( fileName );
        // Create the icon from the file
        ImageIcon icon = new ImageIcon( fileName );

        // Force the icon to have proportions 1.36 x 1
        int icon_width = icon.getIconWidth(  );

        int icon_height = icon.getIconHeight(  );

        int new_icon_width = -1;

        int new_icon_height = -1;

        int max_height = height;

        int max_width = (int)( 1.37 * (double)max_height );

        double new_over_old = (double)max_width / (double)icon_width;

        new_icon_width = max_width;

        new_icon_height = (int)( (double)icon_height * new_over_old );

        if( new_icon_height > max_height )
        {

            double new_over_old2 =
                (double)max_height / (double)new_icon_height;

            new_icon_height = max_height;

            new_icon_width = (int)( (double)max_width * new_over_old2 );

        }

        super.setIcon( 
            new ImageIcon( 
                icon.getImage(  ).getScaledInstance( 
                    new_icon_width, new_icon_height, Image.SCALE_AREA_AVERAGING ) ) );

        //super.setIcon(icon);
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the channel object.
     */
    public TVChannel getChannel(  )
    {

        return chan;

    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the name.
     */
    public String toString(  )
    {

        return chan.toString(  );

    }

    /* (non-Javadoc)


    * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)


    */
    public void componentHidden( ComponentEvent e )
    {
    }

    /* (non-Javadoc)


    * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)


    */
    public void componentMoved( ComponentEvent e )
    {
    }

    /* (non-Javadoc)


    * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)


    */
    public void componentResized( ComponentEvent e )
    {

        if( chan.getIconFileName(  ) != null )
        {
            setIcon( chan.getIconFileName(  ), getHeight(  ) );
        }
    }

    /* (non-Javadoc)


    * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)


    */
    public void componentShown( ComponentEvent e )
    {
    }

    /* (non-Javadoc)


    * @see javax.swing.JLabel#setIcon(javax.swing.Icon)


    */
    public void setIcon( Icon icon )
    {
        super.setIcon( icon );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getRequiredWidth(  )
    {

        FontMetrics myFM = this.getFontMetrics( getFont(  ) );

        int ans = myFM.stringWidth( getText(  ) );

        Icon ic = super.getIcon(  );

        if( ic != null )
        {
            ans += ic.getIconWidth(  );

        }

        if( ans < 10 )
        {
            ans = 10;

        }

        return ans;

    }
}
