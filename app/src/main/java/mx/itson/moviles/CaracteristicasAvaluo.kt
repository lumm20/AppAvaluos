package mx.itson.moviles

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CaracteristicasAvaluo : Fragment() {
    private var menu: ArrayList<Caracteristica> = ArrayList<Caracteristica>()
    private var adapter: AdaptadorCaracteristica? = null

    private var lugar: Int = 0
    private var titulo: String = "Desconocido"
    private var tipo: String = "Pisos"
    private lateinit var bottomNavigation: BottomNavigationView

    // Mapa para almacenar el estado de las características seleccionadas
    private val caracteristicasSeleccionadas = mutableMapOf<String, MutableSet<Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve arguments passed to the fragment
        arguments?.let {
            lugar = it.getInt(ARG_LUGAR, 0)
            titulo = it.getString(ARG_TITULO) ?: "Desconocido"
            tipo = it.getString(ARG_TIPO) ?: "Pisos"
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

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Si lugar está entre 10 y 14 (entorno), ocultamos el BottomNavigationView y ajustamos el título
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
        
        // Restaurar selecciones previas
        val clave = obtenerClaveAlmacenamiento()
        if (caracteristicasSeleccionadas.containsKey(clave)) {
            val seleccionadas = caracteristicasSeleccionadas[clave] ?: emptySet()
            
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

    private fun setupBottomNavigation() {
        // Solo configuramos el BottomNavigationView si lugar está entre 1 y 9
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
        // Solo actualizamos la vista si lugar está entre 1 y 9
        if (lugar !in 1..9) return

        view?.findViewById<TextView>(R.id.txtTitulo)?.text = "Acabados $titulo"
        val listaCaracteristicas = view?.findViewById<ListView>(R.id.listaCaracteristicas)
        menu = obtenerCaracteristicas()
        
        // Restaurar selecciones previas
        val clave = obtenerClaveAlmacenamiento()
        if (caracteristicasSeleccionadas.containsKey(clave)) {
            val seleccionadas = caracteristicasSeleccionadas[clave] ?: emptySet()
            
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

        // Si lugar está entre 10 y 14, mostramos las características del entorno
        when (lugar) {
            10 -> { // Instalaciones Hidráulicas
                menu.add(Caracteristica(1, "Tubería de cobre", false))
                menu.add(Caracteristica(2, "Tubería de PVC", false))
                menu.add(Caracteristica(3, "Tubería de CPVC", false))
                menu.add(Caracteristica(4, "Tubería galvanizada", false))
                menu.add(Caracteristica(5, "Tubería de polietileno", false))
                menu.add(Caracteristica(6, "Tubería de acero inoxidable", false))
                menu.add(Caracteristica(7, "Tubería de hierro fundido", false))
                menu.add(Caracteristica(8, "Sistema de bombeo", false))
                menu.add(Caracteristica(9, "Cisterna", false))
                menu.add(Caracteristica(10, "Tinaco", false))
                menu.add(Caracteristica(11, "Hidroneumático", false))
                menu.add(Caracteristica(12, "Válvulas de control", false))
                menu.add(Caracteristica(13, "Sistema de filtración", false))
            }
            11 -> { // Instalaciones Sanitarias
                menu.add(Caracteristica(1, "Tubería de PVC sanitario", false))
                menu.add(Caracteristica(2, "Tubería de concreto", false))
                menu.add(Caracteristica(3, "Tubería de polipropileno", false))
                menu.add(Caracteristica(4, "Drenaje conectado a red municipal", false))
                menu.add(Caracteristica(5, "Fosa séptica", false))
                menu.add(Caracteristica(6, "Biodigestor", false))
                menu.add(Caracteristica(7, "Registro sanitario", false))
                menu.add(Caracteristica(8, "Sistema de ventilación", false))
                menu.add(Caracteristica(9, "Tubería de desagüe", false))
                menu.add(Caracteristica(10, "Sistema de tratamiento de aguas residuales", false))
            }
            12 -> { // Instalaciones Eléctricas
                menu.add(Caracteristica(1, "Cableado de cobre", false))
                menu.add(Caracteristica(2, "Cableado de aluminio", false))
                menu.add(Caracteristica(3, "Tubería conduit", false))
                menu.add(Caracteristica(4, "Tubería galvanizada", false))
                menu.add(Caracteristica(5, "Interruptor general", false))
                menu.add(Caracteristica(6, "Tablero de distribución", false))
                menu.add(Caracteristica(7, "Apagadores y contactos", false))
                menu.add(Caracteristica(8, "Sistema de puesta a tierra", false))
                menu.add(Caracteristica(9, "Línea monofásica", false))
                menu.add(Caracteristica(10, "Línea bifásica", false))
                menu.add(Caracteristica(11, "Línea trifásica", false))
                menu.add(Caracteristica(12, "Sistema de iluminación LED", false))
                menu.add(Caracteristica(13, "Sistema de iluminación fluorescente", false))
            }
            13 -> { // Obras Complementarias
                menu.add(Caracteristica(1, "Cisterna de concreto", false))
                menu.add(Caracteristica(2, "Tinaco de asbesto", false))
                menu.add(Caracteristica(3, "Tinaco de polietileno", false))
                menu.add(Caracteristica(4, "Barda perimetral", false))
                menu.add(Caracteristica(5, "Pavimentación de concreto", false))
                menu.add(Caracteristica(6, "Pavimentación asfáltica", false))
                menu.add(Caracteristica(7, "Jardinería", false))
                menu.add(Caracteristica(8, "Sistema de riego", false))
                menu.add(Caracteristica(9, "Alumbrado exterior", false))
                menu.add(Caracteristica(10, "Portón eléctrico", false))
                menu.add(Caracteristica(11, "Cochera techada", false))
                menu.add(Caracteristica(12, "Piscina", false))
                menu.add(Caracteristica(13, "Terraza techada", false))
            }
            14 -> { // Elementos Accesorios
                menu.add(Caracteristica(1, "Espejos", false))
                menu.add(Caracteristica(2, "Muebles de baño", false))
                menu.add(Caracteristica(3, "Barandal de acero", false))
                menu.add(Caracteristica(4, "Barandal de madera", false))
                menu.add(Caracteristica(5, "Pasamanos", false))
                menu.add(Caracteristica(6, "Rejas de herrería", false))
                menu.add(Caracteristica(7, "Proyectores de iluminación", false))
                menu.add(Caracteristica(8, "Lámparas decorativas", false))
                menu.add(Caracteristica(9, "Cancelería de aluminio", false))
                menu.add(Caracteristica(10, "Cancelería de madera", false))
                menu.add(Caracteristica(11, "Mosquiteros", false))
                menu.add(Caracteristica(12, "Persianas", false))
                menu.add(Caracteristica(13, "Cortinas", false))
                menu.add(Caracteristica(14, "Sistema de seguridad (cámaras)", false))
                menu.add(Caracteristica(15, "Sistema de alarma", false))
            }
            else -> { // Para lugar entre 1 y 9, usamos el tipo (Pisos, Muros, Plafones)
                when (tipo) {
                    "Pisos" -> {
                        menu.add(Caracteristica(1, "Loseta cerámica", false))
                        menu.add(Caracteristica(2, "Loseta cerámica con zoclos", false))
                        menu.add(Caracteristica(3, "Loseta cerámica rectificada con zoclos", false))
                        menu.add(Caracteristica(4, "Loseta vinílica", false))
                        menu.add(Caracteristica(5, "Mosaico cerámico", false))
                        menu.add(Caracteristica(6, "Laminado imitación duela", false))
                        menu.add(Caracteristica(7, "Duela de cedro", false))
                        menu.add(Caracteristica(8, "Alfombra", false))
                        menu.add(Caracteristica(9, "Loseta antiderrapante", false))
                        menu.add(Caracteristica(10, "Loseta cerámica y antiderrapante en zona húmeda", false))
                        menu.add(Caracteristica(11, "Concreto pulido y antiderrapante en zona húmeda", false))
                        menu.add(Caracteristica(12, "Loseta vinílica y antiderrapante en zona húmeda", false))
                        menu.add(Caracteristica(13, "Mosaico cerámico y antiderrapante en zona húmeda", false))
                        menu.add(Caracteristica(14, "Concreto cerámico y firme de concreto en zona húmeda", false))
                        menu.add(Caracteristica(15, "Firme de concreto pulido", false))
                        menu.add(Caracteristica(16, "Firme de concreto común", false))
                        menu.add(Caracteristica(17, "Firme de concreto escobillado", false))
                        menu.add(Caracteristica(18, "Firme de concreto lavado", false))
                        menu.add(Caracteristica(19, "Huellas de concreto", false))
                        menu.add(Caracteristica(20, "Adoquín", false))
                        menu.add(Caracteristica(21, "Tierra y andadores de concreto", false))
                        menu.add(Caracteristica(22, "Tierra", false))
                        menu.add(Caracteristica(23, "Cantera", false))
                        menu.add(Caracteristica(24, "Pasto sintético", false))
                        menu.add(Caracteristica(25, "Pasto", false))
                        menu.add(Caracteristica(26, "Firme de concreto con tratamiento epóxico", false))
                        menu.add(Caracteristica(27, "Firme de concreto sin reglegar", false))
                        menu.add(Caracteristica(28, "Empedrado", false))
                        menu.add(Caracteristica(29, "Se supone firme de concreto pulido", false))
                        menu.add(Caracteristica(30, "Alta de otro", false))
                    }
                    "Muros" -> {
                        menu.add(Caracteristica(31, "Aplanado mortero cemento-arena y pintura", false))
                        menu.add(Caracteristica(32, "Aplanado fino cemento-arena y pintura", false))
                        menu.add(Caracteristica(33, "Aplanado mortero cemento arena sin pintura", false))
                        menu.add(Caracteristica(34, "Pasta texturizada", false))
                        menu.add(Caracteristica(35, "Block aparente y pintura", false))
                        menu.add(Caracteristica(36, "Azulejo", false))
                        menu.add(Caracteristica(37, "Aplanado de mezcla, pintura y azulejo en área húmeda", false))
                        menu.add(Caracteristica(38, "Aplanado fino de mezcla, pintura y azulejo en área húmeda", false))
                        menu.add(Caracteristica(39, "Aplanado mortero cemento arena sin pintura en área húmeda", false))
                        menu.add(Caracteristica(40, "Pasta texturizada y azulejo en área húmeda", false))
                        menu.add(Caracteristica(41, "Aplanado de yeso con pintura y azulejo en área húmeda", false))
                        menu.add(Caracteristica(42, "Block aparente con pintura y azulejo en área húmeda", false))
                        menu.add(Caracteristica(43, "Azulejo a 2.10m y aplanado de mezcla con pintura", false))
                        menu.add(Caracteristica(44, "Azulejo a 0.90m y aplanado de mezcla con pintura", false))
                        menu.add(Caracteristica(45, "Tirol lanzado", false))
                        menu.add(Caracteristica(46, "Tirol planchado", false))
                        menu.add(Caracteristica(47, "Ladrillo aparente", false))
                        menu.add(Caracteristica(48, "Block aparente", false))
                        menu.add(Caracteristica(49, "Barda con enjarre sin reglegar", false))
                        menu.add(Caracteristica(50, "Mortero lanzado", false))
                        menu.add(Caracteristica(51, "Acabado cerroteado", false))
                        menu.add(Caracteristica(52, "Aplanado de mezcla y recubrimientos de granito", false))
                        menu.add(Caracteristica(53, "Aplanado de mezcla y recubrimientos cerámicos", false))
                        menu.add(Caracteristica(54, "Aplanado de mezcla y recubrimientos de piedra cultivada", false))
                        menu.add(Caracteristica(55, "Aplanado de mezcla y azulejo en regular estado", false))
                        menu.add(Caracteristica(56, "Aplanado de mezcla en regular estado", false))
                        menu.add(Caracteristica(57, "Aplanado de yeso en regular estado", false))
                        menu.add(Caracteristica(58, "Aplanado de mezcla vandalizado en mal estado", false))
                        menu.add(Caracteristica(59, "Yeso vandalizado en mal estado", false))
                        menu.add(Caracteristica(60, "Block aparente vandalizado en mal estado", false))
                        menu.add(Caracteristica(61, "Azulejo vandalizado en mal estado", false))
                        menu.add(Caracteristica(62, "Lámian acanelada", false))
                        menu.add(Caracteristica(63, "Se supone aplanado de mezcla con pintura", false))
                    }
                    "Plafones" -> {
                        menu.add(Caracteristica(64, "Acabado tipo tirol", false))
                        menu.add(Caracteristica(65, "Tirol planchado", false))
                        menu.add(Caracteristica(66, "Tirol y yeso con pintura", false))
                        menu.add(Caracteristica(67, "Yeso acabado pintura", false))
                        menu.add(Caracteristica(68, "Aplanado fino de yeso y pintura", false))
                        menu.add(Caracteristica(69, "Yeso acabado esmalte", false))
                        menu.add(Caracteristica(70, "Aplanado cemento-arena y pintura", false))
                        menu.add(Caracteristica(71, "Pasta texturizada", false))
                        menu.add(Caracteristica(72, "Aplanado cemento-arena sin pintura", false))
                        menu.add(Caracteristica(73, "Aplanado fino con molduras de yeso", false))
                        menu.add(Caracteristica(74, "Adoquín y vigas de madera", false))
                        menu.add(Caracteristica(75, "Lámina y perfiles de madera", false))
                        menu.add(Caracteristica(76, "Lámina y perfiles metálicos", false))
                        menu.add(Caracteristica(77, "Plafones de poliestireno expandido con cancelería", false))
                        menu.add(Caracteristica(78, "Fibra de vidrio y perfiles metálicos", false))
                        menu.add(Caracteristica(79, "Teja y perfiles metálicos", false))
                        menu.add(Caracteristica(80, "Lona y perfiles metálicos", false))
                        menu.add(Caracteristica(81, "Mallasombra y perfiles metálicos", false))
                        menu.add(Caracteristica(82, "Insulpanes y perfiles metálicos", false))
                        menu.add(Caracteristica(83, "Pergolado", false))
                        menu.add(Caracteristica(84, "Losa aparente sin enjarre", false))
                        menu.add(Caracteristica(85, "Aplanado de mezcla en regular estado", false))
                        menu.add(Caracteristica(86, "Yeso terminado pintura en regular estado", false))
                        menu.add(Caracteristica(87, "Losa en mal estado con acero expuesto", false))
                        menu.add(Caracteristica(88, "Losa quemada en mal estado", false))
                        menu.add(Caracteristica(89, "Se supone tirol", false))
                    }
                }
            }
        }
        return menu
    }

    // Nuevo adaptador con listener para los checkboxes
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
            
            // Evita el callback infinito
            checkBox.setOnCheckedChangeListener(null)
            checkBox.isChecked = item.seleccionado
            
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.seleccionado = isChecked
                onCheckBoxClick(item.idCaracteristica, isChecked)
            }

            return vista
        }
    }

    companion object {
        // Define constant keys for arguments
        private const val ARG_LUGAR = "lugar"
        private const val ARG_TITULO = "desc"
        private const val ARG_TIPO = "tipo"

        @JvmStatic
        fun newInstance(lugar: Int, titulo: String, tipo: String) =
            CaracteristicasAvaluo().apply {
                arguments = Bundle().apply {
                    putInt(ARG_LUGAR, lugar)
                    putString(ARG_TITULO, titulo)
                    putString(ARG_TIPO, tipo)
                }
            }
    }
}