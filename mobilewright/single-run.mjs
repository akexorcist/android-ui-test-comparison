import { connectDevice } from 'mobilewright';
import { expect } from '@mobilewright/core';

const DEV = { platform: 'android', deviceId: 'Pixel_5_API_33', deviceType: 'emulator' };
const APP = 'dev.mobilewright.testapp';
const now = () => performance.now();
const ms = (x) => Math.round(x * 10) / 10;

const device = await connectDevice(DEV);
const s = device.screen;
const R = {};
let t;
await device.terminateApp(APP).catch(() => {});
t = now(); await device.launchApp(APP); R.launch = ms(now() - t);

// 1) TAP
t = now(); await s.getByText('TAP ME').tap(); R.tap = ms(now() - t);
t = now(); await expect(s.getByText('Count: 1')).toBeVisible(); R.verify_tap = ms(now() - t);

// 2) TYPE TEXT
t = now(); await s.getByLabel('input-field').fill('hello'); R.type = ms(now() - t);
t = now(); await s.getByText('ECHO').tap(); R.tap_echo = ms(now() - t);
t = now(); await expect(s.getByText('Echo: hello')).toBeVisible(); R.verify_type = ms(now() - t);

// 3) LONG PRESS
t = now(); await s.getByText('LONG PRESS HERE').longPress(); R.long_press = ms(now() - t);
t = now(); await expect(s.getByText('Long: pressed')).toBeVisible(); R.verify_long = ms(now() - t);

// 4) SWIPE / SCROLL
t = now(); await s.getByLabel('bottom-marker').scrollIntoViewIfNeeded(); R.swipe = ms(now() - t);
t = now(); await expect(s.getByLabel('bottom-marker')).toBeVisible(); R.verify_swipe = ms(now() - t);

console.log(JSON.stringify(R));
process.exit(0);
