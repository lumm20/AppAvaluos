package mx.itson.moviles

import java.io.Serializable
import java.util.Date

/**
 * Modelo de datos para un Avalúo
 */
data class Avaluo(
    val folio: String,
    val fecha_registro: Date,
    val correo_usuario: String,
    val caracteristicas_inmueble: MutableList<CaracteristicasInmueble> = mutableListOf(),
    val caracteristicas_entorno: MutableList<CaracteristicasEntorno> = mutableListOf()
) : Serializable

/**
 * Modelo de datos para características del inmueble
 */
data class CaracteristicasInmueble(
    val ubicacion: String,
    val tipo: String,
    val uso: String
) : Serializable

/**
 * Modelo de datos para características del entorno
 */
data class CaracteristicasEntorno(
    val tipo_zona: String,
    val cercania: String,
    val uso: String
) : Serializable

/**
 * Modelo de datos para una Dirección
 */
data class Direccion(
    val id: String,
    val calle: String,
    val numero_exterior: String,
    val numero_interior: String,
    val ciudad: String,
    val codigo_postal: String
) : Serializable

/**
 * Modelo de datos para una Cita
 */
//data class Cita(
//    val id: String,
//    val fecha_registro: Date,
//    val fecha_visita: Date,
//    val tel_contacto: String,
//    val correo_contacto: String,
//    val direccion: Direccion
//) : Serializable