package freeguide.lib.fgspecific.data;

import freeguide.lib.fgspecific.Application;

import java.io.File;
import java.io.Serializable;

import java.util.Iterator;
import java.util.Set;
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
    protected String displayName = "";
    protected String iconURL;

    /** List of programmes for channel. */
    protected Set programmes = new TreeSet(  );

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

        Iterator it = channel.getProgrammesIterator(  );

        while( it.hasNext(  ) )
        {

            TVProgramme p = (TVProgramme)it.next(  );
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
     * Get cached icon's filename.
     *
     * @return cached icon's filename
     */
    public String getIconFileName(  )
    {

        StringBuffer sb = new StringBuffer(  );

        sb.append( Application.getInstance(  ).getWorkingDirectory(  ) );

        sb.append( '/' );

        sb.append( TVChannel.ICONCACHE_SUBDIR );

        sb.append( '/' );

        File dir = new File( sb.toString(  ) );

        if( !dir.exists(  ) )
        {
            dir.mkdirs(  );
        }

        sb.append( id.replace( '.', '_' ).replaceAll( "[^a-zA-Z0-9_]", "-" ) );

        return sb.toString(  );
    }

    /**
     * Put new TVProgramme to channel.
     *
     * @param programme TVProgramme
     */
    public void put( final TVProgramme programme )
    {
        programme.setChannel( this );

        programmes.remove( programme );
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
     * Get iterator by all TVProgrammes.
     *
     * @return Iterator
     */
    public Iterator getProgrammesIterator(  )
    {

        return programmes.iterator(  );

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

        Iterator it = getProgrammesIterator(  );

        while( it.hasNext(  ) )
        {

            TVProgramme prog = (TVProgramme)it.next(  );

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

        Iterator it = getProgrammesIterator(  );

        while( it.hasNext(  ) )
        {

            TVProgramme prog = (TVProgramme)it.next(  );

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
