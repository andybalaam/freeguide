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
package freeguide.lib.general;

/**
 * Version.java Holds the version of a program (e.g. FreeGuide, Java) and
 * allows comparison between them. Among other things, this class is used to
 * check we have the corrcet version of Java, so it does not depend on any
 * post-1.0 features.
 *
 * @author Andy Balaam
 * @version 1
 */
public class Version
{

    // ----------------------------------------------------------------------

    /** DOCUMENT ME! */
    public int major;

    /** DOCUMENT ME! */
    public int minor;

    /** DOCUMENT ME! */
    public int revision;

    /**
     * Create a Version object with the given major, minor and revision
     * numbers
     *
     * @param major DOCUMENT ME!
     * @param minor DOCUMENT ME!
     * @param revision DOCUMENT ME!
     */
    public Version( int major, int minor, int revision )
    {
        this.major = major;

        this.minor = minor;

        this.revision = revision;

    }

    /**
     * Create a Version object from a string that looks like this: d.d.d_x
     * where d represents any number of digits . is a literal dot _ is either
     * an _, - or . character x is any sequence of characters
     *
     * @param version_string DOCUMENT ME!
     *
     * @throws NumberFormatException DOCUMENT ME!
     */
    public Version( String version_string ) throws NumberFormatException
    {

        if( version_string == null )
        {

            return;
        }

        String[] split_version = new String[3];

        int pos = 0;

        int oldpos = 0;

        for( int i = 0; i < 3; i++ )
        {
            pos = version_string.indexOf( '.', oldpos );

            if( pos == -1 )
            {
                pos = version_string.indexOf( '_', oldpos );

            }

            if( pos == -1 )
            {
                pos = version_string.indexOf( '-', oldpos );

            }

            if( pos == -1 )
            {
                pos = version_string.length(  );

            }

            // If we've ended the string
            if( oldpos > pos )
            {
                split_version[i] = "0"; // Just add a zero

            }

            else
            { // Otherwise carry on
                split_version[i] = version_string.substring( oldpos, pos );

            }

            oldpos = pos + 1;

        }

        major = Integer.parseInt( split_version[0] );

        minor = Integer.parseInt( split_version[1] );

        revision = Integer.parseInt( split_version[2] );

    }

    // ----------------------------------------------------------------------
    public String getDotFormat(  )
    {

        if( revision == 0 )
        {

            return major + "." + minor;

        }

        else
        {

            return major + "." + minor + "." + revision;

        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @param other DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int compareTo( Version other )
    {

        if( major > other.major )
        {

            return 1;

        }

        if( major < other.major )
        {

            return -1;

        }

        if( minor > other.minor )
        {

            return 1;

        }

        if( minor < other.minor )
        {

            return -1;

        }

        if( revision > other.revision )
        {

            return 1;

        }

        if( revision < other.revision )
        {

            return -1;

        }

        return 0;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param other DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean greaterThan( Version other )
    {

        return ( compareTo( other ) == 1 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param other DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean lessThan( Version other )
    {

        return ( compareTo( other ) == -1 );

    }

    /**
     * DOCUMENT_ME!
     *
     * @param other DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean equals( Object other )
    {

        // If the passed in object is not a Version, they are not equal
        if( !( other instanceof Version ) )
        {

            return false;

        }

        return ( compareTo( (Version)other ) == 0 );

    }
}
