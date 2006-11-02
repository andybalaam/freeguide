package freeguide.plugins.ui.horizontal.manylabels;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVProgramme;

import freeguide.common.plugininterfaces.IModuleReminder;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Context menu for programme label.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class MenuProgramme extends JPopupMenu
{
/**
     * Creates a new MenuProgramme object.
     *
     * @param main DOCUMENT ME!
     * @param programme DOCUMENT ME!
     */
    public MenuProgramme( 
        final HorizontalViewer main, final TVProgramme programme )
    {
        final IModuleReminder reminder =
            Application.getInstance(  ).getReminder(  );

        if( reminder != null )
        {
            reminder.addItemsToPopupMenu( programme, this );
        }

        if( programme.getLink(  ) != null )
        {
            JMenuItem item = new JMenuItem(  );
            item.setText( 
                Application.getInstance(  )
                           .getLocalizedMessage( "go_to_web_site" ) );

            // Event handler for when the Go to web site popup menu item is clicked 
            item.addActionListener( 
                new java.awt.event.ActionListener(  )
                {
                    public void actionPerformed( ActionEvent evt )
                    {
                        //        String[] cmds =
                        //          Utils.substitute( 
                        //            FreeGuide.prefs.commandline.getStrings( "browser_command" ),
                        //          "%filename%",
                        //        programme.getLink(  ).toString(  ).replaceAll( "%", "%%" ) );
                        //Utils.execNoWait( cmds );
                    }
                } );
        }
    }
}
