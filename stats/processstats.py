#!/usr/bin/python

import getopt
import re
import sys

(options_list, args) = getopt.getopt( sys.argv[1:], "", [ "help", "hits", "unique_hits", "anon_hits" ] )

options_dict = {}
for (option, value) in options_list:
    options_dict[option] = value

if "--help" in options_dict:
        print """Usage:
./processstats.py [--help] [--hits] [--unique_hits] stats_file
"""
        sys.exit( 0 )

if args[0] == "-":
    statsfile = sys.stdin
else:
    statsfile = open( args[0], "r" )

data_split_monthly = {}
hits_monthly = {}
ips_monthly = {}
hits_anon_monthly = {}

# Split the data into months
for line in statsfile.readlines():
    
    (datetime, ip, version) = line.split( " - " )
    
    tmp = re.search( "(.*)\n", version );
    version = tmp.group(1)
    
    (date, time) = datetime.split( " " )
    
    (year, month, day) = date.split( "-" )
    
    yearmonth = year + "-" + month
    
    if not data_split_monthly.has_key(yearmonth):
        data_split_monthly[yearmonth] = {}
    
    this_month = data_split_monthly[yearmonth]
    
    if not this_month.has_key(day):
        this_month[day] = []
    
    this_month[day].append( ( ip, version ) )

statsfile.close()

# ----------------------------------------------------------------------

for yearmonth in data_split_monthly.keys():
    hits_monthly[yearmonth] = 0
    this_month = data_split_monthly[yearmonth]
    for day in this_month.keys():
        for (ip, version) in this_month[day]:
            hits_monthly[yearmonth] += 1
            if not yearmonth in ips_monthly:
                ips_monthly[yearmonth] = {}
            if ip in ips_monthly[yearmonth]:
                ips_monthly[yearmonth][ip] += 1
            else:
                ips_monthly[yearmonth][ip] = 0
                
keys = hits_monthly.keys()
keys.sort()
for yearmonth in keys:
        
        print yearmonth,
        
        if "--hits" in options_dict:
            print " " + str( hits_monthly[yearmonth] ),

        if "--unique_hits" in options_dict:
            print " " + str( len( ips_monthly[yearmonth] ) ),

        if "--anon_hits" in options_dict:
            print " " + str( ips_monthly[yearmonth]["0.0.0.0"] ),
        
        print

