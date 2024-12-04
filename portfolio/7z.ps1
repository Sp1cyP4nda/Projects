#######################
### 7z Script Start ###
###     ~~~~~{@     ###
#######################
#
If (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")){
    Write-Output "This script needs to be run As Admin"
    Break
}
$sourceDirectory = Read-Host "Enter the source directory"
$destinationDirectory = Read-Host "Enter the destination directory"
$7zPath = "C:\Program Files\7-Zip\7z.exe"
$folders = Get-ChildItem -Path $sourceDirectory -Directory

New-Item -Path "$sourceDirectory\finished\" -ItemType Directory -Force | Out-Null # Create \finished\ directory if it doesn't exist

foreach ($folder in $folders) {
    $folderName = $folder.name
    $folderPath = $folder.fullname
    $zipFileName = "$destinationDirectory\$folderName.zip"  # Temp zip file location
    Write-Output "`n"(Get-Date)"`n`Working on"$folderName
    
    if ($folderName -eq "finished" -or $folderName -eq "temp") { # don't touch the \finished\ or \Temp\ folders
        Write-Output "Skipping $folderName..."
    } else {
        # First check to see if the zip file already exists in $destinationDirectory, 
        # if TRUE, then move original and skip
        if (Test-Path -Path "$destinationDirectory\$folderName.zip") { # Do not assign this to $zipFileName, as it may not always be the same
            Write-Output "Zip file for $folderName already exists. Skipping..."
            Move-Item "$folderPath" "$sourceDirectory\finished\"
        } else {
            $zipCommand = "& `"$7zPath`" -mx=0 a `"$zipFileName`" `"$folderPath`""
            "`n$(hostname) $(Get-Date)" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
            Write-Output "Zipping $folderName to $zipFileName" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
            Invoke-Expression $zipCommand | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
            "`n$(hostname) $(Get-Date)" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
            Write-Output "Zipped $folderName to $zipFileName" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
            if (!(Test-Path -Path $destinationDirectory) -or !(Test-Path -Path $sourceDirectory)) { #If something gets disconnected, stop the script
                "`n$(hostname) $(Get-Date)" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
                Write-Output "One of the paths was disconnected `nExiting..." | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
                break
            } else {
                "`n$(hostname) $(Get-Date)" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
                Write-Output "Moving $folderName to finished" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
                Move-Item "$folderPath" "$sourceDirectory\finished\"
            }
        }
    }
}