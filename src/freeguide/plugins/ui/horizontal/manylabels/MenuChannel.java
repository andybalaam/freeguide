package freeguide.plugins.ui.horizontal.manylabels;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Context menu for channel label.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class MenuChannel extends JPopupMenu
{
/**
     * Creates a new MenuChannel object.
     *
     * @param labelChannel DOCUMENT ME!
     * @param main DOCUMENT ME!
     */
    public MenuChannel( 
        final JLabelChannel labelChannel, final HorizontalViewer main )
    {
        JMenuItem mbtChangeIcon =
            new javax.swing.JMenuItem( 
                main.getLocalizer(  ).getLocalizedMessage( "change_icon" ) );
        mbtChangeIcon.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    main.changeIconActionPerformed( 
                        labelChannel.getChannel(  ) );
                }
            } );
        add( mbtChangeIcon );

        if( labelChannel.getIcon(  ) != null )
        {
            JMenuItem mbtResetIcon =
                new javax.swing.JMenuItem( 
                    main.getLocalizer(  )
                        .getLocalizedMessage( "reset_to_default_icon" ) );
            mbtResetIcon.addActionListener( 
                new java.awt.event.ActionListener(  )
                {
                    public void actionPerformed( 
                        java.awt.event.ActionEvent evt )
                    {
                        main.resetIconActionPerformed( 
                            labelChannel.getChannel(  ) );

                    }
                } );
            add( mbtResetIcon );
        }
    }
}
