package freeguide.gui.dialogs;

import freeguide.FreeGuide;

import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.selection.Favourite;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;

/**
 * DOCUMENT ME!
 *
 * @author Alex Buloichik (alex73 at zaval.org) based on FavouritesListDialog
 *         by Brendan Corrigan, based on FavouritesListFrame by Andy Balaam
 */
public class FavouritesController
{

    private TVChannelsSet allChannelsSet;
    private final List favourites;
    private Favourite favourite;
    private DefaultListModel favouritesModel;
    private int latestIndex;
    private JFrame owner = null;
    private final FavouritesListDialog listDialog;
    private boolean changed = false;

    /**
     * Creates a new FavouritesController object.
     *
     * @param owner DOCUMENT ME!
     * @param fav DOCUMENT ME!
     * @param allChannelsSet DOCUMENT ME!
     */
    public FavouritesController( 
        JFrame owner, List fav, TVChannelsSet allChannelsSet )
    {
        listDialog = new FavouritesListDialog( owner );
        this.owner = owner;
        this.allChannelsSet = allChannelsSet;
        this.favourites = new ArrayList( fav.size(  ) );

        for( int i = 0; i < fav.size(  ); i++ )
        {

            Favourite fave = (Favourite)fav.get( i );
            this.favourites.add( fave.clone(  ) );
        }

        favouritesModel = new DefaultListModel(  );
        listDialog.getList(  ).setModel( favouritesModel );
        fillList(  );
        latestIndex = 0;
        selectLatest(  );
        setupList(  );
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the listDialog.
     */
    public FavouritesListDialog getListDialog(  )
    {

        return listDialog;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the changed.
     */
    public boolean isChanged(  )
    {

        return changed;
    }

    /**
     * DOCUMENT_ME!
     */
    public void setupList(  )
    {
        listDialog.getBtnAdd(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {

                    Favourite newFav = new Favourite(  );
                    favourites.add( newFav );

                    if( 
                        new FavouriteEditorDialog( 
                                listDialog,
                                FreeGuide.msg.getString( 
                                    "add_a_new_favourite" ), newFav,
                                allChannelsSet ).showDialog(  ) )
                    {
                        changed = true;
                    }

                    latestIndex = favouritesModel.size(  );
                    reShow(  );
                }
            } );
        listDialog.getBtnEdit(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    latestIndex = listDialog.getList(  ).getSelectedIndex(  );

                    int i = listDialog.getList(  ).getSelectedIndex(  );

                    if( i != -1 )
                    {

                        Favourite fav = (Favourite)favourites.get( i );

                        if( 
                            new FavouriteEditorDialog( 
                                    listDialog,
                                    FreeGuide.msg.getString( "edit_favourite" ),
                                    fav, allChannelsSet ).showDialog(  ) )
                        {
                            changed = true;
                        }
                    }
                }
            } );
        listDialog.getBtnRemove(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    latestIndex = listDialog.getList(  ).getSelectedIndex(  );

                    if( latestIndex == ( favouritesModel.size(  ) - 1 ) )
                    {
                        latestIndex--;
                    }

                    int[] sel = listDialog.getList(  ).getSelectedIndices(  );

                    for( int i = 0; i < sel.length; i++ )
                    {
                        favourites.remove( sel[i] );
                        changed = true;
                    }

                    reShow(  );
                }
            } );
        listDialog.getBtnOK(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    listDialog.dispose(  );
                }
            } );
        listDialog.getBtnCancel(  ).addActionListener( 
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    changed = false;
                    listDialog.dispose(  );
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public List getFavourites(  )
    {

        return favourites;
    }

    private void selectLatest(  )
    {
        listDialog.getList(  ).setSelectedIndex( latestIndex );
    }

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
     * DOCUMENT_ME!
     */
    public void reShow(  )
    {
        fillList(  );
        selectLatest(  );
    }
}
