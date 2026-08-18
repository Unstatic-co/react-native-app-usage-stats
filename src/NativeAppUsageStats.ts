import { TurboModuleRegistry, type TurboModule } from 'react-native';
import type {
  AppUsageStats,
  AppUsageStatsMap,
  AppUsageStatsAggregated,
  UsageApp,
} from './usagestats.type';

export interface Spec extends TurboModule {
  hasUsageStatsPermission(): Promise<boolean>;

  requestUsageStatsPermission(): Promise<boolean>;

  queryAppUsageSessions(
    startRange: number,
    endRange: number
  ): Promise<AppUsageStatsMap>;

  queryAppUsageSessionsByPackageName(
    packageName: string,
    startRange: number,
    endRange: number
  ): Promise<AppUsageStats[]>;

  queryAggregatedUsageStatsByPackageName(
    packageName: string,
    startRange: number,
    endRange: number
  ): Promise<AppUsageStatsAggregated[]>;

  queryAggregatedUsageStats(
    startRange: number,
    endRange: number
  ): Promise<AppUsageStatsAggregated[]>;

  queryUsageApps(startRange: number, endRange: number): Promise<UsageApp[]>;
}

export default TurboModuleRegistry.getEnforcing<Spec>('AppUsageStats');
