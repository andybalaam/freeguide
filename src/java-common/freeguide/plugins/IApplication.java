package freeguide.plugins;

import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.swing.JFrame;

/**
 * Interface for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public interface IApplication
{
    JFrame getApplicationFrame(  );

    IModuleStorage getDataStorage(  );

    IModuleViewer getViewer(  );

    List getChannelsSetsList(  );

    void doEditChannelsSets(  );

    void doStartGrabbers(  );

    void doPrint(  );

    Logger getLogger(  );

    TimeZone getTimeZone(  );

    String getLocalizedMessage( final String key );

    String getLocalizedMessage( final String key, final Object[] params );

    String getWorkingDirectory(  );

    String getInstallDirectory(  );

    boolean isUnix(  );

    IModuleReminder[] getReminders(  );

    void saveAllConfigs(  );

    String getBrowserCommand(  );
}
