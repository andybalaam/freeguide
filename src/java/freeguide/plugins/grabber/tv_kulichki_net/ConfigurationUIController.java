package freeguide.plugins.grabber.tv_kulichki_net;

import freeguide.plugins.IModuleConfigurationUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class ConfigurationUIController implements IModuleConfigurationUI
{

    final protected GrabberKulichki parent;
    final protected ConfigurationUIPanel panel;
    final protected Config config;

    /**
     * Creates a new ConfigurationUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public ConfigurationUIController( final GrabberKulichki parent )
    {
        this.parent = parent;

        this.config = (Config)parent.config.clone(  );

        panel = new ConfigurationUIPanel( parent.getLocalizer(  ) );

        panel.getTreeChannels(  ).setData( config.channels );

        panel.getBtnRefresh(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    try
                    {
                        config.channels.allChannels =
                            parent.getChannelsList(  );

                        config.channels.normalize(  );

                        panel.getTreeChannels(  ).setData( config.channels );

                    }

                    catch( Exception ex )
                    {
                        ex.printStackTrace(  );

                    }
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {

        return panel;

    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
        config.channels.selectedChannelIDs.clear(  );

    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        parent.config = config;

        parent.saveConfig(  );

    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }
}
