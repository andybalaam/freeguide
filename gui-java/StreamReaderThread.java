/*
 * FreeGuide J2
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.io.BufferedReader;
import javax.swing.JTextArea;

/**
 * A thread that eats up the output of BufferedReader until told to stop, 
 * dumping the characters read into a JTextArea
 *
 * @author Andy Balaam
 * @version 1
 */
public class StreamReaderThread implements Runnable {
	
	public StreamReaderThread() {
		storedOutput = new String();
	}

	public void begin(BufferedReader reader) {
		begin(reader, null);
	}
	
	public void begin(BufferedReader reader, String cmdstr) {
		
		this.reader = reader;
		
		String lb = System.getProperty("line.separator");

		// Add a couple of line breaks if we're continuing on a new stream
		if(storedOutput.length()>0) {
			storedOutput += lb + lb;
		}
		
		if(cmdstr!=null) {
			storedOutput += "$" + cmdstr + lb;
		}
		
		// Get going immediately
		start();
		
	}
	
	public void start() {
        if (runner==null) {
            runner = new Thread(this);
            runner.start();
        }
    }

    public void stop() {
        runner=null;
    }

    public void run() {
        Thread thisThread = Thread.currentThread();
		char[] buf = new char[1000];
		int charsRead;
		
		try {
			while( (runner==thisThread)  && ((charsRead=reader.read(buf))>0) ) {
            
				storedOutput += String.copyValueOf(buf, 0, charsRead);
				//System.out.println(String.copyValueOf(buf, 0, charsRead));
				
				//textarea.repaint();
				
				try {
					Thread.yield();
					Thread.sleep(1);
				} catch(java.lang.InterruptedException e) {
					e.printStackTrace();
				}
			
			}
		} catch(java.io.IOException e) {
			e.printStackTrace();
		}
    }
	
	public String getStoredOutput() {
		return storedOutput;
	}
	
	private String storedOutput;
	private BufferedReader reader;
	private Thread runner;
}