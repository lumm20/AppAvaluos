package mx.itson.moviles.modelo

import java.io.Serializable

data class Cita(
    val id: String? = null,
    val fechaRegistro: Long = 0,
    val fechaVisita: Long = 0,
    val telefonoContacto: String = "",
    val correoContacto: String = "",
    val folioAvaluo: String = "",
    val empresa:String? = null,
    val usuarioId: String = "",
    val folioCita: String = "",
    val activo: Boolean = true,
) : Serializable{
    constructor() : this("", 0, 0, "", "", "", "", "", "", true)
}

