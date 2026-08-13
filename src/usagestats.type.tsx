export interface AppUsageStats {
    packageName:string;
    startTimeMs:number;
    endTimeMs:number;
}

export type AppUsageStatsMap = {
  [packageName: string]: AppUsageStats[];
};

export interface AppUsageStatsAggregated {
    packageName:string;
    totalTimeInForeground:number;
    firstTimeStamp:number;
    lastTimeStamp:number;
}