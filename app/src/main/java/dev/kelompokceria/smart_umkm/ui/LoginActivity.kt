package dev.kelompokceria.smart_umkm.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.databinding.ActivityLoginBinding
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.ui.admin.AdminActivity
import dev.kelompokceria.smart_umkm.ui.user.UserActivity
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPref: PreferenceHelper
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = PreferenceHelper(this)

        // Cek apakah sudah login sebelumnya
        checkLogin()

        binding.btnLogin.setOnClickListener {
            val username = binding.edUsername.text.toString()
            val password = binding.edPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                // Panggil getUserLogin dan amati LiveData user
                userViewModel.getUserLogin(username, password)
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Mengamati perubahan nilai user
        userViewModel.user.observe(this) { user ->
            if (user != null) {
                // Simpan status login ke SharedPreferences
                sharedPref.put(Constant.PREF_IS_LOGIN, true)
                sharedPref.put(Constant.PREF_USER_NAME, user.username)
                sharedPref.put(Constant.PREF_USER_PASSWORD, user.password)
                sharedPref.put(Constant.PREF_USER_ROLE, user.role.name) // Simpan role dengan nama enum

                // Navigasi berdasarkan role
                navigateToRole(user.role)
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
