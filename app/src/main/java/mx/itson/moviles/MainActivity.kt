package mx.itson.moviles

import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Hide the status bar for a more immersive experience
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
//        supportActionBar?.hide()

        // Set up click listeners for navigation items
        setupNavigation()
    }

    private fun setupNavigation() {
        val navHome = findViewById<FrameLayout>(R.id.navHome)
        val navStats = findViewById<FrameLayout>(R.id.navStats)
        val navDocuments = findViewById<FrameLayout>(R.id.navDocuments)
        val navInfo = findViewById<FrameLayout>(R.id.navInfo)
        val profileIcon = findViewById<View>(R.id.profileIconContainer)

        navHome.setOnClickListener {
            Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
            // Navigate to home screen
        }

        navStats.setOnClickListener {
            Toast.makeText(this, "Statistics", Toast.LENGTH_SHORT).show()
            // Navigate to statistics screen
        }

        navDocuments.setOnClickListener {
            Toast.makeText(this, "Documents", Toast.LENGTH_SHORT).show()
            // Navigate to documents screen
        }

        navInfo.setOnClickListener {
            Toast.makeText(this, "Information", Toast.LENGTH_SHORT).show()
            // Navigate to information screen
        }

        profileIcon.setOnClickListener {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            // Navigate to profile screen
        }
    }
}