package freeguide.plugins.program.freeguide.options;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.util.TreeSet;

import javax.swing.JCheckBox;

import freeguide.common.gui.FGDialog;
import freeguide.common.lib.fgspecific.Application;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;
import freeguide.plugins.program.freeguide.viewer.MainController;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class GrabbersOptionPanel extends OptionPanel
{
    protected TreeSet activeGrabberIDs = new TreeSet(  );
    ActionListener cbAction =
        new ActionListener(  )
        {
            public void actionPerformed( java.awt.event.ActionEvent e )
            {
                JCheckBox cb = (JCheckBox)e.getSource(  );

                if( cb.isSelected(  ) )
                {
                    activeGrabberIDs.add( cb.getName(  ) );

                }

                else
                {
                    activeGrabberIDs.remove( cb.getName(  ) );

                }
            }
        };

/**
     * Creates a new GrabbersOptionPanel object.
     *
     * @param parent DOCUMENT ME!
     */
    public GrabbersOptionPanel( FGDialog parent )
    {
        super( parent );

    }

    protected void doConstruct(  )
    {
        setLayout( new GridBagLayout(  ) );

        PluginInfo[] grabbers = PluginsManager.getGrabbers(  );

        for( int i = 0; i < grabbers.length; i++ )
        {
            GridBagConstraints gc = new GridBagConstraints(  );

            gc.gridx = 0;

            gc.gridy = i;

            gc.anchor = GridBagConstraints.WEST;

            JCheckBox cb = new JCheckBox(  );

            cb.setName( grabbers[i].getID(  ) );

            cb.setText( grabbers[i].getName(  ) );
            cb.setToolTipText( grabbers[i].getDescription(  ) );

            if( 
                MainController.config.getActiveGrabberIDs().contains( 
                        grabbers[i].getID(  ) ) )
            {
                cb.setSelected( true );

                activeGrabberIDs.add( grabbers[i].getID(  ) );

            }

            else
            {
                cb.setSelected( false );

            }

            cb.addActionListener( cbAction );

            add( cb, gc );

        }
    }

    protected void doLoad( String prefix )
    {
    }

    protected boolean doSave(  )
    {
        MainController.config.setActiveGrabberIDs( activeGrabberIDs );

        return true;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {
        return Application.getInstance(  )
                          .getLocalizedMessage( "OptionsDialog.Tree.Grabbers" );

    }
}
