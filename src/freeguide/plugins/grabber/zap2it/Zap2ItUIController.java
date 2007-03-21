package freeguide.plugins.grabber.zap2it;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

/**
 * Controller for zap2it configuration.
 *
 * @author Alex Buloichik
 */
public class Zap2ItUIController implements IModuleConfigurationUI
{
    protected Zap2ItUIPanel panel;
    protected final GrabberZap2It parent;

/**
     * Creates a new Zap2ItUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public Zap2ItUIController( final GrabberZap2It parent )
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
            panel = new Zap2ItUIPanel( parent.getLocalizer(  ) );
            panel.textUser.setText( parent.config.username );
            panel.textPass.setText( parent.config.password );
        }

        return panel;
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
        if( panel != null )
        {
            parent.config.username = panel.textUser.getText(  );
            parent.config.password = panel.textPass.getText(  );
        }
    }
}
