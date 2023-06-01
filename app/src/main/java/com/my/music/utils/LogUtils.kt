package com.my.music.utils

import android.os.Looper
import android.util.Log

@Suppress("unused")
object LogUtils {
    private var enabled = true
    private const val DEFAULT_TAG = ""
    private const val DEFAULT_MESSAGE = "execute"

    fun v(msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.VERBOSE, DEFAULT_TAG, msg, stackOffset)
    }

    fun v(tag: String = DEFAULT_TAG, msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.VERBOSE, tag, msg, stackOffset)
    }

    fun d(msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.DEBUG, DEFAULT_TAG, msg, stackOffset)
    }

    fun d(tag: String = DEFAULT_TAG, msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.DEBUG, tag, msg, stackOffset)
    }

    fun i(msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.INFO, DEFAULT_TAG, msg, stackOffset)
    }

    fun i(tag: String = DEFAULT_TAG, msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.INFO, tag, msg, stackOffset)
    }

    fun w(msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.WARN, DEFAULT_TAG, msg, stackOffset)
    }

    fun w(tag: String = DEFAULT_TAG, msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.WARN, tag, msg, stackOffset)
    }

    fun e(msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.ERROR, DEFAULT_TAG, msg, stackOffset)
    }

    fun e(tag: String = DEFAULT_TAG, msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.ERROR, tag, msg, stackOffset)
    }

    fun a(msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.ASSERT, DEFAULT_TAG, msg, stackOffset)
    }

    fun a(tag: String = DEFAULT_TAG, msg: String = DEFAULT_MESSAGE, stackOffset: Int = 0) {
        printLog(Log.ASSERT, tag, msg, stackOffset)
    }

    private fun printLog(type: Int, tag: String, content: String, stackOffset: Int) {
        if (!enabled) return
        var mutableStackOffset = stackOffset
        val trace = Throwable().stackTrace.firstOrNull { it.className != this::class.java.name && mutableStackOffset-- == 0 }
        val printContent = "$content ${parseThread()}, ${parseLocation(trace)}"
        val printTag = tag.ifEmpty { parseTag(trace) }
        // Print to console
        Log.println(type, printTag, printContent)
    }

    /**
     * find thread info of this log
     */
    private fun parseThread(): String {
        return "Thread-Name: ${Thread.currentThread().name}, isMain: ${Looper.getMainLooper() == Looper.myLooper()}"
    }

    /**
     * find code location of this log
     */
    private fun parseLocation(trace: StackTraceElement?): String {
        trace ?: return ""
        if (trace.methodName.isNullOrEmpty() || trace.fileName.isNullOrEmpty() || trace.lineNumber <= 0) return ""
        return "Location: ${trace.methodName}(${trace.fileName}:${trace.lineNumber})"
    }

    /**
     * find Class name of this log as tag
     */
    private fun parseTag(trace: StackTraceElement?): String {
        trace ?: return DEFAULT_TAG
        return trace.fileName?.split(".")?.firstOrNull() ?: DEFAULT_TAG
    }
}