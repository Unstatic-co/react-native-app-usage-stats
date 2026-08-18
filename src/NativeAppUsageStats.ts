import { TurboModuleRegistry, type TurboModule } from 'react-native';
import type {
  AppUsageStats,
  AppUsageStatsMap,
  AppUsageStatsAggregated,
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
}

export default TurboModuleRegistry.getEnforcing<Spec>('AppUsageStats');
