/**
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
 
import javax.swing.JFileChooser;
 
/**
 * A JPanel to go on a FreeGuideWizard to choose a directory.  Inherits from
 * the file chooser but sends "true" to the browse() method.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class FreeGuideFileWizardPanel extends FreeGuideAbstractFileWizardPanel {
	
	/**
	 * Create a new FreeGuideDirectoryWizardPanel.  This is almost, but not quite
	 * identical to a FreeGuideFileWizardPanel.
	 */
	public FreeGuideFileWizardPanel() {
		super();
	}
	
	protected int getFileSelectionMode() {
		return JFileChooser.FILES_AND_DIRECTORIES;
	}
	
	protected String getFileChooserMessage() {
		return "Choose File";
	}
	
}
