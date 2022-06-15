package com.tnex.talsec.talsec

import java.util.Arrays

import java.util.ArrayList

import android.content.Context
import android.content.pm.InstallSourceInfo
import android.os.Build
import android.util.Log


class UntrustedInstallationDetected {
    fun isUntrustedInstaller(context: Context):Boolean{
        return !verifyInstallerId(context)
    }

    private fun verifyInstallerId(context: Context): Boolean {
        // A list with valid installers package name
        val validInstallers: List<String> =
            ArrayList(listOf("com.android.vending", "com.google.android.feedback", "com.huawei.appmarket", "com.sec.android.app.samsungapps", "com.xiaomi.mipicks", "com.oppo.market", "com.vivo.appstore","com.amazon.venezia"))

        val installer = context.packageManager.getInstallerPackageName(context.packageName)
        return installer != null && validInstallers.contains(installer)

//        // The package name of the app that has installed your app
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
//            val installer = context.packageManager.getInstallerPackageName(context.packageName)
//            return installer != null && validInstallers.contains(installer)
//        }else{
//            try {
//                val info = context.packageManager.getInstallSourceInfo(context.opPackageName)
//                val packageName = info.installingPackageName
//                return packageName != null && validInstallers.contains(packageName)
//            }catch (e: Exception) {
//                e.message?.let { Log.i("e", it) }
//            }
//            return false;
//        }
    }
}