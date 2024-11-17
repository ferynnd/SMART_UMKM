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
import com.google.android.material.card.MaterialCardView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class CreateUserFragment : Fragment() {


    private lateinit var binding: FragmentCreateUserBinding
    private lateinit var userViewModel: UserViewModel
    private var imageUri: Uri? = null

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
         val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNav)
         bottomNavigationView?.visibility = View.GONE
     }

    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNav)
        bottomNavigationView?.visibility = View.VISIBLE
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

    private fun createUser() {
        val name = binding.edName.text.toString()
        val email = binding.edEmail.text.toString()
        val phone = binding.edPhone.text.toString()
        val username = binding.edUsername.text.toString()
        val password = binding.edPassword.text.toString()
        val role = UserRole.valueOf(binding.userRole.selectedItem.toString())

           // Mengonversi imageUri menjadi Bitmap dan menyimpannya
        val imagePath = imageUri?.let { uri ->
            val bitmap = uriToBitmap(uri)
            bitmap?.let { saveImageToLocalStorage(it) }
        } ?: ""


        val user = User(
            image = imagePath,
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

    private fun uriToBitmap(uri: Uri): Bitmap? {
        return context?.contentResolver?.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it)
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

    private fun saveImageToLocalStorage(imageBitmap: Bitmap): String {
        val filename = "image_${System.currentTimeMillis()}.jpg"
        val file = File(context?.filesDir, filename)
        val outputStream = FileOutputStream(file)
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file.absolutePath
    }



}