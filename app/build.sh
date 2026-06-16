#!/bin/bash
# Builds a signed debug APK with no Gradle — just the Android SDK command-line
# tools. Produces mw-testapp.apk.
set -e
SDK=$HOME/Library/Android/sdk
PLATFORM=$SDK/platforms/android-33/android.jar
BT=$SDK/build-tools/36.1.0
HERE=$(cd "$(dirname "$0")" && pwd)

rm -rf "$HERE/out"
mkdir -p "$HERE/out/classes"

echo "==> javac"
javac -source 11 -target 11 -cp "$PLATFORM" -d "$HERE/out/classes" \
  "$HERE/src/dev/mobilewright/testapp/MainActivity.java"

echo "==> d8 (dex)"
"$BT/d8" --min-api 24 --output "$HERE/out" --lib "$PLATFORM" \
  $(find "$HERE/out/classes" -name '*.class')

echo "==> aapt2 link (base apk with manifest)"
"$BT/aapt2" link \
  --manifest "$HERE/AndroidManifest.xml" \
  -I "$PLATFORM" \
  --min-sdk-version 24 --target-sdk-version 33 \
  -o "$HERE/out/base.apk"

echo "==> add classes.dex into apk"
cd "$HERE/out"
cp base.apk unaligned.apk
zip -j unaligned.apk classes.dex >/dev/null

echo "==> zipalign"
"$BT/zipalign" -f 4 unaligned.apk aligned.apk

echo "==> debug keystore"
KS="$HERE/out/debug.keystore"
if [ ! -f "$KS" ]; then
  keytool -genkeypair -keystore "$KS" -alias androiddebugkey \
    -storepass android -keypass android -keyalg RSA -keysize 2048 -validity 10000 \
    -dname "CN=Android Debug,O=Android,C=US" >/dev/null 2>&1
fi

echo "==> apksigner sign"
"$BT/apksigner" sign --ks "$KS" --ks-pass pass:android --key-pass pass:android \
  --out "$HERE/mw-testapp.apk" aligned.apk

"$BT/apksigner" verify "$HERE/mw-testapp.apk" && echo "==> built $HERE/mw-testapp.apk"
ls -la "$HERE/mw-testapp.apk"
