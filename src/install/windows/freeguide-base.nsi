; freeguide.nsi
;
; Adapted from example1.nsi in the NSIS package
; 

;--------------------------------

!cd ..\..\..
!system "mkdir dist"
!system "mkdir dist\windows"

; The name of the installer
Name "FreeGuide"

; The file to write
OutFile dist\windows\${NAME_VERSION}-win32.exe

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

    SetOutPath $INSTDIR

    CreateShortCut "$DESKTOP\FreeGuide TV Guide.lnk" javaw.exe '-jar "$INSTDIR\startup.jar" --doc_directory="$INSTDIR\doc" --install_directory="$INSTDIR"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

Section "Start menu folder"

    CreateDirectory $SMPROGRAMS\FreeGuide
  
    SetOutPath $INSTDIR

    CreateShortCut "$SMPROGRAMS\FreeGuide\FreeGuide TV Guide.lnk" javaw.exe '-jar "$INSTDIR\startup.jar" --doc_directory="$INSTDIR\doc" --install_directory="$INSTDIR"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

Section "Quicklaunch icon"

    SetOutPath $INSTDIR

    CreateShortCut "$QUICKLAUNCH\FreeGuide TV Guide.lnk" javaw.exe '-jar "$INSTDIR\startup.jar" --doc_directory="$INSTDIR\doc" --install_directory="$INSTDIR"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

; The stuff to install
Section "FreeGuide program"

  ; -------------------- main jar --------------------

  SetOutPath $INSTDIR
  
  File /r build\*.jar
  File /r src\install\windows\run.cmd
  
  ; --------------------- make uninstaller ---------------------
  
  WriteUninstaller $INSTDIR\uninstall.exe
  
  ; --------------------------- docs -----------------------------
  
  CreateDirectory $INSTDIR\doc\

  SetOutPath $INSTDIR\doc\

  File doc\*.html
  File doc\*.css
  File doc\*.txt
  File doc\*.png
  File doc\COPYING
  File doc\TODO
  
  ; -------------------------- xmltv -------------------------

!ifdef XMLTV  

  CreateDirectory $INSTDIR\xmltv\

  SetOutPath $INSTDIR\xmltv\

  File /r src\plugins\other-xmltv-win32-exe\files\xmltv\*.*
  File /r src\plugins\other-xmltv-win32-exe\files-nosrc\xmltv\xmltv.exe

!endif

  ; --------------------------- icons --------------------------
  
  CreateDirectory $INSTDIR\icons\
  
  File /oname=$INSTDIR\icons\logo.ico src\install\windows\icons\logo.ico
  
  ; ---------------- add/remove programs entry ------------------
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayName" "FreeGuide ${VERSION}"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "UninstallString" "$INSTDIR\uninstall.exe"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayIcon" "$INSTDIR\icons\logo.ico"
  
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayVersion" "${VERSION}"
  
SectionEnd ; end the section
