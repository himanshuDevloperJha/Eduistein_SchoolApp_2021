package co.aspirasoft.tasks

import android.app.Activity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executor

/**
 * DummyTask is a placeholder [Task].
 *
 * Purpose of this class is to quickly create an empty task with a successful
 * or failed state which we need in some use cases. This task immediately
 * completes.
 *
 * @param error Exception specifying cause of failure, or null if task successful
 */
class DummyTask(private val error: Exception?) : Task<Void?>() {

    override fun isComplete(): Boolean {
        return true
    }

    override fun getException(): Exception? {
        return error
    }

    override fun addOnFailureListener(listener: OnFailureListener): Task<Void?> {
        exception?.let { listener.onFailure(it) }
        return this
    }

    override fun addOnFailureListener(executor: Executor, listener: OnFailureListener): Task<Void?> {
        return addOnFailureListener(listener)
    }

    override fun addOnFailureListener(activity: Activity, listener: OnFailureListener): Task<Void?> {
        return addOnFailureListener(listener)
    }

    override fun getResult(): Void? {
        return null
    }

    override fun <X : Throwable?> getResult(p0: Class<X>): Void? {
        return null
    }

    override fun addOnSuccessListener(listener: OnSuccessListener<in Void?>): Task<Void?> {
        if (isSuccessful) listener.onSuccess(null)
        return this
    }

    override fun addOnSuccessListener(executor: Executor, listener: OnSuccessListener<in Void?>): Task<Void?> {
        return addOnSuccessListener(listener)
    }

    override fun addOnSuccessListener(activity: Activity, listener: OnSuccessListener<in Void?>): Task<Void?> {
        return addOnSuccessListener(listener)
    }

    override fun addOnCompleteListener(listener: OnCompleteListener<Void?>): Task<Void?> {
        listener.onComplete(this)
        return this
    }

    override fun isSuccessful(): Boolean {
        return error == null
    }

    override fun isCanceled(): Boolean {
        return false
    }

}