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
package freeguide.common.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    protected static final String NUMBER_SEPARATOR = ".";
    protected static final String VERSION_PROPERTY = "java.version";
    protected static final Pattern VERSION_PATTERN =
        Pattern.compile( "(\\d+)(?:\\.(\\d+)(?:\\.(\\d+)(?:[_-](\\d+))?)?)?" );
    protected static final Pattern VERSION_PATTERN_LITE =
        Pattern.compile( "(\\d+)(?:\\.(\\d+)).*" );

    /** Major value. */
    public int major;

    /** Minor value. */
    public int minor;

    /** Revision value. */
    public int revision;

    /** Build value. */
    public int build;

    /**
     * Creates a new Version object.
     *
     * @param major DOCUMENT ME!
     * @param minor DOCUMENT ME!
     */
    public Version( int major, int minor )
    {
        this.major = major;

        this.minor = minor;

    }

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
     * Create a Version object with the given major, minor, revision and build
     * numbers
     *
     * @param major DOCUMENT ME!
     * @param minor DOCUMENT ME!
     * @param revision DOCUMENT ME!
     * @param build DOCUMENT ME!
     */
    public Version( int major, int minor, int revision, int build )
    {
        this.major = major;

        this.minor = minor;

        this.revision = revision;

        this.build = build;
    }

    /**
     * Create a Version object from a string that looks like this: d.d.d_x
     * where d represents any number of digits . is a literal dot _ is either
     * an _, - or . character x is any sequence of characters
     *
     * @param versionString DOCUMENT ME!
     *
     * @throws NumberFormatException DOCUMENT ME!
     */
    public Version( String versionString ) throws NumberFormatException
    {
        if( versionString == null )
        {
            return;
        }

        final Matcher m = VERSION_PATTERN.matcher( versionString );

        if( m.matches(  ) )
        {
            major = parseDigs( m.group( 1 ) );
            minor = parseDigs( m.group( 2 ) );
            revision = parseDigs( m.group( 3 ) );
            build = parseDigs( m.group( 4 ) );
        }
        else
        {
            final Matcher mLite =
                VERSION_PATTERN_LITE.matcher( versionString );

            if( mLite.matches(  ) )
            {
                major = parseDigs( mLite.group( 1 ) );
                minor = parseDigs( mLite.group( 2 ) );
                revision = 0;
                build = 0;
            }
        }
    }

    protected int parseDigs( final String text )
    {
        if( text == null )
        {
            return 0;
        }

        return Integer.parseInt( text );
    }

    // ----------------------------------------------------------------------
    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getDotFormat(  )
    {
        if( ( minor == 0 ) && ( revision == 0 ) && ( build == 0 ) )
        {
            return Integer.toString( major );
        }

        if( ( revision == 0 ) && ( build == 0 ) )
        {
            return major + NUMBER_SEPARATOR + minor;
        }
        else if( build == 0 )
        {
            return major + NUMBER_SEPARATOR + minor + NUMBER_SEPARATOR
            + revision;
        }
        else
        {
            return major + NUMBER_SEPARATOR + minor + NUMBER_SEPARATOR
            + revision + NUMBER_SEPARATOR + build;
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
        else if( major < other.major )
        {
            return -1;

        }

        if( minor > other.minor )
        {
            return 1;

        }
        else if( minor < other.minor )
        {
            return -1;

        }

        if( revision > other.revision )
        {
            return 1;

        }
        else if( revision < other.revision )
        {
            return -1;

        }

        if( build > other.build )
        {
            return 1;

        }
        else if( build < other.build )
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

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public static Version getJavaVersion(  )
    {
        return new Version( System.getProperty( VERSION_PROPERTY ) );
    }
}
