package dev.kelompokceria.smart_umkm.ui

import android.content.Intent
import android.os.Bundle
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
import dev.kelompokceria.smart_umkm.ui.admin.AdminActivity
import dev.kelompokceria.smart_umkm.ui.user.UserActivity
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
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

        binding.btnLogin.setOnClickListener{
            val username = binding.edUsername.text
            val password = binding.edPassword.text

//             if (username.toString().isNotEmpty() && password.toString().isNotEmpty()) {
//                    lifecycleScope.launch {
//                        val user = UserViewModel.geuUserLogin(username.toString(), password.toString())
//
//                        if (user != null) {
//                            when (user.role) {
//                                UserRole.ADMIN -> {
//                                    val intent = Intent(applicationContext, AdminActivity::class.java)
////                                    intent.putExtra("KEY_USERNAME", username)
//                                    startActivity(intent)
//                                }
//                                UserRole.USER -> {
//                                    val intent = Intent(applicationContext, UserActivity::class.java)
////                                    intent.putExtra("KEY_USERNAME", username)
//                                    startActivity(intent)
//                                }
//                            }
//                        } else {
//                            Toast.makeText(
//                                applicationContext,
//                                "Username atau password salah",
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                    }
//             }

        }

    }
}