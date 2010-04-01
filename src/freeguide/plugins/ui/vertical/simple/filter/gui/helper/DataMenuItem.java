package freeguide.plugins.ui.vertical.simple.filter.gui.helper;

import javax.swing.*;

/**
 * Simple menu item which can hold a data item
 *
 * @author Christian Weiske (cweiske at cweiske.de)
 */
public class DataMenuItem extends JMenuItem
{
    /** Everyone can access that */
    public Object data;

    /**
     * Creates a new DataMenuItem object.
     */
    public DataMenuItem(  )
    {
        super(  );
    }

    /**
     * Creates a new DataMenuItem object.
     *
     * @param string DOCUMENT ME!
     */
    public DataMenuItem( String string )
    {
        super( string );
    }

    /**
     * Creates a new DataMenuItem object.
     *
     * @param string DOCUMENT ME!
     * @param data DOCUMENT ME!
     */
    public DataMenuItem( String string, Object data )
    {
        super( string );
        this.data = data;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object getData(  )
    {
        return data;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param data DOCUMENT_ME!
     */
    public void setData( Object data )
    {
        this.data = data;
    }
}
//public class DataMenuItem
