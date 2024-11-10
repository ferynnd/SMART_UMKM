package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding

     private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceHelper(this)
        val username = sharedPref.getString(Constant.PREF_USER_NAME)

        loadFragment(DashboardFragment(), username!!)

        binding.bottomNavUser.setOnItemSelectedListener {
            when (it.itemId) {
                    R.id.dashboard -> loadFragment(DashboardFragment(), username)
                    R.id.user_profile -> loadFragment(ProfileFragment(), username)
                    else -> false
            }
            true
        }
    }

    private  fun loadFragment(fragment: Fragment, username : String){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.nav_host_fragment_user,fragment)
        transaction.commit()
    }
}