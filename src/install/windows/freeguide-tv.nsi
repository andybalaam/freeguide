; freeguide-tv.nsi
;
; Adapted from example1.nsi in the NSIS package
; 

;--------------------------------

; The name of the installer
Name "FreeGuide"

; The file to write
OutFile "..\..\..\installer\FreeGuide-0.8-Install.exe"

; The default installation directory
InstallDir $PROGRAMFILES\freeguide-tv 

;--------------------------------

; Pages

Page directory
Page instfiles

;--------------------------------

Section "un.Uninstaller Section"

    ExecWait 'java -jar "$INSTDIR\FreeGuide-0.8-Win.jar" --uninstall'

    RMDir /r $INSTDIR

    Delete "$SMPROGRAMS\FreeGuide\FreeGuide TV Guide.lnk"
  
    Delete "$QUICKLAUNCH\FreeGuide TV Guide.lnk"
  
    Delete "$DESKTOP\FreeGuide TV Guide.lnk"
    
SectionEnd

; The stuff to install
Section "Installer Section"

  ; -------------------- main jar --------------------

  SetOutPath $INSTDIR
  
  File /oname=FreeGuide.jar ..\..\..\dist\FreeGuide-0.8-Win.jar
  
  ; ------------ remember install dir in Java prefs -------------
  
  ExecWait 'java -jar "$INSTDIR\FreeGuide-0.8-Win.jar" --install "misc.install_directory=$INSTDIR"'
  
  ; --------------------- make uninstaller ---------------------
  
  WriteUninstaller $INSTDIR\uninstall.exe
  
  ; --------------------------- docs -----------------------------
  
  CreateDirectory $INSTDIR\doc\
  
  File /oname=doc\contributors.html ..\..\..\doc\contributors.html
  File /oname=doc\COPYING ..\..\..\doc\COPYING
  File /oname=doc\design.html ..\..\..\doc\design.html
  File /oname=doc\developers.html ..\..\..\doc\developers.html
  File /oname=doc\FAQ.html ..\..\..\doc\FAQ.html
  File /oname=doc\FreeGuide-0_7-Linux-MetalLookAndFeel.png ..\..\..\doc\FreeGuide-0_7-Linux-MetalLookAndFeel.png
  File /oname=doc\index.html ..\..\..\doc\index.html
  File /oname=doc\INSTALL-linux-noxmltv.html ..\..\..\doc\INSTALL-linux-noxmltv.html
  File /oname=doc\INSTALL-windows.html ..\..\..\doc\INSTALL-windows.html
  File /oname=doc\LookAndFeel.html ..\..\..\doc\LookAndFeel.html
  File /oname=doc\README.html ..\..\..\doc\README.html
  File /oname=doc\stylesheet.css ..\..\..\doc\stylesheet.css
  File /oname=doc\timezone.html ..\..\..\doc\timezone.html
  File /oname=doc\TODO ..\..\..\doc\TODO
  File /oname=doc\userguide.html ..\..\..\doc\userguide.html
  
  ; -------------------------- xmltv -------------------------
  
  CreateDirectory $INSTDIR\xmltv\
  
  File /oname=xmltv\xmltv.exe ..\..\..\xmltv\xmltv.exe
  
  ; -------------------- xmltv shared dirs --------------------
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\tv_grab_de_tvtoday
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_de_tvtoday\channel_ids ..\..\..\xmltv\share\xmltv\tv_grab_de_tvtoday\channel_ids
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\\tv_grab_it
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_it\channel_ids ..\..\..\xmltv\share\xmltv\tv_grab_it\channel_ids
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\\tv_grab_nl
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_nl\channels ..\..\..\xmltv\share\xmltv\tv_grab_nl\channels
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\\tv_grab_uk_rt\
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_uk_rt\channel_ids ..\..\..\xmltv\share\xmltv\tv_grab_uk_rt\channel_ids
  
  ; ---------------- menu shortcuts and icons ------------------
  
  CreateDirectory $INSTDIR\icons\
  
  File /oname=$INSTDIR\icons\logo.ico ..\..\..\src\images\logo.ico
  
  CreateDirectory $SMPROGRAMS\FreeGuide
  
  CreateShortCut "$SMPROGRAMS\FreeGuide\FreeGuide TV Guide.lnk" $INSTDIR\FreeGuide.jar x $INSTDIR\icons\logo.ico
  
  CreateShortCut "$QUICKLAUNCH\FreeGuide TV Guide.lnk" $INSTDIR\FreeGuide.jar x $INSTDIR\icons\logo.ico
  
  CreateShortCut "$DESKTOP\FreeGuide TV Guide.lnk" $INSTDIR\FreeGuide.jar x $INSTDIR\icons\logo.ico
  
SectionEnd ; end the section