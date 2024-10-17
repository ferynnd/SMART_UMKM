package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.ActivityAdminBinding

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)


        loadFragment(ListTransactionFragment())

        binding.bottomNavAdmin.setOnItemSelectedListener {
            when (it.itemId) {
                    R.id.transaction -> loadFragment(ListTransactionFragment())
                    R.id.product -> loadFragment(ListProductFragment())
                    R.id.user -> loadFragment(ListUserFragment())
                    R.id.profile -> loadFragment(AdminProfileFragment())
                    else -> false
            }
            true
        }

    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_admin,fragment)
        transaction.commit()
    }


}