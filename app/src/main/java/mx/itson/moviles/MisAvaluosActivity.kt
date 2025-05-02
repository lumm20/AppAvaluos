package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itson.moviles.modelo.Avaluo
import mx.itson.moviles.modelo.CaracteristicaEntorno
import mx.itson.moviles.modelo.CaracteristicaInmueble
import java.util.*

class MisAvaluosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AvaluoAdapter
    private lateinit var emptyView: TextView
    private val avaluos = mutableListOf<Avaluo>()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_avaluos)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.title = "Mis Avalúos"

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerAvaluos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Vista para cuando no hay avalúos
        emptyView = findViewById(R.id.txtEmptyState)
        

        adapter = AvaluoAdapter(this, avaluos) { avaluo ->
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            intent.putExtra("folio", avaluo.folio)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        

        val fab: FloatingActionButton = findViewById(R.id.fabNuevoAvaluo)
        fab.setOnClickListener {
            val intent = Intent(this, NuevoAvaluoActivity::class.java)
            startActivity(intent)
        }
    }
    
    override fun onResume() {
        super.onResume()
        loadAvaluosFromFirebase()
    }
    
    private fun loadAvaluosFromFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showEmptyView("Debe iniciar sesión para ver sus avalúos")
            return
        }
        
        // Referencia a los avalúos en Firebase
        val avaluosRef = database.getReference("avaluos")
        
        avaluosRef.orderByChild("usuarioId").equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    avaluos.clear()
                    
                    for (avaluoSnapshot in snapshot.children) {
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
                        
                        avaluos.add(Avaluo(
                            folio = folio,
                            fechaRegistro = fechaRegistro,
                            usuarioId = usuarioId,
                            correoUsuario = correoUsuario,
                            caracteristicasInmueble = caracteristicasInmueble,
                            caracteristicasEntorno = caracteristicasEntorno
                        ))
                    }
                    
                    adapter.notifyDataSetChanged()
                    
                    if (avaluos.isEmpty()) {
                        showEmptyView("No tienes avalúos registrados.\nPresiona el botón '+' para crear uno nuevo.")
                    } else {
                        hideEmptyView()
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    showEmptyView("Error al cargar avalúos: ${error.message}")
                }
            })
    }
    
    private fun showEmptyView(message: String) {
        emptyView.text = message
        emptyView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }
    
    private fun hideEmptyView() {
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }
}