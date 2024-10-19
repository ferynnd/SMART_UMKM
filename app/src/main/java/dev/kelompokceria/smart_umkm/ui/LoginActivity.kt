package dev.kelompokceria.smart_umkm.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.ActivityLoginBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.model.UserRole.*
import dev.kelompokceria.smart_umkm.ui.admin.AdminActivity
import dev.kelompokceria.smart_umkm.ui.user.UserActivity
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var database: AppDatabase
    val UserViewModel : UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getInstance(this)
           // Buat repository (sesuaikan dengan implementasi Anda)

        binding.btnLogin.setOnClickListener {
                val username = binding.edUsername.text.toString()
                val password = binding.edPassword.text.toString()

                if (username.isNotEmpty() && password.isNotEmpty()) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        UserViewModel.getUserLogin(username, password)
                    }

                    UserViewModel.user.observe(this) { user ->
                        if (user != null) {
                            when (user.role) {
                                ADMIN -> startActivity(Intent(this, AdminActivity::class.java))
                                USER -> startActivity(Intent(this, UserActivity::class.java))
                            }
                        } else {
                            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
                }
            }


    }

}