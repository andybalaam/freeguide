<?php

	print "0\n";
	print "10\n";
	print "10\n";

	$userlog = fopen( "/tmp/persistent/freeguide-tv/userlog.txt", "a" );
	
	$dt = date( "Y-m-d H:i:s" );

	$ip = $_GET{"ip"};

	if( $ip == "" ) {

		$ip = $_SERVER["REMOTE_ADDR"];

	}

	$ver = $_GET{"version"};
	
	fwrite( $userlog, $dt." - ".$ip." - ".$ver."\n" );
	
?>
