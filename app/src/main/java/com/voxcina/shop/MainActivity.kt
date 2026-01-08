package com.voxcina.shop

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.voxcina.shop.data.local.AppActivityTracker
import com.voxcina.shop.data.local.SessionManager
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.presentation.navigation.NavGraph
import com.voxcina.shop.presentation.navigation.Screen
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.OnboardingManagerImpl
import com.voxcina.shop.util.SmsRetrieverHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenManager: TokenManager
    
    @Inject
    lateinit var appActivityTracker: AppActivityTracker
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    @Inject
    lateinit var smsRetrieverHelper: SmsRetrieverHelper
    
    private val smsConsentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            smsRetrieverHelper.handleConsentResult(result.data)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Track app open on cold start (with debounce)
        appActivityTracker.trackAppOpen()
        
        // Set up SMS consent launcher using static callback
        SmsRetrieverHelper.consentLauncher = { intent ->
            smsConsentLauncher.launch(intent)
        }
        
        setContent {
            VoxcinaTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val onboardingManager = remember { OnboardingManagerImpl(context) }
                
                // Handle session expiry - navigate to auth screen
                LaunchedEffect(Unit) {
                    sessionManager.logoutEvent.collect {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
                
                NavGraph(
                    navController = navController,
                    onboardingManager = onboardingManager,
                    tokenManager = tokenManager,
                    smsRetrieverHelper = smsRetrieverHelper
                )
            }
        }
    }
}