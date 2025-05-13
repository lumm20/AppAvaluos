package mx.itson.moviles

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import mx.itson.moviles.modelo.Cita
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CitaAdapter(
    private val context: Context,
    private val citas: MutableList<Cita>
) : RecyclerView.Adapter<CitaAdapter.CitaViewHolder>() {

    init {
        Log.d("CitaAdapter", "Inicializado con ${citas.size} citas")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitaViewHolder {
        Log.d("CitaAdapter", "onCreateViewHolder llamado")
        val view = LayoutInflater.from(context).inflate(R.layout.item_cita, parent, false)
        return CitaViewHolder(view)
    }

    override fun onBindViewHolder(holder: CitaViewHolder, position: Int) {
        val cita = citas[position]
        Log.d("CitaAdapter", "Binding cita en posición $position: ${cita.folioCita}")

        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())

        try {
            val fechaRegistroDate = Date(cita.fechaRegistro)
            val fechaVisitaDate = Date(cita.fechaVisita)

            val fechaRForm = dateFormat.format(fechaRegistroDate)
            val fechaVForm = dateFormat.format(fechaVisitaDate)

            Log.d("CitaAdapter", "Fecha Registro: $fechaRForm, Fecha Visita: $fechaVForm")

            holder.txtFolioC.text = "Folio cita: ${cita.folioCita}"
            holder.txtFechaR.text = "Fecha Registro: $fechaRForm"
            holder.txtFechaV.text = "Fecha Cita: $fechaVForm"
            holder.txtEmpresa.text = "Empresa: ${cita.empresa}"

            holder.txtFolioC.visibility = View.VISIBLE
            holder.txtFechaR.visibility = View.VISIBLE
            holder.txtFechaV.visibility = View.VISIBLE
            holder.txtEmpresa.visibility = View.VISIBLE

        } catch (e: Exception) {
            Log.e("CitaAdapter", "Error al formatear fechas", e)

            // En caso de error, mostrar datos en crudo
            holder.txtFolioC.text = cita.folioCita
            holder.txtFechaR.text = "Fecha Registro: ${cita.fechaRegistro}"
            holder.txtFechaV.text = "Fecha Visita: ${cita.fechaVisita}"
            holder.txtEmpresa.text = "Empresa: ${cita.empresa}"
        }

        holder.btnCancelarCita.setOnClickListener {
            try {
                val adapterPosition = holder.adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val folioToDelete = cita.folioCita
                    val citasRef = FirebaseDatabase.getInstance().getReference("citas")

                    AlertDialog.Builder(context)
                        .setTitle("Cancelar Cita")
                        .setMessage("¿Estás seguro de que deseas cancelar esta cita?")
                        .setPositiveButton("Sí") { dialog, _ ->
                            dialog.dismiss()

                            citasRef.child(folioToDelete).child("activo").setValue(false)
                                .addOnSuccessListener {
                                    try {
                                        if (adapterPosition < citas.size) {
                                            citas.removeAt(adapterPosition)
                                            notifyItemRemoved(adapterPosition)
                                            notifyItemRangeChanged(adapterPosition, citas.size)
                                        }
                                        Toast.makeText(context, "Cita con folio $folioToDelete cancelada", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e("CitaAdapter", "Error al actualizar UI después de cancelar: ${e.message}")
                                    }
                                }
                                .addOnFailureListener { error ->
                                    Toast.makeText(context, "Error al cancelar la cita: ${error.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("CitaAdapter", "Error al cancelar cita: ${error.message}")
                                }
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .show()
                } else {
                    Log.e("CitaAdapter", "Posición no válida para cancelar: $adapterPosition")
                }
            } catch (e: Exception) {
                Log.e("CitaAdapter", "Error al intentar cancelar cita", e)
                Toast.makeText(context, "Error al cancelar cita: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d("CitaAdapter", "getItemCount llamado, retornando: ${citas.size}")
        return citas.size
    }

    class CitaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtFolioC: TextView = itemView.findViewById(R.id.txtFolioCita)
        val txtFechaR: TextView = itemView.findViewById(R.id.txtFechaRegistro)
        val txtFechaV: TextView = itemView.findViewById(R.id.txtFechaVisita)
        val txtEmpresa: TextView = itemView.findViewById(R.id.txtEmpresa)
        val btnCancelarCita: Button = itemView.findViewById(R.id.btnCancelarCita)
    }
}
