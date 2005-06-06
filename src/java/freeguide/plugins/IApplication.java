package freeguide.plugins;

import java.util.List;

import javax.swing.JFrame;

/**
 * Interface for retrieve information about main application.
 *
 * @author Alex Buloichik(alex73 at zaval.org)
 */
public interface IApplication
{
    JFrame getApplicationFrame(  );

    IStorage getDataStorage(  );

    List getChannelsSetsList(  );

    void doEditChannelsSets(  );

    void doStartGrabbers(  );

    void doPrint(  );

    void redraw(  );
}
