package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MisAvaluosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AvaluoAdapter
    private val avaluos = mutableListOf<Avaluo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_avaluos)

        // Configurar la Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Mis Avalúos"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerAvaluos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Cargar avalúos (simulados para esta demo)
        loadSampleAvaluos()
        
        // Configurar el adaptador
        adapter = AvaluoAdapter(this, avaluos) { avaluo ->
            // Al hacer clic en un avalúo, abrimos su detalle
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            intent.putExtra("folio", avaluo.folio)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        
        // Configurar el Floating Action Button para crear un nuevo avalúo
        val fab: FloatingActionButton = findViewById(R.id.fabNuevoAvaluo)
        fab.setOnClickListener {
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            startActivity(intent)
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Recargar avalúos cuando volvemos a esta pantalla
        // Después esto se va a hacer con Firebase :$
        loadSampleAvaluos()
        adapter.notifyDataSetChanged()
    }
    
    private fun loadSampleAvaluos() {
        // Simular carga de avalúos desde Firebase
        avaluos.clear()
        
        // Crear algunos avalúos de ejemplo
        avaluos.add(
            Avaluo(
                folio = "AV20250401123",
                fecha_registro = Date(),
                correo_usuario = "usuario@ejemplo.com",
                caracteristicas_inmueble = mutableListOf(
                    CaracteristicasInmueble("Norte", "Sala", "Residencial"),
                    CaracteristicasInmueble("Sur", "Cocina", "Familiar")
                ),
                caracteristicas_entorno = mutableListOf(
                    CaracteristicasEntorno("Urbano", "Parque", "Recreativo")
                )
            )
        )
        
        avaluos.add(
            Avaluo(
                folio = "AV20250410456",
                fecha_registro = Date(),
                correo_usuario = "usuario@ejemplo.com",
                caracteristicas_inmueble = mutableListOf(
                    CaracteristicasInmueble("Oeste", "Baño", "Residencial")
                ),
                caracteristicas_entorno = mutableListOf(
                    CaracteristicasEntorno("Rural", "Bosque", "Natural"),
                    CaracteristicasEntorno("Rural", "Río", "Natural")
                )
            )
        )
        
        avaluos.add(
            Avaluo(
                folio = "AV20250420789",
                fecha_registro = Date(),
                correo_usuario = "usuario@ejemplo.com",
                caracteristicas_inmueble = mutableListOf(
                    CaracteristicasInmueble("Este", "Recámara", "Residencial"),
                    CaracteristicasInmueble("Norte", "Estudio", "Residencial"),
                    CaracteristicasInmueble("Sur", "Terraza", "Residencial")
                ),
                caracteristicas_entorno = mutableListOf(
                    CaracteristicasEntorno("Suburbano", "Centro Comercial", "Comercial")
                )
            )
        )
    }
}