package mx.itson.moviles

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminarAvaluo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvaluoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_avaluo, parent, false)
        return AvaluoViewHolder(view)
    }    override fun onBindViewHolder(holder: AvaluoViewHolder, position: Int) {
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
        
        holder.btnEliminar.setOnClickListener {
            confirmarEliminarAvaluo(avaluo)
        }
    }
    
    private fun confirmarEliminarAvaluo(avaluo: Avaluo) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Eliminar Avalúo")
        builder.setMessage("¿Estás seguro de que quieres eliminar este avalúo?")
        
        builder.setPositiveButton("Sí") { dialog, _ ->
            verificarCitasActivas(avaluo.folio)
            dialog.dismiss()
        }
        
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        
        val dialog = builder.create()
        dialog.show()
    }
    
    private fun verificarCitasActivas(folioAvaluo: String) {
        val citasRef = FirebaseDatabase.getInstance().getReference("citas")
        
        citasRef.orderByChild("folioAvaluo").equalTo(folioAvaluo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var citaActiva = false
                    
                    for (citaSnapshot in snapshot.children) {
                        val activa = citaSnapshot.child("activo").getValue(Boolean::class.java) ?: false
                        if (activa) {
                            citaActiva = true
                            break
                        }
                    }
                    
                    if (citaActiva) {
                        mostrarErrorCitaActiva()
                    } else {
                        eliminarAvaluo(folioAvaluo)
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error al verificar citas: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    
    private fun mostrarErrorCitaActiva() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("No se puede eliminar")
        builder.setMessage("No es posible eliminar este avalúo porque tiene una cita activa asociada. " +
                "Para eliminarlo, primero cancele la cita.")
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
    
    private fun eliminarAvaluo(folioAvaluo: String) {
        val avaluosRef = FirebaseDatabase.getInstance().getReference("avaluos").child(folioAvaluo)
        
        avaluosRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(context, "Avalúo eliminado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error al eliminar el avalúo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun getItemCount(): Int = avaluos.size
}