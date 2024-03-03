# Inicjalizacja obiektu PSObject do przechowywania danych o przeglądarkach
$browsers = @()

# Funkcja do pobierania wersji Firefoxa
function Get-FirefoxVersion {
    $firefoxRegPath = 'HKLM:\SOFTWARE\mozilla.org\Mozilla'
    try {
        $version = (Get-ItemProperty -Path $firefoxRegPath -Name CurrentVersion).CurrentVersion
        if ($version) {
            return $version
        } else {
            return "Nie można odczytać wersji"
        }
    } catch {
        Write-Host "Błąd przy próbie odczytania wersji Firefoxa."
        return $null
    }
}

# Google Chrome
$chromeRegPath = 'HKLM:\SOFTWARE\Microsoft\Windows\CurrentVersion\App Paths\chrome.exe'
$chromePath = (Get-ItemProperty -Path $chromeRegPath).'(Default)'
$chromeVersion = if (Test-Path $chromePath) { (Get-Item $chromePath).VersionInfo.FileVersion } else { $null }
$browsers += [PSCustomObject]@{
    Name = "Google Chrome"
    Version = $chromeVersion
    Architecture = if ($chromeVersion) { $env:PROCESSOR_ARCHITECTURE } else { "Nie znaleziono" }
}

# Mozilla Firefox
$firefoxVersion = Get-FirefoxVersion
$browsers += [PSCustomObject]@{
    Name = "Mozilla Firefox"
    Version = $firefoxVersion
    Architecture = if ($firefoxVersion) { $env:PROCESSOR_ARCHITECTURE } else { "Nie znaleziono" }
}

# Microsoft Edge
$edgeRegPath = 'HKCU:\SOFTWARE\Microsoft\Edge\BLBeacon'
$edgeVersion = (Get-ItemProperty -Path $edgeRegPath -Name version).version
$browsers += [PSCustomObject]@{
    Name = "Microsoft Edge"
    Version = $edgeVersion
    Architecture = if ($edgeVersion) { $env:PROCESSOR_ARCHITECTURE } else { "Nie znaleziono" }
}

# Eksportowanie danych do pliku JSON
$browsers | ConvertTo-Json | Out-File -FilePath "browsers_info.json"

# Opcjonalnie: Wyświetlanie danych JSON w konsoli
$browsers | ConvertTo-Json | Write-Host
