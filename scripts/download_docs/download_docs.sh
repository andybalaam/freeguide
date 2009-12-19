#!/bin/bash

# Run from inside scripts/download_docs/

cd ../../doc/

rm -r www.artificialworlds.net

wget -nv --mirror --no-parent --convert-links --html-extension http://www.artificialworlds.net/freeguide/

cd www.artificialworlds.net/freeguide/
find ./ -name "*action=*.html" -delete

for RE in \
	"<a [^>]*%3Faction=[^>]*>[^<]*<\/a>" \
	"<a[^>]*AllRecentChanges[^>]*>All<\/a>" \
	"<a[^>]*Site\/Search[^>]*>Search<\/a>" \
	"<a accesskey=''  class='selflink' href='[^']*HomePage.html'>View<\/a>" \
	; do
{
	find ./ -name "*.html" | xargs perl -pi -e "s/$RE//g;"
}; done

cd ../../../
rm -r doc-bin
mv doc/www.artificialworlds.net/freeguide doc-bin
rmdir doc/www.artificialworlds.net

