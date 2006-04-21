package freeguide.plugins.program.freeguide.viewer;

import freeguide.plugins.program.freeguide.FreeGuide;

import freeguide.plugins.program.freeguide.gui.AboutFrame;

import freeguide.plugins.program.freeguide.options.OptionsDialog;

import freeguide.plugins.program.freeguide.updater.UpdaterController;

import freeguide.plugins.program.freeguide.wizard.FirstTimeWizard;

import freeguide.common.lib.fgspecific.Application;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;

import freeguide.common.lib.general.FileHelper;
import freeguide.common.lib.general.Utils;

import freeguide.common.plugininterfaces.IModuleExport;
import freeguide.common.plugininterfaces.IModuleImport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.util.Locale;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Menu handler for MainController
 *
 * @author Alex Buloichik
 */
public class MenuHandler
{

    protected MainController controller;

    protected MenuHandler( final MainController controller )
    {
        this.controller = controller;

        controller.mainFrame.getMenuItemExit(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {

                    WindowListener[] listeners =
                        controller.mainFrame.getWindowListeners(  );

                    for( int i = 0; i < listeners.length; i++ )
                    {
                        listeners[i].windowClosing( 
                            new WindowEvent( 
                                controller.mainFrame,
                                WindowEvent.WINDOW_CLOSING ) );
                    }

                    controller.mainFrame.dispose(  );
                }
            } );

        controller.mainFrame.getMenuItemOptions(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {

                    OptionsDialog dg =
                        new OptionsDialog( controller.mainFrame );
                    Utils.centreDialog( controller.mainFrame, dg );

                    boolean updated = dg.showDialog(  );

                    if( updated )
                    {
                        controller.saveConfigNow(  );
                        FreeGuide.saveConfig(  );

                        controller.setLookAndFeel(  );

                        FreeGuide.setLocale( FreeGuide.config.lang );

                        MainController.remindersReschedule(  );

                        controller.viewer.onDataChanged(  );
                    }
                }
            } );

        controller.mainFrame.getMenuItemDownload(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    controller.doStartGrabbers(  );

                }
            } );

        controller.mainFrame.getMenuItemPrint(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    controller.doPrint(  );

                }
            } );

        controller.mainFrame.getMenuItemChannelsSets(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    controller.doEditChannelsSets(  );

                }
            } );

        controller.mainFrame.getMenuItemUserGuide(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    FileHelper.openFile( 
                        FreeGuide.runtimeInfo.docDirectory + "/userguide.html" );
                }
            } );

        controller.mainFrame.getMenuItemAbout(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    new AboutFrame( controller.mainFrame, true ).setVisible( 
                        true );

                }
            } );

        controller.mainFrame.getMenuItemWizard(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    new FirstTimeWizard( true );
                }
            } );

        controller.mainFrame.getMenuItemUpdater(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {

                    if( FreeGuide.arguments.containsKey( "no-plugin-manager" ) )
                    {
                        JOptionPane.showMessageDialog( 
                            controller.mainFrame,
                            Application.getInstance(  ).getLocalizedMessage( 
                                "UpdateManager.Disabled.Text" ),
                            Application.getInstance(  ).getLocalizedMessage( 
                                "UpdateManager.Disabled.Header" ),
                            JOptionPane.ERROR_MESSAGE );
                    }
                    else
                    {
                        new UpdaterController( controller.mainFrame ).run(  );
                    }
                }
            } );

        PluginInfo[] exporters = PluginsManager.getExporters(  );

        if( ( exporters == null ) || ( exporters.length == 0 ) )
        {
            controller.mainFrame.getMenuItemExport(  ).setVisible( false );
        }
        else
        {

            for( int i = 0; i < exporters.length; i++ )
            {

                final IModuleExport ex =
                    (IModuleExport)exporters[i].getInstance(  );
                final JMenuItem item =
                    new JMenuItem( 
                        exporters[i].getName( Locale.getDefault(  ) ) );
                controller.mainFrame.getMenuItemExport(  ).add( item );
                item.addActionListener( 
                    new ActionListener(  )
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            controller.exportTo( ex );
                        }
                    } );
            }
        }

        PluginInfo[] importers = PluginsManager.getImporters(  );

        if( ( importers == null ) || ( importers.length == 0 ) )
        {
            controller.mainFrame.getMenuItemImport(  ).setVisible( false );
        }
        else
        {

            for( int i = 0; i < importers.length; i++ )
            {

                final IModuleImport im =
                    (IModuleImport)importers[i].getInstance(  );
                final JMenuItem item =
                    new JMenuItem( 
                        importers[i].getName( Locale.getDefault(  ) ) );
                controller.mainFrame.getMenuItemImport(  ).add( item );
                item.addActionListener( 
                    new ActionListener(  )
                    {
                        public void actionPerformed( ActionEvent e )
                        {
                            controller.importFrom( im );
                        }
                    } );
            }
        }
    }
}
