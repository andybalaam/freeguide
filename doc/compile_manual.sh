#!/bin/sh
#compile the manual from the docbook sources to html

BUILDDIR=../build/doc/html

if [ ! -d $BUILDDIR ]; then
	mkdir -p $BUILDDIR
fi
rm $BUILDDIR/*

cp *.png $BUILDDIR/
cp *.css $BUILDDIR/

#normal chunked version with many files
#xmlto -o $BUILDDIR/ xhtml manual.xml
xsltproc \
 docbook-xsl-1.69.1/xhtml/chunk.xsl \
 manual.xml

# Copy the backward-compatibility html files into the build dir
cp *.html $BUILDDIR/

