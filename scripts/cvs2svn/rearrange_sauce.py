#!/usr/bin/python

import os, re

pkg_re = re.compile( "package (.*);" )

def print_and_exec( command ):
	print command
	( child_stdin, child_stdout_and_stderr ) = os.popen4( command )
	for ln in child_stdout_and_stderr:
		print ln,

def add_all_dirs( new_filename ):
	
	split_name = new_filename.split('/')
	if new_filename[0] == '/':
		cur_filename = '/'
	else:
		cur_filename = ""
	
	for part_name in split_name[:-1]:
		cur_filename += part_name
		if not os.path.isdir( cur_filename + "/CVS" ):
			print_and_exec( "svn mkdir " + cur_filename )
		cur_filename += '/'
		
for ( dirpath, dirnames, filenames ) in os.walk( '.' ):
	for filename in filenames:
		if filename.endswith( ".java" ):
			full_path = "%s/%s" % ( dirpath, filename )
			if full_path[:2] == "./":
				full_path = full_path[2:]
			fl = file( full_path, 'r' )
			found_pkg = False
			for ln in fl:
				m = pkg_re.match( ln )
				if m:
					found_pkg = True
					new_filename = m.group( 1 ).replace( '.', '/' )
					new_filename += "/" + filename
					if new_filename != full_path:
						add_all_dirs( new_filename )
						print_and_exec( "svn move %s %s" % ( full_path, new_filename ) )
						
			if not found_pkg:
				print "'%s' does not contain a package line" % full_path
					
					



