package com.appusagestats

import android.Manifest
import android.app.usage.UsageEvents
import android.app.usage.UsageEventsQuery
import android.app.usage.UsageStatsManager
import android.os.Build
import androidx.annotation.RequiresPermission
import com.facebook.react.bridge.ReadableNativeMap
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.WritableNativeMap

class AppUsageStatsQueryHelper(private val usageStatsManager: UsageStatsManager) {


  @RequiresPermission(Manifest.permission.PACKAGE_USAGE_STATS)
  fun queryAppUsageSessions(
    startTime: Long,
    endTime: Long
  ): ReadableNativeMap {
    val events = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      usageStatsManager.queryEvents(
        UsageEventsQuery.Builder(startTime, endTime).setEventTypes(
          UsageEvents.Event.ACTIVITY_RESUMED,
          UsageEvents.Event.ACTIVITY_PAUSED,
          UsageEvents.Event.KEYGUARD_SHOWN,
          UsageEvents.Event.DEVICE_SHUTDOWN
        ).build()
      )
    } else {
      usageStatsManager.queryEvents(startTime, endTime)
    }
    if (events == null) return WritableNativeMap()

    val grouped = mutableMapOf<String, MutableList<AppUsageSessionEvent>>()
    fun addSession(
      packageName: String,
      sessionStart: Long,
      sessionEnd: Long
    ) {
      if (sessionEnd <= sessionStart) {
        return
      }
      grouped
        .getOrPut(packageName) { mutableListOf() }
        .add(
          AppUsageSessionEvent(
            packageName = packageName,
            startTime = sessionStart,
            endTime = sessionEnd
          )
        )
    }
    val eventHolder = UsageEvents.Event()
    var currentPkg: String? = null
    var currentStartTime: Long = 0L
    var lastPausedTime: Long = 0L
    while (events.hasNextEvent()) {
      events.getNextEvent(eventHolder)
      when (eventHolder.eventType) {
        UsageEvents.Event.ACTIVITY_RESUMED -> {
          if (eventHolder.packageName != currentPkg) {
            if (currentPkg != null) {
              if (lastPausedTime > currentStartTime) {
                addSession(currentPkg, currentStartTime, lastPausedTime)
              }
            }
            currentPkg = eventHolder.packageName
            currentStartTime = eventHolder.timeStamp
          }
        }

        UsageEvents.Event.ACTIVITY_PAUSED -> {
          if (eventHolder.packageName == currentPkg) {
            lastPausedTime = eventHolder.timeStamp
          }
        }

        UsageEvents.Event.KEYGUARD_SHOWN, UsageEvents.Event.DEVICE_SHUTDOWN -> {
          if (currentPkg != null) {
            val end =
              if (lastPausedTime > currentStartTime) lastPausedTime else eventHolder.timeStamp
            if (lastPausedTime > currentStartTime) {
              addSession(currentPkg, currentStartTime, end)
            }
          }
          currentPkg = null
        }

        else -> {
          // Bỏ qua các sự kiện khác
        }
      }
    }
    currentPkg?.let {
      if (currentStartTime < endTime) {
        addSession(it, currentStartTime, endTime)
      }
    }
    val result = WritableNativeMap()

    grouped.forEach { (packageName, sessions) ->

      val array = WritableNativeArray()

      sessions.forEach { session ->
        array.pushMap(
          WritableNativeMap().apply {
            putString(
              "packageName",
              session.packageName
            )
            putLong(
              "startTime",
              session.startTime
            )
            putLong(
              "endTime",
              session.endTime
            )
          }
        )
      }

      result.putArray(packageName, array)
    }
    return result
  }

  @RequiresPermission(Manifest.permission.PACKAGE_USAGE_STATS)
  fun queryAppUsageSessionsByPackageName(
    packageName: String,
    startTime: Long,
    endTime: Long
  ): WritableNativeArray {
    val events = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
      usageStatsManager.queryEvents(
        UsageEventsQuery.Builder(startTime, endTime).setEventTypes(
          UsageEvents.Event.ACTIVITY_RESUMED,
          UsageEvents.Event.ACTIVITY_PAUSED,
          UsageEvents.Event.KEYGUARD_SHOWN,
          UsageEvents.Event.DEVICE_SHUTDOWN
        ).build()
      )
    } else {
      usageStatsManager.queryEvents(startTime, endTime)
    }
    if (events == null) return WritableNativeArray()
    val result = WritableNativeArray()
    val eventHolder = UsageEvents.Event()
    var currentPkg: String? = null
    var currentStartTime: Long = 0L
    var lastPausedTime: Long = 0L
    while (events.hasNextEvent()) {
      events.getNextEvent(eventHolder)
      when (eventHolder.eventType) {
        UsageEvents.Event.ACTIVITY_RESUMED -> {
          if (eventHolder.packageName != currentPkg) {
            if (currentPkg != null) {
              if (lastPausedTime > currentStartTime && currentPkg == packageName) {
                result.pushMap(
                  WritableNativeMap().apply {
                    putString("packageName", currentPkg)
                    putDouble("startTime", currentStartTime.toDouble())
                    putDouble("endTime", lastPausedTime.toDouble())
                  })
              }
            }
            currentPkg = eventHolder.packageName
            currentStartTime = eventHolder.timeStamp
          }
        }

        UsageEvents.Event.ACTIVITY_PAUSED -> {
          if (eventHolder.packageName == currentPkg) {
            lastPausedTime = eventHolder.timeStamp
          }
        }

        UsageEvents.Event.KEYGUARD_SHOWN, UsageEvents.Event.DEVICE_SHUTDOWN -> {
          if (currentPkg != null) {
            val end =
              if (lastPausedTime > currentStartTime) lastPausedTime else eventHolder.timeStamp
            if (lastPausedTime > currentStartTime && currentPkg == packageName) {
              result.pushMap(
                WritableNativeMap().apply {
                  putString("packageName", currentPkg)
                  putDouble("startTime", currentStartTime.toDouble())
                  putDouble("endTime", end.toDouble())
                })
            }
          }
          currentPkg = null
        }

        else -> {
          // Bỏ qua các sự kiện khác
        }
      }
    }
    currentPkg?.let {
      if (currentStartTime < endTime && currentPkg == packageName) {
        result.pushMap(
          WritableNativeMap().apply {
            putString("packageName", it)
            putLong("startTime", currentStartTime)
            putLong("endTime", endTime)
          }
        )
      }
    }
    return result
  }

  fun queryAggregatedUsageStats(
    filterPackageName: String?,
    startTime: Long,
    endTime: Long
  ): WritableNativeArray {
    val usageStatsList =
      usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime)
    val resultList = WritableNativeArray()
    for (usageStats in usageStatsList) {
      if (filterPackageName == null || filterPackageName == usageStats.packageName) {
        resultList.pushMap(
          WritableNativeMap().apply {
            putString("packageName", usageStats.packageName)
            putLong("totalTimeInForeground", usageStats.totalTimeInForeground)
            putLong("firstTimeStamp", usageStats.firstTimeStamp)
            putLong("lastTimeStamp", usageStats.lastTimeStamp)
          }
        )
      }
    }
    return resultList
  }


}
