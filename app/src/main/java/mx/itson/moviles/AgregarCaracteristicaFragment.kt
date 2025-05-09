package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "opcioLugar"
private const val ARG_PARAM2 = "folio"

/**
 * A simple [Fragment] subclass.
 * Use the [AgregarCaracteristicaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgregarCaracteristicaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var opcionLugar: String? = null
    private var folio: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            opcionLugar = it.getString(ARG_PARAM1)
            folio = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_agregar_caracteristica, container, false)

        val txtLugar:TextView = view.findViewById(R.id.txtTitulo)
        val txtFolio:TextView = view.findViewById(R.id.txtFolio)

        val txt = "Características del $opcionLugar"
        val folioTxt = "Folio: $folio"
        txtLugar.text = txt
        txtFolio.text =folioTxt

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param opcionLugar Parameter 1.
         * @param folio Parameter 2.
         * @return A new instance of fragment AgregarCaracteristicaFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(opcionLugar: String, folio: String) =
            AgregarCaracteristicaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, opcionLugar)
                    putString(ARG_PARAM2, folio)
                }
            }
    }
}