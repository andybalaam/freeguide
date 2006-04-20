package freeguide.plugins.ui.vertical.simple.filter.gui;

import freeguide.common.lib.fgspecific.Application;

import freeguide.plugins.ui.vertical.simple.VerticalViewer;
import freeguide.plugins.ui.vertical.simple.filter.ProgrammeFilter;
import freeguide.plugins.ui.vertical.simple.filter.gui.helper.DataMenuItem;
import freeguide.plugins.ui.vertical.simple.filter.gui.helper.SettingDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.LinkedList;

import javax.swing.*;

/**
 * List with time selection history
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class GenericFilterMenu extends JPopupMenu implements ActionListener
{

    protected static final int nRecentItems = 15;
    protected ProgrammeFilter filter;
    protected Class dialogClass;
    protected SettingDialog dialog;
    protected JMenuItem mnuEdit;
    protected JMenuItem mnuNoFilter;

    /** List with all "recent" time items */
    protected LinkedList history = new LinkedList(  );

    /**
     * Creates a new GenericFilterMenu object.
     *
     * @param filter DOCUMENT ME!
     * @param dialogClass DOCUMENT ME!
     */
    public GenericFilterMenu( ProgrammeFilter filter, Class dialogClass )
    {
        super(  );
        this.filter = filter;
        this.dialogClass = dialogClass;
        this.buildMenu(  );
    }

    //public GenericFilterMenu(TimeFilter filter)
    protected void buildMenu(  )
    {
        this.mnuEdit =
            new JMenuItem( 
                VerticalViewer.getInstance(  ).getLocalizedMessage( 
                    "genericfilter.menu.editfilter" ) );
        this.mnuEdit.addActionListener( this );
        this.add( this.mnuEdit );

        this.mnuNoFilter =
            new JMenuItem( 
                VerticalViewer.getInstance(  ).getLocalizedMessage( 
                    "genericfilter.menu.nofilter" ) );
        this.mnuNoFilter.addActionListener( this );
        this.add( this.mnuNoFilter );
    }

    //protected void buildMenu()

    /**
     * DOCUMENT_ME!
     *
     * @param e DOCUMENT_ME!
     */
    public void actionPerformed( ActionEvent e )
    {

        if( e.getSource(  ) == this.mnuEdit )
        {
            showSettingDialog(  );
        }
        else if( e.getSource(  ) == this.mnuNoFilter )
        {
            this.filter.deactivate(  );
        }
        else
        {

            //import the filter setting
            this.filter.importSettings( 
                (String)( (DataMenuItem)e.getSource(  ) ).getData(  ) );
        }
    }

    //public void actionPerformed(ActionEvent e)

    /**
     * Shows the time setting dialog and calls the addRecent() method if the
     * dialog has been closed with Ok
     */
    protected void showSettingDialog(  )
    {

        if( this.dialog == null )
        {

            try
            {
                this.dialog = (SettingDialog)dialogClass.newInstance(  );
                this.dialog.setFilter( this.filter );
                this.dialog.init(  );
            }
            catch( Exception e )
            {
                e.printStackTrace(  );
                System.err.println( "Could not instantiate dialog" );

                return;
            }
        }

        this.dialog.show(  );

        if( this.dialog.isClosedWithOk(  ) )
        {
            this.addRecent(  );
        }
    }

    //protected void showSettingDialog()

    /**
     * adds a "recent time" menu item
     */
    protected void addRecent(  )
    {

        if( this.filter.isDeactivated(  ) )
        {

            return;
        }

        if( this.history.size(  ) == 0 )
        {
            this.addSeparator(  );
        }

        String strExportValue = this.filter.exportSettings(  );

        //does it already exist?
        boolean bExists = false;
        Object[] arItems = this.history.toArray(  );

        for( int nA = 0; nA < arItems.length; nA++ )
        {

            if( 
                ( (DataMenuItem)arItems[nA] ).getData(  ).equals( 
                        strExportValue ) )
            {
                bExists = true;

                break;
            }
        }

        if( !bExists )
        {

            JMenuItem item =
                new DataMenuItem( this.filter.getTitle(  ), strExportValue );
            item.addActionListener( this );
            this.add( item );
            this.history.add( item );

            //Remove the oldest item when we reached the max size
            if( this.history.size(  ) > nRecentItems )
            {

                DataMenuItem oldItem = (DataMenuItem)this.history.getFirst(  );
                this.remove( oldItem );
                this.history.remove( oldItem );
            }
        }
    }

    //protected void addRecent()
}


//public class GenericFilterMenu
