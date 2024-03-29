package com.tnex.talsec.talsec

import java.io.InputStreamReader

import java.io.BufferedReader

import java.io.File


class RootedCheck {
    /**
     * Checks if the device is rooted.
     *
     * @return `true` if the device is rooted, `false` otherwise.
     */

    fun isJailBroken(): Boolean {
        return checkRooted()
    }

    private fun checkRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2()
    }

    private fun checkRootMethod1(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su"
        )
        for (path in paths) {
            var isExists = false
            try {
                isExists =  File(path).exists()
            } catch (e: RuntimeException) {

            }

            if(isExists){
                return true
            }
        }

        return false
    }

    private fun checkRootMethod2(): Boolean {
        var process: Process? = null
        try {
            process = Runtime.getRuntime().exec(arrayOf("/system/xbin/which", "su"))
            val inputBuffered = BufferedReader(InputStreamReader(process.inputStream))
            return inputBuffered.readLine() != null
        }catch (e: RuntimeException){
            return false
        }finally {
            process?.destroy()
        }
    }
}