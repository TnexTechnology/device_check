package com.tnex.talsec.talsec

import android.app.ActivityManager

import android.os.Build

import android.content.pm.PackageManager

import android.content.Intent

import android.content.Context

import java.io.File

import java.io.FileInputStream

import java.io.InputStream
import java.util.*


class EmulatorCheck {
    private val QEMU_DRIVERS = arrayOf("goldfish")
    private val GENY_FILES = arrayOf(
        "/dev/socket/genyd",
        "/dev/socket/baseband_genyd"
    )
    private val PIPES = arrayOf(
        "/dev/socket/qemud",
        "/dev/qemu_pipe"
    )
    private val X86_FILES = arrayOf(
        "ueventd.android_x86.rc",
        "x86.prop",
        "ueventd.ttVM_x86.rc",
        "init.ttVM_x86.rc",
        "fstab.ttVM_x86",
        "fstab.vbox86",
        "init.vbox86.rc",
        "ueventd.vbox86.rc"
    )
    private val ANDY_FILES = arrayOf(
        "fstab.andy",
        "ueventd.andy.rc"
    )
    private val NOX_FILES = arrayOf(
        "fstab.nox",
        "init.nox.rc",
        "ueventd.nox.rc"
    )

    /**
     * Detects if app is currently running on emulator, or real device.
     *
     * @param context Apprication context
     * @return true for emulator, false for real devices
     */
    fun isEmulator(context: Context): Boolean {
        if (checkBasic()) return true
        if (checkAdvanced()) return true
        return checkPackageName(context)
    }

    private fun checkBasic(): Boolean {
        var rating = 0
        if (Build.PRODUCT == "sdk_x86_64" || Build.PRODUCT == "sdk_google_phone_x86" || Build.PRODUCT == "sdk_google_phone_x86_64" || Build.PRODUCT == "sdk_google_phone_arm64" || Build.PRODUCT == "vbox86p") {
            rating++
        }
        if (Build.MANUFACTURER == "unknown") {
            rating++
        }
        if (Build.BRAND == "generic" ||
            (!Build.BRAND.isNullOrEmpty() && Build.BRAND.equals(
                "android",
                ignoreCase = true
            ))|| Build.BRAND == "generic_arm64" || Build.BRAND == "generic_x86" || Build.BRAND == "generic_x86_64"
        ) {
            rating++
        }
        if (Build.DEVICE == "generic" || Build.DEVICE == "generic_arm64" || Build.DEVICE == "generic_x86" || Build.DEVICE == "generic_x86_64" || Build.DEVICE == "vbox86p") {
            rating++
        }
        if (Build.MODEL == "sdk" || Build.MODEL == "Android SDK built for arm64" || Build.MODEL == "Android SDK built for armv7" || Build.MODEL == "Android SDK built for x86" || Build.MODEL == "Android SDK built for x86_64") {
            rating++
        }
        if (Build.HARDWARE == "ranchu") {
            rating++
        }
        if (!Build.FINGERPRINT.isNullOrEmpty()
            && (Build.FINGERPRINT.contains("sdk_google_phone_arm64") || Build.FINGERPRINT.contains("sdk_google_phone_armv7"))) {
            rating++
        }
        var result = ((!Build.FINGERPRINT.isNullOrEmpty() && Build.FINGERPRINT.startsWith("generic"))
                || (!Build.MODEL.isNullOrEmpty() && Build.MODEL.contains("google_sdk"))
                || (!Build.MODEL.isNullOrEmpty() && Build.MODEL.toLowerCase(Locale.ROOT).contains("droid4x"))
                || (!Build.MODEL.isNullOrEmpty() && Build.MODEL.contains("Emulator"))
                || (!Build.MODEL.isNullOrEmpty() && Build.MODEL.contains("Android SDK built for x86"))
                || (!Build.MANUFACTURER.isNullOrEmpty() && Build.MANUFACTURER.contains("Genymotion"))
                || Build.HARDWARE == "goldfish"
                || Build.HARDWARE == "vbox86"
                || Build.PRODUCT == "sdk"
                || (!Build.PRODUCT.isNullOrEmpty() && Build.PRODUCT.startsWith("google_sdk"))
                || Build.PRODUCT == "sdk_x86"
                || Build.PRODUCT == "vbox86p"
                || (!Build.BOARD.isNullOrEmpty() && Build.BOARD.toLowerCase(Locale.ROOT).contains("nox"))
                || (!Build.BOOTLOADER.isNullOrEmpty() && Build.BOOTLOADER.toLowerCase(Locale.ROOT).contains("nox"))
                || (!Build.HARDWARE.isNullOrEmpty() && Build.HARDWARE.toLowerCase(Locale.ROOT).contains("nox"))
                || (!Build.PRODUCT.isNullOrEmpty() && Build.PRODUCT.toLowerCase(Locale.ROOT).contains("nox"))
                || (!Build.HOST.isNullOrEmpty() && Build.HOST.contains("Droid4x-BuildStation"))
                || (!Build.MANUFACTURER.isNullOrEmpty() && Build.MANUFACTURER.startsWith("iToolsAVM"))
                || (!Build.DEVICE.isNullOrEmpty() && Build.DEVICE.startsWith("iToolsAVM"))
                || (!Build.MODEL.isNullOrEmpty() && Build.MODEL.startsWith("iToolsAVM"))
                || (!Build.BRAND.isNullOrEmpty() && Build.BRAND.startsWith("generic"))
                || (!Build.HARDWARE.isNullOrEmpty() && Build.HARDWARE.startsWith("vbox86")))
        if (result) return true
        result = result or (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
        if (result) return true
        result = result or ("google_sdk" == Build.PRODUCT)
        return if (result) true else rating >= 2
    }

    private fun checkQEmuDrivers(): Boolean {
        for (drivers_file in arrayOf(File("/proc/tty/drivers"), File("/proc/cpuinfo"))) {
            if (drivers_file.exists() && drivers_file.canRead()) {
                val data = ByteArray(1024)
                try {
                    val `is`: InputStream = FileInputStream(drivers_file)
                    `is`.read(data)
                    `is`.close()
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
                val driverData = String(data)
                for (known_qemu_driver in QEMU_DRIVERS) {
                    if (driverData.contains(known_qemu_driver)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun checkAdvanced(): Boolean {
        return (checkFiles(GENY_FILES)
                || checkFiles(ANDY_FILES)
                || checkFiles(NOX_FILES)
                || checkQEmuDrivers()
                || checkFiles(PIPES)
                || checkFiles(X86_FILES))
    }

    private fun checkFiles(targets: Array<String>): Boolean {
        for (pipe in targets) {
            try {
                val isExists = File(pipe).exists()
                if (isExists) {
                    return true
                }
            }catch (e:RuntimeException){

            }
        }

        return false
    }

    private fun checkPackageName(context: Context): Boolean {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val availableActivities = packageManager.queryIntentActivities(intent, 0)
        for (resolveInfo in availableActivities) {
            if (resolveInfo.activityInfo.packageName.startsWith("com.bluestacks.")) {
                return true
            }
        }
        val packages = packageManager
            .getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            val packageName = packageInfo.packageName
            if(packageName.isNullOrEmpty()){
                return false
            }
            else if (packageName.startsWith("com.vphone.")) {
                return true
            } else if (packageName.startsWith("com.bignox.")) {
                return true
            } else if (packageName.startsWith("com.nox.mopen.app")) {
                return true
            } else if (packageName.startsWith("me.haima.")) {
                return true
            } else if (packageName.startsWith("com.bluestacks.")) {
                return true
            } else if (packageName.startsWith("cn.itools.") && Build.PRODUCT.startsWith("iToolsAVM")) {
                return true
            } else if (packageName.startsWith("com.kop.")) {
                return true
            } else if (packageName.startsWith("com.kaopu.")) {
                return true
            } else if (packageName.startsWith("com.microvirt.")) {
                return true
            } else if (packageName == "com.google.android.launcher.layouts.genymotion") {
                return true
            }
        }

        try {
            val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val serviceInfos = manager.getRunningServices(30)
            for (serviceInfo in serviceInfos) {
                if(serviceInfo?.service != null){
                    val serviceName = serviceInfo.service.className
                    if (serviceName.isNotEmpty() && serviceName.startsWith("com.bluestacks.")) {
                        return true
                    }
                }
            }
        }catch (e: Exception){

        }

        return false
    }
}