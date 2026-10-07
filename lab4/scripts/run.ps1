param([switch]$SkipBuild, [switch]$UseH2Verification)
. "$PSScriptRoot/common.ps1"
Set-Location $labRoot
Import-LabEnvironment
New-Item -ItemType Directory -Path '.local' -Force | Out-Null
if (Test-Path -LiteralPath '.local/application.pid') {
    $labAppPid = [int](Get-Content -LiteralPath '.local/application.pid')
    if (Get-Process -Id $labAppPid -ErrorAction SilentlyContinue) { throw 'Lab app already running. Use scripts/stop.ps1 first.' }
}
if (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue) { throw 'Port 8080 is in use.' }
if (-not $SkipBuild) {
    & .\mvnw.cmd -B package
    if ($LASTEXITCODE -ne 0) { throw 'Maven build failed.' }
}
$jar = Join-Path $labRoot 'target/slot18-orchid-jpa-rest-lab-0.0.1-SNAPSHOT.jar'
if (-not (Test-Path -LiteralPath $jar)) { throw 'Build the project first.' }
# Pass a relative ASCII jar path: Windows Java launchers can corrupt accented absolute paths.
$labJarRelative = 'target/slot18-orchid-jpa-rest-lab-0.0.1-SNAPSHOT.jar'
$labJavaArgs = @('-jar', $labJarRelative)
if ($UseH2Verification) {
    # Diagnostic fallback only. SQL Server remains the project's default datasource.
    $h2 = Get-ChildItem -LiteralPath (Join-Path $env:USERPROFILE '.m2/repository/com/h2database/h2') -Recurse -Filter 'h2-*.jar' | Select-Object -First 1 -ExpandProperty FullName
    if (-not $h2) { throw 'H2 test dependency missing; run Maven tests first.' }
    $labJavaArgs = @(('"-Dloader.path=' + $h2 + '"'), '-cp', $labJarRelative, 'org.springframework.boot.loader.launch.PropertiesLauncher', '--spring.datasource.url=jdbc:h2:file:./.local/http-verification;WRITE_DELAY=0', '--spring.datasource.driver-class-name=org.h2.Driver', '--spring.datasource.username=sa', '--spring.datasource.password=', '--spring.jpa.database-platform=org.hibernate.dialect.H2Dialect', '--spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect')
}
$app = Start-Process -FilePath (Get-Command java).Source -ArgumentList $labJavaArgs -WorkingDirectory $labRoot -WindowStyle Hidden -RedirectStandardOutput (Join-Path $labRoot '.local/application.log') -RedirectStandardError (Join-Path $labRoot '.local/application-error.log') -PassThru
$app.Id | Set-Content -LiteralPath '.local/application.pid'
Wait-LabApi -ApplicationProcessId $app.Id
Write-Host "Orchid API running at http://localhost:8080 (PID $($app.Id))."
