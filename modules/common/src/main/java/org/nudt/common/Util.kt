package org.nudt.common

import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.math.BigDecimal


fun AppCompatActivity.shortToast(msg: String) {
    Toast.makeText(this, msg, LENGTH_SHORT).show()
}

fun AppCompatActivity.shortToast(id: Int) {
    Toast.makeText(this, getString(id), LENGTH_SHORT).show()
}

fun Fragment.shortToast(msg: String) {
    Toast.makeText(this.context, msg, LENGTH_SHORT).show()
}

fun Fragment.shortToast(id: Int) {
    Toast.makeText(this.context, getString(id), LENGTH_SHORT).show()
}

infix fun Long.ratio(bottom: Long): Double {
    if (bottom <= 0) {
        return 0.0
    }
    val result = (this * 100.0).toBigDecimal()
        .divide((bottom * 1.0).toBigDecimal(), 2, BigDecimal.ROUND_FLOOR)
    return result.toDouble()
}