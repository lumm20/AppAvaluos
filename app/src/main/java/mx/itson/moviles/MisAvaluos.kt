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
import mx.itson.moviles.modelo.Avaluo
import mx.itson.moviles.modelo.CaracteristicaEntorno
import mx.itson.moviles.modelo.CaracteristicaInmueble
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MisAvaluos : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyStateText: TextView
    private lateinit var avaluoAdapter: AvaluoAdapter
    private val avaluosList = mutableListOf<Avaluo>()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_avaluos)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        recyclerView = findViewById(R.id.recyclerAvaluos)
        emptyStateText = findViewById(R.id.txtEmptyState)
        val btnBack: ImageButton = findViewById(R.id.btnBack)
        val fabNuevoAvaluo: FloatingActionButton = findViewById(R.id.fabNuevoAvaluo)


        btnBack.setOnClickListener {
            finish()
        }

        fabNuevoAvaluo.setOnClickListener {
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        avaluoAdapter = AvaluoAdapter(this, avaluosList) { avaluo ->
            // Click en un avalúo existente
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            intent.putExtra("folio", avaluo.folio)
            startActivity(intent)
        }
        recyclerView.adapter = avaluoAdapter

        cargarAvaluos()
    }

    override fun onResume() {
        super.onResume()
        cargarAvaluos()
    }

    private fun cargarAvaluos() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showEmptyState("Debe iniciar sesión para ver sus avalúos")
            return
        }
  
        avaluosList.clear()
        
        // Referencia a los avalúos en Firebase
        val avaluosRef = database.getReference("avaluos")

        avaluosRef.orderByChild("usuarioId").equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    avaluosList.clear()
                    
                    for (avaluoSnapshot in snapshot.children) {
                        // Obtener datos básicos del avalúo
                        val folio = avaluoSnapshot.key ?: continue
                        val fechaRegistroLong = avaluoSnapshot.child("fechaRegistro").getValue(Long::class.java) ?: System.currentTimeMillis()
                        val fechaRegistro = Date(fechaRegistroLong)
                        val usuarioId = avaluoSnapshot.child("usuarioId").getValue(String::class.java) ?: ""
                        val correoUsuario = avaluoSnapshot.child("correoUsuario").getValue(String::class.java) ?: ""
                        
                        val caracteristicasInmueble = mutableListOf<CaracteristicaInmueble>()
                        val caracteristicasEntorno = mutableListOf<CaracteristicaEntorno>()
                        
                        avaluoSnapshot.child("caracteristicasInmueble").children.forEach { carSnapshot ->
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

                        avaluoSnapshot.child("caracteristicasEntorno").children.forEach { carSnapshot ->
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
                        
                        avaluosList.add(Avaluo(
                            folio = folio,
                            fechaRegistro = fechaRegistro,
                            usuarioId = usuarioId,
                            correoUsuario = correoUsuario,
                            caracteristicasInmueble = caracteristicasInmueble,
                            caracteristicasEntorno = caracteristicasEntorno
                        ))
                    }
                    
                    if (avaluosList.isEmpty()) {
                        showEmptyState("No tienes avalúos registrados.\nPresiona el botón '+' para crear uno nuevo.")
                    } else {
                        hideEmptyState()
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    showEmptyState("Error al cargar avalúos: ${error.message}")
                }
            })
    }
    
    private fun showEmptyState(message: String) {
        emptyStateText.text = message
        emptyStateText.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
    
    private fun hideEmptyState() {
        emptyStateText.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        avaluoAdapter.notifyDataSetChanged()
    }
}