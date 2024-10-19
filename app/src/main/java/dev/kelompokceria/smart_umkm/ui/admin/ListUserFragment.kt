package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.AdminUserAdapter
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.FragmentListUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListUserBinding.inflate(inflater,container,false)

        recyclerView = binding.recyclerViewUsers

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        userViewModel.allUser.observe(viewLifecycleOwner) { userList ->
            userList?.let {
                recyclerView.adapter = AdminUserAdapter(it,userViewModel
                ) { userName, userEmail, userPhone, userUsername, userPassword, userRole ->
                    getNavigateToEdit(
                        userName,
                        userEmail,
                        userPhone,
                        userUsername,
                        userPassword,
                        userRole
                    )
                }
            }
        }

        binding.editTextSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(searchText: String?): Boolean {
                if (searchText != null) {
                    lifecycleScope.launch {
                        userViewModel.userSearch(searchText)
                    }
                }
                return true
            }
        })




        binding.floatAddUser.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, CreateUserFragment())
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }

    fun getNavigateToEdit(userName: String, userEmail: String, userPhone :String, userUsername : String, userPassword : String, userRole: String) {
        val FragmentEditUser = EditUserFragment()
        val bundle = Bundle()
        bundle.putString("NAME", userName)
        bundle.putString("EMAIL", userEmail)
        bundle.putString("PHONE", userPhone)
        bundle.putString("USERNAME", userUsername)
        bundle.putString("PASSWORD", userPassword)
        bundle.putString("ROLE", userRole)
        FragmentEditUser.arguments = bundle
        parentFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_admin, FragmentEditUser)
            .addToBackStack(null)
            .commit()
    }


}