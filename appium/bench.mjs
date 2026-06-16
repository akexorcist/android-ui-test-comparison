import { remote } from 'webdriverio';
const now = () => performance.now();
const APP = 'dev.mobilewright.testapp';
const N = 6;
const driver = await remote({
  hostname: '127.0.0.1', port: 4723, path: '/', logLevel: 'error',
  capabilities: {
    platformName: 'Android', 'appium:automationName': 'UiAutomator2',
    'appium:udid': 'emulator-5554', 'appium:appPackage': APP,
    'appium:appActivity': 'dev.mobilewright.testapp.MainActivity', 'appium:newCommandTimeout': 180,
  },
});
const ops = ['tap','verify_tap','type','tap_echo','verify_type','long_press','verify_long','swipe','verify_swipe'];
const data = Object.fromEntries(ops.map(o => [o, []]));
for (let i = 0; i < N; i++) {
  if (i > 0) { await driver.terminateApp(APP); await driver.activateApp(APP); }
  let t;
  t = now(); await (await driver.$('~tap-button')).click(); data.tap.push(now() - t);
  t = now(); await (await driver.$('~counter')).getText(); data.verify_tap.push(now() - t);
  t = now(); await (await driver.$('~input-field')).setValue('hello'); data.type.push(now() - t);
  t = now(); await (await driver.$('~echo-button')).click(); data.tap_echo.push(now() - t);
  t = now(); await (await driver.$('~echo')).getText(); data.verify_type.push(now() - t);
  t = now(); const lb = await driver.$('~long-button'); await driver.execute('mobile: longClickGesture', { elementId: lb.elementId, duration: 800 }); data.long_press.push(now() - t);
  t = now(); await (await driver.$('~long-status')).getText(); data.verify_long.push(now() - t);
  t = now(); await driver.execute('mobile: scrollGesture', { left: 100, top: 600, width: 800, height: 1000, direction: 'down', percent: 4.0 }); data.swipe.push(now() - t);
  t = now(); await (await driver.$('~bottom-marker')).isDisplayed(); data.verify_swipe.push(now() - t);
}
const med = (a) => { const b=[...a].sort((x,y)=>x-y); const m=Math.floor(b.length/2); return b.length%2?b[m]:(b[m-1]+b[m])/2; };
const r = (x) => Math.round(x);
const out = {};
for (const o of ops) out[o] = { median: r(med(data[o])), min: r(Math.min(...data[o])), max: r(Math.max(...data[o])) };
console.log(JSON.stringify(out));
await driver.deleteSession();
process.exit(0);
