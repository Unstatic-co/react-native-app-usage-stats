# rn-app-usage-stats

Android app usage statistics API for React Native using TurboModules.

## Installation

```sh
npm install rn-app-usage-stats
```

## Android Permissions

This library requires Android's **Usage Access** permission to query app usage statistics.

The permission cannot be requested through the standard Android runtime permission dialog. The library opens the system Usage Access Settings so the user can grant access.

## Usage

```tsx
import {
  requestUsageStatsPermission,
  hasUsageStatsPermission,
  queryAppUsageSessions,
} from 'rn-app-usage-stats';

const granted = await requestUsageStatsPermission();

if (granted) {
  const endTime = Date.now();
  const startTime = endTime - 24 * 60 * 60 * 1000;

  const sessions = await queryAppUsageSessions(
    startTime,
    endTime
  );

  console.log(sessions);
}
```

### Check Permission

You can check whether Usage Access permission has already been granted:

```ts
const granted = await hasUsageStatsPermission();

console.log('Usage stats permission:', granted);
```

### Request Permission

If permission has not been granted, call:

```ts
const granted = await requestUsageStatsPermission();
```

This opens the Android Usage Access Settings. The returned promise resolves to `true` when the user grants permission and `false` otherwise.

### Query App Usage Sessions

```ts
const endTime = Date.now();
const startTime = endTime - 24 * 60 * 60 * 1000;

const sessions = await queryAppUsageSessions(
  startTime,
  endTime
);
```

The result is grouped by package name:

```ts
{
  'com.google.android.youtube': [
    {
      packageName: 'com.google.android.youtube',
      startTime: 1755061200000,
      endTime: 1755061500000,
    },
  ],
  'com.android.chrome': [
    {
      packageName: 'com.android.chrome',
      startTime: 1755061800000,
      endTime: 1755062100000,
    },
  ],
}
```

Each usage session contains:

```ts
export type AppUsageStats = {
  packageName: string;
  startTime: number;
  endTime: number;
};
```

`startTime` and `endTime` are Unix timestamps in milliseconds.

## API

### `hasUsageStatsPermission()`

```ts
hasUsageStatsPermission(): Promise<boolean>
```

Returns whether the application currently has Android Usage Access permission.

### `requestUsageStatsPermission()`

```ts
requestUsageStatsPermission(): Promise<boolean>
```

Opens Android Usage Access Settings and resolves with the resulting permission state when the user returns to the application.

### `queryAppUsageSessions()`

```ts
queryAppUsageSessions(
  startTime: number,
  endTime: number
): Promise<Record<string, AppUsageStats[]>>
```

Queries foreground application sessions within the specified time range and groups the results by package name.

### `getSampleUsageStats()`

```ts
getSampleUsageStats(
  startRange: number,
  endRange: number
): Promise<Record<string, AppUsageStats[]>>
```

Returns sample usage statistics for development and testing.

### `getSampleUsageStatsByPackageName()`

```ts
getSampleUsageStatsByPackageName(
  packageName: string,
  startRange: number,
  endRange: number
): Promise<AppUsageStats[]>
```

Returns sample usage statistics for a specific package.

### `queryAggregatedUsageStats()`

```ts
queryAggregatedUsageStats(
  startRange: number,
  endRange: number
): Promise<AppUsageStats[]>
```

Queries aggregated usage statistics for the specified time range.

### `queryAggregatedUsageStatsByPackageName()`

```ts
queryAggregatedUsageStatsByPackageName(
  packageName: string,
  startRange: number,
  endRange: number
): Promise<AppUsageStats[]>
```

Queries aggregated usage statistics for a specific package.

## Android Support

This library is currently intended for **Android** and uses Android's `UsageStatsManager` APIs.

Usage statistics are subject to Android's Usage Access permission and the behavior of the Android version running on the device.

## Contributing

* [Development workflow](CONTRIBUTING.md#development-workflow)
* [Sending a pull request](CONTRIBUTING.md#sending-a-pull-request)
* [Code of conduct](CODE_OF_CONDUCT.md)

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
