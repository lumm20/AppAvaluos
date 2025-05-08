package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import mx.itson.moviles.modelo.Usuario

class SignIn : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var tvError: TextView
    private lateinit var emailEditText: EditText
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoPaternoEditText: EditText
    private lateinit var apellidoMaternoEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confPasswordEditText: EditText
    private lateinit var signInButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        emailEditText = findViewById(R.id.email)
        nombreEditText = findViewById(R.id.nombre)
        apellidoPaternoEditText = findViewById(R.id.apellido_paterno)
        apellidoMaternoEditText = findViewById(R.id.apellido_materno)
        phoneEditText = findViewById(R.id.phone)
        passwordEditText = findViewById(R.id.password)
        confPasswordEditText = findViewById(R.id.conf_password)
        tvError = findViewById(R.id.tvError)
        signInButton = findViewById(R.id.sign_in)
        val loginLink = findViewById<TextView>(R.id.login_link)

        signInButton.setOnClickListener {
            validateAndRegister()
        }

        loginLink.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

    private fun validateFields():Boolean{
        if(nombreEditText.text.toString().isBlank()){
            return false
        }
        if(emailEditText.text.toString().isBlank()){
            return false
        }
        if(passwordEditText.text.toString().isBlank()){
            return false
        }
        if(confPasswordEditText.text.toString().isBlank()){
            return false
        }
        return true
    }

    private fun showError(text:String,visible:Boolean){
        val errorTv : TextView =findViewById(R.id.tvError)
        errorTv.text=text
        errorTv.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }

    private fun validateAndRegister() {
        tvError.visibility = View.GONE
        
        val email = emailEditText.text.toString().trim()
        val nombre = nombreEditText.text.toString().trim()
        val apellidoPaterno = apellidoPaternoEditText.text.toString().trim()
        val apellidoMaterno = apellidoMaternoEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val confPassword = confPasswordEditText.text.toString()
        
        if (email.isEmpty() || nombre.isEmpty() || apellidoPaterno.isEmpty() || 
            apellidoMaterno.isEmpty() || phone.isEmpty() || password.isEmpty() || 
            confPassword.isEmpty()
        ) {
            showError("Todos los campos deben ser llenados")
            return
        }
        
        if (!isValidEmail(email)) {
            showError("Por favor ingresa un correo electrónico válido")
            return
        }
        
        if (!isValidPhone(phone)) {
            showError("El teléfono debe tener 10 dígitos")
            return
        }

        if (password != confPassword) {
            showError("Las contraseñas no coinciden")
            return
        }

        if (password.length < 6) {
            showError("La contraseña debe tener al menos 6 caracteres")
            return
        }

        signInButton.isEnabled = false
        
        checkEmailAndPhoneExistence(email, phone) { emailExists, phoneExists -> 
            if (emailExists) {
                showError("Este correo electrónico ya está registrado")
                signInButton.isEnabled = true
                return@checkEmailAndPhoneExistence
            }
            
            if (phoneExists) {
                showError("Este número de teléfono ya está registrado")
                signInButton.isEnabled = true
                return@checkEmailAndPhoneExistence
            }
            
            registerUser(email, password, nombre, apellidoPaterno, apellidoMaterno, phone)
        }
    }
    
    private fun checkEmailAndPhoneExistence(email: String, phone: String, callback: (emailExists: Boolean, phoneExists: Boolean) -> Unit) {
        val usersRef = FirebaseDatabase.getInstance().getReference("users")
        
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var emailExists = false
                var phoneExists = false
                
                for (userSnapshot in snapshot.children) {
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
                Toast.makeText(baseContext, "Error al verificar la información: ${error.message}", Toast.LENGTH_SHORT).show()
                callback(false, false) 
                signInButton.isEnabled = true
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
    
    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun registerUser(
        email: String,
        password: String,
        nombre: String,
        apellidoPaterno: String,
        apellidoMaterno: String,
        phone: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task -> 
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        val usuario = Usuario(
                            id = userId,
                            nombre = nombre,
                            apellidoPaterno = apellidoPaterno,
                            apellidoMaterno = apellidoMaterno,
                            correo = email,
                            telefono = phone,
                            fechaRegistro = System.currentTimeMillis().toString()
                        )

                        database.child("users").child(userId).setValue(usuario)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    baseContext,
                                    "El registro fue exitoso.",
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception -> 
                                signInButton.isEnabled = true
                                Toast.makeText(
                                    baseContext,
                                    "Error al guardar datos: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                } else {
                    signInButton.isEnabled = true
                    showError("Error: ${task.exception?.message}")
                }
            }
    }
}