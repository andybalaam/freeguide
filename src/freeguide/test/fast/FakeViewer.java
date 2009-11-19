package freeguide.test.fast;

import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import freeguide.common.base.IModuleConfigurationUI;
import freeguide.common.plugininterfaces.IModuleViewer;
import freeguide.common.plugininterfaces.IModuleStorage.Info;

public class FakeViewer implements IModuleViewer
{
    public Info info = new Info();

    public void close()
    {
    }

    public JButton getDefaultButton()
    {
        return null;
    }

    public Info getDisplayedInfo()
    {
        return info;
    }

    public JPanel getPanel()
    {
        return null;
    }

    public void onChannelsSetsChanged()
    {
    }

    public void onDataChanged()
    {
    }

    public void open()
    {
    }

    public void printHTML()
    {
    }

    public void redraw()
    {
    }

    public void redrawCurrentProgramme()
    {
    }

    public Object getConfig()
    {
        return null;
    }

    public IModuleConfigurationUI getConfigurationUI( JDialog parentDialog )
    {
        return null;
    }

    public String getI18nName()
    {
        return null;
    }

    public ResourceBundle getLocalizer()
    {
        return null;
    }

    public void reloadResourceBundle() throws Exception
    {
    }
}
