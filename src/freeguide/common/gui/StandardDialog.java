/* =======================================================

 * JCommon : a free general purpose class library for Java

 * =======================================================

 *

 * Project Info:  http://www.object-refinery.com/jcommon/index.html

 * Project Lead:  David Gilbert (david.gilbert@object-refinery.com);

 *

 * (C) Copyright 2000-2002, by Simba Management Limited and Contributors.

 *

 * This library is free software; you can redistribute it and/or modify it

 * under the terms of the GNU Lesser General Public License as published by the

 * Free Software Foundation; either version 2.1 of the License, or (at your

 * option) any later version.

 *

 * This library is distributed in the hope that it will be useful, but WITHOUT

 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or

 * FITNESS FOR A PARTICULAR PURPOSE.

 * See the GNU Lesser General Public License for more details.

 *

 * You should have received a copy of the GNU Lesser General Public License

 * along with this library; if not, write to the Free Software Foundation,

 * Inc., 59 Temple Place, Suite 330,

 * Boston, MA 02111-1307, USA.

 *

 * -------------------

 * StandardDialog.java

 * -------------------

 * (C) Copyright 2000-2002, by Simba Management Limited.

 *

 * Original Author:  David Gilbert (for Simba Management Limited);

 * Modified by: FreeGuide contributors Copyright (c) 2001-2004.

 */
package freeguide.common.gui;

import freeguide.common.lib.fgspecific.Application;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 * The base class for standard dialogs.
 *
 * @author DG
 */
public class StandardDialog extends JDialog implements ActionListener
{
    protected static final String ACTION_OK = "okButton";
    protected static final String ACTION_HELP = "helpButton";
    protected static final String ACTION_CANCEL = "cancelButton";

    /** Flag that indicates whether or not the dialog was cancelled. */
    private boolean cancelled;

    /**
     * Standard constructor - builds a dialog...
     *
     * @param owner the owner.
     * @param title the title.
     * @param modal modal?
     */
    public StandardDialog( Frame owner, String title, boolean modal )
    {
        super( owner, title, modal );

        this.cancelled = false;

    }

    /**
     * Standard constructor - builds a dialog...
     *
     * @param owner the owner.
     * @param title the title.
     * @param modal modal?
     */
    public StandardDialog( Dialog owner, String title, boolean modal )
    {
        super( owner, title, modal );

        this.cancelled = false;

    }

    /**
     * Returns a flag that indicates whether or not the dialog has
     * been cancelled.
     *
     * @return boolean.
     */
    public boolean isCancelled(  )
    {
        return this.cancelled;

    }

    /**
     * Handles clicks on the standard buttons.
     *
     * @param event the event.
     */
    public void actionPerformed( ActionEvent event )
    {
        String command = event.getActionCommand(  );

        if( command.equals( ACTION_HELP ) )
        {
            // display help information
        }

        else if( command.equals( ACTION_OK ) )
        {
            this.cancelled = false;

            setVisible( false );

        }

        else if( command.equals( ACTION_CANCEL ) )
        {
            this.cancelled = true;

            setVisible( false );

        }
    }

    /**
     * Builds and returns the user interface for the dialog.  This
     * method is shared among the constructors.
     *
     * @return the button panel.
     */
    protected L1R2ButtonPanel createButtonPanel(  )
    {
        L1R2ButtonPanel buttons =
            new L1R2ButtonPanel(
                Application.getInstance(  ).getLocalizedMessage( "help" ),
                Application.getInstance(  ).getLocalizedMessage( "cancel" ),
                Application.getInstance(  ).getLocalizedMessage( "ok" ) );

        JButton helpButton = buttons.getLeftButton(  );

        helpButton.setActionCommand( ACTION_HELP );

        helpButton.addActionListener( this );

        JButton cancelButton = buttons.getRightButton1(  );

        cancelButton.setActionCommand( ACTION_CANCEL );

        cancelButton.addActionListener( this );

        JButton okButton = buttons.getRightButton2(  );

        okButton.setActionCommand( ACTION_OK );

        okButton.addActionListener( this );

        buttons.setBorder( BorderFactory.createEmptyBorder( 4, 0, 0, 0 ) );

        return buttons;

    }
}
