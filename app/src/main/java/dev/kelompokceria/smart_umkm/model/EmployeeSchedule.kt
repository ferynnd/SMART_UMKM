package dev.kelompokceria.smart_umkm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "employee_schedules")
data class EmployeeSchedule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val employeeName: String,
    val shiftDate: Long, // Ubah dari Date ke Long
    val shiftTime: String, // Bisa berupa 'Morning', 'Afternoon', atau 'Night'
    val isPresent: Boolean // Menandakan apakah karyawan hadir atau tidak
)
