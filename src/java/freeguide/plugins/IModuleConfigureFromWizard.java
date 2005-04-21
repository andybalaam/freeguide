package freeguide.plugins;

/**
 * Module CAN implement this interface if it can be configured from wizard.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public interface IModuleConfigureFromWizard
{

    /**
     * Calls when wizard select region.
     *
     * @param regionTree region description
     * @param runSelectChannels true if we need to select channels, false if
     *        we just need to set region
     */
    void configureFromWizard( String regionTree, boolean runSelectChannels );
}
