package freeguide.common.lib.fgspecific.data;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.general.StringHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * A class that holds info about a particular programme.
 *
 * @author Andy Balaam
 * @author Alex Buloichik (mailto: alex73 at zaval.org)
 */
public class TVProgramme implements Comparable, Serializable
{
    private final static long five_mins = 1000 * 60 * 5;
    private final static long serialVersionUID = 10;

    /** Description of the Field */
    protected static final String STARS = "***********";
    protected static final String STAR_HALP_SUFFIX = " 1/2";
    protected static final String ICONPATH_RE_FROM =
        "[^0-9A-Za-z_-]|^http://|^ftp://";
    protected static final String ICONPATH_RE_TO = "";

    /** The start time in millis. */
    private long start;

    /** The end time in millis. */
    private long end = 0;

    /** The programme title */
    private String title;

    /** The programme subtitle */
    private String subtitle;

    /** The programme description. */
    private String description;

    /** The channel the prog's on */
    private TVChannel channel;

    /** The URL of the programme's icon */
    private String iconURL;

    /** The categories it fits into */
    private Vector category;

    /** Is it a movie? */
    private boolean isMovie;

    /** Is it a repeat? */
    private boolean previouslyShown;

    /** Its star rating if it's a movie */
    private String starRating;

    /** Does it have subtitles? */
    private boolean isSubtitled;

    /** A URL to more info about the programme */
    private URL link;

    /** Any unrecognised tags go in here. */
    private Map extraTags;

/**
     * Constructor for the Programme object
     */
    public TVProgramme(  )
    {
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Object clone(  )
    {
        final TVProgramme result = new TVProgramme(  );

        result.title = title;
        result.description = description;

        result.start = start;
        result.end = end;

        result.extraTags = extraTags;
        result.category = category;
        result.iconURL = iconURL;
        result.isMovie = isMovie;
        result.isSubtitled = isSubtitled;
        result.link = link;
        result.previouslyShown = previouslyShown;
        result.starRating = starRating;
        result.subtitle = subtitle;

        return result;

    }

    /**
     * Sets the start attribute of the Programme object
     *
     * @param start The new start value
     */
    public void setStart( long start )
    {
        this.start = start;

    }

    /**
     * Gets the start attribute of the Programme object
     *
     * @return The start value
     */
    public long getStart(  )
    {
        return start;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public long getEnd(  )
    {
        return end;

    }

    /**
     * Sets the end attribute of the Programme object
     *
     * @param end The new end value
     */
    public void setEnd( final long end )
    {
        this.end = end;

    }

    /**
     * Sets the isMovie attribute of the Programme object
     *
     * @param isMovie The new isMovie value
     */
    public void setIsMovie( boolean isMovie )
    {
        this.isMovie = isMovie;

    }

    /**
     * Gets the isMovie attribute of the Programme object
     *
     * @return The isMovie value
     */
    public boolean getIsMovie(  )
    {
        return this.isMovie;

    }

    /**
     * Sets the previouslyShown attribute of the Programme object
     *
     * @param repeat The new previouslyShown value
     */
    public void setPreviouslyShown( boolean repeat )
    {
        this.previouslyShown = repeat;

    }

    /**
     * Gets the previouslyShown attribute of the Programme object
     *
     * @return The previouslyShown value
     */
    public boolean getPreviouslyShown(  )
    {
        return this.previouslyShown;

    }

    /**
     * Sets the starRating attribute of the Programme object
     *
     * @param rating The new starRating value
     */
    public void setStarRating( String rating )
    {
        this.starRating = rating;

    }

    /**
     * Gets the starRating attribute of the Programme object
     *
     * @return The starRating value
     */
    public String getStarRating(  )
    {
        return this.starRating;

    }

    /**
     * Gets the starString attribute of the Programme object
     *
     * @return The starString value
     */
    public String getStarString(  )
    {
        String rating = getStarRating(  );

        if( rating == null )
        {
            return StringHelper.EMPTY_STRING;

        }

        int i = rating.indexOf( '/' );

        if( i > 0 )
        {
            try
            {
                double num = Double.parseDouble( rating.substring( 0, i ) );

                if( num == 0 )
                {
                    return Application.getInstance(  )
                                      .getLocalizedMessage( "no_stars" );

                }

                if( Math.floor( num ) == num )
                {
                    return '('
                    + STARS.substring( 
                        0, (int)Math.round( Math.floor( num ) ) ) + ')';

                }

                else
                {
                    return '('
                    + STARS.substring( 
                        0, (int)Math.round( Math.floor( num ) ) )
                    + STAR_HALP_SUFFIX + ')';

                }
            }

            catch( Exception ex )
            {
                return StringHelper.EMPTY_STRING;

            }
        }

        return StringHelper.EMPTY_STRING;

    }

    /**
     * Adds to the title attribute of the Programme object
     *
     * @param title The title of the programme
     */
    public void setTitle( String title )
    {
        this.title = title;
    }

    /**
     * Adds more to the subtitle of the programme
     *
     * @param subtitle The subtitle of the programme
     */
    public void setSubTitle( String subtitle )
    {
        this.subtitle = subtitle;
    }

    /**
     * Gets the title attribute of the Programme object
     *
     * @return The title value
     */
    public String getTitle(  )
    {
        return title;

    }

    /**
     * Gets the subtitle attribute of the Programme object
     *
     * @return The subtitle value
     */
    public String getSubTitle(  )
    {
        return subtitle;

    }

    /**
     * Adds a feature to the Desc attribute of the Programme object
     *
     * @param desc The feature to be added to the Desc attribute
     */
    public void addDesc( String desc )
    {
        if( description == null )
        {
            description = desc;

        }

        else
        {
            description += ( '\n' + desc );

        }
    }

    /**
     * Set description for programme
     *
     * @param desc new description text
     */
    public void setDescription( String desc )
    {
        description = desc;
    }

    /**
     * Gets the longDesc attribute of the Programme object
     *
     * @return The longDesc value
     */
    public String getDescription(  )
    {
        return description;

    }

    /**
     * Gets the channel attribute of the Programme object
     *
     * @return The channel Object
     */
    public TVChannel getChannel(  )
    {
        return channel;

    }

    /**
     * Sets the channel attribute of the Programme object
     *
     * @param channel The new channel value
     */
    protected void setChannel( TVChannel channel )
    {
        this.channel = channel;

    }

    /**
     * Sets whether the programme has subtitles
     *
     * @param isSubtitled DOCUMENT ME!
     */
    public void setSubtitled( boolean isSubtitled )
    {
        this.isSubtitled = isSubtitled;

    }

    /**
     * DOCUMENT ME!
     *
     * @return true if this programme has subtitles
     */
    public boolean isSubtitled(  )
    {
        return isSubtitled;

    }

    /**
     * Adds a feature to the Category attribute of the Programme
     * object
     *
     * @param newCategory The feature to be added to the Category attribute
     */
    public void addCategory( String newCategory )
    {
        if( category == null )
        {
            category = new Vector(  );

        }

        category.add( newCategory );

    }

    /**
     * Gets the category attribute of the Programme object
     *
     * @return The category value
     */
    public String getCategory(  )
    {
        // FIXME just returns first one
        if( ( category != null ) && ( category.size(  ) > 0 ) )
        {
            return (String)category.get( 0 );

        }

        else
        {
            return null;

        }
    }

    /**
     * Sets a URL with more info about this programme
     *
     * @param link the URL to follow for more info
     */
    public void setLink( URL link )
    {
        this.link = link;

    }

    /**
     * Gets a link to follow for more info about this programme
     *
     * @return The URL of more info about the programme
     */
    public URL getLink(  )
    {
        return link;
    }

    /**
     * Decides whether two programme objects refer to the same
     * programme Programmes are assumed to be uniquely identified by their
     * title, start time and channel.
     *
     * @param obj Description of the Parameter
     *
     * @return Description of the Return Value
     */
    public boolean equals( Object obj )
    {
        if( obj == null )
        {
            return false;

        }

        if( !( obj instanceof TVProgramme ) )
        {
            return false;

        }

        TVProgramme other = (TVProgramme)obj;

        if( 
            title.equals( other.getTitle(  ) )
                && ( start == other.getStart(  ) )
                && channel.equals( other.getChannel(  ) ) )
        {
            return true;

        }

        return false;

    }

    /**
     * Returns a hashcode for the programme. Implemented to keep
     * consistency since the equals method was overridden.
     *
     * @return Description of the Return Value
     */
    public int hashCode(  )
    {
        // Just add up 3 values - stupid?
        byte[] titleBytes = title.getBytes(  );

        byte[] channelBytes = channel.getID(  ).getBytes(  );

        int ans = 0;

        for( int i = 0; i < titleBytes.length; i++ )
        {
            ans += titleBytes[i];

        }

        ans += (int)( start / ( 1000 * 60 ) );

        // Time in minutes since 1970
        for( int i = 0; i < channelBytes.length; i++ )
        {
            ans += channelBytes[i];

        }

        return ans;
    }

    /**
     * DOCUMENT_ME!
     *
     * @param tagName DOCUMENT_ME!
     * @param attrName DOCUMENT_ME!
     * @param value DOCUMENT_ME!
     */
    public void setExtraTag( 
        final String tagName, final String attrName, final String value )
    {
        if( extraTags == null )
        {
            extraTags = new HashMap(  );
        }

        Map hashOfAttrs = (Map)extraTags.get( tagName );

        if( hashOfAttrs == null )
        {
            hashOfAttrs = new HashMap(  );
            extraTags.put( tagName, hashOfAttrs );
        }

        String text = (String)hashOfAttrs.get( attrName );

        if( text == null )
        {
            text = value;
        }
        else
        {
            text = text + ';' + ' ' + value;
        }

        hashOfAttrs.put( attrName, text );
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the iconURL from the cache.
     */
    public String getIconURL(  )
    {
        if( iconURL == null )
        {
            return null;

        }

        StringBuffer path = new StringBuffer(  );

        path.append( Application.getInstance(  ).getWorkingDirectory(  ) );

        path.append( '/' );

        path.append( TVChannel.ICONCACHE_SUBDIR );

        path.append( '/' );

        File dir = new File( path.toString(  ) );

        if( !dir.exists(  ) )
        {
            dir.mkdirs(  );
        }

        path.append( iconURL.replaceAll( ICONPATH_RE_FROM, ICONPATH_RE_TO ) );

        // First convert the id to a suitable (and safe!!) filename
        File cache = new File( path.toString(  ) );

        // then verify if the file is in the cache
        if( !cache.canRead(  ) )
        {
            // if not, we try to fetch it from the url
            try
            {
                URL iconURL;

                iconURL = new URL( this.iconURL );

                InputStream i = iconURL.openStream(  );

                FileOutputStream o = new FileOutputStream( cache );

                byte[] buffer = new byte[4096];

                int bCount;

                while( ( bCount = i.read( buffer ) ) != -1 )
                {
                    o.write( buffer, 0, bCount );

                }

                o.close(  );

                i.close(  );

            }

            catch( MalformedURLException e )
            {
                return null;

            }

            catch( IOException e )
            {
                return null;

            }
        }

        try
        {
            return cache.toURI(  ).toURL(  ).toString(  );

        }

        catch( MalformedURLException e )
        {
            return null;

        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param iconURL The iconURL to set.
     */
    public void setIconURL( String iconURL )
    {
        this.iconURL = iconURL;

    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public Map getExtraTags(  )
    {
        return extraTags;
    }

    /**
     * Compare two programmes by their datetimes.
     *
     * @param other The programme to compare with this one.
     *
     * @return -1 if this programme comes first,
     *          1 if the other comes first, and
     *          0 if they overlap.
     */
    public int compareTo( Object other )
    {
        TVProgramme p;

        p = (TVProgramme)other;

        // If the end times have not been downloaded, we
        // must compare purely by start time.
        // If both end times are valid, we delare them equal if
        // They overlap at all, so we don't get overlapping
        // programmes showing.
        if( end == 0 || p.end == 0 )
        {
	        if( start <= p.start )
	        {
	            return -1;
	        }
	        else if( p.start <= start )
	        {
	            return 1;
	        }
        }
        else if( end <= p.start )
        {
            return -1;
        }
        else if( p.end <= start )
        {
            return 1;
        }

        return 0;
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String toString(  )
    {
        return new Date( start ).toString(  ) + ' ' + title;

    }
}
