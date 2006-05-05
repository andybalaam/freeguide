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
package freeguide.common.lib.fgspecific.selection;

import freeguide.common.lib.fgspecific.data.TVProgramme;

import java.util.Iterator;
import java.util.List;

/**
 * Maintains a List of favourites and provides methods for matching
 * programmes to favourites.
 */
class FavouritesHelper
{
    /**
     * Returns the Favourite associated with the given Programme.
     *
     * @param favourites DOCUMENT ME!
     * @param programme DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Favourite getFavourite( 
        List favourites, TVProgramme programme )
    {
        Favourite value = null;

        Iterator favouritesIterator = favourites.iterator(  );

        Favourite currentFavourite;

        while( favouritesIterator.hasNext(  ) )
        {
            currentFavourite = (Favourite)favouritesIterator.next(  );

            if( currentFavourite.matches( programme ) )
            {
                value = currentFavourite;

                break;

            }
        }

        return value;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourites DOCUMENT ME!
     * @param theFavourite DOCUMENT_ME!
     */
    public static void removeFavourite( 
        List favourites, Favourite theFavourite )
    {
        favourites.remove( theFavourite );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourites DOCUMENT ME!
     * @param theFavourite DOCUMENT_ME!
     */
    public static void appendFavourite( 
        List favourites, Favourite theFavourite )
    {
        favourites.add( theFavourite );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param favourites DOCUMENT ME!
     * @param programme DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static boolean isFavourite( List favourites, TVProgramme programme )
    {
        boolean value = false;

        Favourite fav = getFavourite( favourites, programme );

        if( fav != null )
        {
            value = true;

        }

        return value;

    }
}
