#!/bin/bash

# Packs the patched XMLTV code into a Windows EXE.

# Not implemented yet

exit

echo "Making PAR file..."

pp -p -o xmltv.par grab/de/tv_grab_de grab/dk/tv_grab_dk grab/es/tv_grab_es grab/fi/tv_grab_fi grab/hu/tv_grab_hu grab/it/tv_grab_it grab/na/tv_grab_na grab/nl/tv_grab_nl grab/uk/tv_grab_uk

echo "Removing main.pl..."

rm -rf xmltv_par
mkdir xmltv_par

cd xmltv_par

unzip -q ../xmltv.par

rm -f script/main.pl

cat MANIFEST | grep --invert-match script\/main.pl > MANIFEST.NEW

rm -f MANIFEST
mv MANIFEST.NEW MANIFEST

rm -f ../xmltv.par
zip -rq ../xmltv.par *

cd ..

rm -rf xmltv_par

echo "Creating executable..."

#parl -B -O./xmltv xmltv.par
pp -o xmltv xmltv.par

