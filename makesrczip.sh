#!/bin/sh

find -name "*.java"        > tmp
find -name "*.form"       >> tmp
find -name "*.css"        >> tmp
find -name "*.pl"         >> tmp
find -name "README.html"  >> tmp
find -name "README"       >> tmp
find -name "COPYING"      >> tmp
find -name "INSTALL"      >> tmp

tar -czf freeguide-src.tar.gz -T tmp

rm -f tmp
