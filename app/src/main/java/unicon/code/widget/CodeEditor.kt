package unicon.code.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.text.Editable
import android.text.Layout
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import unicon.code.CURRENT_LINE_COLOR
import unicon.code.LINE_NUMBER_COLOR
import unicon.code.plugin.Plugin
import java.io.File
import java.nio.charset.Charset


class CodeEditor(context: Context, var attrs: AttributeSet) : AppCompatEditText(context, attrs) {
    private val highlightPaint = Paint()
    private val dPaint = Paint()

    private val currentLineColor = Color.parseColor(CURRENT_LINE_COLOR)
    private val lineNumberColor = Color.parseColor(LINE_NUMBER_COLOR)
    private val functionColor = Color.parseColor("#dd4462")

    private var currentFile: File? = null
    private var savedBuffer = ""

    private var openFileListener: ((file: File) -> Unit)? = null
    private var saveFileListener: ((file: File) -> Unit)? = null

    var currentPlugin: Plugin? = null

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

    fun setOnOpenFileListener(lam: (file: File) -> Unit) {
        openFileListener = lam
    }

    fun setOnSaveFileListener(lam: (file: File) -> Unit) {
        saveFileListener = lam
    }

    /* открыть файл */
    fun openFile(file: File) {
        currentFile = file

        if(openFileListener != null)
            openFileListener!!.invoke(file)

        if(file.exists()) {
            val content = file.readText(Charset.defaultCharset())

            savedBuffer = content
            setText(content)
        }
    }

    // закрыть файл
    fun closeFile() {
        currentFile = null
        savedBuffer = ""

        setText("")
    }

    // сохранить файл, в ответ возращает результат
    fun saveFile(): Boolean {
        if(currentFile != null) {
            if(saveFileListener != null) saveFileListener!!(currentFile!!)

            currentFile!!.writeText(text.toString(), Charset.defaultCharset())
            savedBuffer = text.toString()

            return true
        }

        return false
    }

    // проверить сохранены ли изменения
    fun isSaved(): Boolean {
        return savedBuffer == text.toString()
    }

    /* получить текущий файл */
    fun getCurrentFile(): File? {
        return currentFile
    }
}