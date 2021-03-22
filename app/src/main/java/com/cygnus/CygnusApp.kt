package com.cygnus

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.orhanobut.hawk.Hawk

/**
 * An [Application] subclass represents this application.
 *
 * Purpose of this class is to define default behaviours, perform
 * SDK initializations and declare any shared data.
 */
class CygnusApp : Application() {

    override fun onCreate() {
            super.onCreate()

        // Hawk is used to manage SharedPreferences
        Hawk.init(this).build()

        // Enabling persistence speeds up app by caching data locally
        // db.setPersistenceEnabled(true)

        STATUS_INVITE_PENDING = getString(R.string.status_invite_pending)
        STATUS_INVITE_ACCEPTED = getString(R.string.status_invite_accepted)
    }

    companion object {
        // the STATUS_* strings are used to represent status of
        // different tasks
        lateinit var STATUS_INVITE_PENDING: String private set
        lateinit var STATUS_INVITE_ACCEPTED: String private set

        // the EXTRA_* strings are used as tags to pass
        // data between activities using Intents
        const val EXTRA_ACCOUNT_TYPE = "account_type"
        const val EXTRA_EDITABLE_MODE = "editable"
        const val EXTRA_INVITE_STATUS = "invite_status"
        const val EXTRA_INVITES = "invites"
        const val EXTRA_NEW_SIGN_UP = "new_user"
        const val EXTRA_NOTICE_POSTS = "notice_posts"
        const val EXTRA_CLASS_ATTENDANCE = "class_attendance"
        const val EXTRA_PROFILE_USER = "profile_user"
        const val EXTRA_REFERRAL_CODE = "referral_code"
        const val EXTRA_SCHOOL = "school"
        const val EXTRA_SCHOOL_SUBJECT = "subject"
        const val EXTRA_NEW_SIGN_IN = "new_sign_in"
        const val EXTRA_EXISTING_SIGN_IN = "after_sign_in"
        const val EXTRA_STUDENT_ROLL_NO = "roll_no"
        const val EXTRA_STUDENT_CLASS_ID = "class_id"
        const val EXTRA_TEST_NAME = "test_name"
        const val EXTRA_USER = "user"

        // the PARAM_* strings are used to define parameters
        // used in dynamic links
        const val PARAM_ACCOUNT_TYPE = "type"
        const val PARAM_LINK_TARGET = "continueUrl"
        const val PARAM_REFERRAL_CODE = "referral"
        const val PARAM_STUDENT_ROLL_NO = "roll_no"
        const val PARAM_STUDENT_CLASS_ID = "class_id"

        // the refTo* functions return a reference to resources
        // in the Firebase database
        private val db get() = FirebaseDatabase.getInstance()
        fun refToAttendance(schoolId: String, classId: String) = db.getReference("$schoolId/classes/$classId/attendance/")
        fun refToAttTeacher(schoolId: String) = db.getReference("$schoolId/teacherattendance/attendance/")
        fun refToClasses(schoolId: String) = db.getReference("$schoolId/classes/")
        fun refToClassNoticeBoard(schoolId: String, classId: String) = db.getReference("$schoolId/classes/$classId/notices/")
        fun refToSchoolPostsInstagram(schoolId: String) = db.getReference("$schoolId/Posts/")
        fun refToAdminPostsInstagram() = db.getReference("admin/posts/")
        fun refToAdminAdsInstagram() = db.getReference("admin/feedads/")
        fun refToAdminQuizAdsInstagram() = db.getReference("admin/quizads/")
        fun refToCoursesOffered() = db.getReference("coursesoffered/")
        fun refToInvites(schoolId: String) = db.getReference("$schoolId/invites/")
        fun refToSchoolId(userId: String) = db.getReference("user_schools/$userId/")
        fun refToSchoolName(schoolId: String) = db.getReference("$schoolId/name/")
        fun refToSubjects(schoolId: String, classId: String) = db.getReference("$schoolId/classes/$classId/subjects/")
        fun refToUsers(schoolId: String) = db.getReference("$schoolId/users/")
        fun refToTeachers(schoolId: String) = db.getReference("$schoolId/users/")

        fun refToTimestampUsers(schoolId: String) = db.getReference("$schoolId/NamewithTimestampMessages/")

    }

}