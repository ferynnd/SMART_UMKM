package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

         val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_admin) as NavHostFragment
        val navController = navHostFragment.navController

    }
}