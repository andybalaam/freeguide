package freeguide.gui.updater;

import freeguide.FreeGuide;

import freeguide.lib.general.Utils;

import freeguide.lib.updater.RepositoryUtils;
import freeguide.lib.updater.data.PluginMirror;
import freeguide.lib.updater.data.PluginPackage;
import freeguide.lib.updater.data.PluginsRepository;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;

import javax.swing.JFrame;

/**
 * Update UI controller.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class UpdaterController
{

    protected final UpdaterUI ui;
    protected PluginsRepository repository;
    protected JFrame parent;

    /**
     * Creates a new UpdaterController object.
     *
     * @param parent DOCUMENT ME!
     */
    public UpdaterController( final JFrame parent )
    {
        ui = new UpdaterUI( parent );
        this.parent = parent;
    }

    protected void setGoButtonState(  )
    {

        if( repository != null )
        {
            ui.getBtnGo(  ).setEnabled( repository.needToUpdate(  ) );
        }
        else
        {
            ui.getBtnGo(  ).setEnabled( false );
        }
    }

    /**
     * Run mathod.
     */
    public void run(  )
    {
        ui.getBtnCheck(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    try
                    {
                        repository =
                            RepositoryUtils.downloadRepositoryInfo(  );
                        ui.getTablePackages(  ).setModel( 
                            new TablePluginsModel( repository ) );

                        ModifiedDefaultTableCellRenderer rend =
                            new ModifiedDefaultTableCellRenderer(  );

                        //                        rend.setHorizontalAlignment(JLabel.CENTER);
                        ui.getTablePackages(  ).getColumnModel(  ).getColumn( 
                            0 ).setMaxWidth( 
                            ui.getTablePackages(  ).getRowHeight(  ) + 10 );
                        ui.getTablePackages(  ).getColumnModel(  ).getColumn( 
                            0 ).setWidth( 
                            ui.getTablePackages(  ).getRowHeight(  ) );
                        ui.getTablePackages(  ).getColumnModel(  ).getColumn( 
                            1 ).setCellRenderer( rend );
                        ui.getTablePackages(  ).getColumnModel(  ).getColumn( 
                            2 ).setCellRenderer( rend );
                        ui.getTablePackages(  ).getColumnModel(  ).getColumn( 
                            3 ).setCellRenderer( rend );
                        ui.getTablePackages(  ).getColumnModel(  ).getColumn( 
                            4 ).setCellRenderer( rend );
                        ui.getTablePackages(  ).doLayout(  );
                        setGoButtonState(  );
                    }
                    catch( Exception ex )
                    {
                        ex.printStackTrace(  );
                    }
                }
            } );
        ui.getBtnClose(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    ui.dispose(  );
                }
            } );
        ui.getBtnGo(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    File dstDir =
                        new File( 
                            FreeGuide.config.workingDirectory + "/updates/" );
                    dstDir.mkdirs(  );

                    try
                    {
                        RepositoryUtils.downloadFiles( 
                            ( (PluginMirror)repository.getAllMirrors(  ).get( 
                                0 ) ).getPath(  ),
                            repository.getFilesForDownload(  ), dstDir );

                        RepositoryUtils.unzipPackages( 
                            dstDir,
                            new File( FreeGuide.runtimeInfo.installDirectory ) );
                    }
                    catch( Exception ex )
                    {
                        ex.printStackTrace(  );
                    }

                    dstDir.delete(  );
                }
            } );
        setGoButtonState(  );
        ui.getTablePackages(  ).setDefaultRenderer( 
            Boolean.class, new ModifiedBooleanRenderer(  ) );

        ui.getTablePackages(  ).setModel( new TablePluginsModel(  ) );

        ui.getTablePackages(  ).setRowSelectionAllowed( false );

        ui.getTablePackages(  ).addMouseListener( 
            new MouseListener(  )
            {
                public void mouseClicked( MouseEvent e )
                {

                    TablePluginsModel model =
                        (TablePluginsModel)ui.getTablePackages(  ).getModel(  );
                    Object rowObj =
                        model.rows.get( 
                            ui.getTablePackages(  ).rowAtPoint( 
                                e.getPoint(  ) ) );
                    int colIndex =
                        ui.getTablePackages(  ).columnAtPoint( e.getPoint(  ) );

                    if( rowObj instanceof PluginPackage && ( colIndex == 0 ) )
                    {

                        PluginPackage pkg = (PluginPackage)rowObj;

                        if( pkg.isInstalled(  ) )
                        {

                            if( pkg.isMarkedForRemove(  ) )
                            {
                                pkg.markOff(  );
                            }
                            else
                            {
                                pkg.markForRemove(  );
                            }
                        }
                        else
                        {

                            if( pkg.isMarkedForInstall(  ) )
                            {
                                pkg.markOff(  );
                            }
                            else
                            {
                                pkg.markForInstall(  );
                            }
                        }
                    }

                    ui.getTablePackages(  ).repaint(  );
                    setGoButtonState(  );
                }

                public void mouseEntered( MouseEvent e )
                {
                }

                public void mouseExited( MouseEvent e )
                {
                }

                public void mousePressed( MouseEvent e )
                {
                }

                public void mouseReleased( MouseEvent e )
                {
                }
            } );

        ui.pack(  );
        Utils.centreDialog( parent, ui );
        ui.setVisible( true );
        ui.dispose(  );
    }
}
