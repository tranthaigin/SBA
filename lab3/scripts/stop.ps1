$ErrorActionPreference = 'Stop'
$pidFile = Join-Path (Split-Path $PSScriptRoot -Parent) '.local/application.pid'
if (-not (Test-Path $pidFile)) { Write-Host 'No recorded Lab03 process'; return }
$appId = [int](Get-Content $pidFile)
$process = Get-CimInstance Win32_Process -Filter "ProcessId=$appId"
if ($process) {
    if ($process.Name -ne 'java.exe' -or $process.CommandLine -notmatch 'lab03-employee-management-0\.0\.1-SNAPSHOT\.jar') { throw 'Recorded PID is not a Lab03 Java process; refusing to stop it' }
    Stop-Process -Id $appId
}
Remove-Item -LiteralPath $pidFile
Write-Host 'Lab03 stopped'
