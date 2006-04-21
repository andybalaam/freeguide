package freeguide.plugins.program.freeguide.options;

import freeguide.plugins.program.freeguide.FreeGuide;

import freeguide.common.gui.FGDialog;

import freeguide.plugins.program.freeguide.viewer.MainController;

import freeguide.common.lib.fgspecific.Application;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginInfo;
import freeguide.plugins.program.freeguide.lib.fgspecific.PluginsManager;

import freeguide.common.plugininterfaces.IModuleGrabber;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JCheckBox;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class GrabbersOptionPanel extends OptionPanel
{

    protected Set activeGrabberIDs = new TreeSet(  );
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

            cb.setText( grabbers[i].getName( Locale.getDefault(  ) ) );
            cb.setToolTipText( 
                grabbers[i].getDescription( Locale.getDefault(  ) ) );

            if( 
                MainController.config.activeGrabberIDs.contains( 
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
        MainController.config.activeGrabberIDs = activeGrabberIDs;

        return true;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {

        return Application.getInstance(  ).getLocalizedMessage( 
            "OptionsDialog.Tree.Grabbers" );

    }
}
