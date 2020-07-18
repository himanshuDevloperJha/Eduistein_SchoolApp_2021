package co.aspirasoft.view

import android.content.Context
import android.util.AttributeSet
import androidx.viewpager.widget.ViewPager

class NestedViewPager : ViewPager {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec = heightMeasureSpec
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            var h = child.measuredHeight
            try {
                h *= (child as NestedListView).adapter?.count ?: 1
            } catch (ignored: Exception) {

            }
            if (h > heightSpec) heightSpec = h
        }
        heightSpec = MeasureSpec.makeMeasureSpec(heightSpec, MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

}