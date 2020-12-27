package app.uicomponents

import android.os.SystemClock
import android.view.View
import java.util.*

private const val minimumInterval = 1000L

class DebouncedOnClickListener(private val onClicks: (view: View) -> Unit) : View.OnClickListener {
    private val lastClickMap: MutableMap<View, Long> = WeakHashMap()

    override fun onClick(clickedView: View) {
        val previousClickTimestamp = lastClickMap[clickedView]
        val currentTimestamp = SystemClock.uptimeMillis()

        if (previousClickTimestamp == null || (currentTimestamp - previousClickTimestamp) > minimumInterval) {
            lastClickMap[clickedView] = currentTimestamp
            onClicks(clickedView)
        }
    }
}