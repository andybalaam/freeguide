package freeguide.common.lib.general;

import java.io.File;

public class PathSearcher implements IPathSearcher
{
    protected String[] getPathDirs()
    {
        return null;
    }

    protected boolean existsAndIsExecutable( final File fullPath )
    {
        return false;
    }

    public String findInPath( String[] listOfExes, String fallbackExe )
    {
        String[] pathDirs = getPathDirs();

        for( int di = 0; di < pathDirs.length; di++ )
        {
            String dir = pathDirs[di];
            for( int ei = 0; ei < listOfExes.length; ei++ )
            {
                String exe = listOfExes[ei];
                if( existsAndIsExecutable( new File( dir, exe ) ) )
                {
                    return exe;
                }
            }
        }
        return fallbackExe;
    }
}

