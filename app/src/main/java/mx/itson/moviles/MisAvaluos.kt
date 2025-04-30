package mx.itson.moviles

import android.content.Intent
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MisAvaluos : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var avaluoAdapter: AvaluoAdapter
    private val avaluosList = mutableListOf<Avaluo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_avaluos)

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerAvaluos)
        emptyStateText = findViewById(R.id.txtEmptyState)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val fabNuevoAvaluo: FloatingActionButton = findViewById(R.id.fabNuevoAvaluo)

        // Configurar botón de regreso
        btnBack.setOnClickListener {
            finish()
        }

        // Configurar botón nuevo avalúo
        fabNuevoAvaluo.setOnClickListener {
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            startActivity(intent)
        }

        // Configurar RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        avaluoAdapter = AvaluoAdapter(this, avaluosList) { avaluo ->
            // Click en un avalúo existente
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            intent.putExtra("folio", avaluo.folio)
            startActivity(intent)
        }
        recyclerView.adapter = avaluoAdapter

        // Cargar datos de prueba o desde Firebase
        cargarAvaluos()
    }

    override fun onResume() {
        super.onResume()
        // Recargar avalúos por si se añadieron nuevos
        cargarAvaluos()
    }

    private fun cargarAvaluos() {
        // Aquí cargaríamos avalúos desde Firebase en una implementación completa
        // Por ahora, solo añadimos datos de prueba
        avaluosList.clear()
        
        // Agregar algunos datos de prueba
        avaluosList.add(
            Avaluo(
                "AV2025001", 
                Date(), 
                "usuario@ejemplo.com",
                mutableListOf(
                    CaracteristicasInmueble("Norte", "Sala", "Residencial"),
                    CaracteristicasInmueble("Este", "Cocina", "Moderna")
                ),
                mutableListOf(
                    CaracteristicasEntorno("Urbano", "Parque", "Recreativo"),
                    CaracteristicasEntorno("Servicios", "Transporte", "Público")
                )
            )
        )
        
        avaluosList.add(
            Avaluo(
                "AV2025002", 
                Date(System.currentTimeMillis() - 86400000), // Ayer
                "usuario@ejemplo.com",
                mutableListOf(
                    CaracteristicasInmueble("Sur", "Casa", "Colonial")
                ),
                mutableListOf(
                    CaracteristicasEntorno("Rural", "Río", "Natural")
                )
            )
        )

        // Actualizar UI
        if (avaluosList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyStateText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyStateText.visibility = View.GONE
            avaluoAdapter.notifyDataSetChanged()
        }
    }
}