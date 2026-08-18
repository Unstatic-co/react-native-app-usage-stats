export interface AppUsageStats {
  packageName: string;
  startTime: number;
  endTime: number;
}

export type AppUsageStatsMap = {
  [packageName: string]: AppUsageStats[];
};

export interface AppUsageStatsAggregated {
  packageName: string;
  totalTimeInForeground: number;
  firstTimeStamp: number;
  lastTimeStamp: number;
}
