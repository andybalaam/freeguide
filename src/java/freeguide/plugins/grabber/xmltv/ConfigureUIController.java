package freeguide.plugins.grabber.xmltv;

import freeguide.FreeGuide;

import freeguide.plugins.IModuleConfigurationUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class ConfigureUIController implements IModuleConfigurationUI
{

    final protected GrabberXMLTV parent;
    final protected ConfigureUIPanel panel;
    final protected Config config;
    protected Color textNoEdited;
    protected Color textEdited = Color.RED;
    protected Map textListeners = new TreeMap(  );
    protected int latestY = 0;
    final protected String[] modules;
    protected ActionListener BtnResetAction =
        new ActionListener(  )
        {
            public void actionPerformed( final ActionEvent e )
            {

                JButton btn = (JButton)e.getSource(  );

                //                config.commandsRun.remove( btn.getName(  ) );

                /*         JTextField tf = (JTextField)textFields.get( btn.getName(  ) );

                         tf.getDocument(  ).removeDocumentListener(
                             (TextChanged)textListeners.get( btn.getName(  ) ) );

                         setTextField( btn.getName(  ), true );

                         tf.getDocument(  ).addDocumentListener(
                             (TextChanged)textListeners.get( btn.getName(  ) ) );*/
            }
        };

    /**
     * Creates a new ConfigureUIController object.
     *
     * @param parent DOCUMENT ME!
     */
    public ConfigureUIController( final GrabberXMLTV parent )
    {
        this.parent = parent;

        modules =
            GrabberXMLTV.getMods( 
                "", ".run." + ( FreeGuide.runtimeInfo.isUnix ? "lin" : "win" ) );

        config = (Config)parent.config.clone(  );

        panel = new ConfigureUIPanel(  );

        for( int i = 0; i < config.modules.size(  ); i++ )
        {
            addModule( (Config.ModuleInfo)config.modules.get( i ) );
        }

        panel.getBtnAdd(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    Config.ModuleInfo info = new Config.ModuleInfo(  );
                    config.modules.add( info );
                    addModule( info );
                    panel.revalidate(  );
                    panel.repaint(  );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        parent.config = config;

        parent.saveConfig(  );

    }

    /**
     * DOCUMENT_ME!
     */
    public void cancel(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel(  )
    {

        return panel;

    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
    }

    private void addModule( final Config.ModuleInfo moduleInfo )
    {

        GridBagConstraints gc = new GridBagConstraints(  );
        gc.gridx = 0;
        gc.gridy = latestY;
        gc.weightx = 1;
        gc.insets = new Insets(10,0,0,0);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        panel.getPanelModules(  ).add( getOnePanel( moduleInfo ), gc );
        latestY++;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param moduleInfo DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JPanel getOnePanel( final Config.ModuleInfo moduleInfo )
    {

        final ConfigureUIPanelModule confPanel =
            new ConfigureUIPanelModule( moduleInfo, new TextChanged(  ) );

        confPanel.getBtnChannels(  ).addActionListener( 
            new BtnChannelsAction( parent, moduleInfo ) );
        confPanel.getBtnCommandReset(  ).addActionListener( 
            new BtnCommandResetAction( confPanel ) );

        confPanel.getBtnCommandReset(  ).addActionListener( BtnResetAction );

        textNoEdited = confPanel.getTextCommand(  ).getForeground(  );

        confPanel.getBtnDelete(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    if( 
                        JOptionPane.showConfirmDialog( 
                                panel, "Are you sure ?", "Delete",
                                JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.OK_OPTION )
                    {
                    	config.modules.remove(confPanel.moduleInfo);
                        panel.getPanelModules(  ).remove( confPanel );
                        panel.revalidate(  );
                        panel.repaint(  );
                    }
                }
            } );

        confPanel.getComboModules(  ).setModel( 
            new DefaultComboBoxModel( modules ) );
        confPanel.getComboModules(  ).setSelectedItem( moduleInfo.moduleName );
        confPanel.getComboModules(  ).addActionListener( 
            new ComboModulesAction( confPanel ) );

        setTextFieldSet( confPanel );
        confPanel.getTextCommand(  ).getDocument(  ).addDocumentListener( 
            confPanel.textChangedEvent );
        confPanel.textChangedEvent.allowEvent = true;

        return confPanel;

    }

    protected void setTextFieldMarkAsEdited( 
        final ConfigureUIPanelModule panel )
    {
        panel.getTextCommand(  ).setForeground( textEdited );
    }

    protected void setTextFieldSet( final ConfigureUIPanelModule panel )
    {

        if( panel.moduleInfo.commandToRun == null )
        {
            panel.getTextCommand(  ).setForeground( textNoEdited );
            panel.getTextCommand(  ).setText( 
                GrabberXMLTV.getCommand( panel.moduleInfo.moduleName, "run" ) );
        }
        else
        {
            panel.getTextCommand(  ).setForeground( textEdited );
            panel.getTextCommand(  ).setText( panel.moduleInfo.commandToRun );
        }
    }

    protected static class BtnChannelsAction implements ActionListener
    {

        final protected GrabberXMLTV parent;
        final protected Config.ModuleInfo moduleInfo;

        /**
         * Creates a new BtnChannelsAction object.
         *
         * @param parent DOCUMENT ME!
         * @param moduleInfo DOCUMENT ME!
         */
        public BtnChannelsAction( 
            final GrabberXMLTV parent, final Config.ModuleInfo moduleInfo )
        {
            this.parent = parent;
            this.moduleInfo = moduleInfo;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( final ActionEvent e )
        {
            new Thread(  )
                {
                    public void run(  )
                    {

                        JButton btn = (JButton)e.getSource(  );

                        parent.configureChannels( moduleInfo );

                    }
                }.start(  );

        }
    }

    protected class BtnCommandResetAction implements ActionListener
    {

        final protected ConfigureUIPanelModule confPanel;

        /**
         * Creates a new BtnCommandResetAction object.
         *
         * @param confPanel DOCUMENT ME!
         */
        public BtnCommandResetAction( final ConfigureUIPanelModule confPanel )
        {
            this.confPanel = confPanel;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( final ActionEvent e )
        {
            confPanel.textChangedEvent.allowEvent = false;
            confPanel.moduleInfo.commandToRun = null;
            setTextFieldSet( confPanel );
            confPanel.textChangedEvent.allowEvent = true;
        }
    }

    protected class ComboModulesAction implements ActionListener
    {

        protected final ConfigureUIPanelModule confPanel;

        /**
         * Creates a new ComboModulesAction object.
         *
         * @param confPanel DOCUMENT ME!
         */
        public ComboModulesAction( final ConfigureUIPanelModule confPanel )
        {
            this.confPanel = confPanel;
        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void actionPerformed( final ActionEvent e )
        {
            confPanel.textChangedEvent.allowEvent = false;
            confPanel.moduleInfo.moduleName =
                (String)( (JComboBox)e.getSource(  ) ).getSelectedItem(  );
            confPanel.moduleInfo.commandToRun = null;
            setTextFieldSet( confPanel );
            confPanel.textChangedEvent.allowEvent = true;
        }
    }

    protected class TextChanged implements DocumentListener
    {

        protected ConfigureUIPanelModule panel;
        protected boolean allowEvent = true;

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void changedUpdate( DocumentEvent e )
        {
            onChanged( e );

        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void insertUpdate( DocumentEvent e )
        {
            onChanged( e );

        }

        /**
         * DOCUMENT_ME!
         *
         * @param e DOCUMENT_ME!
         */
        public void removeUpdate( DocumentEvent e )
        {
            onChanged( e );

        }

        protected void onChanged( DocumentEvent e )
        {

            if( !allowEvent )
            {

                return;
            }

            Document doc = e.getDocument(  );

            try
            {
                panel.moduleInfo.commandToRun =
                    doc.getText( 0, doc.getLength(  ) );

                System.out.println( 
                    "changed " + panel.moduleInfo.moduleName + " to "
                    + doc.getText( 0, doc.getLength(  ) ) );

            }

            catch( BadLocationException ex )
            {
                ex.printStackTrace(  );

            }

            setTextFieldMarkAsEdited( panel );

        }
    }
}
