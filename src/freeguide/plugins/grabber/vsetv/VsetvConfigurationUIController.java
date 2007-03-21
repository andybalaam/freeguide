package freeguide.plugins.grabber.vsetv;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class VsetvConfigurationUIController implements IModuleConfigurationUI
{
    protected static final String CHPREFIX = "ChannelGroup.";
    protected final GrabberVsetv parent;
    protected VsetvConfigurationUIPanel panel;
    protected final List chs = new ArrayList(  );

/**
     * Creates a new ConfigurationUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public VsetvConfigurationUIController( final GrabberVsetv parent )
    {
        this.parent = parent;
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
            panel = new VsetvConfigurationUIPanel( parent.getLocalizer(  ) );

            panel.getTextUser(  ).setText( parent.config.user );

            panel.getTextPass(  ).setText( parent.config.pass );

            DefaultListModel list = new DefaultListModel(  );

            int sel = -1;

            Enumeration it = parent.getLocalizer(  ).getKeys(  );

            while( it.hasMoreElements(  ) )
            {
                String key = (String)it.nextElement(  );

                if( key.startsWith( CHPREFIX ) )
                {
                    String value = parent.getLocalizer(  ).getString( key );

                    key = key.substring( CHPREFIX.length(  ) );

                    list.addElement( value );

                    if( key.equals( parent.config.channelGroup ) )
                    {
                        sel = chs.size(  );

                    }

                    chs.add( key );

                }
            }

            panel.getListChannels(  ).setModel( list );

            if( sel == -1 )
            {
                sel = chs.indexOf( VsetvConfig.PREDEFINED_CHANNEL_GROUP );

            }

            panel.getListChannels(  ).setSelectedIndex( sel );

            if( parent.config.isAuth )
            {
                panel.getRbAuth(  ).setSelected( true );

            }

            else
            {
                panel.getRbNoAuth(  ).setSelected( true );

            }

            panel.getCbGetAll(  ).setSelected( parent.config.isGetAll );
        }

        return panel;
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        if( panel == null )
        {
            return;
        }

        parent.config.isAuth = panel.getRbAuth(  ).isSelected(  );

        parent.config.user = panel.getTextUser(  ).getText(  );

        parent.config.pass = panel.getTextPass(  ).getText(  );

        parent.config.isGetAll = panel.getCbGetAll(  ).isSelected(  );

        JList channels = panel.getListChannels(  );

        if( !channels.isSelectionEmpty(  ) )
        {
            parent.config.channelGroup = (String)chs.get( 
                    channels.getSelectedIndex(  ) );
        }
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
