# -*- perl -*-
# $Id: XMLTV.pm.in,v 1.134 2008/02/17 07:08:59 rmeden Exp $
package XMLTV;

use strict;
use base 'Exporter';
our @EXPORT = ();
our @EXPORT_OK = qw(read_data parse parsefile write_data
		    best_name list_channel_keys list_programme_keys);

# For the time being the version of this library is tied to that of
# the xmltv package as a whole.  This number should be checked by the
# mkdist tool.
#
our $VERSION = '0.5.51';

# Work around changing behaviour of XML::Twig.  On some systems (like
# mine) it always returns UTF-8 data unless KeepEncoding is specified.
# However the encoding() method tells you the encoding of the original
# document, not of the data you receive.  To be sure of what you're
# getting, it is easiest on such a system to not give KeepEncoding and
# just use UTF-8.
#
# But on other systems (seemingly perl 5.8 and above), XML::Twig tries
# to keep the original document's encoding in the strings returned.
# You then have to call encoding() to find out what you're getting.
# To make sure of this behaviour we set KeepEncoding to true on such a
# system.
#
# Setting KeepEncoding true everywhere seems to do no harm, it's a
# pity that we lose conversion to UTF-8 but at least it's the same
# everywhere.  So the library is distributed with this flag on.
#
my $KEEP_ENCODING = 1;

my %warned_unknown_key;
sub warn_unknown_keys( $$ );

=pod

=head1 NAME

XMLTV - Perl extension to read and write TV listings in XMLTV format

=head1 SYNOPSIS

  use XMLTV;
  my $data = XMLTV::parsefile('tv.xml');
  my ($encoding, $credits, $ch, $progs) = @$data;
  my $langs = [ 'en', 'fr' ];
  print 'source of listings is: ', $credits->{'source-info-name'}, "\n"
      if defined $credits->{'source-info-name'};
  foreach (values %$ch) {
      my ($text, $lang) = @{XMLTV::best_name($langs, $_->{'display-name'})};
      print "channel $_->{id} has name $text\n";
      print "...in language $lang\n" if defined $lang;
  }
  foreach (@$progs) {
      print "programme on channel $_->{channel} at time $_->{start}\n";
      next if not defined $_->{desc};
      foreach (@{$_->{desc}}) {
          my ($text, $lang) = @$_;
          print "has description $text\n";
          print "...in language $lang\n" if defined $lang;
      }
  }

The value of $data will be something a bit like:

  [ 'UTF-8',
    { 'source-info-name' => 'Ananova', 'generator-info-name' => 'XMLTV' },
    { 'radio-4.bbc.co.uk' => { 'display-name' => [ [ 'en',  'BBC Radio 4' ],
						   [ 'en',  'Radio 4'     ],
						   [ undef, '4'           ] ],
			       'id' => 'radio-4.bbc.co.uk' },
      ... },
    [ { start => '200111121800', title => [ [ 'Simpsons', 'en' ] ],
        channel => 'radio-4.bbc.co.uk' },
      ... ] ]

=head1 DESCRIPTION

This module provides an interface to read and write files in XMLTV
format (a TV listings format defined by xmltv.dtd).  In general element
names in the XML correspond to hash keys in the Perl data structure.
You can think of this module as a bit like B<XML::Simple>, but
specialized to the XMLTV file format.

The Perl data structure corresponding to an XMLTV file has four
elements.  The first gives the character encoding used for text data,
typically UTF-8 or ISO-8859-1.  (The encoding value could also be
undef meaning 'unknown', when the library canE<39>t work out what it
is.)  The second element gives the attributes of the root <tv>
element, which give information about the source of the TV listings.
The third element is a list of channels, each list element being a
hash corresponding to one <channel> element.  The fourth element is
similarly a list of programmes.  More details about the data structure
are given later.  The easiest way to find out what it looks like is to
load some small XMLTV files and use B<Data::Dumper> to print out the
resulting structure.

=head1 USAGE

=over

=cut

use XML::Twig;
use XML::Writer 0.600;
use Date::Manip;
use Carp;
use Data::Dumper;

# Use Lingua::Preferred if available, else kludge a replacement.
sub my_which_lang { return $_[1]->[0] }
BEGIN {
    eval { require Lingua::Preferred };
    *which_lang = $@ ? \&my_which_lang : \&Lingua::Preferred::which_lang;
}

# Use Log::TraceMessages if installed.
BEGIN {
    eval { require Log::TraceMessages };
    if ($@) {
	*t = sub {};
	*d = sub { '' };
    }
    else {
	*t = \&Log::TraceMessages::t;
	*d = \&Log::TraceMessages::d;
    }
}

# Attributes and subelements of channel.  Each subelement additionally
# needs a handler defined.  Multiplicity is given for both, but for
# attributes the only allowable values are '1' and '?'.
#
# Ordering of attributes is not really important, but we keep the same
# order as they are given in the DTD so that output looks nice.
#
# The ordering of the subelements list gives the order in which these
# elements must appear in the DTD.  In fact, these lists just
# duplicate information in the DTD and add details of what handlers
# to call.
#
our @Channel_Attributes = ([ 'id', '1' ]);
our @Channel_Handlers =
  (
   [ 'display-name', 'with-lang', '+' ],
   [ 'icon',         'icon',      '*' ],
   [ 'url',          'scalar',    '*' ],
  );

# Same for <programme> elements.
our @Programme_Attributes =
  (
   [ 'start',     '1' ],
   [ 'stop',      '?' ],
   [ 'pdc-start', '?' ],
   [ 'vps-start', '?' ],
   [ 'showview',  '?' ],
   [ 'videoplus', '?' ],
   [ 'channel',   '1' ],
   [ 'clumpidx',  '?' ],
  );
our @Programme_Handlers =
  (
   [ 'title',            'with-lang',          '+' ],
   [ 'sub-title',        'with-lang',          '*' ],
   [ 'desc',             'with-lang/m',        '*' ],
   [ 'credits',          'credits',            '?' ],
   [ 'date',             'scalar',             '?' ],
   [ 'category',         'with-lang',          '*' ],
   [ 'language',         'with-lang',          '?' ],
   [ 'orig-language',    'with-lang',          '?' ],
   [ 'length',           'length',             '?' ],
   [ 'icon',             'icon',               '*' ],
   [ 'url',              'scalar',             '*' ],
   [ 'country',          'with-lang',          '*' ],
   [ 'episode-num',      'episode-num',        '*' ],
   [ 'video',            'video',              '?' ],
   [ 'audio',            'audio',              '?' ],
   [ 'previously-shown', 'previously-shown',   '?' ],
   [ 'premiere',         'with-lang/em',       '?' ],
   [ 'last-chance',      'with-lang/em',       '?' ],
   [ 'new',              'presence',           '?' ],
   [ 'subtitles',        'subtitles',          '*' ],
   [ 'rating',           'rating',             '*' ],
   [ 'star-rating',      'star-rating',        '*' ],
  );

# And a hash mapping names like 'with-lang' to pairs of subs.  The
# first for reading, the second for writing.  Note that the writers
# alter the passed-in data as a side effect!  (If the writing sub is
# called with an undef XML::Writer then it writes nothing but still
# warns for (most) bad data checks - and still alters the data.)
#
our %Handlers = ();

# Undocumented interface for adding extensions to the XMLTV format:
# first add an entry to @XMLTV::Channel_Handlers or
# @XMLTV::Programme_Handlers with your new element's name, 'type' and
# multiplicity.  The 'type' should be a string you invent yourself.
# Then $XMLTV::Handlers{'type'} should be a pair of subroutines, a
# reader and a writer.  (Unless you want to use one of the existing
# types such as 'with-lang' or 'scalar'.)
#
# Note that elements and attributes beginning 'x-' are skipped over
# _automatically_, so you can't parse them with this method.  A better
# way to add extensions is needed - doing this not encouraged but is
# sometimes necessary.
#

# read_data() is a deprecated name for parsefile().
sub read_data( $ ) { # FIXME remove altogether
    warn "XMLTV::read_data() deprecated, use XMLTV::parsefile() instead\n";
    &parsefile;
}

# Private.
sub sanity( $ ) {
    for (shift) {
    	croak 'no <tv> element found' if not /<tv/;
    }
}

=pod

=item parse(document)

Takes an XMLTV document (a string) and returns the Perl data
structure.  It is assumed that the document is valid XMLTV; if not
the routine may die() with an error (although the current implementation
just warns and continues for most small errors).

The first element of the listref returned, the encoding, may vary
according to the encoding of the input document, the versions of perl
and C<XML::Parser> installed, the configuration of the XMLTV library
and other factors including, but not limited to, the phase of the
moon.  With luck it should always be either the encoding of the input
file or UTF-8.

Attributes and elements in the XML file whose names begin with 'x-'
are skipped silently.  You can use these to include information which
is not currently handled by the XMLTV format, or by this module.

=cut
sub parse( $ ) {
    my $str = shift;
    sanity($str);
    # FIXME commonize with parsefiles()
    my ($encoding, $credits);
    my %channels;
    my @programmes;
    parse_callback($str,
		   sub { $encoding = shift },
		   sub { $credits = shift },
		   sub { for (shift) { $channels{$_->{id}} = $_ } },
		   sub { push @programmes, shift });
    return [ $encoding, $credits, \%channels, \@programmes ];
}

=pod

=item parsefiles(filename...)

Like C<parse()> but takes one or more filenames instead of a string
document.  The data returned is the merging of those file contents:
the programmes will be concatenated in their original order, the
channels just put together in arbitrary order (ordering of channels
should not matter).

It is necessary that each file have the same character encoding, if
not, an exception is thrown.  Ideally the credits information would
also be the same between all the files, since there is no obvious way to
merge it - but if the credits information differs from one file to the
next, one file is picked arbitrarily to provide credits and a warning
is printed.  If two files give differing channel definitions for the
same XMLTV channel id, then one is picked arbitrarily and a warning
is printed.

In the simple case, with just one file, you neednE<39>t worry
about mismatching of encodings, credits or channels.

The deprecated function C<parsefile()> is a wrapper allowing just one
filename.

=cut
sub parsefiles( @ ) {
    die 'one or more filenames required' if not @_;
    my ($encoding, $credits);
    my %channels;
    my @programmes;
    parsefiles_callback(sub { $encoding = shift },
			sub { $credits = shift },
			sub { for (shift) { $channels{$_->{id}} = $_ } },
			sub { push @programmes, shift },
			@_);
    return [ $encoding, $credits, \%channels, \@programmes ];
}

sub parsefile( $ ) { parsefiles(@_) }

=pod

=item parse_callback(document, encoding_callback, credits_callback,
                     channel_callback, programme_callback)

An alternative interface.  Whereas C<parse()> reads the whole document
and then returns a finished data structure, with this routine you
specify a subroutine to be called as each <channel> element is read
and another for each <programme> element.

The first argument is the document to parse.  The remaining arguments
are code references, one for each part of the document.

The callback for encoding will be called once with a string giving the
encoding.  In present releases of this module, it is also possible for
the value to be undefined meaning 'unknown', but itE<39>s hoped that
future releases will always be able to figure out the encoding used.

The callback for credits will be called once with a hash reference.
For channels and programmes, the appropriate function will be called
zero or more times depending on how many channels / programmes are
found in the file.

The four subroutines will be called in order, that is, the encoding
and credits will be done before the channel handler is called and all
the channels will be dealt with before the first programme handler is
called.

If any of the code references is undef, nothing is called for that part
of the file.

For backwards compatibility, if the value for 'encoding callback' is
not a code reference but a scalar reference, then the encoding found
will be stored in that scalar.  Similarly if the 'credits callback'
is a scalar reference, the scalar it points to will be set to point
to the hash of credits.  This style of interface is deprecated: new
code should just use four callbacks.

For example:

    my $document = '<tv>...</tv>';

    my $encoding;
    sub encoding_cb( $ ) { $encoding = shift }

    my $credits;
    sub credits_cb( $ ) { $credits = shift }

    # The callback for each channel populates this hash.
    my %channels;
    sub channel_cb( $ ) {
	my $c = shift;
	$channels{$c->{id}} = $c;
    }

    # The callback for each programme.  We know that channels are
    # always read before programmes, so the %channels hash will be
    # fully populated.
    #
    sub programme_cb( $ ) {
        my $p = shift;
        print "got programme: $p->{title}->[0]->[0]\n";
        my $c = $channels{$p->{channel}};
        print 'channel name is: ', $c->{'display-name'}->[0]->[0], "\n";
    }

    # Let's go.
    XMLTV::parse_callback($document, \&encoding_cb, \&credits_cb,
                          \&channel_cb, \&programme_cb);

=cut
# Private.
sub new_doc_callback( $$$$ ) {
    my ($enc_cb, $cred_cb, $ch_cb, $p_cb) = @_;
    t 'creating new XML::Twig';
    t '\@Channel_Handlers=' . d \@Channel_Handlers;
    t '\@Programme_Handlers=' . d \@Programme_Handlers;
    new XML::Twig(StartTagHandlers =>
		  { '/tv' => sub {
			my ($t, $node) = @_;
			my $enc;
			if ($KEEP_ENCODING) {
			    t 'KeepEncoding on, get original encoding';
			    $enc = $t->encoding();
			}
			else {
			    t 'assuming UTF-8 encoding';
			    $enc = 'UTF-8';
			}

			if (defined $enc_cb) {
			    for (ref $enc_cb) {
				if ($_ eq 'CODE') {
				    $enc_cb->($enc);
				}
				elsif ($_ eq 'SCALAR') {
				    $$enc_cb = $enc;
				}
				else {
				    die "callback should be code ref or scalar ref, or undef";
				}
			    }
			}

			if (defined $cred_cb) {
			    my $cred = get_attrs($node);
			    for (ref $cred_cb) {
				if ($_ eq 'CODE') {
				    $cred_cb->($cred);
				}
				elsif ($_ eq 'SCALAR') {
				    $$cred_cb = $cred;
				}
				else {
				    die "callback should be code ref or scalar ref, or undef";
				}
			    }
			}
			# Most of the above code can be removed in the
			# next release.
			#
		    },
		  },

		  TwigHandlers =>
		  { '/tv/channel'   => sub {
			my ($t, $node) = @_;
			die if not defined $node;
			my $c = node_to_channel($node);
			$t->purge();
			if (not $c) {
			    warn "skipping bad channel element\n";
			}
			else {
			    $ch_cb->($c);
			}
		    },
		
		    '/tv/programme' => sub {
			my ($t, $node) = @_;
			die if not defined $node;
			my $p = node_to_programme($node);
			$t->purge();
			if (not $p) {
			    warn "skipping bad programme element\n";
			}
			else {
			    $p_cb->($p);
			}
		    },
		  },

		  KeepEncoding => $KEEP_ENCODING,
		 );
}

sub parse_callback( $$$$$ ) {
    my ($str, $enc_cb, $cred_cb, $ch_cb, $p_cb) = @_;
    sanity($str);
    new_doc_callback($enc_cb, $cred_cb, $ch_cb, $p_cb)->parse($str);
}

=pod

=item parsefiles_callback(encoding_callback, credits_callback,
                          channel_callback, programme_callback,
                          filenames...)

As C<parse_callback()> but takes one or more filenames to open,
merging their contents in the same manner as C<parsefiles()>.  Note
that the reading is still gradual - you get the channels and
programmes one at a time, as they are read.

Note that the same <channel> may be present in more than one file, so
the channel callback will get called more than once.  ItE<39>s your
responsibility to weed out duplicate channel elements (since writing
them out again requires that each have a unique id).

For compatibility, there is an alias C<parsefile_callback()> which is
the same but takes only a single filename, B<before> the callback
arguments.  This is deprecated.

=cut
sub parsefile_callback( $$$$$ ) {
    my ($f, $enc_cb, $cred_cb, $ch_cb, $p_cb) = @_;
    parsefiles_callback($enc_cb, $cred_cb, $ch_cb, $p_cb, $f);
}

sub parsefiles_callback( $$$$@ ) {
    my ($enc_cb, $cred_cb, $ch_cb, $p_cb, @files) = @_;
    die "one or more files required" if not @files;
    my $all_encoding; my $have_encoding = 0;
    my $all_credits;
    my %all_channels;

    my $do_next_file; # to be defined below
    my $my_enc_cb = sub( $ ) {
	my $e = shift;
	t 'encoding callback';
	if ($have_encoding) {
	    t 'seen encoding before, just check';
	    my ($da, $de) = (defined $all_encoding, defined $e);
	    if (not $da and not $de) {
		warn "two files both have unspecified character encodings, hope they're the same\n";
	    }
	    elsif (not $da and $de) {
		warn "encoding $e not being returned to caller\n";
		$all_encoding = $e;
	    }
	    elsif ($da and not $de) {
		warn "input file with unspecified encoding, assuming same as others ($all_encoding)\n";
	    }
	    elsif ($da and $de) {
		if ($all_encoding ne $e) {
		    die "this file's encoding $e differs from others' $all_encoding - aborting\n";
		}
	    }
	    else { die }
	}
	else {
	    t 'not seen encoding before, call user';
	    $enc_cb->($e) if $enc_cb;
	    $all_encoding = $e;
	    $have_encoding = 1;
	}
    };

    my $my_cred_cb = sub( $ ) {
	my $c = shift;
	if (defined $all_credits) {
	    if (Dumper($all_credits) ne Dumper($c)) {
		warn "different files have different credits, picking one arbitrarily\n";
		# In fact, we pick the last file in the list since this is the
		# first to be opened.
		#
	    }
	}
	else {
	    $cred_cb->($c) if $cred_cb;
	    $all_credits = $c;
	}
    };

    my $my_ch_cb = sub( $ ) {
	my $c = shift;
	my $id = $c->{id};
	if (defined $all_channels{$id} and Dumper($all_channels{$id}) ne Dumper($c)) {
	    warn "differing channels with id $id, picking one arbitrarily\n";
	}
	else {
	    $all_channels{$id} = $c;
	    $ch_cb->($c) if $ch_cb;
	}
    };

    my $my_p_cb = sub( $ ) {
	$do_next_file->(); # if any
	$p_cb->(@_) if $p_cb;
    };

    $do_next_file = sub() {
	while (@files) {
	    # Last first.
	    my $f = pop @files;

	    # FIXME commonize these augmented warning messages.  Weird
	    # stuff (up to and including segfaults) happens if you
	    # call warn() or die() from these handlers.
	    #
	    local $SIG{__WARN__} = sub {
		my $msg = shift;
 		$msg = "warning: something's wrong" if not defined $msg;
		chomp $msg;
		print STDERR "$f: $msg\n";
	    };
	    local $SIG{__DIE__} = sub {
		my $msg = shift;
 		$msg = "warning: something's wrong" if not defined $msg;
		chomp $msg;
	        print STDERR "$f: $msg\n";
		exit(1);
	    };

	    my $t = new_doc_callback($my_enc_cb, $my_cred_cb, $my_ch_cb, $my_p_cb);
	    $t->parsefile($f);
	}
    };

    # Let's go.
    $do_next_file->();
}

=pod

=item write_data(data, options...)

Takes a data structure and writes it as XML to standard output.  Any
extra arguments are passed on to XML::WriterE<39>s constructor, for example

    my $f = new IO::File '>out.xml'; die if not $f;
    write_data($data, OUTPUT => $f);

The encoding used for the output is given by the first element of the
data.

Normally, there will be a warning for any Perl data which is not
understood and cannot be written as XMLTV, such as strange keys in
hashes.  But as an exception, any hash key beginning with an
underscore will be skipped over silently.  You can store 'internal use
only' data this way.

If a programme or channel hash contains a key beginning with 'debug',
this key and its value will be written out as a comment inside the
<programme> or <channel> element.  This lets you include small
debugging messages in the XML output.

=cut
sub write_data( $;@ ) {
    my $data = shift;
    my $writer = new XMLTV::Writer(encoding => $data->[0], @_);
    $writer->start($data->[1]);
    $writer->write_channels($data->[2]);
    $writer->write_programme($_) foreach @{$data->[3]};
    $writer->end();
}


# Private.
#
# get_attrs()
#
# Given a node, return a hashref of its attributes.  Skips over
# the 'x-whatever' attributes.
#
sub get_attrs( $ ) {
    my $node = shift; die if not defined $node;
    my %r = %{$node->atts()};
    foreach (keys %r) {
	if (/^x-/) {
	    delete $r{$_};
	}
	else {
	    tidy(\$r{$_});
	}
    }
    return \%r;
}


# Private.
#
# get_text()
#
# Given a node containing only text, return that text (with whitespace
# either side stripped).  If the node has no children (as in
# <foo></foo> or <foo />), this is considered to be the empty string.
#
# Parameter: whether newlines are allowed (defaults to false)
#
sub get_text( $;$ ) {
    my $node = shift;
    my $allow_nl = shift; $allow_nl = 0 if not defined $allow_nl;
    my @children = get_subelements($node);
    if (@children == 0) {
	return '';
    }
    elsif (@children == 1) {
	my $v = $children[0]->pcdata();
	t 'got pcdata: ' . d $v;
	if (not defined $v) {
	    my $name = get_name($node);
	    warn "node $name expected to contain text has other stuff\n";
	}
	else {
	    # Just hope that the encoding we got uses \n...
	    if (not $allow_nl and $v =~ tr/\n//d) {
		my $name = get_name($node);
		warn "removing newlines from content of node $name\n";
	    }
	    tidy(\$v);
	}
	t 'returning: ' . d $v;
	return $v;
    }
    elsif (@children > 1) {
	my $name = get_name($node);
	warn "node $name expected to contain text has more than one child\n";
	return undef;
    }
    else { die }
}

# Private.  Clean up parsed text.  Takes ref to scalar.
sub tidy( $ ) {
    our $v; local *v = shift; die if not defined $v;
    if ($XML::Twig::VERSION < 3.01 || $KEEP_ENCODING) {
	# Old versions of XML::Twig had stupid behaviour with
	# entities - and so do the new ones if KeepEncoding is on.
	#
	for ($v) {
	    s/&gt;/>/g;
	    s/&lt;/</g;
	    s/&apos;/\'/g;
	    s/&quot;/\"/g;
	    s/&amp;/&/g;	# needs to be last
	}
    }
    else {
	t 'new XML::Twig, not KeepEncoding, entities already dealt with';
    }

    for ($v) {
	s/^\s+//;
	s/\s+$//;

	# On Windows there seems to be an inconsistency between
	# XML::Twig and XML::Writer.  The former returns text with
	# \r\n line endings to the application, but the latter adds \r
	# characters to text outputted.  So reading some text and
	# writing it again accumulates an extra \r character.  We fix
	# this by removing \r from the input here.
	#
	tr/\r//d;
    }
}

# Private.
#
# get_subelements()
#
# Return a list of all subelements of a node.  Whitespace is
# ignored; anything else that isn't a subelement is warned about.
# Skips over elements with name 'x-whatever'.
#
sub get_subelements( $ ) {
    grep { (my $tmp = get_name($_)) !~ /^x-/ } $_[0]->children();
}

# Private.
#
# get_name()
#
# Return the element name of a node.
#
sub get_name( $ ) { $_[0]->gi() }
	
# Private.
#
# dump_node()
#
# Return some information about a node for debugging.
#
sub dump_node( $ ) {
    my $n = shift;
    # Doesn't seem to be easy way to get 'type' of node.
    my $r = 'name: ' . get_name($n) . "\n";
    for (trunc($n->text())) {
	$r .= "value: $_\n" if defined and length;
    }
    return $r;
}
# Private.  Truncate a string to a reasonable length and add '...' if
# necessary.
#
sub trunc {
    local $_ = shift;
    return undef if not defined;
    if (length > 1000) {
	return substr($_, 0, 1000) . '...';
    }
    return $_;
}

=pod

=item best_name(languages, pairs [, comparator])

The XMLTV format contains many places where human-readable text is
given an optional 'lang' attribute, to allow mixed languages.  This is
represented in Perl as a pair [ text, lang ], although the second
element may be missing or undef if the language is unknown.  When
several alernatives for an element (such as <title>) can be given, the
representation is a list of [ text, lang ] pairs.  Given such a list,
what is the best text to use?  It depends on the userE<39>s preferred
language.

This function takes a list of acceptable languages and a list of [string,
language] pairs, and finds the best one to use.  This means first finding
the appropriate language and then picking the 'best' string in that
language.

The best is normally defined as the first one found in a usable
language, since the XMLTV format puts the most canonical versions
first.  But you can pass in your own comparison function, for example
if you want to choose the shortest piece of text that is in an
acceptable language.

The acceptable languages should be a reference to a list of language
codes looking like 'ru', or like 'de_DE'.  The text pairs should be a
reference to a list of pairs [ string, language ].  (As a special case
if this list is empty or undef, that means no text is present, and the
result is undef.)  The third argument if present should be a cmp-style
function that compares two strings of text and returns 1 if the first
argument is better, -1 if the second better, 0 if theyE<39>re equally
good.

Returns: [s, l] pair, where s is the best of the strings to use and l
is its language.  This pair is 'live' - it is one of those from the
list passed in.  So you can use C<best_name()> to find the best pair
from a list and then modify the content of that pair.

(This routine depends on the C<Lingua::Preferred> module being
installed; if that module is missing then the first available
language is always chosen.)

Example:

    my $langs = [ 'de', 'fr' ]; # German or French, please

    # Say we found the following under $p->{title} for a programme $p.
    my $pairs = [ [ 'La CitE des enfants perdus', 'fr' ],
                  [ 'The City of Lost Children', 'en_US' ] ];

    my $best = best_name($langs, $pairs);
    print "chose title $best->[0]\n";

=cut
sub best_name( $$;$ ) {
    my ($wanted_langs, $pairs, $compare) = @_;
    t 'best_name() ENTRY';
    t 'wanted langs: ' . d $wanted_langs;
    t '[text,lang] pairs: ' . d $pairs;
    t 'comparison fn: ' . d $compare;
    return undef if not defined $pairs;
    my @pairs = @$pairs;

    my @avail_langs;
    my (%seen_lang, $seen_undef);
    # Collect the list of available languages.
    foreach (map { $_->[1] } @pairs) {
	if (defined) {
	    next if $seen_lang{$_}++;
	}
	else {
	    next if $seen_undef++;
	}
	push @avail_langs, $_;
    }

    my $pref_lang = which_lang($wanted_langs, \@avail_langs);

    # Gather up [text, lang] pairs which have the desired language.
    my @candidates;
    foreach (@pairs) {
	my ($text, $lang) = @$_;
	next unless ((not defined $lang)
		     or (defined $pref_lang and $lang eq $pref_lang));
	push @candidates, $_;
    }

    return undef if not @candidates;

    # If a comparison function was passed in, use it to compare the
    # text strings from the candidate pairs.
    #
    @candidates = sort { $compare->($a->[0], $b->[0]) } @candidates
      if defined $compare;

    # Pick the first candidate.  This will be the one ordered first by
    # the comparison function if given, otherwise the earliest in the
    # original list.
    #
    return $candidates[0];
}


=item list_channel_keys(), list_programme_keys()

Some users of this module may wish to enquire at runtime about which
keys a programme or channel hash can contain.  The data in the hash
comes from the attributes and subelements of the corresponding element
in the XML.  The values of attributes are simply stored as strings,
while subelements are processed with a handler which may return a
complex data structure.  These subroutines returns a hash mapping key
to handler name and multiplicity.  This lets you know what data types
can be expected under each key.  For keys which come from attributes
rather than subelements, the handler is set to 'scalar', just as for
subelements which give a simple string.  See L<"DATA STRUCTURE"> for
details on what the different handler names mean.

It is not possible to find out which keys are mandatory and which
optional, only a list of all those which might possibly be present.
An example use of these routines is the L<tv_grep(1)> program, which
creates its allowed command line arguments from the names of programme
subelements.

=cut
# Private.
sub list_keys( $$ ) {
    my %r;

    # Attributes.
    foreach (@{shift()}) {
	my ($k, $mult) = @$_;
	$r{$k} = [ 'scalar', $mult ];
    }

    # Subelements.
    foreach (@{shift()}) {
	my ($k, $h_name, $mult) = @$_;
	$r{$k} = [ $h_name, $mult ];
    }

    return \%r;
}
# Public.
sub list_channel_keys() {
    list_keys(\@Channel_Attributes, \@Channel_Handlers);
}
sub list_programme_keys() {
    list_keys(\@Programme_Attributes, \@Programme_Handlers);
}

=pod

=item catfiles(w_args, filename...)

Concatenate several listings files, writing the output to somewhere
specified by C<w_args>.  Programmes are catenated together, channels
are merged, for credits we just take the first and warn if the others
differ.

The first argument is a hash reference giving information to pass to
C<XMLTV::Writer>E<39>s constructor.  But do not specify encoding, this
will be taken from the input files.  Currently C<catfiles()> will fail
work if the input files have different encodings.

=cut
sub catfiles( $@ ) {
    my $w_args = shift;
    my $w;
    my %seen_ch;
    XMLTV::parsefiles_callback
      (sub {
	   die if defined $w;
	   $w = new XMLTV::Writer(%$w_args, encoding => shift);
       },
       sub { $w->start(shift) },
       sub {
	   my $c = shift;
	   my $id = $c->{id};
	   if (not defined $seen_ch{$id}) {
	       $w->write_channel($c);
	       $seen_ch{$id} = $c;
	   }
	   elsif (Dumper($seen_ch{$id}) eq Dumper($c)) {
	       # They're identical, okay.
	   }
	   else {
	       warn "channel $id may differ between two files, "
		 . "picking one arbitrarily\n";
	   }
       },
       sub { $w->write_programme(shift) },
       @_);
    $w->end();
}

=pod

=item cat(data, ...)

Concatenate (and merge) listings data.  Programmes are catenated
together, channels are merged, for credits we just take the first and
warn if the others differ (except that the 'date' of the result is the
latest date of all the inputs).

Whereas C<catfiles()> reads and writes files, this function takes
already-parsed listings data and returns some more listings data.  It
is much more memory-hungry.

=cut
sub cat( @ ) { cat_aux(1, @_) }

=pod

=item cat_noprogrammes

Like C<cat()> but ignores the programme data and just returns
encoding, credits and channels.  This is in case for scalability
reasons you want to handle programmes individually, but still
merge the smaller data.

=cut
sub cat_noprogrammes( @ ) { cat_aux(0, @_) }

sub cat_aux( @ ) {
    my $all_encoding;
    my ($all_credits_nodate, $all_credits_date);
    my %all_channels;
    my @all_progs;
    my $do_progs = shift;

    foreach (@_) {
	t 'doing arg: ' . d $_;
	my ($encoding, $credits, $channels, $progs) = @$_;

	if (not defined $all_encoding) {
	    $all_encoding = $encoding;
	}
	elsif ($encoding ne $all_encoding) {
	    die "different files have different encodings, cannot continue\n";
	}

	# If the credits are different between files there's not a lot
	# we can do to merge them.  Apart from 'date', that is.  There
	# we can say that the date of the concatenated listings is the
	# newest date from all the sources.
	#
 	my %credits_nodate = %$credits; # copy
 	my $d = delete $credits_nodate{date};
 	if (defined $d) {
	    # Need to 'require' rather than 'use' this because
	    # XMLTV.pm is loaded during the build process and
	    # XMLTV::Date isn't available then.  Urgh.
	    #
	    require XMLTV::Date;
 	    my $dp = XMLTV::Date::parse_date($d);
 	    for ($all_credits_date) {
 		if (not defined
		    or Date_Cmp(XMLTV::Date::parse_date($_), $dp) < 0) {
 		    $_ = $d;
 		}
 	    }
 	}
	
 	# Now in uniqueness checks ignore the date.
	if (not defined $all_credits_nodate) {
	    $all_credits_nodate = \%credits_nodate;
	}
	elsif (Dumper(\%credits_nodate) ne Dumper($all_credits_nodate)) {
	    warn "different files have different credits, taking from first file\n";
	}

	foreach (keys %$channels) {
	    if (not defined $all_channels{$_}) {
		$all_channels{$_} = $channels->{$_};
	    }
	    elsif (Dumper($all_channels{$_}) ne Dumper($channels->{$_})) {
		warn "channel $_ differs between two files, taking first appearance\n";
	    }
	}

	push @all_progs, @$progs if $do_progs;
    }

    $all_encoding = 'UTF-8' if not defined $all_encoding;

    my %all_credits;
    %all_credits = %$all_credits_nodate
      if defined $all_credits_nodate;
    $all_credits{date} = $all_credits_date
      if defined $all_credits_date;

    if ($do_progs) {
	return [ $all_encoding, \%all_credits, \%all_channels, \@all_progs ];
    }
    else {
	return [ $all_encoding, \%all_credits, \%all_channels ];
    }
}


# For each subelement of programme, we define a subroutine to read it
# and one to write it.  The reader takes an node for a single
# subelement and returns its value as a Perl scalar (warning and
# returning undef if error).  The writer takes an XML::Writer, an
# element name and a scalar value and writes a subelement for that
# value.  Note that the element name is passed in to the writer just
# for symmetry, so that neither the writer or the reader have to know
# what their element is called.
#
=pod

=back

=head1 DATA STRUCTURE

For completeness, we describe more precisely how channels and
programmes are represented in Perl.  Each element of the channels list
is a hashref corresponding to one <channel> element, and likewise for
programmes.  The possible keys of a channel (programme) hash are the
names of attributes or subelements of <channel> (<programme>).

The values for attributes are not processed in any way; an attribute
C<fred="jim"> in the XML will become a hash element with key C<'fred'>,
value C<'jim'>.

But for subelements, there is further processing needed to turn the
XML content of a subelement into Perl data.  What is done depends on
what type of data is stored under that subelement.  Also, if a certain
element can appear several times then the hash key for that element
points to a list of values rather than just one.

The conversion of a subelementE<39>s content to and from Perl data is
done by a handler.  The most common handler is I<with-lang>, used for
human-readable text content plus an optional 'lang' attribute.  There
are other handlers for other data structures in the file format.
Often two subelements will share the same handler, since they hold the
same type of data.  The handlers defined are as follows; note that
many of them will silently strip leading and trailing whitespace in
element content.  Look at the DTD itself for an explanation of the
whole file format.

Unless specified otherwise, it is not allowed for an element expected
to contain text to have empty content, nor for the text to contain
newline characters.

=over

=item I<credits>

Turns a list of credits (for director, actor, writer, etc.) into a
hash mapping 'role' to a list of names.  The names in each role are
kept in the same order.

=cut
$Handlers{credits}->[0] = sub( $ ) {
    my $node = shift;
    my @roles = qw(director actor writer adapter producer presenter
		   commentator guest);
    my %known_role; ++$known_role{$_} foreach @roles;
    my %r;
    foreach (get_subelements($node)) {
	my $role = get_name($_);
	unless ($known_role{$role}++) {
	    warn "unknown thing in credits: $role";
	    next;
	}
	push @{$r{$role}}, get_text($_);
    }
    return \%r;
};
$Handlers{credits}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_; die if not defined $v;
    my %h = %$v;
    return if not %h; # don't write empty element
    t 'writing credits: ' . d \%h;
    # TODO some 'do nothing' setting in XML::Writer to replace this
    # convention of passing undef.
    #
    $w->startTag($e) if $w;
    foreach (qw[director actor writer adapter producer presenter
		commentator guest] ) {
	next unless defined $h{$_};
	my @people = @{delete $h{$_}};
	foreach my $person (@people) {
	    die if not defined $person;
	    $w->dataElement($_, $person) if $w;
	}
    }
    warn_unknown_keys($e, \%h);
    $w->endTag($e) if $w;
};

=pod

=item I<scalar>

Reads and writes a simple string as the content of the XML element.

=cut
$Handlers{scalar}->[0] = sub( $ ) {
    my $node = shift;
    return get_text($node);
};
$Handlers{scalar}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    t 'scalar';
    $w->dataElement($e, $v) if $w;
};

=pod

=item I<length>

Converts the content of a <length> element into a number of seconds
(so <length units="minutes">5</minutes> would be returned as 300).  On
writing out again tries to convert a number of seconds to a time in
minutes or hours if that would look better.

=cut
$Handlers{length}->[0] = sub( $ ) {
    my $node = shift; die if not defined $node;
    my %attrs = %{get_attrs($node)};
    my $d = get_text($node);
    if ($d =~ /^\s*$/) {
	warn "empty 'length' element";
	return undef;
    }
    if ($d !~ tr/0-9// or $d =~ tr/0-9//c) {
	warn "bad content of 'length' element: $d";
	return undef;
    }
    my $units = $attrs{units};
    if (not defined $units) {
	warn "missing 'units' attr in 'length' element";
	return undef;
    }
    # We want to return a length in seconds.
    if ($units eq 'seconds') {
	# Okay.
    }
    elsif ($units eq 'minutes') {
	$d *= 60;
    }
    elsif ($units eq 'hours') {
	$d *= 60 * 60;
    }
    else {
	warn "bad value of 'units': $units";
	return undef;
    }
    return $d;
};
$Handlers{length}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    t 'length';
    my $units;
    if ($v % 3600 == 0) {
	$units = 'hours';
	$v /= 3600;
    }
    elsif ($v % 60 == 0) {
	$units = 'minutes';
	$v /= 60;
    }
    else {
	$units = 'seconds';
    }
    $w->dataElement($e, $v, units => $units) if $w;
};

=pod

=item I<episode-num>

The representation in Perl of XMLTVE<39>s odd episode numbers is as a
pair of [ content, system ].  As specified by the DTD, if the system is
not given in the file then 'onscreen' is assumed.  Whitespace in the
'xmltv_ns' system is unimportant, so on reading it is normalized to
a single space on either side of each dot.

=cut
$Handlers{'episode-num'}->[0] = sub( $ ) {
    my $node = shift; die if not defined $node;
    my %attrs = %{get_attrs($node)};
    my $system = $attrs{system};
    $system = 'onscreen' if not defined $system;
    my $content = get_text($node);
    if ($system eq 'xmltv_ns') {
	# Make it look nice.
	$content =~ s/\s+//g;
	$content =~ s/\./ . /g;
    }
    return [ $content, $system ];
};
$Handlers{'episode-num'}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    t 'episode number';
    if (not ref $v or ref $v ne 'ARRAY') {
	warn "not writing episode-num whose content is not an array";
	return;
    }
    my ($content, $system) = @$v;
    $system = 'onscreen' if not defined $system;
    $w->dataElement($e, $content, system => $system) if $w;
};

=pod

=item I<video>

The <video> section is converted to a hash.  The <present> subelement
corresponds to the key 'present' of this hash, 'yes' and 'no' are
converted to Booleans.  The same applies to <colour>.  The content of
the <aspect> subelement is stored under the key 'aspect'.  These keys
can be missing in the hash just as the subelements can be missing in
the XML.

=cut
$Handlers{video}->[0] = sub ( $ ) {
    my $node = shift;
    my %r;
    foreach (get_subelements($node)) {
	my $name = get_name($_);
	my $value = get_text($_);
	if ($name eq 'present') {
	    warn "'present' seen twice" if defined $r{present};
	    $r{present} = decode_boolean($value);
	}
	elsif ($name eq 'colour') {
	    warn "'colour' seen twice" if defined $r{colour};
	    $r{colour} = decode_boolean($value);
	}
	elsif ($name eq 'aspect') {
	    warn "'aspect' seen twice" if defined $r{aspect};
	    $value =~ /^\d+:\d+$/ or warn "bad aspect ratio: $value";
	    $r{aspect} = $value;
	}
	elsif ($name eq 'quality') {
	    warn "'quality' seen twice" if defined $r{quality};
	    $r{quality} = $value;
	}
    }
    return \%r;
};
$Handlers{video}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    t "'video' element";
    my %h = %$v;
    return if not %h; # don't write empty element
    $w->startTag($e) if $w;
    if (defined (my $val = delete $h{present})) {
	$w->dataElement('present', encode_boolean($val)) if $w;
    }
    if (defined (my $val = delete $h{colour})) {
	$w->dataElement('colour', encode_boolean($val)) if $w;
    }
    if (defined (my $val = delete $h{aspect})) {
	$w->dataElement('aspect', $val) if $w;
    }
    if (defined (my $val = delete $h{quality})) {
	$w->dataElement('quality', $val) if $w;
    }
    warn_unknown_keys("zz $e", \%h);
    $w->endTag($e) if $w;
};

=pod

=item I<audio>

This is similar to I<video>.  <present> is a Boolean value, while
the content of <stereo> is stored unchanged.

=cut
$Handlers{audio}->[0] = sub( $ ) {
    my $node = shift;
    my %r;
    foreach (get_subelements($node)) {
	my $name = get_name($_);
	my $value = get_text($_);
	if ($name eq 'present') {
	    warn "'present' seen twice" if defined $r{present};
	    $r{present} = decode_boolean($value);
	}
	elsif ($name eq 'stereo') {
	    warn "'stereo' seen twice" if defined $r{stereo};
	    if ($value eq '') {
		warn "empty 'stereo' element not permitted, should be <stereo>stereo</stereo>";
		$value = 'stereo';
	    }
	    warn "bad value for 'stereo': '$value'"
	      if ($value ne 'mono'
	      and $value ne 'stereo'
		  and $value ne 'surround'
		  and $value ne 'dolby digital'
		  and $value ne 'dolby');
	    $r{stereo} = $value;
	}
    }
    return \%r;
};
$Handlers{audio}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    my %h = %$v;
    return if not %h; # don't write empty element
    $w->startTag($e) if $w;
    if (defined (my $val = delete $h{present})) {
	$w->dataElement('present', encode_boolean($val)) if $w;
    }
    if (defined (my $val = delete $h{stereo})) {
	$w->dataElement('stereo', $val) if $w;
    }
    warn_unknown_keys($e, \%h);
    $w->endTag($e) if $w;
};

=pod

=item I<previously-shown>

The 'start' and 'channel' attributes are converted to keys in a hash.

=cut
$Handlers{'previously-shown'}->[0] = sub( $ ) {
    my $node = shift; die if not defined $node;
    my %attrs = %{get_attrs($node)};
    my $r = {};
    foreach (qw(start channel)) {
	my $v = delete $attrs{$_};
	$r->{$_} = $v if defined $v;
    }
    foreach (keys %attrs) {
	warn "unknown attribute $_ in previously-shown";
    }
    return $r;
};
$Handlers{'previously-shown'}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    $w->emptyTag($e, %$v) if $w;
};

=pod

=item I<presence>

The content of the element is ignored: it signfies something by its
very presence.  So the conversion from XML to Perl is a constant true
value whenever the element is found; the conversion from Perl to XML
is to write out the element if true, donE<39>t write anything if false.

=cut
$Handlers{presence}->[0] = sub( $ ) {
    my $node = shift;
    # The 'new' element is empty, it signifies newness by its very
    # presence.
    #
    return 1;
};
$Handlers{presence}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    if (not $v) {
	# Not new, so don't create an element.
    }
    else {
	$w->emptyTag($e) if $w;
    }
};

=pod

=item I<subtitles>

The 'type' attribute and the 'language' subelement (both optional)
become keys in a hash.  But see I<language> for what to pass as the
value of that element.

=cut
$Handlers{subtitles}->[0] = sub( $ ) {
    my $node = shift; die if not defined $node;
    my %attrs = %{get_attrs($node)};
    my %r;
    $r{type} = $attrs{type} if defined $attrs{type};
    foreach (get_subelements($node)) {
	my $name = get_name($_);
	if ($name eq 'language') {
	    warn "'language' seen twice" if defined $r{language};
	    $r{language} = read_with_lang($_, 0, 0);
	}
	else {
	    warn "bad content of 'subtitles' element: $name";
	}
    }
    return \%r;
};
$Handlers{subtitles}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    t 'subtitles';
    my ($type, $language) = ($v->{type}, $v->{language});
    my %attrs; $attrs{type} = $type if defined $type;
    if (defined $language) {
	$w->startTag($e, %attrs) if $w;
	write_with_lang($w, 'language', $language, 0, 0);
	$w->endTag($e) if $w;
    }
    else {
	$w->emptyTag($e, %attrs) if $w;
    }
};

=pod

=item I<rating>

The rating is represented as a tuple of [ rating, system, icons ].
The last element is itself a listref of structures returned by the
I<icon> handler.

=cut
$Handlers{rating}->[0] = sub( $ ) {
    my $node = shift; die if not defined $node;
    my %attrs = %{get_attrs($node)};
    my $system = delete $attrs{system} if exists $attrs{system};
    foreach (keys %attrs) {
	warn "unknown attribute in rating: $_";
    }
    my @children = get_subelements($node);

    # First child node is value.
    my $value_node = shift @children;
    if (not defined $value_node) {
	warn "missing 'value' element inside rating";
	return undef;
    }
    if ((my $name = get_name($value_node)) ne 'value') {
	warn "expected 'value' node inside rating, got '$name'";
	return undef;
    }

    my $rating = read_value($value_node);

    # Remaining children are icons.
    my @icons = map { read_icon($_) } @children;
	
    return [ $rating, $system, \@icons ];
};
$Handlers{rating}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    if (not ref $v or ref $v ne 'ARRAY') {
	warn "not writing rating whose content is not an array";
	return;
    }
    my ($rating, $system, $icons) = @$v;
    if (defined $system) {
	$w->startTag($e, system => $system) if $w;
    }
    else {
	$w->startTag($e) if $w;
    }

    write_value($w, 'value', $rating) if $w;
    if ($w) { write_icon($w, 'icon', $_) foreach @$icons };
    $w->endTag($e) if $w;
};

=pod

=item I<star-rating>

In XML this is a string 'X/Y' plus a list of icons.  In Perl represented
as a pair [ rating, icons ] similar to I<rating>.

Multiple star ratings are now supported. For backward compatability,
you may specify a single [rating,icon] or the preferred double array
[[rating,system,icon],[rating2,system2,icon2]] (like 'ratings')


=cut
$Handlers{'star-rating'}->[0] = sub( $ ) {
    my $node = shift;
    my %attrs = %{get_attrs($node)};
    my $system = delete $attrs{system} if exists $attrs{system};
    my @children = get_subelements($node);

    # First child node is value.
    my $value_node = shift @children;
    if (not defined $value_node) {
	warn "missing 'value' element inside star-rating";
	return undef;
    }
    if ((my $name = get_name($value_node)) ne 'value') {
	warn "expected 'value' node inside star-rating, got '$name'";
	return undef;
    }
    my $rating = read_value($value_node);

    # Remaining children are icons.
    my @icons = map { read_icon($_) } @children;
	
    return [ $rating, $system, \@icons ];
};
$Handlers{'star-rating'}->[1] = sub ( $$$ ) {
    my ($w, $e, $v) = @_;
#
# 10/31/2007 star-rating can now have multiple values (and system=)
# let's make it so old code still works!
#
    if (not ref $v or ref $v ne 'ARRAY') {
	   $v=[$v];
#	   warn "not writing star-rating whose content is not an array";
#	return;
    }
    my ($rating, $system, $icons) = @$v;
    if (defined $system) {
	$w->startTag($e, system => $system) if $w;
    }
    else {
	$w->startTag($e) if $w;
    }
    write_value($w, 'value', $rating) if $w;
    if ($w) { write_icon($w, 'icon', $_) foreach @$icons };
    $w->endTag($e) if $w;
};

=pod

=item I<icon>

An icon in XMLTV files is like the <img> element in HTML.  It is
represented in Perl as a hashref with 'src' and optionally 'width'
and 'height' keys.

=cut
sub write_icon( $$$ ) {
    my ($w, $e, $v) = @_;
    croak "no 'src' attribute for icon\n" if not defined $v->{src};
    croak "bad width $v->{width} for icon\n"
      if defined $v->{width} and $v->{width} !~ /^\d+$/;
    croak "bad height $v->{height} for icon\n"
      if defined $v->{height} and $v->{height} !~ /^\d+$/;

    foreach (keys %$v) {
	warn "unrecognized key in icon: $_\n"
	  if $_ ne 'src' and $_ ne 'width' and $_ ne 'height';
    }

    $w->emptyTag($e, %$v);
}
sub read_icon( $ ) {
    my $node = shift; die if not defined $node;
    my %attrs = %{get_attrs($node)};
    warn "missing 'src' attribute in icon" if not defined $attrs{src};
    return \%attrs;
}
$Handlers{icon}->[0] = \&read_icon;
$Handlers{icon}->[1] = sub( $$$ ) {
    my ($w, $e, $v) = @_;
    write_icon($w, $e, $v) if $w;
};

# To keep things tidy some elements that can have icons store their
# textual content inside a subelement called 'value'.  These two
# routines are a bit trivial but they're here for consistency.
#
sub read_value( $ ) {
    my $value_node = shift;
    my $v = get_text($value_node);
    if (not defined $v or $v eq '') {
	warn "no content of 'value' element";
	return undef;
    }
    return $v;
}
sub write_value( $$$ ) {
    my ($w, $e, $v) = @_;
    $w->dataElement($e, $v) if $w;
};


# Booleans in XMLTV files are 'yes' or 'no'.
sub decode_boolean( $ ) {
    my $value = shift;
    if ($value eq 'no') {
	return 0;
    }
    elsif ($value eq 'yes') {
	return 1;
    }
    else {
	warn "bad boolean: $value";
	return undef;
    }
}
sub encode_boolean( $ ) {
    my $v = shift;
    warn "expected a Perl boolean like 0 or 1, not '$v'\n"
      if $v and $v != 1;
    return $v ? 'yes' : 'no';
}


=pod

=item I<with-lang>

In XML something like title can be either <title>Foo</title>
or <title lang="en">Foo</title>.  In Perl these are stored as
[ 'Foo' ] and [ 'Foo', 'en' ].  For the former [ 'Foo', undef ]
would also be okay.

This handler also has two modifiers which may be added to the name
after '/'.  I</e> means that empty text is allowed, and will be
returned as the empty tuple [], to mean that the element is present
but has no text.  When writing with I</e>, undef will also be
understood as present-but-empty.  You cannot however specify a
language if the text is empty.

The modifier I</m> means that the text is allowed to span multiple
lines.

So for example I<with-lang/em> is a handler for text with language,
where the text may be empty and may contain newlines.  Note that the
I<with-lang-or-empty> of earlier releases has been replaced by
I<with-lang/e>.

=cut
sub read_with_lang( $$$ ) {
    my ($node, $allow_empty, $allow_nl) = @_;
    die if not defined $node;
    my %attrs = %{get_attrs($node)};
    my $lang = $attrs{lang} if exists $attrs{lang};
    my $value = get_text($node, $allow_nl);
    if (not length $value) {
	if (not $allow_empty) {
	    warn 'empty string for with-lang value';
	    return undef;
	}
	warn 'empty string may not have language' if defined $lang;
	return [];
    }
    if (defined $lang) {
	return [ $value, $lang ];
    }
    else {
	return [ $value ];
    }
}
$Handlers{'with-lang'}->[0]    = sub( $ ) { read_with_lang($_[0], 0, 0) };
$Handlers{'with-lang/'}->[0]   = sub( $ ) { read_with_lang($_[0], 0, 0) };
$Handlers{'with-lang/e'}->[0]  = sub( $ ) { read_with_lang($_[0], 1, 0) };
$Handlers{'with-lang/m'}->[0]  = sub( $ ) { read_with_lang($_[0], 0, 1) };
$Handlers{'with-lang/em'}->[0] = sub( $ ) { read_with_lang($_[0], 1, 1) };
$Handlers{'with-lang/me'}->[0] = sub( $ ) { read_with_lang($_[0], 1, 1) };

sub write_with_lang( $$$$$ ) {
    my ($w, $e, $v, $allow_empty, $allow_nl) = @_;
    if (not ref $v or ref $v ne 'ARRAY') {
	warn "not writing with-lang whose content is not an array";
	return;
    }

    if (not @$v) {
	if (not $allow_empty) {
	    warn "not writing no content for $e";
	    return;
	}
	$v = [ '' ];
    }

    my ($text, $lang) = @$v;
    t 'writing character data: ' . d $text;
    if (not defined $text) {
	warn "not writing undefined value for $e";
	return;
    }

#
# strip whitespace silently.
# we used to use a warn, but later on the code catches this and drops the record
#
    my $old_text = $text;
    $text =~ s/^\s+//;
    $text =~ s/\s+$//;  

    if (not length $text) {
	if (not $allow_empty) {
	    warn "not writing empty content for $e";
	    return;
	}
	if (defined $lang) {
	    warn "not writing empty content with language for $e";
	    return;
	}
	$w->emptyTag($e) if $w;
	return;
    }

    if (not $allow_nl and $text =~ tr/\n//) {
	warn "not writing text containing newlines for $e";
	return;
    }

    if (defined $lang) {
	$w->dataElement($e, $text, lang => $lang) if $w;
    }
    else {
	$w->dataElement($e, $text) if $w;
    }
}
$Handlers{'with-lang'}->[1]    = sub( $$$ ) { write_with_lang($_[0], $_[1], $_[2], 0, 0) };
$Handlers{'with-lang/'}->[1]   = sub( $$$ ) { write_with_lang($_[0], $_[1], $_[2], 0, 0) };
$Handlers{'with-lang/e'}->[1]  = sub( $$$ ) { write_with_lang($_[0], $_[1], $_[2], 1, 0) };
$Handlers{'with-lang/m'}->[1]  = sub( $$$ ) { write_with_lang($_[0], $_[1], $_[2], 0, 1) };
$Handlers{'with-lang/em'}->[1] = sub( $$$ ) { write_with_lang($_[0], $_[1], $_[2], 1, 1) };
$Handlers{'with-lang/me'}->[1] = sub( $$$ ) { write_with_lang($_[0], $_[1], $_[2], 1, 1) };

# Sanity check.
foreach (keys %Handlers) {
    my $v = $Handlers{$_};
    if (@$v != 2
        or ref($v->[0]) ne 'CODE'
        or ref($v->[1]) ne 'CODE') {
        die "bad handler pair for $_\n";
    }
}

=pod

=back

Now, which handlers are used for which subelements (keys) of channels
and programmes?  And what is the multiplicity (should you expect a
single value or a list of values)?

The following tables map subelements of <channel> and of <programme>
to the handlers used to read and write them.  Many elements have their
own handler with the same name, and most of the others use
I<with-lang>.  The third column specifies the multiplicity of the
element: B<*> (any number) will give a list of values in Perl, B<+>
(one or more) will give a nonempty list, B<?> (maybe one) will give a
scalar, and B<1> (exactly one) will give a scalar which is not undef.

=head2 Handlers for <channel>


=over

=item display-name, I<with-lang>, B<+>

=item icon, I<icon>, B<*>

=item url, I<scalar>, B<*>


=back

=head2 Handlers for <programme>


=over

=item title, I<with-lang>, B<+>

=item sub-title, I<with-lang>, B<*>

=item desc, I<with-lang/m>, B<*>

=item credits, I<credits>, B<?>

=item date, I<scalar>, B<?>

=item category, I<with-lang>, B<*>

=item language, I<with-lang>, B<?>

=item orig-language, I<with-lang>, B<?>

=item length, I<length>, B<?>

=item icon, I<icon>, B<*>

=item url, I<scalar>, B<*>

=item country, I<with-lang>, B<*>

=item episode-num, I<episode-num>, B<*>

=item video, I<video>, B<?>

=item audio, I<audio>, B<?>

=item previously-shown, I<previously-shown>, B<?>

=item premiere, I<with-lang/em>, B<?>

=item last-chance, I<with-lang/em>, B<?>

=item new, I<presence>, B<?>

=item subtitles, I<subtitles>, B<*>

=item rating, I<rating>, B<*>

=item star-rating, I<star-rating>, B<*>


=back

At present, no parsing or validation on dates is done because dates
may be partially specified in XMLTV.  For example '2001' means that
the year is known but not the month, day or time of day.  Maybe in the
future dates will be automatically converted to and from
B<Date::Manip> objects.  For now they just use the I<scalar> handler.
Similar remarks apply to URLs.

=cut
# Private.
sub node_to_programme( $ ) {
    my $node = shift; die if not defined $node;
    my %programme;

    # Attributes of programme element.
    %programme = %{get_attrs($node)};
    t 'attributes: ' . d \%programme;

    # Check the required attributes are there.  As with most checking,
    # this isn't an alternative to using a validator but it does save
    # some headscratching during debugging.
    #
    foreach (qw(start channel)) {
	if (not defined $programme{$_}) {
	    warn "programme missing '$_' attribute\n";
	    return undef;
	}
    }
    my @known_attrs = map { $_->[0] } @Programme_Attributes;
    my %ka; ++$ka{$_} foreach @known_attrs;
    foreach (keys %programme) {
	unless ($ka{$_}) {
	    warn "deleting unknown attribute '$_'";
	    delete $programme{$_};
	}
    }

    call_handlers_read($node, \@Programme_Handlers, \%programme);
    return \%programme;
}


# Private.
sub node_to_channel( $ ) {
    my $node = shift; die if not defined $node;
    my %channel;
    t 'node_to_channel() ENTRY';

    %channel = %{get_attrs($node)};
    t 'attributes: ' . d \%channel;
    if (not defined $channel{id}) {
	warn "channel missing 'id' attribute\n";
    }
    foreach (keys %channel) {
	unless (/^_/ or $_ eq 'id') {
	    warn "deleting unknown attribute '$_'";
	    delete $channel{$_};
	}
    }
		
    t '\@Channel_Handlers=' . d \@Channel_Handlers;
    call_handlers_read($node, \@Channel_Handlers, \%channel);
    return \%channel;
}



# Private.
#
# call_handlers_read()
#
# Read the subelements of a node according to a list giving a
# handler subroutine for each subelement.
#
# Parameters:
#   node
#   Reference to list of handlers: tuples of
#     [element-name, handler-name, multiplicity]
#   Reference to hash for storing results
#
# Warns if errors, but attempts to contine.
#
sub call_handlers_read( $$$ ) {
    my ($node, $handlers, $r) = @_;
    t 'call_handlers_read() using handlers: ' . d $handlers;

    die unless ref($r) eq 'HASH';
    our %r; local *r = $r;
    t 'going through each child of node';

    # Current position in handlers.  We expect to read the subelements
    # in the correct order as specified by the DTD.
    #
    my $handler_pos = 0;

    SUBELEMENT: foreach (get_subelements($node)) {
	t 'doing subelement';
	my $name = get_name($_);
	t "tag name: $name";

	# Search for a handler - from $handler_pos onwards.  But
	# first, just warn if somebody is trying to use an element in
	# the wrong place (trying to go backwards in the list).
	#
	my $found_pos;
	foreach my $i (0 .. $handler_pos - 1) {
	    if ($name eq $handlers->[$i]->[0]) {
		warn "element $name not expected here";
		next SUBELEMENT;
	    }
	}
	for (my $i = $handler_pos; $i < @$handlers; $i++) {
	    if ($handlers->[$i]->[0] eq $name) {
		t 'found handler';
		$found_pos = $i;
		last;
	    }
	    else {
		t "doesn't match name $handlers->[$i]->[0]";
		my ($handler_name, $h, $multiplicity)
		  = @{$handlers->[$i]};
		die if not defined $handler_name;
		die if $handler_name eq '';

		# Before we skip over this element, check that we got
		# the necessary values for it.
		#
		if ($multiplicity eq '?') {
		    # Don't need to check whether this set.
		}
		elsif ($multiplicity eq '1') {
		    if (not defined $r{$handler_name}) {
			warn "no element $handler_name found";
		    }
		}
		elsif ($multiplicity eq '*') {
		    # It's okay if nothing was ever set.  We don't
		    # insist on putting in an empty list.
		    #
		}
		elsif ($multiplicity eq '+') {
		    if (not defined $r{$handler_name}) {
			warn "no element $handler_name found";
		    }
		    elsif (not @{$r{$handler_name}}) {
			warn "strangely, empty list for $handler_name";
		    }
		}
		else {
		    warn "bad value of $multiplicity: $!";
		}
	    }
	}
	if (not defined $found_pos) {
	    warn "unknown element $name";
	    next;
	}
	# Next time we begin searching from this position.
	$handler_pos = $found_pos;

	# Call the handler.
	t 'calling handler';
	my ($handler_name, $h_name, $multiplicity)
	  = @{$handlers->[$found_pos]};
	die if $handler_name ne $name;
	my $h = $Handlers{$h_name}; die "no handler $h_name" if not $h;
	my $result = $h->[0]->($_); # call reader sub
	t 'result: ' . d $result;
	warn("skipping bad $name\n"), next if not defined $result;

	# Now set the value.  We can't do multiplicity checking yet
	# because there might be more elements of this type still to
	# come.
	#
	if ($multiplicity eq '?' or $multiplicity eq '1') {
	    warn "seen $name twice"
	      if defined $r{$name};
	    $r{$name} = $result;
	}
	elsif ($multiplicity eq '*' or $multiplicity eq '+') {
	    push @{$r{$name}}, $result;
	}
	else {
	    warn "bad multiplicity: $multiplicity";
	}
    }
}

sub warn_unknown_keys( $$ ) {
    my $elem_name = shift;
    our %k; local *k = shift;
    foreach (keys %k) {
        /^_/
          or $warned_unknown_key{$elem_name}->{$_}++
          or warn "unknown key $_ in $elem_name hash\n";
    }
}

package XMLTV::Writer;
use base 'XML::Writer';
use Carp;

use Date::Manip qw/UnixDate DateCalc/;

# Use Log::TraceMessages if installed.
BEGIN {
    eval { require Log::TraceMessages };
    if ($@) {
	*t = sub {};
	*d = sub { '' };
    }
    else {
	*t = \&Log::TraceMessages::t;
	*d = \&Log::TraceMessages::d;
    }
}

BEGIN {
  Date::Manip::Date_Init("TZ=UTC");
}

# Override dataElement() to refuse writing empty or whitespace
# elements.
#
sub dataElement( $$$@ ) {
    my ($self, $elem, $content, @rest) = @_;
    if ($content !~ /\S/) {
        warn "not writing empty content for $elem";
        return;
    }
    return $self->SUPER::dataElement($elem, $content, @rest);
}

=pod

=head1 WRITING

When reading a file you have the choice of using C<parse()> to gulp
the whole file and return a data structure, or using
C<parse_callback()> to get the programmes one at a time, although
channels and other data are still read all at once.

There is a similar choice when writing data: the C<write_data()>
routine prints a whole XMLTV document at once, but if you want to
write an XMLTV document incrementally you can manually create an
C<XMLTV::Writer> object and call methods on it.  Synopsis:

  use XMLTV;
  my $w = new XMLTV::Writer();
  $w->comment("Hello from XML::Writer's comment() method");
  $w->start({ 'generator-info-name' => 'Example code in pod' });
  my %ch = (id => 'test-channel', 'display-name' => [ [ 'Test', 'en' ] ]);
  $w->write_channel(\%ch);
  my %prog = (channel => 'test-channel', start => '200203161500',
	      title => [ [ 'News', 'en' ] ]);
  $w->write_programme(\%prog);
  $w->end();

XMLTV::Writer inherits from XML::Writer, and provides the following extra
or overridden methods:

=over

=item new(), the constructor

Creates an XMLTV::Writer object and starts writing an XMLTV file, printing
the DOCTYPE line.  Arguments are passed on to XML::WriterE<39>s constructor,
except for the following:

the 'encoding' key if present gives the XML character encoding.
For example:

  my $w = new XMLTV::Writer(encoding => 'ISO-8859-1');

If encoding is not specified, XML::WriterE<39>s default is used
(currently UTF-8).

XMLTW::Writer can also filter out specific days from the data. This is
useful if the datasource provides data for periods of time that does not
match the days that the user has asked for. The filtering is controlled
with the days, offset and cutoff arguments:

  my $w = new XMLTV::Writer(
      offset => 1,
      days => 2,
      cutoff => "050000" );

In this example, XMLTV::Writer will discard all entries that do not have
starttimes larger than or equal to 05:00 tomorrow and less than 05:00
two days after tomorrow. The time offset is stripped off the starttime before
the comparison is made.

=cut
sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my %args = @_;
    croak 'OUTPUT requires a filehandle, not a filename or anything else'
      if exists $args{OUTPUT} and not ref $args{OUTPUT};
    my $encoding = delete $args{encoding};
    my $days = delete $args{days};
    my $offset = delete $args{offset};
    my $cutoff = delete $args{cutoff};

    my $self = $class->SUPER::new(DATA_MODE => 1, DATA_INDENT => 2, %args);
    bless($self, $class);

    if (defined $encoding) {
	$self->xmlDecl($encoding);
    }
    else {
	# XML::Writer puts in 'encoding="UTF-8"' even if you don't ask
	# for it.
	#
	warn "assuming default UTF-8 encoding for output\n";
	$self->xmlDecl();
    }

#    $Log::TraceMessages::On = 1;
    $self->{mintime} = "19700101000000"; 	 
    $self->{maxtime} = "29991231235959"; 	 
    

    if (defined( $days ) and defined( $offset ) and defined( $cutoff )) {
      $self->{mintime} = UnixDate( 
          DateCalc( "today", "+" . $offset . " days" ),
          "%Y%m%d") . $cutoff;
      t "using mintime $self->{mintime}";

      $self->{maxtime} = UnixDate( 
          DateCalc("today", "+" . $offset+$days . " days"), 	 
          "%Y%m%d" ) . $cutoff;
      t "using maxtime $self->{maxtime}";
    }
    elsif (defined( $days ) or defined( $offset ) or defined($cutoff)) {
      croak 'You must specify days, offset and cutoff or none of them';
    }

    {
	local $^W = 0; $self->doctype('tv', undef, 'xmltv.dtd');
    }
    $self->{xmltv_writer_state} = 'new';
    return $self;
}

=pod

=item start()

Write the start of the <tv> element.  Parameter is a hashref which gives
the attributes of this element.

=cut
sub start {
    my $self = shift;
    die 'usage: XMLTV::Writer->start(hashref of attrs)' if @_ != 1;
    my $attrs = shift;

    for ($self->{xmltv_writer_state}) {
	if ($_ eq 'new') {
	    # Okay.
	}
	elsif ($_ eq 'channels' or $_ eq 'programmes') {
	    croak 'cannot call start() more than once on XMLTV::Writer';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Writer';
	}
	else { die }

	$_ = 'channels';
    }
    $self->startTag('tv', order_attrs(%{$attrs}));
}

=pod

=item write_channels()

Write several channels at once.  Parameter is a reference to a hash
mapping channel id to channel details.  They will be written sorted
by id, which is reasonable since the order of channels in an XMLTV
file isnE<39>t significant.

=cut
sub write_channels {
    my ($self, $channels) = @_;
    t('write_channels(' . d($self) . ', ' . d($channels) . ') ENTRY');
    croak 'expected hashref of channels' if ref $channels ne 'HASH';

    for ($self->{xmltv_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Writer first';
	}
	elsif ($_ eq 'channels') {
	    # Okay.
	}
	elsif ($_ eq 'programmes') {
	    croak 'cannot write channels after writing programmes';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Writer';
	}
	else { die }
    }

    my @ids = sort keys %$channels;
    t 'sorted list of channel ids: ' . d \@ids;
    foreach (@ids) {
	t "writing channel with id $_";
	my $ch = $channels->{$_};
	$self->write_channel($ch);
    }
    t('write_channels() EXIT');
}

=pod

=item write_channel()

Write a single channel.  You can call this routine if you want, but
most of the time C<write_channels()> is a better interface.

=cut
sub write_channel {
    my ($self, $ch) = @_;
    croak 'undef channel hash passed' if not defined $ch;
    croak "expected a hashref, got: $ch" if ref $ch ne 'HASH';

    for ($self->{xmltv_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Writer first';
	}
	elsif ($_ eq 'channels') {
	    # Okay.
	}
	elsif ($_ eq 'programmes') {
	    croak 'cannot write channels after writing programmes';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Writer';
	}
	else { die }
    }

    my %ch = %$ch; # make a copy
    my $id = delete $ch{id};
    die "no 'id' attribute in channel" if not defined $id;
    write_element_with_handlers($self, 'channel', { id => $id },
				\@XMLTV::Channel_Handlers, \%ch);
}

=pod

=item write_programme()

Write details for a single programme as XML.

=cut
sub write_programme {
    my $self = shift;
    die 'usage: XMLTV::Writer->write_programme(programme hash)' if @_ != 1;
    my $ref = shift;
    croak 'write_programme() expects programme hashref'
      if ref $ref ne 'HASH';
    t('write_programme(' . d($self) . ', ' . d($ref) . ') ENTRY');

    for ($self->{xmltv_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Writer first';
	}
	elsif ($_ eq 'channels') {
	    $_ = 'programmes';
	}
	elsif ($_ eq 'programmes') {
	    # Okay.
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Writer';
	}
	else { die }
    }

    # We make a copy of the programme hash and delete elements from it
    # as they are dealt with; then we can easily spot any unhandled
    # elements at the end.
    #
    my %p = %$ref;

    # First deal with those hash keys that refer to metadata on when
    # the programme is broadcast.  After taking those out of the hash,
    # we can use the handlers to output individual details.
    #
    my %attrs;
    die if not @XMLTV::Programme_Attributes;
    foreach (@XMLTV::Programme_Attributes) {
	my ($name, $mult) = @$_;
	t "looking for key $name";
	my $val = delete $p{$name};
	if ($mult eq '?') {
	    # No need to check anything.
	}
	elsif ($mult eq '1') {
	    if (not defined $val) {
		warn "programme hash missing $name key, skipping";
		return;
	    }
	}
	else { die "bad multiplicity for attribute: $mult" }
	$attrs{$name} = $val if defined $val;
    }

    # We use string comparisons without timeoffsets for comparing times.
    my( $start ) = split( /\s+/, $attrs{start} );
    if( $start lt $self->{mintime} or
        $start ge $self->{maxtime} ) {
      t "skipping programme with start $attrs{start}";
      return;
    }

    t "beginning 'programme' element";
    write_element_with_handlers($self, 'programme', \%attrs,
				\@XMLTV::Programme_Handlers, \%p);
}

=pod

=item end()

Say youE<39>ve finished writing programmes.  This ends the <tv> element
and the file.

=cut
sub end {
    my $self = shift;

    for ($self->{xmltv_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Writer first';
	}
	elsif ($_ eq 'channels' or $_ eq 'programmes') {
	    $_ = 'end';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Writer';
	}
	else { die }
    }

    $self->endTag('tv');
    $self->SUPER::end(@_);
}


# Private.
# order_attrs()
#
# In XML the order of attributes is not significant.  But to make
# things look nice we try to output them in the same order as given in
# the DTD.
#
# Takes a list of (key, value, key, value, ...) and returns one with
# keys in a nice-looking order.
#
sub order_attrs {
    die "expected even number of elements, from a hash"
      if @_ % 2;
    my @a = ((map { $_->[0] } (@XMLTV::Channel_Attributes,
			       @XMLTV::Programme_Attributes)),
	     qw(date source-info-url source-info-name source-data-url
		generator-info-name generator-info-url));

    my @r;
    my %in = @_;
    foreach (@a) {
	if (exists $in{$_}) {
	    my $v = delete $in{$_};
	    push @r, $_, $v;
	}
    }

    foreach (sort keys %in) {
	warn "unknown attribute $_" unless /^_/;
	push @r, $_, $in{$_};
    }

    return @r;
}


# Private.
#
# Writes the elements of a hash to an XMLTV::Writer using a list of
# handlers.  Deletes keys (modifying the hash passed in) as they are
# written.
#
# Requires all mandatory keys be present in the hash - if you're not
# sure then use check_multiplicity() first.
#
# Returns true if the element was successfully written, or if any
# errors found don't look serious enough to cause bad XML.  If the
# XML::Writer object passed in is undef then nothing is written (since
# the write handlers are coded like that.)
#
sub call_handlers_write( $$$ ) {
    my ($self, $handlers, $input) = @_;
    t 'writing input hash: ' . d $input;
    die if not defined $input;

    my $bad = 0;
    foreach (@$handlers) {
	my ($name, $h_name, $multiplicity) = @$_;
	my $h = $XMLTV::Handlers{$h_name}; die "no handler $h_name" if not $h;
	my $writer = $h->[1]; die if not defined $writer;
	t "doing handler for $name$multiplicity";
	local $SIG{__WARN__} = sub {
	    warn "$name element: $_[0]";
	    $bad = 1;
	};
	my $val = delete $input->{$name};
	t 'got value(s): ' . d $val;
	if ($multiplicity eq '1') {
	    $writer->($self, $name, $val);
	}
	elsif ($multiplicity eq '?') {
	    $writer->($self, $name, $val) if defined $val;
	}
	elsif ($multiplicity eq '*' or $multiplicity eq '+') {
	    croak "value for key $name should be an array ref"
	      if defined $val and ref $val ne 'ARRAY';
	    foreach (@{$val}) {
		t 'writing value: ' . d $_;
		$writer->($self, $name, $_);
		t 'finished writing multiple values';
	    }
	}
	else {
	    warn "bad multiplicity specifier: $multiplicity";
	}
    }
    t 'leftover keys: ' . d([ sort keys %$input ]);
    return not $bad;
}


# Private.
#
# Warns about missing keys that are supposed to be mandatory.  Returns
# true iff everything is okay.
#
sub check_multiplicity( $$ ) {
    my ($handlers, $input) = @_;
    foreach (@$handlers) {
	my ($name, $h_name, $multiplicity) = @$_;
	t "checking handler for $name: $h_name with multiplicity $multiplicity";
	if ($multiplicity eq '1') {
	    if (not defined $input->{$name}) {
		warn "hash missing value for $name";
		return 0;
	    }
	}
	elsif ($multiplicity eq '?') {
	    # Okay if not present.
	}
	elsif ($multiplicity eq '*') {
	    # Not present, or undef, is treated as empty list.
	}
	elsif ($multiplicity eq '+') {
	    t 'one or more, checking for a listref with no undef values';
	    my $val = $input->{$name};
	    if (not defined $val) {
		warn "hash missing value for $name (expected list)";
		return 0;
	    }
	    if (ref($val) ne 'ARRAY') {
		die "hash has bad contents for $name (expected list)";
		return 0;
	    }

	    t 'all values: ' . d $val;
            my @new_val = grep { defined } @$val;
	    t 'values that are defined: ' . d \@new_val;
            if (@new_val != @$val) {
                warn "hash had some undef elements in list for $name, removed";
                @$val = @new_val;
            }

	    if (not @$val) {
		warn "hash has empty list of $name properties (expected at least one)";
		return 0;
	    }
	}
	else {
	    warn "bad multiplicity specifier: $multiplicity";
	}
    }
    return 1;
}


# Private.
#
# Write a complete element with attributes, and subelements written
# using call_handlers_write().  The advantage over doing it by hand is
# that if some required keys are missing, nothing is written (rather
# than an incomplete and invalid element).
#
sub write_element_with_handlers( $$$$$ ) {
    my ($w, $name, $attrs, $handlers, $hash) = @_;
    if (not check_multiplicity($handlers, $hash)) {
        warn "keys missing in $name hash, not writing";
        return;
    }

    # Special 'debug' keys written as comments inside the element.
    my %debug_keys;
    foreach (grep /^debug/, keys %$hash) {
	$debug_keys{$_} = delete $hash->{$_};
    }

    # Call all the handlers with no writer object and make sure
    # they're happy.
    #
    if (not call_handlers_write(undef, $handlers, { %$hash })) {
	warn "bad data inside $name element, not writing\n";
	return;
    }

    $w->startTag($name, order_attrs(%$attrs));
    foreach (sort keys %debug_keys) {
	my $val = $debug_keys{$_};
	$w->comment((defined $val) ? "$_: $val" : $_);
    }
    call_handlers_write($w, $handlers, $hash);
    XMLTV::warn_unknown_keys($name, $hash);
    $w->endTag($name);
}

=pod

=back

=head1 AUTHOR

Ed Avis, ed@membled.com

=head1 SEE ALSO

The file format is defined by the DTD xmltv.dtd, which is included in
the xmltv package along with this module.  It should be installed in
your systemE<39>s standard place for SGML and XML DTDs.

The xmltv package has a web page at
<http://membled.com/work/apps/xmltv/> which carries
information about the file format and the various tools and apps which
are distributed with this module.

=cut
1;
