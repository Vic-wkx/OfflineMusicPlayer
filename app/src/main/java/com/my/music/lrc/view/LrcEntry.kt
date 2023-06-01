package com.my.music.lrc.view

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint

data class LrcEntry constructor(
    val time: Long, val text: String
) : Comparable<LrcEntry?> {

    lateinit var staticLayout: StaticLayout

    /**
     * The distance of lyrics from the top of the view
     */
    var offset = Float.MIN_VALUE

    fun init(paint: TextPaint, width: Int) {
        staticLayout = StaticLayout.Builder.obtain(text, 0, text.length, paint, width)
            .setAlignment(Layout.Alignment.ALIGN_CENTER)
            .build()
        offset = Float.MIN_VALUE
    }

    val height: Int
        get() = staticLayout.height

    override fun compareTo(other: LrcEntry?): Int {
        return if (other == null) {
            -1
        } else (time - other.time).toInt()
    }
}