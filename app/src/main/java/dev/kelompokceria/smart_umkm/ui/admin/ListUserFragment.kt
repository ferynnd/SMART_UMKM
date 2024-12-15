package dev.kelompokceria.smart_umkm.ui.admin

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.AdminUserAdapter
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.databinding.FragmentListUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var userAdapter: AdminUserAdapter

    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        networkStatusViewModel = ViewModelProvider(this)[NetworkStatusViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListUserBinding.inflate(inflater, container, false)

        // Inisialisasi adapter dengan list kosong
        userAdapter = AdminUserAdapter({ user ->
            onEditClick(user)
        }, { user ->
            onDeleteClick(user)
        })

        binding.recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
        }

        networkStatusViewModel.networkStatus.observe(viewLifecycleOwner){ isConnected ->
            if (isConnected) {
                 binding.floatAddUser.setOnClickListener {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_admin, CreateUserFragment())
                        .addToBackStack(null)
                        .commit()
                }
            } else {
                Toast.makeText(requireContext(), "Network is not connected", Toast.LENGTH_LONG).show()
            }
        }


        binding.searchView1.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                      lifecycleScope.launch {
                        newText?.let {
                            userViewModel.searchUser(it)
                            userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
                                userList?.let {
                                    userAdapter.submitList(it)
                                }
                            }
                        }
                    }
                    return true
                }
        })


         binding.swiperefresh.setOnRefreshListener {
            lifecycleScope.launch {
                try {
                    userViewModel.refreshUsers()
                    userViewModel.refreshDeleteUsers()
                } catch (e: Exception) {
                    Log.e("ProductList", "Error fetching product data", e)
                } finally {
                    binding.swiperefresh.isRefreshing = false
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.allUser.observe(viewLifecycleOwner) { user ->
                  user?.let {
                      lifecycleScope.launch(Dispatchers.Main) {
                          if (user.isNotEmpty()){
                              binding.linear.visibility = View.GONE
                               userAdapter.submitList(user)
                          } else {
                              binding.linear.visibility = View.VISIBLE
                              userAdapter.submitList(emptyList())
                              userAdapter.notifyDataSetChanged()
                          }
                      }
                  }
            }
        }

//        setupSearchView()

        return binding.root
    }

//    private fun setupSearchView() {
//        binding.searchView1.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
//                    userList?.let {
//                        userAdapter.submitList(it)
//                    }
//                }
//                return false
//            }
//
//            override fun onQueryTextChange(searchText: String?): Boolean {
//                userAdapter.filter(searchText ?: "")
//                return true
//            }
//        })
//    }

    private fun onDeleteClick(user: User) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage("Apakah Anda yakin ingin menghapus user ${user.name}?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {

                        // Hapus produk dari ViewModel
                        userViewModel.deleteUser(user, user.id)

                        withContext(Dispatchers.Main) {
                             userViewModel.allUser.observe(viewLifecycleOwner) { productList ->
                                productList?.let {
                                    userAdapter.submitList(productList)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // Tampilkan pesan error di thread utama
                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
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


     private fun onEditClick(user: User ) {
        val bundle = Bundle().apply {
            putInt("userId", user.id ?: 0)
        }
        val createUserFragment = CreateUserFragment()
        createUserFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, createUserFragment)
            .addToBackStack(null)
            .commit()
    }


}
