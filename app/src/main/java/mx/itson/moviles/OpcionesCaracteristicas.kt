package mx.itson.moviles

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Fragment para mostrar opciones de características de avalúos.
 */
class OpcionesCaracteristicas : Fragment() {
    private var opcion: String? = null
    private var folio: String? = null
    private var esTipoInstalacion: Boolean = false
    private lateinit var recyclerView: RecyclerView
    
    private val opciones = mutableListOf<OpcionCaracteristica>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            opcion = it.getString(ARG_OPCION)
            folio = it.getString(ARG_FOLIO)
            esTipoInstalacion = it.getBoolean(ARG_ES_TIPO_INSTALACION, false)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutId = if (esTipoInstalacion) 
            R.layout.fragment_opciones_caracteristicas2 
        else 
            R.layout.fragment_opciones_caracteristicas
            
        val view = inflater.inflate(layoutId, container, false)

        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val txtFolio: TextView = view.findViewById(R.id.txtFolio)
        val btnBack: ImageButton = view.findViewById(R.id.btn_back)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (esTipoInstalacion) {
            txtTitulo.setText(R.string.caracteristicas_entorno)
        } else {
            txtTitulo.setText(R.string.caracteristicas_inmueble)
        }
        txtFolio.text = getString(R.string.folio_placeholder, folio)

        inicializarOpciones()

        recyclerView = view.findViewById(R.id.recyclerCaracteristicas)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        recyclerView.adapter = OpcionAdapter(requireContext(), opciones) { opcion ->
            navegarACaracteristicas(opcion)
        }

        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)
        btnAceptar.setOnClickListener {
            guardarCaracteristicasAvaluo()
            Toast.makeText(context, getString(R.string.guardado_exito), Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun guardarCaracteristicasAvaluo() {
        if (esTipoInstalacion) {
        } else {
        }
    }

    private fun inicializarOpciones() {
        opciones.clear()
        
        if (!esTipoInstalacion) {
            // Opciones para inmueble
            opciones.add(OpcionCaracteristica(1, getString(R.string.sala), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(2, getString(R.string.comedor), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(3, getString(R.string.cocina), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(4, getString(R.string.bano), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(5, getString(R.string.recamara), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(6, getString(R.string.estancia), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(7, getString(R.string.patio_posterior), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(8, getString(R.string.estacionamiento), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(9, getString(R.string.terraza), R.drawable.ic_home))
        } else {
            // Opciones para entorno
            opciones.add(OpcionCaracteristica(10, getString(R.string.instalaciones_hidraulicas), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(11, getString(R.string.instalaciones_sanitarias), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(12, getString(R.string.instalaciones_electricas), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(13, getString(R.string.obras_complementarias), R.drawable.ic_home))
            opciones.add(OpcionCaracteristica(14, getString(R.string.elementos_accesorios), R.drawable.ic_home))
        }

        actualizarEstadoOpciones()
    }
    
    private fun actualizarEstadoOpciones() {
        for (opcion in opciones) {
            opcion.tieneSelecciones = hayCaracteristicasSeleccionadas(opcion.id)
        }
    }
    
    private fun hayCaracteristicasSeleccionadas(lugarId: Int): Boolean {
        if (folio.isNullOrEmpty()) return false
        
        val prefijo = if (esTipoInstalacion) "entorno_" else "inmueble_"
        
        val mapaFolio = CaracteristicasAvaluo.caracteristicasGuardadasPorFolio[folio] ?: return false
        
        for (clave in mapaFolio.keys) {
            if (clave.startsWith("$prefijo$lugarId")) {
                val selecciones = mapaFolio[clave]
                if (selecciones != null && selecciones.isNotEmpty()) {
                    return true
                }
            }
        }
        return false
    }

    private fun navegarACaracteristicas(opcion: OpcionCaracteristica) {
        val folioSeguro = folio ?: ""
        
        val fragment = CaracteristicasAvaluo.newInstance(opcion.id, opcion.nombre, "Pisos", folioSeguro)

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val ARG_OPCION = "opcion"
        private const val ARG_FOLIO = "folio"
        private const val ARG_ES_TIPO_INSTALACION = "esTipoInstalacion"

        @JvmStatic
        fun newInstance(opcion: String, folio: String, esTipoInstalacion: Boolean = false) =
            OpcionesCaracteristicas().apply {
                arguments = Bundle().apply {
                    putString(ARG_OPCION, opcion)
                    putString(ARG_FOLIO, folio)
                    putBoolean(ARG_ES_TIPO_INSTALACION, esTipoInstalacion)
                }
            }
    }
}

/**
 * Modelo para las opciones de características
 */
data class OpcionCaracteristica(
    val id: Int,
    val nombre: String,
    val iconoResId: Int,
    var tieneSelecciones: Boolean = false
)

/**
 * Adaptador para el RecyclerView de opciones de características
 */
class OpcionAdapter(
    private val context: Context,
    private val opciones: List<OpcionCaracteristica>,
    private val onItemClick: (OpcionCaracteristica) -> Unit
) : RecyclerView.Adapter<OpcionAdapter.OpcionViewHolder>() {

    class OpcionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icono: ImageView = view.findViewById(R.id.imgCaracteristica)
        val nombre: TextView = view.findViewById(R.id.txvCarac)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpcionViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_opcion_caracteristica, parent, false)
        return OpcionViewHolder(view)
    }

    override fun onBindViewHolder(holder: OpcionViewHolder, position: Int) {
        val opcion = opciones[position]
        
        holder.nombre.text = opcion.nombre
        holder.icono.setImageResource(opcion.iconoResId)
        
        if (opcion.tieneSelecciones) {
            holder.nombre.setTextColor(context.getColor(R.color.primary))
            holder.nombre.text = "${opcion.nombre} ✓"
        } else {
            holder.nombre.setTextColor(context.getColor(R.color.text_primary))
        }
        
        holder.itemView.setOnClickListener {
            onItemClick(opcion)
        }
    }

    override fun getItemCount() = opciones.size
}