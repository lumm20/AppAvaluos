import android.app.AlertDialog
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
import mx.itson.moviles.EmpresasFragment
import mx.itson.moviles.HomeFragment
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

    private val db = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var avaluoSpinner: Spinner
    private lateinit var correoEt: EditText
    private lateinit var telefonoEt: EditText
    private lateinit var fechaEt: EditText
    private lateinit var horaEt: EditText
    private lateinit var direccionTv: TextView
    private lateinit var empresaEt: TextView
    private lateinit var siguienteBtn: Button

    private var fechaSeleccionada: Date? = null
    private var horaSeleccionada: String? = null
    private var empresa: String? = null
    private var listaFolios: List<String> = emptyList()


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
        empresaEt = view.findViewById(R.id.empresa_et)

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

        cargarCorreoYTelefono()
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

        empresaEt.setText(empresa)
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empresa = it.getString(ARG_EMPRESA)
        }
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
    }    private fun cargarFoliosAvaluosFirebase() {
        val avaluosRef = db.getReference("avaluos")
        val currentUser = auth.currentUser
        
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Debe iniciar sesión para ver sus avalúos", Toast.LENGTH_SHORT).show()
            return
        }

        avaluosRef.orderByChild("usuarioId").equalTo(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
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

                listaFolios = folios
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

    private fun cargarCorreoYTelefono() {
        val usuario = auth.currentUser
        if (usuario != null) {
            val uid = usuario.uid
            val userRef = db.getReference("users").child(uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val correo = snapshot.child("correo").getValue(String::class.java)
                    val telefono = snapshot.child("telefono").getValue(String::class.java)

                    correoEt.setText(correo)
                    telefonoEt.setText(telefono)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AgendarCitas", "Error al leer datos del usuario desde la DB: ${error.message}")
                    Toast.makeText(requireContext(), "Error al cargar información", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Log.d("AgendarCitas", "No hay usuario autenticado.")
        }
    }    private fun guardarCitaFirebase() {
        val correo = correoEt.text.toString()
        val telefono = telefonoEt.text.toString()
        val folioAvaluoSeleccionado = avaluoSpinner.selectedItem.toString()
        val usuarioIdActual = auth.currentUser?.uid

        if (listaFolios.isEmpty()) {
            Toast.makeText(requireContext(), "No hay avalúos disponibles para agendar una cita.", Toast.LENGTH_SHORT).show()
            return
        }

        if (correo.isEmpty()) {
            correoEt.error = "Por favor, ingresa tu correo electrónico"
            return
        }

        if (telefono.isEmpty()) {
            telefonoEt.error = "Por favor, ingresa tu número de teléfono"
            return
        }

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
        
        // Verificar si ya existe una cita activa con el mismo avalúo para la misma empresa
        verificarCitaExistente(folioAvaluoSeleccionado, empresa ?: "")
    }

    private fun generarFolioCita(): String {
        val fechaHora = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val random = UUID.randomUUID().toString().substring(0, 8)
        return "$fechaHora-$random"
    }    private fun navegarACitaRegistrada(folioCita: String) {
        try {
            Log.d("AgendarCitas", "Navegando a CitaRegistradaFragment con folio: $folioCita")
            
            // Crear un nuevo fragmento de confirmación y pasar el folio de la cita
            val citaRegistradaFragment = CitaRegistradaFragment()
            val bundle = Bundle()
            bundle.putString("folioCita", folioCita)
            citaRegistradaFragment.arguments = bundle

            // Usar el fragmentManager con try-catch para mayor seguridad
            try {
                val fragmentManager = requireActivity().supportFragmentManager
                fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, citaRegistradaFragment)
                    .commit()
            } catch (e: Exception) {
                Log.e("AgendarCitas", "Error al reemplazar fragmento", e)
                throw e // Relanzar para capturar en el try-catch exterior
            }
        } catch (e: Exception) {
            Log.e("AgendarCitas", "Error navegando a CitaRegistradaFragment", e)
            // Intentar mostrar un mensaje al usuario
            try {
                Toast.makeText(requireContext(), 
                    "Cita registrada con éxito. Folio: $folioCita", 
                    Toast.LENGTH_LONG).show()
            } catch (e2: Exception) {
                Log.e("AgendarCitas", "Error al mostrar Toast de recuperación", e2)
            }
        }
    }

    // Función para verificar si ya existe una cita activa con el mismo avalúo para la misma empresa
    private fun verificarCitaExistente(folioAvaluo: String, empresaSeleccionada: String) {
        val citasRef = db.getReference("citas")
        
        citasRef.orderByChild("folioAvaluo").equalTo(folioAvaluo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var citaExistente = false
                    
                    for (citaSnapshot in snapshot.children) {
                        val activa = citaSnapshot.child("activo").getValue(Boolean::class.java) ?: false
                        val empresaCita = citaSnapshot.child("empresa").getValue(String::class.java) ?: ""
                        
                        if (activa && empresaCita == empresaSeleccionada) {
                            citaExistente = true
                            break
                        }
                    }
                    
                    if (citaExistente) {
                        mostrarAdvertenciaCitaExistente()
                    } else {
                        procesarRegistroCita()
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error al verificar citas existentes: ${error.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AgendarCitas", "Error al verificar citas: ${error.message}")
                }
            })
    }
    
    private fun mostrarAdvertenciaCitaExistente() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Cita ya programada")
        builder.setMessage("Ya existe una cita activa para este avalúo con la empresa '$empresa'. No es posible agendar múltiples citas para el mismo avalúo con la misma empresa.")
        builder.setPositiveButton("Entendido") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }
      private fun procesarRegistroCita() {
        try {
            val correo = correoEt.text.toString()
            val telefono = telefonoEt.text.toString()
            val folioAvaluoSeleccionado = avaluoSpinner.selectedItem?.toString() ?: ""
            val usuarioIdActual = auth.currentUser?.uid ?: ""
            
            if (folioAvaluoSeleccionado.isEmpty()) {
                Toast.makeText(requireContext(), "Error: No hay avalúo seleccionado", Toast.LENGTH_SHORT).show()
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
                id = nuevoFolioCita,  // Usar el mismo valor para id y folioCita
                fechaRegistro = System.currentTimeMillis(),
                fechaVisita = fechaVisitaTimestamp,
                telefonoContacto = telefono,
                correoContacto = correo,
                folioAvaluo = folioAvaluoSeleccionado,
                empresa = empresa,
                usuarioId = usuarioIdActual,
                folioCita = nuevoFolioCita,
                activo = true
            )

            val citasRef = db.getReference("citas")
            citasRef.child(nuevoFolioCita).setValue(nuevaCita)
                .addOnSuccessListener {
                    try {
                        Toast.makeText(requireContext(), "Cita agendada con éxito", Toast.LENGTH_SHORT).show()
                        
                        // Limpiar los campos después de éxito
                        correoEt.text.clear()
                        telefonoEt.text.clear()
                        fechaEt.text.clear()
                        horaEt.text.clear()
                        fechaSeleccionada = null
                        horaSeleccionada = null
                        
                        navegarACitaRegistrada(nuevoFolioCita)
                    } catch (e: Exception) {
                        Log.e("AgendarCitas", "Error después de agendar cita con éxito", e)
                        // Intentar navegar al Home en caso de error
                        try {
                            val homeFragment = HomeFragment()
                            requireActivity().supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, homeFragment)
                                .commit()
                        } catch (e2: Exception) {
                            Log.e("AgendarCitas", "Error en navegación de recuperación", e2)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al agendar la cita: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("Firebase", "Error al agendar la cita", e)
                }
        } catch (e: Exception) {
            Log.e("AgendarCitas", "Error en procesarRegistroCita", e)
            Toast.makeText(requireContext(), "Error inesperado: ${e.message}", Toast.LENGTH_SHORT).show()
        }
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