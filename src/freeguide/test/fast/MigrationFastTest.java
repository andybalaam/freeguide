package freeguide.test.fast;

import java.util.Map;
import java.util.logging.Level;

import freeguide.plugins.program.freeguide.FreeGuide;
import freeguide.plugins.program.freeguide.migration.Migrate0_10_12To0_11;
import freeguide.test.FreeGuideTest;

public class MigrationFastTest
{
    public void run() throws Exception
    {
        // Suppress INFO log messages.
        FreeGuide.log.setLevel( Level.WARNING );
        FreeGuide.log.getParent(  ).setLevel( Level.WARNING );
        FreeGuide.log.getParent(  ).getHandlers(  )[0].setLevel(
            Level.WARNING );

        test_0_10_12To0_11();
        test_0_10_12To0_11_NoPanelWidth();
    }

    private void test_0_10_12To0_11()
    throws Exception
    {
        // Sub-class to access members and bypass initialisation
        class Migr extends Migrate0_10_12To0_11
        {
            public Map getFrom()
            {
                return prefFrom;
            }
        }

        Migr migr = new Migr();

        migr.getFrom().put(
            "modules/ui-horizontal/sizeProgrammePanelWidth", "8000" );

        migr.migrate();

        // The old programme panel size is gone
        FreeGuideTest.my_assert(
            migr.getResult().get(
                "modules/ui-horizontal/sizeProgrammePanelWidth" )
            == null );

        // The new programme hour width is old width / 24
        FreeGuideTest.my_assert(
            migr.getResult().get(
                "modules/ui-horizontal/sizeProgrammeHour" )
                .equals( "333" ) );
    }


    private void test_0_10_12To0_11_NoPanelWidth()
    throws Exception
    {
        Migrate0_10_12To0_11 migr = new Migrate0_10_12To0_11();

        // Don't put in a sizeProgrammePanelWidth

        migr.migrate();

        // No exception should be thrown, even though the expected
        // setting is missing.
    }

}
