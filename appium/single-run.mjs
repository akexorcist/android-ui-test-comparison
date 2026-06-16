import { remote } from 'webdriverio';
const now = () => performance.now();
const ms = (x) => Math.round(x * 10) / 10;

const driver = await remote({
  hostname: '127.0.0.1', port: 4723, path: '/', logLevel: 'error',
  capabilities: {
    platformName: 'Android', 'appium:automationName': 'UiAutomator2',
    'appium:udid': 'emulator-5554', 'appium:appPackage': 'dev.mobilewright.testapp',
    'appium:appActivity': 'dev.mobilewright.testapp.MainActivity', 'appium:newCommandTimeout': 120,
  },
});
const R = {}; let t;
// 1) TAP
t = now(); await (await driver.$('~tap-button')).click(); R.tap = ms(now() - t);
t = now(); const c1 = await (await driver.$('~counter')).getText(); R.verify_tap = ms(now() - t);
// 2) TYPE
t = now(); await (await driver.$('~input-field')).setValue('hello'); R.type = ms(now() - t);
t = now(); await (await driver.$('~echo-button')).click(); R.tap_echo = ms(now() - t);
t = now(); const e1 = await (await driver.$('~echo')).getText(); R.verify_type = ms(now() - t);
// 3) LONG PRESS
t = now();
const lb = await driver.$('~long-button');
await driver.execute('mobile: longClickGesture', { elementId: lb.elementId, duration: 800 });
R.long_press = ms(now() - t);
t = now(); const ls = await (await driver.$('~long-status')).getText(); R.verify_long = ms(now() - t);
// 4) SWIPE / SCROLL
t = now();
await driver.execute('mobile: scrollGesture', { left: 100, top: 600, width: 800, height: 1000, direction: 'down', percent: 4.0 });
R.swipe = ms(now() - t);
t = now(); const bm = await (await driver.$('~bottom-marker')).isDisplayed(); R.verify_swipe = ms(now() - t);
R._results = { count: c1, echo: e1, long: ls, bottomVisible: bm };
console.log(JSON.stringify(R));
await driver.deleteSession();
process.exit(0);
