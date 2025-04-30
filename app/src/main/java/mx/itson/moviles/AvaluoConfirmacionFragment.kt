package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class AvaluoConfirmacionFragment : Fragment() {
    private var avaluo: Avaluo? = null

    companion object {
        private const val ARG_AVALUO = "avaluo"

        @JvmStatic
        fun newInstance(avaluo: Avaluo) = AvaluoConfirmacionFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_AVALUO, avaluo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            avaluo = it.getSerializable(ARG_AVALUO) as? Avaluo
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_avaluo_confirmacion, container, false)

        // Configurar vistas
        val txtFolio: TextView = view.findViewById(R.id.txtFolioResumen)
        val txtFecha: TextView = view.findViewById(R.id.txtFechaResumen)
        val containerInmueble: LinearLayout = view.findViewById(R.id.containerCaracteristicasInmueble)
        val containerEntorno: LinearLayout = view.findViewById(R.id.containerCaracteristicasEntorno)
        val btnVolver: Button = view.findViewById(R.id.btnVolver)

        // Mostrar información del avalúo
        avaluo?.let { av ->
            txtFolio.text = "Folio: ${av.folio}"
            
            // Formatear fecha
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            txtFecha.text = "Fecha: ${dateFormat.format(av.fecha_registro)}"

            // Mostrar características del inmueble
            av.caracteristicas_inmueble.forEach { caracteristica ->
                val itemView = inflater.inflate(R.layout.item_caracteristica_resumen, containerInmueble, false)
                val txtTipo: TextView = itemView.findViewById(R.id.txtTipo)
                val txtDetalles: TextView = itemView.findViewById(R.id.txtDetalles)
                
                txtTipo.text = caracteristica.tipo
                txtDetalles.text = "Ubicación: ${caracteristica.ubicacion}, Uso: ${caracteristica.uso}"
                
                containerInmueble.addView(itemView)
            }
            
            // Mostrar características del entorno
            av.caracteristicas_entorno.forEach { caracteristica ->
                val itemView = inflater.inflate(R.layout.item_caracteristica_resumen, containerEntorno, false)
                val txtTipo: TextView = itemView.findViewById(R.id.txtTipo)
                val txtDetalles: TextView = itemView.findViewById(R.id.txtDetalles)
                
                txtTipo.text = caracteristica.cercania
                txtDetalles.text = "Tipo de zona: ${caracteristica.tipo_zona}, Uso: ${caracteristica.uso}"
                
                containerEntorno.addView(itemView)
            }
        }

        // Configurar botón volver
        btnVolver.setOnClickListener {
            // Volver a la pantalla principal
            activity?.finish()
        }

        return view
    }
}

/**
 * Adaptador para mostrar las características del inmueble
 */
class CaracteristicaInmuebleAdapter(
    private val context: android.content.Context,
    private val caracteristicas: List<CaracteristicasInmueble>
) : RecyclerView.Adapter<CaracteristicaInmuebleAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTipo: TextView = view.findViewById(R.id.txtTipo)
        val txtDetalles: TextView = view.findViewById(R.id.txtDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_caracteristica_resumen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val caracteristica = caracteristicas[position]
        holder.txtTipo.text = caracteristica.tipo
        holder.txtDetalles.text = "Ubicación: ${caracteristica.ubicacion}, Uso: ${caracteristica.uso}"
    }

    override fun getItemCount() = caracteristicas.size
}

/**
 * Adaptador para mostrar las características del entorno
 */
class CaracteristicaEntornoAdapter(
    private val context: android.content.Context,
    private val caracteristicas: List<CaracteristicasEntorno>
) : RecyclerView.Adapter<CaracteristicaEntornoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTipo: TextView = view.findViewById(R.id.txtTipo)
        val txtDetalles: TextView = view.findViewById(R.id.txtDetalles)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_caracteristica_resumen, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val caracteristica = caracteristicas[position]
        holder.txtTipo.text = caracteristica.cercania
        holder.txtDetalles.text = "Tipo de zona: ${caracteristica.tipo_zona}, Uso: ${caracteristica.uso}"
    }

    override fun getItemCount() = caracteristicas.size
}