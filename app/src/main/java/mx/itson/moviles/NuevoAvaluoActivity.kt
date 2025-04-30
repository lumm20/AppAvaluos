package mx.itson.moviles

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class NuevoAvaluoActivity : AppCompatActivity() {
    private var folio: String? = null
    private var avaluo: Avaluo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nuevo_avaluo)

        // Inicializar vistas
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val btnInmueble: Button = findViewById(R.id.btnInmueble)
        val btnEntorno: Button = findViewById(R.id.btnEntorno)
        val btnContinuar: Button = findViewById(R.id.btnContinuar)
        val txtFolio: TextView = findViewById(R.id.txtFolio)

        // Revisar si es un avalúo existente o nuevo
        folio = intent.getStringExtra("folio")
        
        if (folio == null) {
            // Generar un nuevo folio para el avalúo
            val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
            val today = dateFormat.format(Date())
            val random = (100..999).random()
            folio = "AV$today$random"
            
            // Crear un nuevo avalúo
            avaluo = Avaluo(
                folio = folio!!,
                fecha_registro = Date(),
                correo_usuario = "usuario@ejemplo.com"
            )
        } else {
            // Cargar avalúo existente (que luego se va a hacer con el Firebase)
            loadExistingAvaluo(folio!!)
        }
        
        // Mostrar el folio
        txtFolio.text = "Folio: $folio"
        
        // Configurar botones
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
            // Simular guardar el avalúo
            saveAvaluo()
            
            // Mostrar pantalla de confirmación
            showConfirmationScreen()
        }
    }
    
    private fun loadExistingAvaluo(folio: String) {
        // Aquí se cargaría el avalúo desde Firebase
        // Mientras creamos un avalúo de ejemplo
        avaluo = Avaluo(
            folio = folio,
            fecha_registro = Date(),
            correo_usuario = "usuario@ejemplo.com",
            caracteristicas_inmueble = mutableListOf(
                CaracteristicasInmueble("Norte", "Sala", "Residencial")
            ),
            caracteristicas_entorno = mutableListOf(
                CaracteristicasEntorno("Urbano", "Parque", "Recreativo")
            )
        )
    }
    
    private fun saveAvaluo() {
        // En una implementación real, aquí se guardaría el avalúo en Firebase
        // Por ahora, solo agregamos algunas características de ejemplo si no existen
        if (avaluo?.caracteristicas_inmueble?.isEmpty() == true) {
            avaluo?.caracteristicas_inmueble?.add(
                CaracteristicasInmueble("Norte", "Sala", "Residencial")
            )
        }
        
        if (avaluo?.caracteristicas_entorno?.isEmpty() == true) {
            avaluo?.caracteristicas_entorno?.add(
                CaracteristicasEntorno("Urbano", "Parque", "Recreativo")
            )
        }
    }
    
    private fun showConfirmationScreen() {
        // Ocultar la vista de ScrollView principal
        findViewById<ScrollView>(R.id.mainScrollView).visibility = View.GONE
        
        // Mostrar pantalla de confirmación
        val fragment = AvaluoConfirmacionFragment.newInstance(avaluo!!)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}