package mx.itson.moviles.modelo

import java.io.Serializable

/**
 * Modelo de datos para Usuario que sigue el diagrama ER.
 * Se utiliza para almacenar la información del usuario en la aplicación.
 */
data class Usuario(
    val id: String = "",
    val nombre: String = "",
    val apellidoPaterno: String = "",
    val apellidoMaterno: String = "",
    val correo: String = "",
    val telefono: String = "",
    val fechaRegistro: String = ""
) : Serializable