package unicon.code.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Layout
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import unicon.code.CURRENT_LINE_COLOR
import unicon.code.LINE_NUMBER_COLOR


class CodeEditor(context: Context, var attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private val highlightPaint = Paint()
    private val dPaint = Paint()

    private val currentLineColor = Color.parseColor(CURRENT_LINE_COLOR)
    private val lineNumberColor = Color.parseColor(LINE_NUMBER_COLOR)

    init {
        setHorizontallyScrolling(true)

        highlightPaint.color = currentLineColor

        dPaint.textSize = textSize
        dPaint.color = lineNumberColor
    }

    override fun onDraw(canvas: Canvas?) {
        val line = getCurrentLine()
        highlightLine(line, canvas!!)

        for (i in 0 until lineCount) {
            val baseline = getLineBounds(i, null)
            val num = (i + 1).toString()

            canvas.drawText(num, (10 + scrollX).toFloat(), baseline.toFloat(), dPaint)
        }

        val x = scrollX + compoundPaddingLeft - 10
        val y = Math.max(
            lineHeight * lineCount,
            height
        ) + extendedPaddingBottom
        canvas.drawLine(x.toFloat(), 0f, x.toFloat(), y.toFloat(), dPaint)

        super.onDraw(canvas)
    }

    override fun getCompoundPaddingLeft(): Int {
        val offset = (textSize / 1.5).toInt()
        return lineCount.toString().length * offset + offset
    }

    private fun highlightLine(line: Int, canvas: Canvas) {
        val lineBounds = Rect()
        getLineBounds(line, lineBounds)
        lineBounds.left = 0
        canvas.drawRect(lineBounds, highlightPaint)
    }

    private fun getCurrentLine(): Int {
        val l: Layout? = layout
        return l?.getLineForOffset(selectionStart) ?: -1
    }
}