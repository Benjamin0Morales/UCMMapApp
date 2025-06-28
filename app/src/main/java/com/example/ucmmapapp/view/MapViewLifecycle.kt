package com.example.ucmmapapp.view

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.osmdroid.views.MapView

/**
 * Un `Composable` que crea y recuerda una instancia de `MapView` y la vincula al ciclo de vida
 * del componente de Compose que lo contiene. Esto es fundamental para que el `MapView` de osmdroid,
 * que es una Vista tradicional de Android, funcione correctamente dentro del paradigma declarativo
 * de Jetpack Compose.
 *
 * Utiliza un `DisposableEffect` para añadir un observador al ciclo de vida. Este observador
 * llama a `mapView.onResume()` y `mapView.onPause()` en los momentos adecuados. Cuando el
 * composable se elimina de la composición, el efecto se desecha (`onDispose`) y el observador
 * se elimina para prevenir fugas de memoria.
 *
 * @return Una instancia de `MapView` que se puede usar en un `AndroidView` y cuyo ciclo de vida
 *         está gestionado automáticamente.
 */
@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = android.R.id.content
        }
    }

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    return mapView
}
