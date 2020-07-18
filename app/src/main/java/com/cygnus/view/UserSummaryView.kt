package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.User
import com.cygnus.storage.ImageLoader

/**
 * UserSummaryView is custom view for displaying a summary of user account.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class UserSummaryView : BaseView<User> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val userImage: ImageView
    private val userNameLabel: TextView
    private val userSchoolLabel: TextView
    private val headlineLabel: TextView

    private var onProfileButtonClickedListener: ((user: User) -> Unit)? = null

    fun setOnProfileButtonClickedListener(onProfileButtonClickedListener: (user: User) -> Unit) {
        this.onProfileButtonClickedListener = onProfileButtonClickedListener
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_summary, this)
        userImage = findViewById(R.id.userImage)
        userNameLabel = findViewById(R.id.userNameLabel)
        userSchoolLabel = findViewById(R.id.userEmailLabel)
        headlineLabel = findViewById(R.id.headline)
    }

    /**
     * Displays user details.
     */
    override fun updateView(model: User) {
        userNameLabel.text = model.name
        userSchoolLabel.text = model.credentials.email
        headlineLabel.text = model.type
        findViewById<Button>(R.id.profileButton).setOnClickListener {
            onProfileButtonClickedListener?.let { it(model) }
        }

        ImageLoader.with(context)
                .load(model)
                .into(userImage)
    }

}