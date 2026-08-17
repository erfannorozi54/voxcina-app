package com.voxcina.shop

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.svg.SvgDecoder
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Voxcina Shop.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class VoxcinaApplication : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}
