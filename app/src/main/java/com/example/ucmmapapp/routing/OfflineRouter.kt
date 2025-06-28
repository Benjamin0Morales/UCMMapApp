package com.example.ucmmapapp.routing

import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import java.util.*

// --- Estructuras de Datos para el Grafo de Rutas ---

/**
 * Representa un nodo (o vértice) en el grafo de caminos. Cada nodo tiene una ubicación geográfica.
 * Se sobreescriben `equals` y `hashCode` para basar la identidad del nodo únicamente en su `id`.
 * Esto es crucial para evitar errores de `StackOverflowError` en colecciones cuando el grafo tiene ciclos,
 * ya que la implementación por defecto de `data class` compararía recursivamente las listas de aristas.
 *
 * @property id Un identificador único para el nodo (usamos sus coordenadas).
 * @property point La ubicación geográfica (latitud, longitud) del nodo.
 * @property edges Una lista de aristas que conectan este nodo con sus vecinos.
 */
data class Node(val id: String, val point: GeoPoint, val edges: MutableList<Edge> = mutableListOf()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return id == (other as Node).id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Representa una arista (o borde) en el grafo, conectando dos nodos.
 * La identidad de una arista se define por sus nodos de origen y destino.
 *
 * @property from El nodo de origen de la arista.
 * @property to El nodo de destino de la arista.
 * @property weight El "costo" de atravesar esta arista, que es la distancia geográfica entre los nodos.
 */
data class Edge(val from: Node, val to: Node, val weight: Double) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        val edge = other as Edge
        // Comparamos los IDs de los nodos para evitar la recursión infinita.
        return from.id == edge.from.id && to.id == edge.to.id
    }

    override fun hashCode(): Int {
        var result = from.id.hashCode()
        result = 31 * result + to.id.hashCode()
        return result
    }
}

/**
 * Representa el grafo completo de caminos. Contiene todos los nodos y las conexiones entre ellos.
 */
class Graph {
    // Los nodos se almacenan en un mapa usando su ID como clave para un acceso rápido y eficiente.
    val nodes = mutableMapOf<String, Node>()

    /**
     * Añade un nuevo nodo al grafo si no existe uno con el mismo ID.
     * @param id El ID único del nodo.
     * @param geoPoint La ubicación geográfica del nodo.
     * @return El nodo nuevo o el existente si ya estaba en el grafo.
     */
    fun addNode(id: String, geoPoint: GeoPoint): Node {
        // `getOrPut` es una forma idiomática y eficiente de añadir un nodo solo si no existe.
        return nodes.getOrPut(id) { Node(id, geoPoint) }
    }

    /**
     * Añade una arista bidireccional entre dos nodos.
     * @param fromNode El nodo de inicio.
     * @param toNode El nodo de fin.
     */
    fun addEdge(fromNode: Node, toNode: Node) {
        val distance = fromNode.point.distanceToAsDouble(toNode.point)
        // Añadimos aristas en ambas direcciones para que el grafo sea no dirigido (se puede ir y volver).
        fromNode.edges.add(Edge(fromNode, toNode, distance))
        toNode.edges.add(Edge(toNode, fromNode, distance))
    }
}

/**
 * El motor principal para el cálculo de rutas offline.
 * Construye un grafo a partir de datos GeoJSON y utiliza el algoritmo de Dijkstra para encontrar el camino más corto.
 *
 * @param geoJson El objeto JSONObject que contiene la definición de los caminos (LineStrings).
 */
class OfflineRouter(geoJson: JSONObject) {

    private val graph = Graph()

    // El bloque de inicialización se encarga de construir el grafo tan pronto como se crea una instancia del router.
    init {
        buildGraph(geoJson)
    }

    /**
     * Construye el grafo de nodos y aristas a partir de un FeatureCollection de GeoJSON.
     * Itera sobre cada 'LineString' (camino), convirtiendo cada par de coordenadas en un nodo
     * y cada segmento entre coordenadas en una arista.
     */
    private fun buildGraph(geoJson: JSONObject) {
        val features = geoJson.optJSONArray("features") ?: return
        for (i in 0 until features.length()) {
            val feature = features.optJSONObject(i) ?: continue
            val geometry = feature.optJSONObject("geometry") ?: continue
            if (geometry.optString("type") == "LineString") {
                val coords = geometry.optJSONArray("coordinates") ?: continue
                var previousNode: Node? = null
                for (j in 0 until coords.length()) {
                    val coord = coords.optJSONArray(j) ?: continue
                    val lon = coord.optDouble(0)
                    val lat = coord.optDouble(1)
                    val currentPoint = GeoPoint(lat, lon)
                    // Usamos las coordenadas como ID único para cada nodo.
                    val nodeId = "${lat},${lon}"
                    val currentNode = graph.addNode(nodeId, currentPoint)

                    // Si hay un nodo anterior, creamos una arista entre ellos.
                    if (previousNode != null) {
                        graph.addEdge(previousNode, currentNode)
                    }
                    previousNode = currentNode
                }
            }
        }
        android.util.Log.d("OfflineRouter", "Grafo construido con ${graph.nodes.size} nodos.")
    }

    /**
     * Encuentra el nodo en el grafo que está geográficamente más cercano a un punto arbitrario.
     * Esto permite "anclar" los puntos de inicio y fin de la ruta a la red de caminos existente.
     * @param point El punto geográfico para el cual se busca el nodo más cercano.
     * @return El nodo más cercano, o null si el grafo está vacío.
     */
    private fun findNearestNode(point: GeoPoint): Node? {
        if (graph.nodes.isEmpty()) return null
        return graph.nodes.values.minByOrNull { it.point.distanceToAsDouble(point) }
    }

    /**
     * Calcula la ruta más corta entre dos puntos geográficos usando el algoritmo de Dijkstra.
     * @param startPoint El punto de inicio de la ruta.
     * @param endPoint El punto de destino de la ruta.
     * @return Una lista de GeoPoints que representa el camino, o null si no se encuentra una ruta.
     */
    fun findShortestPath(startPoint: GeoPoint, endPoint: GeoPoint): List<GeoPoint>? {
        // 1. Anclar los puntos de inicio y fin a los nodos más cercanos del grafo.
        val startNode = findNearestNode(startPoint) ?: return null
        val endNode = findNearestNode(endPoint) ?: return null

        // 2. Inicialización de las estructuras de datos de Dijkstra.
        val distances = mutableMapOf<Node, Double>().withDefault { Double.MAX_VALUE } // Distancias desde el inicio
        val previousNodes = mutableMapOf<Node, Node?>() // Para reconstruir el camino
        val priorityQueue = PriorityQueue<Pair<Node, Double>>(compareBy { it.second }) // Nodos a visitar, ordenados por distancia

        distances[startNode] = 0.0
        priorityQueue.add(startNode to 0.0)

        // 3. Bucle principal del algoritmo.
        while (priorityQueue.isNotEmpty()) {
            val (currentNode, currentDistance) = priorityQueue.poll() // Obtener el nodo no visitado más cercano

            // Si hemos llegado al destino, reconstruimos y devolvemos el camino.
            if (currentNode == endNode) {
                val path = mutableListOf<GeoPoint>()
                var step: Node? = endNode
                while (step != null) {
                    path.add(step.point)
                    step = previousNodes[step]
                }
                return path.reversed() // La ruta se reconstruye hacia atrás, así que la invertimos.
            }

            // Optimización: si ya encontramos un camino más corto a este nodo, lo ignoramos.
            if (currentDistance > distances.getValue(currentNode)) continue

            // 4. Explorar los vecinos del nodo actual.
            for (edge in currentNode.edges) {
                val neighborNode = edge.to
                val newDistance = currentDistance + edge.weight

                // Si encontramos un camino más corto hacia un vecino, actualizamos su distancia y lo añadimos a la cola.
                if (newDistance < distances.getValue(neighborNode)) {
                    distances[neighborNode] = newDistance
                    previousNodes[neighborNode] = currentNode
                    priorityQueue.add(neighborNode to newDistance)
                }
            }
        }

        return null // No se encontró un camino si la cola de prioridad se vacía.
    }
}
