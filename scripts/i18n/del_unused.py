#!/usr/bin/python -u

# This assumes there is a list of unused strings to be deleted from all the
# i18n files in a files called unused.txt in the current dir.

import os

i18n_dir = "../../src/resources/i18n"

i18n_dir_files = os.listdir( i18n_dir )

strs_to_delete = []

anal_fl = file( "unused.txt", 'r' )

for to_del in anal_fl:
	to_del = to_del.strip()
	strs_to_delete.append( to_del )

anal_fl.close()

for fn in i18n_dir_files:
	
	full_path = i18n_dir + "/" + fn
	
	if os.path.isfile( full_path ) and fn.endswith( ".properties" ):
		
		fl = file( full_path, 'r' )
		new_fl = None
		new_fl_contents = ""
		
		for ln in fl:
			delete_this_line = False
			if ln.find( '=' ) != -1:
				( key, val ) = ln.split( '=', 1 )
				key = key.strip()
				if key in strs_to_delete:
					if not new_fl:
						new_fl = file( full_path + ".new", 'w' )
					delete_this_line = True
			
			if not delete_this_line:
				new_fl_contents += ln
		
		fl.close()
		
		if new_fl:
			new_fl.write( new_fl_contents )
			new_fl.close()
			os.rename( full_path, full_path + ".old" )
			os.rename( full_path + ".new", full_path )
			print "  Changed: " + full_path
		else:
			print "Unchanged: " + full_path
		
	
