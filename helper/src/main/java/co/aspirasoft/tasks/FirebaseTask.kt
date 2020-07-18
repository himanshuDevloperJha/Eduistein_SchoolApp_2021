package co.aspirasoft.tasks

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*


/**
 * Interface for creating tasks involving operations on [FirebaseDatabase].
 */
abstract class FirebaseTask : ValueEventListener {

    /**
     * Listener for when the task execution finishes.
     */
    lateinit var onTaskCompleteListener: (Task<Void?>) -> Unit

    /**
     * Callback for when result for initial query is received.
     */
    final override fun onDataChange(snapshot: DataSnapshot) {
        onTaskCompleteListener(if (checkCriteria(snapshot)) onQuerySuccess() else onQueryFailure())
    }

    /**
     * Callback for when initial query fails.
     *
     * @param error The error which caused this failure.
     */
    final override fun onCancelled(error: DatabaseError) {
        onTaskCompleteListener(DummyTask(error.toException()))
    }

    /**
     * Create an initial database [Query].
     *
     * This initial query can be used to define a criteria for task execution.
     * Query result received in [checkCriteria] can be used to check if the
     * criteria is met.
     *
     * @return [Query] Initial query, or null if no preconditions
     */
    protected abstract fun init(): Query?

    /**
     * Checks the precondition for executing the task.
     *
     * Return `true` if conditions satisfied or no initial query.
     */
    protected abstract fun checkCriteria(snapshot: DataSnapshot): Boolean

    /**
     * Called when the initial query succeeds or no query was specified.
     *
     * Actual execution of the task happens here.
     */
    protected abstract fun onQuerySuccess(): Task<Void?>

    /**
     * Called when the initial query fails.
     *
     * Task failure is handled here.
     */
    protected abstract fun onQueryFailure(): Task<Void?>

    /**
     * Starts execution of the task.
     */
    fun start(onTaskCompleteListener: (Task<Void?>) -> Unit) {
        this.onTaskCompleteListener = onTaskCompleteListener
        init()?.addListenerForSingleValueEvent(this) ?: onTaskCompleteListener(onQuerySuccess())
    }

}