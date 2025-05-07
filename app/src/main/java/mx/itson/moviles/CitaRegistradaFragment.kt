package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class CitaRegistradaFragment : Fragment() {

    private lateinit var folioTv: TextView
    private lateinit var volverInicioBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cita_registrada, container, false)

        folioTv = view.findViewById(R.id.folio_tv)
        volverInicioBtn = view.findViewById(R.id.volver_inicio_btn)

        val folioCita = arguments?.getString("folioCita")
        folioTv.text = "Folio: #$folioCita"

        volverInicioBtn.setOnClickListener {
            val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNavigation.selectedItemId = R.id.nav_home

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}