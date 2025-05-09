package mx.itson.moviles.modelo

import java.io.Serializable
import java.util.Date

/**
 * Modelo de datos para un avalúo.
 */
data class Avaluo(
    val folio: String = "",
    val fechaRegistro: Long = 0,
    val usuarioId: String = "",
    val correoUsuario: String = "",
    val caracteristicasInmueble: MutableList<CaracteristicaInmueble> = mutableListOf(),
    val caracteristicasEntorno: MutableList<CaracteristicaEntorno> = mutableListOf(),
    val direccion: Direccion?
) : Serializable{

}

/**
 * Modelo de datos para características del inmueble.
 */
data class CaracteristicaInmueble(
    val id: String = "",
    val zona: String = "",
    val nombre: String ="",
    val categoria: String=""
) : Serializable

/**
 * Modelo de datos para características del entorno
 */
data class CaracteristicaEntorno(
    val id: String = "",
    val tipo: String = "",
    val nombre: String = "",
    val categoria: String = ""
) : Serializable

/**
 * Modelo de datos para una dirección.
 */
data class Direccion(
    val id: String = "",
    val calle: String = "",
    val numeroExterior: String = "",
    val numeroInterior: String = "",
    val ciudad: String = "",
    val codigoPostal: String = ""
) : Serializable

/**
 * Modelo de datos para una cita.
 */
//data class Cita(
//    val id: String,
//    val fechaRegistro: Date,
//    val fechaVisita: Date,
//    val telefonoContacto: String,
//    val correoContacto: String,
//    val direccionId: String,
//    val direccion: Direccion? = null
//) : Serializable
