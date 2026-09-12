# release.ps1 — tag current commit and push a GitHub release with the JAR
# Usage: .\release.ps1 [version]   e.g.  .\release.ps1 1.0.1
param([string]$Version = "")

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

# ── 1. determine version ──────────────────────────────────────���───────────────
if (-not $Version) {
    # auto-increment patch from last tag
    $last = git describe --tags --abbrev=0 2>$null
    if ($last -match "^v?(\d+)\.(\d+)\.(\d+)") {
        $Version = "$($Matches[1]).$($Matches[2]).$([int]$Matches[3] + 1)"
    } else {
        $Version = "1.0.0"
    }
}
$Tag = "v$Version"
Write-Host "Releasing $Tag" -ForegroundColor Cyan

# ── 2. build ──────────────────────────────────────────────────────────────────
Write-Host "Building..." -ForegroundColor Yellow
.\gradlew.bat build -x test --no-daemon
if ($LASTEXITCODE -ne 0) { throw "Build failed" }

$Jar = Get-ChildItem "build/libs" -Filter "feclient-*.jar" |
       Where-Object { $_.Name -notmatch "sources" } |
       Sort-Object LastWriteTime -Descending |
       Select-Object -First 1
if (-not $Jar) { throw "No JAR found in build/libs" }
Write-Host "JAR: $($Jar.Name)" -ForegroundColor Green

# ── 3. commit + tag ───────────────────────────────────────────────────────────
git add -A
$dirty = git status --porcelain
if ($dirty) {
    git commit -m "release: $Tag"
}
git tag $Tag
Write-Host "Tagged $Tag" -ForegroundColor Green

# ── 4. push ────────────────────────────────────────────────────���──────────────
git push origin master
git push origin $Tag
Write-Host "Pushed to GitHub" -ForegroundColor Green

# ── 5. create GitHub release with JAR ────────────────────────────────────────
$remote = git remote get-url origin
if ($remote -match "github\.com[:/](.+?)(?:\.git)?$") {
    $repo = $Matches[1]   # e.g. fxderico/feclient
    Write-Host "Creating GitHub release for $repo..." -ForegroundColor Yellow

    # Use gh CLI if available, otherwise fall back to API with stored creds
    $ghPath = (Get-Command gh -ErrorAction SilentlyContinue)?.Source
    if ($ghPath) {
        gh release create $Tag $Jar.FullName `
            --title "feclient $Tag" `
            --notes "Minecraft 1.21.1 Fabric — see README for install." `
            --repo $repo
        Write-Host "Release created via gh CLI" -ForegroundColor Green
    } else {
        Write-Host "gh CLI not found — upload the JAR manually at:" -ForegroundColor Yellow
        Write-Host "  https://github.com/$repo/releases/new?tag=$Tag"
        Write-Host "  JAR: $($Jar.FullName)"
    }
} else {
    Write-Host "Could not detect GitHub repo from remote: $remote" -ForegroundColor Red
}

Write-Host "`nDone! $Tag released." -ForegroundColor Cyan
