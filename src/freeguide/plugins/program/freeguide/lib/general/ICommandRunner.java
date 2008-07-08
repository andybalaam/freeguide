package freeguide.plugins.program.freeguide.lib.general;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IProgress;

public interface ICommandRunner
{
	public boolean run(
        final IProgress progress, final ILogger logger );
}
