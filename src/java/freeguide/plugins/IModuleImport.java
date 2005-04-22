package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVData;

import java.io.File;
import java.io.IOException;

/**
 * Load data from file(s). Each import module should implement this interface.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleImport extends IModule
{

    /**
     * Load data from file.
     *
     * @param inFile
     *
     * @return data
     *
     * @throws IOException
     */
    TVData load( File inFile ) throws IOException;
}