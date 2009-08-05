package freeguide.test.fast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import freeguide.common.plugininterfaces.FGLogger;
import freeguide.common.plugininterfaces.IApplication;
import freeguide.common.plugininterfaces.IExecutionController;
import freeguide.common.plugininterfaces.IModuleReminder;
import freeguide.common.plugininterfaces.IModuleStorage;
import freeguide.common.plugininterfaces.IModuleViewer;

/**
 * An Application implementation you can pass in to tests that does essentially
 * nothing.
 */
public class FakeApplication implements IApplication
{
    public ArrayList<IModuleReminder> reminders = new ArrayList<IModuleReminder>();

    public void doEditChannelsSets()
    {
    }

    public void doPrint()
    {
    }

    public void doStartGrabbers()
    {
    }

    public JButton getApplicationForegroundButton()
    {
        return null;
    }

    public JFrame getApplicationFrame()
    {
        return null;
    }

    public JProgressBar getApplicationProgressBar()
    {
        return null;
    }

    public String getBrowserCommand()
    {
        return null;
    }

    public List getChannelsSetsList()
    {
        return null;
    }

    public JFrame getCurrentFrame()
    {
        return null;
    }

    public IModuleStorage getDataStorage()
    {
        return null;
    }

    public IExecutionController getExecutionController()
    {
        return null;
    }

    public FGLogger getFGLogger()
    {
        return null;
    }

    public String getInstallDirectory()
    {
        return null;
    }

    public String getLibDirectory()
    {
        return null;
    }

    /**
     * Always returns the empty string
     */
    public String getLocalizedMessage( String key )
    {
        return "";
    }

    /**
     * Always returns the empty string
     */
    public String getLocalizedMessage( String key, Object[] params )
    {
        return "";
    }

    public Logger getLogger()
    {
        return null;
    }

    public IMainMenu getMainMenu()
    {
        return null;
    }

    public IModuleReminder[] getReminders()
    {
        return reminders.toArray( new IModuleReminder[0] );
    }

    public Locale[] getSupportedLocales()
    {
        return null;
    }

    public TimeZone getTimeZone()
    {
        return null;
    }

    public IModuleViewer getViewer()
    {
        return null;
    }

    public String getWorkingDirectory()
    {
        return null;
    }

    public boolean isUnix()
    {
        return false;
    }

    public void saveAllConfigs()
    {
    }

}
