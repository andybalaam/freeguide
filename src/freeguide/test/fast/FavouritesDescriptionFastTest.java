package freeguide.test.fast;

import freeguide.common.lib.fgspecific.Application;
import freeguide.common.lib.fgspecific.data.TVChannel;
import freeguide.common.lib.fgspecific.data.TVProgramme;
import freeguide.common.lib.fgspecific.selection.Favourite;
import freeguide.test.FreeGuideTest;

public class FavouritesDescriptionFastTest
{
    private TVProgramme prog1 = new TVProgramme();
    private TVProgramme prog2 = new TVProgramme();
    private TVProgramme prog3 = new TVProgramme();
    private TVProgramme prog4 = new TVProgramme();
    private TVProgramme prog5 = new TVProgramme();

    private void init( )
    {
        prog1.setTitle( "prog1" );
        prog1.setStart( 1000 );
        prog1.setDescription( "description1" );
        prog1.setExtraTag( "tagname1", "episode-num", "EP14" );
        prog1.setExtraTag( "tagname2", "Actor", "actor1" );
        prog1.setExtraTag( "tagname2", "Actor", "actor2" );
        prog1.setExtraTag( "tagname2", "Actor", "actor3" );
        prog1.setExtraTag( "tagname2", "Actor", "actor4" );
        prog1.setExtraTag( "tagname2", "Actor", "actor5" );
        prog1.setExtraTag( "tagname2", "Actor", "actor6" );

        prog2.setTitle( "prog2" );
        prog2.setStart( 1001 );
        prog2.setDescription( "description2" );
        prog2.setExtraTag( "tagname1", "episode-num", "EP25" );
        prog2.setExtraTag( "tagname2", "Actor", "actor7" );
        prog2.setExtraTag( "tagname2", "Actor", "actor2" );
        prog2.setExtraTag( "tagname2", "Actor", "actor8" );
        prog2.setExtraTag( "tagname2", "Actor", "actor9" );
        TVChannel chan1 = new TVChannel( "channel1" );
        chan1.setDisplayName( "Channel One" );
        chan1.put( prog1 );
        chan1.put( prog2 );

        prog3.setTitle( "prog3" );
        prog3.setStart( 1002 );
        prog3.setDescription ( "description2" );
        prog3.setExtraTag( "tagname1", "episode-num", "EP29" );
        TVChannel chan2 = new TVChannel( "channel2" );
        chan2.setDisplayName( "Channel Two" );
        chan2.put( prog3 );

        prog4.setTitle( "prog4" );
        prog4.setStart( 1003 );
        prog4.setDescription ( "description4" );

        prog5.setTitle( "prog5" );
        prog5.setStart( 1004 );
        prog5.setExtraTag( "tagname1", "episode-num", "EP29" );
        TVChannel chan3 = new TVChannel( "channel3" );
        chan3.setDisplayName( "Channel Three" );
        chan3.put( prog4 );
        chan3.put( prog5 );
    }

    private void testFindByTitleAndActor() throws Exception
    {
        Favourite fav = new Favourite();
        fav.setTitleContains( "prog2" );
        fav.setDescriptionContains( "actor2" );

        FreeGuideTest.my_assert( !fav.matches( prog1 ) );
        FreeGuideTest.my_assert( fav.matches( prog2 ) );
    }

    private void testFindByActorOnly() throws Exception
    {
        Favourite fav = new Favourite();
        fav.setDescriptionContains( "actor2" );

        FreeGuideTest.my_assert( fav.matches( prog1 ) );
        FreeGuideTest.my_assert( fav.matches( prog2 ) );
        FreeGuideTest.my_assert( !fav.matches( prog4 ) );
    }

    private void testFindByDescriptionOnly() throws Exception
    {
        Favourite fav = new Favourite();
        fav.setDescriptionContains( "description2" );

        FreeGuideTest.my_assert( fav.matches( prog2 ) );
        FreeGuideTest.my_assert( fav.matches( prog3 ) );
        FreeGuideTest.my_assert( !fav.matches( prog4 ) ) ;
    }

    private void testFindByEpisodeNumberAndChannel() throws Exception
    {
        Favourite fav = new Favourite();
        fav.setDescriptionContains ( "EP29" );
        fav.setChannelID( "channel3" );

        FreeGuideTest.my_assert( fav.matches( prog5 ) );
        FreeGuideTest.my_assert( !fav.matches( prog4 ) );

    }

    private void testEmptyProgrammeDoesNotMatch() throws Exception
    {
        Favourite fav = new Favourite();
        fav.setDescriptionContains ( "EP29" );

        TVProgramme prog = new TVProgramme();
        prog1.setTitle( "prog1" );
        prog1.setStart( 1000 );

        // This programme has no description or tags, so it can't
        // match the favourite.
        FreeGuideTest.my_assert( !fav.matches( prog ) );

    }

    public void run() throws Exception
    {
        Application.setInstance( new FakeApplication() ); // Needed for getTimeZone

        init( );
        testFindByTitleAndActor( );
        testFindByActorOnly( );
        testFindByDescriptionOnly( );
        testFindByEpisodeNumberAndChannel( );
        testEmptyProgrammeDoesNotMatch( );
    }
}

