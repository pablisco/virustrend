package org.virustrend.android.views

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.counter_box.view.*
import org.virustrend.R
import java.text.NumberFormat
import kotlin.properties.Delegates

class CounterBox(context: Context, attrs: AttributeSet) : CardView(context, attrs) {

    var count by Delegates.observable(0) { _, _, new ->
        countView.text = new.formatted()
    }

    private val hint: String = attributeFrom(attrs, R.styleable.CounterBox) {
        getString(R.styleable.CounterBox_hint)
    } ?: ""

    override fun onFinishInflate() {
        super.onFinishInflate()
        View.inflate(context, R.layout.counter_box, this)
        hintView.text = hint
    }

}

private fun Int.formatted(): String = NumberFormat.getNumberInstance().format(this)

private fun <R> View.attributeFrom(set: AttributeSet, attrs: IntArray, block: TypedArray.() -> R) : R =
    context.theme
        .obtainStyledAttributes(set, attrs, 0, 0)
        .run {
            try {
                block()
            } finally {
                recycle()
            }
        }
