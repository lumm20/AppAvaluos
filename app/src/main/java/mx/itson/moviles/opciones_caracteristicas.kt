package mx.itson.moviles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

class opciones_caracteristicas : Fragment() {
    private var opcion: String? = null
    private var folio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val view = inflater.inflate(R.layout.fragment_opciones_caracteristicas2, container, false)

        val txtTitulo: TextView = view.findViewById(R.id.txtTitulo)
        val txtFolio: TextView = view.findViewById(R.id.txtFolio)

        txtTitulo.text = "Características del $opcion"
        txtFolio.text = "Folio: $folio"

        val btnInsHidra: Button = view.findViewById(R.id.btnInsHidra)
        val btnInsSani: Button = view.findViewById(R.id.btnInsSani)
        val btnInsElec: Button = view.findViewById(R.id.btnInsElec)
        val btnObraComp: Button = view.findViewById(R.id.btnObraComp)
        val btnElemAcc: Button = view.findViewById(R.id.btnElemAcc)
        val btnAceptar: Button = view.findViewById(R.id.btnAceptar)

        // Ajustamos los valores de lugar para que coincidan con CaracteristicasAvaluo
        cargarCaracteristicas(btnInsHidra, 10, "Instalaciones hidráulicas")
        cargarCaracteristicas(btnInsSani, 11, "Instalaciones sanitarias")
        cargarCaracteristicas(btnInsElec, 12, "Instalaciones eléctricas")
        cargarCaracteristicas(btnObraComp, 13, "Obras complementarias")
        cargarCaracteristicas(btnElemAcc, 14, "Elementos accesorios")

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
                    putString("tipo", "Pisos") // No se usará para lugar >= 10, pero se incluye por compatibilidad
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
            opciones_caracteristicas().apply {
                arguments = Bundle().apply {
                    putString(ARG_OPCION, opcion)
                    putString(ARG_FOLIO, folio)
                }
            }
    }
}