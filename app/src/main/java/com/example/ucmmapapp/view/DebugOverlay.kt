package com.example.ucmmapapp.view

import android.graphics.Canvas
import android.util.Log
import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

/**
 * `DebugOverlay` es una superposición (Overlay) de osmdroid diseñada exclusivamente para depuración.
 * No tiene ninguna representación visual en el mapa, pero intercepta y registra los eventos de toque,
 * como toques simples y pulsaciones largas, en la consola de Logcat y en la salida estándar.
 *
 * Es útil para verificar que los gestos del usuario se están detectando correctamente en el `MapView`
 * sin interferir con otras superposiciones funcionales (como las de los edificios o rutas).
 */
class DebugOverlay : Overlay() {

    /**
     * Se invoca cuando se detecta un toque simple confirmado en el mapa.
     * Registra el evento en la consola para fines de depuración.
     *
     * @return `false` siempre, para no consumir el evento. Esto permite que otras superposiciones
     *         en la pila también puedan procesar este toque.
     */
    override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
        Log.d("DebugOverlay", "onSingleTapConfirmed recibido: ${e?.action}")
        println("[DEBUG] DebugOverlay onSingleTapConfirmed: ${e?.action}")
        return false
    }

    /**
     * Se invoca cuando se detecta una pulsación larga en el mapa.
     * Registra el evento en la consola para fines de depuración.
     *
     * @return `false` siempre, para no consumir el evento y permitir que otras superposiciones
     *         puedan reaccionar a la pulsación larga.
     */
    override fun onLongPress(e: MotionEvent?, mapView: MapView?): Boolean {
        Log.d("DebugOverlay", "onLongPress recibido: ${e?.action}")
        println("[DEBUG] DebugOverlay onLongPress: ${e?.action}")
        return false
    }

    /**
     * Este método se encarga de dibujar la superposición en el `Canvas` del mapa.
     * Se deja intencionadamente vacío porque esta superposición es invisible y solo se usa
     * para registrar eventos.
     */
    override fun draw(c: Canvas?, osmv: MapView?, shadow: Boolean) {
        // No se dibuja nada, ya que es una superposición de depuración invisible.
    }
}
