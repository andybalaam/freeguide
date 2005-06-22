package freeguide.plugins.ui.horizontal;

import freeguide.lib.fgspecific.Application;
import freeguide.lib.fgspecific.data.TVProgramme;

import java.awt.event.ActionEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ProgrammePopupMenu extends JPopupMenu
{

    /**
     * DOCUMENT_ME!
     *
     * @param label DOCUMENT_ME!
     */
    public void display( final ProgrammeJLabel label )
    {

        final MenuElement[] it = getSubElements(  );

        for( int i = it.length - 1; i >= 0; i-- )
        {
            remove( i );
        }

        for( int i = 0; i < label.reminders.length; i++ )
        {
            label.reminders[i].addItemsToPopupMenu( label.programme, this );
        }

        if( label.programme.getLink(  ) != null )
        {

            JMenuItem item = new JMenuItem(  );
            item.setText( 
                Application.getInstance(  ).getLocalizedMessage( 
                    "go_to_web_site" ) );

            // Event handler for when the Go to web site popup menu item is clicked 
            item.addActionListener( 
                new java.awt.event.ActionListener(  )
                {
                    public void actionPerformed( ActionEvent evt )
                    {

                        TVProgramme programme =
                            label.getModel(  ).getValue(  );

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

    /*getActionMap(  ).put(
"select",
new AbstractAction(  )
{
public void actionPerformed( ActionEvent e )
{
toggleSelection(  );

}
} );

getActionMap(  ).put(
"favourite",
new AbstractAction(  )
{
public void actionPerformed( ActionEvent e )
{
setFavourite( !getModel(  ).isFavourite(  ) );

}
} );

getActionMap(  ).put(
"menu",
new AbstractAction(  )
{
public void actionPerformed( ActionEvent e )
{

ProgrammePopupMenu menu = getPopupMenu(  );

menu.show( ProgrammeJLabel.this, 0, getHeight(  ) );

}
} );*/
}
