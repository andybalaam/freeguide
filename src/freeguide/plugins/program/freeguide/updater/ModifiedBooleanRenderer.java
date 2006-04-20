package freeguide.plugins.program.freeguide.updater;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import javax.swing.table.TableCellRenderer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ModifiedBooleanRenderer extends JCheckBox
    implements TableCellRenderer, UIResource
{

    private static final Border noFocusBorder = new EmptyBorder( 1, 1, 1, 1 );

    /**
     * Creates a new ModifiedBooleanRenderer object.
     */
    public ModifiedBooleanRenderer(  )
    {
        super(  );
        setHorizontalAlignment( JLabel.CENTER );
        setBorderPainted( true );
    }

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

        if( isSelected )
        {
            setForeground( table.getSelectionForeground(  ) );
            super.setBackground( table.getSelectionBackground(  ) );
        }
        else
        {
            setForeground( table.getForeground(  ) );
            setBackground( table.getBackground(  ) );
        }

        if( value != null )
        {
            setSelected( ( (Boolean)value ).booleanValue(  ) );

            if( hasFocus )
            {
                setBorder( 
                    UIManager.getBorder( "Table.focusCellHighlightBorder" ) );
            }
            else
            {
                setBorder( noFocusBorder );
            }

            return this;
        }
        else
        {

            return null;
        }
    }
}
