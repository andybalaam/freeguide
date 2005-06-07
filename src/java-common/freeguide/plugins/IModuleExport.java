package freeguide.plugins;

import freeguide.lib.fgspecific.data.TVData;

import java.io.IOException;

import javax.swing.JFrame;

/**
 * Save data to file(s).  Each export module should implement this interface.
 * TBD: we need some method for setup additional information for export.  For
 * example, for enter some additional parameters.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleExport extends IModule
{

    /**
     * Save data to file(s).
     *
     * @param data
     * @param parent
     *
     * @throws IOException
     */
    void exportData( final TVData data, final JFrame parent )
        throws IOException;
}