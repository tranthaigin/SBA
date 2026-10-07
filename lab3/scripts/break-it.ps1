$ErrorActionPreference = 'Stop'
$project = Split-Path $PSScriptRoot -Parent
$controller = Join-Path $project 'src/main/java/com/example/employeemanagement/controllers/EmployeeController.java'
$evidence = Join-Path $project 'evidence/console'
New-Item -ItemType Directory -Force $evidence | Out-Null
$original = [IO.File]::ReadAllBytes($controller)
$beforeHash = (Get-FileHash $controller -Algorithm SHA256).Hash
$text = [Text.Encoding]::UTF8.GetString($original)
$good = 'return ResponseEntity.created(URI.create(request.getRequestURI() + "/" + saved.empId())).body(EmployeeV1.from(saved));'
$bad = 'return ResponseEntity.ok(EmployeeV1.from(saved));'
if (-not $text.Contains($good)) { throw 'Expected POST creation line is missing; aborting mutation' }
Push-Location $project
try {
    [IO.File]::WriteAllText($controller, $text.Replace($good, $bad), (New-Object Text.UTF8Encoding($false)))
    # Windows PowerShell 5 treats native stderr warnings as ErrorRecord; inspect exit code explicitly.
    $ErrorActionPreference = 'Continue'
    & .\mvnw.cmd -B '-Dtest=EmployeeControllerTest#create_valid_returns201LocationAndBody' test *> (Join-Path $evidence 'break-it-red.txt')
    $redExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
} finally {
    [IO.File]::WriteAllBytes($controller, $original)
    Pop-Location
}
if ($redExit -eq 0) { throw 'Mutation survived: POST regression test did not fail' }
$redLog = Get-Content (Join-Path $evidence 'break-it-red.txt') -Raw
if ($redLog -notmatch 'expected:<201> but was:<200>') { throw 'Red run failed for an unexpected reason; inspect evidence' }
Push-Location $project
try {
    $ErrorActionPreference = 'Continue'
    & .\mvnw.cmd -B '-Dtest=EmployeeControllerTest#create_valid_returns201LocationAndBody' test *> (Join-Path $evidence 'break-it-green.txt')
    $greenExit = $LASTEXITCODE
    $ErrorActionPreference = 'Stop'
} finally { Pop-Location }
if ($greenExit -ne 0) { throw 'Restored POST regression test is not green' }
$afterHash = (Get-FileHash $controller -Algorithm SHA256).Hash
if ($beforeHash -ne $afterHash) { throw 'Source was not restored exactly' }
@{ mutation='POST 201 -> 200'; test='EmployeeControllerTest#create_valid_returns201LocationAndBody'; redExit=$redExit; greenExit=$greenExit; beforeSha256=$beforeHash; afterSha256=$afterHash; sourceRestored=$true } | ConvertTo-Json | Set-Content (Join-Path $evidence 'break-it-summary.json') -Encoding utf8
Write-Host 'PASS: production mutation caused expected 201/200 failure; restored source is green'
