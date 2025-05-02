package mx.itson.moviles

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView

class CaracteristicasAvaluo : Fragment() {
    private var menu: ArrayList<Caracteristica> = ArrayList<Caracteristica>()
    private var adapter: AdaptadorCaracteristica? = null

    private var lugar: Int = 0
    private var titulo: String = "Desconocido"
    private var tipo: String = "Pisos"
    private var folio: String = ""
    private lateinit var bottomNavigation: BottomNavigationView

    // Mapa para almacenar el estado de las características seleccionadas
    private val caracteristicasSeleccionadas = mutableMapOf<String, MutableSet<Int>>()

    // Objeto companion para guardar las características seleccionadas por folio
    companion object {
        // Define constant keys for arguments
        private const val ARG_LUGAR = "lugar"
        private const val ARG_TITULO = "desc"
        private const val ARG_TIPO = "tipo"
        private const val ARG_FOLIO = "folio"
        
        // Mapa estático para mantener las características seleccionadas por folio
        // La clave externa es el folio, y el valor es otro mapa que almacena las características por lugar/tipo
        val caracteristicasGuardadasPorFolio = mutableMapOf<String, MutableMap<String, MutableSet<Int>>>()

        @JvmStatic
        fun newInstance(lugar: Int, titulo: String, tipo: String, folio: String) =
            CaracteristicasAvaluo().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LUGAR, lugar)
                    putString(ARG_TITULO, titulo)
                    putString(ARG_TIPO, tipo)
                    putString(ARG_FOLIO, folio)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            lugar = it.getInt(ARG_LUGAR, 0)
            titulo = it.getString(ARG_TITULO) ?: "Desconocido"
            tipo = it.getString(ARG_TIPO) ?: "Pisos"
            folio = it.getString(ARG_FOLIO) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_caracteristicas_avaluo, container, false)

        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        bottomNavigation = view.findViewById(R.id.secondBottomNavigation)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)
        val btnGuardar: Button = view.findViewById(R.id.btnGuardar)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        btnGuardar.setOnClickListener {
            guardarCaracteristicas()
            Toast.makeText(context, "Características guardadas correctamente", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        if (lugar in 10..14) {
            bottomNavigation.visibility = View.GONE
            txtTitulo.text = titulo // Mostramos el título
        } else {
            bottomNavigation.visibility = View.VISIBLE
            txtTitulo.text = "Acabados $titulo" // Para Pisos, Muros, Plafones
            setupBottomNavigation()
        }

        val listaCaracteristicas: ListView = view.findViewById(R.id.listaCaracteristicas)
        menu = obtenerCaracteristicas()
        
        val clave = obtenerClaveAlmacenamiento()
        if (haySeleccionesParaFolio(folio, clave)) {
            val seleccionadas = obtenerSeleccionesParaFolio(folio, clave)
            
            for (caracteristica in menu) {
                caracteristica.seleccionado = caracteristica.idCaracteristica in seleccionadas
            }
        }
        
        adapter = AdaptadorCaracteristica(requireContext(), menu) { id, isChecked ->
            actualizarSeleccion(id, isChecked)
        }
        listaCaracteristicas.adapter = adapter

        return view
    }

    private fun obtenerClaveAlmacenamiento(): String {
        return if (lugar in 10..14) {
            "entorno_$lugar"
        } else {
            "inmueble_${lugar}_$tipo"
        }
    }
    
    private fun haySeleccionesParaFolio(folio: String, clave: String): Boolean {
        return caracteristicasGuardadasPorFolio[folio]?.containsKey(clave) == true
    }
    
    private fun obtenerSeleccionesParaFolio(folio: String, clave: String): Set<Int> {
        return caracteristicasGuardadasPorFolio[folio]?.get(clave) ?: emptySet()
    }

    private fun actualizarSeleccion(idCaracteristica: Int, seleccionado: Boolean) {
        val clave = obtenerClaveAlmacenamiento()
        
        if (!caracteristicasSeleccionadas.containsKey(clave)) {
            caracteristicasSeleccionadas[clave] = mutableSetOf()
        }
        
        val selecciones = caracteristicasSeleccionadas[clave]!!
        
        if (seleccionado) {
            selecciones.add(idCaracteristica)
        } else {
            selecciones.remove(idCaracteristica)
        }
    }
    
    private fun guardarCaracteristicas() {
        if (!caracteristicasGuardadasPorFolio.containsKey(folio)) {
            caracteristicasGuardadasPorFolio[folio] = mutableMapOf()
        }
        
        val mapaParaFolio = caracteristicasGuardadasPorFolio[folio]!!
        
        for ((clave, selecciones) in caracteristicasSeleccionadas) {
            if (!mapaParaFolio.containsKey(clave)) {
                mapaParaFolio[clave] = mutableSetOf()
            }
            
            val seleccionesGuardadas = mapaParaFolio[clave]!!
            seleccionesGuardadas.clear()
            seleccionesGuardadas.addAll(selecciones)
        }
    }

    private fun setupBottomNavigation() {
        if (lugar !in 1..9) return

        bottomNavigation.selectedItemId = when (tipo) {
            "Pisos" -> R.id.nav_piso
            "Muros" -> R.id.nav_muro
            "Plafones" -> R.id.nav_plafones
            else -> R.id.nav_piso
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_piso -> {
                    if (tipo != "Pisos") {
                        tipo = "Pisos"
                        actualizarVista()
                    }
                    true
                }
                R.id.nav_muro -> {
                    if (tipo != "Muros") {
                        tipo = "Muros"
                        actualizarVista()
                    }
                    true
                }
                R.id.nav_plafones -> {
                    if (tipo != "Plafones") {
                        tipo = "Plafones"
                        actualizarVista()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun actualizarVista() {
        if (lugar !in 1..9) return

        view?.findViewById<TextView>(R.id.txtTitulo)?.text = "Acabados $titulo"
        val listaCaracteristicas = view?.findViewById<ListView>(R.id.listaCaracteristicas)
        menu = obtenerCaracteristicas()
        
        val clave = obtenerClaveAlmacenamiento()
        if (haySeleccionesParaFolio(folio, clave)) {
            val seleccionadas = obtenerSeleccionesParaFolio(folio, clave)
            
            for (caracteristica in menu) {
                caracteristica.seleccionado = caracteristica.idCaracteristica in seleccionadas
            }
        }
        
        adapter = AdaptadorCaracteristica(requireContext(), menu) { id, isChecked ->
            actualizarSeleccion(id, isChecked)
        }
        listaCaracteristicas?.adapter = adapter
    }

    private fun obtenerCaracteristicas(): ArrayList<Caracteristica> {
        val menu = ArrayList<Caracteristica>()

        if (lugar in 10..14) {
            menu.addAll(CatalogoCaracteristicas.obtenerCaracteristicasEntorno(lugar))
        } else {
            menu.addAll(CatalogoCaracteristicas.obtenerCaracteristicasInmueble(tipo))
        }
        
        return menu
    }

    private class AdaptadorCaracteristica(
        private val contexto: Context, 
        private val caracteristicas: ArrayList<Caracteristica>, 
        private val onCheckBoxClick: (Int, Boolean) -> Unit
    ) : BaseAdapter() {

        override fun getCount(): Int = caracteristicas.size

        override fun getItem(position: Int): Any = caracteristicas[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val item = caracteristicas[position]
            val inflador = LayoutInflater.from(contexto)
            val vista = convertView ?: inflador.inflate(R.layout.item_caracteristica, null)

            val txtCaracteristica: TextView = vista.findViewById(R.id.txvCarac)
            val checkBox: CheckBox = vista.findViewById(R.id.cbx)

            txtCaracteristica.text = item.nombre
            
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = item.seleccionado
            
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.seleccionado = isChecked
                onCheckBoxClick(item.idCaracteristica, isChecked)
            }

            return vista
        }
    }
}