package mx.itson.moviles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itson.moviles.modelo.Usuario

class PerfilFragment : Fragment() {
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var tvNombre: TextView
    private lateinit var tvPasswordError: TextView
    private lateinit var passwordSection: LinearLayout
    private lateinit var btnShowPasswordFields: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var nombre = ""
    private var apellidoPaterno = ""
    private var apellidoMaterno = ""
    private var passwordSectionVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val profileIcon = view.findViewById<ImageView>(R.id.profileIcon)
        tvNombre = view.findViewById(R.id.tvNombre)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etOldPassword = view.findViewById(R.id.etOldPassword)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        etConfirmNewPassword = view.findViewById(R.id.etConfirmNewPassword)
        tvPasswordError = view.findViewById(R.id.tvPasswordError)
        passwordSection = view.findViewById(R.id.passwordSection)
        btnShowPasswordFields = view.findViewById(R.id.btnShowPasswordFields)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        passwordSection.visibility = View.GONE
        

        btnShowPasswordFields.setOnClickListener {
            passwordSectionVisible = !passwordSectionVisible
            passwordSection.visibility = if (passwordSectionVisible) View.VISIBLE else View.GONE
            btnShowPasswordFields.text = if (passwordSectionVisible) "Cancelar cambio de contraseña" else "Cambiar contraseña"
            
            if (!passwordSectionVisible) {
                etOldPassword.setText("")
                etNewPassword.setText("")
                etConfirmNewPassword.setText("")
                tvPasswordError.visibility = View.GONE
            }
        }
        loadUserData()

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
            saveUserData()
        }

        configureEditTextFocusListeners()

        return view
    }
    
    private fun configureEditTextFocusListeners() {
        val editTexts = arrayOf(etEmail, etPhone, etOldPassword, etNewPassword, etConfirmNewPassword)
        
        for (editText in editTexts) {
            editText.setOnEditorActionListener { _, _, _ ->
                editText.clearFocus()
                true
            }
        }
    }

    private fun loadUserData() {
        val user = auth.currentUser
        user?.let {
            val userId = user.uid
            val userRef = database.getReference("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    nombre = dataSnapshot.child("nombre").getValue(String::class.java) ?: ""
                    apellidoPaterno = dataSnapshot.child("apellidoPaterno").getValue(String::class.java) ?: ""
                    apellidoMaterno = dataSnapshot.child("apellidoMaterno").getValue(String::class.java) ?: ""
                    val email = dataSnapshot.child("correo").getValue(String::class.java) ?: ""
                    val telefono = dataSnapshot.child("telefono").getValue(String::class.java) ?: ""

                    tvNombre.text = "$nombre $apellidoPaterno $apellidoMaterno"
                    etEmail.setText(email)
                    etPhone.setText(telefono)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(context, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun saveUserData() {
        val user = auth.currentUser
        user?.let {
            val userId = user.uid
            val userRef = database.getReference("users").child(userId)

            val email = etEmail.text.toString().trim()
            val telefono = etPhone.text.toString().trim()
            
            // Validar campos básicos
            if (email.isEmpty() || telefono.isEmpty()) {
                Toast.makeText(context, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Validar formato de correo electrónico
            if (!isValidEmail(email)) {
                Toast.makeText(context, "Por favor ingresa un correo electrónico válido", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Validar formato de teléfono (10 dígitos)
            if (!isValidPhone(telefono)) {
                Toast.makeText(context, "El teléfono debe tener 10 dígitos", Toast.LENGTH_SHORT).show()
                return
            }
            
            // Verificar si el correo y teléfono ya existen 
            checkEmailAndPhoneExistence(email, telefono, userId) { emailExists, phoneExists -> 
                if (emailExists) {
                    Toast.makeText(context, "Este correo electrónico ya está en uso por otro usuario", Toast.LENGTH_SHORT).show()
                    return@checkEmailAndPhoneExistence
                }
                
                if (phoneExists) {
                    Toast.makeText(context, "Este número de teléfono ya está en uso por otro usuario", Toast.LENGTH_SHORT).show()
                    return@checkEmailAndPhoneExistence
                }
                
                // Continuar si todo bien
                continueWithUserUpdate(user, userRef, email, telefono)
            }
        }
    }
    
    private fun continueWithUserUpdate(user: com.google.firebase.auth.FirebaseUser, userRef: com.google.firebase.database.DatabaseReference, email: String, telefono: String) {
        if (passwordSectionVisible) {
            val oldPassword = etOldPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmNewPassword = etConfirmNewPassword.text.toString()
            
            // Validar que los campos necesarios no estén vacíos
            if (oldPassword.isEmpty()) {
                showPasswordError("La contraseña actual es requerida")
                return
            }
            
            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                showPasswordError("Todos los campos de contraseña son requeridos")
                return
            }
            
            // Validar que las contraseñas nuevas coincidan
            if (newPassword != confirmNewPassword) {
                showPasswordError("Las contraseñas nuevas no coinciden")
                return
            }
            
            // Validar formato de contraseña (igual que en registro)
            if (newPassword.length < 6) {
                showPasswordError("La contraseña debe tener al menos 6 caracteres")
                return
            }
            
            // Autenticar al usuario nuevamente para verificar la contraseña anterior
            val credential = com.google.firebase.auth.EmailAuthProvider
                .getCredential(user.email!!, oldPassword)
            
            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPassword)
                        .addOnSuccessListener {
                            updateUserInfo(userRef, email, telefono)
                            Toast.makeText(context, "Datos y contraseña actualizados con éxito", Toast.LENGTH_SHORT).show()

                            passwordSectionVisible = false
                            passwordSection.visibility = View.GONE
                            btnShowPasswordFields.text = "Cambiar contraseña"
                            etOldPassword.setText("")
                            etNewPassword.setText("")
                            etConfirmNewPassword.setText("")
                            tvPasswordError.visibility = View.GONE
                        }
                        .addOnFailureListener { e ->
                            showPasswordError("Error al actualizar la contraseña: ${e.message}")
                        }
                }
                .addOnFailureListener {
                    showPasswordError("Contraseña actual incorrecta")
                }
        } else {
            // Si la sección de contraseña no está visible, solo actualizar datos básicos
            updateUserInfo(userRef, email, telefono)
        }
    }
    
    private fun checkEmailAndPhoneExistence(email: String, phone: String, currentUserId: String, callback: (emailExists: Boolean, phoneExists: Boolean) -> Unit) {
        val usersRef = database.getReference("users")
        
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailExists = false
                var phoneExists = false
                
                for (userSnapshot in snapshot.children) {
                    if (userSnapshot.key == currentUserId) continue
                    
                    val userEmail = userSnapshot.child("email").getValue(String::class.java)
                    val userPhone = userSnapshot.child("telefono").getValue(String::class.java)
                    
                    if (userEmail == email) {
                        emailExists = true
                    }
                    
                    if (userPhone == phone) {
                        phoneExists = true
                    }
                    
                    if (emailExists && phoneExists) break
                }
                
                callback(emailExists, phoneExists)
            }
            
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al verificar la información: ${error.message}", Toast.LENGTH_SHORT).show()
                callback(false, false) 
            }
        })
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }
    
    private fun isValidPhone(phone: String): Boolean {
        return phone.length == 10 && phone.all { it.isDigit() }
    }

    private fun updateUserInfo(userRef: com.google.firebase.database.DatabaseReference, email: String, telefono: String) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val usuario = Usuario(
                    id = snapshot.key ?: "",
                    nombre = nombre,
                    apellidoPaterno = apellidoPaterno,
                    apellidoMaterno = apellidoMaterno,
                    correo = email,  
                    telefono = telefono, 
                    fechaRegistro = snapshot.child("fechaRegistro").getValue(String::class.java) ?: ""
                )
                
                userRef.setValue(usuario)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al guardar los datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Error al obtener datos actuales: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    
    private fun showPasswordError(errorMsg: String) {
        tvPasswordError.text = errorMsg
        tvPasswordError.visibility = View.VISIBLE
    }
}