package dev.kelompokceria.smart_umkm.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.kelompokceria.smart_umkm.model.EmployeeSchedule

@Dao
interface EmployeeScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: EmployeeSchedule)

    @Update
    suspend fun updateSchedule(schedule: EmployeeSchedule)

    @Delete
    suspend fun deleteSchedule(schedule: EmployeeSchedule)

    @Query("SELECT * FROM employee_schedules WHERE id = :id")
    fun getScheduleById(id: Int): LiveData<EmployeeSchedule>

    @Query("SELECT * FROM employee_schedules ORDER BY shiftDate ASC")
    fun getAllSchedules(): LiveData<List<EmployeeSchedule>>
}
