package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AvaluoNuevo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avaluo_nuevo)

        val btnInmuebles: Button = findViewById(R.id.btnInmueble)
        val btnEntorno : Button = findViewById(R.id.btnEntorno)

        btnInmuebles.setOnClickListener {
            var intent: Intent = Intent(this,AvaluoCaracteristicas::class.java)
            intent.putExtra("opcionLugar","Inmueble")
            startActivity(intent)
        }
        btnEntorno.setOnClickListener {
            var intent: Intent = Intent(this,AvaluoCaracteristicas::class.java)
            intent.putExtra("opcionLugar","Entorno")
            startActivity(intent)
        }
    }
}