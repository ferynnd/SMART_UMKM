package dev.kelompokceria.smart_umkm.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.model.EmployeeSchedule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeeScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val scheduleDao = AppDatabase.getDatabase(application).employeeScheduleDao()
    val allSchedules: LiveData<List<EmployeeSchedule>> = scheduleDao.getAllSchedules()

    fun addSchedule(schedule: EmployeeSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.insertSchedule(schedule)
        }
    }

    fun updateSchedule(schedule: EmployeeSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.updateSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: EmployeeSchedule) {
        viewModelScope.launch(Dispatchers.IO) {
            scheduleDao.deleteSchedule(schedule)
        }
    }

    fun getScheduleById(id: Int): LiveData<EmployeeSchedule> {
        return scheduleDao.getScheduleById(id)
    }
}
