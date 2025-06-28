package com.example.ucmmapapp.model

import kotlinx.serialization.Serializable

/**
 * Representa un sector, edificio o área de interés principal dentro del campus.
 * Anotado con `@Serializable` para permitir la deserialización desde formatos como JSON.
 *
 * @property id El identificador único del sector.
 * @property nombre El nombre del sector (ej: "Facultad de Ingeniería").
 * @property descripcion Una breve descripción del sector.
 */
@Serializable
data class Sector(
    val id: Int,
    val nombre: String,
    val descripcion: String
)

/**
 * Representa una sala, aula u oficina específica que se encuentra dentro de un sector.
 *
 * @property id El identificador único de la sala.
 * @property nombre El nombre o código de la sala (ej: "Auditorio" o "Sala 201").
 * @property piso El número del piso en el que se encuentra la sala.
 * @property id_sector El ID del sector al que pertenece esta sala (clave foránea).
 */
@Serializable
data class Sala(
    val id: Int,
    val nombre: String,
    val piso: Int,
    val id_sector: Int
)

/**
 * Representa un evento programado que tiene lugar en un sector específico del campus.
 *
 * @property id El identificador único del evento.
 * @property titulo El título o nombre del evento.
 * @property descripcion Información detallada sobre el evento.
 * @property fecha La fecha y hora del evento, representada como un String.
 * @property id_sector El ID del sector donde se realiza el evento (clave foránea).
 */
@Serializable
data class Evento(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val fecha: String,
    val id_sector: Int
)

/**
 * Representa una única coordenada geográfica que, junto con otras, define el polígono
 * o la forma de un sector en el mapa.
 *
 * @property id El identificador único de la coordenada.
 * @property id_sector El ID del sector al que pertenece esta coordenada (clave foránea).
 * @property latitud El valor de la latitud.
 * @property longitud El valor de la longitud.
 * @property orden_punto El orden secuencial de este punto en el polígono, para dibujarlo correctamente.
 */
@Serializable
data class SectorCoordenada(
    val id: Int,
    val id_sector: Int,
    val latitud: Double,
    val longitud: Double,
    val orden_punto: Int
)
