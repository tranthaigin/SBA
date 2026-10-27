$ErrorActionPreference = "Stop"

$securePassword = Read-Host "SQL Server sa password" -AsSecureString
if ($securePassword.Length -eq 0) {
    throw "SQL Server password cannot be empty."
}

$passwordPointer = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($securePassword)
$keyBytes = New-Object byte[] 32
$random = [Security.Cryptography.RandomNumberGenerator]::Create()

try {
    $env:DB_PASSWORD = [Runtime.InteropServices.Marshal]::PtrToStringBSTR($passwordPointer)
    $random.GetBytes($keyBytes)
    $env:JWT_SECRET = [Convert]::ToBase64String($keyBytes)

    & mvn spring-boot:run
    if ($LASTEXITCODE -ne 0) {
        throw "Spring Boot stopped with exit code $LASTEXITCODE."
    }
}
finally {
    [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($passwordPointer)
    [Array]::Clear($keyBytes, 0, $keyBytes.Length)
    $random.Dispose()
    Remove-Item Env:DB_PASSWORD -ErrorAction SilentlyContinue
    Remove-Item Env:JWT_SECRET -ErrorAction SilentlyContinue
}
