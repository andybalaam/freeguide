package freeguide.plugins.grabber.xmltv;

import java.util.logging.Level;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.plugins.grabber.xmltv.XMLTVConfig.ModuleInfo;
import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.general.ICommandRunner;

public class SingleConfigCommandRunner implements ICommandRunner
{
    IModuleGrabber grabber;
    ModuleInfo moduleInfo;

    SingleConfigCommandRunner( IModuleGrabber grabber, ModuleInfo moduleInfo )
    {
        this.grabber = grabber;
        this.moduleInfo = moduleInfo;
    }

	public boolean run( IProgress progress, ILogger logger )
	{
		boolean cmdSucceeded = true;

		try
		{
            int retCode = grabber.chooseChannelsOne( moduleInfo, progress, logger );
            cmdSucceeded = ( retCode == 0 );
		}
		/*catch( ClosedByInterruptException ex )
		{
			break;
		}
		catch( InterruptedException ex )
		{
			break;
		}*/
		catch( Throwable ex )
		{
			cmdSucceeded = false;

			if( logger != null )
			{
				if( ex instanceof Exception )
				{
					logger.error(
						"Error configuring grabber '"
						+ "'", (Exception)ex );
				}
				else
				{
					logger.error(
						"Error configuring grabber '"
						+ "': " + ex.getClass(  ).getName(  ) );
				}
			}

			FreeGuide.log.log(
				Level.WARNING,
				"Error configuring by grabber '", ex );
		}

		return cmdSucceeded;
	}
}
