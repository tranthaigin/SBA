. "$PSScriptRoot/common.ps1"
$pidFile = Join-Path $labRoot '.local/application.pid'
if (Test-Path -LiteralPath $pidFile) {
    $labAppPid = [int](Get-Content -LiteralPath $pidFile)
    $app = Get-CimInstance Win32_Process -Filter "ProcessId=$labAppPid"
    if ($app -and $app.CommandLine.Contains('slot18-orchid-jpa-rest-lab-0.0.1-SNAPSHOT.jar')) {
        Stop-Process -Id $labAppPid
        Write-Host 'Stopped the Lab4 API. Database files are preserved.'
    } elseif ($app) { throw 'PID now belongs to another process; refusing to stop it.' }
    Remove-Item -LiteralPath $pidFile
}
