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

import java.awt.*;

/*
 *  A nice easy and concise way of handling a GridBagLayout.
 *
 * @author     Andy Balaam
 * @created    10 Dec 2003
 * @version    1
 */

public class GridBagEasy {

	GridBagEasy( Container container ) {
		
		this.container = container;
		
		container.setLayout( new GridBagLayout() );
		
	}
	
	
	
	// ----------------------------------------------
	
	protected void add( Component comp, int gridx, int gridy ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		
		container.add( comp, gbc );
		
	}
	
	protected void addAWXWY( Component comp, int gridx, int gridy, int anchor,
			double weightx, double weighty ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.anchor = anchor;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		
		container.add( comp, gbc );
		
	}
	
	protected void addAWX( Component comp, int gridx, int gridy,
			int anchor, double weightx ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.anchor = anchor;
		gbc.weightx = weightx;
		
		container.add( comp, gbc );
		
	}
	
	protected void addFWX( Component comp, int gridx, int gridy, int fill,
			double weightx ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.fill = fill;
		gbc.weightx = weightx;
		
		container.add( comp, gbc );
		
	}
	
	protected void addAWXGW( Component comp, int gridx,
			int gridy, int anchor, double weightx, int gridwidth ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.anchor = anchor;
		gbc.weightx = weightx;
		gbc.gridwidth = gridwidth;
		
		container.add( comp, gbc );
		
	}
		
	protected void addWXWYGW( Component comp, int gridx,
			int gridy, double weightx, double weighty, int gridwidth ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.gridwidth = gridwidth;
		
		container.add( comp, gbc );
		
	}
	
	protected void addWXWY( Component comp, int gridx, int gridy,
			double weightx, double weighty ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		
		container.add( comp, gbc );
		
	}
	
	protected void addFWXWYGW( Component comp, int gridx, int gridy, int fill,
			double weightx, double weighty, int gridwidth ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.fill = fill;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		gbc.gridwidth = gridwidth;
		
		container.add( comp, gbc );
		
	}
	
	protected void addAWXPXPY( Component comp, int gridx, int gridy, int anchor,
			double weightx, int ipadx, int ipady ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.anchor = anchor;
		gbc.weightx = weightx;
		gbc.ipadx = ipadx;
		gbc.ipady = ipady;
		
		container.add( comp, gbc );
		
	}
	
	protected void addFWXWY( Component comp, int gridx, int gridy, int fill,
			double weightx, double weighty ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.fill = fill;
		gbc.weightx = weightx;
		gbc.weighty = weighty;
		
		container.add( comp, gbc );
		
	}
	
	protected void addWY( Component comp, int gridx, int gridy,
			double weighty ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.weighty = weighty;
		
		container.add( comp, gbc );
		
	}
	
	protected void addAFWX( Component comp, int gridx, int gridy,
			int anchor, int fill, double weightx ) {
		
		setDefaults();
		
		gbc.gridx = gridx;
		gbc.gridy = gridy;
		gbc.anchor = anchor;
		gbc.fill = fill;
		gbc.weightx = weightx;
		
		container.add( comp, gbc );
		
	}
	
	// -------------
	
	private void setDefaults() {
		
		gbc.anchor = default_anchor;
		gbc.fill = default_fill;
		gbc.gridheight = default_gridheight;
		gbc.gridwidth = default_gridwidth;
		gbc.insets = default_insets;
		gbc.ipadx = default_ipadx;
		gbc.ipady = default_ipady;
		gbc.weightx = default_weightx;
		gbc.weighty = default_weighty;
		
	}

	// ----------------------------------------
	
	private GridBagConstraints gbc = new GridBagConstraints();
	private Container container;
	
	public int default_anchor = ANCH_CENTER;
	public int default_fill = FILL_NONE;
	public int default_gridheight = 1;
	public int default_gridwidth = 1;
	public Insets default_insets = new Insets(3, 3, 3, 3);
	public int default_ipadx = 2;
	public int default_ipady = 2;
	public int default_weightx = 0;
	public int default_weighty = 0;
	
	// ----------------------------------------
	
	protected final static int FILL_NONE = GridBagConstraints.NONE;
	protected final static int FILL_BOTH = GridBagConstraints.BOTH;
	protected final static int FILL_HOR = GridBagConstraints.HORIZONTAL;
	protected final static int FILL_VER = GridBagConstraints.VERTICAL;
	
	protected final static int ANCH_CENTER = GridBagConstraints.CENTER;
	protected final static int ANCH_NORTH = GridBagConstraints.NORTH;
	protected final static int ANCH_EAST = GridBagConstraints.EAST;
	protected final static int ANCH_SOUTH = GridBagConstraints.SOUTH;
	protected final static int ANCH_WEST = GridBagConstraints.WEST;
	
}
