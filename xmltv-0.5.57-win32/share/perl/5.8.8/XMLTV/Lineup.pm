# Initial attempt to implement xmltv-lineups support into XMLTV
# See http://xmltv.org/wiki/lineupproposal.html for more details
# Initial version is being developed to support DVB/ATSC lineups
#
# $Id: Lineup.pm.in,v 1.4 2010/03/31 06:27:42 rmeden Exp $

package XMLTV::Lineup;

use strict;
use base 'Exporter';
our @EXPORT = ();
our @EXPORT_OK = qw(write_data best_name list_channel_keys);

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

use XML::Twig;
use XML::Writer 0.600;
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
   [ 'display-name',     'with-lang', '+' ],
   [ 'icon',             'icon',      '*' ],
   [ 'homepage-url',     'scalar',    '?' ],
   [ 'old-id',           'scalar',    '*' ],
   [ 'preferred-preset', 'scalar',    '?' ],
   [ 'service-id',       'scalar',    '?' ],
   [ 'transport-id',     'scalar',    '?' ],
   [ 'network-id',       'scalar',    '?' ],
   [ 'freq-number',      'scalar',    '?' ],
   [ 'freq-hertz',       'scalar',    '?' ],
   [ 'iptv-addr',        'scalar',    '?' ],
   [ 'multicast-addr',   'scalar',    '?' ],
   [ 'multicast-port',   'scalar',    '?' ],
   [ 'preset',           'scalar',    '?' ],
  );

# And a hash mapping names like 'with-lang' to pairs of subs.  The
# first for reading, the second for writing.  Note that the writers
# alter the passed-in data as a side effect!  (If the writing sub is
# called with an undef XML::Writer then it writes nothing but still
# warns for (most) bad data checks - and still alters the data.)
#
our %Handlers = ();

=head1 NAME

XMLTV::Lineup - Perl extension to read and write TV lineup information in XMLTV lineup format

=over

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

If a display name or channel hash contains a key beginning with 'debug',
this key and its value will be written out as a comment inside the
<display-name> or <channel> element.  This lets you include small
debugging messages in the XML output.

=cut

sub write_data( $;@ ) {
    my $data = shift;
    my $writer = new XMLTV::Lineup::Writer(encoding => $data->[0], @_);
    $writer->start($data->[1]);
    $writer->write_display_name($data->[2]);
    $writer->write_channels($data->[3]);
    $writer->end();
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

    # Say we found the following under $ch->{display-name} for a channel $ch.
    my $pairs = [ [ 'BBC Trois', 'fr' ],
                  [ 'BBC One', 'en_US' ] ];

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

=pod

=item list_display_name_keys(), list_channel_keys()

Some users of this module may wish to enquire at runtime about which
keys a programme or channel hash can contain.  The data in the hash
comes from the attributes and subelements of the corresponding element
in the XML.  The values of attributes are simply stored as strings,
while subelements are processed with a handler which may return a
complex data structure.  These subroutines returns a hash mapping key
to handler name and multiplicity.  This lets you know what data types
can be expected under each key.  For keys which come from attributes
rather than subelements, the handler is set to 'scalar', just as for
subelements which give a simple string.

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

Now, which handlers are used for which subelements (keys) of display names
and channels?  And what is the multiplicity (should you expect a single value 
or a list of values)?

The following tables map subelements of <display-name> and of <channel>
to the handlers used to read and write them.  Many elements have their
own handler with the same name, and most of the others use
I<with-lang>.  The third column specifies the multiplicity of the
element: B<*> (any number) will give a list of values in Perl, B<+>
(one or more) will give a nonempty list, B<?> (maybe one) will give a
scalar, and B<1> (exactly one) will give a scalar which is not undef.

=back

=head2 Handlers for <channel>


=over

=item display-name, I<with-lang>, B<+>

=item icon, I<icon>, B<*>

=item homepage-url, I<scalar>, B<?>

=item old-id, I<scalar>, B<*>

=item preferred-preset, I<scalar>, B<?>

=item service-id, I<scalar>, B<?>

=item transport-id, I<scalar>, B<?>

=item network-id, I<scalar>, B<?>

=item freq-number, I<scalar>, B<?>

=item freq-hertz, I<scalar>, B<?>

=item iptv-addr, I<scalar>, B<?>

=item multicast-addr, I<scalar>, B<?>

=item multicast-port, I<scalar>, B<?>

=item preset, I<scalar>, B<?>


=back

=cut

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
    # in the correct order as specified by the xmltv-lineup DTD.
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


package XMLTV::Lineup::Writer;
use base 'XML::Writer';
use Carp;

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

When writing data: the C<write_data()> routine prints a whole XMLTV::Lineup
document at once, but if you want to write a XMLTV::Lineup document incrementally 
you can manually create an C<XMLTV::Lineup::Writer> object and call methods on it.
Synopsis:

  use XMLTV::Lineup;
  my $w = new XMLTV::Lineup::Writer();
  $w->comment("Hello from XML::Writer's comment() method");
  $w->start({ 'generator-info-name' => 'Example code in pod' });
  my %ch = (id => 'test-channel', 'display-name' => [ [ 'Test', 'en' ] ]);
  $w->write_channel(\%ch);
  $w->end();

XMLTV::Lineup::Writer inherits from XML::Writer, and provides the following extra
or overridden methods:

=over

=item new(), the constructor

Creates an XMLTV::Lineup::Writer object and starts writing a XMLTV::Lineup file, printing
the DOCTYPE line.  Arguments are passed on to XML::WriterE<39>s constructor,
except for the following:

the 'encoding' key if present gives the XML character encoding.
For example:

  my $w = new XMLTV::Lineup::Writer(encoding => 'ISO-8859-1');

If encoding is not specified, XML::WriterE<39>s default is used
(currently UTF-8).

=cut

sub new {
    my $proto = shift;
    my $class = ref($proto) || $proto;
    my %args = @_;
    croak 'OUTPUT requires a filehandle, not a filename or anything else'
      if exists $args{OUTPUT} and not ref $args{OUTPUT};
    my $encoding = delete $args{encoding};

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

    {
	local $^W = 0; $self->doctype('xmltv-lineup', undef, 'xmltv-lineup.dtd');
    }
    $self->{xmltv_lineup_writer_state} = 'new';
    return $self;
}

=pod

=item start()

Write the start of the <xmltv-lineup> element.  Parameter is a hashref which gives
the attributes of this element.

Attributes are 'type', 'id' and 'version'

=cut

sub start {
    my $self = shift;
    die 'usage: XMLTV::Lineup::Writer->start(hashref of attrs)' if @_ != 1;
    my $attrs = shift;

    for ($self->{xmltv_lineup_writer_state}) {
	if ($_ eq 'new') {
	    # Okay.
	}
	elsif ($_ eq 'displaynames' or $_ eq 'lineupicon' or $_ eq 'channels') {
	    croak 'cannot call start() more than once on XMLTV::Lineup::Writer';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Lineup::Writer';
	}
	else { die }

	$_ = 'displaynames';
    }
    $self->startTag('xmltv-lineup', order_attrs(%{$attrs}));
}

=pod

=item write_display_name()

Write a display name element. These are written before channel elements
in the XML file.

=cut

sub write_display_name {
    my ($self, $dn) = @_;
    croak 'undef display name array passed' if not defined $dn;
    croak "expected arrayref, got: $dn" if ref $dn ne 'ARRAY';

    for ($self->{xmltv_lineup_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Lineup::Writer first';
	}
	elsif ($_ eq 'displaynames') {
	    # Okay.
	}
	elsif ($_ eq 'lineupicon') {
	    croak 'must write display names before icon';
	}
	elsif ($_ eq 'channels') {
	    croak 'must write display names before channels';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Lineup::Writer';
	}
	else { die }
    }

    my @dn = @$dn; # make a copy

    XMLTV::Lineup::write_with_lang($self, 'display-name', \@dn, 0, 0);
    $self->{xmltv_lineup_writer_state} = 'lineupicon';
}

=pod

=item write_lineup_icon()

Write an icon for the lineup. This is written before channel elements in the XML 
file.

=cut

sub write_lineup_icon {
    my ($self, $icon) = @_;
    croak 'undef icon hash passed' if not defined $icon;
    croak "expected hash, got: $icon" if ref $icon ne 'HASH';

    for ($self->{xmltv_lineup_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Lineup::Writer first';
	}
	elsif ($_ eq 'displaynames') {
	    croak 'must write icon after display names';
	}
	elsif ($_ eq 'lineupicon') {
	    # Okay.
	}
	elsif ($_ eq 'channels') {
	    croak 'must write icon before channels';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Lineup::Writer';
	}
	else { die }
    }

    my %icon = %$icon; # make a copy

    XMLTV::Lineup::write_icon($self, 'lineup-icon', \%icon);
    $self->{xmltv_lineup_writer_state} = 'channels';
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

    for ($self->{xmltv_lineup_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Lineup::Writer first';
	}
	elsif ($_ eq 'displayname') {
	    croak 'must write display names before channels';
	}
	elsif ($_ eq 'channels') {
	    # Okay.
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Lineup::Writer';
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

    for ($self->{xmltv_lineup_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Lineup::Writer first';
	}
        elsif ($_ eq 'displaynames') {
	    croak 'must write display names before channels';
	}
        elsif ($_ eq 'channels') {
	    # Okay.
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Lineup::Writer';
	}
	else { die }
    }

    my %ch = %$ch; # make a copy
    my $id = delete $ch{id};
    die "no 'id' attribute in channel" if not defined $id;
    write_element_with_handlers($self, 'channel', { id => $id },
				\@XMLTV::Lineup::Channel_Handlers, \%ch);
}

=pod

=item end()

This ends the <xmltv-lineup> element and the file.

=cut

sub end {
    my $self = shift;

    for ($self->{xmltv_lineup_writer_state}) {
	if ($_ eq 'new') {
	    croak 'must call start() on XMLTV::Lineup::Writer first';
	}
	elsif ($_ eq 'displaynames' or $_ eq 'channels') {
	    $_ = 'end';
	}
	elsif ($_ eq 'end') {
	    croak 'cannot do anything with end()ed XMLTV::Lineup::Writer';
	}
	else { die }
    }

    $self->endTag('xmltv-lineup');
    $self->SUPER::end(@_);
}

# Private.
# order_attrs()
#
# In XML the order of attributes is not significant.  But to make
# things look nice we try to output them in the same order as given in
# the xmltv-lineup DTD.
#
# Takes a list of (key, value, key, value, ...) and returns one with
# keys in a nice-looking order.
#
sub order_attrs {
    die "expected even number of elements, from a hash"
      if @_ % 2;
    my @a = ((map { $_->[0] } (@XMLTV::Lineup::Channel_Attributes)),
	     qw(type id version source-info-url source-info-name 
                source-data-url generator-info-name generator-info-url));

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
# Writes the elements of a hash to an XMLTV::Lineup::Writer using a list of
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
	my $h = $XMLTV::Lineup::Handlers{$h_name}; die "no handler $h_name" if not $h;
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
# true if everything is okay.
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
    XMLTV::Lineup::warn_unknown_keys($name, $hash);
    $w->endTag($name);
}

=pod

=back

=head1 AUTHOR

Nick Morrott, knowledgejunkie@gmail.com

This file borrows _very_ heavily from XMLTV.pm.in by Ed Avis.

=head1 TODO

Write the DTD xmltv-lineup.dtd

=head1 SEE ALSO

The file format is defined by the DTD xmltv-lineup.dtd, which is included in
the xmltv package along with this module.  It should be installed in
your systemE<39>s standard place for SGML and XML DTDs.

The xmltv package has a web page at
<http://membled.com/work/apps/xmltv/> which carries
information about the file format and the various tools and apps which
are distributed with this module.

=head1 COPYRIGHT

Copyright (C) 2009 Nick Morrott

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

=cut

1;
