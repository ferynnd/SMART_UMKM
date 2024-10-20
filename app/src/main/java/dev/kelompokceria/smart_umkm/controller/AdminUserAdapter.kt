package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUserAdapter(
    private var userList: List<User>,
    private var userViewModel: UserViewModel,
    private val userEditNavigate: (userName: String, userEmail: String, userPhone: String, userUsername: String, userPassword: String, userRole: String) -> Unit
) : RecyclerView.Adapter<AdminUserAdapter.UserViewHolder>() {

    private var filteredUserList: List<User> = userList // Daftar pengguna yang difilter

    class UserViewHolder(var binding: CardUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredUserList.size // Mengembalikan ukuran daftar yang difilter
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = filteredUserList[position]
        val view = holder.binding

        // Menggunakan Glide untuk memuat gambar
        user.image?.let {
            Glide.with(view.imageView.context)
                .load(it)
                .placeholder(R.drawable.picture) // Placeholder jika gambar tidak tersedia
                .into(view.imageView)
        } ?: run {
            view.imageView.setImageResource(R.drawable.picture) // Gambar default
        }

        view.textViewName.text = user.name
        view.textViewEmail.text = user.email
        view.textViewPhone.text = user.phone
        view.textViewUsername.text = user.username
        view.textViewPassword.text = user.password
        view.textViewRole.text = user.role.toString()

        // Navigasi ke Edit User
        view.buttonEdit.setOnClickListener {
            userEditNavigate(user.name, user.email, user.phone, user.username, user.password, user.role.toString())
        }

        // Toggle tampilan detail pengguna
        view.Expand.setOnClickListener {
            view.groupProfile.visibility = if (view.groupProfile.visibility == View.GONE) {
                View.VISIBLE // Tampilkan
            } else {
                View.GONE // Sembunyikan
            }
        }

        // Hapus pengguna
        view.buttonDelete.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                userViewModel.userDelete(user.username)
                withContext(Dispatchers.Main) {
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                }
            }
        }
    }

    // Fungsi untuk memperbarui daftar pengguna
    fun updateUsers(newUserList: List<User>) {
        userList = newUserList
        filteredUserList = newUserList // Memperbarui daftar yang difilter
        notifyDataSetChanged() // Memperbarui seluruh daftar
    }

    // Fungsi untuk memfilter pengguna berdasarkan nama
    fun filter(query: String) {
        filteredUserList = if (query.isEmpty()) {
            userList // Jika tidak ada query, tampilkan semua pengguna
        } else {
            userList.filter { user ->
                user.name?.contains(query, ignoreCase = true) == true // Filter berdasarkan nama
            }
        }
        notifyDataSetChanged() // Memperbarui tampilan dengan daftar yang difilter
    }
}
