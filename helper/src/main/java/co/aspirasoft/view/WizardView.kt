package co.aspirasoft.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import co.aspirasoft.R
import kotlinx.android.synthetic.main.view_wizard.view.*

/**
 * WizardView allows creation of a UI with multiple steps.
 *
 * This view can be used to break down complex tasks into simpler steps and
 * present them to the user one after the other. It can be used to create
 * intros, sign up forms, etc.
 *
 * @author saifkhichi96
 * @version 1.0.0
 */
class WizardView(context: Context, attrs: AttributeSet?, defStyleInt: Int)
    : LinearLayout(context, attrs, defStyleInt) {

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    var icon: Drawable? = null
        set(value) {
            field = value
            wizardIcon.visibility = if (value != null) View.VISIBLE else View.GONE
            wizardIcon.setImageDrawable(value)
        }

    var isDividerShown: Boolean = true
        set(value) {
            field = value
            divider.visibility = if (value) View.VISIBLE else View.GONE
        }

    var isIndicatorShown: Boolean = true
        set(value) {
            field = value
            stepsView.visibility = if (value) View.VISIBLE else View.GONE
        }

    var isTitleShown: Boolean = true
        set(value) {
            field = value
            wizardTitle.visibility = if (value) View.VISIBLE else View.GONE
        }

    var nextLabel: String = "Next"
        set(value) {
            field = value
            nextButton.text = value
        }

    var submitLabel: String = "Submit"

    init {
        View.inflate(context, R.layout.view_wizard, this)
        context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.WizardView,
                0, 0).apply {

            try {
                icon = getDrawable(R.styleable.WizardView_icon)

                isDividerShown = getBoolean(R.styleable.WizardView_showDivider, true)
                isIndicatorShown = getBoolean(R.styleable.WizardView_showIndicator, true)
                isTitleShown = getBoolean(R.styleable.WizardView_showTitle, true)

                nextLabel = getString(R.styleable.WizardView_nextLabel) ?: nextLabel
                submitLabel = getString(R.styleable.WizardView_submitLabel) ?: submitLabel
            } finally {
                recycle()
            }
        }
    }

    private var onSubmitListener: ((data: SparseArray<Any>) -> Unit)? = null

    private lateinit var mSteps: List<WizardViewStep>

    private val isCurrentInputValid: Boolean
        get() = mSteps[stepsView.statusView.currentCount - 1].isDataValid()

    private val isFirstStep: Boolean
        get() = stepsView.statusView.currentCount == 1

    private val isLastStep: Boolean
        get() = stepsView.statusView.currentCount == mSteps.size

    private val data: SparseArray<Any>
        get() {
            val wizardData = SparseArray<Any>()
            for (step in mSteps) {
                for (i in 0 until step.data.size()) {
                    val key = step.data.keyAt(i)
                    val value = step.data.valueAt(i)
                    wizardData.put(key, value)
                }
            }
            return wizardData
        }

    fun setupWithWizardSteps(supportFragmentManager: FragmentManager, steps: List<WizardViewStep>) {
        this.mSteps = steps
        stepsView.statusView.stepCount = steps.size

        selectPosition(0)

        val adapter = WizardStepAdapter(supportFragmentManager, steps)
        contentView.adapter = adapter
        contentView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(newPosition: Int) {
                val currentPosition = stepsView.statusView.currentCount - 1
                if (newPosition <= currentPosition || isCurrentInputValid) {
                    selectPosition(newPosition)
                } else {
                    contentView.currentItem = currentPosition
                }

                nextButton.text = if (isLastStep) submitLabel else nextLabel
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

        nextButton.setOnClickListener {
            if (isCurrentInputValid) {
                onNextClicked()
            }
        }
    }

    fun setOnSubmitListener(l: ((data: SparseArray<Any>) -> Unit)?) {
        onSubmitListener = l
    }

    fun onBackPressed(): Boolean {
        return if (!isFirstStep) {
            contentView.currentItem = contentView.currentItem - 1
            true
        } else false
    }

    private fun onNextClicked() {
        if (isLastStep) {
            onSubmitListener?.let { it(data) }
        } else {
            contentView.currentItem = contentView.currentItem + 1
        }
    }

    private fun selectPosition(position: Int) {
        stepsView.statusView.currentCount = position + 1
        wizardTitle.text = mSteps[position].title
    }

    class WizardStepAdapter(manager: FragmentManager, private val steps: List<WizardViewStep>)
        : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int {
            return steps.size
        }

        override fun getItem(position: Int): WizardViewStep {
            return steps[if (position < count) position else 0]
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return steps[position].title
        }

    }

}