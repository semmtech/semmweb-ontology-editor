# See http://nsis.sourceforge.net/Simple_tutorials

OutFile  "sample-installer.exe"
InstallDir $DESKTOP

Section

    # Read a registry value from the Windows registry
    ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion
    MessageBox MB_OK "Version: $0"

    # Open a file and write to it
    FileOpen $1 "$DESKTOP\JRE-version.txt" w
    FileWrite $1 "JRE Version = $0"
    FileWrite $1 "Destination = $INSTDIR"
    
    SetOutPath $INSTDIR
    File readme.txt
    
    CreateShortCut "$SMPROGRAMS\Shortcut.lnk" "$INSTDIR\readme.txt"
    
    WriteUninstaller "$INSTDIR\uninstaller.exe"
    
SectionEnd

Section "Uninstall"
    
    Delete "$INSTDIR\uninstaller.exe"
    Delete "$INSTDIR\readme.txt"
    Delete "$INSTDIR\JRE-version.txt"
    Delete "$SMPROGRAMS\Shortcut.lnk"

SectionEnd