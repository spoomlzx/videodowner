package org.nudt.common

import java.math.BigDecimal

infix fun Long.ratio(bottom: Long): Double {
    if (bottom <= 0) {
        return 0.0
    }
    val result = (this * 100.0).toBigDecimal()
        .divide((bottom * 1.0).toBigDecimal(), 2, BigDecimal.ROUND_FLOOR)
    return result.toDouble()
}

fun Long.formatFileSize(): String {
    if (this < 0) {
        return "0kb"
    }
    val kiloByte = this / 1024
    if (kiloByte < 1) {
        return this.toString() + "b"
    }
    val megaByte = kiloByte / 1024
    if (megaByte < 1) {
        val result1 = BigDecimal(kiloByte.toString())
        return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "kb"
    }
    val gigaByte = megaByte / 1024
    if (gigaByte < 1) {
        val result2 = BigDecimal(megaByte.toString())
        return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "mb"
    }
    val teraBytes = gigaByte / 1024
    if (teraBytes < 1) {
        val result3 = BigDecimal(gigaByte.toString())
        return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "gb"
    }
    val result4 = BigDecimal(teraBytes)
    return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "tb"
}