package mx.itson.moviles

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class AvaluoCaracteristicas : AppCompatActivity() {
    var menu: ArrayList<String> = ArrayList<String>()
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



        var btnSala : Button = findViewById(R.id.btnSala)
        var btnCome : Button = findViewById(R.id.btnComedor)
        var btnCocina : Button = findViewById(R.id.btnCocina)
        var btnBano : Button = findViewById(R.id.btnBano)
        var btnRecamara : Button = findViewById(R.id.btnRecamara)
        var btnEstancia : Button = findViewById(R.id.btnEstancia)
        var btnPatioP : Button = findViewById(R.id.btnPatioP)
        var btnEstac : Button = findViewById(R.id.btnEstac)
        var btnTerraza : Button = findViewById(R.id.btnTerraza)
        var btnInsHidra : Button = findViewById(R.id.btnInsHidra)
        var btnInsSani : Button = findViewById(R.id.btnInsSani)
        var btnInsElec : Button = findViewById(R.id.btnInsElec)
        var btnObraComp : Button = findViewById(R.id.btnObraComp)
        var btnElemAcc : Button = findViewById(R.id.btnElemAcc)

        cargarCaracteristicas(btnSala, 1)
        cargarCaracteristicas(btnCome, 2)
        cargarCaracteristicas(btnCocina, 3)


    }

    fun cargarCaracteristicas(btn:Button,id: Int){
        btn.setOnClickListener{
            var intent: Intent = Intent(this,AvaluoCaracteristicas::class.java)
            intent.putExtra("caracteristica",id)
            startActivity(intent)
        }
    }

}