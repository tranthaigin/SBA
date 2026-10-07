param([string]$BaseUrl = 'http://127.0.0.1:8083')
$ErrorActionPreference = 'Stop'
$project = Split-Path $PSScriptRoot -Parent
$postman = Join-Path $project 'postman'
$evidence = Join-Path $project 'evidence/postman'
New-Item -ItemType Directory -Force $evidence | Out-Null
Push-Location $postman
try {
    $ErrorActionPreference = 'Continue'
    & npm.cmd ci --no-audit --no-fund
    $installExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    if ($installExit -ne 0) { throw 'Newman dependency install failed' }
    $ErrorActionPreference = 'Continue'
    & .\node_modules\.bin\newman.cmd run Employee-Lab03.postman_collection.json --env-var "baseUrl=$BaseUrl" --reporters cli,json --reporter-json-export ../evidence/postman/newman-run.json *> (Join-Path $evidence 'newman-console.txt')
    $runExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
    Get-Content (Join-Path $evidence 'newman-console.txt') -Tail 24
    if ($runExit -ne 0) { throw 'Postman assertions failed; inspect evidence/postman' }
} finally { Pop-Location }
