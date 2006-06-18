#!/bin/bash

# This script is run on the sourceforge server to create 
# a build of the latest version of freeguide every night.

function die
{
	echo "******** $1" 1>&2
	exit 1
}

FG_HOME=/home/groups/f/fr/freeguide-tv
START_DIR=$FG_HOME/svn-builds
OUTPUT_DIR=$FG_HOME/htdocs/nightbuilds
export ANT_HOME=$START_DIR/apache-ant-1.6.5
ANT_CMD=$ANT_HOME/bin/ant
export JAVA_HOME=/usr/java/j2sdk1.4.2_07/

cd $START_DIR

if [ -d freeguide-tv ]; then
{
	rm -rf freeguide-tv
}; fi

svn checkout https://svn.sourceforge.net/svnroot/freeguide-tv/trunk/freeguide-tv freeguide-tv || die "svn failed"

cd freeguide-tv

$ANT_CMD clean || die "clean failed"
$ANT_CMD jars  || die "jars failed"

cd build/package

FILENAME=freeguide-nightly-`date '+%Y%m%d'`.zip

zip -qr $FILENAME * || die "zip failed"

# Move the latest build into the build dir
mv $FILENAME $OUTPUT_DIR/

# Delete files older then 28 days from the nightly build dir

TO_REMOVE=`find $OUTPUT_DIR/ -mtime +28 | xargs echo`

if [ -n $TO_REMOVE ]; then
{
	rm $TO_REMOVE
};fi

