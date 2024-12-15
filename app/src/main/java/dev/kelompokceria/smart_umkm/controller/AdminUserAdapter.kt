package dev.kelompokceria.smart_umkm.controller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.helper.NetworkStatusViewModel
import dev.kelompokceria.smart_umkm.databinding.CardUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUserAdapter(
    private val onEditClick: (User) -> Unit,
    private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, AdminUserAdapter.UserViewHolder>(UserDiffCallback()) {

    inner class UserViewHolder(var binding: CardUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.textViewName.text = user.name
            binding.textViewEmail.text = user.email
            binding.textViewPhone.text = user.phone
            binding.textViewUsername.text = user.username
            binding.textViewPassword.text = user.password
            binding.textViewRole.text = user.role.toString()

            Glide.with(binding.imageView.context)
                .load(user.image)
                .placeholder(R.drawable.picture)
                .into(binding.imageView)

            getViewModel(binding.root.context).networkStatus.observe(binding.root.context as LifecycleOwner) { isConnected ->
                    if (isConnected) {
                        binding.buttonEdit.setOnClickListener { onEditClick(user) }
                        binding.buttonDelete.setOnClickListener { onDeleteClick(user)  }
                    } else {
                        Toast.makeText(binding.root.context, "Network is not connected", Toast.LENGTH_SHORT).show()
                    }
            }

            binding.Expand.setOnClickListener {
                binding.groupProfile.visibility = if (binding.groupProfile.visibility == View.GONE)  View.VISIBLE else View.GONE
            }

        }
    }

    private fun getViewModel(context: Context): NetworkStatusViewModel {
        return ViewModelProvider(context as ViewModelStoreOwner)[NetworkStatusViewModel::class.java]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        val user = item as User
        holder.bind(user)
    }

    override fun getItemId(position: Int): Long = position.toLong()

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            currentList
        } else {
            currentList.filter { user ->
                user.name?.contains(query, ignoreCase = true) == true
            }
        }
        submitList(filteredList) // Submit filtered list
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username // Assuming username is unique
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
