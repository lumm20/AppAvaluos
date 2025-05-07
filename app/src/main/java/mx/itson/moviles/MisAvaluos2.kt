package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MisAvaluos2.newInstance] factory method to
 * create an instance of this fragment.
 */
class MisAvaluos2 : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AvaluoAdapter
    private lateinit var emptyView: TextView
    private val avaluos = mutableListOf<Avaluo>()
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_avaluos2, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.title = "Mis Avalúos"

        toolbar.setNavigationOnClickListener {
            (activity as? MainActivity)?.onBackPressedDispatcher?.onBackPressed()
        }

        recyclerView = view.findViewById(R.id.recyclerAvaluos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Vista para cuando no hay avalúos
        emptyView = view.findViewById(R.id.txtEmptyState)


        adapter = AvaluoAdapter(requireContext(), avaluos) { avaluo ->
            val intent = Intent(requireContext(), NuevoAvaluoActivity::class.java)
            intent.putExtra("folio", avaluo.folio)
            startActivity(intent)
        }
        recyclerView.adapter = adapter


        val fab: FloatingActionButton = view.findViewById(R.id.fabNuevoAvaluo)
        fab.setOnClickListener {
            val intent = Intent(requireContext(), NuevoAvaluoActivity::class.java)
            startActivity(intent)
        }


        return view
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
                        val fechaRegistro = fechaRegistroLong
                        val usuarioId = avaluoSnapshot.child("usuarioId").getValue(String::class.java) ?: ""
                        val correoUsuario = avaluoSnapshot.child("correoUsuario").getValue(String::class.java) ?: ""


                        val caracteristicasInmueble = mutableListOf<CaracteristicaInmueble>()
                        val caracteristicasEntorno = mutableListOf<CaracteristicaEntorno>()
                        //val direccion = Direccion

                        avaluoSnapshot.child("caracteristicasInmueble").children.forEach { carSnapshot ->
                            val zona = carSnapshot.child("zona").getValue(String::class.java) ?: ""
                            val nombre = carSnapshot.child("nombre").getValue(String::class.java) ?: ""
                            val categoria = carSnapshot.child("categoria").getValue(String::class.java) ?: ""

                            caracteristicasInmueble.add(
                                CaracteristicaInmueble(
                                    id = carSnapshot.key ?: "",
                                    zona = zona,
                                    nombre = nombre,
                                    categoria = categoria
                                )
                            )
                        }

                        avaluoSnapshot.child("caracteristicasEntorno").children.forEach { carSnapshot ->
                            val tipo = carSnapshot.child("tipo").getValue(String::class.java) ?: ""
                            val nombre = carSnapshot.child("nombre").getValue(String::class.java) ?: ""
                            val categoria = carSnapshot.child("categoria").getValue(String::class.java) ?: ""

                            caracteristicasEntorno.add(
                                CaracteristicaEntorno(
                                    id = carSnapshot.key ?: "",
                                    tipo = tipo,
                                    nombre = nombre,
                                    categoria = categoria
                                )
                            )
                        }

                        avaluos.add(Avaluo(
                            folio = folio,
                            fechaRegistro = fechaRegistro,
                            usuarioId = usuarioId,
                            correoUsuario = correoUsuario,
                            caracteristicasInmueble = caracteristicasInmueble,
                            caracteristicasEntorno = caracteristicasEntorno,
                            //direccion = null
                            ////////////////////////////////////////////////////////////////////////////////////
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MisAvaluos2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MisAvaluos2().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}