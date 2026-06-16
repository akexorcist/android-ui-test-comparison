# Android UI Test Tool Comparison — Results

Per-operation timing for **mobilewright** (agent & adb backends), **Appium**,
**Kakao/Espresso**, and **Maestro**, running the **same flow** on the **same app**
across **three device environments**.

- **Methodology:** each operation timed separately, **6 iterations**, reported as
  **median (min–max) ms**. Tools run **one at a time** (never concurrently).
  **Animations disabled** on every device. App launch / tool startup is **excluded**
  (not measured consistently across tools).
- **Flow total** = sum of the 9 operation medians.

## Test environments

| | Local emulator | Remote Genymotion (cloud) | Real device (USB) |
|---|---|---|---|
| Device | Pixel 5 (AVD) | Genymotion "Phone" | Pixel 7 |
| Android | 13 (**API 33**) | 15 (**API 35**) | 16 (**API 36**) |
| ABI | arm64‑v8a | arm64‑v8a | arm64‑v8a |
| Screen | 1080×2340 @440dpi | 720×1520 | 1080×2400 |
| Transport | in‑memory (localhost) | **adb over network** (AWS) | **adb over USB 3.2 Gen 2** (SuperSpeed+, 10 Gbps) |
| Hosting | local Mac | AWS EC2 | physical, USB‑tethered |

### Genymotion cloud instance (AWS)

| | |
|---|---|
| EC2 instance type | **`m6g.large`** (AWS Graviton2, arm64, 2 vCPU / 8 GB) |
| Connection | adb over TCP/IP (network, via VPN) |

Host machine for all runs: macOS 26.3 (Apple Silicon, arm64).

## Test flow (what is measured)

Single‑activity test app (`dev.mobilewright.testapp`) in a `ScrollView`; every view has a
`contentDescription`. Four basic interactions, each with a verification:

| # | Interaction | Step | Verification |
|---|---|---|---|
| 1 | **tap** | tap `TAP ME` | text `Count: 1` |
| 2 | **type text** | type `hello` into `input-field`, tap `ECHO` | text `Echo: hello` |
| 3 | **long press** | long‑press `LONG PRESS HERE` | text `Long: pressed` |
| 4 | **swipe / scroll** | scroll to off‑screen `bottom-marker` | `BOTTOM REACHED` visible |

---

## 1) Local emulator — Pixel 5, API 33 — median (min–max) ms

| Operation | mw agent | mw adb | Appium | Kakao | Maestro |
|---|---|---|---|---|---|
| tap | 69 (63–463) | 1697 (1687–1740) | 404 (380–452) | 296 (290–405) | 2434 (2371–2725) |
| verify text | 32 (23–124) | 856 (840–870) | 126 (117–429) | **5 (1–11)** | 81 (42–94) |
| type text | **76 (72–159)** | 1812 (1757–1833) | 1094 (593–1134) | 721 (716–985) | 2083 (2015–2095) |
| tap (echo) | 134 (107–164) | 1925 (1893–1945) | 559 (551–580) | 300 (281–308) | 2125 (2030–2370) |
| verify text | 29 (26–131) | 931 (909–967) | 537 (505–547) | **6 (2–8)** | 107 (72–127) |
| long press | 575 (570–579) | 2438 (2396–2843) | 1244 (1218–1282) | 757 (737–787) | 4492 (4401–4762) |
| verify text | 23 (13–27) | 926 (918–958) | 40 (37–204) | **5 (2–6)** | 102 (70–124) |
| swipe / scroll | 1540 (1505–1582) | 3178 (3169–3194) | 688 (677–726) | **5 (2–12)** | 4132 (3924–4482) |
| verify visible | 7 (3–11) | 896 (893–917) | 102 (83–125) | **2 (1–3)** | 117 (85–177) |
| **flow total** | **~2.6 s** | ~14.8 s | ~4.9 s | **~2.4 s** | ~16.3 s |

---

## 2) Remote Genymotion (AWS `m6g.large`) — API 35, over network — median (min–max) ms

| Operation | mw agent | mw adb | Appium | Kakao | Maestro |
|---|---|---|---|---|---|
| tap | 2063 (2018–9755) | 1996 (1951–2018) | 1792 (764–2159) | 284 (276–509) | 4177 (4070–4306) |
| verify text | 991 (981–1029) | 998 (967–1110) | 511 (490–560) | 1.5 (1–2) | 242 (227–288) |
| type text | 2248 (2182–2506) | 2308 (2174–2436) | 1117 (645–1193) | 752 (734–802) | 1359 (1311–1459) |
| tap (echo) | 2275 (2204–2379) | 2268 (2189–2388) | 594 (543–614) | 284 (279–304) | 2826 (2718–3875) |
| verify text | 1140 (1110–1176) | 1094 (1090–1170) | 551 (537–573) | 2 (1–15) | 249 (237–269) |
| long press | 2745 (2705–3168) | 2775 (2725–2864) | 1031 (1018–1090) | 735 (731–749) | 5347 (5319–5428) |
| verify text | 1049 (1037–1060) | 1044 (1036–1104) | 114 (112–145) | 2 (1–2) | 266 (236–321) |
| swipe / scroll | 6557 (6481–6644) | 6513 (6411–6602) | 1735 (1717–1738) | 2 (2–3) | 6044 (5860–6364) |
| verify visible | 1015 (991–1042) | 1025 (1013–1117) | 120 (117–135) | 1 (1–2) | 246 (239–260) |
| **flow total** | ~20.1 s | ~20.0 s | ~7.6 s | ~2.1 s | ~23.7 s |

> **Note — network round‑trip.** Every out‑of‑process operation crosses the network to the
> AWS instance (~1 s per round‑trip), so that latency dominates the per‑op time and the
> agent's local dump advantage disappears (agent ≈ adb). Only Kakao (in‑process, runs
> on‑device) is unaffected.

---

## 3) Real device — Pixel 7, API 36, over USB — median (min–max) ms

| Operation | mw agent¹ | mw adb | Appium | Kakao | Maestro |
|---|---|---|---|---|---|
| tap | 2293 (2098–2355) | 2248 (2183–2349) | 1279 (618–1295) | 295 (284–313) | 3389 (3191–3752) |
| verify text | 1092 (1004–1111) | 1077 (1050–1101) | 124 (80–579) | 9 (3–27) | 248 (139–283) |
| type text | 2383 (2247–2626) | 2411 (2265–2467) | 1190 (692–1202) | 899 (726–1017) | 2074 (2049–2268) |
| tap (echo) | 2794 (2468–2872) | 2821 (2751–2900) | 1131 (661–1149) | 301 (289–507) | 3520 (3136–3738) |
| verify text | 1354 (1299–1389) | 1317 (1247–1368) | 122 (95–599) | 7 (5–14) | 516 (444–603) |
| long press | 3337 (3215–3642) | 3254 (3208–3487) | 1485 (1393–2073) | 747 (738–781) | 5954 (5884–6502) |
| verify text | 1340 (1207–1381) | 1385 (1257–1399) | 91 (77–102) | 7 (5–23) | 445 (386–528) |
| swipe / scroll | 4115 (3977–4257) | 4204 (3927–4296) | 858 (835–900) | 9 (6–14) | 6853 (6680–7450) |
| verify visible | 1405 (1273–1523) | 1404 (1263–1574) | 119 (86–164) | 5 (3–7) | 502 (406–712) |
| **flow total** | ~20.1 s | ~20.1 s | ~6.4 s | ~2.3 s | ~26.9 s |

¹ On **Android 16 the on‑device agent is reaped** (`app_process` can't hold
`UiAutomation`), so mobilewright‑agent silently **falls back to adb** — the two columns are
effectively identical. Kakao on API 36 also required **Espresso 3.7.0** (3.6.1 throws
`NoSuchMethodException: InputManager.getInstance`, removed in Android 15/16).

---

## Cross‑environment comparison — flow total (9 ops)

| Tool | Local emu (API 33) | Remote Genymotion (API 35, net) | Real Pixel 7 (API 36, USB) |
|---|---:|---:|---:|
| **Kakao** (in‑process) | ~2.4 s | ~2.1 s | ~2.3 s |
| mobilewright **agent** | **~2.6 s** | ~20.1 s | ~20.1 s (reaped→adb) |
| mobilewright **adb** | ~14.8 s | ~20.0 s | ~20.1 s |
| **Appium** | ~4.9 s | ~7.6 s | ~6.4 s |
| **Maestro** | ~16.3 s | ~23.7 s | ~26.9 s |

### Raw primitive cost (mobilewright, local emulator)

| Primitive | agent | adb | ratio |
|---|---:|---:|---:|
| UI dump (read hierarchy) | 2.8 ms | 783 ms | ~280× |
| tap injection | 2.1 ms | 38.6 ms | ~18× |

(`uiautomator dump` on the real Pixel 7 is ~2.2 s — even slower than the emulator.)

## Findings

1. **Kakao (in‑process) is constant (~2.3 s) across all three environments.** It runs on
   the device, so transport (in‑memory / network / USB) and the dump bottleneck never touch
   per‑operation time. Its verifications are 2–9 ms everywhere. It wins decisively on
   remote and real devices.
2. **The on‑device‑agent advantage is environment‑fragile.** Huge locally (2.6 s vs adb's
   14.8 s, from a 280× faster dump), it **disappears remotely** (the ~1 s network round‑trip
   dominates both backends) and **does not run at all on Android 16** (agent reaped → adb).
   It only pays off in its sweet spot: a local, older‑API emulator.
3. **Dump‑based tools (mw‑adb, Maestro) scale with dump cost**, which is worst on the real
   device (~2.2 s per `uiautomator dump`); Maestro additionally re‑dumps to confirm each
   action settled.
4. **Appium is the most consistent out‑of‑process tool** — UiAutomator2 runs on‑device and
   returns only the matched element (small payload), so verifications stay ~100–130 ms even
   over USB / network, while mobilewright/Maestro transfer the full hierarchy XML.
5. **Espresso reliability cost:** each Kakao *action* (~300 ms tap) is Espresso's
   synchronized settle wait — a raw `onView().perform(click())` is ~450 ms, *slower* than
   Kakao's wrapper, so the cost is Espresso core, not Kakao, and not animations.

> Numbers are device/emulator‑specific; absolute values differ by hardware, but the
> relative ordering (in‑process vs out‑of‑process, dump cost, network sensitivity) holds.
