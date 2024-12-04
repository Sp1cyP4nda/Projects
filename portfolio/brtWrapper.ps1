########################
### brt Script Start ###
###      ~~~{@       ###
########################

If (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")){
    Write-Output "This script needs to be run As Admin"
    Break
}
Write-Output "Updating brt"
python3 -m pip install --extra-index-url=https://pypi.us.code42.com --upgrade bulk-restore-tool # update brt
Write-Output `n"brt up to date"
if ($Env:VIRTUAL_ENV) {
    Write-Output "You are already in a virtual environment: $Env:VIRTUAL_ENV"
} else {
    Set-Location $PSScriptRoot # sets the location to be where the script is stored
    .\Scripts\activate
    Write-Output "You are now in a virtual environment: $Env:VIRTUAL_ENV"
}
Set-Location $PSScriptRoot # sets the location to be where the script is stored
Write-Output "Please do not include quotes in your inputs"
$destinationDirectory = Read-Host "Enter the destination directory"
$tempDir="C:\temp\"
New-Item -Path "$destinationDirectory\logs\" -ItemType Directory -Force | Out-Null # Create \logs\ directory if it doesn't exist
New-Item -Path "$tempDir" -ItemType Directory -Force | Out-Null # Create \logs\ directory if it doesn't exist
$inputFile=Read-Host "Enter \path\to\input.file"
$jobArray=@(Get-Content $inputFile)
$jobs=$jobArray -split "`n"
$jobCount=$jobs.Count
Write-Output "$jobCount job(s) loaded" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append
$jobNumber=0
while ($jobNumber -lt $jobCount) {
    # Summarize current job
    $jobName=$jobs[$jobNumber-0]
    Write-Output "`n"(Get-Date)"`n`Working on job number"($jobNumber+1)"of"$jobCount": $jobName" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append

    # Reformat the inputs
    $splitName=$jobName -split "_"
    $userName=$splitName[0]

    # Check if all peripheries are connected
    if (!(Test-Path -Path $destinationDirectory)) { #If something gets disconnected, stop the script
        "`n$(hostname) $(Get-Date)" | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
        Write-Output "$destinationDirectory was disconnected `nExiting..." | Tee-Object -FilePath "$destinationDirectory\7zip.log" -Append
        break
    } else {
        # Download restore
        Write-Output "`n"(Get-Date)"Starting $jobName" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append
        brt create-job --log-file $destinationDirectory\logs\"$jobname"_create.log --name "$jobName" --target-folder "$tempDir" --type username "$userName@abiomed.com" --include-deleted
        Write-Output "`n"(Get-Date)"Downloading restore: $jobName" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append
        brt start-job ".\restore_$jobName.json" --log-file $destinationDirectory\logs\"$jobname"_run.log --log-level WARN

        # Zip restore
        Write-Output "`n"(Get-Date)"Zipping restore $jobName" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append
        & 'C:\Program Files\7-Zip\7z.exe' -mx=0 a "$destinationDirectory\$jobName.zip" "$tempDir\$jobName" -y -sdel | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append

        # Move .json out of the way to keep tidy
        Write-Output (Get-Date)"Cleaning up backend" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append
        Move-Item ".\restore_$jobName.json" .\finished\ -Force
    }
    $jobNumber++
}
Write-Output "`n"(Get-Date)"$jobCount job(s) complete" | Tee-Object -FilePath "$destinationDirectory\brt.log" -Append
deactivate
break