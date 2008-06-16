package freeguide.plugins.program.freeguide.lib.fgspecific;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IProgress;

public interface CommandRunner
{
	public boolean run(
        final IProgress progress, final ILogger logger );
}
