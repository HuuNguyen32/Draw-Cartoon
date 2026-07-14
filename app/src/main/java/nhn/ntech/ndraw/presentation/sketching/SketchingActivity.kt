package nhn.ntech.ndraw.presentation.sketching

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import nhn.ntech.ndraw.R
import nhn.ntech.ndraw.databinding.ActivitySketchingBinding

class SketchingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySketchingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sketching)
        setPaddingScreen()
    }

    private fun setPaddingScreen() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}