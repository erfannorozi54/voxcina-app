package com.voxcina.shop.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for SMS User Consent API to auto-fill OTP codes.
 * Shows a one-tap prompt to user - no hash required in SMS.
 */
@Singleton
class SmsRetrieverHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var smsReceiver: BroadcastReceiver? = null

    companion object {
        private val OTP_PATTERN = Regex("\\b(\\d{5})\\b")
        
        // Static callback for activity result - survives across instances
        var consentLauncher: ((Intent) -> Unit)? = null
        
        // Static OTP callback - survives across recompositions
        var otpCallback: ((String) -> Unit)? = null

        fun extractOtpFromMessage(message: String): String? {
            return OTP_PATTERN.find(message)?.groupValues?.getOrNull(1)
        }
    }

    /**
     * Starts SMS User Consent API - will show a prompt when SMS arrives.
     */
    fun startSmsRetriever(
        onOtpReceived: (String) -> Unit,
        onFailure: () -> Unit
    ) {
        otpCallback = onOtpReceived
        Log.d("SmsRetrieverHelper", "Starting SMS User Consent...")

        val client = SmsRetriever.getClient(context)
        client.startSmsUserConsent(null)
            .addOnSuccessListener {
                Log.d("SmsRetrieverHelper", "SMS User Consent started successfully")
                registerConsentReceiver()
            }
            .addOnFailureListener {
                Log.e("SmsRetrieverHelper", "Failed to start SMS consent", it)
                onFailure()
            }
    }

    /**
     * Call this from activity result when user grants consent.
     */
    fun handleConsentResult(data: Intent?) {
        val message = data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
        Log.d("SmsRetrieverHelper", "Consent result message: $message")
        message?.let { sms ->
            extractOtpFromMessage(sms)?.let { otp ->
                Log.d("SmsRetrieverHelper", "Extracted OTP: $otp, callback: ${otpCallback != null}")
                otpCallback?.invoke(otp)
            }
        }
    }

    fun stopSmsRetriever() {
        smsReceiver?.let { receiver ->
            try {
                context.unregisterReceiver(receiver)
            } catch (e: IllegalArgumentException) {
                // Already unregistered
            }
        }
        smsReceiver = null
        // Don't clear otpCallback - it's needed for consent result
    }

    @Suppress("DEPRECATION")
    private fun registerConsentReceiver() {
        Log.d("SmsRetrieverHelper", "Registering consent receiver...")
        smsReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                Log.d("SmsRetrieverHelper", "Broadcast received: ${intent?.action}")
                if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                    val extras = intent.extras ?: return

                    val status: Status? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        extras.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
                    } else {
                        extras.getParcelable(SmsRetriever.EXTRA_STATUS)
                    }

                    Log.d("SmsRetrieverHelper", "Status code: ${status?.statusCode}")
                    when (status?.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val consentIntent: Intent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                            } else {
                                extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                            }
                            Log.d("SmsRetrieverHelper", "Consent intent received, launcher: ${consentLauncher != null}")
                            consentIntent?.let { consentLauncher?.invoke(it) }
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            Log.d("SmsRetrieverHelper", "SMS consent timeout")
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
        Log.d("SmsRetrieverHelper", "Consent receiver registered")
    }
}
