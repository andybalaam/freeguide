To build an EXE, ensure javac, java, ant and makensis are in your path, then do this:

cd d:\dev\freeguide
ant dist-exe

This builds an installer in dist\bin\.

To install, do:

dist\bin\freeguide-0.10.6-with-xmltv-win32.exe

