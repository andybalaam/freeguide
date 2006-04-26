#!/usr/bin/python

import os, re

clip_path_re = re.compile(
	".*/freeguide/plugins/(.*)/i18n\\.(.*)\\.properties" )

svn_path_re = re.compile( ".*/\\.svn/.*" )

def print_and_exec( command ):
	print command
	( child_stdin, child_stdout_and_stderr ) = os.popen4( command )
	for ln in child_stdout_and_stderr:
		print ln,

# def add_all_dirs( new_filename ):
	# 
	# split_name = new_filename.split('/')
	# if new_filename[0] == '/':
		# cur_filename = '/'
	# else:
		# cur_filename = ""
	# 
	# for part_name in split_name[:-1]:
		# cur_filename += part_name
		# if not os.path.isdir( cur_filename + "/CVS" ):
			# print_and_exec( "svn mkdir " + cur_filename )
		# cur_filename += '/'
		
for ( dirpath, dirnames, filenames ) in os.walk( '.' ):
	for filename in filenames:
		full_path = "%s/%s" % ( dirpath, filename )
		m = clip_path_re.match( full_path )
		if m and not svn_path_re.match( full_path):
			plugin = m.group( 1 ).replace( '/', '_' )
			language = m.group( 2 )
			
			new_filename = "resources/i18n/%s.%s.properties" % (
				plugin, language )
				
			print_and_exec( "svn move %s %s" % ( full_path, new_filename ) )
					
					



