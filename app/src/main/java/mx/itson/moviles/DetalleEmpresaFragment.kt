package mx.itson.moviles

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_NOMBRE_EMPRESA= "empresa"
private const val ARG_DESCRIPCION = "descripcion"
private const val ARG_UBICACION = "ubicacion"
private const val ARG_SRC = "src"

/**
 * A simple [Fragment] subclass.
 * Use the [DetalleEmpresaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetalleEmpresaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var empresa: String? = null
    private var descripcion: String? = null
    private var ubicacion: String? = null
    private var src: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empresa = it.getString(ARG_NOMBRE_EMPRESA)
            descripcion = it.getString(ARG_DESCRIPCION)
            ubicacion = it.getString(ARG_UBICACION)
            src = it.getInt(ARG_SRC)
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
        val imagen = view.findViewById<ImageView>(R.id.imagen)

        imagen.setImageResource(src)
        nombreEmpresa.text = empresa
        descripcionEmpresa.text = descripcion

        agendarCita.setOnClickListener {
            val agendarCitasFragment= AgendarCitasFragment.newInstance(nombreEmpresa.text.toString())
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, agendarCitasFragment)
                .commit()
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
        fun newInstance(nombre: String, descripcion: String, ubicacion : String, src: Int) =
            DetalleEmpresaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NOMBRE_EMPRESA, nombre)
                    putString(ARG_DESCRIPCION, descripcion)
                    putString(ARG_UBICACION, ubicacion)
                    putInt(ARG_SRC, src)
                }
            }
    }
}