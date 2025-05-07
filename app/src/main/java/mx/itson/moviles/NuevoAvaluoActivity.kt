package mx.itson.moviles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import mx.itson.moviles.modelo.Avaluo
import mx.itson.moviles.modelo.CaracteristicaEntorno
import mx.itson.moviles.modelo.CaracteristicaInmueble
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

        // Revisar si es un avalúo existente o nuevo
        folio = intent.getStringExtra("folio")
        
        if (folio == null) {
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
                    correoUsuario = currentUser.email ?: ""
                )
            } else {
                Toast.makeText(this, "Debe iniciar sesión para crear avalúos", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
        } else {
            loadExistingAvaluo(folio!!)
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
    }
    
    private fun loadExistingAvaluo(folio: String) {
        val avaluosRef = database.getReference("avaluos").child(folio)
        
        avaluosRef.get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val fechaRegistroLong = snapshot.child("fechaRegistro").getValue(Long::class.java) ?: System.currentTimeMillis()
                val fechaRegistro = Date(fechaRegistroLong)
                val usuarioId = snapshot.child("usuarioId").getValue(String::class.java) ?: ""
                val correoUsuario = snapshot.child("correoUsuario").getValue(String::class.java) ?: ""
                
                val caracteristicasInmueble = mutableListOf<CaracteristicaInmueble>()
                val caracteristicasEntorno = mutableListOf<CaracteristicaEntorno>()
                
                snapshot.child("caracteristicasInmueble").children.forEach { carSnapshot ->
                    val zona = carSnapshot.child("zona").getValue(String::class.java) ?: ""
                    val nombre = carSnapshot.child("nombre").getValue(String::class.java) ?: ""
                    val categoria = carSnapshot.child("categoria").getValue(String::class.java) ?: ""
                    
                    caracteristicasInmueble.add(CaracteristicaInmueble(
                        id = carSnapshot.key ?: "",
                        zona = zona,
                        nombre = nombre,
                        categoria = categoria
                    ))
                }
                
                snapshot.child("caracteristicasEntorno").children.forEach { carSnapshot ->
                    val tipo = carSnapshot.child("tipo").getValue(String::class.java) ?: ""
                    val nombre = carSnapshot.child("nombre").getValue(String::class.java) ?: ""
                    val categoria = carSnapshot.child("categoria").getValue(String::class.java) ?: ""
                    
                    caracteristicasEntorno.add(CaracteristicaEntorno(
                        id = carSnapshot.key ?: "",
                        tipo = tipo,
                        nombre = nombre,
                        categoria = categoria
                    ))
                }

                avaluo = Avaluo(
                    folio = folio,
                    fechaRegistro = fechaRegistroLong,
                    usuarioId = usuarioId,
                    correoUsuario = correoUsuario,
                    caracteristicasInmueble = caracteristicasInmueble,
                    caracteristicasEntorno = caracteristicasEntorno
                )
                
                if (auth.currentUser?.uid != usuarioId) {
                    Toast.makeText(this, "Este avalúo pertenece a otro usuario", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "No se encontró el avalúo", Toast.LENGTH_SHORT).show()
                finish()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Error al cargar el avalúo: ${exception.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun saveAvaluo() {
        if (avaluo == null) {
            Toast.makeText(this, "Error: no se pudo crear el avalúo", Toast.LENGTH_SHORT).show()
            return
        }
        
        convertirCaracteristicasSeleccionadas()
        
        val avaluosRef = database.getReference("avaluos")
        
        val avaluoData = hashMapOf<String, Any>(
            "folio" to avaluo!!.folio,
            "fechaRegistro" to avaluo!!.fechaRegistro,
            "usuarioId" to avaluo!!.usuarioId,
            "correoUsuario" to avaluo!!.correoUsuario
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