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

import java.util.*;

/**
 * A copy of the java.util.prefs.PreferenceChangeEvent class that applies to
 * the FGPreferences object instead of the java.utils.prefs.Preferences one.
 *
 *@author     Andy Balaam
 *@created    23 Feb 2005
 *@version    1
 */

public class FGPreferenceChangeEvent extends EventObject {
    
    String key;
    Object value;
    FGPreferences node;
    
    FGPreferenceChangeEvent( String key, Object value, FGPreferences node ) {
        super( node );
        
        this.key = key;
        this.value = value;
        this.node = node;
        
    }
    
    public String getKey() {
        return key;
    }
    
    public Object getNewValue() {
        return value;
    }
    
    public FGPreferences getNode() {
        return node;
    }
    
    public String toString() {
        
        return "FGPreferenceChangeEvent: node=" + node + " key=" + key
            + " value=" + value + ".";
        
    }
    
}
