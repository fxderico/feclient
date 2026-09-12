# smtc-now-playing.ps1 — one-shot: reads Windows SMTC and prints single-line JSON to stdout.
# Spawned per-poll by SmtcMediaClient. Exits immediately after printing.

# Force UTF-8 on stdout. Windows PowerShell 5.1 defaults to UTF-16LE-with-BOM
# on the console output stream, which lands in the Java subprocess reader as
# garbage bytes and breaks JSON parsing. pwsh 7 varies by host. Pin it here
# so both hosts emit the same bytes.
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
$OutputEncoding           = [System.Text.Encoding]::UTF8
$PSDefaultParameterValues['Out-File:Encoding'] = 'utf8'

$ErrorActionPreference = 'Stop'
try {
    # Force-load the WinRT projection helpers first. Without this Add-Type
    # PS 5.1 can't resolve [System.WindowsRuntimeSystemExtensions] (the class
    # lives in System.Runtime.WindowsRuntime.dll, which is not auto-loaded
    # into a -NoProfile session). Once loaded, both AsTask<T>(IAsyncOperation<T>)
    # and IAsyncOperation<> itself become reachable.
    Add-Type -AssemblyName System.Runtime.WindowsRuntime

    # Force-load the WinRT types. IMPORTANT: PS 5.1's parser rejects backtick
    # line-continuation INSIDE a type-literal '[…]', so each declaration must
    # sit on ONE physical line — splitting the "Type, Assembly, ContentType=…"
    # tuple with a `-newline` yields "Missing ] at end of attribute or type
    # literal" and the whole script aborts at parse time (never runs).
    [void][Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType=WindowsRuntime]
    [void][Windows.Foundation.IAsyncOperation`1, Windows.Foundation, ContentType=WindowsRuntime]
    [void][Windows.Storage.Streams.IRandomAccessStreamWithContentType, Windows.Storage.Streams, ContentType=WindowsRuntime]

    # Resolve the AsStream(IRandomAccessStream) extension method once. PS 5.1
    # can't do overload-resolution against a System.__ComObject argument (the
    # WinRT stream projections all come through as raw COM), so calling it
    # directly with $stream.AsStream() throws "does not contain a method
    # named". Reflecting the MethodInfo out of WindowsRuntimeStreamExtensions
    # and Invoke()ing it bypasses PS's overload check — the CLR then does the
    # real COM→interface projection cleanly.
    $script:asStreamFromRAS = [System.IO.WindowsRuntimeStreamExtensions].GetMethods() |
        Where-Object { $_.Name -eq 'AsStream' -and
                       $_.GetParameters().Count -eq 1 -and
                       $_.GetParameters()[0].ParameterType.Name -eq 'IRandomAccessStream' } |
        Select-Object -First 1

    # IAsyncOperation<T> -> T. Uses WindowsRuntimeSystemExtensions.AsTask.
    function Await($op, $resultType) {
        $asTask = [System.WindowsRuntimeSystemExtensions].GetMethods() |
                  Where-Object { $_.Name -eq 'AsTask' -and
                                 $_.GetParameters().Count -eq 1 -and
                                 $_.GetGenericArguments().Count -eq 1 } |
                  Select-Object -First 1
        $t = $asTask.MakeGenericMethod($resultType).Invoke($null, @($op))
        $t.Wait(-1) | Out-Null
        $t.Result
    }

    $mgrType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]
    $mgr = Await ($mgrType::RequestAsync()) $mgrType
    $sess = $mgr.GetCurrentSession()
    if ($null -eq $sess) { Write-Output '{"playing":false}'; return }

    $playback = $sess.GetPlaybackInfo()
    $timeline = $sess.GetTimelineProperties()
    # capturedAtMs anchors the position reading to a specific wall-clock so
    # Java can extrapolate accurately. Taken IMMEDIATELY after the timeline
    # read (not at PS start, not at output write) so we're pinning the closest
    # possible moment to when SMTC actually gave us positionMs. Java rebases
    # every render frame off this instead of the parse time — otherwise the
    # bar lags the song by however long WinRT init + Await take on this poll.
    $capturedAtMs = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
    $propType = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties]
    $props    = Await ($sess.TryGetMediaPropertiesAsync()) $propType

    $isPlaying = ($playback.PlaybackStatus.ToString() -eq 'Playing')

    # ── Album-art dump ──────────────────────────────────────────────────
    # SMTC's Thumbnail is an IRandomAccessStreamReference. Open it, project
    # the WinRT stream through AsStream (via reflection — see the resolve
    # block above for the "why"), copy into a MemoryStream, dump to a fixed
    # temp file. Hand Java both the path and an artKey (SHA-1 prefix of
    # title|artist) so the Java texture cache can invalidate cleanly on
    # track change without diffing bytes. Silent-fail: art is optional; a
    # broken thumbnail must not blow up the whole poll.
    $artPath = ''
    $artKey  = ''
    try {
        if ($props.Thumbnail -ne $null -and $null -ne $script:asStreamFromRAS) {
            $streamType = [Windows.Storage.Streams.IRandomAccessStreamWithContentType]
            $stream = Await ($props.Thumbnail.OpenReadAsync()) $streamType
            if ($stream -ne $null) {
                $netStream = $script:asStreamFromRAS.Invoke($null, @($stream))
                try {
                    if ($netStream.Length -gt 0 -and $netStream.Length -lt 8MB) {
                        $ms = New-Object System.IO.MemoryStream
                        try {
                            $netStream.CopyTo($ms)
                            $bytes = $ms.ToArray()
                        } finally { $ms.Dispose() }

                        $tempDir = [System.IO.Path]::GetTempPath()
                        $artPath = [System.IO.Path]::Combine($tempDir, 'codeengine-smtc-art.dat')
                        [System.IO.File]::WriteAllBytes($artPath, $bytes)

                        $keySrc = ("{0}|{1}" -f $props.Title, $props.Artist)
                        $sha    = New-Object System.Security.Cryptography.SHA1Managed
                        $hash   = $sha.ComputeHash([System.Text.Encoding]::UTF8.GetBytes($keySrc))
                        $artKey = [System.BitConverter]::ToString($hash).Replace('-','').Substring(0,16)
                    }
                } finally { $netStream.Dispose() }
            }
        }
    } catch {
        # swallow — leave artPath/artKey empty so Java falls back to placeholder
    }

    $obj = [ordered]@{
        playing    = $isPlaying
        title      = if ($props.Title)      { $props.Title }      else { '' }
        artist     = if ($props.Artist)     { $props.Artist }     else { '' }
        album      = if ($props.AlbumTitle) { $props.AlbumTitle } else { '' }
        positionMs = [long]$timeline.Position.TotalMilliseconds
        durationMs = [long]$timeline.EndTime.TotalMilliseconds
        source        = if ($sess.SourceAppUserModelId) { $sess.SourceAppUserModelId } else { '' }
        artPath       = $artPath
        artKey        = $artKey
        capturedAtMs  = $capturedAtMs
    }
    Write-Output ($obj | ConvertTo-Json -Compress -Depth 3)
}
catch {
    # Sanitise the message so it survives being embedded in JSON.
    $msg = ($_.Exception.Message -replace '["\r\n\t]', ' ')
    Write-Output ('{"error":"' + $msg + '"}')
}
