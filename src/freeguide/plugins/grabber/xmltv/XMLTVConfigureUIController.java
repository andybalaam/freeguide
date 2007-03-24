package freeguide.plugins.grabber.xmltv;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.StringHelper;

import freeguide.common.plugininterfaces.IModuleConfigurationUI;

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
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.tree.MutableTreeNode;

/**
 * Edit options controller.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class XMLTVConfigureUIController implements IModuleConfigurationUI
{
    final protected GrabberXMLTV parent;
    protected XMLTVConfigureUIPanel panel;
    final protected XMLTVConfig config;
    protected Color textNoEdited;
    protected Color textEdited = Color.BLUE;
    protected Map textListeners = new TreeMap(  );
    protected int latestY = 0;
    final protected String[] modules;
    protected ActionListener BtnResetAction =
        new ActionListener(  )
        {
            public void actionPerformed( final ActionEvent e )
            {
                //JButton btn = (JButton)e.getSource(  );

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
    public XMLTVConfigureUIController( final GrabberXMLTV parent )
    {
        this.parent = parent;

        modules = GrabberXMLTV.getMods( 
                StringHelper.EMPTY_STRING,
                '.' + GrabberXMLTV.RUN_KEY_SUFFIX + '.'
                + ( Application.getInstance(  ).isUnix(  )
                ? GrabberXMLTV.LIN_KEY_SUFFIX : GrabberXMLTV.WIN_KEY_SUFFIX ) );

        config = (XMLTVConfig)parent.config.clone(  );
    }

    /**
     * DOCUMENT_ME!
     */
    public void save(  )
    {
        parent.config = config;

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
     * @param leafName DOCUMENT ME!
     * @param node DOCUMENT ME!
     * @param tree DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public Component getPanel( 
        String leafName, MutableTreeNode node, JTree tree )
    {
        if( panel == null )
        {
            panel = new XMLTVConfigureUIPanel( parent.getLocalizer(  ) );

            for( int i = 0; i < config.modules.size(  ); i++ )
            {
                addModule( (XMLTVConfig.ModuleInfo)config.modules.get( i ) );
            }

            panel.getBtnAdd(  ).addActionListener( 
                new ActionListener(  )
                {
                    public void actionPerformed( ActionEvent e )
                    {
                        XMLTVConfig.ModuleInfo info =
                            new XMLTVConfig.ModuleInfo(  );

                        synchronized( config.modules )
                        {
                            config.modules.add( info );
                        }

                        addModule( info );
                        panel.revalidate(  );
                        panel.repaint(  );
                    }
                } );
        }

        return panel;
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
    public String[] getTreeNodes(  )
    {
        return null;
    }

    private void addModule( final XMLTVConfig.ModuleInfo moduleInfo )
    {
        GridBagConstraints gc = new GridBagConstraints(  );
        gc.gridx = 0;
        gc.gridy = latestY;
        gc.weightx = 1;
        gc.insets = new Insets( 10, 0, 0, 0 );
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
    public JPanel getOnePanel( final XMLTVConfig.ModuleInfo moduleInfo )
    {
        final XMLTVConfigureUIPanelModule confPanel =
            new XMLTVConfigureUIPanelModule( 
                parent.getLocalizer(  ), moduleInfo, new TextChanged(  ),
                new ConfigTextChanged(  ) );

        confPanel.getBtnChannels(  )
                 .addActionListener( 
            new BtnChannelsAction( parent, moduleInfo ) );
        confPanel.getBtnCommandReset(  )
                 .addActionListener( new BtnCommandResetAction( confPanel ) );

        confPanel.getBtnCommandReset(  ).addActionListener( BtnResetAction );

        textNoEdited = confPanel.getTextCommand(  ).getForeground(  );

        confPanel.getBtnDelete(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    if( 
                        JOptionPane.showConfirmDialog( 
                                panel,
                                parent.getLocalizer(  ).getString( 
                                    "UI.Confirm" ),
                                parent.getLocalizer(  ).getString( 
                                    "UI.Remove" ), JOptionPane.OK_CANCEL_OPTION ) == JOptionPane.OK_OPTION )
                    {
                        synchronized( config.modules )
                        {
                            config.modules.remove( confPanel.moduleInfo );
                        }

                        panel.getPanelModules(  ).remove( confPanel );
                        panel.revalidate(  );
                        panel.repaint(  );
                    }
                }
            } );

        confPanel.getComboModules(  )
                 .setModel( new DefaultComboBoxModel( modules ) );
        confPanel.getComboModules(  ).setSelectedItem( moduleInfo.moduleName );
        confPanel.getComboModules(  )
                 .addActionListener( new ComboModulesAction( confPanel ) );

        setTextFieldSet( confPanel );
        confPanel.getTextCommand(  ).getDocument(  )
                 .addDocumentListener( confPanel.textChangedEvent );
        confPanel.textChangedEvent.allowEvent = true;

        setConfigTextFieldSet( confPanel );
        confPanel.getTextConfigCommand(  ).getDocument(  )
                 .addDocumentListener( confPanel.configTextChangedEvent );
        confPanel.configTextChangedEvent.allowEvent = true;

        return confPanel;

    }

    protected void setTextFieldMarkAsEdited( 
        final XMLTVConfigureUIPanelModule panel )
    {
        panel.getTextCommand(  ).setForeground( textEdited );
    }

    protected void setConfigTextFieldMarkAsEdited( 
        final XMLTVConfigureUIPanelModule panel )
    {
        panel.getTextConfigCommand(  ).setForeground( textEdited );
    }

    protected void setTextFieldSet( final XMLTVConfigureUIPanelModule panel )
    {
        if( panel.moduleInfo.commandToRun == null )
        {
            panel.getTextCommand(  ).setForeground( textNoEdited );
            panel.getTextCommand(  )
                 .setText( 
                GrabberXMLTV.getCommand( 
                    panel.moduleInfo.moduleName, GrabberXMLTV.RUN_KEY_SUFFIX ) );
        }
        else
        {
            panel.getTextCommand(  ).setForeground( textEdited );
            panel.getTextCommand(  ).setText( panel.moduleInfo.commandToRun );
        }
    }

    protected void setConfigTextFieldSet( 
        final XMLTVConfigureUIPanelModule panel )
    {
        if( panel.moduleInfo.configCommandToRun == null )
        {
            panel.getTextConfigCommand(  ).setForeground( textNoEdited );
            panel.getTextConfigCommand(  )
                 .setText( 
                GrabberXMLTV.getCommand( 
                    panel.moduleInfo.moduleName, GrabberXMLTV.CONFIG_KEY_SUFFIX ) );
        }
        else
        {
            panel.getTextConfigCommand(  ).setForeground( textEdited );
            panel.getTextConfigCommand(  )
                 .setText( panel.moduleInfo.configCommandToRun );
        }
    }

    protected static class BtnChannelsAction implements ActionListener
    {
        final protected GrabberXMLTV parent;
        final protected XMLTVConfig.ModuleInfo moduleInfo;

/**
         * Creates a new BtnChannelsAction object.
         *
         * @param parent DOCUMENT ME!
         * @param moduleInfo DOCUMENT ME!
         */
        public BtnChannelsAction( 
            final GrabberXMLTV parent, final XMLTVConfig.ModuleInfo moduleInfo )
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
        final protected XMLTVConfigureUIPanelModule confPanel;

/**
         * Creates a new BtnCommandResetAction object.
         *
         * @param confPanel DOCUMENT ME!
         */
        public BtnCommandResetAction( 
            final XMLTVConfigureUIPanelModule confPanel )
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
            confPanel.configTextChangedEvent.allowEvent = false;
            confPanel.moduleInfo.commandToRun = null;
            confPanel.moduleInfo.configCommandToRun = null;
            setTextFieldSet( confPanel );
            setConfigTextFieldSet( confPanel );
            confPanel.textChangedEvent.allowEvent = true;
            confPanel.configTextChangedEvent.allowEvent = true;
        }
    }

    protected class ComboModulesAction implements ActionListener
    {
        protected final XMLTVConfigureUIPanelModule confPanel;

/**
         * Creates a new ComboModulesAction object.
         *
         * @param confPanel DOCUMENT ME!
         */
        public ComboModulesAction( 
            final XMLTVConfigureUIPanelModule confPanel )
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
            confPanel.configTextChangedEvent.allowEvent = false;
            confPanel.moduleInfo.moduleName = (String)( (JComboBox)e.getSource(  ) )
                .getSelectedItem(  );
            confPanel.moduleInfo.commandToRun = null;
            confPanel.moduleInfo.configCommandToRun = null;
            setTextFieldSet( confPanel );
            setConfigTextFieldSet( confPanel );
            confPanel.textChangedEvent.allowEvent = true;
            confPanel.configTextChangedEvent.allowEvent = true;
        }
    }

    protected class TextChanged implements DocumentListener
    {
        protected XMLTVConfigureUIPanelModule panel;
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
                panel.moduleInfo.commandToRun = doc.getText( 
                        0, doc.getLength(  ) );
            }
            catch( BadLocationException ex )
            {
            }

            setTextFieldMarkAsEdited( panel );

        }
    }

    protected class ConfigTextChanged implements DocumentListener
    {
        protected XMLTVConfigureUIPanelModule panel;
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
                panel.moduleInfo.configCommandToRun = doc.getText( 
                        0, doc.getLength(  ) );
            }
            catch( BadLocationException ex )
            {
            }

            setConfigTextFieldMarkAsEdited( panel );

        }
    }
}
