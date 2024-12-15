package dev.kelompokceria.smart_umkm.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.data.helper.RetrofitHelper
import dev.kelompokceria.smart_umkm.databinding.ActivityAdminBinding
import dev.kelompokceria.smart_umkm.ui.ConnectionDialog
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
//import dev.kelompokceria.smart_umkm.ui.user.TransactionFragment
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var sharedPref: PreferenceHelper
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val productViewModel: ProductViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

     private lateinit var networkStatusViewModel: NetworkStatusViewModel



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkStatusViewModel = NetworkStatusViewModel(application)

        sharedPref = PreferenceHelper(this)
        val username = sharedPref.getString(Constant.PREF_USER_NAME)

        networkStatusViewModel.networkStatus.observe(this, Observer { isConnected ->
            if (isConnected) {
                userViewModel.refreshUsers()
                userViewModel.refreshDeleteUsers()
                transactionViewModel.refreshTransaction()
                transactionViewModel.refreshDeleteTransaction()
                productViewModel.refreshProducts()
                productViewModel.refreshDeleteProducts()
            } else {
                showConnectionErrorDialog()
            }
        })

        loadFragment(ListTransactionFragment(), username!!)

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

     private fun showConnectionErrorDialog() {
        val connectionErrorDialog = ConnectionDialog(this)
        connectionErrorDialog.show {
            Toast.makeText(this, "Retrying connection...", Toast.LENGTH_SHORT).show()
            // Retry the connection
        }
    }


}