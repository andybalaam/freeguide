#!/bin/bash
#compile the manual from the docbook sources to html

if [ "$1" == "web" ] ; then {
	mv html-local html-local-real
	mv html-web html-local
}; fi

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

if [[ "$1" == "web" ]]; then {
	cp VERSION.php $BUILDDIR/
	cp upload-web.nosh $BUILDDIR/upload-web.sh
	chmod u+x $BUILDDIR/upload-web.sh
	mv html-local html-web
	mv html-local-real html-local
}; fi

