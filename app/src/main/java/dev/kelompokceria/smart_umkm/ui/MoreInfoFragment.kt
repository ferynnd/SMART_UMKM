package dev.kelompokceria.smart_umkm.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.databinding.FragmentMoreInfoBinding
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel

class MoreInfoFragment : Fragment() {
    private var _binding: FragmentMoreInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel

    private lateinit var sharedPref: PreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        sharedPref = PreferenceHelper(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreInfoBinding.inflate(inflater, container, false)

        val username = sharedPref.getString(Constant.PREF_USER_NAME)


        if (username == null) {
            Toast.makeText(requireContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            // Ambil data user berdasarkan username
            userViewModel.getUserByUsername(username)

            // Observe LiveData dari userViewModel
            userViewModel.loggedInUser.observe(viewLifecycleOwner) { user ->
                if (user != null) {
                    user.image.let {
                        Glide.with(binding.ivProfile.context)
                            .load(it)
                            .placeholder(R.drawable.picture) // Placeholder if image is unavailable
                            .into(binding.ivProfile)
                    } ?: run {
                        binding.ivProfile.setImageResource(R.drawable.picture) // Default image
                    }
                    binding.userName.text = user.name
                    binding.userRole.text = user.role.toString()
                    binding.nameTextView.text = user.name
                    binding.emailTextView.text = user.email
                    binding.phoneTextView.text = user.phone
                    binding.usernameTextView.text = user.username
                } else {
                    Toast.makeText(requireContext(), "User tidak ditemukan", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            // Set up klik listener untuk tombol kembali
            binding.backButton.setOnClickListener {
                // Kembali ke fragment sebelumnya (AdminProfileFragment)
                requireActivity().onBackPressed()
            }

        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
