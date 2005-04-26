package freeguide.plugins.grabber.www_vsetv_com;

import freeguide.plugins.IModuleConfigurationUI;

import java.awt.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DefaultListModel;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class VsetvConfigurationUIController implements IModuleConfigurationUI
{

    protected static final String CHPREFIX = "ChannelGroup.";
    protected final GrabberVsetv parent;
    protected final VsetvConfigurationUIPanel panel;
    protected final List chs = new ArrayList(  );

    /**
     * Creates a new ConfigurationUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public VsetvConfigurationUIController( final GrabberVsetv parent )
    {
        this.parent = parent;

        panel = new VsetvConfigurationUIPanel( parent.getLocalizer(  ) );

        panel.getTextUser(  ).setText( parent.config.user );

        panel.getTextPass(  ).setText( parent.config.pass );

        DefaultListModel list = new DefaultListModel(  );

        int sel = -1;

        Iterator it = parent.getLocalizer(  ).getKeys(  ).iterator(  );

        while( it.hasNext(  ) )
        {

            String key = (String)it.next(  );

            if( key.startsWith( CHPREFIX ) )
            {

                String value =
                    parent.getLocalizer(  ).getLocalizedMessage( key );

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
            sel = chs.indexOf( "base" );

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
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        parent.config.isAuth = panel.getRbAuth(  ).isSelected(  );

        parent.config.user = panel.getTextUser(  ).getText(  );

        parent.config.pass = panel.getTextPass(  ).getText(  );

        parent.config.isGetAll = panel.getCbGetAll(  ).isSelected(  );

        parent.config.channelGroup =
            (String)chs.get( panel.getListChannels(  ).getSelectedIndex(  ) );

        parent.saveConfig(  );

    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }
}
