package co.aspirasoft.model

import java.io.Serializable
import java.util.*

abstract class BaseModel : Observable(), Serializable {

    override fun notifyObservers() {
        setChanged()
        super.notifyObservers()
        clearChanged()
    }

}