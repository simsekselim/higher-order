package com.gap.higher_order

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KProperty0

/**
 * @Author: Selim Simsek
 * @Date: 2.05.2023
 */

fun View.onClick(action: () -> Unit) {
    setOnClickListener { action() }
}

/**
button.onClick {
    Toast.makeText(context, "Button clicked", Toast.LENGTH_SHORT).show()
}
*/

fun View.animateProperty(
    property: KProperty0<Float>,
    fromValue: Float,
    toValue: Float,
    duration: Long,
    onComplete: () -> Unit = {}
) {
    val animator = ObjectAnimator.ofFloat(this, property.name, fromValue, toValue).apply {
        setDuration(duration)
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onComplete()
            }
        })
    }
    animator.start()
}

/**
view.animateProperty(
    View.TRANSLATION_X,
    fromValue = 0f,
    toValue = 100f,
    duration = 500,
    onComplete = { onAnimationComplete() }
)
*/

fun <T> RecyclerView.bindData(
    data: List<T>,
    layoutRes: Int,
    bindFunc: (View, T) -> Unit,
    clickListener: ((T) -> Unit)? = null
) {
    adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val item = data[position]
            bindFunc(holder.itemView, item)
            clickListener?.let { listener ->
                holder.itemView.setOnClickListener { listener(item) }
            }
        }

        override fun getItemCount() = data.size
        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    }
}

/**
recyclerView.bindData(
    data = listOf("item1", "item2", "item3"),
    layoutRes = R.layout.list_item,
    bindFunc = { view, item -> view.findViewById<TextView>(R.id.text_view).text = item },
    clickListener = { item -> onItemClick(item) }
)
*/


fun <T> runOnBackgroundThread(backgroundFunc: () -> T, callback: (T) -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    Thread {
        val result = backgroundFunc()
        handler.post { callback(result) }
    }.start()
}

/**
runOnBackgroundThread(
    { doExpensiveCalculation() },
    { onResultLoaded(it) }
)
*/

fun Activity.withPermissions(vararg permissions: String, callback: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val ungrantedPermissions = permissions.filter {
            checkSelfPermission(it) == PackageManager.PERMISSION_DENIED
        }
        if (ungrantedPermissions.isEmpty()) {
            callback()
        } else {
            requestPermissions(ungrantedPermissions.toTypedArray(), 0)
        }
    } else {
        callback()
    }
}

/**
withPermissions(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
) {
    // Code to execute when permissions are granted
}
*/


fun String.validate(validationFunc: (String) -> Boolean): Boolean {
    return validationFunc(this)
}

/**
val input = "example input"
val isInputValid = input.validate { input -> input.isNotEmpty() }
*/

fun <T> log(tag: String, message: String, function: () -> T): T {
    Log.d(tag, message)
    val result = function()
    Log.d(tag, "Function result: $result")
    return result
}

/**
val result = log("myTag", "Calculating result...") {
    // Perform some expensive calculation
    42
}
*/


fun <T> lazyDelegate(initializer: () -> T) = lazy(initializer)::getValue

/**
val lazyProperty: String by lazyDelegate {
    println("Initializing lazy property")
    "Hello, World!"
}
*/



/**
fun <T> runInTransaction(
    dao: MyDao,
    action: () -> T): T {
    return dao.runInTransaction {
        action()
    }
}*/

/**
val dao = MyDatabase.getInstance(context).myDao()
val result = runInTransaction(dao) {
    dao.getSomeData()
}
*/

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    activity?.showToast(message, duration)
}

/**
showToast("Hello, world!")

showToast("Hello, world!", Toast.LENGTH_LONG)
*/
