package freeguide.plugins.program.freeguide.lib.fgspecific;

import java.util.Iterator;
import java.util.logging.Level;

import freeguide.common.plugininterfaces.ILogger;
import freeguide.common.plugininterfaces.IModuleGrabber;
import freeguide.common.plugininterfaces.IProgress;
import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.lib.general.ICommandRunner;
import freeguide.plugins.program.freeguide.viewer.MainController;

public class ConfigCommandRunner implements ICommandRunner
{

	public boolean run( IProgress progress, ILogger logger )
	{
		boolean cmdSucceeded = true;

		Iterator it = MainController.config.activeGrabberIDs.iterator(  );

		while( it.hasNext(  ) )
		{
			String grabberID = (String)it.next(  );

			try
			{
				IModuleGrabber grabber =
					(IModuleGrabber)PluginsManager.getModuleByID(
						grabberID );

				if( grabber == null )
				{
					FreeGuide.log.warning(
						"There is no grabber " + grabberID );

					continue;

				}

				if( Thread.interrupted(  ) )
				{
					break;
				}
				cmdSucceeded = grabber.chooseChannels(
				        progress, logger );

				if( Thread.interrupted(  ) )
				{
					break;
				}
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
							"Error configuring grabber '" + grabberID
							+ "'", (Exception)ex );
					}
					else
					{
						logger.error(
							"Error configuring grabber '" + grabberID
							+ "': " + ex.getClass(  ).getName(  ) );
					}
				}

				FreeGuide.log.log(
					Level.WARNING,
					"Error configuring grabber '" + grabberID, ex );
			}

		}
		return cmdSucceeded;
	}
}
