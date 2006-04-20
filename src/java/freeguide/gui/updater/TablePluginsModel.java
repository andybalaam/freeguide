package freeguide.plugins.program.freeguide.updater;

import freeguide.common.lib.fgspecific.Application;

import freeguide.common.lib.updater.data.PluginPackage;
import freeguide.common.lib.updater.data.PluginsRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.table.DefaultTableModel;

/**
 * Table model for view plugins.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class TablePluginsModel extends DefaultTableModel
{

    protected final String[] COLUMNS =
        new String[]
        {
            " ",
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableColumns.Name" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableColumns.Version" ),
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableColumns.Status" )
        };
    final protected List rows = new ArrayList(  );

    /**
     * Creates a new TablePluginsModel object based on runned plugins.
     */
    public TablePluginsModel(  )
    {
    }

    /**
     * Creates a new TablePluginsModel object based on repository information.
     *
     * @param repository repository description
     */
    public TablePluginsModel( final PluginsRepository repository )
    {
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.Programme" ) + ":",
            PluginsRepository.PACKAGE_TYPE_APPLICATION, repository );
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.UI" ) + ":",
            PluginsRepository.PACKAGE_TYPE_UI, repository );
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.Grabbers" ) + ":",
            PluginsRepository.PACKAGE_TYPE_GRABBER, repository );
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.Storages" ) + ":",
            PluginsRepository.PACKAGE_TYPE_STORAGE, repository );
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.Reminders" ) + ":",
            PluginsRepository.PACKAGE_TYPE_REMINDER, repository );
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.ImpExp" ) + ":",
            PluginsRepository.PACKAGE_TYPE_IMPEXP, repository );
        addSubList( 
            Application.getInstance(  ).getLocalizedMessage( 
                "UpdateManager.TableGroupName.Other" ) + ":",
            PluginsRepository.PACKAGE_TYPE_OTHER, repository );
    }

    protected void addSubList( 
        final String title, final String type,
        final PluginsRepository repository )
    {
        rows.add( new SubListTitle( title ) );

        final List subList = repository.getPackagesByType( type );

        Collections.sort( 
            subList,
            new Comparator(  )
            {
                public int compare( Object arg0, Object arg1 )
                {

                    if( 
                        arg0 instanceof PluginPackage
                            && arg1 instanceof PluginPackage )
                    {

                        PluginPackage pkg0 = (PluginPackage)arg0;
                        PluginPackage pkg1 = (PluginPackage)arg1;

                        String name0 = pkg0.getName( "en" );
                        String name1 = pkg1.getName( "en" );

                        if( ( name0 != null ) && ( name1 != null ) )
                        {

                            return pkg0.getName( "en" ).compareTo( 
                                pkg1.getName( "en" ) );
                        }
                        else
                        {

                            return 0;
                        }
                    }

                    return 0;
                }
            } );
        rows.addAll( subList );
    }

    /**
     * Get columns count for model.
     *
     * @return columns count
     */
    public int getColumnCount(  )
    {

        return COLUMNS.length;
    }

    /**
     * Get rows count for model.
     *
     * @return rows count
     */
    public int getRowCount(  )
    {

        if( rows == null )
        {

            return 0;
        }

        return rows.size(  );
    }

    /**
     * Get columns name for model.
     *
     * @param column column index
     *
     * @return column name
     */
    public String getColumnName( int column )
    {

        return COLUMNS[column];
    }

    /**
     * DOCUMENT_ME!
     *
     * @param columnIndex DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Class getColumnClass( int columnIndex )
    {

        if( columnIndex == 0 )
        {

            return Boolean.class;
        }
        else
        {

            return super.getColumnClass( columnIndex );
        }
    }

    /**
     * Disable editing.
     *
     * @param row
     * @param column
     *
     * @return false
     */
    public boolean isCellEditable( int row, int column )
    {

        return false;
    }

    /**
     * Get cell text.
     *
     * @param row row index
     * @param column column index
     *
     * @return cell text
     */
    public Object getValueAt( int row, int column )
    {

        Object rowObject = rows.get( row );
        PluginPackage pkg = null;

        if( rowObject instanceof PluginPackage )
        {
            pkg = (PluginPackage)rowObject;
        }
        else
        {

            if( column == 1 )
            {

                return rowObject;
            }
            else
            {

                return null;
            }
        }

        switch( column )
        {

        case 0:

            if( pkg.isInstalled(  ) )
            {

                return Boolean.valueOf( !pkg.isMarkedForRemove(  ) );
            }
            else
            {

                return Boolean.valueOf( pkg.isMarkedForInstall(  ) );
            }

        case 1:
            return "    " + getName( pkg );

        case 2:
            return getVersion( pkg );

        case 3:
            return getStatus( pkg );
        }

        return null;
    }

    protected String getName( final PluginPackage pkg )
    {

        return pkg.getName( "en" );
    }

    protected String getCategory( final PluginPackage pkg )
    {

        return pkg.getType(  );
    }

    protected String getVersion( final PluginPackage pkg )
    {

        return pkg.getVersion(  ).getDotFormat(  );
    }

    protected String getStatus( final PluginPackage pkg )
    {

        if( pkg.isInstalled(  ) )
        {

            if( pkg.isMarkedForRemove(  ) )
            {

                return Application.getInstance(  ).getLocalizedMessage( 
                    "UpdateManager.Status.WillRemoved" );
            }
            else
            {

                if( pkg.needToUpdate(  ) )
                {

                    return Application.getInstance(  ).getLocalizedMessage( 
                        "UpdateManager.Status.WillUpdated" );
                }
                else
                {

                    return Application.getInstance(  ).getLocalizedMessage( 
                        "UpdateManager.Status.Installed" );
                }
            }
        }
        else
        {

            if( pkg.isMarkedForInstall(  ) )
            {

                return Application.getInstance(  ).getLocalizedMessage( 
                    "UpdateManager.Status.WillInstalled" );
            }
            else
            {

                return Application.getInstance(  ).getLocalizedMessage( 
                    "UpdateManager.Status.Removed" );
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class SubListTitle
    {

        protected final String title;

        /**
         * Creates a new SubListTitle object.
         *
         * @param title DOCUMENT ME!
         */
        public SubListTitle( final String title )
        {
            this.title = title;
        }

        /**
         * DOCUMENT_ME!
         *
         * @return DOCUMENT_ME!
         */
        public String toString(  )
        {

            return title;
        }
    }
}
