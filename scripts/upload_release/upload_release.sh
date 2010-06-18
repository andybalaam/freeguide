#!/bin/bash

# Run from the freeguide root directory.

# TODO: get version number from file names
VERSION=0.11

scp dist/bin/* axis3x3,freeguide-tv@frs.sourceforge.net:/home/frs/project/f/fr/freeguide-tv/freeguide/$VERSION/

scp dist/src/* axis3x3,freeguide-tv@frs.sourceforge.net:/home/frs/project/f/fr/freeguide-tv/freeguide-source/$VERSION/

