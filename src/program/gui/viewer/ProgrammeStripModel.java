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

package freeguide.gui.viewer;

import freeguide.lib.general.*;
import freeguide.lib.fgspecific.*;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.SortedMap;
import java.util.Vector;

/**
 * The StripView.Model for a Programme
 *
 *@author     
 *@created    
 *@version    1
 */

public class ProgrammeStripModel implements StripView.Model {
    // An array of TreeMaps.
    // Index by row (channel) number to get a TreeMap containing all
    // the Programme objects of that channel for the period.
    private ArrayList programmes;
    private int rowCount;

    ProgrammeStripModel(
        ChannelSetInterface chSet,
        ViewerFrameXMLTVLoader xmltvLoader
    ) {
        rowCount = chSet.getNoChannels();

        programmes = new ArrayList();
        int num_progs = xmltvLoader.programmes.size();
        for (int p = 0; p < num_progs; p++) {
            Programme prog = (Programme)xmltvLoader.programmes.get(p);
            int	chNo = chSet.getChannelNo(prog.getChannel());
            if (chNo < 0)
                continue;

            TreeMap channelProgs = null;
            int size = programmes.size();
            if (chNo < size) {
                channelProgs = (TreeMap)programmes.get(chNo);
            } else {
                for (int i = size; i <= chNo; i++)
                    programmes.add(null);
            }
            if (channelProgs == null) {
                channelProgs = new TreeMap();
                programmes.set(chNo, channelProgs);
            }

            channelProgs.put(new Long(prog.getStart().getTimeInMillis()), prog);
        }

    }

    public int getRowCount() {
        return rowCount;
    }

    public StripView.Strip getValueAt(int rowIndex, long x) {
        if (rowIndex >= programmes.size())
            return null;

        TreeMap channelProgs = (TreeMap)programmes.get(rowIndex);
        if (channelProgs == null)
            return null;

        Long toKey = new Long(x + 1);
        SortedMap head = channelProgs.headMap(toKey);

        if (head.isEmpty())
            return null;

        Programme p = (Programme)head.get(head.lastKey());

        return new StripView.Strip(
            p.getStart().getTimeInMillis(),
            p.getEnd().getTimeInMillis(),
            p
        );
    }

    public StripView.Strip getNextStrip(int rowIndex, long x) {
        if (rowIndex >= programmes.size())
            return null;

        TreeMap channelProgs = (TreeMap)programmes.get(rowIndex);
        if (channelProgs == null)
            return null;

        Long fromKey = new Long(x);
        SortedMap tail = channelProgs.tailMap(fromKey);
        if (tail.isEmpty())
            return null;

        Programme p = (Programme)tail.get(tail.firstKey());
        return new StripView.Strip(
            p.getStart().getTimeInMillis(),
            p.getEnd().getTimeInMillis(),
            p
        );
    }

    /*
    public void setValueAt(Object aValue, int rowIndex, long x1, long x2) {
    }
    */

    public Vector getAll() {
        Vector v = new Vector();

        if (programmes == null)
                return v;

        for (int i = 0; i < programmes.size(); i++) {
            TreeMap t = (TreeMap)programmes.get(i);
            if (t != null)
                v.addAll(t.values());
        }

        return v;
    }
}
