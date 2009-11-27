#!/bin/bash

# Run from the freeguide root directory.

VERSION=0.10.13

scp dist/deb/* dist/exe/* dist/install-tgz/* dist/tgz/* dist/rpm/* axis3x3,freeguide-tv@frs.sourceforge.net:/home/frs/project/f/fr/freeguide-tv/freeguide/$VERSION/

scp dist/src/* dist/srpm/* axis3x3,freeguide-tv@frs.sourceforge.net:/home/frs/project/f/fr/freeguide-tv/freeguide-source/$VERSION/

