package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class OpcionesCaracteristicas : Fragment() {
    private var opcion: String? = null
    private var folio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Retrieve arguments passed to the fragment
        arguments?.let {
            opcion = it.getString("opcion")
            folio = it.getString("folio")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_opciones_caracteristicas, container, false)

        // Find TextViews
        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val txtFolio: TextView = view.findViewById(R.id.txtFolio)

        // Set TextViews
        txtTitulo.text = "Características del $opcion"
        txtFolio.text = "Folio: $folio"

        // Find Buttons
        val btnSala: Button = view.findViewById(R.id.btnSala)
        val btnCome: Button = view.findViewById(R.id.btnComedor)
        val btnCocina: Button = view.findViewById(R.id.btnCocina)
        val btnBano: Button = view.findViewById(R.id.btnBano)
        val btnRecamara: Button = view.findViewById(R.id.btnRecamara)
        val btnEstancia: Button = view.findViewById(R.id.btnEstancia)
        val btnPatioP: Button = view.findViewById(R.id.btnPatioP)
        val btnEstac: Button = view.findViewById(R.id.btnEstac)
        val btnTerraza: Button = view.findViewById(R.id.btnTerraza)
        val btnInsHidra: Button = view.findViewById(R.id.btnInsHidra)
        val btnInsSani: Button = view.findViewById(R.id.btnInsSani)
        val btnInsElec: Button = view.findViewById(R.id.btnInsElec)
        val btnObraComp: Button = view.findViewById(R.id.btnObraComp)
        val btnElemAcc: Button = view.findViewById(R.id.btnElemAcc)

        // Load characteristics for some buttons (example)
        cargarCaracteristicas(btnSala, 1,"Sala")
        cargarCaracteristicas(btnCome, 2, "Comedor")
        cargarCaracteristicas(btnCocina, 3,"Cocina")
        cargarCaracteristicas(btnBano, 4,"Baño")
        cargarCaracteristicas(btnRecamara, 5,"Recamara")
        cargarCaracteristicas(btnEstancia, 6,"Estancia")
        cargarCaracteristicas(btnPatioP, 7,"Patio posterior")
        cargarCaracteristicas(btnEstac, 8,"Estacionamiento")
        cargarCaracteristicas(btnTerraza, 9,"Terraza")
        cargarCaracteristicas(btnInsHidra, 10,"Instalciones hidráulicas")
        cargarCaracteristicas(btnInsSani, 11,"Instalaciones sanitarias")
        cargarCaracteristicas(btnInsElec, 12,"Instalaciones eléctricas")
        cargarCaracteristicas(btnObraComp, 13,"Obras complementarias")
        cargarCaracteristicas(btnElemAcc, 14,"Elementos accesorios")

        return view
    }

    private fun cargarCaracteristicas(btn: Button, id: Int, desc: String) {
        btn.setOnClickListener {
            val fragment = CaracteristicasAvaluo().apply {
                arguments = Bundle().apply {
                    putString("desc", desc)
                    putInt("lugar",id)
                    putString("tipo", "Pisos")
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment) // Usa el fragment con argumentos
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        // Define constant keys for arguments
        private const val ARG_OPCION = "opcion"
        private const val ARG_FOLIO = "folio"

        // Factory method to create a new instance of the fragment
        @JvmStatic
        fun newInstance(opcion: String, folio: String) =
            OpcionesCaracteristicas().apply {
                arguments = Bundle().apply {
                    putString(ARG_OPCION, opcion)
                    putString(ARG_FOLIO, folio)
                }
            }
    }
}