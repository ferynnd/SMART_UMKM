package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import dev.kelompokceria.smart_umkm.databinding.FragmentListEmployeeScheduleBinding
import dev.kelompokceria.smart_umkm.viewmodel.EmployeeScheduleViewModel
import dev.kelompokceria.smart_umkm.adapter.EmployeeScheduleAdapter

class ListEmployeeScheduleFragment : Fragment() {

    private lateinit var binding: FragmentListEmployeeScheduleBinding
    private lateinit var employeeScheduleViewModel: EmployeeScheduleViewModel
    private lateinit var adapter: EmployeeScheduleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListEmployeeScheduleBinding.inflate(inflater, container, false)

        // Setup RecyclerView
        adapter = EmployeeScheduleAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        // ViewModel
        employeeScheduleViewModel = ViewModelProvider(this).get(EmployeeScheduleViewModel::class.java)
        employeeScheduleViewModel.allSchedules.observe(viewLifecycleOwner) { schedules ->
            adapter.updateSchedules(schedules)
        }

        // Tambah aksi pada tombol "Tambah Jadwal"
        binding.btnAddSchedule.setOnClickListener {
            // Navigasi ke CreateEmployeeScheduleFragment
            val createEmployeeScheduleFragment = CreateEmployeeScheduleFragment()
            parentFragmentManager.beginTransaction()
                .replace(dev.kelompokceria.smart_umkm.R.id.nav_host_fragment_admin, createEmployeeScheduleFragment)
                .addToBackStack(null) // Menyimpan fragment sebelumnya ke backstack
                .commit()
        }

        return binding.root
    }
}
