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

package freeguide.lib.fgspecific;

import freeguide.*;
import java.util.*;

/**
 * Maintains a List of favourites and provides methods for matching programmes
 * to favourites.
 */
public class FavouritesList {

    private static FavouritesList favouritesList;

    private List favourites;

    private FavouritesList() {
        readFavourites();
    }

    /**
     * Returns an instance of FavouritesList.  This method assumes that
     * there will be only one FavouritesList per instance of FreeGuide.
     */
    public static FavouritesList getInstance() {
        if (favouritesList == null) {
            favouritesList = new FavouritesList();
        }
        return favouritesList;
    }

    public List getFavourites() {
        // Retrieve the favourites from the backing store
        return favourites;
    }

    public void setFavourites( List favourites ) {
        this.favourites = favourites;
        // Save the favourites to the backing store
        FreeGuide.prefs.replaceFavourites( favourites );
    }

    /**
     * Returns the Favourite associated with the given Programme.
     */
    public Favourite getFavourite(Programme programme) {
        Favourite value = null;
        Iterator favouritesIterator = favourites.iterator();
        Favourite currentFavourite;
        while ( favouritesIterator.hasNext() ) {
            currentFavourite = (Favourite)favouritesIterator.next();
            if ( currentFavourite.matches( programme ) ) {
                value = currentFavourite;
                break;
            }
        }
        return value;
    }

    public void removeFavourite( Favourite theFavourite ) {
        int index = favourites.indexOf( theFavourite );
        if( index > -1 ) {
            FreeGuide.prefs.favourites.removeFavourite( index + 1 );
        }
        readFavourites();
    }

    public void appendFavourite( Favourite theFavourite ) {
        FreeGuide.prefs.favourites.appendFavourite( theFavourite );
        readFavourites();
    }


    public boolean isFavourite( Programme programme ) {
        boolean value = false;
        Favourite fav = getFavourite( programme );
        if ( fav != null) {
            value = true;
        }
        return value;
    }

    private void readFavourites() {
        // Retrieve the favourites from the backing store
        favourites = FreeGuide.prefs.getFavourites();
    }

}
