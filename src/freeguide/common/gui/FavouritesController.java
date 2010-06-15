package freeguide.common.gui;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannelsSet;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.common.lib.general.StringHelper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;

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
    private DefaultListModel favouritesModel;
    private int latestIndex;
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
        this.allChannelsSet = allChannelsSet;
        this.favourites = new ArrayList( fav.size(  ) );

        for( int i = 0; i < fav.size(  ); i++ )
        {
            Favourite fave = (Favourite)fav.get( i );
            this.favourites.add( fave.clone(  ) );
        }

        Collections.sort( this.favourites, Favourite.GetNameComparator() );

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

                    if(
                        new FavouriteEditorDialog(
                                listDialog,
                                Application.getInstance(  )
                                               .getLocalizedMessage(
                                    "add_a_new_favourite" ), newFav,
                                allChannelsSet ).showDialog(  ) )
                    {
                        // save the favourite only if one was created
                        latestIndex = favourites.size(  );
                        favourites.add( newFav );

                        changed = true;

                        // update the display
                        reShow(  );
                    }
                }
            } );
        listDialog.getBtnEdit(  ).addActionListener(
            new ActionListener(  )
            {
                public void actionPerformed( ActionEvent e )
                {
                    latestIndex = listDialog.getList(  ).getSelectedIndex(  );

                    if( latestIndex != -1 )
                    {
                        Favourite fav = (Favourite)favourites.get( latestIndex );

                        if(
                            new FavouriteEditorDialog(
                                    listDialog,
                                    Application.getInstance(  )
                                                   .getLocalizedMessage(
                                        "edit_favourite" ), fav, allChannelsSet )
                                .showDialog(  ) )
                        {
                            changed = true;

                            // update the display
                            reShow(  );
                        }
                    }

                    reShow();
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
        JList list = listDialog.getList(  );

        list.setSelectedIndex( latestIndex );
        list.ensureIndexIsVisible(latestIndex);
    }

    private void fillList(  )
    {
        favouritesModel.removeAllElements(  );

        for( int i = 0; i < favourites.size(  ); i++ )
        {
            // Ensure that we have some kind of name, even if something
            // went wrong when creating the favourite.
            String name = ( (Favourite)( favourites.get( i ) ) ).getName(  );

            if( ( name == null ) ||
                ( name.equals( StringHelper.EMPTY_STRING ) ) )
            {
                name = Application.getInstance(  )
                                  .getLocalizedMessage( "all_programmes" );
            }

            favouritesModel.addElement( name );
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
