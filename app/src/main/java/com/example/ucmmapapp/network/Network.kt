package com.example.ucmmapapp.network

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Una instancia global y compartida del cliente HTTP de Ktor, configurada para toda la aplicación.
 * Utilizar una única instancia es una práctica recomendada para la eficiencia de recursos, como la reutilización
 * de conexiones y pools de hilos.
 *
 * La configuración incluye:
 * - **Motor OkHttp:** Se utiliza `OkHttp` como el motor subyacente para realizar las peticiones HTTP,
 *   conocido por su robustez y eficiencia en Android.
 * - **ContentNegotiation:** Se instala el plugin `ContentNegotiation` para manejar automáticamente
 *   la serialización y deserialización de los cuerpos de las peticiones y respuestas.
 * - **Serialización JSON:** Se configura para usar `kotlinx.serialization.json`. La opción
 *   `ignoreUnknownKeys = true` hace que el cliente sea más resiliente a cambios en la API,
 *   ya que no fallará si el servidor envía campos que no están definidos en los modelos de datos locales.
 */
val client = HttpClient(OkHttp) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
}
