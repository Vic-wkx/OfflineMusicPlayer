package com.my.music.lrc.view

import android.text.format.DateUtils
import java.io.File
import java.util.Locale
import java.util.regex.Pattern

/**
 * 工具类
 */
internal object LrcUtils {
    private val PATTERN_LINE = Pattern.compile("((\\[\\d\\d:\\d\\d\\.\\d{2,3}])+)(.+)")
    private val PATTERN_TIME = Pattern.compile("\\[(\\d\\d):(\\d\\d)\\.(\\d{2,3})]")

    /**
     * Parse lrc from file
     */
    fun parseLrc(lrcFile: File): List<LrcEntry> {
        if (!lrcFile.exists()) return emptyList()
        val entryList: MutableList<LrcEntry> = ArrayList()
        lrcFile.forEachLine { line ->
            entryList.addAll(parseLine(line))
        }
        entryList.sort()
        return entryList
    }

    /**
     * Parse a line of lrc
     */
    private fun parseLine(line: String): List<LrcEntry> {
        // [00:00.000]Content
        val lineMatcher = PATTERN_LINE.matcher(line)
        if (!lineMatcher.matches()) return emptyList()
        val times = lineMatcher.group(1)!!
        val text = lineMatcher.group(3)!!
        val entryList: MutableList<LrcEntry> = ArrayList()
        // [00:00.000]
        val timeMatcher = PATTERN_TIME.matcher(times)
        while (timeMatcher.find()) {
            val min = timeMatcher.group(1)!!.toLong()
            val sec = timeMatcher.group(2)!!.toLong()
            val milString = timeMatcher.group(3)
            var mil = milString!!.toLong()
            // If the millisecond is two digits, it needs to be multiplied by 10.
            if (milString.length == 2) {
                mil *= 10
            }
            val time = min * DateUtils.MINUTE_IN_MILLIS + sec * DateUtils.SECOND_IN_MILLIS + mil
            entryList.add(LrcEntry(time, text))
        }
        return entryList
    }

    /**
     * Turn to [minutes: seconds]
     */
    fun formatTime(milli: Long): String {
        val m = (milli / DateUtils.MINUTE_IN_MILLIS).toInt()
        val s = (milli / DateUtils.SECOND_IN_MILLIS % 60).toInt()
        val mm = String.format(Locale.getDefault(), "%02d", m)
        val ss = String.format(Locale.getDefault(), "%02d", s)
        return "$mm:$ss"
    }

//    fun formatTimeWithMills(milli: Long): String {
//        val m = (milli / DateUtils.MINUTE_IN_MILLIS).toInt()
//        val s = (milli / DateUtils.SECOND_IN_MILLIS % 60).toInt()
//        val milli = (milli % DateUtils.SECOND_IN_MILLIS).toInt()
//        val mm = String.format(Locale.getDefault(), "%02d", m)
//        val ss = String.format(Locale.getDefault(), "%02d", s)
//        val SSS = String.format(Locale.getDefault(), "%03d", milli)
//        return "[$mm:$ss.$SSS]"
//    }
}