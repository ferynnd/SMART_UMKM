package dev.kelompokceria.smart_umkm.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.databinding.ActivityLoginBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.ui.admin.AdminActivity
import dev.kelompokceria.smart_umkm.ui.user.UserActivity
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPref: PreferenceHelper
    private val userViewModel: UserViewModel by viewModels()

    private val networkStatusViewModel: NetworkStatusViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceHelper(this)

        // Cek apakah sudah login sebelumnya
        checkLogin()

        networkStatusViewModel.networkStatus.observe(this){ isConnected ->
            if (isConnected) {

                Toast.makeText(this, "Network is connected", Toast.LENGTH_SHORT).show()

                binding.btnLogin.setOnClickListener {
                    val username = binding.edUsername.text.toString()
                    val password = binding.edPassword.text.toString()

                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        // Panggil getUserLogin dan amati LiveData user
                        lifecycleScope.launch {
                            userViewModel.getUserLogin(username, password)
                        }
                    } else {
                        Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Network is not connected", Toast.LENGTH_SHORT).show()
            }
        }

        // Mengamati perubahan nilai user
        userViewModel.loggedInUser.observe(this) { user ->
            if (user != null) {
                // Simpan status login ke SharedPreferences
                sharedPref.put(Constant.PREF_IS_LOGIN, true)
                user.username?.let { sharedPref.put(Constant.PREF_USER_NAME, it) }
                user.password?.let { sharedPref.put(Constant.PREF_USER_PASSWORD, it) }
                user.role?.let { sharedPref.put(Constant.PREF_USER_ROLE, it.name) } // Simpan role dengan nama enum
                // Navigasi berdasarkan role
                user.role?.let { navigateToRole(it) }
            } else {
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkLogin() {
        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            val role = sharedPref.getString(Constant.PREF_USER_ROLE)
            when (role) {
                UserRole.ADMIN.name -> navigateToRole(UserRole.ADMIN)
                UserRole.USER.name -> navigateToRole(UserRole.USER)
                else -> {
                    // Tidak ada role, logout
                    sharedPref.clear()
                }
            }
        }
    }

    private fun navigateToRole(role: UserRole) {
        when (role) {
            UserRole.ADMIN -> {
                startActivity(Intent(this, AdminActivity::class.java))
                finish()
            }
            UserRole.USER -> {
                startActivity(Intent(this, UserActivity::class.java))
                finish()
            }
        }
    }
}
