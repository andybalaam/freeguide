# Not sure what to do with #!

use strict;
use File::Copy;
use File::Remove qw(remove);

# Packs the patched XMLTV code into a Windows EXE.
# The below dir must contain a compiled and installed xmltv distribution
# This script uses lots of assumptions about stuff that's installed, and
# where it's installed, so it will require a little work to make it work
# on a different machine.

chdir "C:/BuildXMLTV/xmltv-0.5.25-tkgui";

remove \1, "tmp"; 
mkdir "tmp";

print "Making xmltv.par\n";

system "pp -p -o tmp/xmltv.par grab/de/tv_grab_de grab/dk/tv_grab_dk grab/es/tv_grab_es grab/fi/tv_grab_fi grab/hu/tv_grab_hu grab/it/tv_grab_it grab/na/tv_grab_na grab/nl/tv_grab_nl grab/uk/tv_grab_uk";

print "Unzipping and altering xmltv.par\n";

chdir "tmp";

system '"C:\Program Files\7-Zip\7z" -tzip x xmltv.par';

# Remove main.pl

sub removefile( $ ) {
	
	my $fl = shift;
	
	# Remove the file
	unlink "$fl";
	
	# And remove from the MANIFEST
	
	open MANIFEST, "<MANIFEST";
	open MANIFESTNEW, ">MANIFEST.NEW";
	
	while( <MANIFEST> ) {
		
		if( $_ !~ /$fl/ ) {
			
			print MANIFESTNEW "$_";
			
		}
		
	}
	
	close MANIFEST;
	close MANIFESTNEW;
	
	unlink "MANIFEST";
	rename "MANIFEST.NEW", "MANIFEST";
	
}

sub addfile( $ ) {
	
	my $fl = shift;
	
	$fl =~ /(.*)\/.*?\.pm/;
	
	my $dr = $1;
	
	copy( "C:/perl/site/5.8.0/$fl", "$dr" );
	
	open MANIFEST, ">>MANIFEST";
	
	print MANIFEST "$fl\n";

	close MANIFEST;
	
}

removefile( "script/main.pl" );

mkdir "lib/XMLTV/Ask";
addfile( "lib/XMLTV/Ask/GDialog.pm" );
addfile( "lib/XMLTV/Ask/Term.pm" );
addfile( "lib/XMLTV/Ask/Tk.pm" );

mkdir "lib/XMLTV/ProgressBar";
addfile( "lib/XMLTV/ProgressBar/GDialog.pm" );
addfile( "lib/XMLTV/ProgressBar/None.pm" );
addfile( "lib/XMLTV/ProgressBar/Term.pm" );
addfile( "lib/XMLTV/ProgressBar/Tk.pm" );

addfile( "lib/Tk/ProgressBar.pm" );

unlink "xmltv.par";

system '"C:\Program Files\7-Zip\7z" -tzip -r a xmltv.par *';

print "Compiling altered xmltv.par\n";

system( "pp -o xmltv.exe xmltv.par" );

unlink "../xmltv.exe";

move( "xmltv.exe", "../" );

