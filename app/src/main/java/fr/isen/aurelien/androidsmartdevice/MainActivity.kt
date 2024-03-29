package fr.isen.aurelien.androidsmartdevice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import fr.isen.aurelien.androidsmartdevice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.begin.setOnClickListener {
            val intent = Intent(this, ScanActivity::class.java)
            startActivity(intent)
        }

    }
}