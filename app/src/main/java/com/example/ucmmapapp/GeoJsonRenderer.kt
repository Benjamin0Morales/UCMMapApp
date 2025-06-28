package com.example.ucmmapapp

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline

/**
 * Un objeto de utilidad para analizar datos GeoJSON y renderizarlos como superposiciones en un MapView de osmdroid.
 * Proporciona métodos para dibujar polígonos (edificios) y polilíneas (caminos).
 */
object GeoJsonRenderer {

    /**
     * Carga un archivo desde la carpeta 'assets' y lo convierte en un JSONObject.
     *
     * @param context El contexto de la aplicación para acceder a los assets.
     * @param filename El nombre del archivo GeoJSON en la carpeta 'assets'.
     * @return Un JSONObject que representa el contenido del archivo, o null si ocurre un error.
     */
    fun loadGeoJsonObjectFromAsset(context: Context, filename: String): JSONObject? {
        return try {
            val inputStream = context.assets.open(filename)
            val json = inputStream.bufferedReader().use { it.readText() }
            JSONObject(json)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Comprueba si un punto geográfico se encuentra dentro de una lista de vértices de polígono.
     * Utiliza el algoritmo de trazado de rayos (ray-casting) para la detección.
     *
     * @param point El GeoPoint a comprobar.
     * @param polygon La lista de GeoPoints que definen los vértices del polígono.
     * @return `true` si el punto está dentro del polígono, `false` en caso contrario.
     */
    fun isPointInsidePolygon(point: org.osmdroid.util.GeoPoint, polygon: List<org.osmdroid.util.GeoPoint>): Boolean {
        var intersectCount = 0
        for (j in polygon.indices) {
            val k = (j + 1) % polygon.size // Vértice siguiente, con ajuste para el último segmento
            val a = polygon[j]
            val b = polygon[k]
            // Cuenta cuántas veces un rayo horizontal desde el punto cruza los segmentos del polígono
            if (rayCrossesSegment(point, a, b)) {
                intersectCount++
            }
        }
        // Si el número de cruces es impar, el punto está dentro.
        return intersectCount % 2 == 1
    }

    /**
     * Función auxiliar para `isPointInsidePolygon`. Determina si un rayo horizontal que se extiende
     * desde el punto `p` hacia la derecha cruza el segmento de línea definido por los puntos `a` y `b`.
     *
     * @param p El punto de origen del rayo.
     * @param a El primer vértice del segmento.
     * @param b El segundo vértice del segmento.
     * @return `true` si el rayo cruza el segmento.
     */
    fun rayCrossesSegment(p: org.osmdroid.util.GeoPoint, a: org.osmdroid.util.GeoPoint, b: org.osmdroid.util.GeoPoint): Boolean {
        val px = p.longitude
        var py = p.latitude
        val ax = a.longitude
        val ay = a.latitude
        val bx = b.longitude
        val by = b.latitude

        // Asegura que 'a' sea el punto con la latitud más baja para simplificar los cálculos
        if (ay > by) return rayCrossesSegment(p, b, a)
        // Evita casos de colinealidad horizontal ajustando ligeramente la latitud del punto
        if (py == ay || py == by) py += 0.00000001
        // El punto no puede cruzar si está por encima, por debajo o a la derecha del cuadro delimitador del segmento
        if (py > by || py < ay || px >= maxOf(ax, bx)) return false
        // Si el punto está a la izquierda del cuadro delimitador, definitivamente cruza
        if (px < minOf(ax, bx)) return true

        // Compara la pendiente del segmento con la pendiente desde 'a' hasta 'p'
        val red = if (ax != bx) (by - ay) / (bx - ax) else Double.MAX_VALUE
        val blue = if (ax != px) (py - ay) / (px - ax) else Double.MAX_VALUE
        return blue >= red
    }

    /**
     * Renderiza todas las geometrías de tipo Polígono y MultiPolígono de un FeatureCollection de GeoJSON.
     * Asigna InfoWindows clicables a los polígonos que son edificios.
     *
     * @param map El MapView donde se dibujarán los polígonos.
     * @param geoJson El JSONObject que contiene los datos GeoJSON.
     * @param fillColor El color de relleno para los polígonos.
     * @param strokeColor El color del borde para los polígonos.
     * @param strokeWidth El ancho del borde de los polígonos.
     * @param onRouteRequested Un callback que se invoca cuando el usuario solicita una ruta desde la InfoWindow de un edificio.
     */
    fun addPolygonsToMap(
        map: MapView,
        geoJson: JSONObject?,
        fillColor: Int,
        strokeColor: Int,
        strokeWidth: Float,
        onRouteRequested: ((destination: GeoPoint, name: String) -> Unit)? = null
    ) {
        if (geoJson == null) return
        val features = geoJson.optJSONArray("features") ?: return
        var unnamedBuildingCounter = 1 // Contador para asignar nombres a edificios sin nombre

        for (i in 0 until features.length()) {
            val feature = features.optJSONObject(i) ?: continue
            val geometry = feature.optJSONObject("geometry") ?: continue
            val type = geometry.optString("type")
            if (type != "Polygon" && type != "MultiPolygon") continue

            val properties = feature.optJSONObject("properties")

            // Asigna un nombre genérico si es un edificio y no tiene uno definido en las propiedades.
            var displayName = properties?.optString("name")
            if (properties?.optString("building") == "yes" && displayName.isNullOrBlank()) {
                displayName = "Edificio ${unnamedBuildingCounter++}"
            }

            // Función lambda para crear un polígono y configurar su InfoWindow.
            // Se reutiliza para geometrías 'Polygon' y 'MultiPolygon'.
            val polygonCreator = { points: List<GeoPoint> ->
                object : Polygon(map) {
                    init {
                        this.points = points
                        // Aplica los estilos de color y borde
                        fillPaint.color = fillColor
                        outlinePaint.color = strokeColor
                        outlinePaint.strokeWidth = strokeWidth

                        // Configura la ventana de información (InfoWindow) solo para polígonos marcados como edificios.
                        if (properties?.optString("building") == "yes") {
                            this.infoWindow = object : org.osmdroid.views.overlay.infowindow.InfoWindow(
                                com.example.ucmmapapp.R.layout.info_window_building, map
                            ) {
                                override fun onOpen(item: Any?) {
                                    val buildingName = displayName ?: "Lugar sin nombre"
                                    mView.findViewById<android.widget.TextView>(com.example.ucmmapapp.R.id.bubble_title).text = buildingName
                                    val routeButton = mView.findViewById<android.widget.Button>(com.example.ucmmapapp.R.id.bubble_btn_route)

                                    // Muestra el botón de ruta y le asigna la acción del callback.
                                    if (onRouteRequested != null) {
                                        routeButton.visibility = android.view.View.VISIBLE
                                        routeButton.setOnClickListener {
                                            // Calcula el centroide del polígono para usarlo como destino.
                                            val centroid = GeoPoint(points.map { it.latitude }.average(), points.map { it.longitude }.average())
                                            onRouteRequested.invoke(centroid, buildingName)
                                            close() // Cierra la InfoWindow después de solicitar la ruta.
                                        }
                                    } else {
                                        routeButton.visibility = android.view.View.GONE
                                    }
                                }
                                override fun onClose() { /* No se necesita acción al cerrar. */ }
                            }
                        }
                    }

                    // Sobrescribe el evento de toque para manejar la apertura de la InfoWindow.
                    override fun onSingleTapConfirmed(event: android.view.MotionEvent, mapView: MapView): Boolean {
                        if (properties?.optString("building") == "yes") {
                            val iGeoPoint = mapView.projection.fromPixels(event.x.toInt(), event.y.toInt())
                            // Comprueba si el toque fue dentro de este polígono.
                            if (isPointInsidePolygon(org.osmdroid.util.GeoPoint(iGeoPoint.latitude, iGeoPoint.longitude), this.points)) {
                                // Cierra todas las demás InfoWindows y abre la de este polígono.
                                org.osmdroid.views.overlay.infowindow.InfoWindow.closeAllInfoWindowsOn(mapView)
                                this.infoWindow?.open(this, this.bounds.center, 0, 0)
                                return true // Evento consumido.
                            }
                        }
                        return false // Evento no consumido.
                    }
                }
            }

            // Procesa la geometría según su tipo.
            when (type) {
                "Polygon" -> {
                    val coords = geometry.optJSONArray("coordinates")?.optJSONArray(0) ?: continue
                    val polygon = polygonCreator(parseLinearRing(coords))
                    map.overlays.add(polygon)
                }
                "MultiPolygon" -> {
                    val multiCoords = geometry.optJSONArray("coordinates") ?: continue
                    for (j in 0 until multiCoords.length()) {
                        val coords = multiCoords.optJSONArray(j)?.optJSONArray(0) ?: continue
                        val polygon = polygonCreator(parseLinearRing(coords))
                        map.overlays.add(polygon)
                    }
                }
            }
        }
        map.invalidate() // Redibuja el mapa para mostrar los nuevos polígonos.
    }

    /**
     * Renderiza todas las geometrías de tipo LineString de un FeatureCollection de GeoJSON.
     * Usado principalmente para dibujar los caminos peatonales.
     *
     * @param map El MapView donde se dibujarán las líneas.
     * @param geoJson El JSONObject que contiene los datos GeoJSON.
     * @param color El color para las líneas.
     * @param width El ancho de las líneas.
     */
    fun addPolylinesToMap(
        map: MapView,
        geoJson: JSONObject?,
        color: Int,
        width: Float
    ) {
        if (geoJson == null) return
        val features = geoJson.optJSONArray("features") ?: return
        for (i in 0 until features.length()) {
            val feature = features.optJSONObject(i) ?: continue
            val geometry = feature.optJSONObject("geometry") ?: continue
            if (geometry.optString("type") == "LineString") {
                val coords = geometry.optJSONArray("coordinates") ?: continue
                val polyline = Polyline(map).apply {
                    setPoints(parseLineString(coords))
                    paint.color = color
                    paint.strokeWidth = width
                    infoWindow = null // Las polilíneas de caminos no tienen InfoWindow.
                }
                map.overlays.add(polyline)
            }
        }
    }

    /**
     * Función auxiliar para convertir un array de coordenadas JSON en una lista de GeoPoints para un polígono.
     * @param coords El JSONArray de coordenadas.
     * @return Una lista de GeoPoints.
     */
    private fun parseLinearRing(coords: JSONArray?): List<GeoPoint> {
        val points = mutableListOf<GeoPoint>()
        if (coords == null) return points
        for (i in 0 until coords.length()) {
            val coord = coords.optJSONArray(i) ?: continue
            val lon = coord.optDouble(0)
            val lat = coord.optDouble(1)
            points.add(GeoPoint(lat, lon))
        }
        return points
    }

    /**
     * Función auxiliar para convertir un array de coordenadas JSON en una lista de GeoPoints para una polilínea.
     * @param coords El JSONArray de coordenadas.
     * @return Una lista de GeoPoints.
     */
    private fun parseLineString(coords: JSONArray?): List<GeoPoint> {
        val points = mutableListOf<GeoPoint>()
        if (coords == null) return points
        for (i in 0 until coords.length()) {
            val coord = coords.optJSONArray(i) ?: continue
            val lon = coord.optDouble(0)
            val lat = coord.optDouble(1)
            points.add(GeoPoint(lat, lon))
        }
        return points
    }
}
