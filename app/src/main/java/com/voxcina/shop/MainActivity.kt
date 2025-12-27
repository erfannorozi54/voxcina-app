package com.voxcina.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.voxcina.shop.data.local.TokenManager
import com.voxcina.shop.presentation.navigation.NavGraph
import com.voxcina.shop.ui.theme.VoxcinaTheme
import com.voxcina.shop.util.OnboardingManagerImpl
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenManager: TokenManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VoxcinaTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val onboardingManager = remember { OnboardingManagerImpl(context) }
                NavGraph(
                    navController = navController,
                    onboardingManager = onboardingManager,
                    tokenManager = tokenManager
                )
            }
        }
    }
}