package freeguide.gui.options;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import freeguide.FreeGuide;
import freeguide.gui.viewer.MainController;
import freeguide.lib.general.LookAndFeelManager;
import freeguide.plugins.IModuleConfigurationUI;

/**
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PanelGeneralController implements IModuleConfigurationUI {
	PanelGeneralUI panel;
	public Component getPanel() {
		if (panel == null) {
			panel = new PanelGeneralUI();

			panel.getCbLF().setModel(new DefaultComboBoxModel(LookAndFeelManager.getAvailableLooksAndFeels(  ).toArray()));

			resetToDefaults();
		}
		return panel;
	}
	public void resetToDefaults() {
        panel.getTextWorkingDir().setText( FreeGuide.config.workingDirectory );

        LookAndFeel currentLAF = UIManager.getLookAndFeel(  );

        String defaultLAFName = "Metal";

        if( currentLAF != null )
        {
            defaultLAFName = currentLAF.getName(  );

        }

        panel.getCbLF().setSelectedItem( MainController.config.ui.LFname );
	}
	public void save() {
        FreeGuide.config.workingDirectory = panel.getTextWorkingDir().getText(  );

        MainController.config.ui.LFname =
        	panel.getCbLF().getSelectedItem(  ).toString(  );
	}
	public void cancel() {
	}
}
