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
package freeguide.gui.wizard;

import freeguide.*;

import freeguide.lib.fgspecific.Application;

import javax.swing.JFileChooser;

/**
 * A JPanel to go on a FreeGuideWizard to choose a directory. Inherits from
 * the file chooser but sends "true" to the browse() method.
 *
 * @author Andy Balaam
 * @version 1
 */
public class DirectoryWizardPanel extends AbstractFileWizardPanel
{

    /**
     * Create a new FreeGuideDirectoryWizardPanel. This is almost, but not
     * quite identical to a FreeGuideFileWizardPanel.
     */
    public DirectoryWizardPanel(  )
    {
        super(  );

    }

    /**
     * Gets the fileSelectionMode attribute of the
     * FreeGuideDirectoryWizardPanel object
     *
     * @return The fileSelectionMode value
     */
    protected int getFileSelectionMode(  )
    {

        return JFileChooser.DIRECTORIES_ONLY;

    }

    /**
     * Gets the fileChooserMessage attribute of the
     * FreeGuideDirectoryWizardPanel object
     *
     * @return The fileChooserMessage value
     */
    protected String getFileChooserMessage(  )
    {

        return Application.getInstance(  ).getLocalizedMessage( 
            "choose_directory" );

    }
}
