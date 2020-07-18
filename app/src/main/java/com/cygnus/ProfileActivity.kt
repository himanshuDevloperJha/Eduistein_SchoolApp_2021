package com.cygnus

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import co.aspirasoft.util.PermissionUtils
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.School
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.storage.FileManager
import com.cygnus.storage.ImageLoader
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_profile.*
import java.io.ByteArrayOutputStream


class ProfileActivity : DashboardChildActivity() {

    private lateinit var mFileManager: FileManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val user = intent.getSerializableExtra(CygnusApp.EXTRA_PROFILE_USER) as User? ?: currentUser
        if (user.id != currentUser.id) {
            changeUserImageButton.visibility = View.GONE
            changePasswordButton.visibility = View.GONE
            deleteAccountButton.visibility = View.GONE
        }
        currentUser = user
        mFileManager = FileManager.newInstance(this, "users/${currentUser.id}/")

        changeUserImageButton.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.permission_storage),
                            RC_WRITE_PERMISSION
                    )) {
                pickImage()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_ACTION_LOAD -> if (resultCode == Activity.RESULT_OK) {
                data?.data?.let { selectedImage ->
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    contentResolver.query(
                            selectedImage,
                            filePathColumn,
                            null,
                            null,
                            null
                    )?.let { cursor ->
                        cursor.moveToFirst()

                        val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                        val picturePath: String = cursor.getString(columnIndex)
                        cursor.close()

                        // Crop center region
                        val source = BitmapFactory.decodeFile(picturePath)
                        val bitmap = Bitmap.createScaledBitmap(if (source.width >= source.height) {
                            Bitmap.createBitmap(
                                    source,
                                    source.width / 2 - source.height / 2,
                                    0,
                                    source.height,
                                    source.height
                            )
                        } else {
                            Bitmap.createBitmap(
                                    source,
                                    0,
                                    source.height / 2 - source.width / 2,
                                    source.width,
                                    source.width
                            )
                        }, 256, 256, false)

                        val outputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        val bytes = outputStream.toByteArray()

                        val status = Snackbar.make(userImage, "Uploading...", Snackbar.LENGTH_INDEFINITE)
                        status.show()
                        mFileManager.upload("photo.png", bytes)
                                .addOnFailureListener {
                                    status.setText(it.message ?: "Failed to upload photo.")
                                    Handler().postDelayed({ status.dismiss() }, 1500L)
                                }
                                .addOnSuccessListener {
                                    status.setText("Photo uploaded.")
                                    Handler().postDelayed({ status.dismiss() }, 1500L)
                                    showUserImage(true)
                                }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            }
        }
    }

    override fun updateUI(currentUser: User) {
        // PROFILE IMAGE
        showUserImage()

        // BASIC INFO
        userNameLabel.text = currentUser.name
        headline.text = currentUser.type

        // PERSONAL INFO
        if (currentUser is School) personalSection.visibility = View.GONE
        if (currentUser is Student) {
            birthdayLabel.visibility = View.VISIBLE
            birthdayLabel.text = currentUser.dateOfBirth
        }
        genderLabel.text = currentUser.gender

        // CONTACT INFO
        if (currentUser is School) {
            addressLabel.visibility = View.GONE
            phoneLabel.visibility = View.GONE
        }
        addressLabel.text = currentUser.address
        userEmailLabel.text = currentUser.email
        phoneLabel.text = currentUser.phone

        // ACADEMIC INFO
        academicSection.visibility = View.VISIBLE
        when (currentUser) {
            is Student -> {
                className.text = currentUser.classId
                rollNo.text = "Roll # ${currentUser.rollNo}"
            }
            is Teacher -> {
                className.text = when (currentUser.classId.isNullOrBlank()) {
                    true -> "No Class Assigned"
                    else -> currentUser.classId
                }
                rollNo.visibility = View.GONE
            }
            is School -> academicSection.visibility = View.GONE
        }

        // EMERGENCY INFO
        when (currentUser) {
            is Student -> {
                emergencySection.visibility = View.VISIBLE
                bloodType.text = "${currentUser.bloodType}"
                emergencyContactName.text = currentUser.emergencyContact
                emergencyContactEmail.text = when {
                    currentUser.emergencyEmail.isNullOrBlank() -> "Not Set"
                    else -> currentUser.emergencyEmail
                }
                emergencyContactPhone.text = currentUser.emergencyPhone
            }
            else -> emergencySection.visibility = View.GONE
        }

        // ACCOUNT SETTINGS
        // TODO: Allow password reset
        // TODO: Allow account deletion
    }

    private fun showUserImage(invalidate: Boolean = false) {
        ImageLoader.with(this)
                .load(currentUser)
                .skipCache(invalidate)
                .into(userImage)
    }

    private fun pickImage() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(i, RESULT_ACTION_LOAD)
    }

    companion object {
        private const val RESULT_ACTION_LOAD = 100
        private const val RC_WRITE_PERMISSION = 200
    }

}