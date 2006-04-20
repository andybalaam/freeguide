package freeguide.common.plugins;

import java.io.File;

import javax.swing.JFrame;

/**
 * Load data from file(s). Each import module should implement this interface.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleImport extends IModule
{

    /**
     * Load data by UI.
     *
     * @param inFile
     *
     * @throws IOException
     */
    void importDataUI( final JFrame parent, final IStoragePipe storage )
        throws Exception;

    /**
     * Load data from file.
     *
     * @param inFile
     *
     * @throws IOException
     */
    void importData( final File path, final IStoragePipe storage )
        throws Exception;
}
