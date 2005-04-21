package freeguide.plugins;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Interface for viewer modules.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public interface IModuleViewer extends IModule
{

    /**
     * Get panel for viewer. This panel will be displayed inside main frame.
     * Method calls AFTER setDataStorage and setMenu methods.
     *
     * @return JPanel
     */
    JPanel getPanel(  );

    /**
     * Method calls when application open viewer.
     *
     * @param parent DOCUMENT ME!
     */
    void open( Parent parent );

    /**
     * Method calls when application closes or viewer changed.
     */
    void close(  );

    void onDataChanged(  );

    void onFavouritesChanged(  );

    void onChannelsSetsChanged(  );

    void printHTML(  );

    JButton getDefaultButton(  );

    /**
     * Interface for parent of viewer.
     *
     * @author Alex Buloichik (alex73 at zaval.org)
     */
    public interface Parent
    {
        JFrame getApplicationFrame(  );

        IStorage getDataStorage(  );

        List getChannelsSetsList(  );

        void doEditChannelsSets(  );

        void doEditFavourites(  );

        void doStartGrabbers(  );

        void doPrint(  );
    }
}
