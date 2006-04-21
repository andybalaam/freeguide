package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class KulichkiConfigurationUIController
    implements IModuleConfigurationUI
{

    final protected GrabberKulichki parent;
    final protected KulichkiConfigurationUIPanel panel;
    final protected KulichkiConfig config;

    /**
     * Creates a new ConfigurationUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public KulichkiConfigurationUIController( final GrabberKulichki parent )
    {
        this.parent = parent;

        this.config = (KulichkiConfig)parent.config.clone(  );

        panel = new KulichkiConfigurationUIPanel( parent.getLocalizer(  ) );

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
                        Application.getInstance(  ).getLogger(  ).log( 
                            Level.WARNING,
                            "Error load channels list from tv.kulichki.net", ex );
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

    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }
}
