#!/bin/sh

# This is my personal script for making a source distribution

cd $HOME/freeguide-tv
./maketxtreadme.sh

cd ..

find freeguide-tv -name "*.java"     	> tmpmksrc
find freeguide-tv -name "*.form"     	>> tmpmksrc
find freeguide-tv -name "*.sh"       	>> tmpmksrc
find freeguide-tv -name "*.lnk"      	>> tmpmksrc
echo "freeguide-tv/README.html"  	>> tmpmksrc
echo "freeguide-tv/README"       	>> tmpmksrc
echo "freeguide-tv/COPYING"      	>> tmpmksrc
echo "freeguide-tv/INSTALL"      	>> tmpmksrc
echo "freeguide-tv/TODO"		>> tmpmksrc

find .xmltv/freeguide-tv -name "*.css"	>> tmpmksrc

tar -czf freeguide-tv/freeguide-j2-src-0_4.tar.gz -T tmpmksrc

rm -f tmpmksrc

