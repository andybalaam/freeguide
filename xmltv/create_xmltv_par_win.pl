use strict;
use File::Copy;
use File::Remove qw(remove);

sub removefile( $ );
sub addfile( $ );

#
# create_xmltv_par_win.pl
#
# Copyright (c) 2004 by Andy Balaam
# Released under the GNU General Public License with ABSOLUTELY NO WARRANTY.
# See http://www.gnu.org/copyleft/gpl.html for more info.
#
# Uses PAR to pack a (possibly patched) XMLTV into a standalone Windows EXE.
#
# To run this you need a compiled and installed XMLTV, and the 7zip zip program.
# You'll also need to modify a few things just below this message.
#
# (A small amount of work should allow you to change the lines mentioning 7zip
# to use some other commandline zip program e.g. pkzip.)
#
# In general, your mileage may vary.
#
# Contact andybalaam at artificialworlds.net if you need help.
#

# Modify the following line to decide which scripts to include in your EXE
my $these_scripts = "grab/de/tv_grab_de grab/dk/tv_grab_dk grab/es/tv_grab_es grab/fi/tv_grab_fi grab/hu/tv_grab_hu grab/it/tv_grab_it grab/na/tv_grab_na grab/nl/tv_grab_nl grab/uk/tv_grab_uk";

# Is this the tkgui-patched sources or not?
my $tkgui_patched = 1;

# The following dir must contain a compiled and installed xmltv distribution
my $base_dir = "C:/BuildXMLTV/xmltv-0.5.27-tkgui";

# -------------------------------------------------------

chdir $base_dir;

remove \1, "tmp"; 
mkdir "tmp";

print "Making xmltv.par\n";

system "pp -p -o tmp/xmltv.par $these_scripts";

print "Unzipping and altering xmltv.par\n";

chdir "tmp";

system '"C:\Program Files\7-Zip\7z" -tzip x xmltv.par';

# Remove main.pl
removefile( "script/main.pl" );

# Add in all the libraries not included
if( $tkgui_patched ) {
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
}

unlink "xmltv.par";

system '"C:\Program Files\7-Zip\7z" -tzip -r a xmltv.par *';

print "Compiling altered xmltv.par\n";

system( "pp -o xmltv.exe xmltv.par" );

unlink "../xmltv.exe";

move( "xmltv.exe", "../" );

# ---------------------------------------------------------------------

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

