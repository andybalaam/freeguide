package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.base.IModuleConfigurationUI;
import freeguide.common.gui.FontChooserDialog;

import freeguide.common.lib.general.Time;


import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class ConfigureUIController implements IModuleConfigurationUI
{
    final protected HorizontalViewer parent;
    final protected JDialog inDialog;
    protected ConfigureUIPanel panel;
    protected Font currentFont;
    protected HorizontalViewerConfig config;
    protected ChangeListener sliderChange =
        new ChangeListener(  )
        {
            public void stateChanged( ChangeEvent e )
            {
                JTextField textField = null;

                if( e.getSource(  ) == panel.getSliderHeight(  ) )
                {
                    textField = panel.getTextHeight(  );

                }

                else if( e.getSource(  ) == panel.getSliderWidth(  ) )
                {
                    textField = panel.getTextWidth(  );

                }

                textField.setText(
                    Integer.toString(
                        ( (JSlider)e.getSource(  ) ).getValue(  ) ) );

            }
        };

    protected ActionListener colorBtnAction =
        new ActionListener(  )
        {
            public void actionPerformed( ActionEvent e )
            {
                JPanel panelColor = null;

                if( e.getSource(  ) == panel.getBtnColorChannel(  ) )
                {
                    panelColor = panel.getPanelColorChannel(  );
                }
                else if( e.getSource(  ) == panel.getBtnColorMovie(  ) )
                {
                    panelColor = panel.getPanelColorMovie(  );
                }
                else if( e.getSource(  ) == panel.getBtnColorSelected(  ) )
                {
                    panelColor = panel.getPanelColorSelected(  );
                }
                else if( e.getSource(  ) == panel.getBtnColorNormal(  ) )
                {
                    panelColor = panel.getPanelColorNormal(  );
                }
                else if( e.getSource(  ) == panel.getBtnColorNew(  ) )
                {
                    panelColor = panel.getPanelColorNew(  );
                }

                Color col =
                    JColorChooser.showDialog(
                        inDialog,
                        parent.getLocalizer(  ).getString( "choose_a_colour" ),
                        panelColor.getBackground(  ) );

                if( col != null )
                {
                    panelColor.setBackground( col );
                }
            }
        };

    /**
     * Creates a new ConfigureUIController object.
     *
     * @param parent
     *            DOCUMENT ME!
     * @param parentDialog
     *            DOCUMENT ME!
     */
    public ConfigureUIController(
        final HorizontalViewer parent, final JDialog parentDialog )
    {
        this.parent = parent;

        this.inDialog = parentDialog;
    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        if( null != panel )
        {
            HorizontalViewerConfig config = (HorizontalViewerConfig) parent.getConfig();

            config.fontName = currentFont.getName(  );
            config.fontSize = currentFont.getSize(  );
            config.fontStyle = currentFont.getStyle(  );
            config.sizeChannelHeight = panel.getSliderHeight(  ).getValue(  );
            config.sizeProgrammeHour = panel.getSliderWidth(  ).getValue(  );
            config.colorNew = panel.getPanelColorNew(  ).getBackground(  );
            config.colorChannel = panel.getPanelColorChannel(  ).getBackground(  );
            config.colorMovie = panel.getPanelColorMovie(  ).getBackground(  );
            config.colorTicked = panel.getPanelColorSelected(  ).getBackground(  );
            config.colorNonTicked = panel.getPanelColorNormal(  )
                                         .getBackground(  );
            config.displayTooltips = panel.getCbDisplayTooltips(  ).isSelected(  );
            config.displayTime = panel.getCbDrawTime(  ).isSelected(  );
            config.displayAlignToLeft = panel.getCbAlignLeft(  ).isSelected(  );
            config.display24time = panel.getRbTime24(  ).isSelected(  );
            config.dayStartTime = new Time( panel.getDayStart(  ).getText(  ) );
            config.timeTracking = panel.getCbTimeTracking(  ).isSelected(  );

            parent.setConfig( config );
        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {
        if( panel == null )
        {
            panel = new ConfigureUIPanel( parent.getLocalizer(  ) );
            setup(  );
        }

        return panel;
    }

    protected void setup(  )
    {
        currentFont = new Font(
                parent.config.fontName, parent.config.fontStyle, parent.config.fontSize );
        setupFont(  );
        panel.getTextHeight(  )
             .setText( Integer.toString( parent.config.sizeChannelHeight ) );
        panel.getSliderHeight(  ).setValue( parent.config.sizeChannelHeight );
        panel.getTextWidth(  )
             .setText( Integer.toString( parent.config.sizeProgrammeHour ) );
        panel.getSliderWidth(  ).setValue( parent.config.sizeProgrammeHour );
        panel.getPanelColorChannel(  ).setBackground( parent.config.colorChannel );
        panel.getPanelColorMovie(  ).setBackground( parent.config.colorMovie );
        panel.getPanelColorSelected(  ).setBackground( parent.config.colorTicked );
        panel.getPanelColorNormal(  ).setBackground( parent.config.colorNonTicked );
        panel.getPanelColorNew(  ).setBackground( parent.config.colorNew );

        panel.getBtnFont(  ).addActionListener(
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    FontChooserDialog fontDialog =
                        new FontChooserDialog(
                            inDialog,
                            parent.getLocalizer(  ).getString( "choose_font" ),
                            true,
                            new Font(
                                parent.config.fontName, parent.config.fontStyle,
                                parent.config.fontSize ) );
                    Dimension fontDialogSize = new Dimension( 300, 200 );
                    Dimension parentSize = inDialog.getSize(  );
                    Point parentLocation = inDialog.getLocationOnScreen(  );
                    fontDialog.setLocation(
                        parentLocation.x
                        + ( ( parentSize.width - fontDialogSize.width ) / 2 ),
                        parentLocation.y
                        + ( ( parentSize.height - fontDialogSize.height ) / 2 ) );

                    fontDialog.setSize( fontDialogSize );
                    fontDialog.setVisible( true );
                    currentFont = fontDialog.getSelectedFont(  );
                    setupFont(  );
                }
            } );
        panel.getBtnColorChannel(  ).addActionListener( colorBtnAction );
        panel.getBtnColorSelected(  ).addActionListener( colorBtnAction );
        panel.getBtnColorMovie(  ).addActionListener( colorBtnAction );
        panel.getBtnColorNormal(  ).addActionListener( colorBtnAction );
        panel.getBtnColorNew(  ).addActionListener( colorBtnAction );
        panel.getSliderHeight(  ).addChangeListener( sliderChange );
        panel.getSliderWidth(  ).addChangeListener( sliderChange );
        panel.getCbDisplayTooltips(  ).setSelected( parent.config.displayTooltips );
        panel.getCbDrawTime(  ).setSelected( parent.config.displayTime );
        panel.getCbAlignLeft(  ).setSelected( parent.config.displayAlignToLeft );
        panel.getCbTimeTracking(  ).setSelected( parent.config.timeTracking );

        if( parent.config.display24time )
        {
            panel.getRbTime24(  ).setSelected( true );
        }
        else
        {
            panel.getRbTime12(  ).setSelected( true );
        }

        panel.getDayStart(  ).setText( parent.config.dayStartTime.getHHMMString(  ) );
    }

    protected void setupFont(  )
    {
        panel.getTextFont(  ).setFont( currentFont );

        panel.getTextFont(  )
             .setText(
            currentFont.getFontName(  ) + ", " + currentFont.getSize(  ) );

    }
}
