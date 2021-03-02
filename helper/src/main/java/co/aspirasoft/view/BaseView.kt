package co.aspirasoft.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import co.aspirasoft.model.BaseModel
import java.util.*

abstract class BaseView<T : BaseModel>(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : Observer, LinearLayout(context, attrs, defStyleAttr) {

    abstract fun updateView(model: T)

    override fun update(o: Observable?, arg: Any?) {
        o?.let {
            updateView(it as T)
        }
    }

    fun bindWithModel(model:T) {
        model.addObserver(this)
        model.notifyObservers()
    }

}