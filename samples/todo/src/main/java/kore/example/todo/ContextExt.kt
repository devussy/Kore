package kore.example.todo

import android.content.Context
import android.util.TypedValue

fun Context.toPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}
