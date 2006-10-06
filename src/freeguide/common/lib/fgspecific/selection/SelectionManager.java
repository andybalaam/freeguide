package freeguide.common.lib.fgspecific.selection;

import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.general.PreferencesHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Manager for favourites and manual selection. Each programme can have
 * one from 3 states: in "white" list, in "black" list , and not in list. If
 * it in "white" list, then we select it manually and this program will be in
 * guide. If it in "black" list, then we deselect it manually and this
 * program will not be in guide. If it not in lists, then we test favourites
 * for decide - should this program be in guide.
 *
 * @author Alex Buloichik (alex73 at zaval.org)
 */
public class SelectionManager
{
    final static protected Storage storage = new Storage(  );

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isFavourite( final TVProgramme programme )
    {
        synchronized( storage )
        {
            for( int i = 0; i < storage.favouritesList.size(  ); i++ )
            {
                Favourite fav = (Favourite)storage.favouritesList
                    .get( i );

                if( fav.matches( programme ) )
                {
                    return true;

                }
            }
        }

        return false;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isInGuide( final TVProgramme programme )
    {
        synchronized( storage )
        {
            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                return sel.isSelected(  );

            }

            else
            {
                return isFavourite( programme );

            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public static void selectProgramme( final TVProgramme programme )
    {
        synchronized( storage )
        {
            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                sel.setSelected( true );

            }

            else
            {
                storage.manualSelectionList.add( 
                    new ManualSelection( programme, true, true ) );

            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public static void deselectProgramme( final TVProgramme programme )
    {
        synchronized( storage )
        {
            ManualSelection sel = getManualSelection( programme );

            if( sel != null )
            {
                sel.setSelected( false );

            }

            else
            {
                storage.manualSelectionList.add( 
                    new ManualSelection( programme, false, false ) );

            }
        }
    }

    protected static ManualSelection getManualSelection( 
        final TVProgramme programme )
    {
        synchronized( storage )
        {
            for( int i = 0; i < storage.manualSelectionList.size(  ); i++ )
            {
                ManualSelection sel =
                    (ManualSelection)storage.manualSelectionList.get( i );

                if( sel.matches( programme ) )
                {
                    return sel;

                }
            }
        }

        return null;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourite DOCUMENT_ME!
     */
    public static void addFavourite( final Favourite favourite )
    {
        synchronized( storage )
        {
            storage.favouritesList.add( favourite );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public static void addFavouriteByProgramme( final TVProgramme programme )
    {
        synchronized( storage )
        {
            Favourite f = new Favourite(  );

            f.setTitleString( programme.getTitle(  ) );

            f.setName( programme.getTitle(  ) );

            storage.favouritesList.add( f );

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param programme DOCUMENT_ME!
     */
    public static void removeFavouriteByProgramme( 
        final TVProgramme programme )
    {
        synchronized( storage )
        {
            Iterator it = storage.favouritesList.iterator(  );

            while( it.hasNext(  ) )
            {
                Favourite fav = (Favourite)it.next(  );

                if( fav.matches( programme ) )
                {
                    it.remove(  );

                }
            }
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static List getFavouritesList(  )
    {
        return storage.favouritesList;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param list DOCUMENT_ME!
     */
    public static void setFavouritesList( final List list )
    {
        storage.favouritesList = list;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param node DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void load( Preferences node ) throws Exception
    {
        PreferencesHelper.load( node, storage );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param node DOCUMENT_ME!
     *
     * @throws Exception DOCUMENT_ME!
     */
    public static void save( Preferences node ) throws Exception
    {
        PreferencesHelper.save( node, storage );

    }

    /**
     * DOCUMENT ME!
     *
     * @author $author$
     * @version $Revision$
     */
    public static class Storage
    {
        /** DOCUMENT ME! */
        public static final Class favouritesList_TYPE = Favourite.class;

        /** DOCUMENT ME! */
        public static final Class manualSelectionList_TYPE =
            ManualSelection.class;

        /** DOCUMENT ME! */
        public List favouritesList = new ArrayList(  );

        /** DOCUMENT ME! */
        public List manualSelectionList = new ArrayList(  );
    }
}
