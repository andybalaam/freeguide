package freeguide.plugins.ui.vertical.simple.filter.gui;

import freeguide.plugins.ui.vertical.simple.VerticalViewer;
import freeguide.plugins.ui.vertical.simple.filter.ProgrammeFilter;
import freeguide.plugins.ui.vertical.simple.filter.TitleFilter;
import freeguide.plugins.ui.vertical.simple.filter.gui.helper.SettingDialog;

import javax.swing.*;

/**
 * DOCUMENT ME!
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class TitleDialog implements SettingDialog
{
    protected TitleFilter filter;
    protected boolean bClosedWithOk = false;

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean isClosedWithOk(  )
    {
        return this.bClosedWithOk;
    }

    //public boolean isClosedWithOk()
    /**
     * DOCUMENT_ME!
     */
    public void init(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @param filter DOCUMENT_ME!
     */
    public void setFilter( ProgrammeFilter filter )
    {
        this.filter = (TitleFilter)filter;
    }

    //public void setFilter(ProgrammeFilter filter)
    /**
     * DOCUMENT_ME!
     */
    public void show(  )
    {
        String strAnswer =
            JOptionPane.showInputDialog( 
                VerticalViewer.getInstance(  )
                              .getLocalizedMessage( "titledialog.question" ),
                this.filter.getSearchString(  ) );
        this.filter.setSearchString( strAnswer );
        this.bClosedWithOk = ( strAnswer != null );
    }

    //public void show()
}
//public class TitleDialog
