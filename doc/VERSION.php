<?php

	print "0\n";
	print "7\n";
	print "3\n";

	$userlog = fopen( "/home/groups/f/fr/freeguide-tv/userlog/userlog.txt", "a" );
	
	$dt = date( "Y-m-d H:i:s" );

	$ip = $_GET{"ip"};

	if( $ip == "" ) {

		$ip = $_SERVER["REMOTE_ADDR"];

	}

	$ver = $_GET{"version"};
	
	fwrite( $userlog, $dt." - ".$ip." - ".$ver."\n" );
	
?>
