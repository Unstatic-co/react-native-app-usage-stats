package com.appusagestats

import com.facebook.react.bridge.ReactApplicationContext

class AppUsageStatsModule(reactContext: ReactApplicationContext) :
  NativeAppUsageStatsSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeAppUsageStatsSpec.NAME
  }
}
