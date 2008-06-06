package freeguide.common.lib.fgspecific.data;

import freeguide.common.lib.general.StringHelper;

import java.io.Serializable;

import java.util.TreeSet;

/**
 * Class for storage channel data.
 *
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class TVChannel implements Serializable
{
    private final static long serialVersionUID = 10;

    /** DOCUMENT ME! */
    public static final String ICONCACHE_SUBDIR = "iconcache";
    protected static final long PROG_LENGTH_DEFAULT = 30L * 60L * 1000L; // 30 min

    /** Maximum programme length in milliseconds. */
    public static final long PROG_LENGTH_MAX = 6L * 60L * 60L * 1000L; // 6 h

    /**
     * Channel ID. It MUST be unique in system. We are using mode
     * "grabber/site/region/channel" for channel ID.
     */
    protected String id;

    /** Display name for channel. */
    protected String displayName = StringHelper.EMPTY_STRING;
    protected String iconURL;

    /** List of programmes for channel. */
    protected TreeSet<TVProgramme> programmes = new TreeSet<TVProgramme>();

/**
     * Create channel with specified ID. It should be locale-insensitive.
     *
     * @param id channel ID
     */
    public TVChannel( final String id )
    {
        this.id = id;

    }

/**
     * Create channel with specified ID and display name.
     *
     * @param id channel ID
     * @param displayName display name
     */
    public TVChannel( final String id, final String displayName )
    {
        this.id = id;
        this.displayName = displayName;
    }

    /**
     * Merge name and icon from channel.
     *
     * @param channel channel from which merge
     */
    public void mergeHeaderFrom( final TVChannel channel )
    {
        if( 
            ( channel.displayName != null )
                && ( channel.displayName.length(  ) > 0 ) )
        {
            displayName = channel.displayName;
        }

        if( channel.iconURL != null )
        {
            iconURL = channel.iconURL;
        }
    }

    /**
     * Merge name, icons and move programmes from channel to itself.
     *
     * @param channel channel from which merge
     */
    public void moveFrom( final TVChannel channel )
    {
        mergeHeaderFrom( channel );

        for( TVProgramme p : channel.getProgrammes(  ) )
        {
            put( p );
        }

        channel.clearProgrammes(  );
    }

    /**
     * Get channel ID.
     *
     * @return channel ID
     */
    public String getID(  )
    {
        return id;

    }

    /**
     * DOCUMENT ME!
     *
     * @param id The id to set.
     */
    public void setID( final String id )
    {
        this.id = id;

    }

    /**
     * Get channel's display name.
     *
     * @return channel's display name
     */
    public String getDisplayName(  )
    {
        return displayName;

    }

    /**
     * Set channel's display name.
     *
     * @param displayName channel's display name
     */
    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;

    }

    /**
     * Get icon's URL.
     *
     * @return Returns the iconURL.
     */
    public String getIconURL(  )
    {
        return iconURL;

    }

    /**
     * Set icon's URL.
     *
     * @param iconURL The iconURL to set.
     */
    public void setIconURL( final String iconURL )
    {
        this.iconURL = iconURL;

    }

    /**
     * Put new TVProgramme to channel.
     *
     * @param programme TVProgramme
     */
    public void put( final TVProgramme programme )
    {
        programme.setChannel( this );

        // Keep on removing programmes that overlap this one until there
        // aren't any more (note this TreeSet uses a
        // TVProgrammeOverlapIsEqualComparator
        while( programmes.remove( programme ) ) {}

        // Now add this one
        programmes.add( programme );
    }

    /**
     * Put new TVProgramme's to channel.
     *
     * @param programme TVProgramme's
     */
    public void put( final TVProgramme[] programme )
    {
        for( int i = 0; i < programme.length; i++ )
        {
            put( programme[i] );
        }
    }

    /**
     * Get all programmes list.
     *
     * @return Iterator
     */
    public TreeSet<TVProgramme> getProgrammes(  )
    {
        return programmes;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public int getProgrammesCount(  )
    {
        return programmes.size(  );
    }

    /**
     * Get programme by specified time.
     *
     * @param startTime
     *
     * @return TVProgramme, or null if not found
     */
    public TVProgramme getProgrammeByTime( final long startTime )
    {
        for( final TVProgramme prog : getProgrammes(  ) )
        {
            if( prog.getStart(  ) == startTime )
            {
                return prog;
            }
        }

        return null;

    }

    /**
     * DOCUMENT_ME!
     *
     * @param obj DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public boolean equals( Object obj )
    {
        TVChannel ch = (TVChannel)obj;

        return id.equals( ch.id );

    }

    /**
     * Normalize time for all programmes.
     */
    public void normalizeTime(  )
    {
        TVProgramme prevProg = null;

        for( TVProgramme prog : getProgrammes(  ) )
        {
            if( ( prevProg != null ) && ( prevProg.getEnd(  ) == 0 ) )
            {
                if( 
                    ( prog.getStart(  ) - prevProg.getStart(  ) ) <= PROG_LENGTH_MAX )
                {
                    prevProg.setEnd( prog.getStart(  ) );
                }
                else
                {
                    prevProg.setEnd( 
                        prevProg.getStart(  ) + PROG_LENGTH_DEFAULT );
                }
            }

            prevProg = prog;
        }

        if( ( prevProg != null ) && ( prevProg.getEnd(  ) == 0 ) )
        {
            prevProg.setEnd( prevProg.getStart(  ) + PROG_LENGTH_DEFAULT );

        }
    }

    /**
     * DOCUMENT_ME!
     */
    public void clearProgrammes(  )
    {
        programmes.clear(  );
    }
}
