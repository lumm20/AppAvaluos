package mx.itson.moviles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class PerfilFragment : Fragment() {
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_perfil, container, false)

        val profileIcon = view.findViewById<ImageView>(R.id.profileIcon)
        val tvNombre = view.findViewById<TextView>(R.id.tvNombre)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etPassword = view.findViewById(R.id.etPassword)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)

        tvNombre.text = "Usuario Ejemplo"
        etEmail.setText("usuario@ejemplo.com")
        etPhone.setText("123-456-7890")
        etPassword.setText("••••••••")

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        etEmail.setOnEditorActionListener { _, _, _ ->
            etEmail.clearFocus()
            true
        }

        etPhone.setOnEditorActionListener { _, _, _ ->
            etPhone.clearFocus()
            true
        }

        etPassword.setOnEditorActionListener { _, _, _ ->
            etPassword.clearFocus()
            true
        }

        return view
    }
}