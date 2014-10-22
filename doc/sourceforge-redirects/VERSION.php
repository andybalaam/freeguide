<?php

	print "0\n";
	print "11\n";
	print "1\n";

	$userlog = fopen( "/home/groups/f/fr/freeguide-tv/persistent/userlog.txt", "a" );
	
	$dt = date( "Y-m-d H:i:s" );

	$ip = $_GET{"ip"};

	if( $ip == "" ) {

		$ip = $_SERVER["REMOTE_ADDR"];

	}

	$ver = $_GET{"version"};
	
	fwrite( $userlog, $dt." - ".$ip." - ".$ver."\n" );

	fclose( $userlog );
?>
