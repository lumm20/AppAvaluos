package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignIn : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        val btnRegistrar: Button = findViewById(R.id.sign_in)

        btnRegistrar.setOnClickListener {
            val intent= Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}