/*
 * FreeGuide J2
 *
 * Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */
package freeguide.lib.general;

import freeguide.gui.dialogs.*;

import java.awt.Container;

import java.io.BufferedReader;

import javax.swing.JTextArea;

/**
 * A thread that eats up the output of BufferedReader until told to stop,
 * dumping the characters read into a JTextArea
 *
 * @author Andy Balaam
 * @version 1
 */
public class StreamReaderThread implements Runnable
{

    private String storedOutput;
    private BufferedReader reader;
    private Thread runner;
    private String lb;
    private StringViewer viewer;
    private StringBuffer viewbuf;

    /**
     * Creates a new StreamReaderThread object.
     */
    public StreamReaderThread(  )
    {
        storedOutput = new String(  );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param reader DOCUMENT_ME!
     * @param viewer DOCUMENT_ME!
     * @param viewbuf DOCUMENT_ME!
     */
    public void begin( 
        BufferedReader reader, StringViewer viewer, StringBuffer viewbuf )
    {
        begin( reader, null, viewer, viewbuf );
    }

    /**
     * DOCUMENT_ME!
     *
     * @param reader DOCUMENT_ME!
     * @param cmdstr DOCUMENT_ME!
     * @param viewer DOCUMENT_ME!
     * @param viewbuf DOCUMENT_ME!
     */
    public void begin( 
        BufferedReader reader, String cmdstr, StringViewer viewer,
        StringBuffer viewbuf )
    {
        this.reader = reader;
        this.viewer = viewer;
        this.viewbuf = viewbuf;

        lb = System.getProperty( "line.separator" );

        // Add a couple of line breaks if we're continuing on a new stream
        if( storedOutput.length(  ) > 0 )
        {
            storedOutput += ( lb + lb );
        }

        if( cmdstr != null )
        {
            storedOutput += ( "$" + cmdstr + lb );
        }

        // Get going immediately
        start(  );
        viewbuf.append( storedOutput );
        viewer.repaint(  );
        storedOutput = "";
    }

    /**
     * DOCUMENT_ME!
     */
    public void start(  )
    {

        //System.out.println("test: StreamReaderThread=" + this);
        //if(runner==null) {
        runner = new Thread( this );
        runner.start(  );

        //}
    }

    /**
     * DOCUMENT_ME!
     */
    public void stop(  )
    {
        runner = null;
    }

    /**
     * DOCUMENT_ME!
     */
    public void run(  )
    {

        Thread thisThread = Thread.currentThread(  );
        char[] buf = new char[1000];
        int charsRead;

        try
        {

            while( 
                ( runner == thisThread )
                    && ( ( charsRead = reader.read( buf ) ) > 0 ) )
            {

                String tstr = String.copyValueOf( buf, 0, charsRead );

                if( viewer != null )
                {
                    viewbuf.append( storedOutput + tstr );
                    viewer.repaint(  );
                    storedOutput = "";

                }
                else
                {
                    storedOutput += tstr;

                }

                try
                {
                    Thread.sleep( 1 );

                }
                catch( java.lang.InterruptedException e )
                {
                    e.printStackTrace(  );
                }
            }
        }
        catch( java.io.IOException e )
        {
            e.printStackTrace(  );
        }
    }

    /**
     * DOCUMENT_ME!
     *
     * @return DOCUMENT_ME!
     */
    public String getStoredOutput(  )
    {

        return storedOutput;
    }
}
