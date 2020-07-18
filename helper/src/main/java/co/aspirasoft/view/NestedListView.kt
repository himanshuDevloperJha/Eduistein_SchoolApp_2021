package co.aspirasoft.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView

class NestedListView : ListView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightSpec: Int = if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
            MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        } else {
            heightMeasureSpec
        }

        super.onMeasure(widthMeasureSpec, heightSpec)
    }

}