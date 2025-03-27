package mx.itson.moviles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView


class CitaRegistradaFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_cita_registrada, container, false)

        val btnVolver: Button = view.findViewById(R.id.volver_inicio_btn)

        val fragment = HomeFragment()

        btnVolver.setOnClickListener {

            val bottomNavigation = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNavigation.selectedItemId = R.id.nav_home

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        return view
    }


}