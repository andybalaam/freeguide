import javax.swing.JPanel;
import javax.swing.Scrollable;
import java.awt.Rectangle;
import java.awt.Dimension;

class FreeGuideInnerPanel extends JPanel implements Scrollable {

	public int getScrollableUnitIncrement(Rectangle r, int i1, int i2) {
		return r.width/10;
	}
	
	public int getScrollableBlockIncrement(Rectangle r, int i1, int i2) {
		return r.width;
	}
	
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}
	
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}
	
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}
	
}
