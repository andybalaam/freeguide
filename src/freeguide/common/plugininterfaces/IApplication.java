package freeguide.common.plugininterfaces;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JProgressBar;

/**
 * Interface for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public interface IApplication
{
    JFrame getApplicationFrame(  );

    JFrame getCurrentFrame(  );

    IModuleStorage getDataStorage(  );

    IModuleViewer getViewer(  );

    List getChannelsSetsList(  );

    void doEditChannelsSets(  );

    void doStartGrabbers(  );

    void doPrint(  );

    Logger getLogger(  );
    FGLogger getFGLogger(  );

    TimeZone getTimeZone(  );

    String getLocalizedMessage( final String key );

    String getLocalizedMessage( final String key, final Object[] params );

    String getWorkingDirectory(  );

    String getInstallDirectory(  );

    String getLibDirectory(  );

    boolean isUnix(  );

    IModuleReminder[] getReminders(  );

    void saveAllConfigs(  );

    String getBrowserCommand(  );

    IMainMenu getMainMenu(  );

    Locale[] getSupportedLocales(  );

/**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public interface IMainMenu
    {
        JMenu getTools(  );
    }

    IExecutionController getExecutionController();

    JProgressBar getApplicationProgressBar();

    JButton getApplicationForegroundButton();
}
