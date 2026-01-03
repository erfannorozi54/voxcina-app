package com.voxcina.shop.ui.components

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.voxcina.shop.ui.theme.Primary
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

data class LatLng(val latitude: Double, val longitude: Double)

@Composable
fun LocationPicker(
    initialLocation: LatLng?,
    onLocationSelected: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf(initialLocation) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var hasLocationPermission by remember { mutableStateOf(checkLocationPermission(context)) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions.values.any { it }
        if (hasLocationPermission) {
            getCurrentLocation(context) { location ->
                location?.let {
                    selectedLocation = it
                    onLocationSelected(it)
                    mapView?.controller?.animateTo(GeoPoint(it.latitude, it.longitude))
                }
            }
        }
    }

    // Initialize OSMDroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Column(modifier = modifier) {
        Text(
            text = "موقعیت مکانی را روی نقشه انتخاب کنید",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            // Map View
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        
                        // Default to Tehran if no initial location
                        val startPoint = initialLocation?.let { 
                            GeoPoint(it.latitude, it.longitude) 
                        } ?: GeoPoint(35.6892, 51.3890)
                        controller.setCenter(startPoint)
                        
                        // Add marker for initial location
                        initialLocation?.let { addMarker(this, it) }
                        
                        // Handle map clicks
                        setOnTouchListener { _, event ->
                            if (event.action == android.view.MotionEvent.ACTION_UP) {
                                val projection = projection
                                val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt()) as GeoPoint
                                val newLocation = LatLng(geoPoint.latitude, geoPoint.longitude)
                                selectedLocation = newLocation
                                onLocationSelected(newLocation)
                                updateMarker(this, newLocation)
                            }
                            false
                        }
                        
                        mapView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            
            // Center marker indicator
            Icon(
                Icons.Default.Place,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.Center)
            )
            
            // My Location FAB
            FloatingActionButton(
                onClick = {
                    if (hasLocationPermission) {
                        getCurrentLocation(context) { location ->
                            location?.let {
                                selectedLocation = it
                                onLocationSelected(it)
                                mapView?.controller?.animateTo(GeoPoint(it.latitude, it.longitude))
                                mapView?.let { mv -> updateMarker(mv, it) }
                            }
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(40.dp),
                containerColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.MyLocation,
                    contentDescription = "موقعیت من",
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        // Show selected coordinates
        selectedLocation?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "مختصات: ${String.format("%.6f", it.latitude)}, ${String.format("%.6f", it.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
    
    DisposableEffect(Unit) {
        onDispose {
            mapView?.onDetach()
        }
    }
}

private fun checkLocationPermission(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PermissionChecker.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PermissionChecker.PERMISSION_GRANTED
}

private fun getCurrentLocation(context: Context, onResult: (LatLng?) -> Unit) {
    if (!checkLocationPermission(context)) {
        onResult(null)
        return
    }
    
    try {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location?.let {
                onResult(LatLng(it.latitude, it.longitude))
            } ?: onResult(null)
        }.addOnFailureListener {
            onResult(null)
        }
    } catch (e: SecurityException) {
        onResult(null)
    }
}

private fun addMarker(mapView: MapView, location: LatLng) {
    val marker = Marker(mapView).apply {
        position = GeoPoint(location.latitude, location.longitude)
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
    }
    mapView.overlays.add(marker)
}

private fun updateMarker(mapView: MapView, location: LatLng) {
    mapView.overlays.clear()
    addMarker(mapView, location)
    mapView.invalidate()
}
