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
package freeguide.plugins.program.freeguide.wizard;

import freeguide.*;

import freeguide.common.lib.fgspecific.Application;

import javax.swing.JFileChooser;

/**
 * A JPanel to go on a FreeGuideWizard to choose a directory. Inherits from
 * the file chooser but sends "true" to the browse() method.
 *
 * @author Andy Balaam
 * @version 1
 */
public class FileWizardPanel extends AbstractFileWizardPanel
{

    /**
     * Create a new FreeGuideDirectoryWizardPanel. This is almost, but not
     * quite identical to a FreeGuideFileWizardPanel.
     */
    public FileWizardPanel(  )
    {
        super(  );

    }

    /**
     * Gets the fileSelectionMode attribute of the FreeGuideFileWizardPanel
     * object
     *
     * @return The fileSelectionMode value
     */
    protected int getFileSelectionMode(  )
    {

        return JFileChooser.FILES_AND_DIRECTORIES;

    }

    /**
     * Gets the fileChooserMessage attribute of the FreeGuideFileWizardPanel
     * object
     *
     * @return The fileChooserMessage value
     */
    protected String getFileChooserMessage(  )
    {

        return Application.getInstance(  ).getLocalizedMessage( "choose_file" );

    }
}
