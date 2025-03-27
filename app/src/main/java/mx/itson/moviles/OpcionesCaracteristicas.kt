package mx.itson.moviles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)

        // Load characteristics for some buttons
        cargarCaracteristicas(btnSala, 1, "Sala")
        cargarCaracteristicas(btnCome, 2, "Comedor")
        cargarCaracteristicas(btnCocina, 3, "Cocina")
        cargarCaracteristicas(btnBano, 4, "Baño")
        cargarCaracteristicas(btnRecamara, 5, "Recamara")
        cargarCaracteristicas(btnEstancia, 6, "Estancia")
        cargarCaracteristicas(btnPatioP, 7, "Patio posterior")
        cargarCaracteristicas(btnEstac, 8, "Estacionamiento")
        cargarCaracteristicas(btnTerraza, 9, "Terraza")

        btnAceptar.setOnClickListener {
            Toast.makeText(context, "Guardado con éxito", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun cargarCaracteristicas(btn: Button, id: Int, desc: String) {
        btn.setOnClickListener {
            val fragment = CaracteristicasAvaluo().apply {
                arguments = Bundle().apply {
                    putString("desc", desc)
                    putInt("lugar", id)
                    putString("tipo", "Pisos")
                }
            }

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        private const val ARG_OPCION = "opcion"
        private const val ARG_FOLIO = "folio"

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