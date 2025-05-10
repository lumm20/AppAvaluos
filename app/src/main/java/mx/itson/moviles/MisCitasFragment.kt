package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itson.moviles.modelo.Avaluo
import mx.itson.moviles.modelo.CaracteristicaEntorno
import mx.itson.moviles.modelo.CaracteristicaInmueble
import mx.itson.moviles.modelo.Cita

class MisCitasFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitaAdapter
    private lateinit var emptyView: TextView
    private val citas = mutableListOf<Cita>()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_citas, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.onBackPressedDispatcher?.onBackPressed()
        }

        // Inicializar RecyclerView
        recyclerView = view.findViewById(R.id.recyclerCitas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Texto de estado vacío
        emptyView = view.findViewById(R.id.txtEmptyStateC)

        // Inicializar adapter y asignarlo al RecyclerView
        adapter = CitaAdapter(requireContext(), citas)
        recyclerView.adapter = adapter

        // Cargar datos inicialmente
        loadCitasFromFirebase()

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // Recargar datos cuando el fragmento vuelve a primer plano
        loadCitasFromFirebase()
    }

    private fun showEmptyView(message: String) {
        Log.d("MisCitasFragment", "Mostrando vista vacía: $message")
        emptyView.text = message
        emptyView.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun hideEmptyView() {
        Log.d("MisCitasFragment", "Ocultando vista vacía")
        emptyView.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun loadCitasFromFirebase() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            showEmptyView("Debe iniciar sesión para ver sus citas")
            return
        }

        val citasRef = database.getReference("citas")
        Log.d("MisCitasFragment", "Cargando citas para usuario: ${currentUser.uid}")

        citasRef.orderByChild("usuarioId").equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    citas.clear()

                    Log.d("MisCitasFragment", "Datos recibidos, snapshot count: ${snapshot.childrenCount}")
                    for (citaSnapshot in snapshot.children) {
                        val folio = citaSnapshot.key ?: continue
                        val fechaRegistroLong = citaSnapshot.child("fechaRegistro").getValue(Long::class.java) ?: System.currentTimeMillis()
                        val fechaVisitaLong = citaSnapshot.child("fechaVisita").getValue(Long::class.java) ?: System.currentTimeMillis()
                        val usuarioId = citaSnapshot.child("usuarioId").getValue(String::class.java) ?: ""
                        val empresa = citaSnapshot.child("empresa").getValue(String::class.java) ?: ""
                        val activo = citaSnapshot.child("activo").getValue(Boolean::class.java) ?: true

                        Log.d("MisCitasFragment", "Cita encontrada - Folio: $folio, Activo: $activo")

                        // Solo añadir citas activas
                        if (activo) {
                            val cita = Cita(
                                usuarioId = usuarioId,
                                folioCita = folio,
                                fechaVisita = fechaVisitaLong,
                                fechaRegistro = fechaRegistroLong,
                                empresa = empresa,
                                activo = activo
                            )

                            citas.add(cita)
                        }
                    }

                    Log.d("MisCitasFragment", "Número de citas cargadas: ${citas.size}")

                    // Actualiza UI en el hilo principal
                    requireActivity().runOnUiThread {
                        adapter.notifyDataSetChanged()

                        if (citas.isEmpty()) {
                            showEmptyView("No tienes citas registradas.")
                        } else {
                            hideEmptyView()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MisCitasFragment", "Error al cargar citas: ${error.message}")
                    showEmptyView("Error al cargar citas: ${error.message}")
                }
            })
    }
}