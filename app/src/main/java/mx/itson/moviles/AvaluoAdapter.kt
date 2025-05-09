package mx.itson.moviles

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import mx.itson.moviles.modelo.Avaluo
import java.text.SimpleDateFormat
import java.util.Locale

class AvaluoAdapter(
    private val context: Context,
    private val avaluos: List<Avaluo>,
    private val onItemClick: (Avaluo) -> Unit
) : RecyclerView.Adapter<AvaluoAdapter.AvaluoViewHolder>() {

    class AvaluoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val txtFolio: TextView = itemView.findViewById(R.id.txtFolio)
        val txtFecha: TextView = itemView.findViewById(R.id.txtFecha)
        val txtCantidadInmueble: TextView = itemView.findViewById(R.id.txtCantidadInmueble)
        val txtCantidadEntorno: TextView = itemView.findViewById(R.id.txtCantidadEntorno)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaluoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_avaluo, parent, false)
        return AvaluoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AvaluoViewHolder, position: Int) {
        val avaluo = avaluos[position]
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaFormateada = dateFormat.format(avaluo.fechaRegistro)
        
        holder.txtFolio.text = "Folio: ${avaluo.folio}"
        holder.txtFecha.text = "Fecha: $fechaFormateada"
        
        val cantidadInmueble = avaluo.caracteristicasInmueble.size
        val cantidadEntorno = avaluo.caracteristicasEntorno.size
        
        holder.txtCantidadInmueble.text = "$cantidadInmueble características del inmueble"
        holder.txtCantidadEntorno.text = "$cantidadEntorno características del entorno"

        holder.cardView.setOnClickListener {
            onItemClick(avaluo)
        }
    }

    override fun getItemCount(): Int = avaluos.size
}