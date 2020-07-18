package co.aspirasoft.view

import android.util.SparseArray
import androidx.fragment.app.Fragment

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 05/04/2019 6:18 PM
 */
abstract class WizardViewStep(val title: String) : Fragment() {

    val data = SparseArray<Any>()

    abstract fun isDataValid(): Boolean

}