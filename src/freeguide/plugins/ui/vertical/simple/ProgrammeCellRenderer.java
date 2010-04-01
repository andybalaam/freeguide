package freeguide.plugins.ui.vertical.simple;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleReminder;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class ProgrammeCellRenderer extends DefaultTableCellRenderer
{
    private IModuleReminder reminder = null;

    /**
     * DOCUMENT_ME!
     */
    public void init(  )
    {
        IModuleReminder[] rems = Application.getInstance(  ).getReminders(  );

        if( rems.length > 0 )
        {
            this.reminder = rems[0];
        }
    }

    //public void init()
    /**
     * DOCUMENT_ME!
     *
     * @param table DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     * @param isSelected DOCUMENT_ME!
     * @param hasFocus DOCUMENT_ME!
     * @param row DOCUMENT_ME!
     * @param column DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column )
    {
        Component c =
            super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column );
        TVProgramme programme =
            (TVProgramme)table.getModel(  )
                              .getValueAt( row, TvTableModel.COL_PROGRAMME );

        if( isSelected || hasFocus )
        {
            //c.setBackground(VerticalViewerConfig.colorSelected);
        }
        else if( this.reminder != null )
        {
            if( this.reminder.isSelected( programme ) )
            {
                c.setBackground( VerticalViewerConfig.colorTicked );
            }
            else if( programme.getIsMovie(  ) )
            {
                c.setBackground( VerticalViewerConfig.colorMovie );
            }
            else
            {
                c.setBackground( VerticalViewerConfig.colorNonTicked );
            }
        }

        //TODO: Nifty percentage as maxemum tv guide does it
        return c;
    }

    //public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
}
//public class ProgrammeCellRenderer extends DefaultTableCellRenderer
