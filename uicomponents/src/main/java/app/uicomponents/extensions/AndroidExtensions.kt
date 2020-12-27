package app.uicomponents.extensions

import android.os.Build

fun isNougat(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
}
