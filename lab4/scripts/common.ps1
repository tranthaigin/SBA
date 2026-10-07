$ErrorActionPreference = 'Stop'
$labRoot = Split-Path $PSScriptRoot -Parent

function Import-LabEnvironment {
    $envFile = Join-Path $labRoot '.env'
    if (-not (Test-Path -LiteralPath $envFile)) { throw 'Run scripts/init-local.ps1 or create .env from .env.example first.' }
    foreach ($line in [IO.File]::ReadAllLines($envFile)) {
        if ($line -match '^([A-Z_]+)=(.*)$') {
            [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
        }
    }
}

function Invoke-LabSql([string]$Query) {
    $Query | docker compose -f (Join-Path $labRoot 'compose.yaml') exec -T sqlserver sh -c 'SQLCMDPASSWORD="$MSSQL_SA_PASSWORD" /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -C -b -d OrchidDB -W -s ,'
    if ($LASTEXITCODE -ne 0) { throw 'SQL Server verification failed.' }
}

function Wait-LabApi([int]$ApplicationProcessId = 0) {
    for ($attempt = 0; $attempt -lt 60; $attempt++) {
        if ($ApplicationProcessId -gt 0 -and -not (Get-Process -Id $ApplicationProcessId -ErrorAction SilentlyContinue)) {
            throw 'Application process exited. See .local/application-error.log and .local/application.log.'
        }
        try {
            $null = Invoke-RestMethod 'http://localhost:8080/api/categories' -TimeoutSec 2
            return
        } catch { Start-Sleep -Seconds 2 }
    }
    throw 'API did not start. See .local/application.log and .local/application-error.log.'
}
