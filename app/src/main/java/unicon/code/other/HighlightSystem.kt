package unicon.code.other

import android.graphics.Color
import android.net.sip.SipSession
import android.text.Editable
import android.text.Spannable
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import java.util.regex.Pattern
import kotlin.concurrent.thread

class HighlightSystem() {
    private var spansListener: ((spans: ArrayList<SyntaxHighlightSpan>) -> Unit)? = null
    private var syntaxHighlightSpans: ArrayList<SyntaxHighlightSpan> = ArrayList()

    private var keywords: Pattern? = null
    private var state = false

    fun runTask(text: Editable) {
        if(keywords == null) return

        state = true
        syntaxHighlightSpans.clear()

        thread {
            while (state) {
                val matcher = keywords!!.matcher(text)
                matcher.region(0, text!!.length)
                while (matcher.find()) {
                    syntaxHighlightSpans.add(SyntaxHighlightSpan(
                            Color.parseColor("#7F0055"),
                            matcher.start(),
                            matcher.end()
                    ))
//                    text!!.setSpan(
//                            ForegroundColorSpan(Color.parseColor("#7F0055")),
//                            matcher.start(),
//                            matcher.end(),
//                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//                    )
                }

                Thread.sleep(250)
                if(spansListener != null)
                    spansListener!!.invoke(syntaxHighlightSpans)
            }
        }
    }

    fun cancelTask() { state = false }

    fun updatePattern(pattern: Pattern) {
        keywords = pattern
    }

    fun setOnSpansListener(lam: (spans: ArrayList<SyntaxHighlightSpan>) -> Unit) {
        spansListener = lam
    }
}

data class SyntaxHighlightSpan(private val color: Int, val start: Int, val end: Int) : CharacterStyle() {
    override fun updateDrawState(textPaint: TextPaint?) {
        textPaint?.color = color
    }
}