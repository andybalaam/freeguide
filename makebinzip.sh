#!/bin/sh

# This is my own personal script for making a distribution.

# Make the JAR file
cd $HOME/freeguide-tv/gui-java/
./makejar.sh

# Make the text version of the README
cd ..
./maketxtreadme.sh

# Do the rest
cd ..

find freeguide-tv/ -name "*.jar"        		>  mkbintmp

find freeguide-tv/ -name "runfreeguide.sh"		>> mkbintmp
find freeguide-tv/ -name "*.lnk"         		>> mkbintmp
find freeguide-tv/ -name "README.html"  		>> mkbintmp
find freeguide-tv/ -name "README"       		>> mkbintmp
find freeguide-tv/ -name "COPYING"      		>> mkbintmp
find freeguide-tv/ -name "INSTALL"      		>> mkbintmp
find freeguide-tv/ -name "TODO"				>> mkbintmp

find .xmltv/freeguide-tv/ -name "*.dtd"       		>> mkbintmp
find .xmltv/freeguide-tv/ -name "*.css" 		>> mkbintmp

tar -czf freeguide-tv/freeguide-j2-bin-0_3.tar.gz -T mkbintmp

rm -f mkbintmp
