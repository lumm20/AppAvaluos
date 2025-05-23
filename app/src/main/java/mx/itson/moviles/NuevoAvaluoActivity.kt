package mx.itson.moviles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import mx.itson.moviles.modelo.Avaluo
import mx.itson.moviles.modelo.CaracteristicaEntorno
import mx.itson.moviles.modelo.CaracteristicaInmueble
import mx.itson.moviles.modelo.Direccion
import java.text.SimpleDateFormat
import java.util.*

class NuevoAvaluoActivity : AppCompatActivity() {
    private var folio: String? = null
    private var avaluo: Avaluo? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_avaluo)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnInmueble: Button = findViewById(R.id.btnInmueble)
        val btnEntorno: Button = findViewById(R.id.btnEntorno)
        val btnContinuar: Button = findViewById(R.id.btnContinuar)
        val txtFolio: TextView = findViewById(R.id.txtFolio)


            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val today = dateFormat.format(Date())
            val random = (100..999).random()
            folio = "AV$today$random"

            val currentUser = auth.currentUser
            if (currentUser != null) {
                avaluo = Avaluo(
                    folio = folio!!,
                    fechaRegistro = System.currentTimeMillis(),
                    usuarioId = currentUser.uid,
                    correoUsuario = currentUser.email ?: "",
                    direccion = null
                )
            } else {
                Toast.makeText(this, "Debe iniciar sesión para crear avalúos", Toast.LENGTH_SHORT).show()
                finish()
                return
            }


        txtFolio.text = "Folio: $folio"
        

        btnBack.setOnClickListener {
            finish()
        }
        
        btnInmueble.setOnClickListener {
            val fragment = OpcionesCaracteristicas.newInstance("inmueble", folio!!, false)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        btnEntorno.setOnClickListener {
            val fragment = OpcionesCaracteristicas.newInstance("entorno", folio!!, true)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        
        btnContinuar.setOnClickListener {
            saveAvaluo()
        }

        val expandDireccionBtn: Button = findViewById(R.id.expand_direccion_btn)
        val direccionLayout: LinearLayout = findViewById(R.id.direccion_layout)

        expandDireccionBtn.setOnClickListener {
            direccionLayout.visibility = if (direccionLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }
    }

    private fun saveAvaluo() {
        if (avaluo == null) {
            Toast.makeText(this, "Error: no se pudo crear el avalúo", Toast.LENGTH_SHORT).show()
            return
        }
        val campos = validarCamposDireccion()
        if(campos != null){
            Toast.makeText(this, campos, Toast.LENGTH_SHORT).show()
            return
        }

        convertirCaracteristicasSeleccionadas()

        val avaluosRef = database.getReference("avaluos")

        val avaluoData = hashMapOf<String, Any>(
            "folio" to avaluo!!.folio,
            "fechaRegistro" to avaluo!!.fechaRegistro,
            "usuarioId" to avaluo!!.usuarioId,
            "correoUsuario" to avaluo!!.correoUsuario,
            "direccion" to createDireccion()
        )

        avaluosRef.child(avaluo!!.folio).updateChildren(avaluoData)
            .addOnSuccessListener {
                guardarCaracteristicasInmueble(avaluo!!.folio)

                guardarCaracteristicasEntorno(avaluo!!.folio)

                showConfirmationScreen()

                Toast.makeText(this, "Avalúo guardado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error al guardar el avalúo: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validarCamposDireccion(): String? {
        val calleEditText: EditText = findViewById(R.id.calle_et)
        val numeroExteriorEditText: EditText = findViewById(R.id.numero_exterior_et)
        val ciudadEditText: EditText = findViewById(R.id.ciudad_et)
        val codigoPostalEditText: EditText = findViewById(R.id.codigo_postal_et)

        val calle = calleEditText.text.toString().trim()
        val numeroExterior = numeroExteriorEditText.text.toString().trim()
        val ciudad = ciudadEditText.text.toString().trim()
        val codigoPostal = codigoPostalEditText.text.toString().trim()


        if (calle.isEmpty()) {
            return "Campo Calle requerido falta"
        }

        if (numeroExterior.isEmpty()) {
            return "Campo Número Exterior requerido falta"
        }

        if (ciudad.isEmpty()) {
            return "Campo Ciudad requerido falta"
        }

        if (codigoPostal.isEmpty()) {
            return "Campo Código Postal requerido falta"
        }
        // Si todos los campos requeridos son válidos, devuelve null
        return null
    }

    private fun convertirCaracteristicasSeleccionadas() {
        val inmuebleMap = CaracteristicasAvaluo.caracteristicasGuardadasPorFolio[folio]

        avaluo?.caracteristicasInmueble?.clear()
        avaluo?.caracteristicasEntorno?.clear()

        inmuebleMap?.entries?.forEach { entry ->
            val clave = entry.key
            val selecciones = entry.value

            if (clave.startsWith("inmueble_") && selecciones.isNotEmpty()) {
                val parts = clave.split("_")
                if (parts.size >= 3) {
                    val lugarId = parts[1].toIntOrNull() ?: 0
                    val tipo = parts[2]

                    // Determinar zona según lugarId
                    val zona = when (lugarId) {
                        1 -> "Sala"
                        2 -> "Comedor"
                        3 -> "Cocina"
                        4 -> "Baño"
                        5 -> "Recámara"
                        6 -> "Estancia"
                        7 -> "Patio Posterior"
                        8 -> "Estacionamiento"
                        9 -> "Terraza"
                        else -> "Otro"
                    }

                    selecciones.forEach { idCaracteristica ->
                        val nombre = obtenerNombreCaracteristica(idCaracteristica)

                        avaluo?.caracteristicasInmueble?.add(
                            CaracteristicaInmueble(
                                zona = zona,
                                nombre = nombre,
                                categoria = tipo
                            )
                        )
                    }
                }
            } else if (clave.startsWith("entorno_") && selecciones.isNotEmpty()) {
                val parts = clave.split("_")
                if (parts.size >= 2) {
                    val lugarId = parts[1].toIntOrNull() ?: 0

                    // Determinar tipo según lugarId
                    val tipo = when (lugarId) {
                        10 -> "Instalaciones Hidráulicas"
                        11 -> "Instalaciones Sanitarias"
                        12 -> "Instalaciones Eléctricas"
                        13 -> "Obras Complementarias"
                        14 -> "Elementos Accesorios"
                        else -> "Otro"
                    }

                    selecciones.forEach { idCaracteristica ->
                        val nombre = obtenerNombreCaracteristica(idCaracteristica)

                        avaluo?.caracteristicasEntorno?.add(
                            CaracteristicaEntorno(
                                tipo = tipo,
                                nombre = nombre,
                                categoria = "General"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun guardarCaracteristicasInmueble(folio: String) {
        val caracteristicasRef = database.getReference("avaluos").child(folio).child("caracteristicasInmueble")

        caracteristicasRef.removeValue().addOnSuccessListener {
            avaluo?.caracteristicasInmueble?.forEach { caracteristica ->
                val caracteristicaId = caracteristicasRef.push().key ?: return@forEach

                val caracteristicaData = hashMapOf(
                    "zona" to caracteristica.zona,
                    "nombre" to caracteristica.nombre,
                    "categoria" to caracteristica.categoria
                )

                caracteristicasRef.child(caracteristicaId).setValue(caracteristicaData)
            }
        }
    }

    private fun guardarCaracteristicasEntorno(folio: String) {
        val caracteristicasRef = database.getReference("avaluos").child(folio).child("caracteristicasEntorno")

        caracteristicasRef.removeValue().addOnSuccessListener {
            avaluo?.caracteristicasEntorno?.forEach { caracteristica ->
                val caracteristicaId = caracteristicasRef.push().key ?: return@forEach

                val caracteristicaData = hashMapOf(
                    "tipo" to caracteristica.tipo,
                    "nombre" to caracteristica.nombre,
                    "categoria" to caracteristica.categoria
                )

                caracteristicasRef.child(caracteristicaId).setValue(caracteristicaData)
            }
        }
    }

    private fun createDireccion(): Direccion {
        val calleEditText: EditText = findViewById(R.id.calle_et)
        val numeroExteriorEditText: EditText = findViewById(R.id.numero_exterior_et)
        val numeroInteriorEditText: EditText = findViewById(R.id.numero_interior_et)
        val ciudadEditText: EditText = findViewById(R.id.ciudad_et)
        val codigoPostalEditText: EditText = findViewById(R.id.codigo_postal_et)

        val calle = calleEditText.text.toString()
        val numeroExterior = numeroExteriorEditText.text.toString()
        val numeroInterior = numeroInteriorEditText.text.toString()
        val ciudad = ciudadEditText.text.toString()
        val codigoPostal = codigoPostalEditText.text.toString()

        // Generar un ID único para la dirección. Podrías usar UUID o cualquier otro método de generación de ID.
        val id = UUID.randomUUID().toString()

        return Direccion(
            id = id,
            calle = calle,
            numeroExterior = numeroExterior,
            numeroInterior = numeroInterior,
            ciudad = ciudad,
            codigoPostal = codigoPostal
        )
    }

    private fun showConfirmationScreen() {
        findViewById<ScrollView>(R.id.mainScrollView).visibility = View.GONE

        val fragment = AvaluoConfirmacionFragment.newInstance(avaluo!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    /**
     * Obtiene el nombre de una característica a partir de su ID
     * @param idCaracteristica ID de la característica
     * @return Nombre de la característica
     */
    private fun obtenerNombreCaracteristica(idCaracteristica: Int): String {
        return CatalogoCaracteristicas.obtenerNombreCaracteristica(idCaracteristica)
    }
}