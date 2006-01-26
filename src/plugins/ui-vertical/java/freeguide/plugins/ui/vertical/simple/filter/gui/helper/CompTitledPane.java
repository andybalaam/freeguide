package freeguide.plugins.ui.vertical.simple.filter.gui.helper;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * @version 1.0 08/12/99
 */
public class CompTitledPane extends JPanel
{

    private static final long serialVersionUID = 1L;
    protected CompTitledBorder border;
    protected JComponent component;
    protected JPanel panel;
    protected boolean transmittingAllowed;
    protected StateTransmitter transmitter;



    public CompTitledPane()
    {
        this(new JLabel("Title"));
        // debug
        // JLabel label = (JLabel)getTitleComponent();
        // label.setOpaque(true);
        // label.setBackground(Color.yellow);
    }



    public CompTitledPane(JComponent component)
    {
        this.component = component;
        border = new CompTitledBorder(component);
        setBorder(border);
        panel = new JPanel(new BorderLayout());
        setLayout(null);
        add(component, false);
        add(panel, false);
        transmittingAllowed = false;
        transmitter = null;
    }



    public JComponent getTitleComponent()
    {
        return component;
    }



    public void setTitleComponent(JComponent newComponent)
    {
        remove(component);
        add(newComponent);
        border.setTitleComponent(newComponent);
        component = newComponent;
    }



    public Component add(Component c)
    {
        return this.add(c, true);
    }



    public Component add(Component c, boolean bToContentPane)
    {
        if (bToContentPane) {
            this.getContentPane().add(c);
        } else {
            super.add(c);
        }
        return c;
    }



    public void remove(Component c)
    {
        getContentPane().remove(c);
        getContentPane().updateUI();
    }



    public JPanel getContentPane()
    {
        return panel;
    }



    public void doLayout()
    {
        Insets insets = getInsets();
        Rectangle rect = getBounds();
        rect.x = 0;
        rect.y = 0;

        Rectangle compR = border.getComponentRect(rect, insets);
        component.setBounds(compR);
        rect.x += insets.left;
        rect.y += insets.top;
        rect.width -= insets.left + insets.right;
        rect.height -= insets.top + insets.bottom;
        panel.setBounds(rect);
    }



    public void setTransmittingAllowed(boolean enable)
    {
        transmittingAllowed = enable;
    }



    public boolean getTransmittingAllowed()
    {
        return transmittingAllowed;
    }



    public void setTransmitter(StateTransmitter transmitter)
    {
        this.transmitter = transmitter;
    }



    public StateTransmitter getTransmitter()
    {
        return transmitter;
    }



    public void setEnabled(boolean enable)
    {
        super.setEnabled(enable);
        if (transmittingAllowed && transmitter != null) {
            transmitter.setChildrenEnabled(enable);
        }
    }

}