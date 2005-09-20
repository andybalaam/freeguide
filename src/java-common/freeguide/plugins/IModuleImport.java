package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVData;

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
     * @return data
     *
     * @throws IOException
     */
    TVData importDataUI( final JFrame parent ) throws Exception;

    /**
     * Load data from file.
     *
     * @param inFile
     *
     * @return data
     *
     * @throws IOException
     */
    TVData importData( final File path ) throws Exception;
}
