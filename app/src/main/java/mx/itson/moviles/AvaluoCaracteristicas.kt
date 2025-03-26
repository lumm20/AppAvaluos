package mx.itson.moviles

import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AvaluoCaracteristicas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_avaluo_caracteristicas)

        var lugar: String? = intent.getStringExtra("opcionLugar")
        var folio: String? = intent.getStringExtra("folio")

        var txtTitulo : TextView = findViewById(R.id.txtTitulo)

        txtTitulo.setText("Características del $lugar")
        var txtFolio : TextView = findViewById(R.id.txtFolio)
        txtFolio.setText("Folio: $folio")
        var listaView : ListView = findViewById<ListView>(R.id.listaLugares)


    }
}