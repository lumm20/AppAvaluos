package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SignIn : AppCompatActivity() {
    private lateinit var btnSignin:Button
    private lateinit var username:EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)

        btnSignin = findViewById(R.id.sign_in)
        username = findViewById(R.id.user)
        email= findViewById(R.id.email)
        password= findViewById(R.id.password)
        confPassword= findViewById(R.id.conf_password)

        btnSignin.setOnClickListener{
            if(validateFields()){
                val intent= Intent(this, MainActivity::class.java)
                startActivity(intent)
            }else{
                showError("credenciales incorrectas",true)
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun validateFields():Boolean{
        if(username.text.toString().isBlank()){
            return false
        }
        if(email.text.toString().isBlank()){
            return false
        }
        if(password.text.toString().isBlank()){
            return false
        }
        if(confPassword.text.toString().isBlank()){
            return false
        }
        return true
    }

    private fun showError(text:String,visible:Boolean){
        val errorTv : TextView =findViewById(R.id.tvError)
        errorTv.text=text
        errorTv.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }
}