package freeguide.plugins.grabber.xmltv;

import freeguide.FreeGuide;

import freeguide.plugins.IModuleConfigurationUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Map;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
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
    final protected JScrollPane scrollPane;
    final protected Config config;
    protected Color textNoEdited;
    protected Color textEdited = Color.RED;
    protected Map textFields = new TreeMap(  );
    protected Map textListeners = new TreeMap(  );
    protected ActionListener CbAction =
        new ActionListener(  )
        {
            public void actionPerformed( ActionEvent e )
            {

                JCheckBox cb = (JCheckBox)e.getSource(  );

                if( cb.isSelected(  ) )
                {
                    config.needToRun.add( cb.getName(  ) );

                }

                else
                {
                    config.needToRun.remove( cb.getName(  ) );

                }
            }
        };

    protected ActionListener BtnChannelsAction =
        new ActionListener(  )
        {
            public void actionPerformed( final ActionEvent e )
            {
                new Thread(  )
                    {
                        public void run(  )
                        {

                            JButton btn = (JButton)e.getSource(  );

                            parent.configureChannels( btn.getName(  ) );

                        }
                    }.start(  );

            }
        };

    protected ActionListener BtnResetAction =
        new ActionListener(  )
        {
            public void actionPerformed( final ActionEvent e )
            {

                JButton btn = (JButton)e.getSource(  );

                config.commandsRun.remove( btn.getName(  ) );

                JTextField tf = (JTextField)textFields.get( btn.getName(  ) );

                tf.getDocument(  ).removeDocumentListener( 
                    (TextChanged)textListeners.get( btn.getName(  ) ) );

                setTextField( btn.getName(  ), true );

                tf.getDocument(  ).addDocumentListener( 
                    (TextChanged)textListeners.get( btn.getName(  ) ) );

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

        config = (Config)parent.config.clone(  );

        scrollPane = new JScrollPane( createMainPanel(  ) );

        scrollPane.setHorizontalScrollBarPolicy( 
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );

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

        return scrollPane;

    }

    /**
     * DOCUMENT_ME!
     */
    public void resetToDefaults(  )
    {
    }

    private JPanel createMainPanel(  )
    {

        JPanel panel = new JPanel( new GridBagLayout(  ) );

        GridBagConstraints gc = new GridBagConstraints(  );

        gc.gridx = 0;

        gc.gridy = 0;

        gc.weightx = 1;

        gc.anchor = GridBagConstraints.WEST;

        gc.fill = GridBagConstraints.HORIZONTAL;

        String[] mods =
            GrabberXMLTV.getMods( 
                "", ".run." + ( FreeGuide.runtimeInfo.isUnix ? "lin" : "win" ) );

        for( int i = 0; i < mods.length; i++ )
        {
            panel.add( getOnePanel( mods[i] ), gc );

            gc.gridy++;

        }

        return panel;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param modName DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public JPanel getOnePanel( final String modName )
    {

        final ConfigureUIPanel confPanel = new ConfigureUIPanel( modName );

        confPanel.getCbGrab(  ).setSelected( 
            parent.config.needToRun.contains( modName ) );

        confPanel.getCbGrab(  ).setName( modName );

        confPanel.getCbGrab(  ).addActionListener( CbAction );

        confPanel.getBtnChannels(  ).setName( modName );

        confPanel.getBtnChannels(  ).addActionListener( BtnChannelsAction );

        confPanel.getBtnCommandReset(  ).setName( modName );

        confPanel.getBtnCommandReset(  ).addActionListener( BtnResetAction );

        textNoEdited = confPanel.getTextCommand(  ).getForeground(  );

        confPanel.getTextCommand(  ).setName( modName );

        TextChanged cl = new TextChanged( modName );

        textFields.put( modName, confPanel.getTextCommand(  ) );

        textListeners.put( modName, cl );

        setTextField( modName, true );

        confPanel.getTextCommand(  ).getDocument(  ).addDocumentListener( cl );

        return confPanel;

    }

    protected void setTextField( 
        final String modName, final boolean changeText )
    {

        JTextField tf = (JTextField)textFields.get( modName );

        String cmdRun = (String)config.commandsRun.get( modName );

        if( cmdRun == null )
        {
            tf.setForeground( textNoEdited );

            tf.setText( GrabberXMLTV.getCommand( modName, "run" ) );

        }

        else
        {
            tf.setForeground( textEdited );

            if( changeText )
            {
                tf.setText( cmdRun );

            }
        }
    }

    protected class TextChanged implements DocumentListener
    {

        protected String modName;

        TextChanged( String modName )
        {
            this.modName = modName;

        }

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

            Document doc = e.getDocument(  );

            try
            {
                config.commandsRun.put( 
                    modName, doc.getText( 0, doc.getLength(  ) ) );

                System.out.println( 
                    "changed " + modName + " to "
                    + doc.getText( 0, doc.getLength(  ) ) );

            }

            catch( BadLocationException ex )
            {
                ex.printStackTrace(  );

            }

            setTextField( modName, false );

        }
    }
}
