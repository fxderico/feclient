# 67Client Spotify bridge - Windows SMTC (System Media Transport Controls).
# Polls the Spotify media session and emits one JSON line per state update on
# stdout; accepts commands on stdin: NEXT | PREV | PLAYPAUSE | SEEK <ms> | VOLUME <0-100>.
# SMTC has no volume control, so VOLUME goes through the Core Audio session API
# (Spotify's per-app level in the Windows mixer) instead.
# Exits when stdin closes (i.e. when the game quits). Pure PowerShell 5.1.
$ErrorActionPreference = 'SilentlyContinue'
Add-Type -AssemblyName System.Runtime.WindowsRuntime

$null = [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager, Windows.Media.Control, ContentType = WindowsRuntime]

$asTaskGeneric = ([System.WindowsRuntimeSystemExtensions].GetMethods() | Where-Object {
    $_.Name -eq 'AsTask' -and $_.GetParameters().Count -eq 1 -and $_.GetParameters()[0].ParameterType.Name -eq 'IAsyncOperation`1'
})[0]

function Await($WinRtTask, $ResultType) {
    $asTask = $asTaskGeneric.MakeGenericMethod($ResultType)
    $netTask = $asTask.Invoke($null, @($WinRtTask))
    $null = $netTask.Wait(5000)
    $netTask.Result
}

function Get-SpotifySession($mgr) {
    foreach ($s in $mgr.GetSessions()) {
        if ($s.SourceAppUserModelId -like '*spotify*') { return $s }
    }
    return $null
}

$mgr = Await ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager]::RequestAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionManager])
if ($null -eq $mgr) { [Console]::Out.WriteLine('{"fatal":"no session manager"}'); exit 1 }

$artPath = $args[0]
if (-not $artPath) { $artPath = Join-Path $env:TEMP '67client_spotify_art.img' }

# Load the Windows.Storage.Streams projection BEFORE resolving the stream
# extension overload, or the interface type is null this early in the session
$null = [Windows.Storage.Streams.RandomAccessStreamReference, Windows.Storage.Streams, ContentType = WindowsRuntime]
$asStreamMethod = [System.IO.WindowsRuntimeStreamExtensions].GetMethod('AsStreamForRead',
    [type[]]@([Windows.Storage.Streams.IRandomAccessStream]))

# Per-app volume lives in Core Audio, not SMTC. Define the minimal COM
# interfaces (vtable order preserved with unused-slot placeholders) and a
# helper that finds Spotify's audio session and reads/writes its level.
$hasVol = $false
try {
    Add-Type -Language CSharp @'
using System;
using System.Runtime.InteropServices;
using System.Diagnostics;
namespace SixSeven {
  [ComImport, Guid("BCDE0395-E52F-467C-8E3D-C4579291692E")]
  public class MMDeviceEnumerator { }
  [Guid("A95664D2-9614-4F35-A746-DE8DB63617E6"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown), ComImport]
  public interface IMMDeviceEnumerator {
    int f0();
    [PreserveSig] int GetDefaultAudioEndpoint(int dataFlow, int role, out IMMDevice ppDevice);
  }
  [Guid("D666063F-1587-4E43-81F1-B948E807363F"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown), ComImport]
  public interface IMMDevice {
    [PreserveSig] int Activate(ref Guid iid, int dwClsCtx, IntPtr pActivationParams, [MarshalAs(UnmanagedType.IUnknown)] out object ppInterface);
  }
  [Guid("77AA99A0-1BD6-484F-8BC7-2C654C9A9B6F"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown), ComImport]
  public interface IAudioSessionManager2 {
    int f0(); int f1();
    [PreserveSig] int GetSessionEnumerator(out IAudioSessionEnumerator SessionEnum);
  }
  [Guid("E2F5BB11-0570-40CA-ACDD-3AA01277DEE8"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown), ComImport]
  public interface IAudioSessionEnumerator {
    [PreserveSig] int GetCount(out int SessionCount);
    [PreserveSig] int GetSession(int SessionCount, out IAudioSessionControl2 Session);
  }
  [Guid("bfb7ff88-7239-4fc9-8fa2-07c950be9c6d"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown), ComImport]
  public interface IAudioSessionControl2 {
    int f0(); int f1(); int f2(); int f3(); int f4(); int f5(); int f6(); int f7(); int f8(); int f9(); int f10();
    [PreserveSig] int GetProcessId(out uint pRetVal);
    [PreserveSig] int IsSystemSoundsSession();
    int f13();
  }
  [Guid("87CE5498-68D6-44E5-9215-6DA47EF883D8"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown), ComImport]
  public interface ISimpleAudioVolume {
    [PreserveSig] int SetMasterVolume(float fLevel, ref Guid EventContext);
    [PreserveSig] int GetMasterVolume(out float pfLevel);
    [PreserveSig] int SetMute(int bMute, ref Guid EventContext);
    [PreserveSig] int GetMute(out int pbMute);
  }
  public static class SpotVol {
    static bool IsSpotify(uint pid) {
      if (pid == 0) return false;
      try { return string.Equals(Process.GetProcessById((int)pid).ProcessName, "Spotify", StringComparison.OrdinalIgnoreCase); }
      catch { return false; }
    }
    static IAudioSessionEnumerator Sessions() {
      var de = (IMMDeviceEnumerator)(new MMDeviceEnumerator());
      IMMDevice dev;
      if (de.GetDefaultAudioEndpoint(0, 1, out dev) != 0 || dev == null) return null;
      Guid iid = typeof(IAudioSessionManager2).GUID;
      object o;
      if (dev.Activate(ref iid, 1, IntPtr.Zero, out o) != 0 || o == null) return null;
      IAudioSessionEnumerator e;
      if (((IAudioSessionManager2)o).GetSessionEnumerator(out e) != 0) return null;
      return e;
    }
    public static int Get() {
      try {
        var e = Sessions(); if (e == null) return -1;
        int n; e.GetCount(out n);
        for (int i = 0; i < n; i++) {
          IAudioSessionControl2 c;
          if (e.GetSession(i, out c) != 0 || c == null) continue;
          uint pid; c.GetProcessId(out pid);
          if (IsSpotify(pid)) { float lvl; ((ISimpleAudioVolume)c).GetMasterVolume(out lvl); return (int)Math.Round(lvl * 100f); }
        }
      } catch { }
      return -1;
    }
    public static bool Set(int pct) {
      float lvl = Math.Max(0f, Math.Min(1f, pct / 100f));
      bool found = false;
      try {
        var e = Sessions(); if (e == null) return false;
        int n; e.GetCount(out n);
        Guid ctx = Guid.Empty;
        for (int i = 0; i < n; i++) {
          IAudioSessionControl2 c;
          if (e.GetSession(i, out c) != 0 || c == null) continue;
          uint pid; c.GetProcessId(out pid);
          if (IsSpotify(pid)) { ((ISimpleAudioVolume)c).SetMasterVolume(lvl, ref ctx); found = true; }
        }
      } catch { }
      return found;
    }
  }
}
'@
    $hasVol = $true
} catch { }

$lastTrack = ''
$artVersion = 0

# Console.In.ReadLineAsync runs SYNCHRONOUSLY on console streams in .NET
# Framework, so stdin is read on a background runspace into a queue instead.
$cmdQueue = New-Object 'System.Collections.Concurrent.ConcurrentQueue[string]'
$readerRunspace = [runspacefactory]::CreateRunspace()
$readerRunspace.Open()
$readerRunspace.SessionStateProxy.SetVariable('q', $cmdQueue)
$reader = [powershell]::Create()
$reader.Runspace = $readerRunspace
$null = $reader.AddScript('while ($true) { $l = [Console]::In.ReadLine(); if ($null -eq $l) { $q.Enqueue("__EOF__"); break }; $q.Enqueue($l) }')
$null = $reader.BeginInvoke()

while ($true) {
    # ---- commands (non-blocking) --------------------------------------
    $line = $null
    while ($cmdQueue.TryDequeue([ref]$line)) {
        if ($line -eq '__EOF__') { exit 0 }  # stdin closed -> game gone
        $parts = $line.Trim() -split ' '
        $s = Get-SpotifySession $mgr
        if ($s) {
            switch ($parts[0].ToUpper()) {
                'NEXT' { $null = Await ($s.TrySkipNextAsync()) ([bool]) }
                'PREV' { $null = Await ($s.TrySkipPreviousAsync()) ([bool]) }
                'PLAYPAUSE' { $null = Await ($s.TryTogglePlayPauseAsync()) ([bool]) }
                'SEEK' {
                    if ($parts.Count -ge 2) {
                        $ticks = [long]$parts[1] * 10000
                        $null = Await ($s.TryChangePlaybackPositionAsync($ticks)) ([bool])
                    }
                }
                'VOLUME' {
                    if ($hasVol -and $parts.Count -ge 2) {
                        $null = [SixSeven.SpotVol]::Set([int]$parts[1])
                    }
                }
            }
        }
    }

    # ---- state ---------------------------------------------------------
    $s = Get-SpotifySession $mgr
    if ($null -eq $s) {
        [Console]::Out.WriteLine('{"active":false}')
    } else {
        $props = Await ($s.TryGetMediaPropertiesAsync()) ([Windows.Media.Control.GlobalSystemMediaTransportControlsSessionMediaProperties])
        if ($props) {
            $tl = $s.GetTimelineProperties()
            $pb = $s.GetPlaybackInfo()
            $playing = $pb.PlaybackStatus -eq [Windows.Media.Control.GlobalSystemMediaTransportControlsSessionPlaybackStatus]::Playing
            $posMs = [long]$tl.Position.TotalMilliseconds
            if ($playing) {
                $posMs += [long]([DateTimeOffset]::UtcNow - $tl.LastUpdatedTime.ToUniversalTime()).TotalMilliseconds
            }
            $track = "$($props.Title)|$($props.Artist)"

            if ($track -ne $lastTrack -and $props.Thumbnail) {
                try {
                    $streamRaw = Await ($props.Thumbnail.OpenReadAsync()) ([Windows.Storage.Streams.IRandomAccessStreamWithContentType])
                    # PS can't cast the RCW; reflection Invoke QIs through the CLR
                    $netStream = $asStreamMethod.Invoke($null, @($streamRaw))
                    $fs = [IO.File]::Create($artPath)
                    $netStream.CopyTo($fs)
                    $fs.Dispose()
                    $netStream.Dispose()
                    $artVersion++
                } catch { }
            }
            $lastTrack = $track

            $vol = -1
            if ($hasVol) { $vol = [SixSeven.SpotVol]::Get() }

            $obj = @{
                active = $true
                title = "$($props.Title)"
                artist = "$($props.Artist)"
                posMs = $posMs
                durMs = [long]$tl.EndTime.TotalMilliseconds
                playing = $playing
                canSeek = [bool]$pb.Controls.IsPlaybackPositionEnabled
                artV = $artVersion
                vol = $vol
            }
            [Console]::Out.WriteLine(($obj | ConvertTo-Json -Compress))
        }
    }
    [Console]::Out.Flush()
    Start-Sleep -Milliseconds 600
}
