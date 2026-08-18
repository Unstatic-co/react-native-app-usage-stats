package com.appusagestats

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AppUsageStatsModule(reactContext: ReactApplicationContext) :
  NativeAppUsageStatsSpec(reactContext), LifecycleEventListener {
  private var permissionPromise: Promise? = null
  private val usageStatsManager: UsageStatsManager by lazy {
    reactContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
  }
  private val helper: AppUsageStatsQueryHelper by lazy {
    AppUsageStatsQueryHelper(usageStatsManager, reactApplicationContext.packageManager)
  }
  private val coroutineScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

  init {
    reactContext.addLifecycleEventListener(this)
  }

  override fun hasUsageStatsPermission(promise: Promise) {
    promise.resolve(
      hasUsageStatsPermission(reactApplicationContext)
    )
  }

  private fun hasUsageStatsPermission(context: ReactApplicationContext): Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = when {
      Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA -> {
        appOps.checkOpNoThrow(
          AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName
        )
      }

      Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
        appOps.unsafeCheckOpNoThrow(
          AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName
        )
      }

      else -> {
        appOps.checkOpNoThrow(
          AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.packageName
        )
      }
    }
    return mode == AppOpsManager.MODE_ALLOWED
  }

  override fun requestUsageStatsPermission(promise: Promise) {
    if (hasUsageStatsPermission(reactApplicationContext)) {
      promise.resolve(true)
      return
    }

    permissionPromise = promise

    val intent = Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    reactApplicationContext.startActivity(intent)
  }

  override fun queryAppUsageSessions(
    startRange: Double, endRange: Double, promise: Promise
  ) {
    coroutineScope.launch {
      try {
        val result = helper.queryAppUsageSessions(startRange.toLong(), endRange.toLong())
        promise.resolve(result)
      } catch (e: Exception) {
        promise.reject("USAGE_STATS_ERROR", e.message, e)
      }
    }
  }

  override fun queryAppUsageSessionsByPackageName(
    packageName: String?, startRange: Double, endRange: Double, promise: Promise
  ) {
    if (packageName == null) {
      promise.reject("USAGE_STATS_ERROR", "Package name cannot be null")
      return
    }
    coroutineScope.launch {

      try {
        val result = helper.queryAppUsageSessionsByPackageName(
          packageName,
          startRange.toLong(),
          endRange.toLong()
        )
        promise.resolve(result)
      } catch (e: Exception) {
        promise.reject("USAGE_STATS_ERROR", e.message, e)
      }
    }

  }

  override fun queryAggregatedUsageStatsByPackageName(
    packageName: String?, startRange: Double, endRange: Double, promise: Promise
  ) {
    coroutineScope.launch {
      try {

        val result =
          helper.queryAggregatedUsageStats(packageName, startRange.toLong(), endRange.toLong())
        promise.resolve(result)
      } catch (e: Exception) {
        promise.reject("USAGE_STATS_ERROR", e.message, e)
      }
    }
  }

  override fun queryAggregatedUsageStats(
    startRange: Double, endRange: Double, promise: Promise
  ) {
    queryAggregatedUsageStatsByPackageName(null, startRange, endRange, promise)
  }

  override fun queryUsageApps(
    startRange: Double, endRange: Double, promise: Promise
  ) {
    coroutineScope.launch {
      try {
        promise.resolve(helper.queryUsageApps(startRange.toLong(), endRange.toLong()))
      } catch (e: Exception) {
        promise.reject("USAGE_STATS_ERROR", e.message, e)
      }
    }
  }


  override fun onHostResume() {
    val promise = permissionPromise ?: return

    permissionPromise = null

    promise.resolve(
      hasUsageStatsPermission(reactApplicationContext)
    )
  }

  override fun onHostPause() {
  }

  override fun onHostDestroy() {
  }

  companion object {
    const val NAME = NativeAppUsageStatsSpec.NAME
  }
}
