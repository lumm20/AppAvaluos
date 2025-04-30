package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.cardview.widget.CardView

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var isProfileOptionsVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("param1")
            param2 = it.getString("param2")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val profileIcon = view.findViewById<CardView>(R.id.profile)
        val profileOptionsCard = view.findViewById<CardView>(R.id.profileOptionsCard)
        val btnMisCitas = view.findViewById<Button>(R.id.btnMisCitas)
        val btnEditarPerfil = view.findViewById<Button>(R.id.btnEditarPerfil)
        val btnCerrarSesion = view.findViewById<Button>(R.id.btnCerrarSesion)
        val btnNuevoAvaluo = view.findViewById<Button>(R.id.btnNuevoAvaluo)

        profileIcon.setOnClickListener {
            isProfileOptionsVisible = !isProfileOptionsVisible
            profileOptionsCard.visibility = if (isProfileOptionsVisible) View.VISIBLE else View.GONE
        }

        btnMisCitas.setOnClickListener {
            val misCitasFragment = MisCitasFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, misCitasFragment)
                .addToBackStack(null)
                .commit()
        }

        btnEditarPerfil.setOnClickListener {
            val perfilFragment = PerfilFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, perfilFragment)
                .addToBackStack(null)
                .commit()
        }

        btnCerrarSesion.setOnClickListener {
            val intent = Intent(requireContext(), Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
        
        btnNuevoAvaluo.setOnClickListener {
            val listAvaluosIntent = Intent(requireContext(), MisAvaluosActivity::class.java)
            startActivity(listAvaluosIntent)
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}