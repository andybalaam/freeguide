/*
 * Jove
 *
 * Copyright (c) 2001 by Andy Balaam
 *
 * Released under the GNU General Public License
 * with ABSOLUTELY NO WARRANTY.
 *
 * See the file COPYING for more information.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Administers a log file for a program.
 *
 * @author  Andy Balaam
 * @version 1
 */
public class JoveLogFile {

	/**
	 * Creates or re-opens a log file.
	 *
	 * @param path the string path of the log file
	 */
    public JoveLogFile(String path) {
		
		// Remember this path but do nothing
		this.path = path;
		
		checkExists();
		
    }//JoveLogFile
	
	/**
	 * Creates or re-opens a log file.
	 *
	 * @param path      the string path of the log file
	 * @param overwrite true if the old log entries should be deleted
	 *                  before creating this log.
	 */
    public JoveLogFile(String path, boolean overwrite) {
		
		// Remember this path
		this.path = path;
		
		if(overwrite) {	// If we're deleting the old log file
			
			openFile();
			
		} else {
			
			checkExists();
			
		}//if
		
    }//JoveLogFile

	//------------------------------------------------------------------------
	
	/**
	 * Writes a line to this log file, stamping it with the date and time
	 *
	 * @param line the string to write to the log
	 */
	public void writeLine(String line) {
		
		try {//IOException
		
			BufferedWriter buffy = new BufferedWriter(new FileWriter(path, true));
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm-ss");
			String datestr = fmt.format(new Date());
			
			buffy.write(datestr);
			buffy.write(" - ");
			buffy.write(line);
			buffy.newLine();
		
			buffy.close();
			
			//System.out.println(datestr+" - "+line);
			
		} catch(IOException e) {
			
			quit(e);
			
		}//try
		
	}//writeLine
	
	//------------------------------------------------------------------------

	/**
	 * Check the log file exists and make it if not.
	 */
	private void checkExists() {
		
		File testFile = new File(path);
		
		if(!testFile.exists()) {
			
			openFile();
			
		}
		
	}//checkExists
	
	/**
	 * Wipe the log file and start again.
	 */
	private void openFile() {
		
		try {//IOException
			
			// Open and close file without enabling overwrite
			FileWriter f = new FileWriter(path);
			f.close();
				
		} catch(IOException e) {
				
			quit(e);
				
		}//try
		
	}//openFile
	
	/** 
	 * Halts program running unceremoniously.
	 */	
	private void quit() {
		
		System.out.println("JoveLogFile - Warning: Writing log file failed.");
		
	}//quit
	
	/** 
	 * Halts program running unceremoniously.
	 *
	 * @param e the exception that caused the problem
	 */	
	private void quit(Exception e) {
		
		System.out.println("JoveLogFile - Warning: Writing log file failed.");
		e.printStackTrace();
		
	}//quit
	
	//------------------------------------------------------------------------
	
	private String path;
	
}
