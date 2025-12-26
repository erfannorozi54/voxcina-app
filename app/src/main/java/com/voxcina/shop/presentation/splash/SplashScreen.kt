package com.voxcina.shop.presentation.splash

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.voxcina.shop.R
import com.voxcina.shop.ui.theme.Primary
import com.voxcina.shop.util.ImmersiveModeEffect
import kotlinx.coroutines.delay

private const val LOADING_DELAY_MS = 1500L

/**
 * Splash screen composable that displays a branded video animation
 * followed by a loading state before navigating to the main screen.
 *
 * @param onSplashComplete Callback invoked when splash sequence is complete
 */
@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    val context = LocalContext.current
    var splashState by remember { mutableStateOf<SplashState>(SplashState.Playing) }

    // Enable immersive mode during splash - hides system bars
    // System bars are automatically restored when leaving this composable
    ImmersiveModeEffect(enabled = true)

    // Build video URI from raw resource
    val videoUri = remember {
        Uri.parse("android.resource://${context.packageName}/${R.raw.splash}")
    }

    // Initialize ExoPlayer
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            repeatMode = Player.REPEAT_MODE_OFF
            prepare()
            playWhenReady = true
        }
    }

    // Listen for playback completion
    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_ENDED -> {
                        splashState = SplashState.Loading
                    }
                    Player.STATE_IDLE -> {
                        // Handle error case - skip to loading state
                        if (exoPlayer.playerError != null) {
                            splashState = SplashState.Loading
                        }
                    }
                }
            }
        }
        exoPlayer.addListener(listener)

        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }


    // Handle loading state delay and transition to complete
    LaunchedEffect(splashState) {
        if (splashState is SplashState.Loading) {
            delay(LOADING_DELAY_MS)
            splashState = SplashState.Complete
            onSplashComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Video player view
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Loading indicator overlay - white color, positioned below center
        if (splashState is SplashState.Loading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .offset(y = 100.dp),
                color = Color.White,
                strokeWidth = 4.dp
            )
        }
    }
}
