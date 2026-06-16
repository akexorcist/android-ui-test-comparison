import { connectDevice } from 'mobilewright';
import { expect } from '@mobilewright/core';

const DEV = { platform: 'android', deviceId: 'Pixel_5_API_33', deviceType: 'emulator' };
const APP = 'dev.mobilewright.testapp';
const now = () => performance.now();
const N = 6;

const device = await connectDevice(DEV);
const s = device.screen;
const ops = ['launch','tap','verify_tap','type','tap_echo','verify_type','long_press','verify_long','swipe','verify_swipe'];
const data = Object.fromEntries(ops.map(o => [o, []]));

for (let i = 0; i < N; i++) {
  let t;
  await device.terminateApp(APP).catch(() => {});
  t = now(); await device.launchApp(APP); data.launch.push(now() - t);
  t = now(); await s.getByText('TAP ME').tap(); data.tap.push(now() - t);
  t = now(); await expect(s.getByText('Count: 1')).toBeVisible(); data.verify_tap.push(now() - t);
  t = now(); await s.getByLabel('input-field').fill('hello'); data.type.push(now() - t);
  t = now(); await s.getByText('ECHO').tap(); data.tap_echo.push(now() - t);
  t = now(); await expect(s.getByText('Echo: hello')).toBeVisible(); data.verify_type.push(now() - t);
  t = now(); await s.getByText('LONG PRESS HERE').longPress(); data.long_press.push(now() - t);
  t = now(); await expect(s.getByText('Long: pressed')).toBeVisible(); data.verify_long.push(now() - t);
  t = now(); await s.getByLabel('bottom-marker').scrollIntoViewIfNeeded(); data.swipe.push(now() - t);
  t = now(); await expect(s.getByLabel('bottom-marker')).toBeVisible(); data.verify_swipe.push(now() - t);
}
const med = (a) => { const b = [...a].sort((x,y)=>x-y); const m = Math.floor(b.length/2); return b.length%2 ? b[m] : (b[m-1]+b[m])/2; };
const r = (x) => Math.round(x);
const out = {};
for (const o of ops) out[o] = { median: r(med(data[o])), min: r(Math.min(...data[o])), max: r(Math.max(...data[o])) };
console.log(JSON.stringify(out));
process.exit(0);
