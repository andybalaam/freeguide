#!/bin/sh

# This is my own personal script for making a distribution

cd $HOME/freeguide-tv
./maketxtreadme.sh

cd $HOME

find freeguide-tv/ -name "*.jar"        		>  mkbintmp
find freeguide-tv/ -name "*.css"        		>> mkbintmp
find freeguide-tv/xmltv -path "*"				>> mkbintmp
find freeguide-tv/ -name "runfreeguide.sh"		>> mkbintmp
find freeguide-tv/ -name "*.lnk"         		>> mkbintmp
find freeguide-tv/ -name "README.html"  		>> mkbintmp
find freeguide-tv/ -name "README"       		>> mkbintmp
find freeguide-tv/ -name "COPYING"      		>> mkbintmp
find freeguide-tv/ -name "INSTALL"      		>> mkbintmp

find .freeguide-tv/ -name "*.dtd"       		>> mkbintmp
find .freeguide-tv/ -name "freeguiderc.txt" 	>> mkbintmp

tar -czf freeguide-tv/freeguide-bin.tar.gz -T mkbintmp

rm -f mkbintmp
