public class FreeGuideWizardPanelFactory {

	
	/**
	 * Constructs a FreeGuideWizardPanel that is linked to a config entry.
	 */
	public static FreeGuideWizardPanel createFreeGuideWizardPanel(
			String configGroup, String configEntry) {
		
		// Get the type of this config entry expects
		int guessType = FreeGuideConfigGuesser.guessType(configGroup, configEntry);

		FreeGuideWizardPanel ans;
		
		switch(guessType) {
		case FreeGuideConfigGuesser.COMMANDS_TYPE:
			 ans = new FreeGuideCommandsWizardPanel();
		case FreeGuideConfigGuesser.CHOICE_TYPE:
			return new FreeGuideChoiceWizardPanel(FreeGuideConfigGuesser.guessChoices(configGroup, configEntry));
		case FreeGuideConfigGuesser.TEXT_TYPE:
			return new FreeGuideTextWizardPanel();
		case FreeGuideConfigGuesser.FILE_TYPE:
			return new FreeGuideFileWizardPanel();
		case FreeGuideConfigGuesser.DIRECTORY_TYPE:
			return new FreeGuideDirectoryWizardPanel();
		default:
			ans = null;
		}
		
		// Get a 2-entry array of stuff from the guesser that gives the messages
		// to ask the user
		String[] guessMessages = FreeGuideConfigGuesser.guessMessages(
			configGroup, configEntry);
		
		ans.setMessages(guessMessages[0], guessMessages[1]);
		ans.setConfig(configGroup, configEntry);
		ans.construct();
		
		return ans;
		
	}
	
	

}
