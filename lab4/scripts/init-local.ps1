. "$PSScriptRoot/common.ps1"
Set-Location $labRoot
if (-not (Test-Path -LiteralPath '.env')) {
    $labPassword = 'Orchid!9' + [Guid]::NewGuid().ToString('N')
    $lines = @('DB_USERNAME=sa', "DB_PASSWORD=$labPassword", 'DB_PORT=14334', 'DB_URL=jdbc:sqlserver://localhost:14334;databaseName=OrchidDB;encrypt=true;trustServerCertificate=true')
    [IO.File]::WriteAllLines((Join-Path $labRoot '.env'), $lines)
    Write-Host 'Generated .env with a local password (ignored by Git).'
}
Import-LabEnvironment
docker compose up -d --wait --wait-timeout 240
if ($LASTEXITCODE -ne 0) { throw 'SQL Server container is not ready. Check Docker Desktop.' }
'IF DB_ID(N''OrchidDB'') IS NULL CREATE DATABASE OrchidDB;' | docker compose exec -T sqlserver sh -c 'SQLCMDPASSWORD="$MSSQL_SA_PASSWORD" /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -C -b'
if ($LASTEXITCODE -ne 0) { throw 'Could not create OrchidDB.' }
Write-Host 'SQL Server and OrchidDB are ready on localhost:14334.'
