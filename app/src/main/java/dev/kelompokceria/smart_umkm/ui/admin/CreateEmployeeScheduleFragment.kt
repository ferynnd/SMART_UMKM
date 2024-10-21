package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateEmployeeScheduleBinding
import dev.kelompokceria.smart_umkm.model.EmployeeSchedule
import dev.kelompokceria.smart_umkm.viewmodel.EmployeeScheduleViewModel
import java.util.Date

class CreateEmployeeScheduleFragment : Fragment() {

    private lateinit var binding: FragmentCreateEmployeeScheduleBinding
    private lateinit var employeeScheduleViewModel: EmployeeScheduleViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateEmployeeScheduleBinding.inflate(inflater, container, false)

        // Inisialisasi ViewModel
        employeeScheduleViewModel = ViewModelProvider(this).get(EmployeeScheduleViewModel::class.java)

        binding.btnSave.setOnClickListener {
            if (validateInput()) {
                val name = binding.edEmployeeName.text.toString()
                val shiftDate = Date() // Anda bisa mengganti ini dengan date picker di implementasi yang sebenarnya
                val shiftTime = binding.spinnerShiftTime.selectedItem.toString()
                val isPresent = binding.switchIsPresent.isChecked

                val schedule = EmployeeSchedule(
                    employeeName = name,
                    shiftDate = shiftDate.time, // Ubah Date menjadi Long
                    shiftTime = shiftTime,
                    isPresent = isPresent
                )

                employeeScheduleViewModel.addSchedule(schedule)
                Toast.makeText(requireContext(), "Jadwal karyawan ditambahkan", Toast.LENGTH_SHORT).show()

                // Bersihkan form setelah penyimpanan
                clearForm()

                // Kembali ke halaman sebelumnya setelah berhasil menyimpan data
                parentFragmentManager.popBackStack()

            } else {
                Toast.makeText(requireContext(), "Mohon isi semua kolom", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun validateInput(): Boolean {
        return binding.edEmployeeName.text.isNotEmpty() && binding.spinnerShiftTime.selectedItem != null
    }

    private fun clearForm() {
        binding.edEmployeeName.text.clear()
        binding.switchIsPresent.isChecked = false
    }
}
