#!/bin/bash

# A convenience script I use to generate the list of shared files
# for tv_grab_uk's install-win-uk.props file

# Example:

# ./share.sh tv_grab_uk

#or

# ./share.sh tv_grab_it 

COUNTRY=$1

#DIR="/cygdrive/c/Documents and Settings/root/My Documents/cvs-freeguide-tv/build/install-win/share/xmltv/$COUNTRY"
#DIR="/home/andy/code/freeguide-tv/package/windows/share/xmltv/$COUNTRY"
DIR="/home/andy/code/freeguide-tv/xmltv/share/xmltv/$COUNTRY"

SUBDIR=$2

TMPFILE=/tmp/fgshare-sh-count

FILES=`ls "$DIR/$SUBDIR"`

for FILE in $FILES; do {
	
	if [ -d "$DIR/$SUBDIR/$FILE" ]; then {

		if [ -z $SUBDIR ]; then {

			bash ./share.sh $COUNTRY "$FILE"
		
		}; else {
	
			bash ./share.sh $COUNTRY "$SUBDIR/$FILE"
	
		}; fi
	
	}; else {
	
		if [ -f "$TMPFILE" ]; then {

			COUNT=`cat $TMPFILE`

		}; else {

			COUNT=1

		}; fi
	
		if [ -z $SUBDIR ]; then {

			echo "file.$COUNT=share/xmltv/$COUNTRY/$FILE>%misc.share_directory%/$COUNTRY/$FILE"
		
		}; else {
	
			echo "file.$COUNT=share/xmltv/$COUNTRY/$SUBDIR/$FILE>%misc.share_directory%/$COUNTRY/$SUBDIR/$FILE"
	
		}; fi
	
		let COUNT++
		echo $COUNT > $TMPFILE
	
	}; fi

}; done

if [ -z "$SUBDIR" ]; then {

	rm -f $TMPFILE

} fi
