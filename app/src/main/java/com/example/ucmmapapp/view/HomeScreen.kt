package com.example.ucmmapapp.view

// osmdroid imports
import org.osmdroid.config.Configuration
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// Android & System imports
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast

// ActivityResult & Permissions
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

// Jetpack Compose imports
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties

// Lifecycle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

// Coroutines
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Location Services
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// Project-specific imports
import com.example.ucmmapapp.GeoJsonRenderer
import com.example.ucmmapapp.routing.OfflineRouter

@Composable
fun HomeScreen() {
    // --- Contexto y Ciclo de Vida ---
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = rememberMapViewWithLifecycle(context, lifecycleOwner)
    val scope = rememberCoroutineScope()

    // --- Estados de la Interfaz y del Mapa ---
    var routeOverlay by remember { mutableStateOf<Polyline?>(null) }
    var userLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var recenterRequest by remember { mutableStateOf(false) }
    var offlineRouter by remember { mutableStateOf<OfflineRouter?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // --- Estados para la Lógica de Selección de Rutas ---
    var selectedDestination by remember { mutableStateOf<Pair<GeoPoint, String>?>(null) }
    var showRouteOptionsDialog by remember { mutableStateOf(false) }
    var isSelectingStartBuilding by remember { mutableStateOf(false) }

    // --- Gestión de Permisos de Ubicación ---
    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasLocationPermission = permissions.values.all { it }
        }
    )

    fun calculateAndDrawRoute(start: GeoPoint, end: GeoPoint) {
        val currentOfflineRouter = offlineRouter
        if (currentOfflineRouter == null) {
            Toast.makeText(context, "El motor de rutas no está listo", Toast.LENGTH_SHORT).show()
            return
        }

        scope.launch(Dispatchers.IO) {
            val path = currentOfflineRouter.findShortestPath(start, end)
            withContext(Dispatchers.Main) {
                routeOverlay?.let { mapView.overlays.remove(it) }

                if (!path.isNullOrEmpty()) {
                    val newRouteOverlay = Polyline().apply {
                        setPoints(path)
                        color = android.graphics.Color.parseColor("#FF007AFF")
                        width = 20f
                    }
                    mapView.overlays.add(newRouteOverlay)
                    routeOverlay = newRouteOverlay
                    mapView.invalidate()
                } else {
                    Toast.makeText(context, "No se pudo encontrar una ruta", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    DisposableEffect(mapView, isSelectingStartBuilding) {
        val eventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (isSelectingStartBuilding) {
                    Toast.makeText(context, "Selecciona un edificio de origen y pulsa 'Ruta aquí'", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }
        }
        val eventOverlay = MapEventsOverlay(eventsReceiver)
        mapView.overlays.add(0, eventOverlay)

        onDispose {
            mapView.overlays.remove(eventOverlay)
        }
    }

    val onRouteRequested: (GeoPoint, String) -> Unit = { geoPoint, name ->
        routeOverlay?.let {
            mapView.overlays.remove(it)
            mapView.invalidate()
        }
        routeOverlay = null

        if (isSelectingStartBuilding) {
            val startPoint = geoPoint
            val endPoint = selectedDestination!!.first
            calculateAndDrawRoute(startPoint, endPoint)
            isSelectingStartBuilding = false
            selectedDestination = null
        } else {
            selectedDestination = geoPoint to name
            showRouteOptionsDialog = true
        }
    }

    if (showRouteOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showRouteOptionsDialog = false },
            title = { Text("Calcular ruta a ${selectedDestination?.second ?: ""}") },
            text = { Text("¿Desde dónde quieres iniciar la ruta?") },
            confirmButton = {
                Button(
                    onClick = {
                        if (userLocation != null) {
                            calculateAndDrawRoute(userLocation!!, selectedDestination!!.first)
                        } else {
                            Toast.makeText(context, "Ubicación actual no disponible", Toast.LENGTH_SHORT).show()
                        }
                        showRouteOptionsDialog = false
                    }
                ) { Text("Mi ubicación") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRouteOptionsDialog = false
                        isSelectingStartBuilding = true
                        Toast.makeText(context, "Toca el edificio de origen", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("Otro edificio") }
            },
            properties = DialogProperties(dismissOnClickOutside = true)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                mapView.apply {
                    val edificiosGeoJson = GeoJsonRenderer.loadGeoJsonObjectFromAsset(context, "campus_zonas.geojson")
                    if (edificiosGeoJson != null) {
                        GeoJsonRenderer.addPolygonsToMap(
                            map = this,
                            geoJson = edificiosGeoJson,
                            fillColor = android.graphics.Color.argb(100, 0, 255, 0),
                            strokeColor = android.graphics.Color.GREEN,
                            strokeWidth = 4.0f,
                            onRouteRequested = onRouteRequested
                        )
                    }

                    val caminosGeoJson = GeoJsonRenderer.loadGeoJsonObjectFromAsset(context, "campus_caminos.geojson")
                    if (caminosGeoJson != null) {
                        GeoJsonRenderer.addPolylinesToMap(
                            map = this,
                            geoJson = caminosGeoJson,
                            color = android.graphics.Color.DKGRAY,
                            width = 5.0f
                        )
                        offlineRouter = OfflineRouter(caminosGeoJson)
                    }
                }
            },
            update = { /* No update needed */ }
        )

        FloatingActionButton(
            onClick = { recenterRequest = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Centrar en mi ubicación")
        }
    }

    LaunchedEffect(Unit) {
        if (locationPermissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
            hasLocationPermission = true
        } else {
            requestPermissionLauncher.launch(locationPermissions)
        }
    }

    DisposableEffect(hasLocationPermission, mapView) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        var locationCallback: LocationCallback? = null
        val userMarker = Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = ContextCompat.getDrawable(context, org.osmdroid.library.R.drawable.person)
        }

        if (hasLocationPermission) {
            mapView.overlays.add(userMarker)
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(10000)
                .build()
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    val location = result.lastLocation ?: return
                    val userPoint = GeoPoint(location.latitude, location.longitude)
                    userMarker.position = userPoint
                    userLocation = userPoint
                    mapView.invalidate()
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            mapView.overlays.remove(userMarker)
            userLocation = null
        }

        onDispose {
            locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
            mapView.overlays.remove(userMarker)
        }
    }

    LaunchedEffect(recenterRequest) {
        if (recenterRequest) {
            userLocation?.let { mapView.controller.animateTo(it) }
            recenterRequest = false
        }
    }
}

@Composable
fun rememberMapViewWithLifecycle(context: Context, lifecycleOwner: LifecycleOwner): MapView {
    val mapView = remember {
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_pref", Context.MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            controller.setZoom(18.0)
            controller.setCenter(GeoPoint(-35.43567763777879, -71.62208124406006))
            setMultiTouchControls(true)
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) = mapView.onResume()
            override fun onPause(owner: LifecycleOwner) = mapView.onPause()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    return mapView
}
