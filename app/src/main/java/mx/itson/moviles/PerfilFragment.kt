package mx.itson.moviles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment

class PerfilFragment : Fragment() {
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etOldPassword: EditText
    private lateinit var etNewPassword: EditText

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
        etOldPassword = view.findViewById(R.id.etOldPassword)
        etNewPassword = view.findViewById(R.id.etNewPassword)
        val btnBack = view.findViewById<ImageButton>(R.id.btn_back)
        val btnSave = view.findViewById<Button>(R.id.btn_save)

        tvNombre.text = "Usuario Ejemplo"
        etEmail.setText("usuario@ejemplo.com")
        etPhone.setText("123-456-7890")
        etOldPassword.setText("")
        etNewPassword.setText("")

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnSave.setOnClickListener {
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

        etOldPassword.setOnEditorActionListener { _, _, _ ->
            etOldPassword.clearFocus()
            true
        }

        etNewPassword.setOnEditorActionListener { _, _, _ ->
            etNewPassword.clearFocus()
            true
        }

        return view
    }
}