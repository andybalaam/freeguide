#!/usr/bin/python

import os, shutil

from_dir = '../../../freeguide-tv/HEAD/src'

for ( dirpath, dirnames, filenames ) in os.walk( from_dir ):
	for filename in filenames:
		if filename.endswith( ".java" ):
			full_path = "%s/%s" % ( dirpath, filename )
			shutil.copy( full_path, full_path[ len(from_dir)+1 :] )
 
