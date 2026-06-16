import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: '.',
  testMatch: '*.test.ts',
  timeout: 60_000,
  workers: 1,
  retries: 0,
  reporter: 'list',
  use: {
    platform: 'android',
    bundleId: 'dev.mobilewright.testapp',
    autoAppLaunch: true,
  } as any,
});
