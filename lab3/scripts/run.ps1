param([switch]$SkipBuild, [ValidateRange(1024,65535)][int]$Port = 8083)
$ErrorActionPreference = 'Stop'
$project = Split-Path $PSScriptRoot -Parent
$runtime = Join-Path $project '.local'
New-Item -ItemType Directory -Force $runtime | Out-Null
if (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue) {
    throw "Port $Port is already in use. Stop the Lab03 process or choose -Port."
}
if (-not $SkipBuild) {
    Push-Location $project
    try {
        & .\mvnw.cmd -B clean package
        if ($LASTEXITCODE -ne 0) { throw 'Build failed' }
    } finally { Pop-Location }
}
$jar = 'target/lab03-employee-management-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path (Join-Path $project $jar))) { throw 'Build the application first' }
$app = Start-Process -FilePath (Get-Command java).Source -ArgumentList @('-jar', $jar, "--server.port=$Port") -WorkingDirectory $project -WindowStyle Hidden -PassThru -RedirectStandardOutput (Join-Path $runtime 'stdout.log') -RedirectStandardError (Join-Path $runtime 'stderr.log')
Set-Content (Join-Path $runtime 'application.pid') $app.Id -Encoding ascii
try {
    for ($i = 0; $i -lt 60; $i++) {
        if (-not (Get-Process -Id $app.Id -ErrorAction SilentlyContinue)) { throw 'Java exited before the API became ready; inspect .local/stderr.log' }
        try {
            $data = Invoke-RestMethod "http://127.0.0.1:$Port/api/employees" -TimeoutSec 2
            if ($data.totalElements -ge 30) {
                Write-Host "Lab03 ready: http://127.0.0.1:$Port/swagger-ui/index.html (PID $($app.Id))"
                return
            }
        } catch { }
        Start-Sleep -Seconds 1
    }
    throw 'API startup timed out'
} catch {
    if (Get-Process -Id $app.Id -ErrorAction SilentlyContinue) { Stop-Process -Id $app.Id }
    Remove-Item -LiteralPath (Join-Path $runtime 'application.pid') -ErrorAction SilentlyContinue
    throw
}
