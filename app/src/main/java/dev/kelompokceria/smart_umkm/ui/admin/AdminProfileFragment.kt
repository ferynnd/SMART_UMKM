package dev.kelompokceria.smart_umkm.ui.admin

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.Constant
import dev.kelompokceria.smart_umkm.data.helper.PreferenceHelper
import dev.kelompokceria.smart_umkm.databinding.FragmentAdminProfileBinding
import dev.kelompokceria.smart_umkm.ui.AboutUsFragment
import dev.kelompokceria.smart_umkm.ui.FaqFragment
import dev.kelompokceria.smart_umkm.ui.LoginActivity
import dev.kelompokceria.smart_umkm.ui.MoreInfoFragment
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class AdminProfileFragment : Fragment() {

    private lateinit var binding: FragmentAdminProfileBinding
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
        // Inflate the layout for this fragment
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)
        // Ambil username dari arguments
        val username = sharedPref.getString(Constant.PREF_USER_NAME)

        if (username == null) {
            Toast.makeText(requireContext(), "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
        } else {
            userViewModel.getUserByUsername(username)

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
                    binding.tvName.text = user.username
                }
            }
        }

        binding.btnLogout.setOnClickListener {
            val dialogBuilder = AlertDialog.Builder(requireContext())
            dialogBuilder.setMessage("Apakah Anda yakin ingin logout")
                .setCancelable(false)
                .setPositiveButton("Ya") { _, _ ->
                    sharedPref.clear()
                    Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                        dialog.dismiss()
                }

            val alert = dialogBuilder.create()
            alert.setTitle("Logout")
            alert.show()

        }

        binding.btnMoreInfo.setOnClickListener{
            val fragment = MoreInfoFragment()
            val pindah = parentFragmentManager.beginTransaction()
            pindah.replace(R.id.nav_host_fragment_admin, fragment)
            pindah.addToBackStack(null)
            pindah.commit()
        }

        binding.btnFaq.setOnClickListener{
            val fragment = FaqFragment()
            val pindah = parentFragmentManager.beginTransaction()
            pindah.replace(R.id.nav_host_fragment_admin, fragment)
            pindah.addToBackStack(null)
            pindah.commit()
        }

        binding.btnAboutUs.setOnClickListener{
            val fragment = AboutUsFragment()
            val pindah = parentFragmentManager.beginTransaction()
            pindah.replace(R.id.nav_host_fragment_admin, fragment)
            pindah.addToBackStack(null)
            pindah.commit()
        }

            return binding.root
        }

        override fun onDestroyView() {
            super.onDestroyView()
        }
}