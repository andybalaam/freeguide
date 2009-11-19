package freeguide.common.plugininterfaces;

import freeguide.common.base.IModule;
import freeguide.common.lib.fgspecific.data.TVData;

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
     * @throws Exception
     */
    void exportData( final TVData data, final JFrame parent )
        throws Exception;
}
