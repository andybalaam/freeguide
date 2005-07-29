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
Name: freeguide-grabber-kulichki
Provides: freeguide-grabber-kulichki
Requires: freeguide-base >= 0.10.2
Version: 1
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
This is a grabber for FreeGuide to download listings from kulichki.

%prep
%setup

%build
ant buildall

%install
ant -Dinstall_share_dir=$RPM_BUILD_ROOT/%{_datadir} -Dinstall_bin_dir=$RPM_BUILD_ROOT/%{_bindir} -Dinstall_doc_dir=$RPM_BUILD_ROOT/%{_datadir}/doc/freeguide -Dinstall_real_doc_dir=%{_datadir}/doc/freeguide -Dplugin_name=grabber-kulichki install-linux-plugin


%files
%{_datadir}/freeguide/plugins/grabber-kulichki.jar

