/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
package freeguide.gui.dialogs;

import freeguide.*;

import freeguide.lib.fgspecific.*;

import java.util.*;

import javax.swing.*;
import javax.swing.DefaultListModel;

/*
 *  Provides a list of the user's favourites and allows them to add or edit
 *  them by launching a FreeGuideFavouriteEditor.
 *
 * @author     Brendan Corrigan (based on FavouritesListFrame by Andy Balaam)
 * @created    22nd August 2003
 * @version    1
 */
public class FavouritesListDialog extends FGDialog
{
    private javax.swing.JButton butAdd;
    private javax.swing.JButton butCancel;
    private javax.swing.JButton butEdit;
    private javax.swing.JButton butOK;
    private javax.swing.JButton butRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JList list;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private List favourites;
    private Favourite favourite;
    private DefaultListModel favouritesModel;
    private int latestIndex;

    /** The owner frame */
    JFrame owner = null;

    /**
     * Constructor which sets the favourites list up as a JDialog...
     *
     * @param owner - the <code>JFrame</code> from which the dialog is
     *        displayed
     */
    public FavouritesListDialog( JFrame owner )
    {
        super( owner, FreeGuide.msg.getString( "favourites" ) );

        this.owner = owner;

        favouritesModel = new DefaultListModel(  );
        initComponents(  );
        loadFavourites(  );
        fillList(  );
        latestIndex = 0;
        selectLatest(  );

    }

    /**
     * Description of the Method
     */
    private void selectLatest(  )
    {
        list.setSelectedIndex( latestIndex );

    }

    /**
     * Description of the Method
     */
    private void loadFavourites(  )
    {
        favourites = FavouritesList.getInstance(  ).getFavourites(  );

    }

    /**
     * Description of the Method
     */
    private void fillList(  )
    {
        favouritesModel.removeAllElements(  );

        for( int i = 0; i < favourites.size(  ); i++ )
        {
            favouritesModel.addElement( 
                ( (Favourite)( favourites.get( i ) ) ).getName(  ) );
        }
    }

    /**
     * Description of the Method
     */
    private void saveFavourites(  )
    {
        FavouritesList.getInstance(  ).setFavourites( favourites );
        updatedFlag = true;

    }

    private void initComponents(  )
    {
        java.awt.GridBagConstraints gridBagConstraints;

        getContentPane(  ).setLayout( new java.awt.GridBagLayout(  ) );

        jPanel1 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );

        butAdd = new javax.swing.JButton( FreeGuide.msg.getString( "add" ) );
        butAdd.setPreferredSize( new java.awt.Dimension( 83, 26 ) );
        butAdd.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butAddActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butAdd, gridBagConstraints );

        butEdit = new javax.swing.JButton( FreeGuide.msg.getString( "edit" ) );
        butEdit.setPreferredSize( new java.awt.Dimension( 83, 26 ) );
        butEdit.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butEditActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butEdit, gridBagConstraints );

        butRemove = new javax.swing.JButton(
            FreeGuide.msg.getString( "remove" ) );
        butRemove.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butRemoveActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 10 );
        jPanel1.add( butRemove, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane(  ).add( jPanel1, gridBagConstraints );

        list = new javax.swing.JList( favouritesModel );

        jScrollPane1 = new javax.swing.JScrollPane( list );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets( 5, 10, 5, 5 );
        getContentPane(  ).add( jScrollPane1, gridBagConstraints );

        jLabel1 = new javax.swing.JLabel(
            FreeGuide.msg.getString( "your_favourites" ) + ":",
            javax.swing.SwingConstants.CENTER );
        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 5, 5 );
        getContentPane(  ).add( jLabel1, gridBagConstraints );

        jPanel2 = new javax.swing.JPanel( new java.awt.GridBagLayout(  ) );

        butOK = new javax.swing.JButton( FreeGuide.msg.getString( "ok" ) );
        butOK.setPreferredSize( new java.awt.Dimension( 83, 26 ) );
        butOK.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butOKActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 10, 5 );
        jPanel2.add( butOK, gridBagConstraints );

        butCancel = new javax.swing.JButton(
            FreeGuide.msg.getString( "cancel" ) );
        butCancel.setPreferredSize( new java.awt.Dimension( 83, 26 ) );
        butCancel.addActionListener( 
            new java.awt.event.ActionListener(  )
            {
                public void actionPerformed( java.awt.event.ActionEvent evt )
                {
                    butCancelActionPerformed( evt );
                }
            } );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.insets = new java.awt.Insets( 5, 5, 10, 10 );
        jPanel2.add( butCancel, gridBagConstraints );

        gridBagConstraints = new java.awt.GridBagConstraints(  );
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        getContentPane(  ).add( jPanel2, gridBagConstraints );

        getRootPane(  ).setDefaultButton( butOK );

        pack(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butRemoveActionPerformed( java.awt.event.ActionEvent evt )
    {
        latestIndex = list.getSelectedIndex(  );

        if( latestIndex == ( favouritesModel.size(  ) - 1 ) )
        {
            latestIndex--;
        }

        int[] sel = list.getSelectedIndices(  );

        for( int i = 0; i < sel.length; i++ )
        {
            favourites.remove( sel[i] );

        }

        reShow(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butCancelActionPerformed( java.awt.event.ActionEvent evt )
    {
        quit(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butOKActionPerformed( java.awt.event.ActionEvent evt )
    {
        saveFavourites(  );
        quit(  );

    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butEditActionPerformed( java.awt.event.ActionEvent evt )
    {
        latestIndex = list.getSelectedIndex(  );

        int i = list.getSelectedIndex(  );

        if( i != -1 )
        {

            Favourite fav = (Favourite)favourites.get( i );
            new FavouriteEditorDialog( 
                this, FreeGuide.msg.getString( "edit_favourite" ), fav )
            .setVisible( true );
        }
    }

    /**
     * Description of the Method
     *
     * @param evt Description of the Parameter
     */
    private void butAddActionPerformed( java.awt.event.ActionEvent evt )
    {

        Favourite newFav = new Favourite(  );
        favourites.add( newFav );
        new FavouriteEditorDialog( 
            this, FreeGuide.msg.getString( "add_a_new_favourite" ), newFav )
        .setVisible( true );
        latestIndex = favouritesModel.size(  );

        reShow(  );

    }

    /**
     * Description of the Method
     */
    public void reShow(  )
    {
        fillList(  );
        selectLatest(  );
    }
}
