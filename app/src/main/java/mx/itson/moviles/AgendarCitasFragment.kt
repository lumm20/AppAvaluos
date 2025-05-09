import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
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
import mx.itson.moviles.modelo.Direccion
import java.util.UUID
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ARG_EMPRESA = "empresa"
class AgendarCitasFragment : Fragment() {
    private var empresa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empresa = it.getString(ARG_EMPRESA)
        }
    }

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var avaluoSpinner: Spinner
    private lateinit var correoEt: EditText
    private lateinit var telefonoEt: EditText
    private lateinit var fechaEt: EditText
    private lateinit var horaEt: EditText
    private lateinit var direccionTv: TextView
    private lateinit var siguienteBtn: Button

    private var fechaSeleccionada: Date? = null
    private var horaSeleccionada: String? = null

    // Mapa para almacenar folios y sus direcciones correspondientes
    private val foliosDirecciones = mutableMapOf<String, String>()

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
        direccionTv = view.findViewById(R.id.dirección_et)  // Asegúrate de que el ID sea correcto
        siguienteBtn = view.findViewById(R.id.siguiente_btn)

        // Configurar el listener para el spinner
        avaluoSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val folioSeleccionado = parent?.getItemAtPosition(position).toString()
                // Actualizar el campo de dirección con la dirección correspondiente al folio
                actualizarDireccion(folioSeleccionado)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
        val btnSiguiente: Button = view.findViewById(R.id.siguiente_btn)
        val etEmpresa: EditText= view.findViewById(R.id.nombre_et)
        etEmpresa.setText(empresa)
        val fragment = CitaRegistradaFragment()

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
                foliosDirecciones.clear() // Limpiar el mapa antes de agregar nuevos valores

                for (avaluoSnapshot in snapshot.children) {
                    val folio = avaluoSnapshot.child("folio").getValue(String::class.java)

                    // Obtener la dirección del avalúo
                    val direccion = obtenerDireccionDesdeSnapshot(avaluoSnapshot)

                    if (folio != null && direccion != null) {
                        folios.add(folio)
                        foliosDirecciones[folio] = direccion
                        Log.d("AgendarCitas", "Folio: $folio, Dirección: $direccion")
                    }
                }

                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, folios)
                avaluoSpinner.adapter = adapter

                // Si hay elementos, actualizar la dirección con el primer elemento
                if (folios.isNotEmpty()) {
                    actualizarDireccion(folios[0])
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error al cargar avalúos: ${error.message}", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error al cargar avalúos", error.toException())
            }
        })
    }

    // Función para extraer la dirección del snapshot de avalúo adaptada a tu modelo de datos
    private fun obtenerDireccionDesdeSnapshot(snapshot: DataSnapshot): String? {
        // Intentar obtener el objeto Direccion completo
        val direccionSnapshot = snapshot.child("direccion")
        if (direccionSnapshot.exists()) {
            try {
                // Intenta convertir a objeto Direccion (si la estructura coincide)
                val direccion = direccionSnapshot.getValue(Direccion::class.java)
                if (direccion != null) {
                    // Formatea la dirección usando los campos del modelo Direccion
                    return formatearDireccion(direccion)
                } else {
                    // Si no se pudo convertir a objeto, intenta leer campo por campo
                    val calle = direccionSnapshot.child("calle").getValue(String::class.java) ?: ""
                    val numeroExterior = direccionSnapshot.child("numeroExterior").getValue(String::class.java) ?: ""
                    val numeroInterior = direccionSnapshot.child("numeroInterior").getValue(String::class.java) ?: ""
                    val ciudad = direccionSnapshot.child("ciudad").getValue(String::class.java) ?: ""
                    val codigoPostal = direccionSnapshot.child("codigoPostal").getValue(String::class.java) ?: ""

                    return formatearDireccionManual(calle, numeroExterior, numeroInterior, ciudad, codigoPostal)
                }
            } catch (e: Exception) {
                Log.e("AgendarCitas", "Error al convertir dirección", e)
            }
        }


        // Si no se encuentra en ningún lado, devolver un valor por defecto
        return "No disponible"
    }

    // Formatea una dirección a partir del objeto Direccion
    private fun formatearDireccion(direccion: Direccion): String {
        val interior = if (direccion.numeroInterior.isNotEmpty()) {
            " Int. ${direccion.numeroInterior}"
        } else {
            ""
        }

        return "${direccion.calle} ${direccion.numeroExterior}$interior, ${direccion.ciudad}, CP ${direccion.codigoPostal}"
    }

    // Formatea una dirección a partir de campos individuales
    private fun formatearDireccionManual(
        calle: String,
        numeroExterior: String,
        numeroInterior: String,
        ciudad: String,
        codigoPostal: String
    ): String {
        val interior = if (numeroInterior.isNotEmpty()) {
            " Int. $numeroInterior"
        } else {
            ""
        }

        return "$calle $numeroExterior$interior, $ciudad, CP $codigoPostal"
    }

    // Función para actualizar el campo de dirección con la dirección correspondiente al folio seleccionado
    private fun actualizarDireccion(folioSeleccionado: String) {
        val direccion = foliosDirecciones[folioSeleccionado] ?: "No disponible"
        direccionTv.text = direccion
        Log.d("AgendarCitas", "Actualizando dirección para folio $folioSeleccionado: $direccion")
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
            id = null,  // Firebase generará el ID
            fechaRegistro = System.currentTimeMillis(),
            fechaVisita = fechaVisitaTimestamp,
            telefonoContacto = telefono,
            correoContacto = correo,
            folioAvaluo = folioAvaluoSeleccionado,
            empresa = null,
            usuarioId = usuarioIdActual,
            folioCita = nuevoFolioCita
        )

        val citasRef = db.getReference("citas")
        citasRef.child(nuevoFolioCita).setValue(nuevaCita)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Cita agendada con éxito", Toast.LENGTH_SHORT).show()
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param empresa Parameter 1.
         * @return A new instance of fragment EmpresasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(empresa: String) =
            AgendarCitasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMPRESA, empresa)
                }
            }
    }

}