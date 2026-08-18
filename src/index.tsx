import NativeAppUsageStats from './NativeAppUsageStats';
import type { AppUsageStats, AppUsageStatsAggregated } from './usagestats.type';

export type {
  AppUsageStats,
  AppUsageStatsAggregated,
  AppUsageStatsMap,
} from './usagestats.type';

export async function requestUsageStatsPermission(): Promise<boolean> {
  return await NativeAppUsageStats.requestUsageStatsPermission();
}
export async function hasUsageStatsPermission(): Promise<boolean> {
  return await NativeAppUsageStats.hasUsageStatsPermission();
}

export async function queryAppUsageSessions(
  startRange: number,
  endRange: number
): Promise<Record<string, AppUsageStats[]>> {
  return await NativeAppUsageStats.queryAppUsageSessions(startRange, endRange);
}

export async function queryAppUsageSessionsByPackageName(
  packageName: string,
  startRange: number,
  endRange: number
): Promise<AppUsageStats[]> {
  return await NativeAppUsageStats.queryAppUsageSessionsByPackageName(
    packageName,
    startRange,
    endRange
  );
}

export async function queryAggregatedUsageStatsByPackageName(
  packageName: string,
  startRange: number,
  endRange: number
): Promise<AppUsageStatsAggregated[]> {
  return await NativeAppUsageStats.queryAggregatedUsageStatsByPackageName(
    packageName,
    startRange,
    endRange
  );
}

export async function queryAggregatedUsageStats(
  startRange: number,
  endRange: number
): Promise<AppUsageStatsAggregated[]> {
  return await NativeAppUsageStats.queryAggregatedUsageStats(
    startRange,
    endRange
  );
}
