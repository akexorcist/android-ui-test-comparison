import { test, expect } from '@mobilewright/test';

// Drive the installed test app. Set your device in mw.config.ts (platform/bundleId).
test('basic UI interactions', async ({ screen }) => {
  // 1) tap
  await screen.getByText('TAP ME').tap();
  await expect(screen.getByText('Count: 1')).toBeVisible();

  // 2) type text
  await screen.getByLabel('input-field').fill('hello');
  await screen.getByText('ECHO').tap();
  await expect(screen.getByText('Echo: hello')).toBeVisible();

  // 3) long press
  await screen.getByText('LONG PRESS HERE').longPress();
  await expect(screen.getByText('Long: pressed')).toBeVisible();

  // 4) swipe / scroll
  await screen.getByLabel('bottom-marker').scrollIntoViewIfNeeded();
  await expect(screen.getByLabel('bottom-marker')).toBeVisible();
});
