package dev.kelompokceria.smart_umkm.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.model.EmployeeSchedule

class EmployeeScheduleAdapter(
    private var schedules: MutableList<EmployeeSchedule>
) : RecyclerView.Adapter<EmployeeScheduleAdapter.ScheduleViewHolder>() {

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val employeeName: TextView = itemView.findViewById(R.id.employeeName)
        val shiftDate: TextView = itemView.findViewById(R.id.shiftDate)
        val shiftTime: TextView = itemView.findViewById(R.id.shiftTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_employee_schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.employeeName.text = schedule.employeeName
        holder.shiftDate.text = schedule.shiftDate.toString()
        holder.shiftTime.text = schedule.shiftTime
    }

    override fun getItemCount(): Int = schedules.size

    fun updateSchedules(newSchedules: List<EmployeeSchedule>) {
        schedules.clear()
        schedules.addAll(newSchedules)
        notifyDataSetChanged()
    }
}
