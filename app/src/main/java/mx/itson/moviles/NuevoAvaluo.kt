package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class NuevoAvaluo : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_nuevo_avaluo, container, false)

        // Find buttons
        val btnInmuebles: Button = view.findViewById(R.id.btnInmueble)
        val btnEntorno: Button = view.findViewById(R.id.btnEntorno)

        // Set click listeners
        btnInmuebles.setOnClickListener {
            val fragment = OpcionesCaracteristicas.newInstance(
                opcion = "inmueble",
                folio = "XX0000111",
                esTipoInstalacion = false
            )

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        btnEntorno.setOnClickListener {
            val fragment = OpcionesCaracteristicas.newInstance(
                opcion = "entorno",
                folio = "XX0000111",
                esTipoInstalacion = true
            )

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}