package freeguide.plugins.ui.horizontal;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * Panel for choose date.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class DateSelectPanel extends JPanel
{

    private JButton btnNow = null;
    private JButton btnLeft = null;
    private JComboBox comboDateList = null;
    private JButton btnRight = null;

    /**
     * This is the default constructor
     */
    public DateSelectPanel(  )
    {
        super(  );

        initialize(  );

    }

    /**
     * This method initializes this
     */
    private void initialize(  )
    {
        this.add( getBtnNow(  ), null );

        this.add( getBtnLeft(  ), null );

        this.add( getComboDateList(  ), null );

        this.add( getBtnRight(  ), null );

    }

    /**
     * This method initializes jButton
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnNow(  )
    {

        if( btnNow == null )
        {
            btnNow = new JButton(  );

            btnNow.setText( "Now" );

        }

        return btnNow;

    }

    /**
     * This method initializes jButton1
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnLeft(  )
    {

        if( btnLeft == null )
        {
            btnLeft = new JButton(  );

            btnLeft.setText( "-" );

        }

        return btnLeft;

    }

    /**
     * This method initializes comboDateList
     *
     * @return javax.swing.JComboBox
     */
    public JComboBox getComboDateList(  )
    {

        if( comboDateList == null )
        {
            comboDateList = new JComboBox(  );

        }

        return comboDateList;

    }

    /**
     * This method initializes jButton2
     *
     * @return javax.swing.JButton
     */
    public JButton getBtnRight(  )
    {

        if( btnRight == null )
        {
            btnRight = new JButton(  );

            btnRight.setText( "+" );

        }

        return btnRight;

    }
}
