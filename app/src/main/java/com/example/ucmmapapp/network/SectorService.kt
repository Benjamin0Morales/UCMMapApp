package com.example.ucmmapapp.network

import com.example.ucmmapapp.model.Sector
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Proporciona los métodos para interactuar con el endpoint de la API relacionado con los sectores.
 * Encapsula la lógica de las peticiones de red para obtener datos de los sectores del campus.
 *
 * @property client La instancia del cliente Ktor utilizada para realizar las peticiones HTTP.
 */
class SectorService(private val client: HttpClient) {

    /**
     * Obtiene la lista completa de todos los sectores desde la API.
     * Es una función de suspensión, por lo que debe ser llamada desde una corrutina o desde otra función suspend.
     *
     * La URL `http://10.0.2.2:5000/` apunta al localhost de la máquina anfitriona cuando se ejecuta
     * desde el emulador de Android, permitiendo el desarrollo local con un backend en la misma máquina.
     *
     * @return Una lista de objetos `Sector` deserializados desde la respuesta JSON de la API.
     */
    suspend fun getSectores(): List<Sector> {
        return client.get("http://10.0.2.2:5000/api/sectores/").body()
    }
}
