package com.example.ucmmapapp.network

import com.example.ucmmapapp.model.SectorCoordenada
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Proporciona los métodos para interactuar con el endpoint de la API relacionado con las coordenadas de los sectores.
 * Este servicio es crucial para obtener los datos geográficos que definen los polígonos de los edificios en el mapa.
 *
 * @property client La instancia del cliente Ktor utilizada para realizar las peticiones HTTP.
 */
class CoordenadasService(private val client: HttpClient) {

    /**
     * Obtiene la lista completa de todas las coordenadas de los sectores desde la API.
     * Cada objeto `SectorCoordenada` representa un vértice en el polígono de un sector.
     *
     * @return Una lista de objetos `SectorCoordenada` deserializados desde la respuesta JSON de la API.
     */
    suspend fun getCoordenadas(): List<SectorCoordenada> {
        return client.get("http://10.0.2.2:5000/api/sector-coords/").body()
    }
}
