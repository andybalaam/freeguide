#!/usr/bin/python

import os, re

fileroot_re = re.compile( ".*/(.*).java" )

def print_and_exec( command ):
	print command
	( child_stdin, child_stdout_and_stderr ) = os.popen4( command )
	for ln in child_stdout_and_stderr:
		print ln,

fl = file( "moved_files.txt", 'r' )
a_lines = {}
r_lines = {}

for ln in fl:
	( prefix, filepath ) = ln.split()
	
	m = fileroot_re.match( filepath )
	if m:
		root = m.group( 1 )
		if prefix == "A":
			a_lines[root] = filepath
		elif prefix == "R":
			r_lines[root] = filepath
	

for root in r_lines.keys():
	r_filepath = r_lines[root]
	a_filepath = a_lines[root]
	print( "mv %s %s" % ( a_filepath, r_filepath ) )
	os.renames( a_filepath, r_filepath )
	print_and_exec( "cvs remove " + a_filepath )
	print_and_exec( "cvs add -r " + r_filepath )


					



