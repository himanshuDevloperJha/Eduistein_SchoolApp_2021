package com.cygnus.model

import co.aspirasoft.model.BaseModel
import com.google.firebase.storage.StorageMetadata

class CourseFile(val name: String, val metadata: StorageMetadata) : BaseModel() {

    override fun toString(): String {
        return name
    }

}