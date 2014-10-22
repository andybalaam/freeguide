# $Id: Get_nice.pm,v 1.29 2014/04/13 16:12:17 bilbo_uk Exp $
#
# Library to wrap LWP::UserAgent to put in a random delay between
# requests and set the User-Agent string.  We really should be using
# LWP::RobotUI but this is better than nothing.
#
# If you're sure your app doesn't need a random delay (because it is
# fetching from a site designed for that purpose) then set
# $XMLTV::Get_nice::Delay to zero, or a value in seconds.  This is the
# maximum delay - on average the sleep will be half that.
#
#This random delay will be between 0 and 5 ($Delay) seconds. This means
# some sites will complain you're grabbing too fast (since 20% of your 
# grabs will be less than 1 second apart). To introduce a minimum delay 
# set $XMLTV::Get_nice::MinDelay to a value in seconds.
# This will be added to $Delay to derive the actual delay used. 
# E.g. Delay = 5 and MinDelay = 3, then the actual delay will be 
# between 3 and 8 seconds,
#
# get_nice() is the function to call, however
# XMLTV::Get_nice::get_nice_aux() is the one to cache with
# XMLTV::Memoize or whatever.  If you want an HTML::Tree object use
# get_nice_tree().
# Alternatively, get_nice_json() will get you a JSON object,
# or get_nice_xml() will get a XML::Parser 'Tree' object
#

use strict;

package XMLTV::Get_nice;

# use version number for feature detection:
# 0.005065 : new methods get_nice_json(), get_nice_xml()
# 0.005065 : add decode option to get_nice_tree()
# 0.005065 : expose the LWP response object ($Response)
# 0.005066 : support unknown tags in HTML::TreeBuilder ($IncludeUnknownTags)
our $VERSION = 0.005066;

use base 'Exporter';
our @EXPORT = qw(get_nice get_nice_tree get_nice_xml get_nice_json error_msg);
use LWP::UserAgent;
use XMLTV;
our $Delay = 5; # in seconds
our $MinDelay = 0; # in seconds
our $FailOnError = 1; # Fail on fetch error
our $Response; # LWP response object
our $IncludeUnknownTags = 0; # add support for HTML5 tags which are unknown to older versions of TreeBuilder (and therfore ignored by it)



our $ua = LWP::UserAgent->new;
$ua->agent("xmltv/$XMLTV::VERSION");
$ua->env_proxy;
our %errors = ();


sub error_msg($) {
    my ($url) = @_;
    $errors{$url};
}
sub get_nice( $ ) {
    # This is to ensure scalar context, to work around weirdnesses
    # with Memoize (I just can't figure out how SCALAR_CACHE and
    # LIST_CACHE relate to each other, with or without MERGE).
    #
    return scalar get_nice_aux($_[0]);
}

# Fetch page and return as HTML::Tree object.   
# Optional arguments:
# i) a function to put the page data through (eg, to clean up bad
# characters) before parsing.
# ii) convert incoming page to UNICODE using this codepage (use "UTF-8" for  strict utf-8)
#
sub get_nice_tree( $;$$ ) {
    my ($uri, $filter, $codepage) = @_;
    require HTML::TreeBuilder;
    my $content = get_nice $uri;
    $content = $filter->($content) if $filter;
    if ($codepage) {
      # (note: HTML::Parser->utf8_mode is only available Perl 5.8) 
      require Encode; import Encode;
      $content = decode($codepage, $content);
    }
    my $t = HTML::TreeBuilder->new();
		$t->ignore_unknown(!$IncludeUnknownTags);
		$t->parse($content) or die "cannot parse content of $uri\n";
    $t->eof;
    return $t;
}

# Fetch page and return as XML::Parser 'Tree' object.  
# Optional arguments:
# i) a function to put the page data through (eg, to clean up bad
# characters) before parsing.
# ii) convert incoming page to UNICODE using this codepage (use "UTF-8" for  strict utf-8)
#
sub get_nice_xml( $;$$ ) {
    my ($uri, $filter, $codepage) = @_;
    require XML::Parser;
    my $content = get_nice $uri;
    $content = $filter->($content) if $filter;
    if ($codepage) {
      require Encode; import Encode;
      $content = decode($codepage, $content);
    }
    my $t = XML::Parser->new(Style => 'Tree')->parse($content) or die "cannot parse content of $uri\n";
    return $t;
}

# Fetch page and return as JSON::PP object.  
# Optional arguments:
# i) a function to put the page data through (eg, to clean up bad
# characters) before parsing.
# ii) convert incoming UTF-8 to UNICODE
#
sub get_nice_json( $;$$ ) {
    my ($uri, $filter, $utf8) = @_;
    require JSON::PP;
    my $content = get_nice $uri;
    $content = $filter->($content) if $filter;
    $utf8 = defined $utf8 ? 1 : 0;
    my $t = JSON::PP->new()->utf8($utf8)->decode($content) or die "cannot parse content of $uri\n";
    return $t;
}

my $last_get_time;
sub get_nice_aux( $ ) {
    my $url = shift;

    if (defined $last_get_time) {
        # A page has already been retrieved recently.  See if we need
        # to sleep for a while before getting the next page - being
        # nice to the server.
        #
        my $next_get_time = $last_get_time + (rand $Delay) + $MinDelay;
        my $sleep_time = $next_get_time - time();
        sleep $sleep_time if $sleep_time > 0;
    }

    my $r = $ua->get($url);

    # Then start the delay from this time on the next fetch - so we
    # make the gap _between_ requests rather than from the start of
    # one request to the start of the next.  This punishes modem users
    # whose individual requests take longer, but it also punishes
    # downloads that take a long time for other reasons (large file,
    # slow server) so it's about right.
    #
    $last_get_time = time();

		# expose the response object for those grabbers which need to process the headers, status code, etc.
		$Response = $r;
		
    if ($r->is_error) {
        # At the moment download failures seem rare, so the script dies if
        # any page cannot be fetched.  We could later change this routine
        # to return undef on failure.  But dying here makes sure that a
        # failed page fetch doesn't get stored in XMLTV::Memoize's cache.
        #
        die "could not fetch $url, error: " . $r->status_line . ", aborting\n" if $FailOnError;
        $errors{$url} = $r->status_line;
        return undef;
    } else {
        return $r->content;
    }

}

1;
