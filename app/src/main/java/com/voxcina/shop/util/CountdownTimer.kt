package com.voxcina.shop.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Utility object for countdown timer formatting.
 */
object CountdownFormatter {

    /**
     * Formats total seconds into HH:MM:SS format with Persian digits.
     *
     * @param totalSeconds The total number of seconds to format
     * @return Formatted time string in HH:MM:SS format with Persian digits
     */
    fun formatCountdown(totalSeconds: Long): String {
        if (totalSeconds < 0) return PersianDigitConverter.toPersianDigits("00:00:00")
        
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        
        val formatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        return PersianDigitConverter.toPersianDigits(formatted)
    }

    /**
     * Parses hours, minutes, and seconds from total seconds.
     *
     * @param totalSeconds The total number of seconds
     * @return Triple of (hours, minutes, seconds)
     */
    fun parseTime(totalSeconds: Long): Triple<Int, Int, Int> {
        if (totalSeconds < 0) return Triple(0, 0, 0)
        
        val hours = (totalSeconds / 3600).toInt()
        val minutes = ((totalSeconds % 3600) / 60).toInt()
        val seconds = (totalSeconds % 60).toInt()
        
        return Triple(hours, minutes, seconds)
    }
}

/**
 * Data class representing countdown timer state.
 */
data class CountdownState(
    val hours: Int,
    val minutes: Int,
    val seconds: Int,
    val totalSeconds: Long,
    val isFinished: Boolean
) {
    val formattedTime: String
        get() = CountdownFormatter.formatCountdown(totalSeconds)
}

/**
 * Composable that provides countdown timer functionality with automatic tick updates.
 *
 * @param endTimeMillis The end time in milliseconds (System.currentTimeMillis() based)
 * @param onTick Callback invoked every second with the current countdown state
 * @param onFinish Callback invoked when the countdown reaches zero
 */
@Composable
fun CountdownTimer(
    endTimeMillis: Long,
    onTick: (CountdownState) -> Unit,
    onFinish: () -> Unit = {}
) {
    var remainingSeconds by remember { mutableLongStateOf(0L) }
    
    LaunchedEffect(endTimeMillis) {
        while (true) {
            val currentTime = System.currentTimeMillis()
            val remaining = (endTimeMillis - currentTime) / 1000
            
            if (remaining <= 0) {
                remainingSeconds = 0
                val (hours, minutes, seconds) = CountdownFormatter.parseTime(0)
                onTick(CountdownState(hours, minutes, seconds, 0, true))
                onFinish()
                break
            }
            
            remainingSeconds = remaining
            val (hours, minutes, seconds) = CountdownFormatter.parseTime(remaining)
            onTick(CountdownState(hours, minutes, seconds, remaining, false))
            
            delay(1000L)
        }
    }
}
