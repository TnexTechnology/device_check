package com.tnex.talsec.talsec

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import java.util.*


class HookDetectionCheck {
    /**
     * Detects if there is any suspicious installed application.
     *
     * @return `true` if some bad application is installed, `false` otherwise.
     */
    fun hookDetected(context: Context): Boolean {
        val packageManager = context.packageManager
        val applicationInfoList =
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        val dangerousPackages = arrayOf(
            "de.robv.android.xposed.installer",
            "com.saurik.substrate",
            "de.robv.android.xposed"
        )
        for (applicationInfo in applicationInfoList) {
            if(!applicationInfo.packageName.isNullOrEmpty() && dangerousPackages.contains(applicationInfo.packageName)){
                return true
            }
        }

        return advancedHookDetection(context)
    }

    private fun advancedHookDetection(context: Context): Boolean {
        try {
            throw Exception()
        } catch (e: Exception) {
            var zygoteInitCallCount = 0
            for (stackTraceElement in e.stackTrace) {
                if (stackTraceElement != null &&
                    !stackTraceElement.className.isNullOrEmpty() &&
                    stackTraceElement.className == "com.android.internal.os.ZygoteInit") {
                    zygoteInitCallCount++
                    if (zygoteInitCallCount == 2) {
                        return true
                    }
                }
                if (stackTraceElement != null &&
                    !stackTraceElement.className.isNullOrEmpty() &&
                    !stackTraceElement.methodName.isNullOrEmpty() &&
                    stackTraceElement.className == "com.saurik.substrate.MS$2" &&
                    stackTraceElement.methodName == "invoked") {
                    return true
                }
                if (stackTraceElement != null &&
                    !stackTraceElement.className.isNullOrEmpty() &&
                    !stackTraceElement.methodName.isNullOrEmpty() &&
                    stackTraceElement.className == "de.robv.android.xposed.XposedBridge" &&
                    stackTraceElement.methodName == "main") {
                    return true
                }
                if (stackTraceElement != null &&
                    !stackTraceElement.className.isNullOrEmpty() &&
                    !stackTraceElement.methodName.isNullOrEmpty() &&
                    stackTraceElement.className == "de.robv.android.xposed.XposedBridge" &&
                    stackTraceElement.methodName == "handleHookedMethod") {
                    return true
                }
            }
        }
        return checkFrida(context)
    }

    private fun checkFrida(context: Context): Boolean {
        try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val runningServices = activityManager.getRunningServices(300)
            if (runningServices != null) {
                for (i in runningServices.indices) {
                    if (!runningServices[i].process.isNullOrEmpty() &&
                        runningServices[i].process.contains("fridaserver")) {
                        return true
                    }
                }
            }
        }catch (e: Exception){

        }

        return false
    }
}