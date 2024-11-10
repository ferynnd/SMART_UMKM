package dev.kelompokceria.smart_umkm.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.AdminUserAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userAdapter: AdminUserAdapter

    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListUserBinding.inflate(inflater, container, false)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dengan list kosong
        userAdapter = AdminUserAdapter({ user ->
            deleteUser(user)
        }, { userImage, userName, userEmail, userPhone, userUsername, userPassword, userRole, userId ->
            getNavigateToEdit(userImage, userName, userEmail, userPhone, userUsername, userPassword, userRole, userId)
        })

        binding.recyclerViewUsers.adapter = userAdapter

        userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
            userList?.let {
                userAdapter.submitList(it)
            }
        }

        setupSearchView()

        binding.floatAddUser.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, CreateUserFragment())
                .addToBackStack(null)
                .commit()
        }

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView1.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
                    userList?.let {
                        userAdapter.submitList(it)
                    }
                }
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                userAdapter.filter(searchText ?: "")
                return true
            }
        })
    }

    private fun getNavigateToEdit(userImage: String, userName: String, userEmail: String, userPhone: String, userUsername: String, userPassword: String, userRole: String, userId : Int) {
        val fragmentEditUser = EditUserFragment()
        val bundle = Bundle()
        bundle.putString("IMAGE_PATH", userImage)
        bundle.putString("NAME", userName)
        bundle.putString("EMAIL", userEmail)
        bundle.putString("PHONE", userPhone)
        bundle.putString("USERNAME", userUsername)
        bundle.putString("PASSWORD", userPassword)
        bundle.putString("ROLE", userRole)
        bundle.putString("USER_ID", userId.toString())
        fragmentEditUser.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, fragmentEditUser)
            .addToBackStack(null)
            .commit()
    }

    private fun deleteUser(user: User) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus User ${user.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                 lifecycleScope.launch {
                    try {
                        userViewModel.deleteUser(user)
                        Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Hapus User")
        alert.show()
    }


}
