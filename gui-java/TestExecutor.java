import java.util.logging.Logger;

class TestExecutor implements FreeGuideLauncher {

	public TestExecutor() {
		
		FreeGuide.prefs = new FreeGuidePreferencesGroup();
		FreeGuide.log = Logger.getLogger("org.freeguide-tv");
		
		String[] cmds = new String[2];
		cmds[0] = "\"C:\\Program Files\\freeguide-tv\\xmltv\\tv_grab_uk.exe\" --config-file \"C:\\Program Files\\freeguide-tv\\xmltv\\tv_grab_uk\" --output \"C:\\Program Files\\freeguide-tv\\data\\listings_unprocessed.xml\"";
		cmds[1] = "\"C:\\Program Files\\freeguide-tv\\xmltv\\tv_split.exe\" \"C:\\Program Files\\freeguide-tv\\data\\listings_unprocessed.xml\" --output \"C:\\Program Files\\freeguide-tv\\data\\%%channel-%%Y%%m%%d.fgd\"";
		FreeGuideUtils.execAndWait(cmds, "Test commands", this);
		
	}
	
	public static void main(String[] args) {
	
		new TestExecutor();
	
	}
	
	public void reShow() {
		
	}
	
	public FreeGuideLauncher getLauncher() {
		return null;
	}
	
	
	public void setVisible(boolean show) {
		
	}
	

}
