package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.AdminUserAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentListUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel

class ListUserFragment : Fragment() {

    private lateinit var binding: FragmentListUserBinding
    private lateinit var userViewModel: UserViewModel
    private lateinit var recyclerView: RecyclerView
    private var userList = mutableListOf<User>()

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
                recyclerView.adapter = AdminUserAdapter(it)
            }
        }


        binding.floatAddUser.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_admin, CreateUserFragment())
                .addToBackStack(null)
                .commit()
        }
        return binding.root
    }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }




}