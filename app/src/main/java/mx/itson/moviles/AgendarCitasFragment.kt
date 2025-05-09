package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

private const val ARG_EMPRESA = "empresa"
class AgendarCitasFragment : Fragment() {
    private var empresa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empresa = it.getString(ARG_EMPRESA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_agendar_citas, container, false)

        val btnSiguiente: Button = view.findViewById(R.id.siguiente_btn)
        val etEmpresa: EditText= view.findViewById(R.id.nombre_et)
        etEmpresa.setText(empresa)
        val fragment = CitaRegistradaFragment()

        btnSiguiente.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param empresa Parameter 1.
         * @return A new instance of fragment EmpresasFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(empresa: String) =
            AgendarCitasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_EMPRESA, empresa)
                }
            }
    }

}