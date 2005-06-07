package freeguide.plugins;

import java.awt.Component;

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
     * @return UI component. Usually - JPanel.
     */
    Component getPanel(  );

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
}
