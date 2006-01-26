package freeguide.plugins.ui.vertical.simple.filter.gui.helper;

import javax.swing.*;


/**
 * Simple menu item which can hold a data item
 *
 * @author Christian Weiske <cweiske@cweiske.de>
 */
public class DataMenuItem extends JMenuItem
{
    /**
     * Everyone can access that
     */
    public Object data;



    public DataMenuItem()
    {
        super();
    }



    public DataMenuItem(String string)
    {
        super(string);
    }



    public DataMenuItem(String string, Object data)
    {
        super(string);
        this.data = data;
    }



    public Object getData()
    {
        return data;
    }



    public void setData(Object data)
    {
        this.data = data;
    }
}//public class DataMenuItem
