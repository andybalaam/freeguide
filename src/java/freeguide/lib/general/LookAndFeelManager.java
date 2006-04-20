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
package freeguide.plugins.program.freeguide.lib.general;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.LookAndFeel;
import javax.swing.UIManager;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class LookAndFeelManager
{

    /**
     * Place to store name/classname of available L&F's so we only have to
     * check availability the hard way once.
     */
    private static Map availableLAFs = null;

    /** Looks and Feels we know of */
    private static final Map knownLAFs = new HashMap(  );

    // initialize the knownLAFs map
    // Sun's defaults and those listed at www.javootoo.com
    static
    {
        knownLAFs.put( "GTK+", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel" );

        // Will need to provide additional information for loading the
        // configuration file for Synth, but Sun hasn't yet finished
        // the documentation for the configuration file format. :)
        // Will incorporate a dynamic contol for specifying Synth
        // configuration file when Synth is selected or Metal Themes
        // when Metal is selected.
        knownLAFs.put( "Synth", "javax.swing.plaf.synth.SynthLookAndFeel" );

        knownLAFs.put( "MacOS", "com.sun.java.swing.plaf.mac.MacLookAndFeel" );

        knownLAFs.put( "Metal", "javax.swing.plaf.metal.MetalLookAndFeel" );

        knownLAFs.put( 
            "CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel" );

        knownLAFs.put( 
            "Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel" );

        knownLAFs.put( 
            "Windows Classic",
            "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel" );

        // LGPL - http://www.incors.org/
        knownLAFs.put( 
            "Kunststoff", "com.incors.plaf.kunststoff.KunststoffLookAndFeel" );

        // Compiere Public License (Modified MPL) -
        // http://sourceforge.net/projects/compiere/
        knownLAFs.put( "Compiere", "org.compiere.plaf.CompierePLAF" );

        // GPL - http://www.geocities.com/shfarr/
        knownLAFs.put( "Fh", "com.shfarr.ui.plaf.fh.FhLookAndFeel" );

        // LGPL - http://gtkswing.sourceforge.net/
        knownLAFs.put( 
            "GTK/Swing", "org.gtk.java.swing.plaf.gtk.GtkLookAndFeel" );

        // HippoLF Software Lic - http://www.diod.se/
        knownLAFs.put( "HippoLF", "se.diod.hippo.plaf.HippoLookAndFeel" );

        // BSD License - http://looks.dev.java.net/
        knownLAFs.put( 
            "JGoodies Plastic", "com.jgoodies.plaf.plastic.PlasticLookAndFeel" );

        knownLAFs.put( 
            "JGoodies Plastic 3D",
            "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel" );

        knownLAFs.put( 
            "JGoodies Plastic XP",
            "com.jgoodies.plaf.plastic.PlasticXPLookAndFeel" );

        knownLAFs.put( 
            "ExtWindows", "com.jgoodies.plaf.windows.ExtWindowsLookAndFeel" );

        // LGPL - http://liquidlnf.sourceforge.net/
        knownLAFs.put( "Liquid", "com.birosoft.liquid.LiquidLookAndFeel" );

        // LGPL - http://mlf.sourceforge.net/
        knownLAFs.put( 
            "Metouia", "net.sourceforge.mlf.metouia.MetouiaLookAndFeel" );

        // Unknown License -
        // http://hp.vector.co.jp/authors/VA008030/swing/
        knownLAFs.put( "NEXT L&F", "nextlf.plaf.NextLookAndFeel" );

        // OYOAHA License (Apache-like) -
        // http://www.oyoaha.com/lookandfeel/ 
        knownLAFs.put( 
            "Oyoaha", "com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel" );

        // GPL - http://www.memoire.com/guillaume-desnoix/slaf/
        knownLAFs.put( "SLAF", "com.memoire.slaf.SlafLookAndFeel" );

        // SkinLF License -
        // http://www.l2fprod.com/
        knownLAFs.put( "SkinLF", "com.l2fprod.gui.plaf.skin.SkinLookAndFeel" );

        // LGPL - http://www.muntjak.de/hans/java/tinylaf/index.html
        knownLAFs.put( 
            "TinyLaF", "de.muntjak.tinylookandfeel.TinyLookAndFeel" );

        // LGPL - http://www.muntjak.de/hans/java/tinylaf/index.html
        knownLAFs.put( 
            "TinyLaF", "de.muntjak.tinylookandfeel.TinyLookAndFeel" );

        // Unknown License - 
        // http://www.geekfarm.org/emeade/index.html?teknolust_plaf/index.html
        knownLAFs.put( 
            "Teknolust", "com.teknolust.plaf.teknolust.TeknolustLookAndFeel" );

        // Tonic Look And Feel License
        // http://www.digitprop.com/p.php?page=toniclf&lang=eng
        knownLAFs.put( "Tonic", "com.digitprop.tonic.TonicLookAndFeel" );

        // LGPL - http://www.stefan-krause.com/java/
        knownLAFs.put( "XP", "com.stefankrause.xplookandfeel.XPLookAndFeel" );

    }

    /* Don't let anyone instantate this class */
    private LookAndFeelManager(  )
    {
    }

    /**
     * Returns the name of the class that provides the
     * <code>lookAndFeelName</code> Look & Feel.
     *
     * @param lookAndFeelName DOCUMENT ME!
     *
     * @return DOCUMENT_ME!
     */
    public static String getLookAndFeelClassName( String lookAndFeelName )
    {

        Map map = getMap(  );

        return (String)map.get( lookAndFeelName );

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static List getAvailableLooksAndFeels(  )
    {

        Map map = getMap(  );

        Set keys = map.keySet(  );

        ArrayList list = new ArrayList( keys );

        Collections.sort( list );

        return list;

    }

    private static Map getMap(  )
    {

        if( availableLAFs == null )
        {
            createMap(  );

        }

        return availableLAFs;

    }

    private static void createMap(  )
    {
        availableLAFs = new HashMap(  );

        // Populate the Map with the "installed" Looks and Feels
        String className = null;

        UIManager.LookAndFeelInfo[] laf;

        laf = UIManager.getInstalledLookAndFeels(  );

        for( int i = 0; i < laf.length; i++ )
        {
            className = laf[i].getClassName(  );

            if( isAvailable( className ) )
            {
                availableLAFs.put( laf[i].getName(  ), className );

            }
        }

        // Check the "standard" ones
        // since the UIManager sometimes "forgets" some
        Set knownLAFsKeys = knownLAFs.keySet(  );

        Iterator knownLAFsIterator = knownLAFsKeys.iterator(  );

        Object key;

        Object value;

        while( knownLAFsIterator.hasNext(  ) )
        {
            key = knownLAFsIterator.next(  );

            value = knownLAFs.get( key );

            // Compare values in case our names are different
            if( !availableLAFs.containsValue( value ) )
            {

                if( isAvailable( value.toString(  ) ) )
                {
                    availableLAFs.put( key, value );

                }
            }
        }
    }

    private static boolean isAvailable( String lookAndFeelClassName )
    {

        boolean value = false;

        try
        {

            Class lafClass = Class.forName( lookAndFeelClassName );

            LookAndFeel laf = (LookAndFeel)( lafClass.newInstance(  ) );

            value = laf.isSupportedLookAndFeel(  );

        }

        catch( ClassNotFoundException e )
        {
        }

        catch( InstantiationException e )
        {
        }

        catch( IllegalAccessException e )
        {
        }

        return value;

    }
}
