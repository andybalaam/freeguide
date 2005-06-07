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

 * --------------------

 * L1R2ButtonPanel.java

 * --------------------

 * (C) Copyright 2000-2002, by Simba Management Limited.

 *

 * Original Author:  David Gilbert (for Simba Management Limited);

 * Modified by: FreeGuide contributors Copyright (c) 2001-2004.

 */
package freeguide.gui.jcommon;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * A 'ready-made' panel that has one button on the left and two buttons on the
 * right - nested panels and layout managers take care of resizing.
 *
 * @author DG
 */
public class L1R2ButtonPanel extends JPanel
{

    /** The left button. */
    private JButton left;

    /** The first button on the right of the panel. */
    private JButton right1;

    /** The second button on the right of the panel. */
    private JButton right2;

    /**
     * Standard constructor - creates a three button panel with the specified
     * button labels.
     *
     * @param label1 the label for button 1.
     * @param label2 the label for button 2.
     * @param label3 the label for button 3.
     */
    public L1R2ButtonPanel( String label1, String label2, String label3 )
    {
        setLayout( new BorderLayout(  ) );

        // create the pieces...
        left = new JButton( label1 );

        JPanel rightButtonPanel = new JPanel( new GridLayout( 1, 2 ) );

        right1 = new JButton( label2 );

        right2 = new JButton( label3 );

        rightButtonPanel.add( right1 );

        rightButtonPanel.add( right2 );

        // ...and put them together
        add( left, BorderLayout.WEST );

        add( rightButtonPanel, BorderLayout.EAST );

    }

    /**
     * Returns a reference to button 1, allowing the caller to set labels,
     * action-listeners etc.
     *
     * @return the left button.
     */
    public JButton getLeftButton(  )
    {

        return left;

    }

    /**
     * Returns a reference to button 2, allowing the caller to set labels,
     * action-listeners etc.
     *
     * @return the right button 1.
     */
    public JButton getRightButton1(  )
    {

        return right1;

    }

    /**
     * Returns a reference to button 3, allowing the caller to set labels,
     * action-listeners etc.
     *
     * @return the right button 2.
     */
    public JButton getRightButton2(  )
    {

        return right2;

    }
}
