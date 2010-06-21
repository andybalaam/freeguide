; freeguide.nsi
;
; Adapted from example1.nsi in the NSIS package
;

;--------------------------------

!include "MUI.nsh"

!cd ../..
!system "mkdir dist"
!system "mkdir dist\bin"

; The name of the installer
Name "FreeGuide"

; The file to write
OutFile dist\bin\${NAME_VERSION}-win32.exe

; The default installation directory
InstallDir $PROGRAMFILES\FreeGuide

;--------------------------------
XPStyle on

!insertmacro MUI_LANGUAGE "English"
!insertmacro MUI_LANGUAGE "Afrikaans"
!insertmacro MUI_LANGUAGE "Albanian"
!insertmacro MUI_LANGUAGE "Arabic"
!insertmacro MUI_LANGUAGE "Basque"
!insertmacro MUI_LANGUAGE "Belarusian"
!insertmacro MUI_LANGUAGE "Bosnian"
!insertmacro MUI_LANGUAGE "Breton"
!insertmacro MUI_LANGUAGE "Bulgarian"
!insertmacro MUI_LANGUAGE "Catalan"
!insertmacro MUI_LANGUAGE "Croatian"
!insertmacro MUI_LANGUAGE "Czech"
!insertmacro MUI_LANGUAGE "Danish"
!insertmacro MUI_LANGUAGE "Dutch"
!insertmacro MUI_LANGUAGE "Estonian"
!insertmacro MUI_LANGUAGE "Farsi"
!insertmacro MUI_LANGUAGE "Finnish"
!insertmacro MUI_LANGUAGE "French"
!insertmacro MUI_LANGUAGE "Galician"
!insertmacro MUI_LANGUAGE "German"
!insertmacro MUI_LANGUAGE "Greek"
!insertmacro MUI_LANGUAGE "Hebrew"
!insertmacro MUI_LANGUAGE "Hungarian"
!insertmacro MUI_LANGUAGE "Icelandic"
!insertmacro MUI_LANGUAGE "Indonesian"
!insertmacro MUI_LANGUAGE "Irish"
!insertmacro MUI_LANGUAGE "Italian"
!insertmacro MUI_LANGUAGE "Japanese"
!insertmacro MUI_LANGUAGE "Korean"
!insertmacro MUI_LANGUAGE "Kurdish"
!insertmacro MUI_LANGUAGE "Latvian"
!insertmacro MUI_LANGUAGE "Lithuanian"
!insertmacro MUI_LANGUAGE "Luxembourgish"
!insertmacro MUI_LANGUAGE "Macedonian"
!insertmacro MUI_LANGUAGE "Malay"
!insertmacro MUI_LANGUAGE "Mongolian"
!insertmacro MUI_LANGUAGE "Norwegian"
!insertmacro MUI_LANGUAGE "NorwegianNynorsk"
!insertmacro MUI_LANGUAGE "Polish"
!insertmacro MUI_LANGUAGE "PortugueseBR"
!insertmacro MUI_LANGUAGE "Portuguese"
!insertmacro MUI_LANGUAGE "Romanian"
!insertmacro MUI_LANGUAGE "Russian"
!insertmacro MUI_LANGUAGE "SerbianLatin"
!insertmacro MUI_LANGUAGE "Serbian"
!insertmacro MUI_LANGUAGE "SimpChinese"
!insertmacro MUI_LANGUAGE "Slovak"
!insertmacro MUI_LANGUAGE "Slovenian"
!insertmacro MUI_LANGUAGE "SpanishInternational"
!insertmacro MUI_LANGUAGE "Spanish"
!insertmacro MUI_LANGUAGE "Swedish"
!insertmacro MUI_LANGUAGE "Thai"
!insertmacro MUI_LANGUAGE "TradChinese"
!insertmacro MUI_LANGUAGE "Turkish"
!insertmacro MUI_LANGUAGE "Ukrainian"
!insertmacro MUI_LANGUAGE "Uzbek"
!insertmacro MUI_LANGUAGE "Welsh"

Var JAVA_PATH

Function .onInit
    ; check java in path
    StrCpy $JAVA_PATH 'javaw.exe'
    ExecWait $JAVA_PATH $0
    IfErrors 0 GoLabel

    Call DetectJRE
    StrCmp $JAVA_PATH "" 0 GoLabel

    MessageBox MB_YESNO "You don't have Java installed, but FreeGuide needs Java to work. Please go to http://java.com/getjava and download Java. Install it anyway ?" IDYES GoLabel IDNO AbortLabel
    AbortLabel:
        Abort
    GoLabel:

    !insertmacro MUI_LANGDLL_DISPLAY

FunctionEnd

Function DetectJRE
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  StrCmp $2 "" DetectTry2
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$2" "JavaHome"
  StrCmp $3 "" DetectTry2
  Goto GetJRE

DetectTry2:
  ReadRegStr $2 HKLM "SOFTWARE\JavaSoft\Java Development Kit" "CurrentVersion"
  StrCmp $2 "" NoFound
  ReadRegStr $3 HKLM "SOFTWARE\JavaSoft\Java Development Kit\$2" "JavaHome"
  StrCmp $3 "" NoFound

GetJRE:
  IfFileExists "$3\bin\javaw.exe" 0 NoFound
  StrCpy $JAVA_PATH "$3\bin\javaw.exe"
  Return

NoFound:
  StrCpy $JAVA_PATH ""
  Return
FunctionEnd

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

    CreateShortCut "$DESKTOP\FreeGuide TV Guide.lnk" $JAVA_PATH '-jar "$INSTDIR\FreeGuide.jar" --install_directory="$INSTDIR" --doc_directory="$INSTDIR\doc"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

Section "Start menu folder"

    CreateDirectory $SMPROGRAMS\FreeGuide

    SetOutPath $INSTDIR

    CreateShortCut "$SMPROGRAMS\FreeGuide\FreeGuide TV Guide.lnk" $JAVA_PATH '-jar "$INSTDIR\FreeGuide.jar" --install_directory="$INSTDIR" --doc_directory="$INSTDIR\doc"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

Section "Quicklaunch icon"

    SetOutPath $INSTDIR

    CreateShortCut "$QUICKLAUNCH\FreeGuide TV Guide.lnk" $JAVA_PATH '-jar "$INSTDIR\FreeGuide.jar" --install_directory="$INSTDIR" --doc_directory="$INSTDIR\doc"' $INSTDIR\icons\logo.ico

SectionEnd

;--------------------------------

; The stuff to install
Section "!FreeGuide program"
  SectionIn RO

  ; ------------------ main files to install -------------------

  Delete "$INSTDIR\lib\*.*"

  SetOutPath $INSTDIR

  File /r jar\*.*
  File /r install\windows\run.cmd

  SetOutPath $INSTDIR\doc

  File /r doc-bin\*.*

 !ifdef XMLTV

    CreateDirectory $INSTDIR\xmltv

    SetOutPath $INSTDIR\xmltv

    File /r xmltv\*.*

 !endif

  ; --------------------- make uninstaller ---------------------

  WriteUninstaller $INSTDIR\uninstall.exe

  ; --------------------------- icons --------------------------

  CreateDirectory $INSTDIR\icons

  File /oname=$INSTDIR\icons\logo.ico install\windows\icons\logo.ico

  ; ---------------- add/remove programs entry ------------------

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayName" "FreeGuide ${VERSION}"

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "UninstallString" "$INSTDIR\uninstall.exe"

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayIcon" "$INSTDIR\icons\logo.ico"

  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Product" "DisplayVersion" "${VERSION}"

SectionEnd ; end the section
