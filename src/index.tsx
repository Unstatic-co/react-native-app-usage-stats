

import NativeAppUsageStats from './NativeAppUsageStats';
import type {AppUsageStats, AppUsageStatsAggregated} from './usagestats.type';

export async function requestUsageStatsPermission(): Promise<boolean> {
    return await NativeAppUsageStats.requestUsageStatsPermission();
}
export async function hasUsageStatsPermission(): Promise<boolean> {
    return await NativeAppUsageStats.hasUsageStatsPermission();
}

export async function getSampleUsageStats(
    startRange: number,
    endRange: number
): Promise<Record<string, AppUsageStats[]>> {
    return await NativeAppUsageStats.getSampleUsageStats(startRange, endRange);
}

export async function getSampleUsageStatsByPackageName(
    packageName:string,
    startRange: number,
    endRange: number
): Promise<AppUsageStats[]> {
    return await NativeAppUsageStats.getSampleUsageStatsByPackageName(packageName, startRange, endRange);
}

export async function queryAggregatedUsageStatsByPackageName(
    packageName:string,
    startRange: number,
    endRange: number
): Promise<AppUsageStatsAggregated[]> {
    return await NativeAppUsageStats.queryAggregatedUsageStatsByPackageName(packageName, startRange, endRange);
}

export async function queryAggregatedUsageStats(
    startRange: number,
    endRange: number
): Promise<AppUsageStatsAggregated[]> {
    return await NativeAppUsageStats.queryAggregatedUsageStats(startRange, endRange);
}