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
 * ----------------------
 * FontChooserDialog.java
 * ----------------------
 * (C) Copyright 2000-2002, by Simba Management Limited.
 *
 * Original Author:  David Gilbert (for Simba Management Limited);
 * Modified by: FreeGuide contributors Copyright (c) 2001-2004.
 */

package freeguide.gui.jcommon;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Dialog;
import java.awt.Frame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

/**
 * A dialog for choosing a font from the available system fonts.
 *
 * @author DG
 */
public class FontChooserDialog extends StandardDialog {

    /** The panel within the dialog that contains the font selection controls. */
    private FontChooserPanel fontChooserPanel;

    /**
     * Standard constructor - builds a font chooser dialog owned by another dialog.
     *
     * @param owner  the dialog that 'owns' this dialog.
     * @param title  the title for the dialog.
     * @param modal  a boolean that indicates whether or not the dialog is modal.
     * @param font  the initial font displayed.
     */
    public FontChooserDialog(Dialog owner, String title, boolean modal, Font font) {
        super(owner, title, modal);
        setContentPane(createContent(font));
    }

    /**
     * Standard constructor - builds a font chooser dialog owned by a frame.
     *
     * @param owner  the frame that 'owns' this dialog.
     * @param title  the title for the dialog.
     * @param modal  a boolean that indicates whether or not the dialog is modal.
     * @param font  the initial font displayed.
     */
    public FontChooserDialog(Frame owner, String title, boolean modal, Font font) {
        super(owner, title, modal);
        setContentPane(createContent(font));
    }

    /**
     * Returns the selected font.
     *
     * @return the font.
     */
    public Font getSelectedFont() {
        return fontChooserPanel.getSelectedFont();
    }

    /**
     * Returns the panel that is the user interface.
     *
     * @param font  the font.
     *
     * @return the panel.
     */
    private JPanel createContent(Font font) {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        if (font == null) {
            font = new Font("Dialog", 10, Font.PLAIN);
        }
        fontChooserPanel = new FontChooserPanel(font);
        content.add(fontChooserPanel);

        L1R2ButtonPanel buttons = createButtonPanel();
        buttons.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));
        content.add(buttons, BorderLayout.SOUTH);
		
		getRootPane().setDefaultButton( buttons.getRightButton2() );
		
        return content;
    }
	
}
