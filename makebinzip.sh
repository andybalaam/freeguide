#!/bin/sh

# This is my own personal script for making a distribution

cd $HOME/freeguide-tv
./maketxtreadme.sh

cd $HOME

find freeguide-tv/ -name "*.jar"        		>  mkbintmp
find freeguide-tv/ -name "*.css"        		>> mkbintmp

echo freeguide-tv/xmltv/getlistings_uk_ananova		>> mkbintmp
echo freeguide-tv/xmltv/sort_listings_freeguide		>> mkbintmp
echo freeguide-tv/xmltv/getlistings_na			>> mkbintmp

find freeguide-tv/ -name "runfreeguide.sh"		>> mkbintmp
find freeguide-tv/ -name "crimsonlicence.txt"		>> mkbintmp
find freeguide-tv/ -name "*.lnk"         		>> mkbintmp
find freeguide-tv/ -name "README.html"  		>> mkbintmp
find freeguide-tv/ -name "README"       		>> mkbintmp
find freeguide-tv/ -name "COPYING"      		>> mkbintmp
find freeguide-tv/ -name "INSTALL"      		>> mkbintmp

find .freeguide-tv/ -name "*.dtd"       		>> mkbintmp
find .freeguide-tv/ -name "freeguiderc.txt" 		>> mkbintmp

tar -czf freeguide-tv/freeguide-bin-0_2_1.tar.gz -T mkbintmp

rm -f mkbintmp