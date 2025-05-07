package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class MisCitasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mis_citas, container, false)

        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)
        btnCancelar.setOnClickListener {
            // Regresar a HomeFragment manualmente
            parentFragmentManager.popBackStack()
        }

        return view
    }
}