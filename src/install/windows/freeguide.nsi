; freeguide.nsi
;
; Adapted from example1.nsi in the NSIS package
; 

;--------------------------------

; The name of the installer
Name "FreeGuide"

!define VERSION "0.8.3"

; The file to write
OutFile ..\..\..\installer\FreeGuide-${VERSION}-Windows.exe

; The default installation directory
InstallDir $PROGRAMFILES\FreeGuide 

;--------------------------------

; Pages

Page directory
Page components
Page instfiles

;--------------------------------

Section "un.Uninstaller Section"
    
    ; Remove the installation directory

    RMDir /r $INSTDIR

    ; Remove the shortcuts
    
    Delete "$SMPROGRAMS\FreeGuide\FreeGuide TV Guide.lnk"
    
    RMDir /r $SMPROGRAMS\FreeGuide
  
    Delete "$QUICKLAUNCH\FreeGuide TV Guide.lnk"
  
    Delete "$DESKTOP\FreeGuide TV Guide.lnk"
    
    ; Remove the registry entries for the Add/remove programs dialogue
    
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayName"
    
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "UninstallString"
    
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayIcon"
  
    DeleteRegValue HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayVersion"
    
    ; Remove the Java Preferences registry keys
    
    DeleteRegKey HKU "S-1-5-21-1254056498-3042452539-1974565712-1005\Software\JavaSoft\Prefs\org\freeguide-tv"
    
SectionEnd

;--------------------------------

Section "Desktop icon"

    CreateShortCut "$DESKTOP\FreeGuide TV Guide.lnk" javaw.exe '-jar "$INSTDIR\FreeGuide.jar" --doc_directory "$INSTDIR\doc" --install_directory "$INSTDIR"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

Section "Start menu folder"

    CreateDirectory $SMPROGRAMS\FreeGuide
  
    CreateShortCut "$SMPROGRAMS\FreeGuide\FreeGuide TV Guide.lnk" javaw.exe '-jar "$INSTDIR\FreeGuide.jar" --doc_directory "$INSTDIR\doc" --install_directory "$INSTDIR"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

Section "Quicklaunch icon"

    CreateShortCut "$QUICKLAUNCH\FreeGuide TV Guide.lnk" javaw.exe '-jar "$INSTDIR\FreeGuide.jar" --doc_directory "$INSTDIR\doc" --install_directory "$INSTDIR"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

; The stuff to install
Section "FreeGuide program"

  ; -------------------- main jar --------------------

  SetOutPath $INSTDIR
  
  File /oname=FreeGuide.jar ..\..\..\dist\FreeGuide-${VERSION}-Win.jar
  
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
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\tv_grab_it_lt
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_it_lt\channel_ids ..\..\..\xmltv\share\xmltv\tv_grab_it_lt\channel_ids
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\tv_grab_nl
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_nl\channels ..\..\..\xmltv\share\xmltv\tv_grab_nl\channels
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\tv_grab_uk_bleb\
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_uk_bleb\icon_urls ..\..\..\xmltv\share\xmltv\tv_grab_uk_bleb\icon_urls
  
  CreateDirectory $INSTDIR\xmltv\share\xmltv\tv_grab_uk_rt\
  
  File /oname=$INSTDIR\xmltv\share\xmltv\tv_grab_uk_rt\channel_ids ..\..\..\xmltv\share\xmltv\tv_grab_uk_rt\channel_ids
  
  ; --------------------------- icons --------------------------
  
  CreateDirectory $INSTDIR\icons\
  
  File /oname=$INSTDIR\icons\logo.ico ..\..\..\src\images\logo.ico
  
  ; ---------------- add/remove programs entry ------------------
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayName" "FreeGuide ${VERSION}"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "UninstallString" "$INSTDIR\uninstall.exe"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayIcon" "$INSTDIR\icons\logo.ico"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayVersion" "${VERSION}"
  
SectionEnd ; end the section
