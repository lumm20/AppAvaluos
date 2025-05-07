package mx.itson.moviles

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var profileMenu: FrameLayout
    private lateinit var menuPanel: LinearLayout
    private lateinit var menuBackground: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        replaceFragment(HomeFragment())

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_citas -> replaceFragment(AgendarCitasFragment())
                R.id.nav_info -> replaceFragment(InfoFragment())
                R.id.nav_avaluos -> {
                    val intent = Intent(this, MisAvaluosActivity::class.java)
                    startActivity(intent)
                    return@setOnItemSelectedListener false
                }
            }
            true
        }

    }

    fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }


    fun showProfileMenu() {
        profileMenu.visibility = View.VISIBLE

        menuPanel.animate()
            .translationX(0f)
            .setDuration(300)
            .start()
    }

    fun hideProfileMenu() {
        // Animate the menu sliding out
        menuPanel.animate()
            .translationX(-menuPanel.width.toFloat())
            .setDuration(300)
            .withEndAction {
                profileMenu.visibility = View.GONE
            }
            .start()
    }


}