package freeguide.plugins.grabber.kulichki;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.logging.Level;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class KulichkiConfigurationUIController
    implements IModuleConfigurationUI
{
    final protected GrabberKulichki parent;
    protected KulichkiConfigurationUIPanel panel;
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
    }

    /**
     * DOCUMENT_ME!
     *
     * @param leafName DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param tree DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel( 
        String leafName, MutableTreeNode node, JTree tree )
    {
        if( panel == null )
        {
            panel = new KulichkiConfigurationUIPanel( parent.getLocalizer(  ) );

            panel.getTreeChannels(  ).setData( config.channels );

            panel.getBtnRefresh(  ).addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        try
                        {
                            config.channels.allChannels = parent
                                .getChannelsList(  );

                            config.channels.normalize(  );

                            panel.getTreeChannels(  ).setData( 
                                config.channels );
                        }
                        catch( Exception ex )
                        {
                            Application.getInstance(  ).getLogger(  )
                                       .log( 
                                Level.WARNING,
                                "Error load channels list from tv.kulichki.net",
                                ex );
                        }
                    }
                } );
        }

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

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String[] getTreeNodes(  )
    {
        return null;
    }
}
