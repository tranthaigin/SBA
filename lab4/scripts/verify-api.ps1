param([switch]$VerifyDatabase, [switch]$Restart, [switch]$UseH2Verification)
. "$PSScriptRoot/common.ps1"
Set-Location $labRoot
Import-LabEnvironment
if ($VerifyDatabase -and $UseH2Verification) { throw 'SQL Server assertions cannot run in H2 verification mode.' }
Add-Type -AssemblyName System.Net.Http
$client = New-Object System.Net.Http.HttpClient
$client.Timeout = [TimeSpan]::FromSeconds(15)
$script:matrix = [Collections.Generic.List[string]]::new()
New-Item -ItemType Directory -Path 'evidence/http','evidence/sqlserver','evidence/console' -Force | Out-Null

function Assert-Lab([bool]$Condition, [string]$Message) {
    if (-not $Condition) {
        $script:matrix.Add("| STATE ASSERTION | $Message | true | false | FAIL | See response JSON above |")
        throw $Message
    }
}

function Request-Lab([string]$Id, [string]$Method, [string]$Path, [int]$Expected, $Body = $null, [string]$ContentType = 'application/json') {
    $url = 'http://localhost:8080' + $Path
    $message = New-Object System.Net.Http.HttpRequestMessage ([System.Net.Http.HttpMethod]::new($Method)), $url
    $bodyText = if ($Body -is [string]) { $Body } elseif ($null -ne $Body) { $Body | ConvertTo-Json -Depth 8 -Compress } else { $null }
    if ($null -ne $bodyText) { $message.Content = New-Object System.Net.Http.StringContent $bodyText, ([Text.Encoding]::UTF8), $ContentType }
    try {
        $response = $client.SendAsync($message).GetAwaiter().GetResult()
        $text = $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
        $status = [int]$response.StatusCode
        $record = [ordered]@{ testId=$Id; time=(Get-Date -Format 'yyyy-MM-ddTHH:mm:sszzz'); method=$Method; url=$url; requestBody=$bodyText; contentType=$ContentType; expectedStatus=$Expected; actualStatus=$status; responseBody=$text }
        $record | ConvertTo-Json -Depth 10 | Set-Content -LiteralPath "evidence/http/$Id.json" -Encoding UTF8
        $script:matrix.Add("| $Id | $Method $Path | $Expected | $status | $(if ($status -eq $Expected) {'PASS'} else {'FAIL'}) | [JSON](../evidence/http/$Id.json) |")
        Assert-Lab ($status -eq $Expected) "$Id expected $Expected but received $status : $text"
        Write-Host "$Id PASS ($status)"
        if ($text.StartsWith('[')) {
            foreach ($entry in (ConvertFrom-Json $text)) { Write-Output $entry }
            return
        }
        if ($text.StartsWith('{')) { return ConvertFrom-Json $text }
        return $text
    } finally {
        $message.Dispose()
        if ($response) { $response.Dispose() }
    }
}

try {
    Wait-LabApi
    $categories = @(Request-Lab 'PRE-CATEGORIES' GET '/api/categories' 200)
    Assert-Lab ($categories.Count -ge 2) 'Need at least two categories.'
    $firstId = [long]$categories[0].categoryId
    $secondId = [long]$categories[1].categoryId
    $name = 'Hoa lan Lab4 ' + (Get-Date -Format 'yyyyMMddHHmmss')
    $body = @{ orchidID=999999; orchidName=$name; isNatural=$true; orchidDescription='Hoa lan kiểm thử lưu bền vững'; orchidCategory=@{categoryId=$firstId}; isAttractive=$true; orchidURL='https://example.com/orchid.jpg' }
    $created = Request-Lab T05 POST '/api/orchids' 201 $body
    $orchidId = [long]$created.orchidID
    Assert-Lab ($orchidId -gt 0 -and $orchidId -ne 999999) 'Generated identity was not returned.'
    $list = @(Request-Lab T01 GET '/api/orchids' 200)
    Assert-Lab (@($list | Where-Object orchidID -eq $orchidId).Count -eq 1) 'Created orchid missing from list.'
    $search = @(Request-Lab T02 GET ('/api/orchids?name=' + [Uri]::EscapeDataString($name.ToUpperInvariant())) 200)
    Assert-Lab ($search.Count -eq 1 -and $search[0].orchidID -eq $orchidId) 'Case-insensitive search failed.'
    $read = Request-Lab T03 GET "/api/orchids/$orchidId" 200
    Assert-Lab ($read.orchidName -eq $name -and $read.orchidCategory.categoryId -eq $firstId -and $read.isNatural) 'POST postcondition failed.'
    $null = Request-Lab T04 GET '/api/orchids/999999' 404
    $null = Request-Lab 'T06-MISSING' POST '/api/orchids' 400 @{orchidName='Missing category'}
    $invalid = $body.Clone(); $invalid.orchidCategory = @{categoryId=999999}
    $null = Request-Lab 'T06-INVALID' POST '/api/orchids' 400 $invalid
    $blank = $body.Clone(); $blank.orchidName=' '
    $null = Request-Lab 'VALIDATION-BLANK' POST '/api/orchids' 400 $blank
    $body.orchidName = $name + ' Updated'; $body.orchidCategory = @{categoryId=$secondId}; $body.isNatural=$false; $body.isAttractive=$false
    $updated = Request-Lab T07 PUT "/api/orchids/$orchidId" 200 $body
    $read = Request-Lab 'T07-GET' GET "/api/orchids/$orchidId" 200
    Assert-Lab ($read.orchidName -eq $body.orchidName -and $read.orchidCategory.categoryId -eq $secondId -and -not $read.isNatural -and -not $read.isAttractive -and $read.orchidDescription -eq $body.orchidDescription -and $read.orchidURL -eq $body.orchidURL) 'PUT postcondition failed.'
    $null = Request-Lab T08 PUT '/api/orchids/999999' 404 $body
    $null = Request-Lab 'T07-INVALID' PUT "/api/orchids/$orchidId" 400 $invalid
    $read = Request-Lab 'T07-ROLLBACK' GET "/api/orchids/$orchidId" 200
    Assert-Lab ($read.orchidName -eq $body.orchidName) 'Rejected PUT changed data.'
    if ($VerifyDatabase) {
        $descriptionHex = ([BitConverter]::ToString([Text.Encoding]::Unicode.GetBytes($body.orchidDescription))).Replace('-', '')
        $query = "IF NOT EXISTS (SELECT 1 FROM orchids WHERE orchidid=$orchidId AND category_id=$secondId AND is_natural=0 AND is_attractive=0 AND CONVERT(VARBINARY(MAX),orchid_description)=0x$descriptionHex) THROW 51000, 'T11 row state or Unicode mismatch', 1; IF NOT EXISTS (SELECT 1 FROM sys.foreign_keys WHERE parent_object_id=OBJECT_ID('orchids') AND referenced_object_id=OBJECT_ID('orchid_categories')) THROW 51000, 'T11 FK missing', 1; SELECT orchidid,orchid_name,category_id,is_natural,is_attractive,CONVERT(VARCHAR(MAX),CONVERT(VARBINARY(MAX),orchid_description),2) AS description_utf16_hex FROM orchids WHERE orchidid=$orchidId;"
        Invoke-LabSql $query | Set-Content -LiteralPath 'evidence/sqlserver/T11-row.txt' -Encoding UTF8
        Invoke-LabSql (Get-Content -LiteralPath 'scripts/inspect-db.sql' -Raw) | Set-Content -LiteralPath 'evidence/sqlserver/schema-and-data.txt' -Encoding UTF8
        $script:matrix.Add('| T11 | SQL row/category_id, Unicode and schema | Updated row + FK + Unicode | SQL assertion passed | PASS | [row](../evidence/sqlserver/T11-row.txt), [schema](../evidence/sqlserver/schema-and-data.txt) |')
    }
    if ($Restart) {
        & "$PSScriptRoot/stop.ps1"
        & "$PSScriptRoot/run.ps1" -SkipBuild -UseH2Verification:$UseH2Verification
        $read = Request-Lab T12 GET "/api/orchids/$orchidId" 200
        Assert-Lab ($read.orchidName -eq $body.orchidName -and $read.orchidCategory.categoryId -eq $secondId) 'Data did not survive restart.'
        if ($VerifyDatabase) {
            Invoke-LabSql $query | Set-Content -LiteralPath 'evidence/sqlserver/T12-restart-row.txt' -Encoding UTF8
        }
    }
    $deleted = Request-Lab T09 DELETE "/api/orchids/$orchidId" 204
    Assert-Lab ([string]::IsNullOrEmpty($deleted)) '204 should have no response body.'
    $null = Request-Lab 'T09-GET' GET "/api/orchids/$orchidId" 404
    $null = Request-Lab T10 DELETE "/api/orchids/$orchidId" 404
    if ($VerifyDatabase) {
        Invoke-LabSql "IF EXISTS (SELECT 1 FROM orchids WHERE orchidid=$orchidId) THROW 51000, 'Delete left a row', 1; SELECT COUNT(*) AS deleted_row_count FROM orchids WHERE orchidid=$orchidId; SELECT category_id,category_name FROM orchid_categories WHERE category_id IN ($firstId,$secondId);" | Set-Content -LiteralPath 'evidence/sqlserver/T09-delete.txt' -Encoding UTF8
    }
    $null = Request-Lab 'B7-MALFORMED' POST '/api/orchids' 400 '{'
    $null = Request-Lab 'B7-MEDIA' POST '/api/orchids' 415 '{}' 'text/plain'
    $retest = Request-Lab 'B7-RETEST' POST '/api/orchids' 201 $body
    $null = Request-Lab 'B7-CLEANUP' DELETE ("/api/orchids/" + $retest.orchidID) 204
    $retest = Request-Lab 'B5-RETEST' POST '/api/orchids' 201 $body
    $null = Request-Lab 'B6-RETEST' GET ("/api/orchids/" + $retest.orchidID) 200
    $null = Request-Lab 'B5-CLEANUP' DELETE ("/api/orchids/" + $retest.orchidID) 204
    $null = Request-Lab 'B8-ROUTE' GET '/api/orchid' 404
    $null = Request-Lab 'B8-RETEST' GET '/api/orchids' 200
    $null = Request-Lab 'LEGACY-PATH' GET '/orchids/' 200
    Write-Host 'HTTP verification completed. Evidence saved under evidence/http and evidence/sqlserver.'
} finally {
    $client.Dispose()
    if (-not $VerifyDatabase) {
        $script:matrix.Add('| T11-SQL | SQL Server row / PK / FK | Valid persisted relationship | Not run | PENDING | Docker must be working |')
    }
    if ($UseH2Verification -or -not $Restart) {
        $script:matrix.Add('| T12-SQL | Restart SQL Server application | Same ID, name, Category | Not run | PENDING | H2 T12 above does not verify SQL Server |')
    }
    $database = if ($UseH2Verification) { 'H2 file database (diagnostic fallback; SQL Server unverified)' } else { 'SQL Server (confirm datasource in startup log)' }
    $lines = @('# Lab04 test matrix', '', ('Run: ' + (Get-Date -Format 'yyyy-MM-dd HH:mm:ss zzz')), ('Database: ' + $database), '', 'HTTP responses below come from the running application. SQL Server checks run only with -VerifyDatabase. T12 runs only with -Restart. See verification.md for remaining manual checks.', '', '| ID | Request / check | Expected | Actual | Result | Evidence |', '|---|---|---|---|---|---|') + $script:matrix
    $lines | Set-Content -LiteralPath 'docs/test-matrix.md' -Encoding UTF8
}
