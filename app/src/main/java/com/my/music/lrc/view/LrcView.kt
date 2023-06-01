package com.my.music.lrc.view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.use
import com.my.music.R
import com.my.music.lrc.view.LrcUtils.formatTime
import com.my.music.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

class LrcView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    companion object {
        private const val ADJUST_DURATION: Long = 100
        private const val TIMELINE_KEEP_TIME = 4 * DateUtils.SECOND_IN_MILLIS
    }

    private val lrcEntryList: MutableList<LrcEntry> = ArrayList()
    private val lrcPaint = TextPaint()
    private val timePaint = TextPaint()
    private var timeFontMetrics: Paint.FontMetrics
    private lateinit var playDrawable: Drawable
    private var dividerHeight = 0f
    private var animationDuration: Long = 0
    private var normalTextColor = 0
    private var normalTextSize = 0f
    private var currentTextColor = 0
    private var currentTextSize = 0f
    private var timelineTextColor = 0
    private var timelineColor = 0
    private var timeTextColor = 0
    private val drawableSize: Int
    private var timeTextWidth = 0
    private lateinit var defaultLabel: String
    private var lrcPadding = 0f
    lateinit var onPlayClickListener: (lrcView: LrcView, time: Long) -> Unit
    private var flingAnimator: ValueAnimator? = null
    private var gestureDetector: GestureDetector
    private lateinit var scroller: Scroller
    private var offset = 0f
    private var currentLine = -1
    private var isShowTimeline = false
    private var isTouching = false
    private var isFling = false

    private val simpleOnGestureListener: SimpleOnGestureListener = object : SimpleOnGestureListener() {
        // Stop the fling of lrc if it's fling.
        private var isTouchForStopFling = false
        override fun onDown(e: MotionEvent): Boolean {
            isTouching = true
            removeCallbacks(hideTimelineRunnable)
            if (isFling) {
                isTouchForStopFling = true
                scroller.forceFinished(true)
            } else {
                isTouchForStopFling = false
            }
            return true
        }

        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            if (!hasLrc()) {
                return super.onScroll(e1, e2, distanceX, distanceY)
            }
            if (!isShowTimeline) {
                isShowTimeline = true
            } else {
                offset -= distanceY
                offset = offset.coerceAtMost(getOffset(0)).coerceAtLeast(getOffset(lrcEntryList.size - 1))
            }
            invalidate()
            return true
        }

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            if (!hasLrc()) {
                return super.onFling(e1, e2, velocityX, velocityY)
            }
            if (isShowTimeline) {
                isFling = true
                removeCallbacks(hideTimelineRunnable)
                scroller.fling(0, offset.toInt(), 0, velocityY.toInt(), 0, 0, getOffset(lrcEntryList.size - 1).toInt(), getOffset(0).toInt())
                return true
            }
            return super.onFling(e1, e2, velocityX, velocityY)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (hasLrc() && isShowTimeline && playDrawable.bounds.contains(e.x.toInt(), e.y.toInt())) {
                onPlayClickListener(this@LrcView, lrcEntryList[centerLine].time)
                isShowTimeline = false
                removeCallbacks(hideTimelineRunnable)
                currentLine = centerLine
                invalidate()
                return true
            }
            return super.onSingleTapConfirmed(e)
        }
    }

    private val hideTimelineRunnable = Runnable {
        LogUtils.d("Hide timeline")
        if (hasLrc() && isShowTimeline) {
            isShowTimeline = false
            smoothScrollTo(currentLine)
        }
    }

    /**
     * Gets the number of rows currently in the center of the view.
     */
    private val centerLine: Int
        get() {
            var centerLine = 0
            var minDistance = Float.MAX_VALUE
            for (i in lrcEntryList.indices) {
                if (abs(offset - getOffset(i)) < minDistance) {
                    minDistance = abs(offset - getOffset(i))
                    centerLine = i
                }
            }
            return centerLine
        }

    private val lrcWidth: Float
        get() = width - lrcPadding * 2

    init {
        context?.obtainStyledAttributes(attrs, R.styleable.LrcView)?.use { ta ->
            currentTextSize = ta.getDimension(R.styleable.LrcView_lrcTextSize, resources.getDimension(R.dimen.lrc_text_size))
            normalTextSize = ta.getDimension(R.styleable.LrcView_lrcNormalTextSize, resources.getDimension(R.dimen.lrc_text_size))
            dividerHeight = ta.getDimension(R.styleable.LrcView_lrcDividerHeight, resources.getDimension(R.dimen.lrc_divider_height))
            animationDuration = ta.getInt(R.styleable.LrcView_lrcAnimationDuration, resources.getInteger(R.integer.lrc_animation_duration)).toLong()
            currentTextColor = ta.getColor(R.styleable.LrcView_lrcCurrentTextColor, context.getColor(R.color.lrc_current_text_color))
            normalTextColor = ta.getColor(R.styleable.LrcView_lrcNormalTextColor, context.getColor(R.color.lrc_normal_text_color))
            timelineTextColor = ta.getColor(R.styleable.LrcView_lrcTimelineTextColor, context.getColor(R.color.lrc_timeline_text_color))
            timelineColor = ta.getColor(R.styleable.LrcView_lrcTimelineColor, context.getColor(R.color.lrc_timeline_color))
            timeTextColor = ta.getColor(R.styleable.LrcView_lrcTimeTextColor, context.getColor(R.color.lrc_time_text_color))
            playDrawable = ta.getDrawable(R.styleable.LrcView_lrcPlayDrawable) ?: AppCompatResources.getDrawable(context, R.drawable.ic_lrc_play) ?: throw Exception("Didn't find R.drawable.ic_lrc_play")
            defaultLabel = ta.getString(R.styleable.LrcView_lrcLabel) ?: context.getString(R.string.lrc_label)
            lrcPadding = ta.getDimension(R.styleable.LrcView_lrcPadding, 0f)
            timePaint.apply {
                textSize = ta.getDimension(R.styleable.LrcView_lrcTimeTextSize, resources.getDimension(R.dimen.lrc_time_text_size))
                strokeWidth = ta.getDimension(R.styleable.LrcView_lrcTimelineHeight, resources.getDimension(R.dimen.lrc_timeline_height))
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
                strokeCap = Paint.Cap.ROUND
            }
        }
        timeFontMetrics = timePaint.fontMetrics
        drawableSize = resources.getDimension(R.dimen.lrc_drawable_size).toInt()
        timeTextWidth = resources.getDimension(R.dimen.lrc_time_width).toInt()
        lrcPaint.apply {
            isAntiAlias = true
            textSize = currentTextSize
            textAlign = Paint.Align.LEFT
        }
        gestureDetector = GestureDetector(context, simpleOnGestureListener)
        gestureDetector.setIsLongpressEnabled(false)
        scroller = Scroller(context)
    }

    suspend fun loadLrc(lrcFile: File, time: Long = 0) {
        LogUtils.d("Start to load $lrcFile, time: $time")
        withContext(Dispatchers.Main) {
            reset()
        }
        withContext(Dispatchers.IO) {
            val lrcEntries = LrcUtils.parseLrc(lrcFile)
            lrcEntries.forEach { lrcEntry ->
                lrcEntry.init(lrcPaint, lrcWidth.toInt())
            }
            lrcEntryList.addAll(lrcEntries)
//            LogUtils.d("~~~\n" + lrcEntryList.joinToString("\n") {
//                LrcUtils.formatTimeWithMills(it.time - 3000) + it.text
//            })
            updateTime(time, 0)
            LogUtils.d("Lrc $lrcFile loaded. time: $time")
        }
    }

    fun hasLrc() = lrcEntryList.isNotEmpty()

    /**
     * Refresh lrc by time
     */
    suspend fun updateTime(time: Long, duration: Long = animationDuration) {
        if (!hasLrc()) {
            return
        }
        val line = findShowLine(time)
        if (line != currentLine) {
            currentLine = line
            LogUtils.d("update line: $currentLine, time: ${formatTime(time)}, text: ${lrcEntryList[currentLine].text}")
            withContext(Dispatchers.Main) {
                if (!isShowTimeline) {
                    smoothScrollTo(line, duration)
                } else {
                    invalidate()
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            LogUtils.d("onLayout changed.")
            setupPlayDrawableBounds()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerY = height / 2

        if (!hasLrc()) {
            // Draw default label if there's no lrc.
            lrcPaint.color = currentTextColor
            val staticLayout = StaticLayout.Builder.obtain(defaultLabel, 0, defaultLabel.length, lrcPaint, lrcWidth.toInt())
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .build()
            drawText(canvas, staticLayout, centerY.toFloat())
            return
        }
        if (isShowTimeline) {
            playDrawable.draw(canvas)
            timePaint.color = timelineColor
            canvas.drawLine(timeTextWidth.toFloat(), centerY.toFloat(), (width - timeTextWidth).toFloat(), centerY.toFloat(), timePaint)
            timePaint.color = timeTextColor
            val timeText = formatTime(lrcEntryList[centerLine].time)
            val timeX = (width - timeTextWidth / 2).toFloat()
            val timeY = centerY - (timeFontMetrics.descent + timeFontMetrics.ascent) / 2
            canvas.drawText(timeText, timeX, timeY, timePaint)
        }
        canvas.translate(0f, offset)
        var y = 0f
        for (i in lrcEntryList.indices) {
            if (i > 0) {
                y += (lrcEntryList[i - 1].height + lrcEntryList[i].height) / 2 + dividerHeight
            }
            if (i == currentLine) {
                lrcPaint.textSize = currentTextSize
                lrcPaint.color = currentTextColor
            } else if (isShowTimeline && i == centerLine) {
                lrcPaint.color = timelineTextColor
            } else {
                lrcPaint.textSize = normalTextSize
                lrcPaint.color = normalTextColor
            }
            drawText(canvas, lrcEntryList[i].staticLayout, y)
        }
    }

    /**
     * Draw a line of lrc
     *
     * @param y The center of the line of lrc
     */
    private fun drawText(canvas: Canvas, staticLayout: StaticLayout, y: Float) {
        canvas.save()
        canvas.translate(lrcPadding, y - staticLayout.height / 2)
        staticLayout.draw(canvas)
        canvas.restore()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            isTouching = false
            // Finger off the screen, start the delay task, and restore the position of lyrics.
            if (hasLrc() && isShowTimeline) {
                adjustCenter()
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME)
            }
        }
        return gestureDetector.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            offset = scroller.currY.toFloat()
            invalidate()
        }
        if (isFling && scroller.isFinished) {
            LogUtils.d("Fling is finished.")
            isFling = false
            if (hasLrc() && !isTouching) {
                adjustCenter()
                postDelayed(hideTimelineRunnable, TIMELINE_KEEP_TIME)
            }
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(hideTimelineRunnable)
        super.onDetachedFromWindow()
    }

    private fun setupPlayDrawableBounds() {
        LogUtils.d("setupPlayDrawableBounds")
        val left = (timeTextWidth - drawableSize) / 2
        val top = height / 2 - drawableSize / 2
        val right = left + drawableSize
        val bottom = top + drawableSize
        playDrawable.setBounds(left, top, right, bottom)
    }

    private fun reset() {
        stopFlingAnimation()
        scroller.forceFinished(true)
        isShowTimeline = false
        isTouching = false
        isFling = false
        removeCallbacks(hideTimelineRunnable)
        lrcEntryList.clear()
        offset = 0f
        currentLine = -1
    }

    /**
     * Adjust the line to the center of itself
     */
    private fun adjustCenter() {
        smoothScrollTo(centerLine, ADJUST_DURATION)
    }

    /**
     * Scroll to a specific line
     */
    private fun smoothScrollTo(line: Int, duration: Long = animationDuration) {
        LogUtils.d("smoothScrollTo: $line, duration: $duration", 1)
        val offset = getOffset(line)
        stopFlingAnimation()
        if (duration == 0L) {
            this.offset = offset
            invalidate()
        } else {
            flingAnimator = ValueAnimator.ofFloat(this.offset, offset).apply {
                setDuration(duration)
                interpolator = LinearInterpolator()
                addUpdateListener { animation: ValueAnimator ->
                    this@LrcView.offset = animation.animatedValue as Float
                    invalidate()
                }
                start()
            }
        }
    }

    private fun stopFlingAnimation() {
        if (flingAnimator?.isRunning == true) {
            flingAnimator?.end()
        }
    }

    /**
     * Dichotomy finds the number of lines that should be displayed at the current time.
     */
    private fun findShowLine(time: Long): Int {
        var left = 0
        var right = lrcEntryList.size
        while (left <= right) {
            val middle = (left + right) / 2
            val middleTime = lrcEntryList[middle].time
            if (time < middleTime) {
                right = middle - 1
            } else {
                if (middle + 1 >= lrcEntryList.size || time < lrcEntryList[middle + 1].time) {
                    return middle
                }
                left = middle + 1
            }
        }
        return 0
    }

    /**
     * Gets the distance between the lyrics and the top of the view.
     * Adopt lazy loading mode
     */
    private fun getOffset(line: Int): Float {
        if (lrcEntryList[line].offset == Float.MIN_VALUE) {
            var offset = (height / 2).toFloat()
            for (i in 1..line) {
                offset -= (lrcEntryList[i - 1].height + lrcEntryList[i].height) / 2 + dividerHeight
            }
            lrcEntryList[line].offset = offset
        }
        return lrcEntryList[line].offset
    }
}