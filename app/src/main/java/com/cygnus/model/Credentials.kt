package com.cygnus.model

import co.aspirasoft.model.BaseModel

class Credentials(var email: String, var password: String) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this("", "")

}