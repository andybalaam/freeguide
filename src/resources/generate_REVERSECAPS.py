#!/usr/bin/python -u

import re

en_GB_files = []
en_REVERSECAPS_files = []

en_GB_files.append( file( "MessagesBundle.properties", 'r' ) )
en_REVERSECAPS_files.append( file( "MessagesBundle_en_REVERSECAPS.properties", \
    'w' ) )

en_GB_files.append( file( "PrivacyBundle.properties", 'r' ) )
en_REVERSECAPS_files.append( file( "PrivacyBundle_en_REVERSECAPS.properties", \
    'w' ) )
    
for i in range( len( en_GB_files ) ):
    
    for line in en_GB_files[i].readlines():
    
        split_line = line.split( '=', 1 )
    
        if isinstance( split_line, list ) and len( split_line ) ==2:
            new_line = "%s=%s" % ( split_line[0], split_line[1].swapcase() )
        else:
            new_line = line
        
        en_REVERSECAPS_files[i].write( new_line )
        
    en_REVERSECAPS_files[i].close()
    en_GB_files[i].close()

