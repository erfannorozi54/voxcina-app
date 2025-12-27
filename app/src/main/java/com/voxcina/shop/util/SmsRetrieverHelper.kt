package com.voxcina.shop.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for SMS Retriever API to auto-fill OTP codes.
 * Uses Google Play Services SMS Retriever API which doesn't require SMS permission.
 * 
 * Requirements: 6.1, 6.2, 6.3
 */
@Singleton
class SmsRetrieverHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var smsReceiver: BroadcastReceiver? = null
    private var onOtpReceivedCallback: ((String) -> Unit)? = null
    private var onFailureCallback: (() -> Unit)? = null

    /**
     * Starts the SMS Retriever API listener.
     * When an SMS containing the OTP is received, it will be automatically extracted
     * and passed to the onOtpReceived callback.
     * 
     * @param onOtpReceived Callback invoked with the extracted 5-digit OTP
     * @param onFailure Callback invoked if SMS retrieval fails
     */
    fun startSmsRetriever(
        onOtpReceived: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        this.onOtpReceivedCallback = onOtpReceived
        this.onFailureCallback = onFailure

        val client = SmsRetriever.getClient(context)
        val task = client.startSmsRetriever()

        task.addOnSuccessListener {
            registerSmsReceiver()
        }

        task.addOnFailureListener {
            onFailure()
        }
    }

    /**
     * Stops the SMS Retriever and unregisters the broadcast receiver.
     * Should be called when the OTP verification screen is dismissed.
     */
    fun stopSmsRetriever() {
        smsReceiver?.let { receiver ->
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                // Receiver was not registered or already unregistered
            }
        }
        smsReceiver = null
        onOtpReceivedCallback = null
        onFailureCallback = null
    }

    @Suppress("DEPRECATION")
    private fun registerSmsReceiver() {
        smsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                    val extras = intent.extras ?: return
                    
                    // Use appropriate API based on SDK version
                    val status: Status? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
                    } else {
                        extras.getParcelable(SmsRetriever.EXTRA_STATUS)
                    }

                    when (status?.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                            message?.let { sms ->
                                extractOtpFromMessage(sms)?.let { otp ->
                                    onOtpReceivedCallback?.invoke(otp)
                                }
                            }
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            onFailureCallback?.invoke()
                        }
                    }
                }
            }
        }

        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        ContextCompat.registerReceiver(
            context,
            smsReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    companion object {
        // Regex pattern to extract 5-digit OTP from SMS message
        private val OTP_PATTERN = Regex("\\b(\\d{5})\\b")

        /**
         * Extracts a 5-digit OTP code from an SMS message.
         * 
         * @param message The SMS message content
         * @return The extracted 5-digit OTP, or null if not found
         */
        fun extractOtpFromMessage(message: String): String? {
            return OTP_PATTERN.find(message)?.groupValues?.getOrNull(1)
        }
    }
}
