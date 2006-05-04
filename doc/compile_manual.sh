#!/bin/sh
#compile the manual from the docbook sources to html

#   Note: you need to modify doc/docbook-xsl-1.69.1/xhtml/freeguide-things.xsl
#   if you are going to change this, so that the image is loaded from a remote
#   server instead of locally.
#   You also need to change freeguide-param.xsl (same dir) to build into the
#   correct directory.   Would be good to make this easier.
#BUILDDIR="html-web"
BUILDDIR="html-local"

# Might be needed when we are really building into the build dir
#if [ ! -d $BUILDDIR ]; then
#	mkdir -p $BUILDDIR
#fi
#rm $BUILDDIR/*

#normal chunked version with many files
#xmlto -o $BUILDDIR/ xhtml manual.xml
xsltproc \
 docbook-xsl-1.69.1/xhtml/chunk.xsl \
 manual.xml

cp *.png $BUILDDIR/
cp *.css $BUILDDIR/

# Copy the backward-compatibility html files into the build dir
cp COPYING TODO *.html $BUILDDIR/

# Comment out for local
#cp VERSION.php $BUILDDIR/

