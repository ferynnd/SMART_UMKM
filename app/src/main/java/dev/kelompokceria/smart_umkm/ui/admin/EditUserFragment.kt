package dev.kelompokceria.smart_umkm.ui.admin

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.FragmentEditUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class EditUserFragment : Fragment() {

    private lateinit var binding: FragmentEditUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var database: AppDatabase
    private var userIDINT :Int? = null
    private var userID : String? = null

    private var imageUri: Uri? = null
    private var imageBytes: ByteArray? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditUserBinding.inflate(inflater,container,false)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        database = AppDatabase.getInstance(requireContext())

              // Ambil data dari arguments (termasuk gambar)
        val imageBytesFromArgs = arguments?.getByteArray("IMAGE")
        imageBytesFromArgs?.let {
            imageBytes = it
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            Glide.with(this).load(bitmap).into(binding.ImagePreview) // Menampilkan preview gambar
        }

        binding.edName.isEnabled = false
        binding.edUsername.isEnabled = false
        binding.edName.setText(arguments?.getString("NAME"))
        binding.edEmail.setText(arguments?.getString("EMAIL"))
        binding.edPhone.setText(arguments?.getString("PHONE"))
        binding.edUsername.setText(arguments?.getString("USERNAME"))
        binding.edPassword.setText(arguments?.getString("PASSWORD"))
        val userRoleString : String = arguments?.getString("ROLE")!!
        var spinner = binding.userRole.selectedItem
        spinner = UserRole.valueOf(userRoleString)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinner()


        // Menambahkan listener untuk mengambil gambar
        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }


        // Menetapkan listener untuk tombol
        binding.btnSave.setOnClickListener {
//            Toast.makeText(requireContext(), "id sama dengan ${userID}", Toast.LENGTH_SHORT).show()
             if (validateInputs()) {
                 val email = binding.edEmail.text.toString()
                 val phone = binding.edPhone.text.toString()
                 val username = binding.edUsername.text.toString()
                 val password = binding.edPassword.text.toString()
                 val role = UserRole.valueOf(binding.userRole.selectedItem.toString())

                  // Jika ada gambar baru dipilih, konversi ke byte array
                    val updatedImageBytes = imageUri?.let { uri ->
                        val bitmap = uriToBitmap(uri)
                        bitmapToByteArray(bitmap)
                    } ?: imageBytes // Jika tidak ada perubahan gambar, gunakan gambar yang sudah ada

                 lifecycleScope.launch {
                     userViewModel.userUpdate(updatedImageBytes!!,email,phone,password,role, username)
                 }
                 navigateToUserList() // Berpindah ke ListUserFragment
            } else {
                Toast.makeText(requireContext(), "Silahkan lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            Glide.with(this).load(it).into(binding.ImagePreview) // Menampilkan gambar yang dipilih
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


     private fun navigateToUserList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, ListUserFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun userEdit() {

    }



}