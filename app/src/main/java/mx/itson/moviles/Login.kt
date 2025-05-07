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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        
        auth = FirebaseAuth.getInstance()

        val btnLogin: Button = findViewById(R.id.login)
        val username: EditText = findViewById(R.id.user)
        val password: EditText = findViewById(R.id.pass)
        val signin: TextView = findViewById(R.id.sign_in)

        btnLogin.setOnClickListener {
            val email = username.text.toString().trim()
            val pass = password.text.toString().trim()
            
            if (email.isEmpty() || pass.isEmpty()) {
                showError("Todos los campos son requeridos", true)
                return@setOnClickListener
            }

            btnLogin.isEnabled = false
            
            auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    btnLogin.isEnabled = true 
                    
                    if (task.isSuccessful) {
                        showError("", false)
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Si falla el login
                        showError("Credenciales incorrectas", true)
                    }
                }
        }

        signin.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun showError(text: String = "", visible: Boolean) {
        val errorTv: TextView = findViewById(R.id.tvError)
        errorTv.text = text
        errorTv.visibility = if (visible) View.VISIBLE else View.INVISIBLE
    }
}