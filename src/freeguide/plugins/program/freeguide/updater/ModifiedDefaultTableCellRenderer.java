package freeguide.plugins.program.freeguide.updater;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ModifiedDefaultTableCellRenderer implements TableCellRenderer
{
    protected final JLabel defaultLabel = new JLabel(  );
    protected final JLabel boldLabel = new JLabel(  );

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
        JLabel result;

        if( 
            ( value != null )
                && value instanceof TablePluginsModel.SubListTitle )
        {
            boldLabel.setFont( 
                new Font( 
                    table.getFont(  ).getName(  ), Font.BOLD,
                    table.getFont(  ).getSize(  ) ) );
            result = boldLabel;
        }
        else
        {
            defaultLabel.setFont( table.getFont(  ) );
            result = defaultLabel;
        }

        result.setText( ( value != null ) ? value.toString(  ) : "" );
        result.setHorizontalAlignment( 
            ( column >= 2 ) ? JLabel.CENTER : JLabel.LEFT );

        return result;
    }
}
