#!/bin/sh

./maketxtreadme.sh

find -name "*.java"        > tmp
find -name "*.form"       >> tmp
find -name "*.css"        >> tmp
echo "./xmltv/sort_listings_freeguide"         >> tmp
find -name "*.sh"         >> tmp
find -name "*.lnk"         >> tmp
echo "./README.html"  >> tmp
echo "./README"       >> tmp
echo "./COPYING"      >> tmp
echo "./INSTALL"      >> tmp

tar -czf freeguide-src.tar.gz -T tmp

rm -f tmp
