###
### RPM spec file for FreeGuide
### 
### Adapted from jEdit's spec file http://www.jedit.org
###

### To create the RPM from CVS, cd to src/install/linux and run:
### ./buildrpm
###
### This will place the built RPMs into installers/

### You will need to have ant installed
### for this to work.

Summary: A TV Guide
Name: FreeGuide
Version: 0.8
Release: 3
# REMINDER: bump this with each RPM
Copyright: GPL
Group: Accessories/
Source0: FreeGuide-0.8.tar.gz
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

It works with listings for Canada, Denmark, Finland, France, Germany, Hungary,
Italy, Japan, the Netherlands, Norway, Portugal, Romania, Spain, Sweden, the UK
and the US.

FreeGuide requires Java 2 version 1.4.

%prep
%setup

%build
ant FreeGuide-Linux-NoXMLTV

%install
ant -Dinstall_share_dir=$RPM_BUILD_ROOT/%{_datadir} -Dinstall_bin_dir=$RPM_BUILD_ROOT/%{_bindir} install-Linux-NoXMLTV-files

%clean
ant clean

%files
%{_bindir}/freeguide
%{_datadir}/freeguide/FreeGuide.jar
%{_datadir}/freeguide/doc/contributors.html
%{_datadir}/freeguide/doc/COPYING
%{_datadir}/freeguide/doc/design.html
%{_datadir}/freeguide/doc/developers.html
%{_datadir}/freeguide/doc/FAQ.html
%{_datadir}/freeguide/doc/FreeGuide-0_7-Linux-MetalLookAndFeel.png
%{_datadir}/freeguide/doc/index.html
%{_datadir}/freeguide/doc/INSTALL-linux-noxmltv.html
%{_datadir}/freeguide/doc/INSTALL-windows.html
%{_datadir}/freeguide/doc/LookAndFeel.html
%{_datadir}/freeguide/doc/README.html
%{_datadir}/freeguide/doc/stylesheet.css
%{_datadir}/freeguide/doc/timezone.html
%{_datadir}/freeguide/doc/TODO
%{_datadir}/freeguide/doc/userguide.html
%{_datadir}/pixmaps/freeguide.png
%{_datadir}/pixmaps/freeguide/logo-16x16.png
%{_datadir}/pixmaps/freeguide/logo-32x32.png
%{_datadir}/pixmaps/freeguide/logo-48x48.png
%{_datadir}/pixmaps/freeguide/logo-64x64.png
%{_datadir}/pixmaps/freeguide/logo-72x72.png
%{_datadir}/pixmaps/freeguide/logo-96x96.png
%{_datadir}/applications/freeguide.desktop


