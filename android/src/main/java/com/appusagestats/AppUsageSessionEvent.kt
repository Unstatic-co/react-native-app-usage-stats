package com.appusagestats

data class AppUsageSessionEvent(
  val packageName: String, val startTime: Long, val endTime: Long
)

data class AppUsageAggregateEvent(
  val packageName: String, val totalUsageTime: Long, val startTime: Long, val endTime: Long
)
