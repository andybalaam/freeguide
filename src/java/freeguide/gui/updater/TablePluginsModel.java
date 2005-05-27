package freeguide.gui.updater;

import freeguide.FreeGuide;

import freeguide.lib.updater.data.PluginPackage;
import freeguide.lib.updater.data.PluginsRepository;

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

    final protected List rows = new ArrayList(  );

    /**
     * Creates a new TablePluginsModel object based on runned plugins.
     */
    public TablePluginsModel(  )
    {
        rows.add( "Program" );
        rows.add( "Grabbers" );
        rows.add( "Exporters" );
    }

    /**
     * Creates a new TablePluginsModel object based on repository information.
     *
     * @param repository repository description
     */
    public TablePluginsModel( final PluginsRepository repository )
    {
        addSubList( 
            "Program:", PluginsRepository.PACKAGE_TYPE_PROGRAM, repository );
        addSubList( 
            "Grabbers:", PluginsRepository.PACKAGE_TYPE_GRABBER, repository );
        addSubList( 
            "Import/Export:", PluginsRepository.PACKAGE_TYPE_IMPEXP, repository );
        addSubList( 
            "Other:", PluginsRepository.PACKAGE_TYPE_OTHER, repository );
    }

    protected void addSubList( 
        final String title, final String type,
        final PluginsRepository repository )
    {
        rows.add( title );

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

                        return pkg0.getName( "en" ).compareTo( 
                            pkg1.getName( "en" ) );
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

        return 2;
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

        switch( column )
        {

        case 0:
            return "Module";

        case 1:
            return "State";
        }

        return null;
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

        switch( column )
        {

        case 0:
            return ( pkg != null ) ? pkg.getName( "en" ) : rowObject;

        case 1:

            if( pkg != null )
            {

                if( pkg.isInstalled(  ) )
                {

                    if( pkg.isMarkedForRemove(  ) )
                    {

                        return "Will be removed";
                    }
                    else
                    {

                        try
                        {

                            if( pkg.needToUpdate(  ) )
                            {

                                return "Installed, need to upgrade";
                            }
                            else
                            {

                                return "Installed";
                            }
                        }
                        catch( Exception ex )
                        {
                            FreeGuide.log.warning( 
                                "Error check package for update : "
                                + pkg.getID(  ) );
                        }
                    }
                }
                else
                {

                    if( pkg.isMarkedForInstall(  ) )
                    {

                        return "Will be installed";
                    }
                    else
                    {

                        return "";
                    }
                }
            }
            else
            {

                return "";
            }
        }

        return null;
    }
}
