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
     */
    void open(  );

    /**
     * Method calls when application closes or viewer changed.
     */
    void close(  );

    void onDataChanged(  );

    void onChannelsSetsChanged(  );

    void redraw(  );

    void redrawPersonalizedGuide(  );

    void printHTML(  );

    JButton getDefaultButton(  );
}
