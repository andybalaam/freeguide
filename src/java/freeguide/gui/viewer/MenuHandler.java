package freeguide.gui.viewer;

import freeguide.FreeGuide;

import freeguide.gui.dialogs.AboutFrame;

import freeguide.gui.options.OptionsDialog;

import freeguide.gui.updater.UpdaterController;

import freeguide.gui.wizard.FirstTimeWizard;

import freeguide.lib.fgspecific.PluginsManager;

import freeguide.lib.general.StringHelper;
import freeguide.lib.general.Utils;

import freeguide.plugins.IModuleExport;
import freeguide.plugins.IModuleImport;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

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
                    controller.mainFrame.setVisible( false );
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
                        controller.saveConfig(  );
                        FreeGuide.saveConfig(  );

                        controller.setLookAndFeel(  );

                        if( 
                            !FreeGuide.msg.getLocale(  ).equals( 
                                    FreeGuide.config.lang ) )
                        {
                            FreeGuide.setLocale( FreeGuide.config.lang );
                        }

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

                    String cmd =
                        StringHelper.replaceAll( 
                            FreeGuide.config.browserCommand, "%filename%",
                            FreeGuide.runtimeInfo.docDirectory
                            + "/userguide.html" );
                    Utils.execNoWait( cmd );
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
                    new FirstTimeWizard( null, true, null );

                }
            } );

        controller.mainFrame.getMenuItemUpdater(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent e )
                {
                    new UpdaterController( controller.mainFrame ).run(  );
                }
            } );

        IModuleExport[] exporters = PluginsManager.getExporters(  );

        if( ( exporters == null ) || ( exporters.length == 0 ) )
        {
            controller.mainFrame.getMenuItemExport(  ).setVisible( false );
        }
        else
        {

            for( int i = 0; i < exporters.length; i++ )
            {

                final IModuleExport ex = exporters[i];
                final JMenuItem item =
                    new JMenuItem( exporters[i].getName(  ) );
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

        IModuleImport[] importers = PluginsManager.getImporters(  );

        if( ( importers == null ) || ( importers.length == 0 ) )
        {
            controller.mainFrame.getMenuItemImport(  ).setVisible( false );
        }
        else
        {

            for( int i = 0; i < importers.length; i++ )
            {

                final IModuleImport im = importers[i];
                final JMenuItem item =
                    new JMenuItem( importers[i].getName(  ) );
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
