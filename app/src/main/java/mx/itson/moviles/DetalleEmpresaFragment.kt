package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_NOMBRE_EMPRESA= "empresa"
private const val ARG_DESCRIPCION = "descripcion"

/**
 * A simple [Fragment] subclass.
 * Use the [DetalleEmpresaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetalleEmpresaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var empresa: String? = null
    private var descripcion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empresa = it.getString(ARG_NOMBRE_EMPRESA)
            descripcion = it.getString(ARG_DESCRIPCION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_detalle_empresa, container, false)

        val nombreEmpresa = view.findViewById<TextView>(R.id.title)
        val descripcionEmpresa = view.findViewById<TextView>(R.id.description)
        val agendarCita = view.findViewById<Button>(R.id.agendarCita)

        nombreEmpresa.text = empresa
        descripcionEmpresa.text = descripcion

        agendarCita.setOnClickListener {
            val nuevoAvaluoIntent = Intent(requireContext(), NuevoAvaluoActivity::class.java)
            startActivity(nuevoAvaluoIntent)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetalleEmpresaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(nombre: String, descripcion: String) =
            DetalleEmpresaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NOMBRE_EMPRESA, nombre)
                    putString(ARG_DESCRIPCION, descripcion)
                }
            }
    }
}