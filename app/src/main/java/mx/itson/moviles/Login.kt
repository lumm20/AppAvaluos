package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.math.sign

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val btnLogin:Button= findViewById(R.id.login)
        val username:EditText= findViewById(R.id.user)
        val password:EditText= findViewById(R.id.pass)
        val signin:Button= findViewById(R.id.sign_in)

        btnLogin.setOnClickListener{
            val user=username.text.toString()
            val pass=password.text.toString()
            if(login(user,pass)){
                val intent=Intent(this, MenuPrincipal::class.java)
                startActivity(intent)
            }else{
                showError("credenciales incorrectas",true)
            }
        }

        signin.setOnClickListener{
            val intent=Intent(this, SignIn::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun login(user:String, pass:String):Boolean{
        return true
    }

    fun showError(text:String = "",visible:Boolean){
        val errorTv : TextView =findViewById(R.id.tvError)
        errorTv.text=text
        errorTv.visibility = if(visible) View.VISIBLE else View.INVISIBLE
    }
}