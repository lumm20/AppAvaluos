import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itson.moviles.CitaRegistradaFragment
import mx.itson.moviles.R
import mx.itson.moviles.modelo.Cita
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AgendarCitasFragment : Fragment() {

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var avaluoSpinner: Spinner
    private lateinit var correoEt: EditText
    private lateinit var telefonoEt: EditText
    private lateinit var fechaEt: EditText
    private lateinit var horaEt: EditText
    private lateinit var siguienteBtn: Button

    private var fechaSeleccionada: Date? = null
    private var horaSeleccionada: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_agendar_citas, container, false)
        avaluoSpinner = view.findViewById(R.id.spinner_avaluos)
        correoEt = view.findViewById(R.id.correo_et)
        telefonoEt = view.findViewById(R.id.telefono_et)
        fechaEt = view.findViewById(R.id.fecha_et)
        horaEt = view.findViewById(R.id.hora_et)
        siguienteBtn = view.findViewById(R.id.siguiente_btn)

        cargarFoliosAvaluosFirebase()

        fechaEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mostrarDatePickerDialog()
            }
        }
        fechaEt.setOnClickListener {
            mostrarDatePickerDialog()
        }

        horaEt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                mostrarTimePickerDialog()
            }
        }
        horaEt.setOnClickListener {
            mostrarTimePickerDialog()
        }

        siguienteBtn.setOnClickListener {
            guardarCitaFirebase()
        }

        return view
    }

    private fun mostrarDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, yearSeleccionado, monthSeleccionado, dayOfMonthSeleccionado ->
                calendar.set(yearSeleccionado, monthSeleccionado, dayOfMonthSeleccionado)
                fechaSeleccionada = calendar.time
                val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                fechaEt.setText(formatoFecha.format(fechaSeleccionada!!))
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

    private fun mostrarTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val horaFormateada = String.format("%02d:%02d", hourOfDay, minute)
                horaSeleccionada = horaFormateada
                horaEt.setText(horaFormateada)
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    private fun cargarFoliosAvaluosFirebase() {
        val avaluosRef = db.getReference("avaluos")

        avaluosRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val folios = mutableListOf<String>()
               for (avaluoSnapshot in snapshot.children) {
                    val folio = avaluoSnapshot.child("folio").getValue(String::class.java)
                    folio?.let { folios.add(it) }

                    }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, folios)
                avaluoSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar avalúos: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error al cargar avalúos", error.toException())
            }
        })
    }

    private fun guardarCitaFirebase() {
        val correo = correoEt.text.toString()
        val telefono = telefonoEt.text.toString()
        val folioAvaluoSeleccionado = avaluoSpinner.selectedItem.toString()
        val usuarioIdActual = auth.currentUser?.uid

        if (fechaSeleccionada == null) {
            Toast.makeText(requireContext(), "Por favor, selecciona una fecha", Toast.LENGTH_SHORT).show()
            return
        }

        if (horaSeleccionada == null) {
            Toast.makeText(requireContext(), "Por favor, selecciona una hora", Toast.LENGTH_SHORT).show()
            return
        }

        if (usuarioIdActual == null) {
            Toast.makeText(requireContext(), "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance()
        calendar.time = fechaSeleccionada!!
        val (hour, minute) = horaSeleccionada!!.split(":").map { it.toInt() }
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        val fechaVisitaTimestamp = calendar.time.time

        val nuevoFolioCita = generarFolioCita()

        val nuevaCita = Cita(
            fechaRegistro = System.currentTimeMillis(),
            fechaVisita = fechaVisitaTimestamp,
            telefonoContacto = telefono,
            correoContacto = correo,
            folioAvaluo = folioAvaluoSeleccionado,
            empresa = null,
            usuarioId = usuarioIdActual.toString(),
            folioCita = nuevoFolioCita
        )

        val citasRef = db.getReference("citas")
        citasRef.push().setValue(nuevaCita)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cita agendada con éxito", Toast.LENGTH_SHORT).show()
                val nuevoFolioCita = generarFolioCita()
                navegarACitaRegistrada(nuevoFolioCita)
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al agendar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error al agendar la cita", e)
            }

        correoEt.text.clear()
        telefonoEt.text.clear()
        fechaEt.text.clear()
        horaEt.text.clear()
        fechaSeleccionada = null
        horaSeleccionada = null
    }

    private fun generarFolioCita(): String {
        val fechaHora = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val random = UUID.randomUUID().toString().substring(0, 8)
        return "$fechaHora-$random"
    }

    private fun navegarACitaRegistrada(folioCita: String) {
        val citaRegistradaFragment = CitaRegistradaFragment()
        val bundle = Bundle()
        bundle.putString("folioCita", folioCita)
        citaRegistradaFragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, citaRegistradaFragment)
            .addToBackStack(null)
            .commit()
    }
}