/*
 *  FreeGuide J2
 *
 *  Copyright (c) 2001-2003 by Andy Balaam and the FreeGuide contributors
 *
 *  freeguide-tv.sourceforge.net
 *
 *  Released under the GNU General Public License
 *  with ABSOLUTELY NO WARRANTY.
 *
 *  See the file COPYING for more information.
 */
import java.io.File;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.Vector;
import javax.swing.JOptionPane;

/*
 *  An installer for FreeGuide
 *
 *  @author  Andy Balaam
 *  @version 6
 */
/**
 *  Description of the Class
 *
 *@author     andy
 *@created    02 July 2003
 */
public class Install implements Launcher {

    /**
     *  Constructor for the Install object
     */
    public Install() {

        // Make sure we have the right Java version etc.
        EnvironmentChecker.runChecks();

        prefs = new PreferencesGroup();

        // Branch into Reinstall or first time
        String install_directory = prefs.performSubstitutions(
                prefs.misc.get("install_directory"));

        if (install_directory == null ||
                !(new File(install_directory + File.separator +
                "FreeGuide.jar").exists())) {

            // First time install
            install(install_directory, false);

        } else {

            String txt = "There is a version of FreeGuide installed.";
            txt += System.getProperty("line.separator");
            txt += "Would  you like to uninstall it, or install the new version?";

            String[] options = {
                    "Complete Install (overwrite prefs - recommended)",
                    "Lite Install (keep old preferences)",
                    "Uninstall"};

            Object response = JOptionPane.showInputDialog(null,
                    txt,
                    "Install question",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    "Complete Install (overwrite prefs - recommended)");

            if (response == null) {
                System.err.println("Exiting installer without doing anything.");
                System.exit(0);
            } else if (response.equals("Complete Install (overwrite prefs - recommended)")) {
                install(install_directory, false);
            } else if (response.equals("Lite Install (keep old preferences)")) {
                install(install_directory, true);
            } else {
                uninstall(install_directory);
            }

        }

    }


    /**
     *  Description of the Method
     */
    public void reShow() {
        System.exit(0);
    }


    /**
     *  Gets the launcher attribute of the Install object
     *
     *@return    The launcher value
     */
    public Launcher getLauncher() {
        return null;
    }


    /**
     *  Sets the visible attribute of the Install object
     *
     *@param  show  The new visible value
     */
    public void setVisible(boolean show) {

    }


    /**
     *  Description of the Method
     *
     *@param  install_directory   Description of the Parameter
     *@param  keepOldPreferences  Description of the Parameter
     */
    private void install(String install_directory, boolean keepOldPreferences) {

        keepOldPrefs = keepOldPreferences;

		// If we haven't got a region, assume it's UK.
		if( prefs.misc.get( "region" ) == null ) {
			prefs.misc.get( "region", "UK" );
		}
		
        try {

            getAllRegions();

            WizardPanel[] panels = new WizardPanel[4];

            panels[0] = new LabelWizardPanel("");
            panels[0].setMessages("You are about to install FreeGuide.",
                    "Click \"Next\" to continue.");

			panels[1] = new ChoiceWizardPanel(allRegions);
			Class[] clses = new Class[1];
            clses[0] = Object.class;
			panels[1].setOnExit(this,
                    getClass().getMethod("SetProps", clses));
            panels[1].setMessages("Choose your region.",
                    "This affects which listings grabber will be used.");
			panels[1].setConfig("misc", "region");
					
			panels[2] = new DirectoryWizardPanel();
            panels[2].setMessages("Choose your installation directory.",
                    "This will be created if it doesn't exist.");
            panels[2].setConfig("misc", "install_directory");

            panels[3] = new LabelWizardPanel("Now you need to configure your grabber before you can start using it.");
            panels[3].setMessages("FreeGuide will be installed when you click \"Finish\".", "Read the README in the directory you chose to find out how.");

            new WizardFrame("FreeGuide Setup Wizard", panels, this, this,
                    getClass().getMethod("doInstall",
                    new Class[0])).setVisible(true);

        } catch (java.lang.NoSuchMethodException e) {
            e.printStackTrace();
        } catch (java.lang.SecurityException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Load in the properties file for the chosen region
     *
     *@param  boxValue  The name of the region chosen by the user.
     */
    public void SetProps(Object boxValue) {
		
        String region = (String) boxValue;
		
        for (int i = 0; i < allRegions.length; i++) {
			
            if (allRegions[i].equals(region)) {

                // Load the install.props file
                props = new Properties();

                try {

                    props.load(
                            new BufferedInputStream(
                            getClass().getResourceAsStream(
                            "/install-" + i + ".props")));

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }

                // Do all the preferences in the properties file - set the
                // defaults to them, and set the real values too if we're
                // not keeping old ones.
                int j = 1;
                String prefString = "";
                while ((prefString = props.getProperty(
                        "prefs." + j)) != null) {

                    doPref(prefString);
                    j++;

                }

                return;
            }
        }

        System.err.println(
                "Install.SetProps - Invalid region chosen.");

    }


    /**
     *  Gets the allRegions attribute of the Install object
     */
    private void getAllRegions() {

        int i = 0;

        Vector regs = new Vector();

        Properties pr = new Properties();
        InputStream is;

        is = getClass().getResourceAsStream("/install-" + i + ".props");

        while (is != null) {

            try {
                pr.load(new BufferedInputStream(is));

            } catch (java.io.IOException e) {
                e.printStackTrace();
                return;
            }

            regs.add(pr.getProperty("region"));

            i++;

            is = getClass().getResourceAsStream("/install-" + i + ".props");

        }

        allRegions = Utils.arrayFromVector_String(regs);

    }


    /**
     *  Remove FreeGuide and all the stuff it uses
     *
     *@param  install_directory  The directory from which to uninstall it
     */
    private void uninstall(String install_directory) {

		String w = prefs.performSubstitutions(
			prefs.misc.get( "working_directory" ) );
		
        if (w != null) {
            File work = new File(w);
            deleteDir(work);
        }
		
        File inst = new File(install_directory);
        deleteDir(inst);

        Preferences node = Preferences.userRoot().node("/org/freeguide-tv");

        try {

            node.removeNode();

        } catch (java.util.prefs.BackingStoreException e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(null,
                "FreeGuide has been successfully uninstalled.");

        System.err.println("Finished uninstall.");
        System.exit(0);

    }


    /**
     *  Deletes a whole directory recursively (also deletes a single file).
     *
     *@param  dir  Description of the Parameter
     */
    private void deleteDir(File dir) {

        if (!dir.exists()) {
            return;
        }

        if (dir.isDirectory()) {
            String[] list = dir.list();
            for (int i = 0; i < list.length; i++) {
                deleteDir(new File(dir.getPath() + File.separator + list[i]));
            }
        }

        dir.delete();

    }


    /**
     *  The main program for the Install class
     *
     *@param  args  The command line arguments
     */
    public static void main(String[] args) {

        new Install();

    }


    /**
     *  Description of the Method
     */
    public void doInstall() {

        try {

            String install_directory = prefs.performSubstitutions(
                    prefs.misc.get("install_directory"));

            // Make the required directories
            new File(install_directory).mkdirs();
            new File(prefs.performSubstitutions(
                    prefs.misc.get("xmltv_directory"))).mkdirs();
            new File(prefs.performSubstitutions(
                    prefs.misc.get("working_directory"))).mkdirs();

            // Copy in the files
            int i = 1;
            String filename = "";
            while ((filename = props.getProperty("file." + i)) != null) {
                installFile(filename);
                i++;
            }

            System.err.println("Finished install.");
            System.exit(0);

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

    }


    /**
     *  Description of the Method
     *
     *@param  command                  Description of the Parameter
     *@exception  java.io.IOException  Description of the Exception
     */
    private void installFile(String command) throws java.io.IOException {

        String[] srcdest = command.split(">");

        doInstallFile(srcdest[0],
                prefs.performSubstitutions(srcdest[1]));

    }


    /**
     *  Description of the Method
     *
     *@param  src                      Description of the Parameter
     *@param  dest                     Description of the Parameter
     *@exception  java.io.IOException  Description of the Exception
     */
    private void doInstallFile(String src, String dest)
             throws java.io.IOException {

        byte[] buf = new byte[32768];

        // make the directory if it doesn't exist
        int i = dest.lastIndexOf('/');
        if (i > -1) {
            new File(dest.substring(0, i)).mkdirs();
        }

        BufferedInputStream in = new BufferedInputStream(getClass().getResourceAsStream("/" + src));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dest));

        int count;
        while ((count = in.read(buf, 0, buf.length)) > -1) {
            out.write(buf, 0, count);
        }

        in.close();
        out.close();

    }


    /**
     *  Description of the Method
     *
     *@param  prefString  Description of the Parameter
     */
    private void doPref(String prefString) {

        // Split this string into its constituent parts

        int i = prefString.indexOf('=');
        String key = prefString.substring(0, i);
        String value = prefString.substring(i + 1);

        i = key.indexOf('.');
        String keyCategory = key.substring(0, i);
        key = key.substring(i + 1);

        // Find out what preferences category we're dealing with
        FGPreferences pr;
        if (keyCategory.equals("misc")) {
            pr = prefs.misc;
        } else if (keyCategory.equals("commandline")) {
            pr = prefs.commandline;
        } else {
            // Following is to make it compile ok.
            pr = prefs.misc;
            System.err.println("Unknown preferences group: " + keyCategory
                    + " - Aborting");
            System.exit(1);
        }

        // Set the default value always
        pr.put("default-" + key, value);

		// Only set the real value if we're not keeping old prefs
		// and this isn't the install dir or region (unless these are null)
		boolean overwritePref = !keepOldPrefs
			&& ( !(key.equals( "install_directory" ) )
				|| prefs.misc.get( "install_directory" ) == null );
		
        if( overwritePref ) {
            pr.put(key, value);
        }
    }


    private boolean keepOldPrefs;
    private String[] allRegions;
    private Properties props;
    private PreferencesGroup prefs;
    private String fs = System.getProperty("file.separator");
    private String lb = System.getProperty("line.separator");

}
