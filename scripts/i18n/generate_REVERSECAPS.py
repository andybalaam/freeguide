#!/usr/bin/python -u

# Run this script from within its own directory

import re, os

en_GB_files = []
en_REVERSECAPS_files = []

i18n_dir = "../resources/i18n"
plugins_dir = "../resources/freeguide/plugins"

en_GB_files.append( file( i18n_dir + "/MessagesBundle.en.properties", 'r' ) )
en_REVERSECAPS_files.append(
    file( i18n_dir + "/MessagesBundle.en.REVERSECAPS.properties", 'w' ) )

en_GB_files.append( file( i18n_dir + "/PrivacyBundle.en.html", 'r' ) )
en_REVERSECAPS_files.append(
    file( i18n_dir + "/PrivacyBundle.en.REVERSECAPS.html", 'w' ) )

for plugintype_dir in os.listdir( plugins_dir ):
    if plugintype_dir != "CVS":
        for plugin_dir in os.listdir( plugins_dir + "/" + plugintype_dir ):
            if plugin_dir != "CVS":
                en_GB_files.append(
                    file( plugins_dir + "/" + plugintype_dir + "/" +
                        plugin_dir + "/i18n.en.properties", 'r' ) )
                en_REVERSECAPS_files.append(
                    file( plugins_dir + "/" + plugintype_dir + "/" +
                        plugin_dir + "/i18n.en.REVERSECAPS.properties", 'w' ) )
    
for i in range( len( en_GB_files ) ):
    
    for line in en_GB_files[i].readlines():
    
        split_line = line.split( '=', 1 )
    
        if isinstance( split_line, list ) and len( split_line ) ==2:
            new_line = "%s=%s" % ( split_line[0], split_line[1].swapcase() )
        elif line[0] == "#":
            new_line = line
        else:
            new_line = line.swapcase()
        
        en_REVERSECAPS_files[i].write( new_line )
        
    en_REVERSECAPS_files[i].close()
    en_GB_files[i].close()

