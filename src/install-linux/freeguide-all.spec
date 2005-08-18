###
### RPM spec file for FreeGuide
### 
### Adapted from jEdit's spec file http://www.jedit.org
###

### To create the RPM from CVS, cd to src/install-linux and run:
### ./buildrpm
###
### This will place the built RPMs into dist/

### You will need to have ant installed
### for this to work.

Summary: A TV Guide
Name: freeguide-all
Provides: freeguide-all
Requires: freeguide-base             >= 0.10.2
          freeguide-grabber-cosmostv >= 1
          freeguide-grabber-kulichki >= 1
          freeguide-grabber-newsvm   >= 1
          freeguide-grabber-ntvplus  >= 1
          freeguide-grabber-vsetv    >= 1
          freeguide-grabber-xmltv    >= 1
          freeguide-impexp-jtv       >= 1
          freeguide-impexp-palmatv   >= 1
          freeguide-impexp-xmltv     >= 1
          freeguide-reminder-alarm   >= 1
          freeguide-storage-serfiles >= 1
          freeguide-ui-horizontal    >= 1
Version: 0.10.2
Release: 1
# REMINDER: bump this with each RPM
Copyright: GPL
Group: Accessories/
Source0: freeguide-0.10.2.tar.gz
URL: http://freeguide-tv.sourceforge.net/
Vendor: Andy Balaam <axis3x3@users.sourceforge.net>
Packager: Andy Balaam <axis3x3@users.sourceforge.net>
BuildArch: noarch
BuildRoot: %{_tmppath}/%{name}-%{version}-root

%description
FreeGuide is a TV guide program. It uses parser programs to extract TV
information from web pages and stores them for viewing without the need to
connect to the Internet. The viewer allows the user to view television listings
and create customised TV guides by selecting programmes and by building up a
favourites list.

It works with listings for many countries.  Check the web site freeguide-tv.sf.net for details.

FreeGuide requires Java 2 version 1.4.

The freeguide-all package depends on all the plugins available for freeguide, so if you install it with a package manager such as Synaptic, yum, or apt-get, you will automatically get a full install of everything you need to use freeguide. 

%prep
%setup

%build
ant buildall

%install
ant -Dinstall_share_dir=$RPM_BUILD_ROOT/%{_datadir} -Dinstall_bin_dir=$RPM_BUILD_ROOT/%{_bindir} -Dinstall_doc_dir=$RPM_BUILD_ROOT/%{_datadir}/doc/freeguide -Dinstall_real_doc_dir=%{_datadir}/doc/freeguide install-linux-all
