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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * Provides a standard interface to an application's configuration file.
 *
 * @author  Andy Balaam
 * @version 3
 */
public class JoveConfigFile {

	/** 
	 * Loads or creates a config file after asking the user for its location.
	 *
	 * @param name the default name or path of a config file
	 */
    public JoveConfigFile(String name) {
		
		String[] vitals  = new String[0];
		construct(vitals, name);
		
    }//JoveConfigFile
	
	/** 
	 * Loads a config file from the path given or searches in the
	 * user's home directory
	 * <p>
	 *
	 * @param vitals the lines which must be present if we have to create
	 *               a new config file
	 * @param name   the default name or path of a config file
	 */
    public JoveConfigFile(String[] vitals, String name) {
		
		construct(vitals, name);
		
	}//JoveConfigFile
		
	private void construct(String[] vitals, String name) {
		/*
		 * Note that this method tests a boolean once more than
		 * absolutely necessary.  It makes the code a lot clearer
		 * than the alternative if you ask me.
		 */
		
		// Initialise the two vectors that store all the info
		names = new Vector();
		values = new Vector();
		
		// Make a candidate file for where the config file might be
		File possibleFile = new File(name);
		
		boolean fileExists = possibleFile.exists();
		
		// If this is wrong...
		if(!fileExists) {
			
			// ...have another guess
			possibleFile = new File(System.getProperty("user.home")+possibleFile.getName());
			
			// See whether that exists
			fileExists = possibleFile.exists();
			
		}//if
		
		// If we've found the config file
		if(fileExists) {	// FYI this is the extra test mentioned above
			
			// Remember the path of this config file
			path = possibleFile.getPath();
			
			// Load in the configuration
			load();

		} else {
			
			// Make a new config file or find an old one
			createOrFind(vitals, name);
			
		}//if
		
		
    }//construct
	
	//------------------------------------------------------------------------
	
	/**
	 * Loads the config file from its default path
	 */
	public void load() {
		
		try {//IOException
			
			// Blank out the old config file if there was one
			names = new Vector();
			values = new Vector();
			
			// Open the file for input
			BufferedReader buffy = new BufferedReader(new FileReader(path));
			
			String line = buffy.readLine();
			
			while(line!=null) {
			
				// Check for comments
				int i = line.indexOf("//");
			
				if(i>-1) {	// if we found a comment
				
					// Remove the comment
					line = line.substring(0,i);
				}//if
			
				// Remove whitespace
				line.trim();
			
				if(line.endsWith("=")) {	// If we've got a list
					
					// Name the list
					names.add(line.substring(0,line.length()-1).trim());
					
					// Create a vecotr to store the list elements
					Vector subValues = new Vector();
					
					// Start reading in lines
					line = buffy.readLine();
					
					// Go through all the lines until we hit a blank or EOF
					while(line!=null) {
						
						// Get rid of white space
						line = line.trim();
						
						// Exit if we've hit a blank line
						if(line.equals("")) {
							break;
						}//if
						
						// Add a value
						subValues.add(line);
						
						// Read in another line
						line = buffy.readLine();
					}//while
					
					// Store this line as a subvalue
					values.add(subValues);
				
				} else {	// It wasn't a list
					
					int equalsIndex = line.indexOf('=');
					
					if(equalsIndex>-1) {	// If this line contains an =
						
						// Store this name-value pair
						names.add(line.substring(0,equalsIndex));
						values.add(line.substring(equalsIndex+1));
						
					} else if(!line.equals("")) {	// If this isn't blank
					
						// Something is amiss: print a non-fatal error
						System.out.println("JoveConfigFile Warning - Unreadable line in config file: "+line);
					
					}//if
					
				}//if
				
				line = buffy.readLine();
				
			}//while
			
			// Close the file
			buffy.close();
			
			// Remember we haven't altered anything since the last load/save
			changed = false;
			
		} catch(IOException e) {
			// Abort program execution if we have an error
			quit(e);
			
		}//try
		
	}//load
	
	/**
	 * Saves the config file to its default path
	 */
	public void save() {
		
		try {//IOException
			
			// Open the file for output
			BufferedWriter buffy = new BufferedWriter(new FileWriter(path));
			
			// Go through every name-value pair writing them out
			for(int i=0;i<names.size();i++) {
				
				// Write out the name
				buffy.write(names.get(i).toString());
				buffy.write("=");
				
				// Find what's in the value
				Object obj = values.get(i);
				
				if(obj instanceof String) {	// If it's just a string
					
					// Write it straight out
					buffy.write(obj.toString());
					buffy.newLine();
					
				} else {	// It must be a Vector
					
					// Start a new line
					buffy.newLine();
					
					// So let's cast it to a Vector
					Vector vect = (Vector)obj;
					
					// Step through each element
					for(int j=0;j<vect.size();j++) {
						
						// Write out each one on a new line
						buffy.write(vect.get(j).toString());
						buffy.newLine();
						
					}//for
					
					// Make a blank line below the list
					buffy.newLine();
					
				}//if
				
			}//for
			
			// Close the file
			buffy.close();
			
			// Remember we haven't altered anything since the last load/save
			changed=false;
			
		} catch(IOException e) {
			// Abort program execution if we have an error
			quit(e);
			
		}//try
		
	}//save
	
	/**
	 * Sets a property to a string or adds it if it wasn't already present.
	 *
	 * @param name     the String name of the property to change
	 * @param newValue the String value to change it to
	 */
	public void setValue(String name, String newValue) {
		
		setValueFromObject(name, newValue);
		
	}//setValue
	
	/**
	 * Sets a list property to a Vector or adds it if it wasn't 
	 * already present.
	 *
	 * @param name     the String name of the property to change
	 * @param newValue the Vector list to change it to
	 */
	public void setListValue(String name, Vector newValue) {
		
		setValueFromObject(name, newValue);
		
	}//setListValue
	
	/**
	 * Gets a value from this config file referenced by its name.
	 *
	 * @param name  the String name of the property required
	 * @param literal whether to get literal value (without substitutions)
	 * @returns     the value of the requested property or the empty string
	 *              when the property doesn't exist or it's a vector
	 *              property.  A warning is printed to stdout in the latter 
	 *              case.
	 */
	
	public String getValue(String name) {
		return getValue(name, false);
	}
	
	public String getValue(String name, boolean literal) {
		
		// Find the requested value
		int i = names.indexOf(name);
		
		if(i>-1) {	// If it exists
			
			Object obj = values.get(i);
			
			// If it's a simple string value
			if(obj instanceof String) {
				
				String ans = (String)obj;
				
				if(!literal) {
				
					// Substitute any variables
					ans = stringSubstitutions(ans);
					ans = vectorSubstitutions(ans);
					
				}
				
				// Pass back the value of the config entry
				return ans;
				
			} else {	// Otherwise it must be a vector
				
				System.out.println("JoveConfigFile - Warning: string requested from vector config entry.");
				
				// Return a blank - user is confused asking for
				// a strig when the entry is a vector.
				return null;
				
			}//if
			
		} else {	// If it doesn't exist
			
			// Return blank
			return null;
			
		}//if
		
	}//getValue
	
	/**
	 * Gets a list of values from this config file referenced by its name.
	 *
	 * @param name  the String name of the list property required
	 * @returns     the vector list of the requested property or an empty
	 *              vector when the property doesn't exist or it's a
	 *              string type property.  A warning is printed to stdout 
	 *              in the latter case.
	 */
	public Vector getListValue(String name) {
		return getListValue(name, false);
	}
	public Vector getListValue(String name, boolean literal) {
		
		// Find the requested value
		int i = names.indexOf(name);
		
		if(i>-1) {	// If it exists
			
			Object obj = values.get(i);
			
			// If it's a simple string value
			if(obj instanceof String) {
				
				// WARNING: behaviour here may change
				
				System.out.println("JoveConfigFile - Warning: vector requested from string config entry.");
				
				// User is confused - pass back an empty Vector
				return null;
				
			} else {	// Otherwise it must be a vector
					
				// Return it after substitutions
				Vector vobj =  (Vector)obj;
				
				if(!literal) {
				
					for(int j=0;j<vobj.size();j++) {	
						// Substitute any variables
						vobj.set(j, stringSubstitutions((String)vobj.get(j)));
						vobj.set(j, vectorSubstitutions((String)vobj.get(j)));
					
					}
				}
			
				return vobj;
				
			}//if
			
		} else {	// If it doesn't exist
			
			// Return blank
			return null;
			
		}//if
		
	}//getValue
	
	/**
	 * Returns whether or not this config file contains unsaved changes.
	 *
	 * @return true if any configuration settings have been changed since
	 *         the last save or load
	 */
	public boolean isChanged() {
		
		return changed;
		
	}//getChanged
	
	//------------------------------------------------------------------------
	
	/** 
	 * Asks the user whether they would like to create a new config file
	 * or load an old one.
	 *
	 * @param vitals the lines which must be present if we have to create
	 *               a new config file
	 * @param name   the default name of the config file
	 */	
	private void createOrFind(String[] vitals, String name) {
		
		// Ask the user whether to create a file or load one
		
		Object[] options = { "Create", "Load" };
		
		int retval = JOptionPane.showOptionDialog(null, "Do you want to create a new configuration file or load an old one?", "Create or Load a config file", 0, JOptionPane.QUESTION_MESSAGE, null, options, "Create" );
			
		// Do what the user said
		if(retval==0) {
			
			create(vitals, name);
			
		} else {	// Also goes here if the user closed the dialog
			
			find(name);
			
		}//if
		
	}//createOrFind
	
	/** 
	 * Creates a new config file in a user-specified location.
	 *
	 * @param vitals the lines which must be present if we have to create
	 *               a new config file
	 * @param name   the default name of the config file
	 */	
	private void create(String[] vitals, String name) {
		
		// Open a file dialog for the user to say where the config file goes
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Create a config file");
		chooser.setCurrentDirectory(new File(name));
		chooser.showDialog(null, "Create");
		
		// Find what file they chose
		File f = chooser.getSelectedFile();
		
		// If they chose something
		if(f!=null) {
		
			// Remember what the've said
			path = f.getPath();
		
			try {//IOException
		
				// Open a file for output
				BufferedWriter buffy = new BufferedWriter(new FileWriter(path));
				
				// Write the lines we've been given
				for(int i=0;i<vitals.length;i++) {
				
					buffy.write(vitals[i]);
					buffy.newLine();
				
				}//for
			
				// Close the file
				buffy.close();
				
				load();
			
			} catch(IOException e) {
			
				// Abort on IO error
				quit(e);
			
			}//try
			
		} else {	// If they chose nothing
			
			quit();
			
		}//if
			
	}//create
	
	/** 
	 * Loads a config file from a user-specified location
	 *
	 * @param name   the default name of the config file
	 */	
	private void find(String name) {
		
		// Open a file dialog for the user to say where the config file goes
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Load a config file");
		chooser.setCurrentDirectory(new File(name));
		chooser.showOpenDialog(null);
		
		// Find what file they chose
		File f = chooser.getSelectedFile();
		
		// If they chose something
		if(f!=null) {
		
			// Remember what the've said
			path = chooser.getSelectedFile().getPath();
		
			// Load the file
			load();
			
		} else {	// If they chose nothing
			
			quit();
			
		}//if
		
	}//find
	
	private void setValueFromObject(String name, Object newValue) {
		
		int i = names.indexOf(name);
		
		if(i>-1) {	// If the property waa already present
			
			// Change it
			values.set(i, newValue);
			
		} else {	// If it wasn't there
			
			// Add it and set its value
			names.add(name);
			values.add(newValue);
			
		}//if
		
		// Remember we haven't saved since a change
		changed = true;
		
	}//setValueFromObject
	
	/** 
	 * Halts program running unceremoniously.
	 */	
	private void quit() {
		
		System.out.println("JoveConfigFile - Warning: Config file loading failed - program aborted.");
		System.exit(1);
		
	}//quit
	
	/** 
	 * Halts program running unceremoniously.
	 *
	 * @param e the exception that caused the problem
	 */	
	private void quit(Exception e) {
		
		System.out.println("JoveConfigFile - Warning: Config file loading failed - program aborted.");
		e.printStackTrace();
		System.exit(1);
		
	}//quit
	
	//------------------------------------------------------------------------

	/*
	 * Replace any $$xxx$$ entries with the value of the variable named xxx.
	 *
	 * @param input the string to be transformed
	 * @returns the transformed string with all $$..$$ strings replaced
	 */
	String stringSubstitutions(String input){
	
		String ans = input;
		
		int j = ans.indexOf("$$");
		while(j>-1){	// If there is a substitution to do
					
			// Find the end of it
			int k = ans.indexOf("$$", j+1);
					
			// If it is malformed, exit
			if(k==-1){
				break;
			}
					
			// Find the name of the variable
			String presub = ans.substring(j+2, k);
					
			// This is where the value of that variable will go
			String postsub;
					
			// Check for any predefined variable names
			//if(presub.equals("freeguideDirectory")){
				//postsub = System.getenv(name
			//	postsub = System.getProperty("user.dir");
			//} else {	// Otherwise get a config file entry
				
			//}
			
			postsub = this.getValue(presub);
			
			// Replace the variable name with its value
			ans = ans.substring(0,j) + postsub + ans.substring(k+2, ans.length());
			
			j = ans.indexOf("$$");
		}//while
		
		return ans;
	}
	
	/*
	 * Replace any %%xxx%% entries with the values of the list variable 
	 * named xxx.
	 *
	 * @param input the string to be transformed
	 * @returns the transformed string with all %%..%% strings replaced by a
	 *          space-separated list
	 */
	String vectorSubstitutions(String input){
	
		String ans = input;
		
		int j = ans.indexOf("%%");
		while(j>-1){	// If there is a substitution to do
					
			// Find the end of it
			int k = ans.indexOf("%%", j+1);
					
			// If it is malformed, exit
			if(k==-1){
				break;
			}
					
			// Find the name of the variable
			String presub = ans.substring(j+2, k);
					
			// This is where the value of that variable will go
			String postsub="";
					
			Vector tmpList = this.getListValue(presub);
			
			for(int i=0;i<tmpList.size();i++){
				postsub += tmpList.get(i)+" ";
			}
					
			// Replace the variable name with its value
			ans = ans.substring(0,j) + postsub + ans.substring(k+2, ans.length());
			
			j = ans.indexOf("$$");
		}//while
		
		return ans;
	}
	
	private Vector names;	// The names of each config entry
	private Vector values;	// The values of each entry
	private String path;	// The path where the config file is to be found
	private boolean changed;// A flag saying whether the file has been changed 
							// without saving
	
}//class
