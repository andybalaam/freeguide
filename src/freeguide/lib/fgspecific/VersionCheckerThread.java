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

import freeguide.*;

import freeguide.gui.dialogs.*;

import freeguide.lib.general.*;

import java.io.*;

import java.net.*;

import javax.swing.*;

/**
 * A thread that checks the FreeGuide web site for what the latest version is,
 * and informs the user if they are using an old version.
 *
 * @author Andy Balaam
 * @version 1
 */
public class VersionCheckerThread implements Runnable
{

    JFrame parent;

    /**
     * Creates a new VersionCheckerThread object.
     *
     * @param parent DOCUMENT ME!
     */
    public VersionCheckerThread( JFrame parent )
    {
        this.parent = parent;
    }

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {
        new Thread( this ).start(  );

    }

    /**
     * DOCUMENT_ME!
     */
    public void run(  )
    {

        int ans;

        int major;
        int minor;
        int revision;

        // Load the version number from the Internet
        try
        {

            String url =
                "http://freeguide-tv.sourceforge.net/"
                + "VERSION.php?version=" + FreeGuide.version.getDotFormat(  );

            String privacy = FreeGuide.prefs.misc.get( "privacy", "no" );

            if( privacy.startsWith( "yes_nick:" ) )
            {
                url += ( "&ip=" + privacy.substring( 9 ) );

            }
            else if( privacy.equals( "yes_nothing" ) )
            {
                url += "&ip=0.0.0.0";

            }

            /* if privacy=no     then we won't get here.
             * if privacy=yes_ip then we don't add anything to the url - the
             *                   server finds the ip automatically.
             */
            URL fgversion = new URL( url );
            BufferedReader in =
                new BufferedReader( 
                    new InputStreamReader( fgversion.openStream(  ) ) );

            major = Integer.parseInt( in.readLine(  ) );
            minor = Integer.parseInt( in.readLine(  ) );
            revision = Integer.parseInt( in.readLine(  ) );

            in.close(  );

        }
        catch( java.net.MalformedURLException e )
        {
            e.printStackTrace(  );

            return;
        }
        catch( java.io.IOException e )
        {
            FreeGuide.log.info( 
                FreeGuide.msg.getString( "unable_to_check_version" ) );

            return;
        }

        Version online_version = new Version( major, minor, revision );

        int comp = online_version.compareTo( FreeGuide.version );

        if( comp == 1 )
        {
            warnOldVersion(  );
        }
        else if( comp == -1 )
        {
            warnFutureVersion(  );
        }
    }

    /**
     * Warn the user that they are using a futuristic version of FreeGuide.
     * (Only warns in a log as time travel is perfectly acceptable
     * behaviour.)
     */
    private void warnFutureVersion(  )
    {
        FreeGuide.log.info( 
            FreeGuide.msg.getString( 
                "you_are_using_a_development_version_of_freeguide" ) );

    }

    /**
     * Warn the user that they are using a obselete version of FreeGuide.
     * (This is virtually unforgivable in this day and age.)
     */
    private void warnOldVersion(  )
    {
        new NewVersionDialog( parent ).setVisible( true );

    }
}
