package dev.kelompokceria.smart_umkm.ui.admin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.AdminUserAdapter
import dev.kelompokceria.smart_umkm.data.dao.UserDao
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.FragmentListUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel

class ListUserFragment : Fragment() {

    private var _binding : FragmentListUserBinding? = null
    private val binding get() = _binding!!

    private var userList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentListUserBinding.inflate(inflater,container,false)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())


        return binding.root
    }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Menetapkan listener untuk tombol
        binding.floatAddUser.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, CreateUserFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}