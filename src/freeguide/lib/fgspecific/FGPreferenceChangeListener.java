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
 * A copy of the java.util.prefs.PreferenceChangeListener interface that
 * applies to the FGPreferences object instead of the
 * java.utils.prefs.Preferences one.
 *
 * @author Andy Balaam
 * @version 1
 */
public interface FGPreferenceChangeListener extends EventListener
{
    void preferenceChange( FGPreferenceChangeEvent evt );
}
