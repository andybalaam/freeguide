/*

 *  FreeGuide J2

 *

 *  Copyright (c) 2001-2004 by Andy Balaam and the FreeGuide contributors

 *

 *  Released under the GNU General Public License

 *  with ABSOLUTELY NO WARRANTY.

 *

 *  See the file COPYING for more information.

 */
package freeguide.plugins.ui.horizontal;

import freeguide.lib.fgspecific.data.TVChannel;
import freeguide.lib.fgspecific.data.TVChannelsSet;
import freeguide.lib.fgspecific.data.TVData;
import freeguide.lib.fgspecific.data.TVIteratorProgrammes;
import freeguide.lib.fgspecific.data.TVProgramme;

import freeguide.plugins.ui.horizontal.components.StripView;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The StripView.Model for a Programme
 *
 * @author Andy Balaam
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class ProgrammeStripModel implements StripView.Model
{

    // An array of TreeMaps.
    // Index by row (channel) number to get a TreeMap containing all
    // the Programme objects of that channel for the period.
    private TreeMap[] programmes;
    private int rowCount;

    /**
     * DOCUMENT ME!
     *
     * @param channelsSet
     * @param data this data MUST include only channels from channelsSet
     */
    ProgrammeStripModel( final TVChannelsSet channelsSet, TVData data )
    {
        rowCount = data.getChannelsCount(  );

        programmes = new TreeMap[rowCount];

        for( int i = 0; i < rowCount; i++ )
        {
            programmes[i] = new TreeMap(  );

        }

        data.iterate( 
            new TVIteratorProgrammes(  )
            {
                protected void onChannel( TVChannel channel )
                {
                }

                public void onProgramme( TVProgramme programme )
                {

                    int chNo =
                        channelsSet.getChannelIndex( 
                            getCurrentChannel(  ).getID(  ) );

                    if( chNo != -1 )
                    {
                        programmes[chNo].put( 
                            new Long( programme.getStart(  ) ), programme );

                    }
                }
            } );
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getRowCount(  )
    {

        return rowCount;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param rowIndex DOCUMENT_ME!
     * @param x DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public StripView.Strip getValueAt( int rowIndex, long x )
    {

        if( rowIndex >= programmes.length )
        {

            return null;

        }

        Long toKey = new Long( x + 1 );

        SortedMap head = programmes[rowIndex].headMap( toKey );

        if( head.isEmpty(  ) )
        {

            return null;

        }

        TVProgramme p = (TVProgramme)head.get( head.lastKey(  ) );

        return new StripView.Strip( p.getStart(  ), p.getEnd(  ), p );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param rowIndex DOCUMENT_ME!
     * @param x DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public StripView.Strip getNextStrip( int rowIndex, long x )
    {

        if( rowIndex >= programmes.length )
        {

            return null;

        }

        Long fromKey = new Long( x );

        SortedMap tail = programmes[rowIndex].tailMap( fromKey );

        if( tail.isEmpty(  ) )
        {

            return null;

        }

        TVProgramme p = (TVProgramme)tail.get( tail.firstKey(  ) );

        return new StripView.Strip( p.getStart(  ), p.getEnd(  ), p );

    }
}
