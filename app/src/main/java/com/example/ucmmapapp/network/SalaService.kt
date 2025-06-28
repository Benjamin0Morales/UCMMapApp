package com.example.ucmmapapp.network

import com.example.ucmmapapp.model.Sala
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

/**
 * Proporciona los métodos para interactuar con el endpoint de la API relacionado con las salas o aulas.
 * Encapsula la lógica de las peticiones de red para obtener datos de las salas del campus.
 *
 * @property client La instancia del cliente Ktor utilizada para realizar las peticiones HTTP.
 */
class SalaService(private val client: HttpClient) {

    /**
     * Obtiene la lista completa de todas las salas desde la API.
     * Es una función de suspensión, por lo que debe ser llamada desde una corrutina.
     *
     * La URL utiliza la IP `10.0.2.2` para conectar con el servidor de desarrollo local
     * desde el emulador de Android.
     *
     * @return Una lista de objetos `Sala` deserializados desde la respuesta JSON de la API.
     */
    suspend fun getSalas(): List<Sala> {
        return client.get("http://10.0.2.2:5000/api/salas/").body()
    }
}
