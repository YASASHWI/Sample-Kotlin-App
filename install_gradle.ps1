$gradleVersion = "8.5"
$downloadUrl = "https://services.gradle.org/distributions/gradle-$gradleVersion-bin.zip"
$installDir = "C:\Gradle"
$zipFile = "$env:TEMP\gradle.zip"

Write-Host "Downloading Gradle $gradleVersion..."
Invoke-WebRequest -Uri $downloadUrl -OutFile $zipFile

Write-Host "Extracting Gradle..."
If (Test-Path $installDir) { Remove-Item -Path $installDir -Recurse -Force }
Expand-Archive -Path $zipFile -DestinationPath "C:\" -Force

# Rename extracted folder (gradle-8.5) to just 'Gradle'
$extractedFolder = "C:\gradle-$gradleVersion"
if (Test-Path $extractedFolder) {
    Rename-Item -Path $extractedFolder -NewName "Gradle"
}

Write-Host "Adding to PATH..."
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
$gradleBin = "$installDir\bin"

if ($currentPath -notlike "*$gradleBin*") {
    $newPath = "$currentPath;$gradleBin"
    [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
    Write-Host "Gradle added to PATH. Please restart your terminal."
} else {
    Write-Host "Gradle is already in the PATH."
}

Write-Host "Installation Complete!"
Write-Host "You MUST restart your terminal (close and reopen) for the 'gradle' command to work."
