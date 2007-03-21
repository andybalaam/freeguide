package freeguide.common.plugininterfaces;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.MutableTreeNode;

/**
 * Interface for setup module. Each module CAN return this interface for
 * configure through UI.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleConfigurationUI
{
    /**
     * Get component with configuration UI.
     *
     * @param leafName name of leaf in the optiona dialog
     * @param node DOCUMENT ME!
     * @param tree DOCUMENT ME!
     *
     * @return UI component. Usually - JPanel.
     */
    Component getPanel( String leafName, MutableTreeNode node, JTree tree );

    /**
     * Save selected settings.
     */
    void save(  );

    /**
     * Reset all values to defaults.
     */
    void resetToDefaults(  );

    /**
     * Cancel settings without save.
     */
    void cancel(  );

    /**
     * Get nodes for options dialog tree.
     *
     * @return nodes or null if need to add module name
     */
    String[] getTreeNodes(  );
}
