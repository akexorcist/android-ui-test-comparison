# Android UI Test Tool Comparison

Benchmark comparing how fast five Android UI testing tools run the same flow against the same app. The only variable is the tool.

| Tool | How it drives the app |
|---|---|
| mobilewright (agent backend) | Playwright-style runner → mobilecli → on-device agent (`UiAutomation`) — speed improvement from [mobilecli PR #286](https://github.com/mobile-next/mobilecli/pull/286) |
| mobilewright (adb backend) | Playwright-style runner → mobilecli → `adb` / `uiautomator dump` |
| Appium | UiAutomator2 driver over HTTP (WebdriverIO client) |
| Kakao / Espresso | In-process instrumentation (`connectedAndroidTest`) |
| Maestro | YAML flow, hierarchy-based driver |

## Test devices

| | Local emulator | Real device |
|---|---|---|
| Device | Pixel 5 (AVD) | Pixel 7 |
| OS | Android 13 (API 33) | Android 16 (API 36) |
| Animations | disabled | disabled |

Host: macOS 26.3, Apple Silicon.

Disable animations before running (required for Espresso):

```bash
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

## Test flow

`app/` is a single-activity app. Every view has a `contentDescription` so all tools locate elements the same way.

| # | Interaction | Action | Verification |
|---|---|---|---|
| 1 | tap | tap `TAP ME` | text `Count: 1` |
| 2 | type text | type `hello` into `input-field`, tap `ECHO` | text `Echo: hello` |
| 3 | long press | long-press `LONG PRESS HERE` | text `Long: pressed` |
| 4 | swipe / scroll | scroll to off-screen `bottom-marker` | `BOTTOM REACHED` visible |

Each step (action + verification) is timed separately over 6 iterations, reported as median (min–max). Tools run one at a time.

## Results — per operation, median (min–max) ms

App launch and tool startup are excluded.

| Operation | Appium | Kakao | Maestro | mw adb | mw agent |
|---|---|---|---|---|---|
| tap | 404 (380–452) | 296 (290–405) | 2434 (2371–2725) | 1697 (1687–1740) | 69 (63–463) |
| verify text | 126 (117–429) | 5 (1–11) | 81 (42–94) | 856 (840–870) | 32 (23–124) |
| type text | 1094 (593–1134) | 721 (716–985) | 2083 (2015–2095) | 1812 (1757–1833) | 76 (72–159) |
| tap (echo) | 559 (551–580) | 300 (281–308) | 2125 (2030–2370) | 1925 (1893–1945) | 134 (107–164) |
| verify text | 537 (505–547) | 6 (2–8) | 107 (72–127) | 931 (909–967) | 29 (26–131) |
| long press | 1244 (1218–1282) | 757 (737–787) | 4492 (4401–4762) | 2438 (2396–2843) | 575 (570–579) |
| verify text | 40 (37–204) | 5 (2–6) | 102 (70–124) | 926 (918–958) | 23 (13–27) |
| swipe / scroll | 688 (677–726) | 5 (2–12) | 4132 (3924–4482) | 3178 (3169–3194) | 1540 (1505–1582) |
| verify visible | 102 (83–125) | 2 (1–3) | 117 (85–177) | 896 (893–917) | 7 (3–11) |
| flow total (9 ops) | ~4.9 s | ~2.4 s | ~16.3 s | ~14.8 s | ~2.6 s |

## How to run

### 0. Build & install the test app (mobilewright, Appium, Maestro)

```bash
cd app
sh build.sh
adb install -r mw-testapp.apk
```

### Maestro

```bash
maestro test maestro/comprehensive.yaml

# with per-step timings:
maestro test --debug-output out maestro/comprehensive.yaml
```

### Appium

```bash
cd appium
npm install
npx appium driver install uiautomator2
npx appium &
node bench.mjs
```

### mobilewright

```bash
cd mobilewright
npm install

# run tests (auto-starts mobilecli server):
npm test

# benchmark harness:
mobilecli server start --listen localhost:12000 &
node bench.mjs
```

### Kakao / Espresso

```bash
cd kakao
./gradlew :app:connectedDebugAndroidTest
adb logcat -d -s MWBENCH
```

Kakao builds its own copy of the app — no prebuilt APK needed.
