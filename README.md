# Android UI Test Tool Comparison

A small, reproducible benchmark comparing how fast four Android UI‑testing tools drive
the **same flow** against the **same app**:

| Tool | How it drives the app |
|---|---|
| **mobilewright** (agent backend) | Playwright‑style runner → mobilecli → on‑device agent (`UiAutomation`) |
| **mobilewright** (adb backend) | Playwright‑style runner → mobilecli → `adb` / `uiautomator dump` |
| **Appium** | UiAutomator2 driver over HTTP (WebdriverIO client) |
| **Kakao / Espresso** | In‑process instrumentation (Gradle `connectedAndroidTest`) |
| **Maestro** | YAML flow, hierarchy‑based driver |

Everything drives one deterministic test app (`app/`) with labelled views, so each tool
runs an identical script and the only variable is the tool itself.

> The two mobilewright backends differ only in the on‑device transport: the **agent**
> backend reads the view hierarchy via `UiAutomation` in‑process (~3 ms) instead of
> shelling out to `uiautomator dump` (~800 ms). It comes from
> [mobilecli PR #286](https://github.com/mobile-next/mobilecli/pull/286).

## Test devices

| | Local emulator | Real device |
|---|---|---|
| Device | Pixel 5 (AVD, `sdk_gphone64_arm64`) | Pixel 7 |
| OS | Android 13 (API 33) | Android 16 (API 36) |
| ABI | arm64‑v8a | arm64‑v8a |
| Screen | 1080×2340 @440dpi | 1080×2400 |
| Transport | in‑memory (localhost) | adb over USB 3.2 Gen 2 (SuperSpeed+, 10 Gbps) |
| Animations | **disabled** | **disabled** |

Host machine: macOS 26.3 (Apple Silicon, arm64).

Disabling animations matters for Espresso (it waits for the UI to be idle); the other
tools are unaffected. Set all three scales to 0 before running:

```bash
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0
```

## The test app & flow

`app/` is a single‑activity app (`dev.mobilewright.testapp`) wrapped in a `ScrollView`,
exposing one verifiable result per basic interaction. Every view has a
`contentDescription` so all tools can locate it the same way.

The benchmark flow exercises the four basic interactions, each with a verification:

| # | Interaction | Step | Verification |
|---|---|---|---|
| 1 | **tap** | tap `TAP ME` | text `Count: 1` |
| 2 | **type text** | type `hello` into `input-field`, tap `ECHO` | text `Echo: hello` |
| 3 | **long press** | long‑press `LONG PRESS HERE` | text `Long: pressed` |
| 4 | **swipe / scroll** | scroll to the off‑screen `bottom-marker` | `BOTTOM REACHED` visible |

Each operation (action and verification) is timed **separately**, run **6 iterations**,
reported as **median (min–max)**. Tools run **one at a time** (never concurrently) for
stable numbers.

## Results — per operation, median (min–max) ms

| Operation | mw agent | mw adb | Appium | Kakao | Maestro |
|---|---|---|---|---|---|
| **tap** | 69 (63–463) | 1697 (1687–1740) | 404 (380–452) | 296 (290–405) | 2434 (2371–2725) |
| verify text | 32 (23–124) | 856 (840–870) | 126 (117–429) | **5 (1–11)** | 81 (42–94) |
| **type text** | **76 (72–159)** | 1812 (1757–1833) | 1094 (593–1134) | 721 (716–985) | 2083 (2015–2095) |
| tap (echo) | 134 (107–164) | 1925 (1893–1945) | 559 (551–580) | 300 (281–308) | 2125 (2030–2370) |
| verify text | 29 (26–131) | 931 (909–967) | 537 (505–547) | **6 (2–8)** | 107 (72–127) |
| **long press** | 575 (570–579) | 2438 (2396–2843) | 1244 (1218–1282) | 757 (737–787) | 4492 (4401–4762) |
| verify text | 23 (13–27) | 926 (918–958) | 40 (37–204) | **5 (2–6)** | 102 (70–124) |
| **swipe / scroll** | 1540 (1505–1582) | 3178 (3169–3194) | 688 (677–726) | **5 (2–12)** | 4132 (3924–4482) |
| verify visible | 7 (3–11) | 896 (893–917) | 102 (83–125) | **2 (1–3)** | 117 (85–177) |
| **flow total (9 ops)** | **~2.6 s** | ~14.8 s | ~4.9 s | **~2.4 s** | ~16.3 s |

App launch / tool startup is **excluded** (it isn't measured consistently across tools —
e.g. Maestro's ~14.6 s JVM+driver init, Appium's session create, Kakao's
`ActivityScenario.launch`).

### How to read it
- **Locate / verify** ops split by architecture: in‑process Kakao (2–6 ms, direct view
  read) ≪ mobilewright‑agent (3 ms dump) ≪ Appium (warm a11y cache) ≪ adb / Maestro
  (a full hierarchy dump, ~0.8–1 s, every time).
- **Kakao gestures look slow (~300 ms)** because Espresso synchronizes the main thread
  after every action — a raw `onView().perform(click())` is ~450 ms, *slower* than
  Kakao's wrapper, so it's Espresso core, not Kakao, and not animations.
- **mobilewright‑agent's only slow op is swipe** — `scrollIntoViewIfNeeded` iterates
  swipe→dump until the element is on screen.
- **Maestro** is slowest per action: it dumps the hierarchy, taps, then **re‑dumps to
  confirm the UI changed**, so ~2 dumps + settle per gesture.

## How to run each tool

### 0. Build & install the test app (shared by mobilewright / Appium / Maestro)
```bash
cd app
sh build.sh                 # needs JDK + Android SDK build-tools
adb install -r mw-testapp.apk
```
Package `dev.mobilewright.testapp`, launcher `.MainActivity`.

### Maestro
```bash
# install: curl -fsSL "https://get.maestro.mobile.dev" | bash
maestro test maestro/comprehensive.yaml

# per-step timings (duration per command, in ms):
maestro test --debug-output out maestro/comprehensive.yaml
#   -> out/.maestro/tests/<ts>/commands-(comprehensive.yaml).json   (metadata.duration)
```

### Appium
```bash
cd appium
npm install
npx appium driver install uiautomator2
npx appium &                # start server on :4723
node bench.mjs              # 6-iteration per-op median/min/max
# edit the udid in bench.mjs if your device isn't emulator-5554
```

### mobilewright
```bash
cd mobilewright
npm install                 # see https://github.com/mobile-next/mobilewright

# idiomatic test (auto-starts the mobilecli server):
npm test

# per-op benchmark harness (needs a running mobilecli server):
mobilecli server start --listen localhost:12000 &
node bench.mjs              # set deviceId in bench.mjs

# agent backend  = default (requires mobilecli built from PR #286)
# adb backend    = MOBILECLI_DISABLE_AGENT=1 mobilecli server start ...
```

### Kakao / Espresso
```bash
cd kakao
./gradlew :app:connectedDebugAndroidTest
# per-action timings are logged to logcat under tag MWBENCH:
adb logcat -d -s MWBENCH
```
The Kakao project builds its **own** copy of the app and tests it in‑process, so it does
not need the prebuilt APK.

## Notes
- `bench.mjs` (mobilewright/appium) and the Kakao test loop the flow 6× internally and
  emit median/min/max; Maestro is looped externally and aggregated from its command JSON.
- Device IDs are hard‑coded to this setup (`Pixel_5_API_33` / `emulator-5554`) — change
  them for your device.
- Numbers are emulator‑specific; absolute values will differ on real hardware, but the
  relative ordering (dump cost, in‑process vs out‑of‑process) holds.
