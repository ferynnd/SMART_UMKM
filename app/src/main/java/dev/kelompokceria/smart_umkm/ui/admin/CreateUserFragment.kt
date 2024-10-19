package dev.kelompokceria.smart_umkm.ui.admin

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class CreateUserFragment : Fragment() {


    private lateinit var binding: FragmentCreateUserBinding
    private lateinit var userViewModel: UserViewModel
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateUserBinding.inflate(inflater,container,false)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hideBottomNavigationView()
        setupSpinner()

        // Menetapkan listener untuk tombol
        binding.btnLogin.setOnClickListener {
             if (validateInputs()) {
                createUser() // Membuat user jika input valid
                navigateToUserList() // Berpindah ke ListUserFragment
            } else {
                Toast.makeText(requireContext(), "Silahkan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*") // Memilih gambar dari galeri
        }

    }

      override fun onDestroyView() {
        super.onDestroyView()

        showBottomNavigationView()
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


    private fun setupSpinner() {
        val roles = arrayListOf("ADMIN", "USER")

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

    private fun createUser() {
        val name = binding.edName.text.toString()
        val email = binding.edEmail.text.toString()
        val phone = binding.edPhone.text.toString()
        val username = binding.edUsername.text.toString()
        val password = binding.edPassword.text.toString()
        val role = UserRole.valueOf(binding.userRole.selectedItem.toString())
//
//        val imageBytes = bitmapToByteArray(selectedImage)

           // Ubah URI gambar menjadi Bitmap dan kemudian menjadi Byte Array
        val imageBytes = imageUri?.let { uri ->
            val bitmap = uriToBitmap(uri)
            bitmapToByteArray(bitmap)
        }

        val user = User(
            image = imageBytes,
            name = name,
            email = email,
            phone = phone,
            username = username,
            password = password,
            role = role
        )

        lifecycleScope.launch {
            userViewModel.addUser(user)
        }

    }

    private fun navigateToUserList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, ListUserFragment())
            .addToBackStack(null)
            .commit()
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.ImagePreview) // Preview image
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap?): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
   }

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



}