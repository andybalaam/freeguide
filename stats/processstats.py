#!/usr/bin/python

statsfile = open( "userlog.txt", "r" )

lines = []

for line in statsfile.readlines():
    lines.append(line)


    
statsfile.close()

