package com.tnex.talsec.talsec

import java.util.ArrayList
import android.content.Context
import android.os.Build


class UntrustedInstallationDetected {
    fun isUntrustedInstaller(context: Context):Boolean{
        return !verifyInstallerId(context)
    }

    private fun verifyInstallerId(context: Context): Boolean {
        val validInstallers: List<String> =
            ArrayList(listOf("com.android.vending", "com.google.android.feedback", "com.huawei.appmarket", "com.sec.android.app.samsungapps", "com.xiaomi.mipicks", "com.oppo.market", "com.vivo.appstore","com.amazon.venezia"))

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            val installer = context.packageManager.getInstallerPackageName(context.packageName)
            return if(installer == null){
                true
            }else{
                validInstallers.contains(installer)
            }
        }else{
            try {
                val info = context.packageManager.getInstallSourceInfo(context.opPackageName)
                val packageName = info.installingPackageName
                return if(packageName == null){
                    true
                }else{
                    validInstallers.contains(packageName)
                }
            }catch (e: Exception) {
                return true
            }
        }

    }
}