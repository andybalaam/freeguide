#!/usr/bin/python

import os

os.system( "echo Raw users per month for FreeGuide > hits.csv" )
os.system( "echo Month Hits >> hits.csv" )
os.system( "./processstats.py --hits userlog-2004-07-13.txt >> hits.csv" )

os.system( "echo Anonymous hits per month for FreeGuide > anon_hits.csv" )
os.system( "echo Month Hits >> anon_hits.csv" )
os.system( "./processstats.py --anon_hits userlog-2004-07-13.txt >> anon_hits.csv" )

os.system( "echo Unique hits per month for FreeGuide > unique_hits.csv" )
os.system( "echo Month Hits >> unique_hits.csv" )
os.system( "./processstats.py --unique_hits userlog-2004-07-13.txt >> unique_hits.csv" )

os.system( "./barchart hits.csv > plot_hits.pdf" )
os.system( "gv plot_hits.pdf &" )

os.system( "./barchart anon_hits.csv > plot_anon_hits.pdf" )
os.system( "gv plot_anon_hits.pdf &" )

os.system( "./barchart unique_hits.csv > plot_unique_hits.pdf" )
os.system( "gv plot_unique_hits.pdf &" )

