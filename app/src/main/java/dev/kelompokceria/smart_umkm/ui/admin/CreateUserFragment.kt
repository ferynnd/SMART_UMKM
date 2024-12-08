package dev.kelompokceria.smart_umkm.ui.admin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class CreateUserFragment : Fragment() {


    private lateinit var binding: FragmentCreateUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private var imageUri: Uri? = null
    private var imagePart: MultipartBody.Part? = null
    private var usertId: Int? = null

    private var imageUpdate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCreateUserBinding.inflate(inflater, container, false)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usertId = arguments?.getInt("userId")

        if (usertId != null) {
            userViewModel.fetchUserById(usertId!!)

            userViewModel.user.observe(viewLifecycleOwner) { response ->
                response.data?.let { user ->
                    binding.edName.setText(user.name)
                    binding.edEmail.setText(user.email)
                    binding.edPhone.setText(user.phone)
                    binding.edUsername.setText(user.username)
                    binding.edPassword.setText(user.password)
                    imageUpdate = user.image

                    user.image?.let { imageUrl ->
                        Glide.with(this).load(imageUrl).into(binding.ImagePreview)
                    }

                    binding.btnCreate.text = "UPDATE"
                    binding.btnCreate.setOnClickListener {
                        if (validateInputs()){
                            updateProduct(usertId!!)
                        } else {
                            Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        }
                    }

                } ?: run {
                    Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                    navigateToUserList()
                }
            }
        }

        hideBottomNavigationView()
        setupSpinner()

        binding.btnAddImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnCreate.setOnClickListener {
            if (validateInputs()) {
                uploadUser()
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }


    private fun showBottomNavigationView() {
        val bottomNavigationView = activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)
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

     private fun updateProduct(id: Int) {
        val name = binding.edName.text.toString()
        val email = binding.edEmail.text.toString()
        val phone = binding.edPhone.text.toString()
        val username = binding.edUsername.text.toString()
        val password = binding.edPassword.text.toString()
        val role = UserRole.valueOf(binding.userRole.selectedItem.toString())

        // Validate input
        if (password.length > 6) {
             val user = User(
                image = imageUpdate,
                name = name,
                email = email,
                phone = phone,
                username = username,
                password = password,
                role = role
            )
            lifecycleScope.launch(Dispatchers.IO) {
                // Check if imagePart is not null before updating
                if (imagePart != null) {
                    userViewModel.updateUser(id, user, imagePart,"PUT",
                        onSuccess = {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "User successfully updated", Toast.LENGTH_SHORT).show()
                                clearFields()
                                navigateToUserList()
                            }
                        },
                        onError = { errorMessage ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to update user: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                } else {
                    // If no imagePart, call updateProduct without it
                    userViewModel.updateUser(id, user, method = "PUT",
                        onSuccess = {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "User successfully updated", Toast.LENGTH_SHORT).show()
                                clearFields()
                                navigateToUserList()
                            }
                        },
                        onError = { errorMessage ->
                            lifecycleScope.launch(Dispatchers.Main) {
                                Toast.makeText(context, "Failed to User product: $errorMessage", Toast.LENGTH_LONG).show()
                            }
                        }
                    )
                }
            }
        } else {
            Toast.makeText(requireContext(), "password must be more than 6 characters", Toast.LENGTH_SHORT).show()
        }
    }


    private fun uploadUser() {
        val name = binding.edName.text.toString()
        val email = binding.edEmail.text.toString()
        val phone = binding.edPhone.text.toString()
        val username = binding.edUsername.text.toString()
        val password = binding.edPassword.text.toString()
        val role = UserRole.valueOf(binding.userRole.selectedItem.toString())

        // Validate input
        if ( password.length > 6 && imagePart != null) {
             val user = User(
                image = imageUri.toString(),
                name = name,
                email = email,
                phone = phone,
                username = username,
                password = password,
                role = role
            )

            lifecycleScope.launch(Dispatchers.IO) {
                userViewModel.createUser(user, imagePart!!,
                    onSuccess = {
                        Toast.makeText(context, "User successfully created", Toast.LENGTH_SHORT).show()
                        clearFields()
                        navigateToUserList()
                    },
                    onError = { errorMessage ->
                        Toast.makeText(context, "Failed to create user: $errorMessage", Toast.LENGTH_LONG).show()
                    }
                )
            }
        } else {
            Toast.makeText(context, "must be more than 6 characters", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                imageUri = it
                imagePart = uriToMultipart(it)
                Glide.with(this).load(it).into(binding.ImagePreview)
            }
        }

    @SuppressLint("Recycle")
    private fun uriToMultipart(uri: Uri): MultipartBody.Part? {
            return try {
                val fileDescriptor = requireContext().contentResolver.openFileDescriptor(uri, "r") ?: return null
                val file = File(requireContext().cacheDir, "temp_image.jpg")

                // Menyalin file dari URI ke file sementara
                FileOutputStream(file).use { outputStream ->
                    val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
                    inputStream.copyTo(outputStream)
                }

                // Membuat RequestBody dari file
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                // Mengembalikan MultipartBody.Part
                MultipartBody.Part.createFormData("image", file.name, requestFile)
            } catch (e: Exception) {
                Log.e("CreateProductFragment", "Error converting URI to Multipart: ${e.message}")
                null
            }
    }

    private fun clearFields() {
        binding.edName.text?.clear()
        binding.edEmail.text?.clear()
        binding.edPhone.text?.clear()
        binding.edUsername.text?.clear()
        binding.edPassword.text?.clear()
    }


    private fun navigateToUserList() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, ListUserFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun hideBottomNavigationView() {
        activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.findViewById<MaterialCardView>(R.id.layoutNavAdmin)?.visibility = View.VISIBLE
    }



}