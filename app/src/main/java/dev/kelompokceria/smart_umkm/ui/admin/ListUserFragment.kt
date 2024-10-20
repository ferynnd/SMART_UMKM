package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.AdminUserAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListUserBinding
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.launch

class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: AdminUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListUserBinding.inflate(inflater, container, false)

        recyclerView = binding.recyclerViewUsers
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Inisialisasi adapter dengan list kosong
        userAdapter = AdminUserAdapter(emptyList(), userViewModel) { userName, userEmail, userPhone, userUsername, userPassword, userRole ->
            getNavigateToEdit(userName, userEmail, userPhone, userUsername, userPassword, userRole)
        }

        recyclerView.adapter = userAdapter

        // Observasi LiveData untuk semua pengguna
        userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
            userList?.let {
                userAdapter.updateUsers(it) // Update data di adapter
            }
        }

        // Setup SearchView
        setupSearchView()

        // Navigasi ke CreateUserFragment
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
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                if (searchText.isNullOrEmpty()) {
                    // Jika pencarian dibatalkan atau teks kosong, tampilkan semua pengguna
                    userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
                        userList?.let {
                            userAdapter.updateUsers(it) // Tampilkan semua pengguna
                        }
                    }
                } else {
                    // Panggil metode pencarian di ViewModel
                    lifecycleScope.launch {
                        userAdapter.filter(searchText)
                    }
                }
                return true
            }
        })
    }

    fun getNavigateToEdit(userName: String, userEmail: String, userPhone: String, userUsername: String, userPassword: String, userRole: String) {
        val fragmentEditUser = EditUserFragment()
        val bundle = Bundle()
        bundle.putString("NAME", userName)
        bundle.putString("EMAIL", userEmail)
        bundle.putString("PHONE", userPhone)
        bundle.putString("USERNAME", userUsername)
        bundle.putString("PASSWORD", userPassword)
        bundle.putString("ROLE", userRole)
        fragmentEditUser.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, fragmentEditUser)
            .addToBackStack(null)
            .commit()
    }
}
