package dev.kelompokceria.smart_umkm.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import dev.kelompokceria.smart_umkm.ui.user.TransactionFragment
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private val viewModel : UserViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("KEY_USERNAME")

        loadFragment(TransactionFragment(), username!!)

        binding.bottomNavAdmin.setOnItemSelectedListener {
            when (it.itemId) {
                    R.id.transaction -> loadFragment(ListTransactionFragment(),username)
                    R.id.product -> loadFragment(ListProductFragment(),username)
                    R.id.user -> loadFragment(ListUserFragment(),username)
                    R.id.profile -> loadFragment(AdminProfileFragment(),username)
                    else -> false
            }
            true
        }

    }

    private  fun loadFragment(fragment: Fragment, username : String){
        val bundle = Bundle()
        bundle.putString("KEY_USERNAME", username)
        fragment.arguments = bundle

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_admin,fragment)
        transaction.commit()
    }


}