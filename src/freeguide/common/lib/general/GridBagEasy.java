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
package freeguide.common.lib.general;

import java.awt.*;

/*
 *  A nice easy and concise way of handling a GridBagLayout.
 *
 * @author     Andy Balaam
 * @created    10 Dec 2003
 * @version    2
 */
/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
  */
public class GridBagEasy
{
    // ----------------------------------------
    /** DOCUMENT ME! */
    public final static int FILL_NONE = GridBagConstraints.NONE;

    /** DOCUMENT ME! */
    public final static int FILL_BOTH = GridBagConstraints.BOTH;

    /** DOCUMENT ME! */
    public final static int FILL_HOR = GridBagConstraints.HORIZONTAL;

    /** DOCUMENT ME! */
    public final static int FILL_VER = GridBagConstraints.VERTICAL;

    /** DOCUMENT ME! */
    public final static int ANCH_CENTER = GridBagConstraints.CENTER;

    /** DOCUMENT ME! */
    public final static int ANCH_NORTH = GridBagConstraints.NORTH;

    /** DOCUMENT ME! */
    public final static int ANCH_EAST = GridBagConstraints.EAST;

    /** DOCUMENT ME! */
    public final static int ANCH_SOUTH = GridBagConstraints.SOUTH;

    /** DOCUMENT ME! */
    public final static int ANCH_WEST = GridBagConstraints.WEST;

    // ----------------------------------------
    private GridBagConstraints gbc = new GridBagConstraints(  );
    private Container container;

    /** DOCUMENT ME! */
    public int default_anchor = ANCH_CENTER;

    /** DOCUMENT ME! */
    public int default_fill = FILL_NONE;

    /** DOCUMENT ME! */
    public int default_gridheight = 1;

    /** DOCUMENT ME! */
    public int default_gridwidth = 1;

    /** DOCUMENT ME! */
    public Insets default_insets = new Insets( 3, 3, 3, 3 );

    /** DOCUMENT ME! */
    public int default_ipadx = 2;

    /** DOCUMENT ME! */
    public int default_ipady = 2;

    /** DOCUMENT ME! */
    public int default_weightx = 0;

    /** DOCUMENT ME! */
    public int default_weighty = 0;

/**
     * Creates a new GridBagEasy object.
     *
     * @param container DOCUMENT ME!
     */
    public GridBagEasy( Container container )
    {
        this.container = container;

        container.setLayout( new GridBagLayout(  ) );

    }

    // ----------------------------------------------
    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     */
    public void add( Component comp, int gridx, int gridy )
    {
        gbc.anchor = default_anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = default_weightx;

        gbc.weighty = default_weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param anchor DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     */
    public void addAWXWY( 
        Component comp, int gridx, int gridy, int anchor, double weightx,
        double weighty )
    {
        gbc.anchor = anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param anchor DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     */
    public void addAWX( 
        Component comp, int gridx, int gridy, int anchor, double weightx )
    {
        gbc.anchor = anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = default_weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param fill DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     */
    public void addFWX( 
        Component comp, int gridx, int gridy, int fill, double weightx )
    {
        gbc.anchor = default_anchor;

        gbc.fill = fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = default_weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param anchor DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param gridwidth DOCUMENT_ME!
     */
    public void addAWXGW( 
        Component comp, int gridx, int gridy, int anchor, double weightx,
        int gridwidth )
    {
        gbc.anchor = anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = default_weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     * @param gridwidth DOCUMENT_ME!
     */
    public void addWXWYGW( 
        Component comp, int gridx, int gridy, double weightx, double weighty,
        int gridwidth )
    {
        gbc.anchor = default_anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     */
    public void addWXWY( 
        Component comp, int gridx, int gridy, double weightx, double weighty )
    {
        gbc.anchor = default_anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param fill DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     * @param gridwidth DOCUMENT_ME!
     */
    public void addFWXWYGW( 
        Component comp, int gridx, int gridy, int fill, double weightx,
        double weighty, int gridwidth )
    {
        gbc.anchor = default_anchor;

        gbc.fill = fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param anchor DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param ipadx DOCUMENT_ME!
     * @param ipady DOCUMENT_ME!
     */
    public void addAWXPXPY( 
        Component comp, int gridx, int gridy, int anchor, double weightx,
        int ipadx, int ipady )
    {
        gbc.anchor = anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = ipadx;

        gbc.ipady = ipady;

        gbc.weightx = weightx;

        gbc.weighty = default_weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param fill DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     */
    public void addFWXWY( 
        Component comp, int gridx, int gridy, int fill, double weightx,
        double weighty )
    {
        gbc.anchor = default_anchor;

        gbc.fill = fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param fill DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     * @param gridwidth DOCUMENT_ME!
     * @param gridheight DOCUMENT_ME!
     */
    public void addFWXWYGWGH( 
        Component comp, int gridx, int gridy, int fill, double weightx,
        double weighty, int gridwidth, int gridheight )
    {
        gbc.anchor = default_anchor;

        gbc.fill = fill;

        gbc.gridheight = gridheight;

        gbc.gridwidth = gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param weighty DOCUMENT_ME!
     */
    public void addWY( Component comp, int gridx, int gridy, double weighty )
    {
        gbc.anchor = default_anchor;

        gbc.fill = default_fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = default_weightx;

        gbc.weighty = weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param comp DOCUMENT_ME!
     * @param gridx DOCUMENT_ME!
     * @param gridy DOCUMENT_ME!
     * @param anchor DOCUMENT_ME!
     * @param fill DOCUMENT_ME!
     * @param weightx DOCUMENT_ME!
     */
    public void addAFWX( 
        Component comp, int gridx, int gridy, int anchor, int fill,
        double weightx )
    {
        gbc.anchor = anchor;

        gbc.fill = fill;

        gbc.gridheight = default_gridheight;

        gbc.gridwidth = default_gridwidth;

        gbc.insets = default_insets;

        gbc.ipadx = default_ipadx;

        gbc.ipady = default_ipady;

        gbc.weightx = weightx;

        gbc.weighty = default_weighty;

        gbc.gridx = gridx;

        gbc.gridy = gridy;

        container.add( comp, gbc );

    }
}
