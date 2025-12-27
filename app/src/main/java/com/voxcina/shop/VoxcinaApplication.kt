package com.voxcina.shop

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Voxcina Shop.
 * Annotated with @HiltAndroidApp to enable Hilt dependency injection.
 */
@HiltAndroidApp
class VoxcinaApplication : Application()
