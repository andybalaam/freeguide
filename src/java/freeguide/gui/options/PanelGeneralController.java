package freeguide.gui.options;

import freeguide.FreeGuide;

import freeguide.gui.viewer.MainController;

import freeguide.lib.general.LanguageHelper;
import freeguide.lib.general.LookAndFeelManager;

import freeguide.plugins.IModuleConfigurationUI;

import java.awt.Component;

import java.io.IOException;

import java.util.Locale;
import java.util.logging.Level;

import javax.swing.DefaultComboBoxModel;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class PanelGeneralController implements IModuleConfigurationUI
{

    PanelGeneralUI panel;
    Locale[] locales;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {

        if( panel == null )
        {
            panel = new PanelGeneralUI(  );

            panel.getCbLF(  ).setModel( 
                new DefaultComboBoxModel( 
                    LookAndFeelManager.getAvailableLooksAndFeels(  ).toArray(  ) ) );

            try
            {
                locales =
                    LanguageHelper.getLocaleList( 
                        this.getClass(  ).getClassLoader(  ),
                        "i18n/MessagesBundle" );

                String[] langNames = new String[locales.length];

                for( int i = 0; i < locales.length; i++ )
                {
                    langNames[i] =
                        locales[i].getDisplayName( 
                            FreeGuide.msg.getLocale(  ) );
                }

                panel.getCbLang(  ).setModel( 
                    new DefaultComboBoxModel( langNames ) );
            }
            catch( IOException ex )
            {
                FreeGuide.log.log( Level.SEVERE, "Error locading locale list" );
            }

            resetToDefaults(  );
        }

        return panel;
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
        panel.getTextWorkingDir(  ).setText( 
            FreeGuide.config.workingDirectory );

        LookAndFeel currentLAF = UIManager.getLookAndFeel(  );

        String defaultLAFName = "Metal";

        if( currentLAF != null )
        {
            defaultLAFName = currentLAF.getName(  );

        }

        panel.getCbLF(  ).setSelectedItem( MainController.config.ui.LFname );
        panel.getCbLang(  ).setSelectedItem( 
            FreeGuide.msg.getLocale(  ).getDisplayName( 
                FreeGuide.msg.getLocale(  ) ) );
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        FreeGuide.config.workingDirectory =
            panel.getTextWorkingDir(  ).getText(  );

        MainController.config.ui.LFname =
            panel.getCbLF(  ).getSelectedItem(  ).toString(  );

        if( panel.getCbLang(  ).getSelectedIndex(  ) != -1 )
        {

            //            FreeGuide.config.lang = locales[panel.getCbLang().getSelectedIndex()]; 
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }
}
