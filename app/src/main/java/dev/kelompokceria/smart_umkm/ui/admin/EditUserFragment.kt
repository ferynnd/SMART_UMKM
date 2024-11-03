package dev.kelompokceria.smart_umkm.ui.admin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.FragmentEditUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class EditUserFragment : Fragment() {

    private lateinit var binding: FragmentEditUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var database: AppDatabase

    private var imageUri: Uri? = null
    private var imagePath: String? = null
    private var id: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditUserBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        database = AppDatabase.getInstance(requireContext())

        // Ambil data dari arguments dan tampilkan
        imagePath = arguments?.getString("IMAGE_PATH")
        imagePath?.let {
            Glide.with(this).load(File(it)).into(binding.ImagePreview)
        }

        // Ambil ID
        id = arguments?.getString("USER_ID")?.toInt()

        // Mengisi field input dengan data yang ada
        binding.edName.setText(arguments?.getString("NAME"))
        binding.edEmail.setText(arguments?.getString("EMAIL"))
        binding.edPhone.setText(arguments?.getString("PHONE"))
        binding.edUsername.setText(arguments?.getString("USERNAME"))
        binding.edPassword.setText(arguments?.getString("PASSWORD"))

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()
        hideBottomNavigationView()

        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                updateUser()
            } else {
                Toast.makeText(requireContext(), "Silahkan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showBottomNavigationView()
    }

    private fun updateUser() {
        // Ambil input pengguna
        val name = binding.edName.text.toString()
        val email = binding.edEmail.text.toString()
        val phone = binding.edPhone.text.toString()
        val username = binding.edUsername.text.toString()
        val password = binding.edPassword.text.toString()
        val role = UserRole.valueOf(binding.userRole.selectedItem.toString())

        // Menyimpan path gambar
        val updatedImagePath = imageUri?.let { uri ->
            uriToBitmap(uri)?.let { bitmap ->
                saveImageToLocalStorage(bitmap)
            }
        } ?: imagePath ?: ""

        // Pastikan id tidak null saat membuat objek User
        if (id != null) {
            val user = User(
                id = id!!,
                image = updatedImagePath,
                name = name,
                email = email,
                phone = phone,
                username = username,
                password = password,
                role = role
            )

            lifecycleScope.launch {
                try {
                    userViewModel.updateUser(user)
                    Toast.makeText(requireContext(), "User updated successfully", Toast.LENGTH_SHORT).show()
                    navigateToUserList() // Pindah setelah berhasil
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "User ID is null", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return context?.contentResolver?.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.ImagePreview) // Menampilkan gambar yang dipilih
        }
    }

    private fun setupSpinner() {
        val roles = UserRole.values().map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.userRole.adapter = adapter
    }

    private fun validateInputs(): Boolean {
        return binding.edName.text!!.isNotEmpty() &&
               binding.edEmail.text!!.isNotEmpty() &&
               binding.edPhone.text!!.isNotEmpty() &&
               binding.edUsername.text!!.isNotEmpty() &&
               binding.edPassword.text!!.isNotEmpty()
    }

    private fun navigateToUserList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, ListUserFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun saveImageToLocalStorage(imageBitmap: Bitmap): String {
        val filename = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context?.filesDir, filename)
        val outputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    }

        // Fungsi untuk menyembunyikan BottomNavigationView
    private fun hideBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavAdmin)
        bottomNavigationView?.visibility = View.GONE
    }

    // Fungsi untuk menampilkan BottomNavigationView kembali
    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavAdmin)
        bottomNavigationView?.visibility = View.VISIBLE
    }

}
