#!/bin/bash

# Run from the freeguide root directory.

echo "rm *" | sftp -b - axis3x3,freeguide-tv@web.sourceforge.net:/home/groups/f/fr/freeguide-tv/htdocs/rc

scp dist/bin/* axis3x3,freeguide-tv@web.sourceforge.net:/home/groups/f/fr/freeguide-tv/htdocs/rc/


